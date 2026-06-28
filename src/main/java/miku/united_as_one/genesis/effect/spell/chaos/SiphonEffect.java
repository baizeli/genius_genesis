package miku.united_as_one.genesis.effect.spell.chaos;

import miku.united_as_one.genesis.Genesis;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = Genesis.MOD_ID)
public class SiphonEffect extends MobEffect {
    public SiphonEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF0000);
    }
    
    @Override
    public @NotNull String getDescriptionId() {
        return "effect." + Genesis.MOD_ID + ".siphon";
    }
    
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().getGameTime() % 20 == 0) {
            entity.hurt(entity.damageSources().genericKill(), entity.getMaxHealth() * 0.02f);
        }
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}