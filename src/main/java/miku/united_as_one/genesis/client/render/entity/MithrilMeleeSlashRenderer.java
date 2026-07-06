package miku.united_as_one.genesis.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import miku.bai_ze_li.genesis.api.render.effect.MithrilMeleeSlashEffect;
import miku.united_as_one.genesis.client.TrailRender;
import miku.united_as_one.genesis.entity.effect.MithrilMeleeSlashEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class MithrilMeleeSlashRenderer extends EntityRenderer<MithrilMeleeSlashEntity> {
    public MithrilMeleeSlashRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(@NotNull MithrilMeleeSlashEntity entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        if (!TrailRender.shouldDeferWorldEffects()) {
            renderSlash(entity, partialTicks, poseStack, bufferSource, false);
        }
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    public static void renderSlash(MithrilMeleeSlashEntity entity, float partialTicks, PoseStack poseStack,
                                   MultiBufferSource bufferSource, boolean shaderCompatibleMode) {
        MithrilMeleeSlashEffect.render(
                poseStack,
                bufferSource,
                entity.getAge(partialTicks),
                MithrilMeleeSlashEntity.LIFE_TIME,
                entity.getYRot(),
                entity.getXRot(),
                entity.getStage(),
                entity.getColor(),
                shaderCompatibleMode
        );
    }

    @Override
    public boolean shouldRender(@NotNull MithrilMeleeSlashEntity entity, @NotNull Frustum frustum,
                                double cameraX, double cameraY, double cameraZ) {
        return frustum.isVisible(entity.getBoundingBox().inflate(6.0D));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MithrilMeleeSlashEntity entity) {
        return MithrilMeleeSlashEffect.texture();
    }
}
