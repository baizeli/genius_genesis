package miku.united_as_one.genesis.mixin.ironsspellbooks.item;

import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.item.InkItem;
import miku.united_as_one.genesis.registries.ItemRegistry;
import miku.united_as_one.genesis.content.spell.InnateRarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = InkItem.class, remap = false)
public abstract class InkItemMixin {
    @Inject(method = "getInkForRarity", at = @At("HEAD"), cancellable = true)
    private static void genesis$getInnateInk(SpellRarity rarity, CallbackInfoReturnable<InkItem> cir) {
        if (InnateRarity.is(rarity)) cir.setReturnValue(ItemRegistry.INNATE_INK.get());
    }
}
