package miku.united_as_one.genesis.worldgen.feature;

import com.mojang.serialization.Codec;
import miku.united_as_one.genesis.registries.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SourceSkyrisTreeFeature extends Feature<NoneFeatureConfiguration> {
    private static final int SET_BLOCK_FLAGS = 2;
    private static final int MAX_LEAF_DISTANCE = 6;

    public SourceSkyrisTreeFeature(Codec<NoneFeatureConfiguration> codec) {
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

        int height = 10 + random.nextInt(5);
        if (origin.getY() < level.getMinBuildHeight() + 1 || origin.getY() + height + 2 >= level.getMaxBuildHeight()) {
            return false;
        }
        if (!canPlaceTrunk(level, origin, height)) {
            return false;
        }

        BlockState logY = logState(Direction.Axis.Y);
        BlockState sourceDirt = BlockRegistry.SOURCE_DIRT.getBase().get().defaultBlockState();
        Set<BlockPos> logs = new HashSet<>();
        Set<BlockPos> leafCandidates = new HashSet<>();

        level.setBlock(groundPos, sourceDirt, SET_BLOCK_FLAGS);
        for (int y = 0; y < height; y++) {
            BlockPos logPos = origin.above(y);
            placeLog(level, logs, logPos, logY);
        }

        addCanopy(level, random, origin, logs, leafCandidates, height);
        List<BlockPos> leaves = placeConnectedLeaves(level, logs, leafCandidates);
        placeSourceCrystal(level, random, leaves);
        return true;
    }

    private static void addCanopy(
            WorldGenLevel level,
            RandomSource random,
            BlockPos origin,
            Set<BlockPos> logs,
            Set<BlockPos> leafCandidates,
            int height
    ) {
        addLeafDisk(leafCandidates, origin.above(height - 5), 4, 1, random);
        addLeafDisk(leafCandidates, origin.above(height - 4), 5, 0, random);
        addLeafDisk(leafCandidates, origin.above(height - 3), 4, 1, random);
        addLeafDisk(leafCandidates, origin.above(height - 1), 3, 1, random);
        addLeafDisk(leafCandidates, origin.above(height), 2, 1, random);
        leafCandidates.add(origin.above(height + 1));

        List<Direction> branchDirections = shuffledHorizontalDirections(random);
        int branchCount = 3 + random.nextInt(2);
        for (int i = 0; i < branchCount; i++) {
            Direction direction = branchDirections.get(i);
            int branchY = height - 6 + random.nextInt(4);
            int branchLength = 2 + random.nextInt(3);
            BlockPos branchStart = origin.above(branchY);
            BlockState branchLog = logState(direction.getAxis());
            BlockPos tip = branchStart;

            for (int step = 1; step <= branchLength; step++) {
                BlockPos branchPos = branchStart.relative(direction, step);
                if (!canReplace(level.getBlockState(branchPos))) {
                    break;
                }
                placeLog(level, logs, branchPos, branchLog);
                tip = branchPos;
            }

            addLeafDisk(leafCandidates, tip, 3, 1, random);
            addLeafDisk(leafCandidates, tip.above(), 2, 1, random);
        }
    }

    private static List<BlockPos> placeConnectedLeaves(WorldGenLevel level, Set<BlockPos> logs, Set<BlockPos> candidates) {
        Map<BlockPos, Integer> distances = new HashMap<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        for (BlockPos log : logs) {
            distances.put(log, 0);
            queue.add(log);
        }

        while (!queue.isEmpty()) {
            BlockPos current = queue.removeFirst();
            int distance = distances.get(current);
            if (distance >= MAX_LEAF_DISTANCE) {
                continue;
            }

            for (Direction direction : Direction.values()) {
                BlockPos next = current.relative(direction);
                if (!candidates.contains(next) || distances.containsKey(next)) {
                    continue;
                }
                distances.put(next, distance + 1);
                queue.add(next);
            }
        }

        BlockState leaves = BlockRegistry.SOURCE_PLANKS.getLeaves().orElseThrow().get().defaultBlockState();
        List<BlockPos> placedLeaves = distances.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .map(entry -> {
                    BlockPos pos = entry.getKey();
                    BlockState state = leaves;
                    if (state.hasProperty(LeavesBlock.DISTANCE)) {
                        state = state.setValue(LeavesBlock.DISTANCE, entry.getValue());
                    }
                    if (state.hasProperty(LeavesBlock.PERSISTENT)) {
                        state = state.setValue(LeavesBlock.PERSISTENT, false);
                    }
                    if (canReplace(level.getBlockState(pos))) {
                        level.setBlock(pos, state, SET_BLOCK_FLAGS);
                        return pos;
                    }
                    return null;
                })
                .filter(pos -> pos != null)
                .toList();
        return new ArrayList<>(placedLeaves);
    }

    private static void placeSourceCrystal(WorldGenLevel level, RandomSource random, List<BlockPos> leaves) {
        if (leaves.isEmpty() || random.nextFloat() > 0.12F) {
            return;
        }

        for (int attempts = 0; attempts < 12; attempts++) {
            BlockPos leaf = leaves.get(random.nextInt(leaves.size()));
            BlockPos crystalPos = leaf.below();
            if (level.isEmptyBlock(crystalPos)) {
                level.setBlock(crystalPos, BlockRegistry.SOURCE_CRYSTAL_BLOCK.get().defaultBlockState(), SET_BLOCK_FLAGS);
                return;
            }
        }
    }

    private static void addLeafDisk(Set<BlockPos> leaves, BlockPos center, int radius, int edgeAllowance, RandomSource random) {
        int radiusSquared = radius * radius;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int distanceSquared = dx * dx + dz * dz;
                if (distanceSquared > radiusSquared + edgeAllowance) {
                    continue;
                }
                if (distanceSquared > radiusSquared - radius && random.nextInt(4) == 0) {
                    continue;
                }
                leaves.add(center.offset(dx, 0, dz));
            }
        }
    }

    private static boolean canPlaceTrunk(WorldGenLevel level, BlockPos origin, int height) {
        for (int y = 0; y < height; y++) {
            if (!canReplace(level.getBlockState(origin.above(y)))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidGround(BlockState state) {
        return state.is(BlockTags.DIRT)
                || state.is(BlockRegistry.SOURCE_DIRT.getBase().get())
                || state.is(BlockRegistry.SOURCE_DIRT.getGrass().orElseThrow().get());
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

    private static BlockState logState(Direction.Axis axis) {
        BlockState state = BlockRegistry.SOURCE_LOG.getBase().get().defaultBlockState();
        if (state.hasProperty(RotatedPillarBlock.AXIS)) {
            state = state.setValue(RotatedPillarBlock.AXIS, axis);
        }
        return state;
    }

    private static List<Direction> shuffledHorizontalDirections(RandomSource random) {
        List<Direction> directions = new ArrayList<>(List.of(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST));
        for (int i = directions.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Direction tmp = directions.get(i);
            directions.set(i, directions.get(j));
            directions.set(j, tmp);
        }
        return directions;
    }
}
