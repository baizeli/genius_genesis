package miku.united_as_one.genesis.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.math.Axis;
import miku.bai_ze_li.genesis.api.render.effect.slash.SlashEffectManager;
import miku.bai_ze_li.genesis.mixin.client.GameRendererAccessor;
import miku.bai_ze_li.genesis.api.render.particle.GlowCubeParticle;
import miku.bai_ze_li.genesis.api.render.particle.GlowParticleRenderTypes;
import miku.united_as_one.genesis.client.render.cosmic.CosmicBakedModel;
import miku.united_as_one.genesis.client.render.entity.spell.MeteorProjectileRenderer;
import miku.united_as_one.genesis.client.render.entity.spell.MeteorStarRenderer;
import miku.united_as_one.genesis.client.render.entity.spell.MeleeProjBaseRenderer;
import miku.united_as_one.genesis.client.render.entity.spell.MithrilMeleeSlashRenderer;
import miku.united_as_one.genesis.combat.meleeproj.MeleeProjBase;
import miku.united_as_one.genesis.entity.effect.MithrilMeleeSlashEntity;
import miku.united_as_one.genesis.entity.spell.celestial_source.MeteorProjectileEntity;
import miku.united_as_one.genesis.entity.spell.celestial_source.MeteorStarEntity;
import miku.united_as_one.genesis.mixin.minecraft.client.particle.ParticleEngineAccessor;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.Queue;

public class TrailRender {
    public static boolean IRIS_Setup = ModList.get().isLoaded("oculus");
    private static RenderContext lastContext;

    private static MethodHandle GET_CONFIG;
    private static MethodHandle ARE_SHADERS_ENABLED;
    private static MethodHandle GET_API_INSTANCE;
    private static MethodHandle IS_SHADER_PACK_IN_USE;
    private static boolean wasInit;

    public static void captureLevelRenderContext(PoseStack poseStack, float partialTick, Camera camera, Matrix4f projectionMatrix) {
        lastContext = new RenderContext(
                new Matrix4f(poseStack.last().pose()),
                new Matrix4f(projectionMatrix),
                partialTick,
                camera.getPosition()
        );

        CosmicBakedModel.clearDeferredHandItems();
    }

    public static boolean shouldDeferWorldEffects() {
        if (!IRIS_Setup) {
            return false;
        }

        try {
            if (!wasInit) { // TODO: 可能有bug，记得验证边界情况
                wasInit = true;
                Class<?> irisConfigClass = Class.forName("net.irisshaders.iris.config.IrisConfig");
                Class<?> irisClass = Class.forName("net.irisshaders.iris.Iris");
                Class<?> irisApiClass = Class.forName("net.irisshaders.iris.api.v0.IrisApi");

                MethodHandles.Lookup lookup = MethodHandles.publicLookup();

                GET_CONFIG = lookup.unreflect(irisClass.getMethod("getIrisConfig"));
                ARE_SHADERS_ENABLED = lookup.unreflect(irisConfigClass.getMethod("areShadersEnabled"));
                GET_API_INSTANCE = lookup.unreflect(irisApiClass.getMethod("getInstance"));
                IS_SHADER_PACK_IN_USE = lookup.unreflect(irisApiClass.getMethod("isShaderPackInUse"));
            }

            if (!(boolean)ARE_SHADERS_ENABLED.invoke(GET_CONFIG.invoke())) {
                return false;
            }
            return (boolean) IS_SHADER_PACK_IN_USE.invoke(GET_API_INSTANCE.invoke());
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static void renderTrail(float partialTicks, long finishTimeNano, boolean renderLevel) {
        if (!renderLevel) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            return;
        }

        boolean shaderDeferred = shouldDeferWorldEffects();
        boolean hasMeleeProj = hasMeleeProjs(minecraft);
        if (!shaderDeferred && !hasMeleeProj) {
            lastContext = null;
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

            float renderPartialTicks = context != null ? context.partialTick : partialTicks;
            if (shaderDeferred) {
                renderDeferredEntityTrails(minecraft, camera, bufferSource, renderPartialTicks);
            } else {
                renderMeleeProjs(minecraft, camera, bufferSource, renderPartialTicks);
            }
            bufferSource.endBatch();
            renderMeleeProjWarps(minecraft, camera, bufferSource, renderPartialTicks);
            bufferSource.endBatch();
            if (shaderDeferred) {
                SlashEffectManager.render(new PoseStack(), bufferSource, context != null ? context.partialTick : partialTicks);
                CosmicBakedModel.flushDeferredHandItems(bufferSource);
                renderGlowCubes(minecraft, camera, renderPartialTicks);
            }
        } finally {
            modelViewStack.popPose();
            RenderSystem.restoreProjectionMatrix();
            RenderSystem.applyModelViewMatrix();
            lastContext = null;
        }
    }

    private static boolean hasMeleeProjs(Minecraft minecraft) {
        if (minecraft.level == null) {
            return false;
        }
        for (Entity entity : minecraft.level.entitiesForRendering()) {
            if (entity instanceof MeleeProjBase meleeProj && meleeProj.isAlive()) {
                return true;
            }
        }
        return false;
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

    private static void renderDeferredEntityTrails(Minecraft minecraft, Camera camera, MultiBufferSource bufferSource, float partialTicks) {
        Vec3 cameraPos = camera.getPosition();
        if (minecraft.level == null)
            return;
        for (Entity entity : minecraft.level.entitiesForRendering()) {
            if (entity == null) {
                continue;
            }

            double x = net.minecraft.util.Mth.lerp(partialTicks, entity.xOld, entity.getX());
            double y = net.minecraft.util.Mth.lerp(partialTicks, entity.yOld, entity.getY());
            double z = net.minecraft.util.Mth.lerp(partialTicks, entity.zOld, entity.getZ());

            PoseStack entityPoseStack = new PoseStack();
            entityPoseStack.translate(x - cameraPos.x, y - cameraPos.y, z - cameraPos.z);

            if (entity instanceof MeteorProjectileEntity projectile) {
                MeteorProjectileRenderer.renderTrailOnly(projectile, partialTicks, entityPoseStack, bufferSource);
                MeteorProjectileRenderer.renderStarOnly(projectile, partialTicks, entityPoseStack, bufferSource, true, cameraPos);
            } else if (entity instanceof MeteorStarEntity star) {
                MeteorStarRenderer.renderTrailOnly(star, partialTicks, entityPoseStack, bufferSource);
                MeteorStarRenderer.renderStarOnly(star, partialTicks, entityPoseStack, bufferSource, true, cameraPos);
            } else if (entity instanceof MithrilMeleeSlashEntity slash) {
                MithrilMeleeSlashRenderer.renderSlash(slash, partialTicks, entityPoseStack, bufferSource, true);
            } else if (entity instanceof MeleeProjBase meleeProj) {
                MeleeProjBaseRenderer.renderDeferred(meleeProj, partialTicks, new PoseStack(), bufferSource, cameraPos);
            }
        }
    }

    private static void renderMeleeProjs(Minecraft minecraft, Camera camera, MultiBufferSource bufferSource, float partialTicks) {
        Vec3 cameraPos = camera.getPosition();
        if (minecraft.level == null) {
            return;
        }
        for (Entity entity : minecraft.level.entitiesForRendering()) {
            if (entity instanceof MeleeProjBase meleeProj && meleeProj.isAlive()) {
                MeleeProjBaseRenderer.renderDeferred(meleeProj, partialTicks, new PoseStack(), bufferSource, cameraPos);
            }
        }
    }

    private static void renderMeleeProjWarps(Minecraft minecraft, Camera camera, MultiBufferSource bufferSource, float partialTicks) {
        Vec3 cameraPos = camera.getPosition();
        if (minecraft.level == null) {
            return;
        }
        for (Entity entity : minecraft.level.entitiesForRendering()) {
            if (entity instanceof MeleeProjBase meleeProj && meleeProj.isAlive()) {
                MeleeProjBaseRenderer.renderWarp(meleeProj, partialTicks, new PoseStack(), bufferSource, cameraPos);
            }
        }
    }

    private static void renderGlowCubes(Minecraft minecraft, Camera camera, float partialTicks) {
        Map<ParticleRenderType, Queue<Particle>> particles = ((ParticleEngineAccessor) minecraft.particleEngine).getParticles();
        boolean hasGlowCube = false;
        for (Queue<Particle> queue : particles.values()) {
            for (Particle particle : queue) {
                if (particle instanceof GlowCubeParticle) {
                    hasGlowCube = true;
                    break;
                }
            }
            if (hasGlowCube) {
                break;
            }
        }
        if (!hasGlowCube) {
            return;
        }

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        GlowParticleRenderTypes.GLOW_CUBE.begin(bufferBuilder, minecraft.getTextureManager());
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        try {
            for (Queue<Particle> queue : particles.values()) {
                for (Particle particle : queue) {
                    if (particle instanceof GlowCubeParticle glowCube) {
                        glowCube.renderDeferred(bufferBuilder, camera, partialTicks);
                    }
                }
            }
        } finally {
            GlowParticleRenderTypes.GLOW_CUBE.end(tesselator);
            RenderSystem.enableCull();
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
        }
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
