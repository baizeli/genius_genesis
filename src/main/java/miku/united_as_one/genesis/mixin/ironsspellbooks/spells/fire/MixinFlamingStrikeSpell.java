package miku.united_as_one.genesis.mixin.ironsspellbooks.spells.fire;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.particle.FlameStrikeParticleOptions;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.spells.fire.FlamingStrikeSpell;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import miku.united_as_one.genesis.compat.curios.GenesisCurios;
import miku.united_as_one.genesis.item.curios.RunePlusItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Comparator;

@Mixin(value = FlamingStrikeSpell.class, remap = false)
public abstract class MixinFlamingStrikeSpell extends AbstractSpell {
    @Inject(method = "getEffectiveCastTime", at = @At("HEAD"), cancellable = true)
    private void geniusGenesis$instantFireRuneStrike(int spellLevel, @Nullable LivingEntity entity,
                                                     CallbackInfoReturnable<Integer> cir) {
        if (entity != null && GenesisCurios.has(entity, RunePlusItem::isFire)) {
            cir.setReturnValue(0);
        }
    }

    @Inject(method = "onCast", at = @At("HEAD"), cancellable = true)
    private void geniusGenesis$fireRuneShadowStrike(Level level, int spellLevel, LivingEntity entity,
                                                    CastSource castSource, MagicData playerMagicData, CallbackInfo ci) {
        if (!GenesisCurios.has(entity, RunePlusItem::isFire)) {
            return;
        }

        float distance = 12.0F;
        Vec3 forward = entity.getForward();
        Vec3 end = Utils.raycastForBlock(level, entity.getEyePosition(), entity.getEyePosition().add(forward.scale(distance)), ClipContext.Fluid.NONE).getLocation();
        AABB hitbox = entity.getBoundingBox().expandTowards(end.subtract(entity.getEyePosition())).inflate(2.0D);
        var targetableEntities = level.getEntities(entity, hitbox, target ->
                !target.isSpectator()
                        && (target instanceof LivingEntity || target instanceof Projectile)
                        && target.getBoundingBox().getCenter().subtract(entity.getBoundingBox().getCenter()).normalize().dot(entity.getForward()) >= 0.85D);
        targetableEntities.sort(Comparator.comparingDouble(target -> target.distanceToSqr(entity)));

        if (!targetableEntities.isEmpty() && targetableEntities.get(0).distanceToSqr(entity) < distance * distance) {
            Entity closestEntity = targetableEntities.get(0);
            float radius = 2.5F;
            AABB damageBox = AABB.ofSize(closestEntity.getBoundingBox().getCenter(), radius, radius + 1.0F, radius).move(forward.scale(radius / 2.0F));
            end = damageBox.getCenter().add(end).scale(0.5D);
            var damageEntities = level.getEntities(entity, damageBox);
            var damageSource = this.getDamageSource(entity);

            for (Entity targetEntity : damageEntities) {
                if (targetEntity instanceof Projectile projectile && !projectile.noPhysics) {
                    projectile.setOwner(entity);
                    projectile.shoot(forward.x, forward.y, forward.z, (float) projectile.getDeltaMovement().length(), 0.0F);
                } else if (targetEntity.isAlive()
                        && entity.isPickable()
                        && Utils.hasLineOfSight(level, entity.getEyePosition(), targetEntity.getBoundingBox().getCenter(), true)
                        && DamageSources.applyDamage(targetEntity, geniusGenesis$getDamage(spellLevel, entity), damageSource)) {
                    MagicManager.spawnParticles(level, ParticleHelper.FIRE, targetEntity.getX(), targetEntity.getY() + targetEntity.getBbHeight() * 0.5F, targetEntity.getZ(), 30, targetEntity.getBbWidth() * 0.5F, targetEntity.getBbHeight() * 0.5F, targetEntity.getBbWidth() * 0.5F, 0.03D, false);
                    EnchantmentHelper.doPostDamageEffects(entity, targetEntity);
                }
            }
        }

        Vec3 rayVector = end.subtract(entity.getEyePosition());
        Vec3 impulse = rayVector.scale(1.0D / 6.0D).add(0.0D, 0.1D, 0.0D);
        entity.setDeltaMovement(entity.getDeltaMovement().scale(0.2D).add(impulse));
        entity.hurtMarked = true;
        entity.addEffect(new MobEffectInstance(MobEffectRegistry.FALL_DAMAGE_IMMUNITY.get(), 20, 0, false, false, true));

        Vec3 slashForward = impulse.normalize();
        if (slashForward.lengthSqr() < 0.01D) {
            slashForward = forward;
        }
        Vec3 particlePos = end.subtract(slashForward.scale(3.0D));
        MagicManager.spawnParticles(level,
                new FlameStrikeParticleOptions((float) slashForward.x, (float) slashForward.y, (float) slashForward.z, false, true, 1.0F),
                particlePos.x, particlePos.y + 0.3D, particlePos.z, 1, 0.0D, 0.0D, 0.0D, 0.0D, true);

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
        ci.cancel();
    }

    private float geniusGenesis$getDamage(int spellLevel, LivingEntity entity) {
        return getSpellPower(spellLevel, entity) + Utils.getWeaponDamage(entity, MobType.UNDEFINED) + EnchantmentHelper.getFireAspect(entity);
    }
}
