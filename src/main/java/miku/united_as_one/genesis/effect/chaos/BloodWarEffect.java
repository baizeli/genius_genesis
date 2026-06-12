package miku.united_as_one.genesis.effect.chaos;

import miku.united_as_one.genesis.Genesis;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import org.jetbrains.annotations.NotNull;

public class BloodWarEffect extends MobEffect {
    public BloodWarEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF0000);
    }
    
    @Override
    public @NotNull String getDescriptionId() {
        return "effect." + Genesis.MOD_ID + ".blood_war";
    }
}