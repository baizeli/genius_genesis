package miku.united_as_one.genesis.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import miku.bai_ze_li.genesis.api.render.shader.GenesisRenderType;
import miku.united_as_one.genesis.entity.spell.MeteorShockwaveEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class MeteorShockwaveRenderer extends EntityRenderer<MeteorShockwaveEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/misc/forcefield.png");
    private static final int LATITUDE_SEGMENTS = 16;
    private static final int LONGITUDE_SEGMENTS = 32;

    public MeteorShockwaveRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(@NotNull MeteorShockwaveEntity entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        float alpha = entity.getAlpha(partialTicks);
        if (alpha <= 0.01F) {
            return;
        }

        float pulse = 0.92F + 0.08F * Mth.sin(((float) entity.tickCount + partialTicks) * 0.35F);
        float[] color = MeteorProjectileRenderer.randomEntityColor(entity.getColorSeed(), pulse);
        int red = Mth.clamp((int) (color[0] * 255.0F), 0, 255);
        int green = Mth.clamp((int) (color[1] * 255.0F), 0, 255);
        int blue = Mth.clamp((int) (color[2] * 255.0F), 0, 255);
        int alphaByte = Mth.clamp((int) (alpha * 255.0F), 0, 255);

        VertexConsumer buffer = bufferSource.getBuffer(GenesisRenderType.shockwaveSphere);
        this.renderSphere(buffer, poseStack.last().pose(), entity.getRenderRadius(partialTicks), red, green, blue, alphaByte);
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public boolean shouldRender(@NotNull MeteorShockwaveEntity entity, @NotNull Frustum frustum,
                                double cameraX, double cameraY, double cameraZ) {
        return frustum.isVisible(entity.getBoundingBox().inflate(entity.getMaxSize()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MeteorShockwaveEntity entity) {
        return TEXTURE;
    }

    private void renderSphere(VertexConsumer buffer, Matrix4f pose, float radius, int red, int green, int blue, int alpha) {
        for (int latitude = 0; latitude < LATITUDE_SEGMENTS; latitude++) {
            float theta0 = (float) Math.PI * (float) latitude / (float) LATITUDE_SEGMENTS;
            float theta1 = (float) Math.PI * (float) (latitude + 1) / (float) LATITUDE_SEGMENTS;

            for (int longitude = 0; longitude < LONGITUDE_SEGMENTS; longitude++) {
                float phi0 = Mth.TWO_PI * (float) longitude / (float) LONGITUDE_SEGMENTS;
                float phi1 = Mth.TWO_PI * (float) (longitude + 1) / (float) LONGITUDE_SEGMENTS;

                sphereVertex(buffer, pose, radius, theta0, phi0, red, green, blue, alpha);
                sphereVertex(buffer, pose, radius, theta1, phi0, red, green, blue, alpha);
                sphereVertex(buffer, pose, radius, theta1, phi1, red, green, blue, alpha);

                sphereVertex(buffer, pose, radius, theta0, phi0, red, green, blue, alpha);
                sphereVertex(buffer, pose, radius, theta1, phi1, red, green, blue, alpha);
                sphereVertex(buffer, pose, radius, theta0, phi1, red, green, blue, alpha);
            }
        }
    }

    private void sphereVertex(VertexConsumer buffer, Matrix4f pose, float radius, float theta, float phi,
                              int red, int green, int blue, int alpha) {
        float horizontalRadius = Mth.sin(theta) * radius;
        float x = Mth.cos(phi) * horizontalRadius;
        float y = Mth.cos(theta) * radius;
        float z = Mth.sin(phi) * horizontalRadius;
        buffer.vertex(pose, x, y, z).color(red, green, blue, alpha).endVertex();
    }
}
