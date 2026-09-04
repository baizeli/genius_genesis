package miku.united_as_one.genesis.mixin.ironsspellbooks.gui;

import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import miku.united_as_one.genesis.client.spellhud.SpellCardHudClientEvents;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(value = ClientMagicData.class, remap = false)
public abstract class ClientCastingStateMixin {
    @Inject(method = "setClientCastState(Ljava/lang/String;II"
            + "Lio/redspace/ironsspellbooks/api/spells/CastSource;Ljava/lang/String;)V",
            at = @At("RETURN"), require = 1, remap = false)
    private static void geniusGenesis$startCardCastingAnimation(
            String spellId, int spellLevel, int castDuration,
            CastSource castSource, String slot, CallbackInfo callbackInfo) {
        SpellCardHudClientEvents.animation().signalCastingStarted(Util.getMillis());
    }

    @Inject(method = "resetClientCastState(Ljava/util/UUID;)V",
            at = @At("RETURN"), require = 1, remap = false)
    private static void geniusGenesis$stopCardCastingAnimation(
            UUID casterId, CallbackInfo callbackInfo) {
        var player = Minecraft.getInstance().player;
        if (player != null && player.getUUID().equals(casterId)) {
            SpellCardHudClientEvents.animation().signalCastingStopped();
        }
    }
}
