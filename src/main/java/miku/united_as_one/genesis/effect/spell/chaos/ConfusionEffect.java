package miku.united_as_one.genesis.effect.spell.chaos;

import miku.united_as_one.genesis.Genesis;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = Genesis.MOD_ID)
public class ConfusionEffect extends MobEffect {
    public ConfusionEffect() {
        super(MobEffectCategory.HARMFUL, 0xFF0000);
    }
    
    @Override
    public @NotNull String getDescriptionId() {
        return "effect." + Genesis.MOD_ID + ".confusion";
    }
    
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide) {
            RandomSource random = entity.level().random;
            entity.setDeltaMovement(
                (random.nextDouble() - 0.5) * 0.5, 
                entity.getDeltaMovement().y, 
                (random.nextDouble() - 0.5) * 0.5
            );
        }
        
        super.applyEffectTick(entity, amplifier);
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}