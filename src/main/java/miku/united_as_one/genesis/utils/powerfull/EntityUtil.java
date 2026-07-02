package miku.united_as_one.genesis.utils.powerfull;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.server.level.*;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.util.Mth;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.entity.*;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.lighting.LeveledPriorityQueue;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class EntityUtil {
    public static void killEntity(Entity entity) {
        entity.removalReason = Entity.RemovalReason.DISCARDED;
        entity.entityData.set(Entity.DATA_POSE, Pose.DYING);
        entity.levelCallback = EntityInLevelCallback.NULL;
        entity.canUpdate = false;
        entity.isAddedToWorld = false;

        entity.valid = false;
        CapabilityDispatcher disp = entity.getCapabilities();
        if (disp != null)
            disp.listeners.forEach(Runnable::run);

        for(int i = entity.passengers.size() - 1; i >= 0; --i)
            entity.passengers.get(i).stopRiding();
        entity.stopRiding();

        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.hurtTime = 20;
            livingEntity.deathTime = 20;
            livingEntity.dead = true;

            for (int x = 0; x < livingEntity.handlers.length; ++x) {
                var handler = (LazyOptional<IItemHandlerModifiable>) livingEntity.handlers[x];
                handler.isValid = false;
                handler.listeners.forEach(e -> e.accept(handler));
                handler.listeners.clear();
            }

            livingEntity.brain.memories.keySet().forEach(moduleType -> livingEntity.brain.memories.put(moduleType, Optional.empty()));

            if (entity instanceof Player player)
                player.inventoryMenu.removed(player);
        }

        if (entity.level instanceof ServerLevel serverLevel) {
            chunkRemoveEntity(entity, serverLevel);

            ISGEntityTickList entityTickList = new ISGEntityTickList();
            entityTickList.active = new Int2ObjectLinkedOpenHashMap<>(serverLevel.entityTickList.active);
            entityTickList.passive = new Int2ObjectLinkedOpenHashMap<>(serverLevel.entityTickList.passive);
            entityTickList.remove(entity);
            serverLevel.entityTickList = entityTickList;

            Set<UUID> knownUuids = new HashSet<>(serverLevel.entityManager.knownUuids);
            knownUuids.remove(entity.uuid);
            serverLevel.entityManager.knownUuids = knownUuids;

            var visibleEntityStorage = new ISGEntityLookup();
            for (Entity entity1 : serverLevel.entityManager.visibleEntityStorage.getAllEntities())
                if (!entity1.uuid.equals(entity.uuid))
                    visibleEntityStorage.add(entity1);

            var sectionStorage = new ISGEntitySectionStorage(serverLevel.entityManager.sectionStorage);
            sectionStorage.removeEntity(entity);

            serverLevel.entityManager.visibleEntityStorage = visibleEntityStorage;
            serverLevel.entityManager.sectionStorage = sectionStorage;
            serverLevel.entityManager.entityGetter = new ISGLevelEntityGetterAdapter(visibleEntityStorage, sectionStorage);

            entity.updateDynamicGameEventListener(DynamicGameEventListener::remove);

            if (entity instanceof Mob mob) {
                Set<Mob> openHashSet = new ObjectOpenHashSet<>(serverLevel.navigatingMobs);
                openHashSet.remove(mob);
                serverLevel.navigatingMobs = openHashSet;

                mob.leashHolder = null;
                mob.leashInfoTag = null;
            }

            if (entity instanceof ServerPlayer serverPlayer) {
                serverLevel.players.remove(serverPlayer);
                serverLevel.server.playerList.remove(serverPlayer);

                if (serverPlayer.hasContainerOpen()) {
                    serverPlayer.containerMenu.removed(serverPlayer);
                    transferState(serverPlayer.inventoryMenu, serverPlayer.containerMenu);
                    serverPlayer.containerMenu = serverPlayer.inventoryMenu;
                }
            }

            if (entity.isMultipartEntity())
                for(PartEntity<?> partEntity : entity.getParts())
                    if (partEntity != null)
                        serverLevel.dragonParts.remove(partEntity.id);

            serverLevel.server.scoreboard.playerScores.keySet().removeIf(uuid -> Objects.equals(uuid, entity.stringUUID));
            serverLevel.server.scoreboard.teamsByPlayer.keySet().removeIf(uuid -> Objects.equals(uuid, entity.stringUUID));
        }

        if (entity.level instanceof ClientLevel clientLevel) {
            ISGEntityTickList tickingEntities = new ISGEntityTickList();
            tickingEntities.active = new Int2ObjectLinkedOpenHashMap<>(clientLevel.tickingEntities.active);
            tickingEntities.passive = new Int2ObjectLinkedOpenHashMap<>(clientLevel.tickingEntities.passive);
            tickingEntities.remove(entity);
            clientLevel.tickingEntities = tickingEntities;

            var visibleEntityStorage = new ISGEntityLookup();
            for (Entity entity1 : clientLevel.entityStorage.entityStorage.getAllEntities())
                if (!entity1.uuid.equals(entity.uuid))
                    visibleEntityStorage.add(entity1);

            var sectionStorage = new ISGEntitySectionStorage(clientLevel.entityStorage.sectionStorage);
            sectionStorage.removeEntity(entity);

            clientLevel.entityStorage.entityStorage = visibleEntityStorage;
            clientLevel.entityStorage.sectionStorage = sectionStorage;
            clientLevel.entityStorage.entityGetter = new ISGLevelEntityGetterAdapter(visibleEntityStorage, sectionStorage);

            if (entity instanceof AbstractClientPlayer clientPlayer)
                clientLevel.players.remove(clientPlayer);

            if (entity.isMultipartEntity())
                for(PartEntity<?> partEntity : entity.getParts())
                    if (partEntity != null)
                        clientLevel.partEntities.remove(partEntity.id);

            clientLevel.scoreboard.playerScores.keySet().removeIf(uuid -> Objects.equals(uuid, entity.stringUUID));
            clientLevel.scoreboard.teamsByPlayer.keySet().removeIf(uuid -> Objects.equals(uuid, entity.stringUUID));
        }
    }

    private static void transferState(AbstractContainerMenu thisMenu, AbstractContainerMenu menu) {
        Table<Container, Integer, Integer> table = HashBasedTable.create();

        for(int i = 0; i < menu.slots.size(); ++i) {
            Slot slot = menu.slots.get(i);
            table.put(slot.container, slot.getContainerSlot(), i);
        }

        for(int i = 0; i < thisMenu.slots.size(); ++i) {
            Slot slot = thisMenu.slots.get(i);
            Integer integer = table.get(slot.container, slot.getContainerSlot());
            if (integer != null) {
                thisMenu.lastSlots.set(i, menu.lastSlots.get(integer));
                thisMenu.remoteSlots.set(i, menu.remoteSlots.get(integer));
            }
        }

    }

    private static void chunkRemoveEntity(Entity entity, ServerLevel serverLevel) {
        ChunkMap chunkMap = serverLevel.chunkSource.chunkMap;
        if (entity instanceof ServerPlayer serverPlayer) {
            stopTracking(chunkMap, serverPlayer);

            for (ChunkMap.TrackedEntity trackedEntity : chunkMap.entityMap.values())
                if (trackedEntity.seenBy.remove(serverPlayer.connection))
                    removePairing(trackedEntity, serverPlayer);
        }

        ChunkMap.TrackedEntity removed = chunkMap.entityMap.remove(entity.id);
        if (removed != null)
            broadcastRemoved(removed);
    }

    private static void stopTracking(ChunkMap chunkMap, ServerPlayer serverPlayer) {
        SectionPos sectionPos = serverPlayer.lastSectionPos;
        boolean isIn = chunkMap.playerMap.players.getOrDefault(serverPlayer, true);
        chunkMap.playerMap.players.removeBoolean(serverPlayer);
        if (!isIn)
            removePlayer(chunkMap, sectionPos, serverPlayer);

        if (serverPlayer.level != chunkMap.level)
            return;

        int i = serverPlayer.blockPosition.x >> 4;
        int j = serverPlayer.blockPosition.z >> 4;

        for(int l = i - chunkMap.viewDistance - 1; l <= i + chunkMap.viewDistance + 1; ++l)
            for(int k = j - chunkMap.viewDistance - 1; k <= j + chunkMap.viewDistance + 1; ++k)
                if (ChunkMap.isChunkInRange(l, k, i, j, chunkMap.viewDistance))
                    serverPlayer.connection.send(new ClientboundForgetLevelChunkPacket(l, k));
    }

    private static void removePlayer(ChunkMap chunkMap, SectionPos sectionPos, ServerPlayer serverPlayer) {
        ChunkMap.DistanceManager distanceManager = chunkMap.distanceManager;

        ChunkPos chunkPos = sectionPos.chunk();
        long chunkPosLong = chunkPos.toLong();
        ObjectSet<ServerPlayer> objectset = distanceManager.playersPerChunk.get(chunkPosLong);
        objectset.remove(serverPlayer);
        if (objectset.isEmpty()) {
            distanceManager.playersPerChunk.remove(chunkPosLong);
            removeChunk(distanceManager.naturalSpawnChunkCounter, chunkPosLong, Integer.MAX_VALUE);
            removeChunk(distanceManager.playerTicketManager, chunkPosLong, Integer.MAX_VALUE);
            removeTicket(distanceManager.tickingTicketsTracker, chunkPos, distanceManager.getPlayerTicketLevel(), chunkPos);
        }
    }

    private static void removeChunk(ChunkTracker tracker, long toChunk, int newLevel) {
        removeChunk(tracker, toChunk, newLevel, tracker.getLevel(toChunk), tracker.computedLevels.get(toChunk) & 255);
        tracker.hasWork = tracker.priorityQueue.firstQueuedLevel < tracker.priorityQueue.levelCount;
    }

    private static void removeChunk(ChunkTracker tracker, long toPos, int proposedLevel, int currentLevel, int oldComputedLevel) {
        if (!tracker.isSource(toPos)) {
            proposedLevel = Mth.clamp(proposedLevel, 0, tracker.levelCount - 1);
            currentLevel = Mth.clamp(currentLevel, 0, tracker.levelCount - 1);

            boolean notInQueue = oldComputedLevel == 255;
            if (notInQueue)
                oldComputedLevel = currentLevel;

            int computedLevel = Mth.clamp(tracker.getComputedLevel(toPos, ChunkPos.INVALID_CHUNK_POS, proposedLevel), 0, tracker.levelCount - 1);

            int oldPriority = tracker.calculatePriority(currentLevel, oldComputedLevel);
            if (currentLevel != computedLevel) {
                int newPriority = tracker.calculatePriority(currentLevel, computedLevel);
                if (oldPriority != newPriority && !notInQueue)
                    dequeue(tracker.priorityQueue, toPos, oldPriority, newPriority);
                tracker.priorityQueue.enqueue(toPos, newPriority);
                tracker.computedLevels.put(toPos, (byte) computedLevel);
            } else if (!notInQueue) {
                dequeue(tracker.priorityQueue, toPos, oldPriority, tracker.levelCount);
                tracker.computedLevels.remove(toPos);
            }
        }
    }

    private static void dequeue(LeveledPriorityQueue priorityQueue, long value, int levelIndex, int endIndex) {
        LongLinkedOpenHashSet longs = priorityQueue.queues[levelIndex];
        longs.remove(value);
        if (longs.isEmpty() && priorityQueue.firstQueuedLevel == levelIndex)
            checkFirstQueuedLevel(priorityQueue, endIndex);
    }

    private static void checkFirstQueuedLevel(LeveledPriorityQueue priorityQueue, int endLevelIndex) {
        int i = priorityQueue.firstQueuedLevel;
        priorityQueue.firstQueuedLevel = endLevelIndex;

        for(int j = i + 1; j < endLevelIndex; ++j)
            if (!priorityQueue.queues[j].isEmpty()) {
                priorityQueue.firstQueuedLevel = j;
                break;
            }
    }

    private static void removeTicket(TickingTracker tracker, ChunkPos chunkPos, int ticketLevel, ChunkPos key) {
        long chunkPosLong = chunkPos.toLong();

        var tickets = tracker.getTickets(chunkPosLong);
        tickets.remove(new Ticket<>(TicketType.PLAYER, ticketLevel, key));
        if (tickets.isEmpty())
            tracker.tickets.remove(chunkPosLong);

        removeChunk(tracker, chunkPosLong, tracker.getTicketLevelAt(tickets));
    }

    private static void broadcastRemoved(ChunkMap.TrackedEntity tracked) {
        for(ServerPlayerConnection serverplayerconnection : tracked.seenBy)
            removePairing(tracked, serverplayerconnection.getPlayer());
    }

    private static void removePairing(ChunkMap.TrackedEntity tracked, ServerPlayer serverPlayer) {
        tracked.entity.stopSeenByPlayer(serverPlayer);
        serverPlayer.connection.send(new ClientboundRemoveEntitiesPacket(tracked.entity.id));
    }

    private static class ISGEntityTickList extends EntityTickList {
//        private Int2ObjectMap<Entity> active = new Int2ObjectLinkedOpenHashMap<>();
//        private Int2ObjectMap<Entity> passive = new Int2ObjectLinkedOpenHashMap<>();
//        @Nullable
//        private Int2ObjectMap<Entity> iterated;

        public void ensureActiveIsNotIterated() {
            if (this.iterated == this.active) {
                this.passive.clear();

                for (Int2ObjectMap.Entry<Entity> entityEntry : Int2ObjectMaps.fastIterable(this.active))
                    this.passive.put(entityEntry.getIntKey(), entityEntry.getValue());

                Int2ObjectMap<Entity> active = this.active;
                this.active = this.passive;
                this.passive = active;
            }

        }

        public void add(@NotNull Entity entity) {
            this.ensureActiveIsNotIterated();
            this.active.put(entity.getId(), entity);
        }

        public void remove(Entity entity) {
            this.ensureActiveIsNotIterated();
            this.active.remove(entity.getId());
        }

        public boolean contains(Entity p_156915_) {
            return this.active.containsKey(p_156915_.getId());
        }

        public void forEach(@NotNull Consumer<Entity> consumer) {
            if (this.iterated == null) {
                this.iterated = this.active;

                try {
                    for (Entity accept : this.active.values())
                        consumer.accept(accept);
                } finally {
                    this.iterated = null;
                }
            }
        }
    }

    private static class ISGEntityLookup extends EntityLookup<Entity> {
        private final Int2ObjectMap<Entity> byId = new Int2ObjectLinkedOpenHashMap<>();
        private final Map<UUID, Entity> byUuid = Maps.newHashMap();

        public <U extends Entity> void getEntities(@NotNull EntityTypeTest<Entity, U> entityTypeTest, @NotNull AbortableIterationConsumer<U> consumer) {
            for (Entity entity1 : this.byId.values()) {
                U tryCast = entityTypeTest.tryCast(entity1);
                if (tryCast != null && consumer.accept(tryCast).shouldAbort()) {
                    return;
                }
            }
        }

        public @NotNull Iterable<Entity> getAllEntities() {
            return Iterables.unmodifiableIterable(this.byId.values());
        }

        public void add(Entity entity) {
            UUID uuid = entity.uuid;
            if (!this.byUuid.containsKey(uuid)) {
                this.byUuid.put(uuid, entity);
                this.byId.put(entity.id, entity);
            }
        }

        public void remove(Entity entity) {
            this.byUuid.remove(entity.uuid);
            this.byId.remove(entity.id);
        }

        @Nullable
        public Entity getEntity(int id) {
            return this.byId.get(id);
        }

        @Nullable
        public Entity getEntity(@NotNull UUID uuid) {
            return this.byUuid.get(uuid);
        }

        public int count() {
            return this.byUuid.size();
        }
    }

    private static class ISGEntitySectionStorage extends EntitySectionStorage<Entity> {
        public ISGEntitySectionStorage(EntitySectionStorage<Entity> sectionStorage) {
            super(Entity.class, sectionStorage.intialSectionVisibility);
            this.sections = sectionStorage.sections;
            this.sectionIds = sectionStorage.sectionIds;
        }

        public void forEachAccessibleNonEmptySection(AABB boundingBox, @NotNull AbortableIterationConsumer<EntitySection<Entity>> consumer) {
            int padding = 2;
            int minX = SectionPos.posToSectionCoord(boundingBox.minX - (double) padding);
            int minY = SectionPos.posToSectionCoord(boundingBox.minY - (double) (padding * 2));
            int minZ = SectionPos.posToSectionCoord(boundingBox.minZ - (double) padding);
            int maxX = SectionPos.posToSectionCoord(boundingBox.maxX + (double) padding);
            int maxY = SectionPos.posToSectionCoord(boundingBox.maxY);
            int maxZ = SectionPos.posToSectionCoord(boundingBox.maxZ + (double) padding);

            for (int x = minX; x <= maxX; ++x) {
                long minSectionKey = SectionPos.asLong(x, 0, 0);
                long maxSectionKey = SectionPos.asLong(x, -1, -1);
                LongIterator iterator = this.sectionIds.subSet(minSectionKey, maxSectionKey + 1L).iterator();

                while (iterator.hasNext()) {
                    long sectionPos = iterator.nextLong();
                    int y = SectionPos.y(sectionPos);
                    int z = SectionPos.z(sectionPos);
                    if (y >= minY && y <= maxY && z >= minZ && z <= maxZ) {
                        EntitySection<Entity> section = this.sections.get(sectionPos);
                        if (section != null && !section.isEmpty() && section.getStatus().isAccessible() && consumer.accept(section).shouldAbort())
                            return;
                    }
                }
            }
        }

        public @NotNull LongStream getExistingSectionPositionsInChunk(long chunkPos) {
            LongSortedSet chunkSections = this.getChunkSections(ChunkPos.getX(chunkPos), ChunkPos.getZ(chunkPos));
            if (!chunkSections.isEmpty())
                return StreamSupport.longStream(Spliterators.spliteratorUnknownSize(chunkSections.iterator(), Spliterator.ORDERED | Spliterator.DISTINCT | Spliterator.SORTED | Spliterator.NONNULL | Spliterator.IMMUTABLE), false);
            return LongStream.empty();
        }

        public @NotNull LongSortedSet getChunkSections(int chunkX, int chunkZ) {
            long minSectionKey = SectionPos.asLong(chunkX, 0, chunkZ);
            long maxSectionKey = SectionPos.asLong(chunkX, -1, chunkZ);
            return this.sectionIds.subSet(minSectionKey, maxSectionKey + 1L);
        }

        public @NotNull Stream<EntitySection<Entity>> getExistingSectionsInChunk(long chunkPos) {
            return this.getExistingSectionPositionsInChunk(chunkPos).mapToObj(this.sections::get).filter(Objects::nonNull);
        }

        public static long getChunkKeyFromSectionKey(long sectionPos) {
            return ChunkPos.asLong(SectionPos.x(sectionPos), SectionPos.z(sectionPos));
        }

        public @NotNull EntitySection<Entity> getOrCreateSection(long sectionPos) {
            return this.sections.computeIfAbsent(sectionPos, this::createSection);
        }

        @Nullable
        public EntitySection<Entity> getSection(long sectionPos) {
            return this.sections.get(sectionPos);
        }

        public @NotNull EntitySection<Entity> createSection(long sectionPos) {
            this.sectionIds.add(sectionPos);
            return new EntitySection<>(this.entityClass, this.intialSectionVisibility.get(getChunkKeyFromSectionKey(sectionPos)));
        }

        public @NotNull LongSet getAllChunksWithExistingSections() {
            LongSet chunks = new LongOpenHashSet();
            this.sections.keySet().forEach((sectionPos) -> chunks.add(getChunkKeyFromSectionKey(sectionPos)));
            return chunks;
        }

        public void getEntities(@NotNull AABB boundingBox, @NotNull AbortableIterationConsumer<Entity> consumer) {
            this.forEachAccessibleNonEmptySection(boundingBox, (section) -> section.getEntities(boundingBox, consumer));
        }

        public <U extends Entity> void getEntities(@NotNull EntityTypeTest<Entity, U> typeTest, @NotNull AABB boundingBox, @NotNull AbortableIterationConsumer<U> consumer) {
            this.forEachAccessibleNonEmptySection(boundingBox, (section) -> section.getEntities(typeTest, boundingBox, consumer));
        }

        public void remove(long sectionPos) {
            this.sections.remove(sectionPos);
            this.sectionIds.remove(sectionPos);
        }

        public void removeEntity(Entity entity) {
            this.sections.values().forEach(section -> section.storage.byClass.forEach((type, list) -> {
                if (type.isInstance(entity))
                    list.remove(entity);
            }));
            this.sections.values().forEach(section -> section.storage.allInstances.removeIf(e -> e.uuid.equals(entity.uuid)));
        }

        @Override
        @VisibleForDebug
        public int count() {
            return this.sectionIds.size();
        }
    }

    private static class ISGLevelEntityGetterAdapter extends LevelEntityGetterAdapter<Entity> {
        private final ISGEntityLookup visibleEntities;
        private final ISGEntitySectionStorage sectionStorage;

        public ISGLevelEntityGetterAdapter(ISGEntityLookup visibleEntityStorage, ISGEntitySectionStorage sectionStorage) {
            super(new EntityLookup<>(), new EntitySectionStorage<>(Entity.class, new Long2ObjectOpenHashMap<>()));
            this.visibleEntities = visibleEntityStorage;
            this.sectionStorage = sectionStorage;
        }


        @Nullable
        public Entity get(int id) {
            return this.visibleEntities.getEntity(id);
        }

        @Nullable
        public Entity get(@NotNull UUID uuid) {
            return this.visibleEntities.getEntity(uuid);
        }

        public @NotNull Iterable<Entity> getAll() {
            return this.visibleEntities.getAllEntities();
        }

        public <U extends Entity> void get(@NotNull EntityTypeTest<Entity, U> entityTypeTest, @NotNull AbortableIterationConsumer<U> consumer) {
            this.visibleEntities.getEntities(entityTypeTest, consumer);
        }

        public void get(@NotNull AABB boundingBox, @NotNull Consumer<Entity> consumer) {
            this.sectionStorage.getEntities(boundingBox, AbortableIterationConsumer.forConsumer(consumer));
        }

        public <U extends Entity> void get(@NotNull EntityTypeTest<Entity, U> entityTypeTest, @NotNull AABB boundingBox, @NotNull AbortableIterationConsumer<U> consumer) {
            this.sectionStorage.getEntities(entityTypeTest, boundingBox, consumer);
        }
    }
}
