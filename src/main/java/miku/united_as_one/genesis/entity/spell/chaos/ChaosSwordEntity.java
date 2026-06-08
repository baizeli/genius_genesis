package miku.united_as_one.genesis.entity.spell.chaos;

import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.spells.AbstractShieldEntity;
import io.redspace.ironsspellbooks.entity.spells.ShieldPart;
import io.redspace.ironsspellbooks.entity.spells.fiery_dagger.FieryDaggerEntity;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import miku.united_as_one.genesis.registries.EntityRegistry;
import miku.united_as_one.genesis.registries.SpellRegistry;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ChaosSwordEntity extends FieryDaggerEntity {
    private static final float SPELL_AOE_RADIUS = 5.0F;

    public ChaosSwordEntity(Level level) {
        this(EntityRegistry.CHAOS_SWORD.get(), level);
    }

    public ChaosSwordEntity(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!shouldPierceShields() && (result.getEntity() instanceof ShieldPart || result.getEntity() instanceof AbstractShieldEntity)) {
            onHitBlock(new BlockHitResult(result.getEntity().position(), Direction.fromYRot(getYRot()), result.getEntity().blockPosition(), false));
            return;
        }
        if (!level().isClientSide && result.getEntity() instanceof LivingEntity target) {
            DamageSources.applyDamage(target, getDamage(), spellDamageSource());
            target.invulnerableTime = 0;
            if (getOwner() instanceof LivingEntity owner) {
                owner.heal(getDamage());
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!level().isClientSide) {
            Entity excluded = result instanceof EntityHitResult entityHit ? entityHit.getEntity() : null;
            ChaosSwordAoeEntity aoe = new ChaosSwordAoeEntity(level(), getOwner(), excluded, getDamage() * 0.5F, SPELL_AOE_RADIUS);
            aoe.moveTo(result.getLocation());
            level().addFreshEntity(aoe);
        }
    }

    @Override
    public void impactParticles(double x, double y, double z) {
        MagicManager.spawnParticles(level(), ParticleHelper.BLOOD, x, y, z, 5, 0.1D, 0.1D, 0.1D, 0.25D, true);
    }

    @Override
    public void trailParticles() {
        float yHeading = -((float) (Math.atan2(getDeltaMovement().z, getDeltaMovement().x) * (180F / (float) Math.PI)) + 90.0F);
        float radius = 0.25F;
        int steps = 2;
        Vec3 vec = getDeltaMovement();
        double x2 = getX();
        double x1 = x2 - vec.x;
        double y2 = getY();
        double y1 = y2 - vec.y;
        double z2 = getZ();
        double z1 = z2 - vec.z;

        for (int i = 0; i < steps; i++) {
            double offset = 1.0D / steps * i;
            double radians = (tickCount + offset) / 7.5D * 360.0D * (Math.PI / 180.0D);
            Vec3 swirl = new Vec3(Math.cos(radians) * radius, Math.sin(radians) * radius, 0.0D).yRot(yHeading * ((float) Math.PI / 180F));
            double x = net.minecraft.util.Mth.lerp(offset, x1, x2) + swirl.x;
            double y = net.minecraft.util.Mth.lerp(offset, y1, y2) + swirl.y + getBbHeight() / 2.0F;
            double z = net.minecraft.util.Mth.lerp(offset, z1, z2) + swirl.z;
            level().addParticle(ParticleHelper.BLOOD, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    private SpellDamageSource spellDamageSource() {
        Entity owner = getOwner() == null ? this : getOwner();
        return SpellDamageSource.source(this, owner, SpellRegistry.GUTRENDER_PUNCTURE.get()).setIFrames(0);
    }
}
