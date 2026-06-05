package miku.united_as_one.genesis.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import miku.united_as_one.genesis.registries.ItemRegistry;
import miku.united_as_one.genesis.client.render.cosmic.CosmicBakedModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void genesis$renderCosmicModel(ItemStack stack, ItemDisplayContext context, boolean leftHand,
                                           PoseStack poseStack, MultiBufferSource buffers, int packedLight,
                                           int packedOverlay, BakedModel model, CallbackInfo ci) {
        if (!(model instanceof CosmicBakedModel cosmicModel)) {
            return;
        }

        ci.cancel();
        poseStack.pushPose();
        CosmicBakedModel transformed = (CosmicBakedModel) ForgeHooksClient.handleCameraTransforms(
                poseStack, cosmicModel, context, leftHand
        );
        poseStack.translate(-0.5D, -0.5D, -0.5D);
        transformed.renderItem(stack, context, poseStack, buffers, packedLight, packedOverlay);
        poseStack.popPose();
    }

    @ModifyVariable(method = "render", at = @At("HEAD"), index = 6, argsOnly = true)
    private int genesis$fullBrightGungnir(int packedLight, ItemStack stack) {
        return stack.is(ItemRegistry.GUNGNIR.get()) ? LightTexture.FULL_BRIGHT : packedLight;
    }
}
