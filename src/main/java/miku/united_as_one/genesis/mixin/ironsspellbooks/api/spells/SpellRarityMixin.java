package miku.united_as_one.genesis.mixin.ironsspellbooks.api.spells;

import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import miku.united_as_one.genesis.spell.InnateRarity;
import miku.united_as_one.genesis.spell.DevSpellRarityFallback;
import net.minecraft.ChatFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SpellRarity.class, remap = false)
public abstract class SpellRarityMixin {
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void genesis$installInnateRarity(CallbackInfo ci) {
        DevSpellRarityFallback.ensureInnateExists();
    }

    @Inject(method = "getChatFormatting", at = @At("HEAD"), cancellable = true)
    private void genesis$getInnateFormatting(CallbackInfoReturnable<ChatFormatting> cir) {
        if (InnateRarity.is((SpellRarity) (Object) this)) cir.setReturnValue(ChatFormatting.YELLOW);
    }
}
