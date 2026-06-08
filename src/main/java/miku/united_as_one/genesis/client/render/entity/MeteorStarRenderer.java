package miku.united_as_one.genesis.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import miku.bai_ze_li.genesis.api.render.TrailHelp;
import miku.bai_ze_li.genesis.api.render.TrailRenderApi;
import miku.bai_ze_li.genesis.api.render.TrailRenderStyle;
import miku.bai_ze_li.genesis.api.render.shader.GenesisRenderType;
import miku.bai_ze_li.genesis.api.text.GenesisColor;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.client.TrailRender;
import miku.united_as_one.genesis.entity.spell.MeteorStarEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class MeteorStarRenderer extends EntityRenderer<MeteorStarEntity> {
    private static final ResourceLocation TEXTURE = Genesis.id("textures/entity/feg.png");
    private static final ResourceLocation TRAIL_TEXTURE = Genesis.id("textures/images/trail_stellar.png");
    private static final float SPRITE_SCALE = 2.5F;
    private static final TrailRenderStyle STAR_TRAIL = TrailRenderStyle
            .builder(TRAIL_TEXTURE, MeteorStarRenderer::trailColor)
            .width(0.18F)
            .alphaMultiplier(0.74F)
            .emissive(true)
            .headless(true)
            .renderTypeProvider((style, texture) -> GenesisRenderType.delayedTrail(texture))
            .build();

    public MeteorStarRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(MeteorStarEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        if (!TrailRender.shouldDeferWorldEffects()) {
            renderTrailOnly(entity, partialTicks, poseStack, buffer);
        }
        poseStack.pushPose();

        Vec3 cameraPos = this.entityRenderDispatcher.camera.getPosition();
        Vec3 entityPos = entity.position();
        Vec3 direction = cameraPos.subtract(entityPos);
        if (direction.lengthSqr() < 0.0001D) {
            direction = new Vec3(0.0D, 0.0D, 1.0D);
        } else {
            direction = direction.normalize();
        }

        poseStack.mulPose(Axis.YP.rotation((float) Math.atan2(direction.x, direction.z)));
        poseStack.mulPose(Axis.XP.rotation((float) -Math.asin(direction.y)));
        poseStack.mulPose(Axis.ZP.rotationDegrees((entity.tickCount + partialTicks) * -15.0F));
        poseStack.scale(SPRITE_SCALE, SPRITE_SCALE, SPRITE_SCALE);

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucentEmissive(TEXTURE));
        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        float size = 0.5F;
        float pulse = 0.9F + 0.1F * Mth.sin((entity.tickCount + partialTicks) * 0.45F);
        float[] color = MeteorProjectileRenderer.randomEntityColor(entity.getId(), pulse);

        TrailHelp.addVertexWithColor(consumer, matrix, normal, new Vec3(-size, -size, 0.0D), color, 1.0F, 0.0F, 1.0F, LightTexture.FULL_BRIGHT);
        TrailHelp.addVertexWithColor(consumer, matrix, normal, new Vec3(size, -size, 0.0D), color, 1.0F, 1.0F, 1.0F, LightTexture.FULL_BRIGHT);
        TrailHelp.addVertexWithColor(consumer, matrix, normal, new Vec3(size, size, 0.0D), color, 1.0F, 1.0F, 0.0F, LightTexture.FULL_BRIGHT);
        TrailHelp.addVertexWithColor(consumer, matrix, normal, new Vec3(-size, size, 0.0D), color, 1.0F, 0.0F, 0.0F, LightTexture.FULL_BRIGHT);

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(MeteorStarEntity entity) {
        return TEXTURE;
    }

    public static void renderTrailOnly(MeteorStarEntity entity, float partialTicks, PoseStack poseStack,
                                       MultiBufferSource buffer) {
        TrailRenderApi.renderTrail(entity.getTrailPositions(partialTicks), poseStack, buffer, STAR_TRAIL,
                entity.tickCount + partialTicks, entity.getId());
    }

    private static float[] trailColor(float progress, float time, int entityId) {
        float gradient = wrap01(entityId * 0.381966F + progress * 0.4F - time * 0.015F);
        float pulse = 0.88F + 0.12F * Mth.sin(time * 0.2F + progress * Mth.TWO_PI + entityId);
        float[] color = TrailHelp.interpolateGradientColor(gradient, GenesisColor.RAINBOW);
        return new float[]{
                Mth.clamp(color[0] * pulse, 0.0F, 1.0F),
                Mth.clamp(color[1] * pulse, 0.0F, 1.0F),
                Mth.clamp(color[2] * pulse, 0.0F, 1.0F)
        };
    }

    private static float wrap01(float value) {
        return value - Mth.floor(value);
    }
}
