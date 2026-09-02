package miku.united_as_one.genesis.data.datagen.provider;

import com.tterrag.registrate.util.entry.ItemEntry;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.armor.UpgradeOrbType;
import miku.united_as_one.genesis.registries.ItemRegistry;
import miku.united_as_one.genesis.registries.SpellAttributesRegistry;
import miku.united_as_one.genesis.content.spell.UpgradeOrbTypes;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class ModUpgradeOrbTypeProvider {
    private static final double DEFAULT_AMOUNT = 0.05D;
    private static final AttributeModifier.Operation DEFAULT_OPERATION = AttributeModifier.Operation.MULTIPLY_BASE;

    private ModUpgradeOrbTypeProvider() {
    }

    public static void bootstrap(BootstapContext<UpgradeOrbType> context) {
        type(UpgradeOrbTypes.BLOOD_SPELL_PENETRATION)
                .attribute(SpellAttributesRegistry.BLOOD_SPELL_PENETRATION)
                .container(ItemRegistry.BLOOD_ORB_PRO)
                .save(context);
        type(UpgradeOrbTypes.CELESTIAL_SOURCE_SPELL_POWER)
                .attribute(SpellAttributesRegistry.CELESTIAL_SOURCE_SPELL_POWER)
                .container(ItemRegistry.CELESTIAL_SOURCE_UPGRADE_ORB)
                .save(context);
        type(UpgradeOrbTypes.CELESTIAL_SOURCE_SPELL_PENETRATION)
                .attribute(SpellAttributesRegistry.CELESTIAL_SOURCE_SPELL_PENETRATION)
                .container(ItemRegistry.CELESTIAL_SOURCE_ORB_PRO)
                .save(context);
        type(UpgradeOrbTypes.CHAOS_SPELL_POWER)
                .attribute(SpellAttributesRegistry.CHAOS_SPELL_POWER)
                .container(ItemRegistry.CHAOS_UPGRADE_ORB)
                .save(context);
        type(UpgradeOrbTypes.CHAOS_SPELL_PENETRATION)
                .attribute(SpellAttributesRegistry.CHAOS_SPELL_PENETRATION)
                .container(ItemRegistry.CHAOS_ORB_PRO)
                .save(context);
        type(UpgradeOrbTypes.ELDRITCH_SPELL_POWER)
                .attribute(AttributeRegistry.ELDRITCH_SPELL_POWER)
                .container(ItemRegistry.ELDRITCH_UPGRADE_ORB)
                .save(context);
        type(UpgradeOrbTypes.ELDRITCH_SPELL_PENETRATION)
                .attribute(SpellAttributesRegistry.ELDRITCH_SPELL_PENETRATION)
                .container(ItemRegistry.ELDRITCH_ORB_PRO)
                .save(context);
        type(UpgradeOrbTypes.ENDER_SPELL_PENETRATION)
                .attribute(SpellAttributesRegistry.ENDER_SPELL_PENETRATION)
                .container(ItemRegistry.ENDER_ORB_PRO)
                .save(context);
        type(UpgradeOrbTypes.FIRE_SPELL_PENETRATION)
                .attribute(SpellAttributesRegistry.FIRE_SPELL_PENETRATION)
                .container(ItemRegistry.FIRE_ORB_PRO)
                .save(context);
        type(UpgradeOrbTypes.HOLY_SPELL_PENETRATION)
                .attribute(SpellAttributesRegistry.HOLY_SPELL_PENETRATION)
                .container(ItemRegistry.HOLY_ORB_PRO)
                .save(context);
        type(UpgradeOrbTypes.ICE_SPELL_PENETRATION)
                .attribute(SpellAttributesRegistry.ICE_SPELL_PENETRATION)
                .container(ItemRegistry.ICE_ORB_PRO)
                .save(context);
        type(UpgradeOrbTypes.NATURE_SPELL_PENETRATION)
                .attribute(SpellAttributesRegistry.NATURE_SPELL_PENETRATION)
                .container(ItemRegistry.NATURE_ORB_PRO)
                .save(context);
        type(UpgradeOrbTypes.THUNDER_SPELL_PENETRATION)
                .attribute(SpellAttributesRegistry.THUNDER_SPELL_PENETRATION)
                .container(ItemRegistry.THUNDER_ORB_PRO)
                .save(context);
    }

    private static UpgradeOrbTypeBuilder type(ResourceKey<UpgradeOrbType> key) {
        return new UpgradeOrbTypeBuilder(key);
    }

    private static final class UpgradeOrbTypeBuilder {
        private final ResourceKey<UpgradeOrbType> key;
        private Supplier<Attribute> attribute;
        private double amount = DEFAULT_AMOUNT;
        private AttributeModifier.Operation operation = DEFAULT_OPERATION;
        private Supplier<Item> containerItem;

        private UpgradeOrbTypeBuilder(ResourceKey<UpgradeOrbType> key) {
            this.key = key;
        }

        private UpgradeOrbTypeBuilder attribute(RegistryObject<Attribute> attribute) {
            this.attribute = attribute;
            return this;
        }

        private UpgradeOrbTypeBuilder container(ItemEntry<? extends Item> containerItem) {
            this.containerItem = () -> containerItem.get().asItem();
            return this;
        }

        private void save(BootstapContext<UpgradeOrbType> context) {
            if (attribute == null) {
                throw new IllegalStateException("Missing attribute for " + key.location());
            }
            if (containerItem == null) {
                throw new IllegalStateException("Missing container item for " + key.location());
            }
            context.register(key, new UpgradeOrbType(attribute, amount, operation, containerItem));
        }
    }
}
