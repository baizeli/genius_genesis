package miku.united_as_one.genesis.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.math.Axis;
import miku.bai_ze_li.genesis.mixin.client.GameRendererAccessor;
import miku.united_as_one.genesis.client.render.cosmic.CosmicBakedModel;
import miku.united_as_one.genesis.client.render.effect.SlashEffectManager;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.config.IrisConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class TrailRender {
    public static boolean IRIS_Setup = ModList.get().isLoaded("oculus");
    private static RenderContext lastContext;

    public static void captureLevelRenderContext(PoseStack poseStack, float partialTick, Camera camera, Matrix4f projectionMatrix) {
        if (!shouldDeferWorldEffects()) {
            lastContext = null;
            CosmicBakedModel.clearDeferredHandItems();
            return;
        }

        CosmicBakedModel.clearDeferredHandItems();
        lastContext = new RenderContext(
                new Matrix4f(poseStack.last().pose()),
                new Matrix4f(projectionMatrix),
                partialTick,
                camera.getPosition()
        );
    }

    public static boolean shouldDeferWorldEffects() {
        if (!IRIS_Setup) {
            return false;
        }

        try {
            IrisConfig irisConfig = Iris.getIrisConfig();
            if (!irisConfig.areShadersEnabled()) {
                return false;
            }
            return IrisApi.getInstance().isShaderPackInUse() || irisConfig.areShadersEnabled();
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static void renderTrail(float partialTicks, long finishTimeNano, boolean renderLevel) {
        if (!renderLevel || !shouldDeferWorldEffects()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            return;
        }

        RenderSystem.backupProjectionMatrix();
        PoseStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushPose();

        try {
            Camera camera = minecraft.gameRenderer.getMainCamera();
            RenderContext context = lastContext;
            Matrix4f projectionMatrix = context != null ? new Matrix4f(context.projection) : createProjectionMatrix(minecraft, camera, partialTicks);
            RenderSystem.setProjectionMatrix(projectionMatrix, VertexSorting.DISTANCE_TO_ORIGIN);

            modelViewStack.setIdentity();
            modelViewStack.mulPoseMatrix(createModelViewMatrix(context, camera));
            RenderSystem.applyModelViewMatrix();

            minecraft.getMainRenderTarget().bindWrite(false);
            MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();

            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(GL11.GL_LEQUAL);

            SlashEffectManager.renderDeferred(new PoseStack(), bufferSource, context != null ? context.partialTick : partialTicks);
            bufferSource.endBatch();
            CosmicBakedModel.flushDeferredHandItems(bufferSource);
        } finally {
            modelViewStack.popPose();
            RenderSystem.restoreProjectionMatrix();
            RenderSystem.applyModelViewMatrix();
            lastContext = null;
        }
    }

    private static Matrix4f createProjectionMatrix(Minecraft minecraft, Camera camera, float partialTicks) {
        double fov = ((GameRendererAccessor) minecraft.gameRenderer).callGetFov(camera, partialTicks, true);
        return minecraft.gameRenderer.getProjectionMatrix(fov);
    }

    private static Matrix4f createModelViewMatrix(RenderContext context, Camera camera) {
        if (context != null) {
            return new Matrix4f(context.modelView);
        }

        PoseStack viewPoseStack = new PoseStack();
        viewPoseStack.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));
        viewPoseStack.mulPose(Axis.YP.rotationDegrees(camera.getYRot() + 180.0F));
        return new Matrix4f(viewPoseStack.last().pose());
    }

    private static final class RenderContext {
        private final Matrix4f modelView;
        private final Matrix4f projection;
        private final float partialTick;
        private final Vec3 cameraPos;

        private RenderContext(Matrix4f modelView, Matrix4f projection, float partialTick, Vec3 cameraPos) {
            this.modelView = modelView;
            this.projection = projection;
            this.partialTick = partialTick;
            this.cameraPos = cameraPos;
        }
    }
}
