package miku.united_as_one.genesis.mixin.ironsspellbooks.gui;

import miku.united_as_one.genesis.client.spellhud.SpellCardHudClientEvents;
import io.redspace.ironsspellbooks.gui.overlays.SpellBarOverlay;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SpellBarOverlay.class, remap = false)
public abstract class SpellBarOverlayMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true, remap = false)
    private void geniusGenesis$replaceSpellBar(ForgeGui forgeGui, GuiGraphics graphics,
                                               float partialTick, int screenWidth,
                                               int screenHeight, CallbackInfo callbackInfo) {
        if (SpellCardHudClientEvents.shouldHideOriginalBar()) {
            callbackInfo.cancel();
        }
    }
}
