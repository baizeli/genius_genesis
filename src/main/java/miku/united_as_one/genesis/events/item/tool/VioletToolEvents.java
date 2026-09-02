package miku.united_as_one.genesis.events.item.tool;

import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.content.item.tool.VioletAxe;
import miku.united_as_one.genesis.content.item.tool.VioletPickaxe;
import miku.united_as_one.genesis.content.item.tool.VioletShovel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

@Mod.EventBusSubscriber(modid = Genesis.MOD_ID)
public final class VioletToolEvents {
    private VioletToolEvents() {
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof Level level) || level.isClientSide) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof VioletAxe && event.getState().is(BlockTags.LOGS)) {
            breakTree(level, event.getPos(), player);
        } else if (stack.getItem() instanceof VioletPickaxe && isOre(event.getState())) {
            performChainMining(level, event.getPos(), player, event.getState().getBlock());
        } else if (stack.getItem() instanceof VioletShovel && player.isShiftKeyDown()) {
            breakFiveByFive(level, event.getPos(), player, stack);
        }
    }

    private static boolean isOre(BlockState state) {
        return state.is(Tags.Blocks.ORES)
                || state.is(BlockTags.COAL_ORES)
                || state.is(BlockTags.IRON_ORES)
                || state.is(BlockTags.GOLD_ORES)
                || state.is(BlockTags.DIAMOND_ORES)
                || state.is(BlockTags.REDSTONE_ORES)
                || state.is(BlockTags.LAPIS_ORES)
                || state.is(BlockTags.EMERALD_ORES)
                || state.is(BlockTags.COPPER_ORES);
    }

    private static void breakTree(Level level, BlockPos pos, Player player) {
        Set<BlockPos> logs = new HashSet<>();
        findLogs(level, pos, logs);
        for (BlockPos logPos : logs) {
            level.destroyBlock(logPos, true, player);
        }
    }

    private static void findLogs(Level level, BlockPos pos, Set<BlockPos> found) {
        if (found.size() > 128 || found.contains(pos)) {
            return;
        }

        BlockState state = level.getBlockState(pos);
        if (!state.is(BlockTags.LOGS)) {
            return;
        }

        found.add(pos);
        for (BlockPos neighbor : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
            findLogs(level, neighbor.immutable(), found);
        }
    }

    private static void performChainMining(Level level, BlockPos startPos, Player player, Block targetBlock) {
        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();
        queue.add(startPos);
        visited.add(startPos);

        int count = 0;
        int maxBlocks = 64;
        while (!queue.isEmpty() && count < maxBlocks) {
            BlockPos current = queue.poll();
            if (!current.equals(startPos)) {
                BlockState state = level.getBlockState(current);
                if (!state.is(targetBlock)) {
                    continue;
                }
                level.destroyBlock(current, true, player);
                count++;
            }

            for (BlockPos neighbor : BlockPos.betweenClosed(current.offset(-1, -1, -1), current.offset(1, 1, 1))) {
                BlockPos immutablePos = neighbor.immutable();
                if (visited.add(immutablePos)) {
                    queue.add(immutablePos);
                }
            }
        }
    }

    private static void breakFiveByFive(Level level, BlockPos pos, Player player, ItemStack stack) {
        if (!(player.pick(5.0D, 0.0F, false) instanceof BlockHitResult blockHit)) {
            return;
        }

        Direction face = blockHit.getDirection();
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }

                BlockPos target;
                if (face.getAxis() == Direction.Axis.Y) {
                    target = pos.offset(i, 0, j);
                } else if (face.getAxis() == Direction.Axis.X) {
                    target = pos.offset(0, i, j);
                } else {
                    target = pos.offset(i, j, 0);
                }

                BlockState state = level.getBlockState(target);
                if (stack.isCorrectToolForDrops(state) && state.getDestroySpeed(level, target) >= 0.0F) {
                    level.destroyBlock(target, true, player);
                }
            }
        }
    }
}
