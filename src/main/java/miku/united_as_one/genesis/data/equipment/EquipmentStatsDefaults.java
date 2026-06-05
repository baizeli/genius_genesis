package miku.united_as_one.genesis.data.equipment;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import miku.bai_ze_li.genesis.api.equipment.EquipmentStatAttributes;
import miku.bai_ze_li.genesis.api.equipment.EquipmentStats;
import miku.united_as_one.genesis.Genesis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public final class EquipmentStatsDefaults {
    public static final ResourceLocation ATTACK_DAMAGE = EquipmentStatAttributes.ATTACK_DAMAGE;
    public static final ResourceLocation ATTACK_SPEED = EquipmentStatAttributes.ATTACK_SPEED;
    public static final ResourceLocation ARMOR = EquipmentStatAttributes.ARMOR;
    public static final ResourceLocation ARMOR_TOUGHNESS = EquipmentStatAttributes.ARMOR_TOUGHNESS;
    public static final ResourceLocation KNOCKBACK_RESISTANCE = EquipmentStatAttributes.KNOCKBACK_RESISTANCE;
    public static final ResourceLocation MAX_HEALTH = new ResourceLocation("minecraft", "generic.max_health");

    private static final ResourceLocation MAX_MANA = irons("max_mana");
    private static final ResourceLocation SPELL_POWER = irons("spell_power");
    private static final ResourceLocation HOLY_SPELL_POWER = irons("holy_spell_power");
    private static final ResourceLocation FIRE_SPELL_POWER = irons("fire_spell_power");
    private static final ResourceLocation ICE_SPELL_POWER = irons("ice_spell_power");
    private static final ResourceLocation LIGHTNING_SPELL_POWER = irons("lightning_spell_power");
    private static final ResourceLocation NATURE_SPELL_POWER = irons("nature_spell_power");
    private static final ResourceLocation ENDER_SPELL_POWER = irons("ender_spell_power");
    private static final ResourceLocation BLOOD_SPELL_POWER = irons("blood_spell_power");
    private static final ResourceLocation CAST_TIME_REDUCTION = irons("cast_time_reduction");
    private static final ResourceLocation COOLDOWN_REDUCTION = irons("cooldown_reduction");
    private static final ResourceLocation MANA_REGEN = irons("mana_regen");
    private static final ResourceLocation SPELL_RESIST = irons("spell_resist");
    private static final ResourceLocation CHAOS_SPELL_POWER = Genesis.rl("chaos_spell_power");
    private static final ResourceLocation CELESTIAL_SOURCE_SPELL_POWER = Genesis.rl("celestial_source_spell_power");

    private static final Map<ResourceLocation, EquipmentStats> DEFAULTS = createDefaults();

    private EquipmentStatsDefaults() {
    }

    public static Map<ResourceLocation, EquipmentStats> all() {
        return DEFAULTS;
    }

    private static Map<ResourceLocation, EquipmentStats> createDefaults() {
        Map<ResourceLocation, EquipmentStats> defaults = new LinkedHashMap<>();

        putWeapon(defaults, "laevatain", 250, 6.0D, -2.4D);
        putWeapon(defaults, "mithril_sword", 4062, 7.0D, -1.7D);
        putWeapon(defaults, "divine_metal_sword", 5000, 15.0D, -2.7D);
        putWeapon(defaults, "divine_metal_axe", 5000, 19.0D, -3.3D);
        putWeapon(defaults, "divine_metal_pickaxe", 5000, 4.0D, -1.0D);
        putWeapon(defaults, "divine_metal_shovel", 5000, 4.0D, -1.0D);
        putWeapon(defaults, "divine_metal_hoe", 5000, 0.0D, 96.0D);
        putWeapon(defaults, "mithril_pickaxe", 4062, 0.0D, -1.6D);
        putWeapon(defaults, "violet_sword", 8000, 20.0D, -2.7D, List.of(
                attr(COOLDOWN_REDUCTION, 0.15D, AttributeModifier.Operation.MULTIPLY_TOTAL)
        ));
        putWeapon(defaults, "violet_axe", 8000, 26.0D, -3.3D);
        putWeapon(defaults, "violet_pickaxe", 8000, 0.0D, -1.6D);
        putWeapon(defaults, "violet_shovel", 8000, 4.0D, -1.0D);
        putWeapon(defaults, "violet_hoe", 8000, 0.0D, 96.0D);
        putWeapon(defaults, "fire_boss_dagger", 8000, 11.0D, -2.4D);
        putWeapon(defaults, "blood_boss_dagger", 8000, 13.0D, -2.2D);
        putWeapon(defaults, "gungnir", 0, 17.0D, -1.9D);
        putWeapon(defaults, "flying_swallow_through_willow", 1451, 6.0D, -2.4D);
        putWeapon(defaults, "chaos_staff", null, 6.0D, -3.0D, List.of(
                attr(CHAOS_SPELL_POWER, 0.15D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(COOLDOWN_REDUCTION, 0.20D, AttributeModifier.Operation.MULTIPLY_BASE)
        ));
        putWeapon(defaults, "celestial_source_staff", null, 6.0D, -3.0D, List.of(
                attr(SPELL_POWER, 0.15D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(CELESTIAL_SOURCE_SPELL_POWER, 0.25D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(COOLDOWN_REDUCTION, 0.20D, AttributeModifier.Operation.MULTIPLY_BASE)
        ));

        putDurability(defaults, "thunder_longbow", 2009);
        putDurability(defaults, "frost_longbow", 2009);
        putDurability(defaults, "witchcraft_bow", 2009);
        putDurability(defaults, "flame_bow", 2009);

        putArmorSet(defaults, "divine_metal", 1418, 1608, 1570, 1494, 5, 10, 8, 5, 5.0D, 0.2D, List.of(
                attr(MAX_MANA, 150.0D, AttributeModifier.Operation.ADDITION),
                attr(SPELL_POWER, 0.06D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(HOLY_SPELL_POWER, 0.06D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(CAST_TIME_REDUCTION, 0.07D, AttributeModifier.Operation.MULTIPLY_BASE)
        ));
        putArmorSet(defaults, "celestial_source_spell", 0, 0, 0, 0, 7, 12, 9, 6, 7.0D, 0.0D, List.of(
                attr(SPELL_POWER, 0.10D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(CELESTIAL_SOURCE_SPELL_POWER, 0.10D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(MAX_MANA, 1000.0D, AttributeModifier.Operation.ADDITION),
                attr(CAST_TIME_REDUCTION, 0.10D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(COOLDOWN_REDUCTION, 0.09D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(MANA_REGEN, 0.09D, AttributeModifier.Operation.MULTIPLY_BASE)
        ));
        putArmorSet(defaults, "chaos_spell", 3200, 3200, 3200, 3200, 6, 8, 6, 3, 4.0D, 0.0D, List.of(
                attr(SPELL_POWER, 0.07D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(CHAOS_SPELL_POWER, 0.07D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(MAX_MANA, 500.0D, AttributeModifier.Operation.ADDITION),
                attr(MAX_HEALTH, 0.50D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(HOLY_SPELL_POWER, -0.03D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(CAST_TIME_REDUCTION, 0.10D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(COOLDOWN_REDUCTION, 0.06D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(MANA_REGEN, 0.06D, AttributeModifier.Operation.MULTIPLY_BASE)
        ));
        putArmorSet(defaults, "arcane_crystal", 800, 800, 800, 800, 4, 9, 7, 4, 3.0D, 0.0D, List.of(
                attr(COOLDOWN_REDUCTION, 0.05D, AttributeModifier.Operation.MULTIPLY_TOTAL),
                attr(MAX_MANA, 175.0D, AttributeModifier.Operation.ADDITION),
                attr(SPELL_POWER, 0.07D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(SPELL_RESIST, 0.05D, AttributeModifier.Operation.MULTIPLY_TOTAL)
        ));
        putArmorSet(defaults, "violet_zenith", Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 8, 13, 10, 7, 8.0D, 1.0D, List.of(
                attr(MAX_HEALTH, 0.10D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(MAX_MANA, 130.0D, AttributeModifier.Operation.ADDITION),
                attr(SPELL_POWER, 0.07D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(COOLDOWN_REDUCTION, 0.06D, AttributeModifier.Operation.MULTIPLY_BASE)
        ));

        putCurio(defaults, "eternal_ring", List.of(
                attr(CAST_TIME_REDUCTION, 0.20D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(MAX_MANA, 200.0D, AttributeModifier.Operation.ADDITION),
                attr(SPELL_POWER, 0.15D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(ARMOR, 6.0D, AttributeModifier.Operation.ADDITION),
                attr(new ResourceLocation("minecraft", "generic.luck"), 4.0D, AttributeModifier.Operation.ADDITION),
                attr(COOLDOWN_REDUCTION, 0.20D, AttributeModifier.Operation.MULTIPLY_BASE)
        ));
        putCurio(defaults, "genesis_curse", List.of(attr(MAX_MANA, 100000.0D, AttributeModifier.Operation.ADDITION)));
        putCurio(defaults, "lao_wang_237", List.of());
        putCurio(defaults, "lightning_rune_plus", List.of(
                attr(SPELL_POWER, 0.10D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(LIGHTNING_SPELL_POWER, 0.10D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(COOLDOWN_REDUCTION, 0.15D, AttributeModifier.Operation.MULTIPLY_BASE)
        ));
        putCurio(defaults, "nature_rune_plus", runePlus(NATURE_SPELL_POWER));
        putCurio(defaults, "ender_rune_plus", runePlus(ENDER_SPELL_POWER));
        putCurio(defaults, "holy_rune_plus", runePlus(ENDER_SPELL_POWER));
        putCurio(defaults, "ice_rune_plus", runePlus(ICE_SPELL_POWER));
        putCurio(defaults, "blood_rune_plus", runePlus(BLOOD_SPELL_POWER));
        putCurio(defaults, "fire_rune_plus", runePlus(FIRE_SPELL_POWER));
        putCurio(defaults, "eldritch_rune_plus", List.of());
        putCurio(defaults, "chaos_spell_book", List.of(
                attr(CHAOS_SPELL_POWER, 0.10D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(COOLDOWN_REDUCTION, 0.30D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(SPELL_POWER, 0.10D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(MAX_MANA, 500.0D, AttributeModifier.Operation.ADDITION)
        ));
        putCurio(defaults, "celestial_source_spell_book", List.of(
                attr(SPELL_POWER, 0.10D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(MAX_MANA, 900.0D, AttributeModifier.Operation.ADDITION),
                attr(COOLDOWN_REDUCTION, 0.30D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(CELESTIAL_SOURCE_SPELL_POWER, 0.10D, AttributeModifier.Operation.MULTIPLY_BASE)
        ));
        putCurio(defaults, "lightning_spell_book", List.of(
                attr(MAX_MANA, 100.0D, AttributeModifier.Operation.ADDITION),
                attr(LIGHTNING_SPELL_POWER, 0.10D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(COOLDOWN_REDUCTION, 0.08D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(MANA_REGEN, 0.08D, AttributeModifier.Operation.MULTIPLY_BASE)
        ));
        putCurio(defaults, "disk_spell_book", List.of(
                attr(SPELL_POWER, 0.05D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(COOLDOWN_REDUCTION, 0.15D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(CAST_TIME_REDUCTION, 0.15D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(MANA_REGEN, 0.15D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(MAX_MANA, 300.0D, AttributeModifier.Operation.ADDITION)
        ));

        return Map.copyOf(defaults);
    }

    private static void putWeapon(Map<ResourceLocation, EquipmentStats> defaults, String item, int durability, double attackDamage, double attackSpeed) {
        putWeapon(defaults, item, durability, attackDamage, attackSpeed, List.of());
    }

    private static void putWeapon(Map<ResourceLocation, EquipmentStats> defaults, String item, Integer durability, double attackDamage, double attackSpeed, List<EquipmentStats.AttributeStat> attributes) {
        defaults.put(Genesis.rl(item), new EquipmentStats(durability, new EquipmentStats.WeaponStats(attackDamage, attackSpeed), null, null, attributes));
    }

    private static void putDurability(Map<ResourceLocation, EquipmentStats> defaults, String item, int durability) {
        defaults.put(Genesis.rl(item), EquipmentStats.durability(durability));
    }

    private static void putCurio(Map<ResourceLocation, EquipmentStats> defaults, String item, List<EquipmentStats.AttributeStat> attributes) {
        defaults.put(Genesis.rl(item), EquipmentStats.curio(attributes));
    }

    private static void putArmorSet(Map<ResourceLocation, EquipmentStats> defaults, String prefix, int helmetDurability, int chestplateDurability, int leggingsDurability, int bootsDurability, double helmetArmor, double chestplateArmor, double leggingsArmor, double bootsArmor, double toughness, double knockbackResistance, List<EquipmentStats.AttributeStat> attributes) {
        defaults.put(Genesis.rl(prefix + "_helmet"), EquipmentStats.armor(helmetDurability, helmetArmor, toughness, knockbackResistance, attributes));
        defaults.put(Genesis.rl(prefix + "_chestplate"), EquipmentStats.armor(chestplateDurability, chestplateArmor, toughness, knockbackResistance, attributes));
        defaults.put(Genesis.rl(prefix + "_leggings"), EquipmentStats.armor(leggingsDurability, leggingsArmor, toughness, knockbackResistance, attributes));
        defaults.put(Genesis.rl(prefix + "_boots"), EquipmentStats.armor(bootsDurability, bootsArmor, toughness, knockbackResistance, attributes));
    }

    private static EquipmentStats.AttributeStat attr(ResourceLocation attribute, double amount, AttributeModifier.Operation operation) {
        return new EquipmentStats.AttributeStat(attribute, amount, operation);
    }

    private static List<EquipmentStats.AttributeStat> runePlus(ResourceLocation schoolPower) {
        return List.of(
                attr(SPELL_POWER, 0.10D, AttributeModifier.Operation.MULTIPLY_BASE),
                attr(schoolPower, 0.10D, AttributeModifier.Operation.MULTIPLY_BASE)
        );
    }

    private static ResourceLocation irons(String path) {
        return new ResourceLocation("irons_spellbooks", path);
    }
}
