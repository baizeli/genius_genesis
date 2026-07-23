package miku.united_as_one.genesis.entity.spell.blood_boss.blood_dagger;

import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.magma_ball.FireField;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import miku.united_as_one.genesis.data.damage.DamageTypes;
import miku.united_as_one.genesis.registries.EntityRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class BloodField extends FireField {
    private DamageSource damageSource;

    public BloodField(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public BloodField(Level level) {
        this(EntityRegistry.BLOOD_FIELD.get(), level);
    }

    public void applyEffect(LivingEntity target) {
        if (this.damageSource == null) {
            this.damageSource = new DamageSource(DamageSources.getHolderFromResource(target, DamageTypes.BLOOD_FIELD), this, this.getOwner());
        }

        if (!DamageSources.isFriendlyFireBetween(this.getOwner(), target)) {
            DamageSources.ignoreNextKnockback(target);
            if (target.hurt(this.damageSource, this.getDamage())) {
                target.invulnerableTime = 0;
            }
        }

    }

    public Optional<ParticleOptions> getParticle() {
        return Optional.of(ParticleHelper.BLOOD);
    }
}
