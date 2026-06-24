package miku.united_as_one.genesis.mixin.minecraft.world.entity;

import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.spell.chaos.ReversePlagueSpell;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"), cancellable = true)
    private void addEffect(MobEffectInstance effectInstance, Entity entity, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity living = (LivingEntity) (Object) this;

        if (!(living.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Map<LivingEntity, LivingEntity> entityMap = new HashMap<>();
        ReversePlagueSpell.entityMap.forEach(((uuid, uuid1) -> {
            LivingEntity livingEntity = (LivingEntity) serverLevel.getEntity(uuid);
            LivingEntity livingEntity1 = (LivingEntity) serverLevel.getEntity(uuid1);
            if (livingEntity != null && livingEntity1 != null) {
                entityMap.put(livingEntity, livingEntity1);
            }
        }));

        LivingEntity livingEntity = entityMap.get(living);
        if (livingEntity != null && livingEntity.getPersistentData().getLong(Genesis.MOD_ID + "remaining_time") >= serverLevel.getGameTime()) {
            if (effectInstance.effect.getCategory() == MobEffectCategory.HARMFUL) {
                livingEntity.addEffect(effectInstance);
            }
            cir.cancel();
        } else {
            ReversePlagueSpell.entityMap.remove(living.getUUID());
        }

        if (entityMap.containsValue(living) && living.getPersistentData().getLong(Genesis.MOD_ID + "remaining_time") >= serverLevel.getGameTime() && effectInstance.effect.getCategory() == MobEffectCategory.BENEFICIAL) {
            cir.cancel();
        }
    }
}
