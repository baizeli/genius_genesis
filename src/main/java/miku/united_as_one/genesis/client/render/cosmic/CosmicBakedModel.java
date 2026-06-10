package miku.united_as_one.genesis.client.render.cosmic;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import miku.bai_ze_li.genesis.GenesisLib;
import miku.bai_ze_li.genesis.api.render.cosmic.AvaritiaShaders;
import miku.bai_ze_li.genesis.api.render.cosmic.PerspectiveModelState;
import miku.bai_ze_li.genesis.api.render.cosmic.TransformUtils;
import miku.bai_ze_li.genesis.api.render.shader.GenesisItemShaderEffect;
import miku.bai_ze_li.genesis.api.render.shader.GenesisItemShaderRegistry;
import miku.bai_ze_li.genesis.api.render.shader.GenesisShaderCompat;
import miku.united_as_one.genesis.Genesis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public final class CosmicBakedModel implements BakedModel {
    private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
    private static final FaceBakery FACE_BAKERY = new FaceBakery();
    private static final List<DeferredCosmicItem> DEFERRED_HAND_ITEMS = new ArrayList<>();

    private final BakedModel wrapped;
    private final List<ResourceLocation> masks;
    private final ItemOverrides overrides;
    private final ModelState parentState;
    private LivingEntity entity;
    private ClientLevel level;

    public CosmicBakedModel(BakedModel wrapped, List<ResourceLocation> masks) {
        this.wrapped = wrapped;
        this.masks = List.copyOf(masks);
        this.parentState = TransformUtils.stateFromItemTransforms(wrapped.getTransforms());
        this.overrides = new ItemOverrides() {
            @Override
            public BakedModel resolve(@NotNull BakedModel originalModel, @NotNull ItemStack stack,
                                      ClientLevel level, LivingEntity entity, int seed) {
                CosmicBakedModel.this.entity = entity;
                CosmicBakedModel.this.level = level != null ? level : entity != null
                        ? (ClientLevel) entity.level()
                        : null;
                return CosmicBakedModel.this.wrapped.getOverrides().resolve(originalModel, stack, level, entity, seed);
            }
        };
    }

    public void renderItem(ItemStack stack, ItemDisplayContext transform, PoseStack poseStack,
                           MultiBufferSource buffers, int packedLight, int packedOverlay) {
        BakedModel model = wrapped.getOverrides().resolve(wrapped, stack, level, entity, 0);
        if (model == null) {
            model = wrapped;
        }

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        for (BakedModel pass : model.getRenderPasses(stack, true)) {
            for (RenderType renderType : pass.getRenderTypes(stack, true)) {
                itemRenderer.renderModelLists(pass, stack, packedLight, packedOverlay, poseStack, buffers.getBuffer(renderType));
            }
        }

        GenesisItemShaderEffect effect = GenesisItemShaderRegistry.resolve(stack);
        if (effect != null) {
            renderCosmicLayer(stack, transform, poseStack, buffers, packedLight, packedOverlay, effect);
        }
    }

    private void renderCosmicLayer(ItemStack stack, ItemDisplayContext transform, PoseStack poseStack,
                                   MultiBufferSource buffers, int packedLight, int packedOverlay,
                                   GenesisItemShaderEffect effect) {
        if (AvaritiaShaders.cosmicShader == null || AvaritiaShaders.useType == null) {
            return;
        }

        if (shouldDeferHandCosmic(transform)) {
            queueDeferredHandCosmic(stack, transform, poseStack, packedLight, packedOverlay, effect);
            return;
        }

        if (buffers instanceof MultiBufferSource.BufferSource bufferSource) {
            bufferSource.endBatch();
        }

        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
        float yaw = 0.0F;
        float pitch = 0.0F;
        float scale = effect.scale();

        if (AvaritiaShaders.inventoryRender || transform == ItemDisplayContext.GUI) {
            scale = 100.0F;
            AvaritiaShaders.cosmicIs2D.set(1);
        } else if (minecraft.player != null) {
            yaw = (float) (minecraft.player.getYRot() * 2.0F * Math.PI / 360.0F);
            pitch = -(float) (minecraft.player.getXRot() * 2.0F * Math.PI / 360.0F);
            AvaritiaShaders.cosmicIs2D.set(0);
        }

        AvaritiaShaders.cosmicTime.set((System.currentTimeMillis() - AvaritiaShaders.renderTime) / 2000.0F);
        AvaritiaShaders.cosmicYaw.set(yaw);
        AvaritiaShaders.cosmicPitch.set(pitch);
        AvaritiaShaders.cosmicExternalScale.set(scale);
        AvaritiaShaders.cosmicOpacity.set(1.0F);
        AvaritiaShaders.useType.set(effect.useType());
        AvaritiaShaders.cosmicColor.set(effect.copyColor());
        AvaritiaShaders.cosmicScreenSize.set((float) mainTarget.width, (float) mainTarget.height);
        updateCosmicUvs();

        VertexConsumer consumer = buffers.getBuffer(AvaritiaShaders.COSMIC_RENDER_TYPE);
        minecraft.getItemRenderer().renderQuadList(poseStack, consumer, buildMaskQuads(), stack, packedLight, packedOverlay);
    }

    private static boolean shouldDeferHandCosmic(ItemDisplayContext transform) {
        return GenesisShaderCompat.shouldDeferWorldEffects()
                && (transform == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                || transform == ItemDisplayContext.FIRST_PERSON_LEFT_HAND);
    }

    private void queueDeferredHandCosmic(ItemStack stack, ItemDisplayContext transform, PoseStack poseStack,
                                         int packedLight, int packedOverlay, GenesisItemShaderEffect effect) {
        DEFERRED_HAND_ITEMS.add(new DeferredCosmicItem(
                stack.copy(),
                transform,
                new Matrix4f(poseStack.last().pose()),
                new Matrix4f(RenderSystem.getProjectionMatrix()),
                new ArrayList<>(masks),
                packedLight,
                packedOverlay,
                effect.useType(),
                effect.scale(),
                effect.copyColor()
        ));
    }

    public static void clearDeferredHandItems() {
        DEFERRED_HAND_ITEMS.clear();
    }

    public static void flushDeferredHandItems(MultiBufferSource.BufferSource buffers) {
        if (DEFERRED_HAND_ITEMS.isEmpty() || AvaritiaShaders.cosmicShader == null || AvaritiaShaders.useType == null) {
            DEFERRED_HAND_ITEMS.clear();
            return;
        }

        RenderSystem.backupProjectionMatrix();
        PoseStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushPose();

        try {
            modelViewStack.setIdentity();
            RenderSystem.applyModelViewMatrix();

            for (DeferredCosmicItem item : DEFERRED_HAND_ITEMS) {
                RenderSystem.setProjectionMatrix(new Matrix4f(item.projection), VertexSorting.DISTANCE_TO_ORIGIN);

                PoseStack itemPose = new PoseStack();
                itemPose.mulPoseMatrix(item.pose);
                renderDeferredHandCosmic(item, itemPose, buffers);
                buffers.endBatch(AvaritiaShaders.COSMIC_HAND_RENDER_TYPE);
            }
        } finally {
            modelViewStack.popPose();
            RenderSystem.restoreProjectionMatrix();
            RenderSystem.applyModelViewMatrix();
            DEFERRED_HAND_ITEMS.clear();
        }
    }

    private static void renderDeferredHandCosmic(DeferredCosmicItem item, PoseStack poseStack,
                                                 MultiBufferSource buffers) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget mainTarget = minecraft.getMainRenderTarget();
        float yaw = 0.0F;
        float pitch = 0.0F;

        if (minecraft.player != null) {
            yaw = (float) (minecraft.player.getYRot() * 2.0F * Math.PI / 360.0F);
            pitch = -(float) (minecraft.player.getXRot() * 2.0F * Math.PI / 360.0F);
        }

        AvaritiaShaders.cosmicTime.set((System.currentTimeMillis() - AvaritiaShaders.renderTime) / 2000.0F);
        AvaritiaShaders.cosmicYaw.set(yaw);
        AvaritiaShaders.cosmicPitch.set(pitch);
        AvaritiaShaders.cosmicExternalScale.set(item.scale);
        AvaritiaShaders.cosmicOpacity.set(1.0F);
        AvaritiaShaders.useType.set(item.useType);
        AvaritiaShaders.cosmicColor.set(item.color);
        AvaritiaShaders.cosmicScreenSize.set((float) mainTarget.width, (float) mainTarget.height);
        AvaritiaShaders.cosmicIs2D.set(0);
        updateCosmicUvs();

        VertexConsumer consumer = buffers.getBuffer(AvaritiaShaders.COSMIC_HAND_RENDER_TYPE);
        minecraft.getItemRenderer().renderQuadList(poseStack, consumer, buildMaskQuads(item.masks), item.stack,
                item.packedLight, item.packedOverlay);
    }

    private static void updateCosmicUvs() {
        for (int i = 0; i < 10; ++i) {
            TextureAtlasSprite sprite = Minecraft.getInstance()
                    .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                    .apply(ResourceLocation.fromNamespaceAndPath(GenesisLib.MODID, "item/misc/cosmic_" + i));
            AvaritiaShaders.COSMIC_UVS[i * 4] = sprite.getU0();
            AvaritiaShaders.COSMIC_UVS[i * 4 + 1] = sprite.getV0();
            AvaritiaShaders.COSMIC_UVS[i * 4 + 2] = sprite.getU1();
            AvaritiaShaders.COSMIC_UVS[i * 4 + 3] = sprite.getV1();
        }
        AvaritiaShaders.cosmicUVs.set(AvaritiaShaders.COSMIC_UVS);
    }

    private LinkedList<BakedQuad> buildMaskQuads() {
        return buildMaskQuads(masks);
    }

    private static LinkedList<BakedQuad> buildMaskQuads(List<ResourceLocation> masks) {
        List<TextureAtlasSprite> sprites = new ArrayList<>();
        for (ResourceLocation mask : masks) {
            sprites.add(Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(mask));
        }

        LinkedList<BakedQuad> quads = new LinkedList<>();
        for (int i = 0; i < sprites.size(); i++) {
            TextureAtlasSprite sprite = sprites.get(i);
            List<BlockElement> elements = ITEM_MODEL_GENERATOR.processFrames(i, "layer" + i, sprite.contents());
            for (BlockElement element : elements) {
                for (Map.Entry<Direction, BlockElementFace> entry : element.faces.entrySet()) {
                    quads.add(FACE_BAKERY.bakeQuad(
                            element.from,
                            element.to,
                            entry.getValue(),
                            sprite,
                            entry.getKey(),
                            new PerspectiveModelState(Map.of()),
                            element.rotation,
                            element.shade,
                            ResourceLocation.fromNamespaceAndPath(Genesis.MOD_ID, "dynamic")
                    ));
                }
            }
        }
        return quads;
    }

    private record DeferredCosmicItem(ItemStack stack, ItemDisplayContext transform, Matrix4f pose,
                                      Matrix4f projection, List<ResourceLocation> masks, int packedLight,
                                      int packedOverlay, int useType, float scale, Vector4f color) {
    }

    @Override
    public boolean isCustomRenderer() {
        return true;
    }

    @Override
    public @NotNull BakedModel applyTransform(@NotNull ItemDisplayContext context, @NotNull PoseStack poseStack, boolean leftFlip) {
        if (parentState instanceof PerspectiveModelState modelState) {
            var transform = modelState.getTransform(context);
            Vector3f translation = transform.getTranslation();
            Vector3f scale = transform.getScale();
            poseStack.translate(translation.x(), translation.y(), translation.z());
            poseStack.mulPose(transform.getLeftRotation());
            poseStack.scale(scale.x(), scale.y(), scale.z());
            poseStack.mulPose(transform.getRightRotation());
            if (leftFlip) {
                poseStack.mulPose(Axis.YN.rotationDegrees(180.0F));
            }
            return this;
        }
        return BakedModel.super.applyTransform(context, poseStack, leftFlip);
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(BlockState state, Direction side, @NotNull RandomSource random) {
        return Collections.emptyList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return wrapped.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return wrapped.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return wrapped.usesBlockLight();
    }

    @Override
    public @NotNull TextureAtlasSprite getParticleIcon() {
        return wrapped.getParticleIcon();
    }

    @Override
    public @NotNull TextureAtlasSprite getParticleIcon(@NotNull ModelData data) {
        return wrapped.getParticleIcon(data);
    }

    @Override
    public @NotNull ItemOverrides getOverrides() {
        return overrides;
    }
}
