package miku.united_as_one.genesis.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import miku.bai_ze_li.genesis.api.render.TrailHelp;
import miku.bai_ze_li.genesis.api.render.TrailRenderApi;
import miku.bai_ze_li.genesis.api.render.TrailRenderStyle;
import miku.bai_ze_li.genesis.api.render.shader.GenesisRenderType;
import miku.bai_ze_li.genesis.api.render.shader.GenesisShaders;
import miku.bai_ze_li.genesis.api.text.GenesisColor;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.client.TrailRender;
import miku.united_as_one.genesis.entity.spell.MeteorStarEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class MeteorStarRenderer extends EntityRenderer<MeteorStarEntity> {
    private static final ResourceLocation TEXTURE = Genesis.id("textures/entity/feg.png");
    private static final ResourceLocation TRAIL_TEXTURE = Genesis.id("textures/images/trail_stellar.png");
    private static final float SPRITE_SCALE = 0.7F;
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
            renderStarOnly(entity, partialTicks, poseStack, buffer, false, this.entityRenderDispatcher.camera.getPosition());
        }
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

    public static void renderStarOnly(MeteorStarEntity entity, float partialTicks, PoseStack poseStack,
                                      MultiBufferSource buffer, boolean shaderCompatibleMode, Vec3 cameraPos) {
        poseStack.pushPose();

        Vec3 entityPos = entity.position();
        Vec3 direction = cameraPos.subtract(entityPos);
        if (direction.lengthSqr() < 0.0001D) {
            direction = new Vec3(0.0D, 0.0D, 1.0D);
        } else {
            direction = direction.normalize();
        }

        poseStack.mulPose(Axis.YP.rotation((float) Math.atan2(direction.x, direction.z)));
        poseStack.mulPose(Axis.XP.rotation((float) -Math.asin(direction.y)));
        poseStack.scale(SPRITE_SCALE, SPRITE_SCALE, SPRITE_SCALE);
        if (!shaderCompatibleMode) {
            GenesisShaders.setMeteorStarTime(((float) entity.tickCount + partialTicks) * 0.08F);
        }

        VertexConsumer consumer = buffer.getBuffer(shaderCompatibleMode ? GenesisRenderType.meteorStarCompatible : GenesisRenderType.meteorStar);
        Matrix4f matrix = poseStack.last().pose();
        float age = (float) entity.tickCount + partialTicks;
        float pulse = 0.9F + 0.1F * Mth.sin((entity.tickCount + partialTicks) * 0.45F);
        float[] color = MeteorProjectileRenderer.randomEntityColor(entity.getId(), pulse);
        int red = Mth.clamp((int) (color[0] * 215.0F), 0, 255);
        int green = Mth.clamp((int) (color[1] * 215.0F), 0, 255);
        int blue = Mth.clamp((int) (color[2] * 215.0F), 0, 255);
        float starPulse = 0.92F + 0.10F * Mth.sin(age * 0.62F);

        renderAstralStar(consumer, matrix, starPulse, red, green, blue, shaderCompatibleMode);

        poseStack.popPose();
    }

    static void renderAstralStar(VertexConsumer buffer, Matrix4f pose, float pulse, int red, int green, int blue, boolean shaderCompatibleMode) {
        float alphaScale = shaderCompatibleMode ? 0.42F : 0.66F;
        renderStar(buffer, pose, 0.78F * pulse, 0.16F * pulse, 84, 182, 255, scaledAlpha(52, alphaScale));
        renderStar(buffer, pose, 0.52F * pulse, 0.095F * pulse, red, green, blue, scaledAlpha(176, alphaScale));
        renderStar(buffer, pose, 0.24F * pulse, 0.055F * pulse, 255, 255, 255, scaledAlpha(150, alphaScale));
    }

    private static int scaledAlpha(int alpha, float scale) {
        return Mth.clamp((int) (alpha * scale), 0, 255);
    }

    private static void renderStar(VertexConsumer buffer, Matrix4f pose, float radius, float innerRadius,
                                   int red, int green, int blue, int alpha) {
        float[][] points = new float[][]{
                {0.0F, radius},
                {innerRadius, innerRadius},
                {radius, 0.0F},
                {innerRadius, -innerRadius},
                {0.0F, -radius},
                {-innerRadius, -innerRadius},
                {-radius, 0.0F},
                {-innerRadius, innerRadius}
        };

        for (int i = 0; i < points.length; i++) {
            float[] a = points[i];
            float[] b = points[(i + 1) % points.length];
            vertex(buffer, pose, 0.0F, 0.0F, 0.0F, 255, 255, 255, alpha);
            vertex(buffer, pose, a[0], a[1], 0.0F, red, green, blue, alpha);
            vertex(buffer, pose, b[0], b[1], 0.0F, red, green, blue, alpha);
        }
    }

    private static void vertex(VertexConsumer buffer, Matrix4f pose, float x, float y, float z,
                               int red, int green, int blue, int alpha) {
        buffer.vertex(pose, x, y, z).color(red, green, blue, alpha).endVertex();
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
