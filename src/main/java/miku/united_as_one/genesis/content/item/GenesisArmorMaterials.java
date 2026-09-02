package miku.united_as_one.genesis.content.item;

import io.redspace.ironsspellbooks.item.armor.IronsExtendedArmorMaterial;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.crafting.Ingredient;

public enum GenesisArmorMaterials implements IronsExtendedArmorMaterial {
    // Concrete combat values are supplied by the generated equipment_stats config.
    DIVINE_METAL("divine_metal", 40),
    CELESTIAL_SOURCE_SPELL("celestial_source_spell", 40),
    CHAOS_SPELL("chaos_spell", 40),
    VIOLET_ZENITH("violet_zenith", 35),
    ARCANE_CRYSTAL("arcane_crystal", 30);

    private static final Map<ArmorItem.Type, Integer> BASE_DURABILITY = new EnumMap<>(ArmorItem.Type.class);

    static {
        BASE_DURABILITY.put(ArmorItem.Type.HELMET, 11);
        BASE_DURABILITY.put(ArmorItem.Type.CHESTPLATE, 16);
        BASE_DURABILITY.put(ArmorItem.Type.LEGGINGS, 15);
        BASE_DURABILITY.put(ArmorItem.Type.BOOTS, 13);
    }

    private final String name;
    private final int enchantmentValue;

    GenesisArmorMaterials(String name, int enchantmentValue) {
        this.name = name;
        this.enchantmentValue = enchantmentValue;
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return 0;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return 0;
    }

    @Override
    public int getEnchantmentValue() {
        return enchantmentValue;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_NETHERITE;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.EMPTY;
    }

    @Override
    public Map<Attribute, AttributeModifier> getAdditionalAttributes() {
        return Map.of();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float getToughness() {
        return 0.0F;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.0F;
    }
}
