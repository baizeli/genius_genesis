package miku.united_as_one.genesis.events;

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

/**
 * 根据{@code SpellAttributesRegistry.MAX_MANA_PERCENT}同步实体的{@code AttributeRegistry.MAX_MANA}属性
 */
@Mod.EventBusSubscriber(modid = Genesis.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AttributeSyncHandler {
    private static final UUID MAX_MANA_PERCENT_UUID = Mth.createInsecureUUID(RandomSource.createNewThreadLocalInstance());

    @SubscribeEvent
    public static void syncMaxManaPercentModifier(LivingEvent.LivingTickEvent event) {
        LivingEntity living = event.getEntity();
        if (living.level().isClientSide())
            return;

        AttributeInstance maxMana = living.getAttribute(AttributeRegistry.MAX_MANA.get());
        if (maxMana == null)
            return;

        double percent = living.getAttributeValue(SpellAttributesRegistry.MAX_MANA_PERCENT.get());
        double targetAmount = percent - 1.0D;

        AttributeModifier existing = maxMana.getModifier(MAX_MANA_PERCENT_UUID);
        if (existing != null && Double.compare(existing.getAmount(), targetAmount) == 0)
            return;

        maxMana.removeModifier(MAX_MANA_PERCENT_UUID);
        maxMana.addTransientModifier(new AttributeModifier(
                MAX_MANA_PERCENT_UUID,
                Genesis.MOD_ID + ":max_mana_percent",
                targetAmount,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        ));
    }
}
