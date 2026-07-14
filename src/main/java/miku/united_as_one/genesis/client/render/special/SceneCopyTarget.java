package miku.united_as_one.genesis.client.render.special;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30C;

public final class SceneCopyTarget {
    private static TextureTarget copyTarget;
    private static int lastWidth;
    private static int lastHeight;

    private SceneCopyTarget() {
    }

    public static int copyMainColor() {
        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget mainTarget = minecraft.getMainRenderTarget();
        int width = mainTarget.width;
        int height = mainTarget.height;
        if (copyTarget == null || width != lastWidth || height != lastHeight) {
            if (copyTarget != null) {
                copyTarget.destroyBuffers();
            }
            copyTarget = new TextureTarget(width, height, false, Minecraft.ON_OSX);
            copyTarget.setClearColor(0.0F, 0.0F, 0.0F, 1.0F);
            copyTarget.setFilterMode(GL11.GL_LINEAR);
            lastWidth = width;
            lastHeight = height;
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager._colorMask(true, true, true, true);
        GlStateManager._glBindFramebuffer(GL30C.GL_READ_FRAMEBUFFER, mainTarget.frameBufferId);
        GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, copyTarget.frameBufferId);
        GlStateManager._glBlitFrameBuffer(0, 0, width, height, 0, 0, width, height, GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
        mainTarget.bindWrite(true);
        return copyTarget.getColorTextureId();
    }
}
