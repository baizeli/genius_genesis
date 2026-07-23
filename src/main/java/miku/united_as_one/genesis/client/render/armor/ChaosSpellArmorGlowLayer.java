package miku.united_as_one.genesis.client.render.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.item.armor.ChaosSpellArmorItem;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ChaosSpellArmorGlowLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation GLOW_TEXTURE = Genesis.rl("textures/models/armor/chaos_spell_grow.png");

    private final ChaosSpellArmorModel<LivingEntity> model =
            new ChaosSpellArmorModel<>(ChaosSpellArmorModel.createBodyLayer().bakeRoot());

    public ChaosSpellArmorGlowLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            T entity,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        if (entity.isInvisible() || !(getParentModel() instanceof HumanoidModel<?> humanoidModel)) {
            return;
        }

        renderSlot(poseStack, buffer, entity, humanoidModel, EquipmentSlot.CHEST);
        renderSlot(poseStack, buffer, entity, humanoidModel, EquipmentSlot.LEGS);
        renderSlot(poseStack, buffer, entity, humanoidModel, EquipmentSlot.FEET);
        renderSlot(poseStack, buffer, entity, humanoidModel, EquipmentSlot.HEAD);
    }

    private void renderSlot(
            PoseStack poseStack,
            MultiBufferSource buffer,
            T entity,
            HumanoidModel<?> humanoidModel,
            EquipmentSlot slot
    ) {
        ItemStack stack = entity.getItemBySlot(slot);
        if (!(stack.getItem() instanceof ChaosSpellArmorItem armor) || armor.getEquipmentSlot() != slot) {
            return;
        }

        model.copyFrom(humanoidModel, slot);
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucentEmissive(GLOW_TEXTURE));
        model.renderToBuffer(
                poseStack,
                vertexConsumer,
                LightTexture.FULL_BRIGHT,
                LivingEntityRenderer.getOverlayCoords(entity, 0.0F),
                1.0F,
                1.0F,
                1.0F,
                1.0F
        );
    }
}
