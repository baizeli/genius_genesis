package miku.united_as_one.genesis.client.render.entity.spell;

import com.mojang.blaze3d.vertex.PoseStack;
import miku.united_as_one.genesis.content.entity.effect.MeleeDamageTextEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class MeleeDamageTextRenderer extends EntityRenderer<MeleeDamageTextEntity> {
    public MeleeDamageTextRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(@NotNull MeleeDamageTextEntity entity, float yaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        float age = entity.tickCount + partialTicks;
        float alpha = 1.0F - Mth.clamp(age / MeleeDamageTextEntity.LIFE_TIME, 0.0F, 1.0F);
        if (alpha <= 0.0F) {
            return;
        }

        poseStack.pushPose();
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        float scale = 0.026F * (1.0F + 0.12F * Mth.sin(age * 0.35F));
        poseStack.scale(-scale, -scale, scale);

        Font font = Minecraft.getInstance().font;
        String damage = formatDamage(entity.getDamage());
        float x = -font.width(damage) * 0.5F;
        int alphaByte = Mth.clamp((int) (alpha * 255.0F), 0, 255);
        int color = alphaByte << 24 | 0xAEEBFF;
        Matrix4f matrix = poseStack.last().pose();
        font.drawInBatch(Component.literal(damage), x, 0.0F, color, false, matrix, buffer,
                Font.DisplayMode.SEE_THROUGH, 0, 0xF000F0);
        poseStack.popPose();
    }

    private static String formatDamage(float damage) {
        float rounded = Math.round(damage * 10.0F) / 10.0F;
        if (rounded == (long) rounded) {
            return String.valueOf((long) rounded);
        }
        return String.valueOf(rounded);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MeleeDamageTextEntity entity) {
        return new ResourceLocation("minecraft", "textures/misc/white.png");
    }
}
