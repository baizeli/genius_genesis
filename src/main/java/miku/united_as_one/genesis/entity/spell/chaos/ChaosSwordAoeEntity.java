package miku.united_as_one.genesis.entity.spell.chaos;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.spells.FireEruptionAoe;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import miku.united_as_one.genesis.registries.EntityRegistry;
import miku.united_as_one.genesis.registries.SpellRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.List;

public class ChaosSwordAoeEntity extends FireEruptionAoe {
    private int waveAnim = -1;
    private Entity excludedEntity;
    private int excludedEntityId = -1;

    public ChaosSwordAoeEntity(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    public ChaosSwordAoeEntity(Level level, Entity owner, Entity excludedEntity, float damage, float radius) {
        this(EntityRegistry.CHAOS_SWORD_AOE.get(), level);
        setOwner(owner);
        this.excludedEntity = excludedEntity;
        this.excludedEntityId = excludedEntity == null ? -1 : excludedEntity.getId();
        setDamage(damage);
        setRadius(radius);
    }

    @Override
    public void applyEffect(LivingEntity target) {
        if (target == this.excludedEntity || target.getId() == this.excludedEntityId) {
            return;
        }
        var damageSource = SpellDamageSource.source(this, getOwner() == null ? this : getOwner(), SpellRegistry.GUTRENDER_PUNCTURE.get()).setIFrames(0);
        DamageSources.ignoreNextKnockback(target);
        if (target.hurt(damageSource, this.damage)) {
            target.setDeltaMovement(target.getDeltaMovement().add(0.0D, 0.65D, 0.0D));
            target.invulnerableTime = 0;
            target.hurtMarked = true;
            if (getOwner() instanceof LivingEntity owner) {
                owner.heal(this.damage * 0.5F);
            }
        }
    }

    @Override
    public void tick() {
        float radius = getRadius();
        if (this.waveAnim++ < radius) {
            if (!level().isClientSide) {
                if (this.waveAnim % 2 == 0) {
                    playSound(SoundRegistry.EARTHQUAKE_IMPACT.get(), (this.waveAnim + 8.0F) / 16.0F, Utils.random.nextIntBetweenInclusive(90, 110) * 0.01F);
                }

                float circumferenceMin = (this.waveAnim - 1) * 2.0F * 3.14F;
                int minBlocks = Mth.clamp((int) circumferenceMin, 0, 60);
                float anglePerBlockMin = 360.0F / minBlocks;
                for (int i = 0; i < minBlocks; i++) {
                    Vec3 offset = new Vec3(this.waveAnim * Mth.cos(anglePerBlockMin * i), 0.0D, this.waveAnim * Mth.sin(anglePerBlockMin * i));
                    BlockPos blockPos = BlockPos.containing(Utils.moveToRelativeGroundLevel(level(), position().add(offset), 4)).below();
                    Utils.createTremorBlock(level(), blockPos, 0.1F + this.random.nextFloat() * 0.2F);
                }

                List<LivingEntity> targets = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(getInflation().x, getInflation().y, getInflation().z));
                int inner = this.waveAnim * this.waveAnim;
                int outer = (this.waveAnim + 1) * (this.waveAnim + 1);
                for (LivingEntity target : targets) {
                    double distanceSqr = target.distanceToSqr(this);
                    if (canHitEntity(target) && distanceSqr >= inner && distanceSqr <= outer && canHitTargetForGroundContext(target)) {
                        applyEffect(target);
                        Vec3 center = target.getBoundingBox().getCenter();
                        MagicManager.spawnParticles(level(), new BlastwaveParticleOptions(new Vector3f(0.72F, 0.05F, 0.05F), target.getBbWidth() * 1.5F + 1.5F),
                                center.x, center.y - target.getBbHeight() * 0.5F, center.z, 1, 0.0D, 0.0D, 0.0D, 0.0D, true);
                    }
                }
            }
        } else {
            discard();
        }
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("WaveAnim", this.waveAnim);
        tag.putInt("ExcludedEntityId", this.excludedEntityId);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.waveAnim = tag.getInt("WaveAnim");
        this.excludedEntityId = tag.getInt("ExcludedEntityId");
    }
}
