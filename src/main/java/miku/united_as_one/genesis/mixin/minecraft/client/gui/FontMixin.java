package miku.united_as_one.genesis.mixin.minecraft.client.gui;

import miku.united_as_one.genesis.client.fonts.FontUtil;
import miku.united_as_one.genesis.mixin.minecraft.network.chat.TextColorAccessor;
import miku.united_as_one.genesis.registries.text.Formatting;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(Font.class)
public abstract class FontMixin {

    @Shadow
    public abstract float renderText(FormattedCharSequence p_273322_, float p_272632_, float p_273541_, int p_273200_, boolean p_273312_, Matrix4f p_273276_, MultiBufferSource p_273392_, Font.DisplayMode p_272625_, int p_273774_, int p_273371_);

    @Inject(
            method = "drawInBatch(Lnet/minecraft/util/FormattedCharSequence;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I",
            at = @At("HEAD"),
            cancellable = true
    )
    private void genesis$drawBloodWave(FormattedCharSequence sequence, float x, float y, int color, boolean shadow, Matrix4f matrix, MultiBufferSource buffer, Font.DisplayMode mode, int overlay, int light, CallbackInfoReturnable<Integer> cir) {
        final float[] currentX = {x};
        long time = Util.getMillis();
        Matrix4f pose = new Matrix4f(matrix);

        sequence.accept((index, style, codePoint) -> {
            if (Character.isWhitespace(codePoint)) {
                currentX[0] += ((Font)(Object)this).width(Character.toString(codePoint));
                return true;
            }

            float xOffset;
            float yOffset;
            FormattedCharSequence charSeq = FormattedCharSequence.forward(new String(Character.toChars(codePoint)), style);

            if (genesis$isBloodWave(style)) {
                yOffset = (float) Math.sin(time * 0.045 + index * 2.9f) * 0.35f;
                xOffset = (float) Math.cos(time * 0.045 + index * 3.7f) * 0.35f;

                if (shadow) {
                    FontUtil.renderText(charSeq, currentX[0] + xOffset, y + yOffset, 0, true, matrix, buffer, mode, overlay, light, index, 1);
                    pose.translate(FontAccessor.genesis$getShadowOffset());
                }
                currentX[0] = FontUtil.renderText(charSeq, currentX[0] + xOffset, y + yOffset, 0, false, pose, buffer, mode, overlay, light, index, 1);
            } else if (genesis$isCelestialWave(style)) {
                yOffset = (float) Math.cos(time / 200F + index);

                if (shadow) {
                    FontUtil.renderText(charSeq, currentX[0], y + yOffset, 0, true, matrix, buffer, mode, overlay, light, index, 2);
                    pose.translate(FontAccessor.genesis$getShadowOffset());
                }
                currentX[0] = FontUtil.renderText(charSeq, currentX[0], y + yOffset, 0, false, pose, buffer, mode, overlay, light, index, 2);
            } else {
                int finalColor = FontAccessor.genesis$adjustColor(color);

                if (shadow) {
                    this.renderText(charSeq, currentX[0], y, finalColor, true, matrix, buffer, mode, overlay, light);
                    pose.translate(FontAccessor.genesis$getShadowOffset());
                }
                currentX[0] = this.renderText(charSeq, currentX[0], y, finalColor, false, pose, buffer, mode, overlay, light);
            }
            return true;
        });

        cir.setReturnValue((int) currentX[0] + (shadow ? 1 : 0));
    }

    @Unique
    private boolean genesis$isBloodWave(Style style) {
        if (style.getColor() == null) {
            return false;
        }
        return Objects.equals(((TextColorAccessor) (Object) style.getColor()).genesis$name(), Formatting.BLOOD_WAVE.getName());
    }

    @Unique
    private boolean genesis$isCelestialWave(Style style) {
        if (style.getColor() == null) {
            return false;
        }
        return Objects.equals(((TextColorAccessor) (Object) style.getColor()).genesis$name(), Formatting.CELESTIAL_WAVE.getName());
    }
}
