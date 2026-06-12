package miku.united_as_one.genesis.effect.chaos;

import miku.united_as_one.genesis.Genesis;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = Genesis.MOD_ID)
public class BloodFrenzyEffect extends MobEffect {
    public BloodFrenzyEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF0000);
    }
    
    @Override
    public @NotNull String getDescriptionId() {
        return "effect." + Genesis.MOD_ID + ".blood_frenzy";
    }
    
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().getGameTime() % 20 == 0) {
            entity.heal(1.0f * (amplifier + 1));
        }
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}