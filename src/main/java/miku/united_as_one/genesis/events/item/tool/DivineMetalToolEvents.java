package miku.united_as_one.genesis.events.item.tool;

import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.item.tool.DivineMetalHoe;
import miku.united_as_one.genesis.item.tool.DivineMetalPickaxe;
import miku.united_as_one.genesis.item.tool.DivineMetalShovel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = Genesis.MOD_ID)
public final class DivineMetalToolEvents {
    private DivineMetalToolEvents() {
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        Player player = event.getPlayer();
        if (player.isCreative()) {
            return;
        }

        ItemStack tool = player.getMainHandItem();
        if (tool.getItem() instanceof DivineMetalPickaxe) {
            smeltDrops(event, level, player, tool);
        } else if (tool.getItem() instanceof DivineMetalShovel) {
            glassSand(event, level, player);
        } else if (tool.getItem() instanceof DivineMetalHoe) {
            cookCrops(event, level, player, tool);
        }
    }

    private static void smeltDrops(BlockEvent.BreakEvent event, ServerLevel level, Player player, ItemStack tool) {
        BlockPos pos = event.getPos();
        BlockState state = event.getState();
        if (!state.canHarvestBlock(level, pos, player)) {
            return;
        }

        for (ItemStack drop : Block.getDrops(state, level, pos, level.getBlockEntity(pos), player, tool)) {
            Optional<SmeltingRecipe> recipe = level.getRecipeManager().getRecipeFor(
                    RecipeType.SMELTING,
                    new SimpleContainer(drop),
                    level
            );
            if (recipe.isPresent()) {
                ItemStack result = recipe.get().getResultItem(level.registryAccess());
                if (!result.isEmpty()) {
                    level.addFreshEntity(new ItemEntity(
                            level,
                            pos.getX() + 0.5D,
                            pos.getY() + 0.5D,
                            pos.getZ() + 0.5D,
                            result.copyWithCount(drop.getCount())
                    ));
                    level.addFreshEntity(new ExperienceOrb(
                            level,
                            pos.getX() + 0.5D,
                            pos.getY() + 0.5D,
                            pos.getZ() + 0.5D,
                            Math.max(1, (int) (recipe.get().getExperience() * drop.getCount()))
                    ));
                    level.sendParticles(ParticleTypes.FLAME, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 10, 0.5D, 0.5D, 0.5D, 0.01D);
                    continue;
                }
            }
            level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, drop));
        }

        level.destroyBlock(pos, false);
        event.setCanceled(true);
    }

    private static void glassSand(BlockEvent.BreakEvent event, ServerLevel level, Player player) {
        if (!player.isShiftKeyDown() || event.getState().getBlock() != Blocks.SAND) {
            return;
        }

        BlockPos pos = event.getPos();
        level.destroyBlock(pos, false);
        Block.popResource(level, pos, new ItemStack(Blocks.GLASS));
        level.sendParticles(ParticleTypes.FLAME, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 10, 0.5D, 0.5D, 0.5D, 0.01D);
        event.setCanceled(true);
    }

    private static void cookCrops(BlockEvent.BreakEvent event, ServerLevel level, Player player, ItemStack tool) {
        BlockState state = event.getState();
        if (state.getBlock() != Blocks.WHEAT && state.getBlock() != Blocks.POTATOES) {
            return;
        }

        BlockPos pos = event.getPos();
        List<ItemStack> drops = Block.getDrops(state, level, pos, level.getBlockEntity(pos), player, tool);
        level.destroyBlock(pos, false);
        for (ItemStack drop : drops) {
            ItemStack cooked = drop;
            if (state.getBlock() == Blocks.WHEAT && drop.is(Items.WHEAT)) {
                cooked = new ItemStack(Items.BREAD, drop.getCount());
            } else if (state.getBlock() == Blocks.POTATOES && drop.is(Items.POTATO)) {
                cooked = new ItemStack(Items.BAKED_POTATO, drop.getCount());
            }
            level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, cooked));
        }
        event.setCanceled(true);
    }
}
