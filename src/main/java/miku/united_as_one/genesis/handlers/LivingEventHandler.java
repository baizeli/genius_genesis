package miku.united_as_one.genesis.handlers;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.FireBossEntity;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.registries.SpellAttributesRegistry;
import miku.united_as_one.genesis.utils.EntityUtil;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = Genesis.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LivingEventHandler {
    private static final UUID MAX_MANA_PERCENT_UUID = Mth.createInsecureUUID(RandomSource.createNewThreadLocalInstance());

    private LivingEventHandler() {
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity living = event.getEntity();
        if (living.level().isClientSide())
            return;

        AttributeInstance maxMana = living.getAttribute(AttributeRegistry.MAX_MANA.get());
        if (maxMana != null) {
            double percent = living.getAttributeValue(SpellAttributesRegistry.MAX_MANA_PERCENT.get());
            AttributeModifier modifier = new AttributeModifier(
                    MAX_MANA_PERCENT_UUID,
                    Genesis.MOD_ID + ":max_mana_percent",
                    percent - 1.0D,
                    AttributeModifier.Operation.MULTIPLY_TOTAL
            );
            maxMana.removeModifier(modifier);
            maxMana.addTransientModifier(modifier);
        }

        if (!(living instanceof FireBossEntity))
            return;

        if (!living.getPersistentData().contains(Genesis.KEY_LIFE_TICKS, Tag.TAG_INT))
            return;

        int life = living.getPersistentData().getInt(Genesis.KEY_LIFE_TICKS);
        if (life > 0) {
            living.getPersistentData().putInt(Genesis.KEY_LIFE_TICKS, life - 1);
            living.invulnerable = true;
            living.canUpdate = true;
            living.isAddedToWorld = true;
            living.hurtTime = 0;
            living.deathTime = 0;
            living.dead = false;
            living.entityData.set(LivingEntity.DATA_HEALTH_ID, living.getMaxHealth());
        } else {
            EntityUtil.killEntity(living);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingAttack(LivingAttackEvent event) {
        LivingEntity living = event.getEntity();
        if (living.level().isClientSide())
            return;
        if (!(living instanceof FireBossEntity))
            return;
        if (!living.getPersistentData().contains(Genesis.KEY_LIFE_TICKS, Tag.TAG_INT))
            return;
        event.setCanceled(true);
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity living = event.getEntity();
        if (living.level().isClientSide())
            return;
        if (!(living instanceof FireBossEntity))
            return;
        if (!living.getPersistentData().contains(Genesis.KEY_LIFE_TICKS, Tag.TAG_INT))
            return;
        event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity living = event.getEntity();
        if (living.level().isClientSide())
            return;
        if (!(living instanceof FireBossEntity))
            return;
        if (!living.getPersistentData().contains(Genesis.KEY_LIFE_TICKS, Tag.TAG_INT))
            return;
        event.setCanceled(true);
    }
}
