package miku.united_as_one.genesis.utils;

import miku.united_as_one.genesis.content.item.GenesisArmorMaterials;
import miku.united_as_one.genesis.content.item.armor.GenesisArmorPiece;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public final class ArmorSetUtil {
    private ArmorSetUtil() {
    }

    public static boolean isGenesisArmor(ItemStack stack) {
        return stack.getItem() instanceof GenesisArmorPiece;
    }

    public static boolean isGenesisArmorMaterial(ItemStack stack, GenesisArmorMaterials material) {
        return stack.getItem() instanceof GenesisArmorPiece armor && armor.genesisMaterial() == material;
    }

    public static boolean hasFullSet(LivingEntity entity, GenesisArmorMaterials material) {
        return isWearing(entity, material, EquipmentSlot.HEAD)
                && isWearing(entity, material, EquipmentSlot.CHEST)
                && isWearing(entity, material, EquipmentSlot.LEGS)
                && isWearing(entity, material, EquipmentSlot.FEET);
    }

    public static boolean isWearing(LivingEntity entity, GenesisArmorMaterials material, EquipmentSlot slot) {
        return isGenesisArmorMaterial(entity.getItemBySlot(slot), material);
    }
}
