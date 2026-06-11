package miku.united_as_one.genesis.worldgen.feature;

import com.mojang.serialization.Codec;
import miku.united_as_one.genesis.registries.BlockRegistry;
import miku.united_as_one.genesis.worldgen.ModBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class SourceForestTerrainFeature extends Feature<NoneFeatureConfiguration> {
    private static final int SET_BLOCK_FLAGS = 2;
    private static final int SOIL_DEPTH = 3;
    private static final int SHALLOW_SOURCE_STONE_DEPTH = 18;

    private enum SurfaceKind {
        LAND,
        BEACH,
        UNDERWATER
    }

    public SourceForestTerrainFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        ChunkPos chunkPos = new ChunkPos(context.origin());
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int minY = level.getMinBuildHeight();
        int seaLevel = level.getSeaLevel();
        boolean changed = false;

        for (int x = chunkPos.getMinBlockX(); x <= chunkPos.getMaxBlockX(); x++) {
            for (int z = chunkPos.getMinBlockZ(); z <= chunkPos.getMaxBlockZ(); z++) {
                int topY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - 1;
                if (topY < minY) {
                    continue;
                }

                pos.set(x, topY, z);
                if (!level.getBiome(pos).is(ModBiomes.SOURCE_FOREST)) {
                    continue;
                }

                int groundY = findTopTerrain(level, pos, x, z, topY, minY);
                if (groundY < minY) {
                    continue;
                }

                SurfaceKind surfaceKind = surfaceKind(level, pos, x, z, groundY, seaLevel);
                changed |= replaceColumn(level, pos, x, z, groundY, minY, surfaceKind);
            }
        }

        return changed;
    }

    private static int findTopTerrain(WorldGenLevel level, BlockPos.MutableBlockPos pos, int x, int z, int topY, int minY) {
        for (int y = topY; y >= minY; y--) {
            pos.set(x, y, z);
            BlockState state = level.getBlockState(pos);
            if (state.isAir() || !state.getFluidState().isEmpty()) {
                continue;
            }
            if (isNaturalTerrain(state)) {
                return y;
            }
        }
        return minY - 1;
    }

    private static SurfaceKind surfaceKind(
            WorldGenLevel level,
            BlockPos.MutableBlockPos pos,
            int x,
            int z,
            int groundY,
            int seaLevel
    ) {
        pos.set(x, groundY + 1, z);
        boolean underwater = !level.getFluidState(pos).isEmpty();
        if (groundY < seaLevel - 3 || groundY > seaLevel + 1) {
            return underwater ? SurfaceKind.UNDERWATER : SurfaceKind.LAND;
        }

        if (underwater) {
            return SurfaceKind.BEACH;
        }

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (Math.abs(dx) + Math.abs(dz) != 1) {
                    continue;
                }
                for (int y = groundY; y <= seaLevel; y++) {
                    pos.set(x + dx, y, z + dz);
                    if (!level.getFluidState(pos).isEmpty()) {
                        return SurfaceKind.BEACH;
                    }
                }
            }
        }
        return SurfaceKind.LAND;
    }

    private static boolean replaceColumn(
            WorldGenLevel level,
            BlockPos.MutableBlockPos pos,
            int x,
            int z,
            int groundY,
            int minY,
            SurfaceKind surfaceKind
    ) {
        BlockState sourceGrass = BlockRegistry.SOURCE_DIRT.getGrass().orElseThrow().get().defaultBlockState();
        BlockState sourceDirt = BlockRegistry.SOURCE_DIRT.getBase().get().defaultBlockState();
        BlockState sourceSand = BlockRegistry.SOURCE_SAND.getBase().get().defaultBlockState();
        BlockState sourceStone = BlockRegistry.SOURCE_STONE.getBase().get().defaultBlockState();
        boolean changed = false;

        int bottomY = Math.max(minY, groundY - SHALLOW_SOURCE_STONE_DEPTH);
        for (int y = groundY; y >= bottomY; y--) {
            pos.set(x, y, z);
            BlockState current = level.getBlockState(pos);
            int depth = groundY - y;
            if ((current.isAir() || !current.getFluidState().isEmpty()) && depth > SOIL_DEPTH) {
                level.setBlock(pos, sourceStone, SET_BLOCK_FLAGS);
                changed = true;
                continue;
            }
            if (!isNaturalTerrain(current)) {
                continue;
            }

            BlockState replacement;
            if (depth == 0) {
                replacement = switch (surfaceKind) {
                    case BEACH -> sourceSand;
                    case UNDERWATER -> sourceStone;
                    case LAND -> sourceGrass;
                };
            } else if (depth <= SOIL_DEPTH) {
                replacement = sourceDirt;
            } else {
                replacement = sourceStone;
            }

            if (!current.is(replacement.getBlock())) {
                level.setBlock(pos, replacement, SET_BLOCK_FLAGS);
                changed = true;
            }
        }

        return changed;
    }

    private static boolean isNaturalTerrain(BlockState state) {
        if (state.isAir() || !state.getFluidState().isEmpty()) {
            return false;
        }

        Block block = state.getBlock();
        return state.is(BlockTags.BASE_STONE_OVERWORLD)
                || state.is(BlockTags.STONE_ORE_REPLACEABLES)
                || state.is(BlockTags.DEEPSLATE_ORE_REPLACEABLES)
                || state.is(BlockTags.DIRT)
                || state.is(BlockTags.SAND)
                || block == Blocks.GRAVEL
                || block == Blocks.SANDSTONE
                || block == Blocks.RED_SANDSTONE
                || block == Blocks.CLAY
                || block == BlockRegistry.SOURCE_DIRT.getBase().get()
                || block == BlockRegistry.SOURCE_DIRT.getGrass().orElseThrow().get()
                || block == BlockRegistry.SOURCE_SAND.getBase().get()
                || block == BlockRegistry.SOURCE_STONE.getBase().get();
    }
}
