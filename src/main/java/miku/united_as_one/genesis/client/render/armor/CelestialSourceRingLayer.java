package miku.united_as_one.genesis.client.render.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import miku.united_as_one.genesis.item.armor.GenesisGeoArmorItem;
import miku.united_as_one.genesis.registries.ItemRegistry;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class CelestialSourceRingLayer extends GeoRenderLayer<GenesisGeoArmorItem> {
    private final GeoModel<GenesisGeoArmorItem> ringModel = new CelestialSourceRingModel();

    public CelestialSourceRingLayer(GeoArmorRenderer<GenesisGeoArmorItem> renderer) {
        super(renderer);
    }

    @Override
    public void render(
            PoseStack poseStack,
            GenesisGeoArmorItem armor,
            BakedGeoModel bakedModel,
            RenderType renderType,
            MultiBufferSource bufferSource,
            VertexConsumer buffer,
            float partialTick,
            int packedLight,
            int packedOverlay
    ) {
        GeoArmorRenderer<GenesisGeoArmorItem> armorRenderer = (GeoArmorRenderer<GenesisGeoArmorItem>) this.renderer;
        if (!(armorRenderer.getCurrentEntity() instanceof LivingEntity livingEntity)
                || armorRenderer.getCurrentSlot() != EquipmentSlot.CHEST
                || !hasFullCelestialSourceSet(livingEntity)) {
            return;
        }

        AnimationState<GenesisGeoArmorItem> animationState = new AnimationState<>(armor, 0, 0, partialTick, false);
        long instanceId = this.renderer.getInstanceId(armor);
        ringModel.addAdditionalStateData(armor, instanceId, animationState::setData);
        ringModel.handleAnimations(armor, instanceId, animationState);

        RenderType ringRenderType = RenderType.entityTranslucentEmissive(ringModel.getTextureResource(armor));
        VertexConsumer ringBuffer = bufferSource.getBuffer(ringRenderType);
        BakedGeoModel ringBakedModel = ringModel.getBakedModel(ringModel.getModelResource(armor));

        this.renderer.actuallyRender(
                poseStack,
                armor,
                ringBakedModel,
                ringRenderType,
                bufferSource,
                ringBuffer,
                true,
                partialTick,
                LightTexture.FULL_BRIGHT,
                packedOverlay,
                1.0F,
                1.0F,
                1.0F,
                1.0F
        );
    }

    private static boolean hasFullCelestialSourceSet(LivingEntity entity) {
        return entity.getItemBySlot(EquipmentSlot.HEAD).is(ItemRegistry.CELESTIAL_SOURCE_SPELL_HELMET.get())
                && entity.getItemBySlot(EquipmentSlot.CHEST).is(ItemRegistry.CELESTIAL_SOURCE_SPELL_CHESTPLATE.get())
                && entity.getItemBySlot(EquipmentSlot.LEGS).is(ItemRegistry.CELESTIAL_SOURCE_SPELL_LEGGINGS.get())
                && entity.getItemBySlot(EquipmentSlot.FEET).is(ItemRegistry.CELESTIAL_SOURCE_SPELL_BOOTS.get());
    }
}
