package miku.united_as_one.genesis.mixin.ironsspellbooks.spells.ender;

import io.redspace.ironsspellbooks.entity.spells.comet.Comet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = Comet.class, remap = false)
public class MixinComet {
    @ModifyArg(
            method = "impactParticles",
            at = @At(value = "INVOKE", target = "Lio/redspace/ironsspellbooks/particle/BlastwaveParticleOptions;<init>(Lorg/joml/Vector3f;F)V"),
            index = 1
    )
    public float cometEnhanced(float scale) {
        Comet comet = Comet.class.cast(this);
        return comet.getExplosionRadius() / 2.0F * 1.25F;
    }
}
