package miku.united_as_one.genesis.effect.spell.chaos;

import miku.united_as_one.genesis.Genesis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
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
            if (entity.level() instanceof ServerLevel serverLevel && random.nextInt(3) == 0) {
                serverLevel.sendParticles(
                        ParticleTypes.SCULK_SOUL,
                        entity.getRandomX(0.6D),
                        entity.getRandomY(),
                        entity.getRandomZ(0.6D),
                        1,
                        0.08D,
                        0.12D,
                        0.08D,
                        0.01D
                );
            }
        }
        
        super.applyEffectTick(entity, amplifier);
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
