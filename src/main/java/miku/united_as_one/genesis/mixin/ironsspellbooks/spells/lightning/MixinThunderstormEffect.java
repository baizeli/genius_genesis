package miku.united_as_one.genesis.mixin.ironsspellbooks.spells.lightning;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.effect.ThunderstormEffect;
import io.redspace.ironsspellbooks.entity.spells.LightningStrike;
import miku.united_as_one.genesis.compat.curios.GenesisCurios;
import miku.united_as_one.genesis.content.item.curios.RunePlusItem;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ThunderstormEffect.class, remap = false)
public abstract class MixinThunderstormEffect {
    @Inject(method = "applyEffectTick", at = @At("HEAD"), cancellable = true, remap = true)
    private void geniusGenesis$quickRuneStorm(LivingEntity entity, int amplifier, CallbackInfo ci) {
        if (!GenesisCurios.has(entity, RunePlusItem::isLightning)) {
            return;
        }

        float radiusSqr = 400.0F;
        entity.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(20.0D, 12.0D, 20.0D),
                        livingEntity -> livingEntity != entity
                                && geniusGenesis$horizontalDistanceSqr(livingEntity, entity) < radiusSqr
                                && livingEntity.isPickable()
                                && !livingEntity.isSpectator()
                                && !Utils.shouldHealEntity(entity, livingEntity)
                                && Utils.hasLineOfSight(entity.level(), entity, livingEntity, false))
                .forEach(targetEntity -> {
                    LightningStrike lightningStrike = new LightningStrike(entity.level());
                    lightningStrike.setOwner(entity);
                    lightningStrike.setDamage(ThunderstormEffect.getDamageFromAmplifier(amplifier, entity));
                    lightningStrike.setPos(targetEntity.position());
                    lightningStrike.tickCount = 14;
                    entity.level().addFreshEntity(lightningStrike);
                });

        ci.cancel();
    }

    @Unique
    private static float geniusGenesis$horizontalDistanceSqr(LivingEntity livingEntity, LivingEntity entity) {
        double dx = livingEntity.getX() - entity.getX();
        double dz = livingEntity.getZ() - entity.getZ();
        return (float) (dx * dx + dz * dz);
    }
}
