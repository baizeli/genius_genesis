package miku.united_as_one.genesis.item.spell;

import io.redspace.ironsspellbooks.item.weapons.StaffItem;
import io.redspace.ironsspellbooks.item.weapons.StaffTier;
import miku.bai_ze_li.genesis.api.render.tooltip.ITooltipParticleItem;
import miku.bai_ze_li.genesis.api.render.tooltip.TooltipParticleSystem;
import miku.united_as_one.genesis.client.tooltip.GenesisTooltipParticles;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class CelestialSourceStaff extends StaffItem implements ITooltipParticleItem {
    public CelestialSourceStaff() {
        super(new Item.Properties().rarity(Rarity.EPIC).stacksTo(1), new StaffTier(0, 0));
    }

    @Override
    public TooltipParticleSystem.ParticleConfig getParticleConfig() {
        return GenesisTooltipParticles.celestialSourceStars();
    }
}
