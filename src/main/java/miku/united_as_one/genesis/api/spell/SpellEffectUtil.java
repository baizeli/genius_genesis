package miku.united_as_one.genesis.api.spell;

import miku.united_as_one.genesis.data.datagen.provider.ModMobEffectTagProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;

public class SpellEffectUtil {
    public static boolean isAffectedByChaosEffect(LivingEntity entity) {
        return entity.getActiveEffects().stream()
            .anyMatch(
                effectInstance ->
                BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effectInstance.getEffect()).is(ModMobEffectTagProvider.CHAOS_EFFECT)
            );
    }
}