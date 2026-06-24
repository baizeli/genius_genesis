package miku.united_as_one.genesis.effect.spell.celestial_source;

import miku.united_as_one.genesis.Genesis;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import org.jetbrains.annotations.NotNull;

public class IFlyEffect extends MobEffect {
    public IFlyEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x00FF00);
    }

    @Override
    public @NotNull String getDescriptionId() {
        return "effect." + Genesis.MOD_ID + ".i_fly";
    }
}
