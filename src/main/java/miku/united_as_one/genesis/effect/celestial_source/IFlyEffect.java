package miku.united_as_one.genesis.effect.celestial_source;

import miku.united_as_one.genesis.Genesis;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class IFlyEffect extends MobEffect {
    public IFlyEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x00FF00);
    }

    @Override
    public String getDescriptionId() {
        return "effect." + Genesis.MOD_ID + ".i_fly";
    }
}
