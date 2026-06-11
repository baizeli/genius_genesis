package miku.united_as_one.genesis.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.datafixers.util.Pair;
import miku.united_as_one.genesis.worldgen.ModBiomes;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Optional;
import java.util.Set;

public final class GenesisCommands {
    private static final int DEFAULT_RADIUS_CHUNKS = 1024;
    private static final int MAX_RADIUS_CHUNKS = 4096;
    private static final int BIOME_SEARCH_HORIZONTAL_STEP = 32;
    private static final int BIOME_SEARCH_VERTICAL_STEP = 64;
    private static final int SAFE_SURFACE_SEARCH_RADIUS = 160;
    private static final int SAFE_SURFACE_SEARCH_STEP = 4;

    private GenesisCommands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("genesis")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("source_forest")
                        .executes(context -> teleportToSourceForest(context.getSource(), DEFAULT_RADIUS_CHUNKS))
                        .then(Commands.argument("radius_chunks", IntegerArgumentType.integer(1, MAX_RADIUS_CHUNKS))
                                .executes(context -> teleportToSourceForest(
                                        context.getSource(),
                                        IntegerArgumentType.getInteger(context, "radius_chunks")
                                )))));

        dispatcher.register(Commands.literal("source_forest_tp")
                .requires(source -> source.hasPermission(2))
                .executes(context -> teleportToSourceForest(context.getSource(), DEFAULT_RADIUS_CHUNKS))
                .then(Commands.argument("radius_chunks", IntegerArgumentType.integer(1, MAX_RADIUS_CHUNKS))
                        .executes(context -> teleportToSourceForest(
                                context.getSource(),
                                IntegerArgumentType.getInteger(context, "radius_chunks")
                        ))));
    }

    private static int teleportToSourceForest(CommandSourceStack source, int radiusChunks) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ServerLevel level = source.getLevel();
        int radiusBlocks = radiusChunks * 16;
        BlockPos origin = new BlockPos(player.getBlockX(), level.getSeaLevel(), player.getBlockZ());

        source.sendSuccess(() -> Component.literal("Scanning for genius_genesis:source_forest within "
                + radiusChunks + " chunks..."), false);

        Pair<BlockPos, Holder<Biome>> result = level.findClosestBiome3d(
                biome -> biome.is(ModBiomes.SOURCE_FOREST),
                origin,
                radiusBlocks,
                BIOME_SEARCH_HORIZONTAL_STEP,
                BIOME_SEARCH_VERTICAL_STEP
        );

        if (result == null) {
            source.sendFailure(Component.literal("No genius_genesis:source_forest found within "
                    + radiusChunks + " chunks. Try /genesis source_forest " + MAX_RADIUS_CHUNKS));
            return 0;
        }

        BlockPos biomePos = result.getFirst();
        BlockPos teleportPos = findSafeSurface(level, biomePos).orElseGet(() -> fallbackSurface(level, biomePos));
        player.stopRiding();
        player.teleportTo(
                level,
                teleportPos.getX() + 0.5D,
                teleportPos.getY(),
                teleportPos.getZ() + 0.5D,
                Set.of(),
                player.getYRot(),
                player.getXRot()
        );
        player.resetFallDistance();

        source.sendSuccess(() -> Component.literal("Teleported to source forest at "
                + teleportPos.getX() + " " + teleportPos.getY() + " " + teleportPos.getZ()
                + " (biome sample " + biomePos.getX() + " " + biomePos.getY() + " " + biomePos.getZ() + ")"), true);
        return 1;
    }

    private static Optional<BlockPos> findSafeSurface(ServerLevel level, BlockPos center) {
        for (int radius = 0; radius <= SAFE_SURFACE_SEARCH_RADIUS; radius += SAFE_SURFACE_SEARCH_STEP) {
            Optional<BlockPos> pos = scanRing(level, center, radius);
            if (pos.isPresent()) {
                return pos;
            }
        }
        return Optional.empty();
    }

    private static Optional<BlockPos> scanRing(ServerLevel level, BlockPos center, int radius) {
        if (radius == 0) {
            return safeSurfaceAt(level, center.getX(), center.getZ());
        }

        for (int offset = -radius; offset <= radius; offset += SAFE_SURFACE_SEARCH_STEP) {
            Optional<BlockPos> north = safeSurfaceAt(level, center.getX() + offset, center.getZ() - radius);
            if (north.isPresent()) {
                return north;
            }
            Optional<BlockPos> south = safeSurfaceAt(level, center.getX() + offset, center.getZ() + radius);
            if (south.isPresent()) {
                return south;
            }
        }

        for (int offset = -radius + SAFE_SURFACE_SEARCH_STEP; offset <= radius - SAFE_SURFACE_SEARCH_STEP; offset += SAFE_SURFACE_SEARCH_STEP) {
            Optional<BlockPos> west = safeSurfaceAt(level, center.getX() - radius, center.getZ() + offset);
            if (west.isPresent()) {
                return west;
            }
            Optional<BlockPos> east = safeSurfaceAt(level, center.getX() + radius, center.getZ() + offset);
            if (east.isPresent()) {
                return east;
            }
        }

        return Optional.empty();
    }

    private static Optional<BlockPos> safeSurfaceAt(ServerLevel level, int x, int z) {
        Holder<Biome> biome = level.getUncachedNoiseBiome(
                QuartPos.fromBlock(x),
                QuartPos.fromBlock(level.getSeaLevel()),
                QuartPos.fromBlock(z)
        );
        if (!biome.is(ModBiomes.SOURCE_FOREST)) {
            return Optional.empty();
        }

        level.getChunk(x >> 4, z >> 4);
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        if (y <= level.getMinBuildHeight() + 1) {
            return Optional.empty();
        }

        BlockPos feet = new BlockPos(x, y, z);
        BlockPos groundPos = feet.below();
        BlockState ground = level.getBlockState(groundPos);
        if (ground.isAir() || ground.is(BlockTags.LEAVES) || !ground.getFluidState().isEmpty()) {
            return Optional.empty();
        }
        if (!level.getFluidState(feet).isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(feet);
    }

    private static BlockPos fallbackSurface(ServerLevel level, BlockPos biomePos) {
        level.getChunk(biomePos.getX() >> 4, biomePos.getZ() >> 4);
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, biomePos.getX(), biomePos.getZ()) + 2;
        return new BlockPos(biomePos.getX(), y, biomePos.getZ());
    }
}
