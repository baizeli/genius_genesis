package miku.united_as_one.genesis.client.render.entity.spell;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import miku.united_as_one.genesis.content.entity.spell.blood_boss.blood_dagger.BloodDaggerEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BloodDaggerRenderer extends GeoEntityRenderer<BloodDaggerEntity> {
    public BloodDaggerRenderer(EntityRendererProvider.Context context) {
        super(context, new BloodDaggerModel());
    }

    @Override
    public void preRender(PoseStack poseStack, BloodDaggerEntity entity, BakedGeoModel model, MultiBufferSource bufferSource,
                          VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                          float red, float green, float blue, float alpha) {
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        Vec3 motion = entity.deltaMovementOld.add(entity.getDeltaMovement().subtract(entity.deltaMovementOld).scale(partialTick));
        float xRot = (float) (Mth.atan2(motion.horizontalDistance(), motion.y) * Mth.RAD_TO_DEG) - 90.0F;
        float yRot = -((float) (Mth.atan2(motion.z, motion.x) * Mth.RAD_TO_DEG) - 90.0F);
        poseStack.translate(0.0F, entity.getBbHeight() * 0.5F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
    }

    @Override
    public Color getRenderColor(BloodDaggerEntity animatable, float partialTick, int packedLight) {
        return Color.LIGHT_GRAY;
    }
}
