package miku.united_as_one.genesis.client.fonts;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.EmptyGlyph;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.util.Mth;
import net.minecraft.util.StringDecomposer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import miku.united_as_one.genesis.mixin.minecraft.client.gui.font.BakedGlyphAccessor;
import miku.united_as_one.genesis.mixin.minecraft.client.gui.FontAccessor;

import javax.annotation.Nullable;
import java.util.List;

public class FontUtil {
    private static final Font font = Minecraft.getInstance().font;

    public static int drawInternal(FormattedCharSequence text, float x, float y, int color, boolean dropShadow, Matrix4f matrix, MultiBufferSource buffer, Font.DisplayMode displayMode, int backgroundColor, int packedLightCoords, int index, int type) {
        color = FontAccessor.genesis$adjustColor(color);
        Matrix4f matrix4f = new Matrix4f(matrix);
        if (dropShadow) {
            renderText(text, x, y, color, true, matrix, buffer, displayMode, backgroundColor, packedLightCoords, index, type);
            matrix4f.translate(FontAccessor.genesis$getShadowOffset());
        }

        x = renderText(text, x, y, color, false, matrix4f, buffer, displayMode, backgroundColor, packedLightCoords, index, type);
        return (int)x + (dropShadow ? 1 : 0);
    }

    public static float renderText(String text, float x, float y, int color, boolean dropShadow, Matrix4f matrix, MultiBufferSource buffer, Font.DisplayMode displayMode, int backgroundColor, int packedLightCoords, int index, int type) {
        MyStringRenderOutput font$stringrenderoutput = new MyStringRenderOutput(buffer, x, y, color, dropShadow, matrix, displayMode, packedLightCoords, index, type);
        StringDecomposer.iterateFormatted(text, Style.EMPTY, font$stringrenderoutput);
        return font$stringrenderoutput.finish(backgroundColor, x);
    }

    public static float renderText(FormattedCharSequence text, float x, float y, int color, boolean dropShadow, Matrix4f matrix, MultiBufferSource buffer, Font.DisplayMode displayMode, int backgroundColor, int packedLightCoords, int index, int type) {
        MyStringRenderOutput font$stringrenderoutput = new MyStringRenderOutput(buffer, x, y, color, dropShadow, matrix, displayMode, packedLightCoords, index, type);
        text.accept(font$stringrenderoutput);
        return font$stringrenderoutput.finish(backgroundColor, x);
    }

    @OnlyIn(Dist.CLIENT)
    public static final class MyStringRenderOutput implements FormattedCharSink {
        public final MultiBufferSource bufferSource;
        public final boolean dropShadow;
        public final float dimFactor;
        public final Matrix4f pose;
        public final Font.DisplayMode mode;
        public final int packedLightCoords;
        public float x, y;
        public int color;
        public int type;
        @Nullable public List<BakedGlyph.Effect> effects;

        private final int index; // 第几个字（用于相位偏移）
        private static final int SEGMENTS = 256; // 每个字符的分段数
        private final int[] segmentColors = new int[SEGMENTS + 1];

        public MyStringRenderOutput(MultiBufferSource bufferSource, float x, float y,
                                    int color, boolean dropShadow, Matrix4f pose,
                                    Font.DisplayMode mode, int packedLightCoords, int index, int type) {
            this.bufferSource = bufferSource;
            this.x = x;
            this.y = y;
            this.color = color;
            this.dropShadow = dropShadow;
            this.dimFactor = dropShadow ? 0.25F : 1.0F;
            this.pose = pose;
            this.mode = mode;
            this.packedLightCoords = packedLightCoords;
            this.index = index;
            this.type = type;
        }

        private int calcColor(float localPhase) {
            long time = Util.getMillis();
            if (this.type == 1) {
                float progress = (time * 0.0009f + (index + localPhase) * 0.05f) % 1.0f; // 0~1
                float hue = 0.00f;          // 固定红色
                float sat = 1.0f - progress; // 1→0  深红→灰
                float bri = 1.0f - progress; // 1→0  灰→黑
                return Mth.hsvToRgb(hue, sat, bri) | 0xFF_000000; // 强制不透明
            } else if (this.type == 2){
                float hue = (time * 0.0012f + (index + localPhase) * 0.03f) % 1.0f;            // 全色域循环
                return Mth.hsvToRgb(hue, 1, 1) | 0xFF_000000;
            }
            return this.color;
        }

        @Override
        public boolean accept(int pos, Style style, int codePoint) {
            FontAccessor fontAccessor = (FontAccessor) font;
            FontSet fontSet = fontAccessor.genesis$getFontSet(style.getFont());
            GlyphInfo glyphInfo = fontSet.getGlyphInfo(codePoint, fontAccessor.genesis$filterFishyGlyphs());
            BakedGlyph baked = style.isObfuscated() && codePoint != ' '
                    ? fontSet.getRandomGlyph(glyphInfo)
                    : fontSet.getGlyph(codePoint);

            boolean bold = style.isBold();
            float advance = glyphInfo.getAdvance(bold);

            // 生成多段颜色
            for (int i = 0; i <= SEGMENTS; i++) {
                float phase = (float) i / SEGMENTS;
                segmentColors[i] = calcColor(phase);
            }

            // 多段渲染
            if (!(baked instanceof EmptyGlyph)) {
                float shadowOff = dropShadow ? glyphInfo.getShadowOffset() : 0F;
                float blodOff = bold ? glyphInfo.getBoldOffset() : 0F;
                VertexConsumer vc = bufferSource.getBuffer(baked.renderType(mode));
                renderMultiSegment(baked, style.isItalic(), this.x + shadowOff + blodOff, this.y + shadowOff,
                        this.pose, vc, segmentColors, this.packedLightCoords);
            }

            // 下划线和删除线效果
            float lineY = dropShadow ? 1F : 0F;

            if (style.isStrikethrough()) {
                addEffect(new BakedGlyph.Effect(this.x + lineY - 1F, this.y + lineY + 4.5F, this.x + lineY + advance, this.y + lineY + 4.5F - 1F,
                        0.01F, 0F, 0F, 0F, 1F)); // 颜色在finish时统一处理
            }

            if (style.isUnderlined()) {
                addEffect(new BakedGlyph.Effect(this.x + lineY - 1F, this.y + lineY + 9F, this.x + lineY + advance, this.y + lineY + 9F - 1F,
                        0.01F, 0F, 0F, 0F, 1F));
            }

            this.x += advance;
            return true;
        }

        // 多段渲染方法
        private void renderMultiSegment(BakedGlyph glyph, boolean italic, float x, float y,
                                        Matrix4f matrix, VertexConsumer buffer, int[] colors,
                                        int packedLight) {
            BakedGlyphAccessor accessor = (BakedGlyphAccessor) glyph;
            float left = x + accessor.genesis$left();
            float right = x + accessor.genesis$right();
            float top = y + accessor.genesis$up() - 3.0F;
            float bottom = y + accessor.genesis$down() - 3.0F;

            // 斜体偏移
            float italicTop = italic ? 1.0F - 0.25F * accessor.genesis$up() : 0.0F;
            float italicBottom = italic ? 1.0F - 0.25F * accessor.genesis$down() : 0.0F;

            // 每段的宽度和UV跨度
            float segmentWidth = (right - left) / SEGMENTS;
            float segmentU = (accessor.genesis$u1() - accessor.genesis$u0()) / SEGMENTS;

            // 为每段生成四边形
            for (int i = 0; i < SEGMENTS; i++) {
                float x0 = left + i * segmentWidth;
                float x1 = left + (i + 1) * segmentWidth;
                float u0 = accessor.genesis$u0() + i * segmentU;
                float u1 = accessor.genesis$u0() + (i + 1) * segmentU;

                float[] colLeft = unpack(colors[i]);
                float[] colRight = unpack(colors[i + 1]);

                // 四个顶点
                buffer.vertex(matrix, x0 + italicTop, top, 0.0F).color(colLeft[0], colLeft[1], colLeft[2], colLeft[3]).uv(u0, accessor.genesis$v0()).uv2(packedLight).endVertex();
                buffer.vertex(matrix, x0 + italicBottom, bottom, 0.0F).color(colLeft[0], colLeft[1], colLeft[2], colLeft[3]).uv(u0, accessor.genesis$v1()).uv2(packedLight).endVertex();
                buffer.vertex(matrix, x1 + italicBottom, bottom, 0.0F).color(colRight[0], colRight[1], colRight[2], colRight[3]).uv(u1, accessor.genesis$v1()).uv2(packedLight).endVertex();
                buffer.vertex(matrix, x1 + italicTop, top, 0.0F).color(colRight[0], colRight[1], colRight[2], colRight[3]).uv(u1, accessor.genesis$v0()).uv2(packedLight).endVertex();
            }
        }

        private float[] unpack(int c) {
            return new float[]{
                    (((c >> 16) & 0xFF) / 255F) * dimFactor,
                    (((c >> 8)  & 0xFF) / 255F) * dimFactor,
                    (((c)       & 0xFF) / 255F) * dimFactor,
                    (((c >> 24) & 0xFF) / 255F)
            };
        }

        public float finish(int backgroundColor, float x) {
            if (backgroundColor != 0) {
                float alpha = (float)(backgroundColor >> 24 & 255) / 255.0F;
                float r = (float)(backgroundColor >> 16 & 255) / 255.0F;
                float g = (float)(backgroundColor >> 8 & 255) / 255.0F;
                float b = (float)(backgroundColor & 255) / 255.0F;
                this.addEffect(new BakedGlyph.Effect(x - 1.0F, this.y + 9.0F, this.x + 1.0F, this.y - 1.0F, 0.01F, r, g, b, alpha));
            }

            if (this.effects != null) {
                BakedGlyph bakedglyph = ((FontAccessor) font).genesis$getFontSet(Style.DEFAULT_FONT).whiteGlyph();
                VertexConsumer vertexconsumer = this.bufferSource.getBuffer(bakedglyph.renderType(this.mode));

                for(BakedGlyph.Effect bakedglyph$effect : this.effects) {
                    bakedglyph.renderEffect(bakedglyph$effect, this.pose, vertexconsumer, this.packedLightCoords);
                }
            }

            return this.x;
        }

        public void addEffect(BakedGlyph.Effect effect) {
            if (this.effects == null) {
                this.effects = Lists.newArrayList();
            }
            this.effects.add(effect);
        }
    }
}
