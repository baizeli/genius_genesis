package miku.united_as_one.genesis.client.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import miku.bai_ze_li.genesis.GenesisLib;
import miku.bai_ze_li.genesis.api.math.GenesisFastMath;
import miku.bai_ze_li.genesis.api.render.cosmic.AvaritiaShaders;
import miku.united_as_one.genesis.Genesis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public final class ScrollTooltipBackgroundRenderer {
    private static final ResourceLocation BACKGROUND = Genesis.id("item/mask/background");
    private static final RenderType COSMIC_BACKGROUND = RenderType.create(
            "genesis_cosmic_tooltip_background",
            DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
            VertexFormat.Mode.QUADS,
            256,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(() -> AvaritiaShaders.cosmicShader))
                    .setTextureState(AvaritiaShaders.RenderStateShardAccess.COSMIC_TEXTURE_ISOLATED)
                    .setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
                    .setLightmapState(RenderType.LIGHTMAP)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .setCullState(RenderType.NO_CULL)
                    .createCompositeState(true));

    private ScrollTooltipBackgroundRenderer() {
    }

    public static boolean isAvailable() {
        return AvaritiaShaders.cosmicShader != null && AvaritiaShaders.useType != null;
    }

    public static void render(PoseStack poseStack, MultiBufferSource buffer, float width, float height,
                              double x, double y, int light, int useType) {
        if (!isAvailable()) {
            return;
        }

        RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
        float time = (AvaritiaShaders.renderTime + AvaritiaShaders.renderFrame) / 20.0F;
        float opacity = (float) (0.7F + 0.3F * GenesisFastMath.sin(time * 2.5F));

        AvaritiaShaders.useType.set(useType);
        AvaritiaShaders.cosmicTime.set(time);
        AvaritiaShaders.cosmicYaw.set(0.0F);
        AvaritiaShaders.cosmicPitch.set(0.0F);
        AvaritiaShaders.cosmicExternalScale.set(50.0F);
        AvaritiaShaders.cosmicOpacity.set(opacity);
        AvaritiaShaders.cosmicColor.set(new Vector4f(0.1F, 0.1F, 0.1F, 1.33F));
        AvaritiaShaders.cosmicScreenSize.set((float) mainTarget.width, (float) mainTarget.height);
        AvaritiaShaders.cosmicIs2D.set(1);
        updateCosmicUvs();

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(BACKGROUND);
        VertexConsumer consumer = buffer.getBuffer(COSMIC_BACKGROUND);
        poseStack.pushPose();
        poseStack.translate(x, y, 0.0D);
        float halfWidth = width / 2.0F;
        float halfHeight = height / 2.0F;
        Matrix4f matrix = poseStack.last().pose();
        consumer.vertex(matrix, -halfWidth, -halfHeight, 0).color(255, 255, 255, 255).uv(sprite.getU0(), sprite.getV1()).uv2(light).normal(0, 0, 1).endVertex();
        consumer.vertex(matrix, halfWidth, -halfHeight, 0).color(255, 255, 255, 255).uv(sprite.getU1(), sprite.getV1()).uv2(light).normal(0, 0, 1).endVertex();
        consumer.vertex(matrix, halfWidth, halfHeight, 0).color(255, 255, 255, 255).uv(sprite.getU1(), sprite.getV0()).uv2(light).normal(0, 0, 1).endVertex();
        consumer.vertex(matrix, -halfWidth, halfHeight, 0).color(255, 255, 255, 255).uv(sprite.getU0(), sprite.getV0()).uv2(light).normal(0, 0, 1).endVertex();
        poseStack.popPose();

        if (buffer instanceof MultiBufferSource.BufferSource source) {
            source.endLastBatch();
        }
    }

    private static void updateCosmicUvs() {
        for (int i = 0; i < 10; i++) {
            ResourceLocation texture = new ResourceLocation(GenesisLib.MODID, "item/misc/cosmic_" + i);
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture);
            AvaritiaShaders.COSMIC_UVS[i * 4] = sprite.getU0();
            AvaritiaShaders.COSMIC_UVS[i * 4 + 1] = sprite.getV0();
            AvaritiaShaders.COSMIC_UVS[i * 4 + 2] = sprite.getU1();
            AvaritiaShaders.COSMIC_UVS[i * 4 + 3] = sprite.getV1();
        }
        AvaritiaShaders.cosmicUVs.set(AvaritiaShaders.COSMIC_UVS);
    }
}
