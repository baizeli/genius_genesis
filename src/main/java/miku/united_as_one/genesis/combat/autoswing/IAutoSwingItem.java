package miku.united_as_one.genesis.combat.autoswing;

import net.minecraft.world.item.ItemStack;

public interface IAutoSwingItem {
    SwingPipeline getSwingPipeline(ItemStack stack);
}
