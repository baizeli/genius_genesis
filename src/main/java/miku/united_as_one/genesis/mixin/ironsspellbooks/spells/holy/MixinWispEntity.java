package miku.united_as_one.genesis.mixin.ironsspellbooks.spells.holy;

import io.redspace.ironsspellbooks.entity.spells.wisp.WispEntity;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import miku.bai_ze_li.genesis.api.curios.ModCurios;
import miku.united_as_one.genesis.item.curios.RunePlusItem;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;

@Mixin(value = WispEntity.class, remap = false)
public class MixinWispEntity {
    @Shadow
    @Nullable
    private Entity cachedOwner;

    @Inject(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lio/redspace/ironsspellbooks/damage/DamageSources;applyDamage(Lnet/minecraft/world/entity/Entity;FLnet/minecraft/world/damagesource/DamageSource;)Z", remap = false, shift = At.Shift.AFTER),
            remap = true,
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void onTick(CallbackInfo ci, LivingEntity target) {
        Entity owner = this.cachedOwner;
        if (owner instanceof LivingEntity entity && ModCurios.hasCurios(entity, RunePlusItem::isHoly)) {
            float distance = target.distanceTo(owner);
            target.addEffect(new MobEffectInstance(MobEffectRegistry.GUIDING_BOLT.get(), Mth.ceil(distance * 20.0F)));
        }
    }
}
