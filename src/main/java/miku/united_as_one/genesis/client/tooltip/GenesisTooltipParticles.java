package miku.united_as_one.genesis.client.tooltip;

import miku.bai_ze_li.genesis.api.render.tooltip.PTID;
import miku.bai_ze_li.genesis.api.render.tooltip.TooltipParticleSystem;
import net.minecraft.client.Minecraft;

public final class GenesisTooltipParticles {
    private GenesisTooltipParticles() {
    }

    public static TooltipParticleSystem.ParticleConfig celestialSourceStars() {
        Minecraft minecraft = Minecraft.getInstance();
        int mouseX = (int) minecraft.mouseHandler.xpos();
        int mouseY = (int) minecraft.mouseHandler.ypos();

        return new TooltipParticleSystem.ParticleConfig()
                .setTextures(
                        PTID.Star_0,
                        PTID.Star_1,
                        PTID.Star_2,
                        PTID.Star_3,
                        PTID.Star_4,
                        PTID.Star_5,
                        PTID.Star_6,
                        PTID.Star_7,
                        PTID.Star_8,
                        PTID.Star_9
                )
                .setParticleCount(1, 3)
                .setMaxTotalParticles(400)
                .setSize(4.0F, 12.0F)
                .setRandomSize(false)
                .setLife(3.0F, 4.0F)
                .setSpeed(70.0F, 100.0F)
                .setColors(0xFFFFB800, 0xFFCD7231, 0xFFFFEB00, 0xFFE0AE2D)
                .setRainbowColors(true, 2.0F)
                .setColorTransitionSpeed(1.5F)
                .setGravity(false, 40.0F)
                .setWind(true, 0.0F, 110.0F)
                .setAirResistance(0.1F)
                .setBounciness(10.0F)
                .setRotation(true, 0.0F, 0.01F)
                .setInitialRotation(0.0F, 360.0F)
                .setMotionType(TooltipParticleSystem.MotionType.RAIN)
                .setMotionProperties(20.0F, 1.5F)
                .setCenter((float) mouseX / 3.0F, (float) mouseY / 3.0F)
                .setRadius(200.0F)
                .setFadeIn(true, 0.05F)
                .setFadeOut(true, 0.25F)
                .setDepthLayers(true, 12, 0.08F)
                .setSizeCurve(TooltipParticleSystem.BezierCurveType.STAR_EXPAND)
                .setAlphaCurve(TooltipParticleSystem.BezierCurveType.NONE)
                .setSpeedCurve(TooltipParticleSystem.BezierCurveType.NONE)
                .setRotationCurve(TooltipParticleSystem.BezierCurveType.NONE);
    }
}
