package miku.united_as_one.genesis.mixin.ironsspellbooks.gui.arcane_anvil;

import io.redspace.ironsspellbooks.gui.arcane_anvil.ArcaneAnvilMenu;
import miku.united_as_one.genesis.content.item.InfiniteShrivingStoneItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ArcaneAnvilMenu.class)
public class ArcaneAnvilMenuMixin {
    @Redirect(
            method = "createResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"
            )
    )
    private boolean geniusGenesis$acceptInfiniteShrivingStone(ItemStack stack, Item shrivingStone) {
        return stack.is(shrivingStone) || stack.getItem() instanceof InfiniteShrivingStoneItem;
    }

    @Redirect(
            method = "onTake",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"
            )
    )
    private void geniusGenesis$keepInfiniteShrivingStone(ItemStack stack, int count) {
        if (!(stack.getItem() instanceof InfiniteShrivingStoneItem)) {
            stack.shrink(count);
        }
    }
}
