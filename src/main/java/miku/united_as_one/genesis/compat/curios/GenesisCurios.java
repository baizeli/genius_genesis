package miku.united_as_one.genesis.compat.curios;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import miku.bai_ze_li.genesis.api.equipment.EquipmentStatsManager;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

public final class GenesisCurios {
    private static final TickSnapshotCache<EntityKey, List<ItemStack>> SNAPSHOTS = new TickSnapshotCache<>();

    private GenesisCurios() {
    }

    public static boolean has(@Nullable LivingEntity entity, Predicate<ItemStack> predicate) {
        if (entity == null) {
            return false;
        }
        for (ItemStack stack : equipped(entity)) {
            if (predicate.test(stack)) {
                return true;
            }
        }
        return false;
    }

    public static List<ItemStack> equipped(LivingEntity entity) {
        EntityKey key = EntityKey.of(entity);
        return SNAPSHOTS.get(key, entity.level().getGameTime(), () -> loadEquipped(entity));
    }

    public static void invalidate(LivingEntity entity) {
        SNAPSHOTS.invalidate(EntityKey.of(entity));
    }

    public static void clear() {
        SNAPSHOTS.clear();
    }

    public static void refreshConfiguredAttributes(ServerPlayer player) {
        CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            for (ICurioStacksHandler stacksHandler : handler.getCurios().values()) {
                for (int index = 0; index < stacksHandler.getStacks().getSlots(); index++) {
                    ItemStack stack = stacksHandler.getStacks().getStackInSlot(index);
                    if (!stack.isEmpty()) {
                        EquipmentStatsManager.refreshAccessoryModifiers(player, stack,
                                slotKey(stacksHandler.getIdentifier(), index));
                    }
                }
            }
        });
        invalidate(player);
    }

    public static String slotKey(String identifier, int index) {
        return "curio:" + identifier + ":" + index;
    }

    private static List<ItemStack> loadEquipped(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity).map(handler -> {
            List<ItemStack> equipped = new ArrayList<>();
            for (ICurioStacksHandler stacksHandler : handler.getCurios().values()) {
                for (int index = 0; index < stacksHandler.getStacks().getSlots(); index++) {
                    ItemStack stack = stacksHandler.getStacks().getStackInSlot(index);
                    if (!stack.isEmpty()) {
                        equipped.add(stack);
                    }
                }
            }
            return List.copyOf(equipped);
        }).orElseGet(List::of);
    }

    private record EntityKey(boolean clientSide, ResourceKey<Level> dimension, UUID uuid) {
        private static EntityKey of(LivingEntity entity) {
            return new EntityKey(entity.level().isClientSide, entity.level().dimension(), entity.getUUID());
        }
    }
}
