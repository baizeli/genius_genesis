package miku.united_as_one.genesis.spell.celestial_source;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.registries.EffectRegistry;
import miku.united_as_one.genesis.registries.SpellSchoolRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

@AutoSpellConfig
public class IFlySpell extends CelestialSourceBaseSpell {
    private final ResourceLocation spellId = Genesis.id("i_fly");
    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchoolResource(SpellSchoolRegistry.CELESTIAL_SOURCE_RESOURCE)
            .setMaxLevel(3)
            .setCooldownSeconds(50)
            .build();

    public IFlySpell() {
        this.manaCostPerLevel = 50;
        this.baseSpellPower = 180;
        this.spellPowerPerLevel = 120;
        this.castTime = 100;
        this.baseManaCost = 100;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(Component.translatable(
                "ui.irons_spellbooks.effect_length",
                Utils.timeFromTicks(getDuration(spellLevel, caster), 1)
        ));
    }

    @Override
    public int getCastTime(int spellLevel) {
        return Math.max(20, 100 - (spellLevel - 1) * 20);
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!level.isClientSide && entity instanceof ServerPlayer player) {
            player.addEffect(new MobEffectInstance(
                    EffectRegistry.I_FLY.get(),
                    getDuration(spellLevel, entity),
                    0,
                    false,
                    false,
                    true
            ));
        }

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    private int getDuration(int spellLevel, LivingEntity caster) {
        int[] durations = {60, 120, 180};
        int baseDuration = durations[Math.min(spellLevel - 1, durations.length - 1)] * 20;
        return baseDuration + (int) ((getSpellPower(spellLevel, caster) - 1.0F) * 40);
    }
}
