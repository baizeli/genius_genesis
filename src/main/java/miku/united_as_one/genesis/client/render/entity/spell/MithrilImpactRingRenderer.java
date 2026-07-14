package miku.united_as_one.genesis.client.render.entity.spell;

import com.mojang.blaze3d.vertex.PoseStack;
import miku.bai_ze_li.genesis.api.render.effect.MithrilImpactRingEffect;
import miku.united_as_one.genesis.entity.effect.MithrilImpactRingEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class MithrilImpactRingRenderer extends EntityRenderer<MithrilImpactRingEntity> {
    public MithrilImpactRingRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(@NotNull MithrilImpactRingEntity entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        MithrilImpactRingEffect.render(
                poseStack,
                bufferSource,
                entity.getAge(partialTicks),
                MithrilImpactRingEntity.LIFE_TIME,
                entity.getRadius(),
                entity.getColor()
        );
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public boolean shouldRender(@NotNull MithrilImpactRingEntity entity, @NotNull Frustum frustum,
                                double cameraX, double cameraY, double cameraZ) {
        return frustum.isVisible(entity.getBoundingBox().inflate(entity.getRadius()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MithrilImpactRingEntity entity) {
        return MithrilImpactRingEffect.texture();
    }
}
