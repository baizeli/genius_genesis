package miku.united_as_one.genesis.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.client.model.WingModel;
import miku.united_as_one.genesis.registries.EffectRegistry;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class WingLayer extends RenderLayer<Player, PlayerModel<Player>> {
    private static final ResourceLocation WING_TEXTURE =
            Genesis.id("textures/models/armor/spell_wing.png");

    private final WingModel<Player> wingModel = new WingModel<>();

    public WingLayer(LivingEntityRenderer<Player, PlayerModel<Player>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                       Player player, float limbSwing, float limbSwingAmount, float partialTicks,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        if (!player.hasEffect(EffectRegistry.I_FLY.get())) {
            return;
        }

        poseStack.pushPose();
        if (player.isCrouching()) {
            poseStack.translate(0.0D, 0.25D, 0.0D);
        }
        poseStack.translate(0.0D, -0.1D, 0.1D);
        poseStack.scale(0.7F, 0.7F, 0.7F);

        wingModel.setupAnim(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(WING_TEXTURE));
        wingModel.renderToBuffer(poseStack, vertexConsumer, packedLight,
                OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }
}
