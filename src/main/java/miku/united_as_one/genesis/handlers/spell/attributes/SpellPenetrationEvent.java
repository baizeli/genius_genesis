package miku.united_as_one.genesis.handlers.spell.attributes;

import io.redspace.ironsspellbooks.api.events.SpellDamageEvent;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.registries.SpellAttributesRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

@Mod.EventBusSubscriber(modid = Genesis.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SpellPenetrationEvent {
    private static final List<PenetrationEntry> PENETRATION_ATTRIBUTES = List.of(
            entry(SchoolRegistry.FIRE, SpellAttributesRegistry.FIRE_SPELL_PENETRATION),
            entry(SchoolRegistry.HOLY, SpellAttributesRegistry.HOLY_SPELL_PENETRATION),
            entry(SchoolRegistry.ICE, SpellAttributesRegistry.ICE_SPELL_PENETRATION),
            entry(SchoolRegistry.BLOOD, SpellAttributesRegistry.BLOOD_SPELL_PENETRATION),
            entry(SchoolRegistry.ENDER, SpellAttributesRegistry.ENDER_SPELL_PENETRATION),
            entry(SchoolRegistry.LIGHTNING, SpellAttributesRegistry.THUNDER_SPELL_PENETRATION),
            entry(SchoolRegistry.NATURE, SpellAttributesRegistry.NATURE_SPELL_PENETRATION),
            entry(SchoolRegistry.ELDRITCH, SpellAttributesRegistry.ELDRITCH_SPELL_PENETRATION)
    );

    private SpellPenetrationEvent() {
    }

    @SubscribeEvent
    public static void onSpellDamage(SpellDamageEvent event) {
        if (!(event.getSpellDamageSource().getEntity() instanceof LivingEntity attacker)) {
            return;
        }

        float amount = (float) (event.getAmount()
                * attacker.getAttributeValue(SpellAttributesRegistry.SPELL_DAMAGE_PERCENT.get()));

        SchoolType school = event.getSpellDamageSource().spell().getSchoolType();
        for (PenetrationEntry entry : PENETRATION_ATTRIBUTES) {
            if (school == entry.school().get()) {
                amount *= (float) attacker.getAttributeValue(entry.attribute().get());
                break;
            }
        }

        event.setAmount(amount);
    }

    private static PenetrationEntry entry(RegistryObject<SchoolType> school, RegistryObject<Attribute> attribute) {
        return new PenetrationEntry(school, attribute);
    }

    private record PenetrationEntry(RegistryObject<SchoolType> school, RegistryObject<Attribute> attribute) {
    }
}
