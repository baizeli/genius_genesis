package miku.united_as_one.genesis.client.handlers;

import miku.united_as_one.genesis.compat.curios.GenesisCurios;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.item.curios.EternalRing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Genesis.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class EternalRingClientEvents {
    private EternalRingClientEvents() {
    }

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Pre event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (event.getOverlay().id().equals(VanillaGuiOverlay.FROSTBITE.id())
                && player != null
                && GenesisCurios.has(player, EternalRing::test)) {
            event.setCanceled(true);
        }
    }
}
