package miku.united_as_one.genesis.content.entity.spell.blood_boss.blood_dagger;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import io.redspace.ironsspellbooks.entity.spells.AbstractShieldEntity;
import io.redspace.ironsspellbooks.entity.spells.ShieldPart;
import io.redspace.ironsspellbooks.entity.spells.fiery_dagger.FieryDaggerEntity;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import miku.united_as_one.genesis.api.mixin.ParticleSuppressionManager;
import miku.united_as_one.genesis.content.entity.spell.blood_boss.BloodBossFireEruptionAoe;
import miku.united_as_one.genesis.registries.EntityRegistry;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.lang.reflect.Field;

public class BloodDaggerEntity extends FieryDaggerEntity {
    private static final Field ISGROUNDED;
    public boolean isZone;
    public boolean isSpell;
    public boolean isSword;

    static {
        try {
            ISGROUNDED = FieryDaggerEntity.class.getDeclaredField("isGrounded");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public BloodDaggerEntity(Level level) {
        this(EntityRegistry.BLOOD_DAGGER_PROJECTILE.get(), level);
    }

    public BloodDaggerEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    private void createFireField() {
        BloodField bloodField = new BloodField(this.level);
        bloodField.setOwner(this.getOwner());
        bloodField.setPos(Utils.moveToRelativeGroundLevel(this.level, this.position(), 3));
        bloodField.setRadius(this.explosionRadius + 1.0F);
        bloodField.setCircular();
        bloodField.setDamage(this.getDamage() * 0.5F);
        bloodField.setDuration(300);
        bloodField.setDelay(this.delay + 25);
        bloodField.setRadiusPerTick(-bloodField.getRadius() / (float) bloodField.getDuration());
        this.level.addFreshEntity(bloodField);
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        if (!this.shouldPierceShields() && (entityHitResult.getEntity() instanceof ShieldPart || entityHitResult.getEntity() instanceof AbstractShieldEntity)) {
            this.onHitBlock(new BlockHitResult(entityHitResult.getEntity().position(), Direction.fromYRot(this.getYRot()), entityHitResult.getEntity().blockPosition(), false));
        }
        entityHitResult.getEntity().hurt(new DamageSource(DamageSources.getHolderFromResource(this, ISSDamageTypes.BLOOD_MAGIC), this, this.getOwner()), this.getDamage());
        entityHitResult.getEntity().invulnerableTime = 0;
        if (this.getOwner() instanceof LivingEntity livingEntity && !isSword)
            livingEntity.heal(isZone ? this.damage * 0.5F : this.damage);
    }

    @Override
    protected void onHit(HitResult hitresult) {
        super.onHit(hitresult);
        if (!isZone && !isSword) {
            BloodBossFireEruptionAoe aoe = new BloodBossFireEruptionAoe(level, isSpell ? 5.0F : 8.0F);
            aoe.setOwner(this.getOwner());
            aoe.setDamage(isSpell ? this.damage * 0.5F : SpellRegistry.RAISE_HELL_SPELL.get().getSpellPower(1, this.getOwner()) + Utils.getWeaponDamage((LivingEntity) this.getOwner(), MobType.UNDEFINED));
            if (this.isSpell && hitresult instanceof EntityHitResult result) {
                aoe.isSpell = true;
                aoe.spellHurtEntity = result.getEntity();
            }
            aoe.moveTo(hitresult.getLocation());
            level.addFreshEntity(aoe);
        }
    }

    @Override
    public void createDaggerZone(Vec3 center) {
        MagicManager.spawnParticles(this.level, new BlastwaveParticleOptions(new Vector3f(0.72F, 0.05F, 0.05F), this.explosionRadius + 1.0F), center.x, center.y + 0.15, center.z, 1, 0.0,0.0, 0.0, 0.0, false);
        this.playSound(SoundRegistry.FIRE_CAST.get(), 2.0F, (float)Utils.random.nextIntBetweenInclusive(80, 110) * 0.01F);
        float spawnRadius = this.explosionRadius;
        float density = 1.0F;
        int rings = (int)(spawnRadius * density);
        float ringSpacing = 1.0F / density;

        for(int i = 1; i < rings; ++i) {
            float ringRadius = ringSpacing * (float)i;
            int daggerCount = (int)(ringRadius * ((float)Math.PI * 2F));
            float angle = 360.0F / (float)daggerCount * ((float)Math.PI / 180F);

            for(int j = 0; j < daggerCount; ++j) {
                Vec3 jitter = Utils.getRandomVec3(ringSpacing * 0.4F);
                Vec3 pos = Utils.moveToRelativeGroundLevel(this.level, center.add(ringRadius * Mth.sin(angle * (float)j), 0.0, ringRadius * Mth.cos(angle * (float)j)).add(jitter), 8);
                BloodDaggerEntity dagger = new BloodDaggerEntity(this.level);
                dagger.setOwner(this.getOwner());
                dagger.setDamage(this.getDamage());
                dagger.isZone = true;
                dagger.delay = this.delay + Utils.random.nextInt(20);
                dagger.setDeltaMovement(0.0, this.getSpeed(), 0.0);
                dagger.deltaMovementOld = dagger.getDeltaMovement();
                dagger.moveTo(pos);
                try {
                    ISGROUNDED.set(this, true);
                } catch (Throwable ignored) {}
                this.level.addFreshEntity(dagger);
            }
        }

        this.createFireField();
    }

    @Override
    public void tick() {
        if (this.level.isClientSide) {
            this.level.addParticle(ParticleHelper.BLOOD, this.getX(), this.getY() + (this.getBbHeight() * 0.5), this.getZ(), 0.0, 0.0, 0.0);
        }

        ParticleSuppressionManager.setSuppressEmbers(true);
        try {
            super.tick();
        } finally {
            ParticleSuppressionManager.setSuppressEmbers(false);
        }
    }

    @Override
    public void impactParticles(double x, double y, double z) {
        MagicManager.spawnParticles(this.level, ParticleHelper.BLOOD, x, y, z, 5, 0.1, 0.1, 0.1, 0.25, true);
    }

    public void trailParticles() {
        float yHeading = -((float)(Mth.atan2(this.getDeltaMovement().z, this.getDeltaMovement().x) * (double)(180F / (float)Math.PI)) + 90.0F);
        float radius = 0.25F;
        int steps = 2;
        Vec3 vec = this.getDeltaMovement();
        double x2 = this.getX();
        double x1 = x2 - vec.x;
        double y2 = this.getY();
        double y1 = y2 - vec.y;
        double z2 = this.getZ();
        double z1 = z2 - vec.z;

        for(int j = 0; j < steps; ++j) {
            double offset = 1.0 / steps * j;
            double radians = (this.tickCount + offset) / 7.5 * 360.0 * (Math.PI / 180);
            Vec3 swirl = (new Vec3(Math.cos(radians) * (double)radius, Math.sin(radians) * (double)radius, 0.0F)).yRot(yHeading * ((float)Math.PI / 180F));
            double x = Mth.lerp(offset, x1, x2) + swirl.x;
            double y = Mth.lerp(offset, y1, y2) + swirl.y + (double)(this.getBbHeight() / 2.0F);
            double z = Mth.lerp(offset, z1, z2) + swirl.z;
            Vec3 jitter = Vec3.ZERO;
            this.level.addParticle(ParticleHelper.BLOOD, x, y, z, jitter.x, jitter.y, jitter.z);
        }

    }
}
