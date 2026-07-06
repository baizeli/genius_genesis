package miku.united_as_one.genesis.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OverlordParticle extends TextureSheetParticle {
    private static final int[] COLORS = {3808369, 9197042, 0xD76D77, 16756603};
    private final float rotationSpeed;

    protected OverlordParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.lifetime = 80 + this.random.nextInt(60);
        this.gravity = 0.0F;
        this.friction = 0.98F;
        this.xd = xSpeed + (this.random.nextDouble() - 0.5D) * 0.1D;
        this.yd = ySpeed + (this.random.nextDouble() - 0.5D) * 0.1D;
        this.zd = zSpeed + (this.random.nextDouble() - 0.5D) * 0.1D;
        this.quadSize = 0.05F;
        this.rotationSpeed = (this.random.nextFloat() - 0.5F) * 0.4F;
        this.roll = this.random.nextFloat() * (float) Math.PI * 2.0F;
        this.applyRandomColor();
        this.alpha = 1.0F;
    }

    private void applyRandomColor() {
        int color = COLORS[this.random.nextInt(COLORS.length)];
        this.rCol = (float) (color >> 16 & 0xFF) / 255.0F;
        this.gCol = (float) (color >> 8 & 0xFF) / 255.0F;
        this.bCol = (float) (color & 0xFF) / 255.0F;
    }

    public void setPixTexture(boolean isPix) {
        if (isPix) {
            this.quadSize = 0.025F + this.random.nextFloat() * 0.1F;
        }
    }

    public void setGlowTexture(boolean isGlow) {
        if (isGlow) {
            this.quadSize = 0.05F + this.random.nextFloat() * 0.1F;
        }
    }

    @Override
    public void tick() {
        super.tick();
        float lifeProgress = (float) this.age / (float) this.lifetime;
        this.applySmoothLinearFadeOut(lifeProgress);
        this.quadSize *= 0.8F;
        this.oRoll = this.roll;
        this.roll += this.rotationSpeed;
        this.roll += 0.01F * (this.random.nextFloat() - 0.5F);
        if (this.alpha <= 0.001F || this.quadSize <= 0.005F) {
            this.remove();
        }
    }

    private void applySmoothLinearFadeOut(float lifeProgress) {
        if (lifeProgress <= 0.7F) {
            this.alpha = 1.0F;
        } else {
            float fadeProgress = (lifeProgress - 0.7F) / 0.3F;
            fadeProgress = Math.min(fadeProgress, 1.0F);
            this.alpha = 1.0F - fadeProgress;
        }
    }

    @Override
    public int getLightColor(float partialTick) {
        return 0xF000F0;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return OverlordParticleRenderTypes.STARLINK_RENDER_TYPE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            OverlordParticle particle = new OverlordParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}
