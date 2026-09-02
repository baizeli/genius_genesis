package miku.united_as_one.genesis.content.item.spell;

import io.redspace.ironsspellbooks.item.weapons.StaffItem;
import io.redspace.ironsspellbooks.item.weapons.StaffTier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class ChaosStaff extends StaffItem {
    public ChaosStaff() {
        super(new Item.Properties().rarity(Rarity.EPIC).stacksTo(1), new StaffTier(0, 0));
    }
}
