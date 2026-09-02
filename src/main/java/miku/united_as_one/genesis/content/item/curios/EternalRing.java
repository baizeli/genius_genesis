package miku.united_as_one.genesis.content.item.curios;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

import java.util.Set;

public class EternalRing extends GenesisCurioItem {
    private static final Set<MobEffect> IMMUNE_EFFECTS = Set.of(
            MobEffects.MOVEMENT_SLOWDOWN,
            MobEffects.DIG_SLOWDOWN,
            MobEffects.HARM,
            MobEffects.CONFUSION,
            MobEffects.BLINDNESS,
            MobEffects.HUNGER,
            MobEffects.WEAKNESS,
            MobEffects.POISON,
            MobEffects.WITHER,
            MobEffects.LEVITATION,
            MobEffects.UNLUCK,
            MobEffects.DARKNESS
    );

    public EternalRing(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        clearElementalState(slotContext.entity());
    }

    public static boolean test(ItemStack stack) {
        return stack.getItem() instanceof EternalRing;
    }

    public static boolean immuneEffect(MobEffectInstance effectInstance) {
        MobEffect effect = effectInstance.getEffect();
        return IMMUNE_EFFECTS.contains(effect)
                || (effect.getCategory() == MobEffectCategory.HARMFUL && effect != MobEffects.BAD_OMEN);
    }

    public static void clearElementalState(LivingEntity entity) {
        if (entity.getTicksFrozen() > 0) {
            entity.setTicksFrozen(0);
        }
        if (entity.isOnFire()) {
            entity.clearFire();
        }
    }
}
