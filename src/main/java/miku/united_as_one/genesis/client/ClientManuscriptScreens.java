package miku.united_as_one.genesis.client;

import miku.united_as_one.genesis.client.gui.manuscript.ChaosSpellLearningScreen;
import miku.united_as_one.genesis.client.gui.manuscript.CelestialSourceSpellLearningScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;

public final class ClientManuscriptScreens {
    private ClientManuscriptScreens() {
    }

    public static void openCelestialSource(InteractionHand hand) {
        Minecraft.getInstance().setScreen(new CelestialSourceSpellLearningScreen(
                Component.translatable("item.genius_genesis.celestial_source_manuscript"),
                hand
        ));
    }

    public static void openChaos(InteractionHand hand) {
        Minecraft.getInstance().setScreen(new ChaosSpellLearningScreen(
                Component.translatable("item.genius_genesis.chaos_manuscript"),
                hand
        ));
    }
}
