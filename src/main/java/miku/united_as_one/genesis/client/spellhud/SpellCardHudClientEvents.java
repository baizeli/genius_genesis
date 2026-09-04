package miku.united_as_one.genesis.client.spellhud;

import com.mojang.blaze3d.platform.InputConstants;
import miku.united_as_one.genesis.Genesis;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.Util;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import org.lwjgl.glfw.GLFW;

public final class SpellCardHudClientEvents {
    private static final SpellCardHudState STATE = new SpellCardHudState(true);
    private static final SpellCardAnimationState ANIMATION = new SpellCardAnimationState();
    private static final SpellCardEffectEngine EFFECTS = new SpellCardEffectEngine(true);
    private static final KeyMapping TOGGLE = new KeyMapping(
            "key.genius_genesis.toggle_spell_card_hud",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_X,
            "key.categories.genius_genesis");

    private SpellCardHudClientEvents() {
    }

    public static void registerKeyMapping(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE);
    }

    public static void registerOverlay(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(),
                "spell_card_stack", SpellCardHudOverlay.OVERLAY);
    }

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        while (TOGGLE.consumeClick()) {
            boolean enabled = STATE.toggle();
            EFFECTS.setTargetVisible(enabled, Util.getMillis());
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player != null) {
                minecraft.player.displayClientMessage(Component.translatable(
                        enabled
                                ? "message.genius_genesis.spell_card_hud.enabled"
                                : "message.genius_genesis.spell_card_hud.disabled"), true);
            }
        }
    }

    public static boolean isEnabled() {
        return STATE.isEnabled();
    }

    public static SpellCardAnimationState animation() {
        return ANIMATION;
    }

    public static SpellCardEffectEngine effects() {
        return EFFECTS;
    }

    public static boolean shouldRenderReplacement() {
        return EFFECTS.shouldRender(Util.getMillis());
    }

    public static boolean shouldHideOriginalBar() {
        return EFFECTS.shouldHideOriginalBar(Util.getMillis());
    }

    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        SpellCardHudOverlay.resetTransientState();
    }
}
