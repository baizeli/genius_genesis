package miku.united_as_one.genesis.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import miku.bai_ze_li.genesis.api.render.shader.GenesisRenderType;
import miku.bai_ze_li.genesis.api.render.shader.GenesisShaders;
import miku.united_as_one.genesis.combat.meleeproj.MeleeProjBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class MeleeProjBaseRenderer extends EntityRenderer<MeleeProjBase> {
    private static final float[] STAR_UV = new float[40];
    private static boolean starUvInitialized;

    public MeleeProjBaseRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(@NotNull MeleeProjBase entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MeleeProjBase entity) {
        return entity.getTrailTexture();
    }

    public static void renderDeferred(MeleeProjBase entity, float partialTicks, PoseStack poseStack,
                                      MultiBufferSource bufferSource, Vec3 cameraPos) {
        Vec3 base = renderBasePos(entity, cameraPos, partialTicks);
        renderWeaponModel(entity, poseStack, bufferSource, partialTicks, base);
        setupMeleeTrailShader(entity, partialTicks, entity.getUseType(), 0, null);
        renderTrail(entity, poseStack, bufferSource, partialTicks, base);
    }

    public static void renderWarp(MeleeProjBase entity, float partialTicks, PoseStack poseStack,
                                  MultiBufferSource bufferSource, Vec3 cameraPos) {
        int type = entity.getUseType();
        if (type != 2 && type != 3 && type != 4 && type != 5) {
            return;
        }

        int mainColor = SceneCopyTarget.copyMainColor();
        setupMeleeTrailShader(entity, partialTicks, -1, type, mainColor);
        renderTrail(entity, poseStack, bufferSource, partialTicks, renderBasePos(entity, cameraPos, partialTicks));
    }

    private static void renderWeaponModel(MeleeProjBase entity, PoseStack poseStack, MultiBufferSource bufferSource,
                                          float partialTicks, Vec3 baseTranslation) {
        ItemStack stack = entity.getDisplayStack();
        if (stack == null || stack.isEmpty()) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(baseTranslation.x, baseTranslation.y, baseTranslation.z);
        poseStack.mulPose(entity.swingRotation());
        float rotation = Mth.lerp(partialTicks, entity.getPrevRotation(), entity.getCurrentRotation());
        float anchorRadius = entity.getOuterRadius();
        poseStack.translate(Mth.cos(rotation) * anchorRadius, Mth.sin(rotation) * anchorRadius, 0.0D);
        poseStack.mulPose(Axis.ZP.rotation(rotation));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-45.0F));
        float scale = entity.getOuterRadius() / 1.75F;
        poseStack.scale(scale, scale, scale);
        poseStack.translate(-1.0F, -1.0F, -0.5F);

        renderBakedItem(stack, poseStack, bufferSource, entity.level(), entity.getId());
        poseStack.popPose();
    }

    private static void renderBakedItem(ItemStack stack, PoseStack poseStack, MultiBufferSource bufferSource,
                                        Level level, int seed) {
        Minecraft minecraft = Minecraft.getInstance();
        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        var model = itemRenderer.getModel(stack, level, null, seed);
        if (model == null) {
            return;
        }

        VertexConsumer consumer = bufferSource.getBuffer(Sheets.translucentItemSheet());
        Pose pose = poseStack.last();
        RandomSource random = RandomSource.create();
        for (Direction direction : Direction.values()) {
            random.setSeed(42L);
            for (BakedQuad quad : model.getQuads(null, direction, random)) {
                consumer.putBulkData(pose, quad, 1.0F, 1.0F, 1.0F, 1.0F, 0xF000F0, OverlayTexture.NO_OVERLAY, true);
            }
        }
        random.setSeed(42L);
        for (BakedQuad quad : model.getQuads(null, null, random)) {
            consumer.putBulkData(pose, quad, 1.0F, 1.0F, 1.0F, 1.0F, 0xF000F0, OverlayTexture.NO_OVERLAY, true);
        }
    }

    private static void renderTrail(MeleeProjBase entity, PoseStack poseStack, MultiBufferSource bufferSource,
                                    float partialTicks, Vec3 baseTranslation) {
        float[] rotCache = entity.getRotationCache();
        if (rotCache == null || rotCache.length < 2) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(baseTranslation.x, baseTranslation.y, baseTranslation.z);
        poseStack.mulPose(entity.swingRotation());
        VertexConsumer consumer = bufferSource.getBuffer(GenesisRenderType.bladeTrail(entity.getTrailTexture()));

        float innerRadius = entity.getInnerRadius();
        float outerRadius = entity.getOuterRadius();
        int segments = entity.getTrailSegments();
        float fadePortion = Math.max(1.0E-4F, entity.getHeadFadePortion());
        float currentRot = Mth.lerp(partialTicks, entity.getPrevRotation(), entity.getCurrentRotation());
        float[] rotations = new float[segments];
        rotations[0] = currentRot;
        for (int i = 1; i < segments; i++) {
            rotations[i] = Mth.lerp(partialTicks, rotCache[i], rotCache[i - 1]);
        }

        for (int segment = 0; segment < segments - 1; segment++) {
            float rot1 = rotations[segment];
            float rot2 = rotations[segment + 1];
            for (int sub = 0; sub < entity.getSegment(); sub++) {
                float t1 = (float) sub / (float) entity.getSegment();
                float t2 = (float) (sub + 1) / (float) entity.getSegment();
                float rot1L = Mth.lerp(t1, rot1, rot2);
                float rot2L = Mth.lerp(t2, rot1, rot2);
                float prog1 = ((float) segment + t1) / (float) segments;
                float prog2 = ((float) segment + t2) / (float) segments;
                int c1 = trailColor(prog1, fadePortion);
                int c2 = trailColor(prog2, fadePortion);
                float cos1 = Mth.cos(rot1L);
                float sin1 = Mth.sin(rot1L);
                float cos2 = Mth.cos(rot2L);
                float sin2 = Mth.sin(rot2L);
                addVertex(consumer, poseStack, cos1 * innerRadius, sin1 * innerRadius, prog1, 0.0F, c1, 0xF000F0);
                addVertex(consumer, poseStack, cos1 * outerRadius, sin1 * outerRadius, prog1, 1.0F, c1, 0xF000F0);
                addVertex(consumer, poseStack, cos2 * innerRadius, sin2 * innerRadius, prog2, 0.0F, c2, 0xF000F0);
                addVertex(consumer, poseStack, cos2 * outerRadius, sin2 * outerRadius, prog2, 1.0F, c2, 0xF000F0);
            }
        }
        poseStack.popPose();
    }

    private static void setupMeleeTrailShader(MeleeProjBase entity, float partialTicks, int useType, int warpType, Integer mainTarget) {
        Minecraft minecraft = Minecraft.getInstance();
        TextureManager textureManager = minecraft.getTextureManager();
        TextureAtlas atlas = minecraft.getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS);
        ShaderInstance shader = GenesisShaders.getMeleeTrailShader();
        initStarUv(minecraft);
        shader.setSampler("Texture0", textureManager.getTexture(entity.getTrailTexture0()).getId());
        shader.setSampler("Texture1", textureManager.getTexture(entity.getTrailTexture1()).getId());
        shader.setSampler("Texture2", textureManager.getTexture(entity.getTrailTexture2()).getId());
        shader.setSampler("ColorTexture0", textureManager.getTexture(entity.getTrailColorTexture()).getId());
        shader.setSampler("BloomTexture0", textureManager.getTexture(entity.getTrailBloomTexture()).getId());
        shader.setSampler("WarpTexture0", textureManager.getTexture(entity.getTrailWarpTexture()).getId());
        shader.setSampler("StarTexture0", atlas.getId());
        if (mainTarget != null) {
            shader.setSampler("MainTarget0", mainTarget);
        }
        shader.safeGetUniform("starUV").set(STAR_UV);
        float shaderTime = minecraft.level != null ? (minecraft.level.getGameTime() + partialTicks) * 0.05F : (entity.tickCount + partialTicks) * 0.05F;
        shader.safeGetUniform("time").set(shaderTime);
        shader.safeGetUniform("speed").set(-0.625F);
        shader.safeGetUniform("brightness").set(entity.getBrightness());
        shader.safeGetUniform("useType").set(useType);
        shader.safeGetUniform("warpType").set(warpType);
        shader.safeGetUniform("cosmicScale").set(0.8F);
        if (minecraft.player != null) {
            shader.safeGetUniform("yaw").set((float) ((minecraft.player.getYRot() * 2.0F) * Math.PI / 360.0D));
            shader.safeGetUniform("pitch").set(-((float) ((minecraft.player.getXRot() * 2.0F) * Math.PI / 360.0D)));
        }
        GenesisShaders.setScreenSize(shader);
    }

    private static void initStarUv(Minecraft minecraft) {
        if (starUvInitialized) {
            return;
        }
        for (int i = 0; i < 10; i++) {
            TextureAtlasSprite sprite = minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                    .apply(new ResourceLocation("genesis_api", "item/misc/cosmic_" + i));
            STAR_UV[i * 4] = sprite.getU0();
            STAR_UV[i * 4 + 1] = sprite.getU1();
            STAR_UV[i * 4 + 2] = sprite.getV0();
            STAR_UV[i * 4 + 3] = sprite.getV1();
        }
        starUvInitialized = true;
    }

    private static int trailColor(float progress, float fadePortion) {
        float fadeIn = Mth.clamp(progress / fadePortion, 0.0F, 1.0F);
        fadeIn = fadeIn * fadeIn * (3.0F - 2.0F * fadeIn);
        float fadeOut = 1.0F - progress;
        float alpha = Mth.clamp(fadeIn * fadeOut, 0.0F, 1.0F);
        return (int) (alpha * 255.0F) << 24 | 0xFFFFFF;
    }

    private static Vec3 renderBasePos(MeleeProjBase entity, Vec3 cameraPos, float partialTicks) {
        Vec3 held = entity.getHeldRenderCenter(partialTicks);
        double x;
        double y;
        double z;
        if (held != null) {
            x = held.x;
            y = held.y;
            z = held.z;
        } else {
            x = Mth.lerp(partialTicks, entity.xOld, entity.getX());
            y = Mth.lerp(partialTicks, entity.yOld, entity.getY());
            z = Mth.lerp(partialTicks, entity.zOld, entity.getZ());
        }
        return new Vec3(x - cameraPos.x, y - cameraPos.y, z - cameraPos.z);
    }

    private static void addVertex(VertexConsumer consumer, PoseStack poseStack, float x, float y, float u, float v, int color, int light) {
        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        consumer.vertex(pose, x, y, 0.0F)
                .color(color)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(normal, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }
}
