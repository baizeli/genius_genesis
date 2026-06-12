package miku.united_as_one.genesis.registries;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.effect.chaos.BloodFrenzyEffect;
import miku.united_as_one.genesis.effect.chaos.BloodWarEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public final class EffectRegistry {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, Genesis.MOD_ID);

    public static final RegistryObject<MobEffect> BLOOD_FRENZY =
            MOB_EFFECTS.register("blood_frenzy", BloodFrenzyEffect::new);

    public static final RegistryObject<MobEffect> BLOOD_WAR =
            MOB_EFFECTS.register("blood_war", BloodWarEffect::new);

    public static final RegistryObject<MobEffect> CHAOS_RESISTANCE =
            MOB_EFFECTS.register("chaos_resistance", () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0x8A2BE2) {});

    public static final RegistryObject<MobEffect> MANA_OVERDRAFT =
            MOB_EFFECTS.register("mana_overdraft", () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0x00BFFF) {
                @Override
                public double getAttributeModifierValue(int amplifier, @NotNull AttributeModifier modifier) {
                    return 0.3D + (amplifier * 0.1D);
                }
            }.addAttributeModifier(AttributeRegistry.MAX_MANA.get(), "7106886e-ea78-4383-a9d3-5b80a5e81f1b", 0.0D, AttributeModifier.Operation.MULTIPLY_TOTAL));

    public static final RegistryObject<MobEffect> MANA_SHIELD =
            MOB_EFFECTS.register("mana_shield", () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0x4682B4) {});

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}