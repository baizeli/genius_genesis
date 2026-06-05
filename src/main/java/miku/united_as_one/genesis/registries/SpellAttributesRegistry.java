package miku.united_as_one.genesis.registries;

import io.redspace.ironsspellbooks.api.attribute.MagicPercentAttribute;
import miku.united_as_one.genesis.Genesis;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class SpellAttributesRegistry {
    private static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(ForgeRegistries.ATTRIBUTES, Genesis.MOD_ID);

    public static final RegistryObject<Attribute> CHAOS_MAGIC_RESIST = resistance("chaos");
    public static final RegistryObject<Attribute> CHAOS_SPELL_POWER = power("chaos");
    public static final RegistryObject<Attribute> CELESTIAL_SOURCE_MAGIC_RESIST = resistance("celestial_source");
    public static final RegistryObject<Attribute> CELESTIAL_SOURCE_SPELL_POWER = power("celestial_source");
    public static final RegistryObject<Attribute> CULINARY_MAGIC_RESIST = resistance("culinary");
    public static final RegistryObject<Attribute> CULINARY_SPELL_POWER = power("culinary");

    public static final RegistryObject<Attribute> FIRE_SPELL_PENETRATION = penetration("fire");
    public static final RegistryObject<Attribute> HOLY_SPELL_PENETRATION = penetration("holy");
    public static final RegistryObject<Attribute> ICE_SPELL_PENETRATION = penetration("ice");
    public static final RegistryObject<Attribute> BLOOD_SPELL_PENETRATION = penetration("blood");
    public static final RegistryObject<Attribute> ENDER_SPELL_PENETRATION = penetration("ender");
    public static final RegistryObject<Attribute> THUNDER_SPELL_PENETRATION = penetration("thunder");
    public static final RegistryObject<Attribute> NATURE_SPELL_PENETRATION = penetration("nature");
    public static final RegistryObject<Attribute> ELDRITCH_SPELL_PENETRATION = penetration("eldritch");
    public static final RegistryObject<Attribute> CHAOS_SPELL_PENETRATION = penetration("chaos");
    public static final RegistryObject<Attribute> CELESTIAL_SOURCE_SPELL_PENETRATION = penetration("celestial_source");

    public static final RegistryObject<Attribute> MAX_MANA_PERCENT =
            percent("max_mana_percent", -Double.MAX_VALUE, Double.MAX_VALUE);
    public static final RegistryObject<Attribute> SPELL_DAMAGE_PERCENT =
            percent("spell_damage_percent", -Double.MAX_VALUE, Double.MAX_VALUE);

    private SpellAttributesRegistry() {
    }

    public static void register(IEventBus modBus) {
        ATTRIBUTES.register(modBus);
        modBus.addListener(SpellAttributesRegistry::addEntityAttributes);
    }

    private static void addEntityAttributes(EntityAttributeModificationEvent event) {
        event.getTypes().forEach(type ->
                ATTRIBUTES.getEntries().forEach(attribute -> event.add(type, attribute.get())));
    }

    private static RegistryObject<Attribute> resistance(String school) {
        return percent(school + "_magic_resist", -Double.MAX_VALUE, Double.MAX_VALUE);
    }

    private static RegistryObject<Attribute> power(String school) {
        return percent(school + "_spell_power", -Double.MAX_VALUE, Double.MAX_VALUE);
    }

    private static RegistryObject<Attribute> penetration(String school) {
        return percent(school + "_spell_penetration", 0.0D, 2.0D);
    }

    private static RegistryObject<Attribute> percent(String id, double min, double max) {
        return ATTRIBUTES.register(id, () -> new MagicPercentAttribute(
                "attribute." + Genesis.MOD_ID + "." + id,
                1.0D,
                min,
                max
        ).setSyncable(true));
    }
}
