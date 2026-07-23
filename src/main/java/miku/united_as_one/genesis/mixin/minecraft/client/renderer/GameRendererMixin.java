package miku.united_as_one.genesis.mixin.minecraft.client.renderer;

import miku.united_as_one.genesis.client.render.cosmic.CosmicBakedModel;
import miku.united_as_one.genesis.client.TrailRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Final
    private RenderBuffers renderBuffers;

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/GameRenderer;renderLevel(FJLcom/mojang/blaze3d/vertex/PoseStack;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void genesis$flushDeferredCosmicItems(float partialTicks, long finishTimeNano, boolean renderLevel, CallbackInfo ci) {
        if (renderLevel && minecraft.level != null) {
            TrailRender.renderTrail(partialTicks, finishTimeNano, true);
            if (!TrailRender.shouldDeferWorldEffects()) {
                CosmicBakedModel.flushDeferredHandItems(renderBuffers.bufferSource());
            }
        } else {
            CosmicBakedModel.clearDeferredHandItems();
        }
    }
}
