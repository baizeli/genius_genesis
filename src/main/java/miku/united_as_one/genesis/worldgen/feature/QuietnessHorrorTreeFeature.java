package miku.united_as_one.genesis.worldgen.feature;

import com.mojang.serialization.Codec;
import miku.united_as_one.genesis.registries.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.HashSet;
import java.util.Set;

public class QuietnessHorrorTreeFeature extends Feature<NoneFeatureConfiguration> {
    private static final int SET_BLOCK_FLAGS = 2;

    public QuietnessHorrorTreeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        BlockPos groundPos = origin.below();

        if (!isValidGround(level.getBlockState(groundPos))) {
            return false;
        }

        int height = 4 + random.nextInt(3);
        if (origin.getY() < level.getMinBuildHeight() + 1 || origin.getY() + height + 2 >= level.getMaxBuildHeight()) {
            return false;
        }
        if (!hasFlatFooting(level, groundPos) || !canPlaceTree(level, origin, height)) {
            return false;
        }

        BlockState logY = logState(Direction.Axis.Y);
        Set<BlockPos> logs = new HashSet<>();

        level.setBlock(groundPos, BlockRegistry.QUIETNESS_GRASS.get().defaultBlockState(), SET_BLOCK_FLAGS);
        for (int y = 0; y < height; y++) {
            placeLog(level, logs, origin.above(y), logY);
        }

        placeOakCanopy(level, origin, height);
        return true;
    }

    private static void placeOakCanopy(WorldGenLevel level, BlockPos origin, int height) {
        BlockState leaves = BlockRegistry.QUIETNESS_PLANKS.getLeaves().orElseThrow().get().defaultBlockState();
        for (int y = height - 2; y <= height; y++) {
            int radius = y == height ? 1 : 2;
            int distance = Math.max(1, height - y);
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) == radius && Math.abs(dz) == radius && y != height - 1) {
                        continue;
                    }
                    placeLeaf(level, origin.offset(dx, y, dz), leaves, distance);
                }
            }
        }
        placeLeaf(level, origin.above(height + 1), leaves, 1);
    }

    private static boolean canPlaceTree(WorldGenLevel level, BlockPos origin, int height) {
        for (int y = 0; y < height; y++) {
            if (!canReplace(level.getBlockState(origin.above(y)))) {
                return false;
            }
        }

        for (int y = height - 2; y <= height + 1; y++) {
            int radius = y >= height ? 1 : 2;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (!canReplace(level.getBlockState(origin.offset(dx, y, dz)))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static boolean hasFlatFooting(WorldGenLevel level, BlockPos groundPos) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos pos = groundPos.offset(dx, 0, dz);
                if (!isValidGround(level.getBlockState(pos)) || !level.isEmptyBlock(pos.above())) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isValidGround(BlockState state) {
        return state.is(Blocks.NETHERRACK)
                || state.is(Blocks.WARPED_NYLIUM)
                || state.is(Blocks.CRIMSON_NYLIUM)
                || state.is(BlockRegistry.GENESIS_DIRT.getBase().get())
                || state.is(BlockRegistry.QUIETNESS_GRASS.get());
    }

    private static boolean canReplace(BlockState state) {
        return state.isAir()
                || state.is(BlockTags.REPLACEABLE_BY_TREES)
                || state.is(BlockTags.LEAVES);
    }

    private static void placeLog(WorldGenLevel level, Set<BlockPos> logs, BlockPos pos, BlockState state) {
        level.setBlock(pos, state, SET_BLOCK_FLAGS);
        logs.add(pos.immutable());
    }

    private static void placeLeaf(WorldGenLevel level, BlockPos pos, BlockState leaves, int distance) {
        BlockState state = leaves;
        if (state.hasProperty(LeavesBlock.DISTANCE)) {
            state = state.setValue(LeavesBlock.DISTANCE, distance);
        }
        if (state.hasProperty(LeavesBlock.PERSISTENT)) {
            state = state.setValue(LeavesBlock.PERSISTENT, false);
        }
        if (canReplace(level.getBlockState(pos))) {
            level.setBlock(pos, state, SET_BLOCK_FLAGS);
        }
    }

    private static BlockState logState(Direction.Axis axis) {
        BlockState state = BlockRegistry.QUIETNESS_LOG.getBase().get().defaultBlockState();
        if (state.hasProperty(RotatedPillarBlock.AXIS)) {
            state = state.setValue(RotatedPillarBlock.AXIS, axis);
        }
        return state;
    }

}
