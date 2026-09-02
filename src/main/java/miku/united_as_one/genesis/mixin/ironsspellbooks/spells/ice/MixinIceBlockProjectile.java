package miku.united_as_one.genesis.mixin.ironsspellbooks.spells.ice;

import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.entity.spells.ice_block.IceBlockProjectile;
import miku.united_as_one.genesis.compat.curios.GenesisCurios;
import miku.united_as_one.genesis.item.curios.RunePlusItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = IceBlockProjectile.class, remap = false)
public abstract class MixinIceBlockProjectile extends AbstractMagicProjectile {
    public MixinIceBlockProjectile(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    protected abstract void handleFalling();

    @Inject(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lio/redspace/ironsspellbooks/entity/spells/ice_block/IceBlockProjectile;handleFalling()V", remap = false),
            remap = true
    )
    private void handleFalling(CallbackInfo ci) {
        Entity owner = getOwner();
        if (owner instanceof LivingEntity entity && GenesisCurios.has(entity, RunePlusItem::isIce)) {
            handleFalling();
        }
    }

    @Inject(
            method = "lambda$doImpactDamage$0",
            at = @At(value = "INVOKE", target = "Lio/redspace/ironsspellbooks/damage/DamageSources;applyDamage(Lnet/minecraft/world/entity/Entity;FLnet/minecraft/world/damagesource/DamageSource;)Z")
    )
    private void doImpactDamage(float explosionRadius, Entity target, CallbackInfo ci) {
        Entity owner = getOwner();
        if (owner instanceof LivingEntity entity
                && target instanceof LivingEntity targetLivingEntity
                && GenesisCurios.has(entity, RunePlusItem::isIce)) {
            targetLivingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 8 * 20, 4));
        }
    }
}
