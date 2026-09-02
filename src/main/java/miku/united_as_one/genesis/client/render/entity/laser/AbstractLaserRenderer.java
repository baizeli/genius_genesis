package miku.united_as_one.genesis.client.render.entity.laser;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import miku.united_as_one.genesis.content.entity.laser.AbstractLaserEntity;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractLaserRenderer<T extends AbstractLaserEntity> extends EntityRenderer<T> {
    private boolean clearerView = false;

    public AbstractLaserRenderer(EntityRendererProvider.Context mgr) {
        super(mgr);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T entity) {
        return getLaserTexture();
    }

    protected abstract ResourceLocation getLaserTexture();

    @Override
    public void render(T laser, float entityYaw, float delta, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn) {
        this.clearerView = (laser.caster instanceof Player && (Minecraft.getInstance()).player == laser.caster && (Minecraft.getInstance()).options.getCameraType() == CameraType.FIRST_PERSON);

        double posX = laser.xo + (laser.getX() - laser.xo) * delta;
        double posY = laser.yo + (laser.getY() - laser.yo) * delta;
        double posZ = laser.zo + (laser.getZ() - laser.zo) * delta;
        float yaw = laser.prevYaw + (laser.renderYaw - laser.prevYaw) * delta;
        float pitch = laser.prevPitch + (laser.renderPitch - laser.prevPitch) * delta;

        int frame = Mth.floor(((laser.appearTimer - 1) + delta) * 2);
        if (frame < 0) frame = 6;

        VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.create("glow_beam",
                DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256,
                true, true, RenderType.CompositeState.builder()
                        .setTextureState(new RenderStateShard.TextureStateShard(getTextureLocation(laser), false, false))
                        .setShaderState(RenderStateShard.RENDERTYPE_EYES_SHADER)
                        .setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
                        .setCullState(RenderStateShard.NO_CULL)
                        .setOverlayState(RenderStateShard.OVERLAY)
                        .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                        .createCompositeState(false)));

        if (laser.getRenderStart()) renderStart(frame, matrixStackIn, ivertexbuilder, packedLightIn);
        renderBeam(laser.getLaserLength(), laser.getLaserRadius(), 57 * yaw, 57 * pitch, frame, matrixStackIn, ivertexbuilder, packedLightIn);
        if (laser.getRenderEnd()) {
            matrixStackIn.pushPose();
            double endX = posX + laser.getLaserLength() * Math.cos(Math.toRadians(57 * yaw)) * Math.cos(Math.toRadians(57 * pitch));
            double endZ = posZ + laser.getLaserLength() * Math.sin(Math.toRadians(57 * yaw)) * Math.cos(Math.toRadians(57 * pitch));
            double endY = posY + laser.getLaserLength() * Math.sin(Math.toRadians(57 * pitch));
            matrixStackIn.translate(endX - posX, endY - posY, endZ - posZ);
            renderEnd(frame, laser.blockSide, matrixStackIn, ivertexbuilder, packedLightIn);
            matrixStackIn.popPose();
        }
    }

    protected void renderFlatQuad(int frame, PoseStack matrixStackIn, VertexConsumer builder, int packedLightIn) {
        float minU = 0 + 0.0625f * frame;
        float minV = 0;
        float maxU = minU + 0.0625f;
        float maxV = minV + 0.5f;
        PoseStack.Pose matrixstack$entry = matrixStackIn.last();
        Matrix4f matrix4f = matrixstack$entry.pose();
        Matrix3f matrix3f = matrixstack$entry.normal();
        drawVertex(matrix4f, matrix3f, builder, -1.3f, -1.3f, 0, minU, minV, 1, packedLightIn);
        drawVertex(matrix4f, matrix3f, builder, -1.3f, 1.3f, 0, minU, maxV, 1, packedLightIn);
        drawVertex(matrix4f, matrix3f, builder, 1.3f, 1.3f, 0, maxU, maxV, 1, packedLightIn);
        drawVertex(matrix4f, matrix3f, builder, 1.3f, -1.3f, 0, maxU, minV, 1, packedLightIn);
    }

    protected void renderStart(int frame, PoseStack matrixStackIn, VertexConsumer builder, int packedLightIn) {
        if (this.clearerView) return;
        matrixStackIn.pushPose();
        Quaternionf quat = this.entityRenderDispatcher.cameraOrientation();
        matrixStackIn.mulPose(quat);
        renderFlatQuad(frame, matrixStackIn, builder, packedLightIn);
        matrixStackIn.popPose();
    }

    protected void renderEnd(int frame, Direction side, PoseStack matrixStackIn, VertexConsumer builder, int packedLightIn) {
        matrixStackIn.pushPose();
        Quaternionf quat = this.entityRenderDispatcher.cameraOrientation();
        matrixStackIn.mulPose(quat);
        renderFlatQuad(frame, matrixStackIn, builder, packedLightIn);
        matrixStackIn.popPose();
        if (side == null) return;
        matrixStackIn.pushPose();
        Quaternionf sideQuat = side.getRotation();
        sideQuat.mul(Axis.XP.rotationDegrees(90));
        matrixStackIn.mulPose(sideQuat);
        matrixStackIn.translate(0, 0, -0.01f);
        renderFlatQuad(frame, matrixStackIn, builder, packedLightIn);
        matrixStackIn.popPose();
    }

    protected void drawBeam(float length, float radius, int frame, PoseStack matrixStackIn, VertexConsumer builder, int packedLightIn) {
        float minU = 0f;
        float minV = 0.5f + 0.03125f * frame;
        float maxU = minU + 0.078125f;
        float maxV = minV + 0.03125f;
        Matrix4f matrix4f = matrixStackIn.last().pose();
        Matrix3f matrix3f = matrixStackIn.last().normal();
        float offset = this.clearerView ? -1 : 0;
        drawVertex(matrix4f, matrix3f, builder, -radius, offset, 0, minU, minV, 1, packedLightIn);
        drawVertex(matrix4f, matrix3f, builder, -radius, length, 0, minU, maxV, 1, packedLightIn);
        drawVertex(matrix4f, matrix3f, builder, radius, length, 0, maxU, maxV, 1, packedLightIn);
        drawVertex(matrix4f, matrix3f, builder, radius, offset, 0, maxU, minV, 1, packedLightIn);
    }

    protected void renderBeam(float length, float radius, float yaw, float pitch, int frame, PoseStack matrixStackIn, VertexConsumer builder, int packedLightIn) {
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(90));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(yaw - 90));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(-pitch));
        matrixStackIn.pushPose();
        if (!this.clearerView) matrixStackIn.mulPose(Axis.YP.rotationDegrees((Minecraft.getInstance()).gameRenderer.getMainCamera().getXRot() + 90));
        drawBeam(length, radius, frame, matrixStackIn, builder, packedLightIn);
        matrixStackIn.popPose();
        if (!this.clearerView) {
            matrixStackIn.pushPose();
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(-(Minecraft.getInstance()).gameRenderer.getMainCamera().getXRot() - 90));
            drawBeam(length, radius, frame, matrixStackIn, builder, packedLightIn);
            matrixStackIn.popPose();
        }
        matrixStackIn.popPose();
    }

    public void drawVertex(Matrix4f matrix, Matrix3f normals, VertexConsumer vertexBuilder, float offsetX, float offsetY, float offsetZ, float textureX, float textureY, float alpha, int packedLightIn) {
        vertexBuilder.vertex(matrix, offsetX, offsetY, offsetZ).color(1, 1, 1, alpha).uv(textureX, textureY).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normals, 0, 1, 0).endVertex();
    }
}
