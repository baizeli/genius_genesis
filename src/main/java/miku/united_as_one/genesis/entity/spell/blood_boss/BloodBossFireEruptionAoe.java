package miku.united_as_one.genesis.entity.spell.blood_boss;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.spells.FireEruptionAoe;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import miku.united_as_one.genesis.registries.EntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public class BloodBossFireEruptionAoe extends FireEruptionAoe {
    int waveAnim;
    public boolean isSpell;
    public Entity spellHurtEntity;

    public BloodBossFireEruptionAoe(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.waveAnim = -1;
    }

    public BloodBossFireEruptionAoe(Level level, float radius) {
        this(EntityRegistry.BLOOD_BOSS_FIRE_ERUPTION_AOE.get(), level);
        this.setRadius(radius);
    }

    public void applyEffect(LivingEntity target) {
        SpellDamageSource damageSource = SpellRegistry.RAISE_HELL_SPELL.get().getDamageSource(this.getOwner() == null ? this : this.getOwner());
        if (isSpell) {
            if (target != spellHurtEntity) {
                DamageSources.ignoreNextKnockback(target);
                if (target.hurt(damageSource, this.damage)) {
                    target.setDeltaMovement(target.getDeltaMovement().add(0.0, 0.65, 0.0));
                    target.invulnerableTime = 0;
                    target.hurtMarked = true;
                }
                ((LivingEntity)this.getOwner()).heal(this.damage * 0.5F);
            }
        } else {
            DamageSources.ignoreNextKnockback(target);
            if (target.hurt(damageSource, this.damage)) {
                target.setDeltaMovement(target.getDeltaMovement().add(0.0, 0.65, 0.0));
                target.invulnerableTime = 0;
                target.hurtMarked = true;
            }
        }
    }

    @Override
    public void tick() {
        float radius = this.getRadius();
        Level level = this.level;
        if ((float) (this.waveAnim++) < radius) {
            if (!level.isClientSide) {
                if (this.waveAnim % 2 == 0) {
                    float volume = (float) (this.waveAnim + 8) / 16.0F;
                    this.playSound(SoundRegistry.EARTHQUAKE_IMPACT.get(), volume, (float) Utils.random.nextIntBetweenInclusive(90, 110) * 0.01F);
                }

                float circumferenceMin = (float) ((this.waveAnim - 1) * 2) * 3.14F;
                int minBlocks = Mth.clamp((int) circumferenceMin, 0, 60);
                float anglePerBlockMin = 360.0F / (float) minBlocks;

                for (int i = 0; i < minBlocks; ++i) {
                    Vec3 vec3 = new Vec3((float) this.waveAnim * Mth.cos(anglePerBlockMin * (float) i), 0.0, (float) this.waveAnim * Mth.sin(anglePerBlockMin * (float) i));
                    BlockPos blockPos = BlockPos.containing(Utils.moveToRelativeGroundLevel(level, this.position().add(vec3), 4)).below();
                    Utils.createTremorBlock(level, blockPos, 0.1F + this.random.nextFloat() * 0.2F);
                }

                List<LivingEntity> targets = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(this.getInflation().x, this.getInflation().y, this.getInflation().z));
                int r1Sqr = this.waveAnim * this.waveAnim;
                int r2Sqr = (this.waveAnim + 1) * (this.waveAnim + 1);

                for (LivingEntity target : targets) {
                    double distanceSqr = target.distanceToSqr(this);
                    if (this.canHitEntity(target) && distanceSqr >= (double) r1Sqr && distanceSqr <= (double) r2Sqr && this.canHitTargetForGroundContext(target)) {
                        this.applyEffect(target);
                        Vec3 center = target.getBoundingBox().getCenter();
                        MagicManager.spawnParticles(this.level,
                                new BlastwaveParticleOptions(new Vector3f(0.72F, 0.05F, 0.05F),
                                        target.getBbWidth() * 1.5F + 1.5F),
                                center.x, center.y - target.getBbHeight() * 0.5F, center.z, 1, 0.0, 0.F, 0.0, 0.0, true);
                    }
                }
            }
        } else {
            this.discard();
        }
    }
}
