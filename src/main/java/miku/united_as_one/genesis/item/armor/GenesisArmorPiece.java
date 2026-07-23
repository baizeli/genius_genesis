package miku.united_as_one.genesis.item.armor;

import miku.united_as_one.genesis.item.GenesisArmorMaterials;
import net.minecraft.world.item.ArmorItem;

public interface GenesisArmorPiece {
    GenesisArmorMaterials genesisMaterial();

    ArmorItem.Type getType();
}
