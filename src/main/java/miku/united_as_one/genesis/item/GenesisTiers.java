package miku.united_as_one.genesis.item;

import java.util.function.Supplier;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public enum GenesisTiers implements Tier {
    MITHRIL(4, 2031, 10.0F, 4.0F, 18, () -> Ingredient.EMPTY),
    DIVINE_METAL(5, 2600, 11.0F, 5.0F, 22, () -> Ingredient.EMPTY),
    VIOLET(5, 3200, 12.0F, 6.0F, 24, () -> Ingredient.EMPTY),
    LEGENDARY(5, 4096, 12.0F, 7.0F, 25, () -> Ingredient.EMPTY),
    DAGGER(4, 1561, 8.0F, 2.0F, 18, () -> Ingredient.EMPTY);

    private final int level;
    private final int uses;
    private final float speed;
    private final float damage;
    private final int enchantmentValue;
    private final Supplier<Ingredient> repairIngredient;

    GenesisTiers(int level, int uses, float speed, float damage, int enchantmentValue, Supplier<Ingredient> repairIngredient) {
        this.level = level;
        this.uses = uses;
        this.speed = speed;
        this.damage = damage;
        this.enchantmentValue = enchantmentValue;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getUses() {
        return uses;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return damage;
    }

    @Override
    public int getEnchantmentValue() {
        return enchantmentValue;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairIngredient.get();
    }
}
