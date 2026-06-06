package miku.united_as_one.genesis.handlers;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.registries.SpellAttributesRegistry;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.event.entity.living.LivingEvent;
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
        if (!living.level().isClientSide()) {
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
        }
    }
}
