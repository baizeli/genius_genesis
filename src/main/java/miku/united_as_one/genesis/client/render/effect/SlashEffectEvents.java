package miku.united_as_one.genesis.client.render.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import miku.bai_ze_li.genesis.api.render.effect.SlashEffectManager;
import miku.bai_ze_li.genesis.api.render.shader.GenesisShaderCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;

public final class SlashEffectEvents {
    private SlashEffectEvents() {
    }

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            SlashEffectManager.tick();
        }
    }

    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }
        if (GenesisShaderCompat.shouldDeferWorldEffects()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            return;
        }

        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        SlashEffectManager.render(event.getPoseStack(), bufferSource, event.getPartialTick());
        bufferSource.endBatch();
    }

    public static void renderDeferred(MultiBufferSource.BufferSource bufferSource, float partialTick) {
        if (!GenesisShaderCompat.shouldDeferWorldEffects()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            SlashEffectManager.clear();
            return;
        }

        SlashEffectManager.renderDeferred(new PoseStack(), bufferSource, partialTick);
        bufferSource.endBatch();
    }

    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel().isClientSide()) {
            SlashEffectManager.clear();
        }
    }
}
