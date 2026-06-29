package miku.united_as_one.genesis.effect.spell.chaos;

import miku.united_as_one.genesis.Genesis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = Genesis.MOD_ID)
public class WarpedBarrierEffect extends MobEffect {
    public WarpedBarrierEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF0000);
    }

    @Override
    public @NotNull String getDescriptionId() {
        return "effect." + Genesis.MOD_ID + ".warped_barrier";
    }

    @Override
    public void removeAttributeModifiers(LivingEntity livingEntity, @NotNull AttributeMap attributeMap, int amplifier) {
        String key = Genesis.MOD_ID + ":shield_amount";
        CompoundTag data = livingEntity.getPersistentData();

        if (data.contains(key, CompoundTag.TAG_FLOAT)) {
            livingEntity.setAbsorptionAmount(livingEntity.getAbsorptionAmount() - data.getFloat(key));
            data.remove(key);
        }
        super.removeAttributeModifiers(livingEntity, attributeMap, amplifier);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
