package miku.united_as_one.genesis.spell.chaos;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.api.spell.SpellUtils;
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
public class BloodWarSpell extends ChaosBaseSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(Genesis.MOD_ID, "blood_war");
    private final DefaultConfig defaultConfig = new DefaultConfig()
        .setMinRarity(SpellRarity.LEGENDARY)
        .setSchoolResource(SpellSchoolRegistry.CHAOS_RESOURCE)
        .setMaxLevel(3)
        .setCooldownSeconds(102)
        .build();

    public BloodWarSpell() {
        this.manaCostPerLevel = 20;
        this.baseSpellPower = 60;
        this.spellPowerPerLevel = 60;
        this.castTime = 20;
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
        int duration = getBuffDuration(spellLevel);
        double[] spellPowerBonuses = {1.0, 2.0, 3.0}; // 法强
        double[] damageBonuses = {1.0, 2.0, 3.0}; // 伤害
        double[] speedBonuses = {3.0, 4.0, 5.0}; // 移速
        
        return List.of(
            Component.translatable("ui.irons_spellbooks.cooldown",
                Utils.timeFromTicks(getCooldownInTicks(CastSource.COMMAND, caster), 1)
            ),
            Component.translatable("ui.irons_spellbooks.effect_length",
                Utils.timeFromTicks(duration, 1)
            ),
            Component.translatable(
                "ui.genius_genesis.spell_power",
                Utils.stringTruncation(spellPowerBonuses[spellLevel - 1], 1)
            ),
            Component.translatable(
                "ui.irons_spellbooks.damage", 
                Utils.stringTruncation(damageBonuses[spellLevel - 1], 1)
            ),
            Component.translatable(
                "ui.genius_genesis.movement_speed",
                Utils.stringTruncation(speedBonuses[spellLevel - 1], 1)
            )
        );
    }

    private int getCooldownInTicks(CastSource castSource, LivingEntity caster) {
        double playerCooldownModifier = 1d;
        float itemCoolDownModifer = 1f;

        if (caster != null) playerCooldownModifier = caster.getAttributeValue(AttributeRegistry.COOLDOWN_REDUCTION.get());
        if (castSource == CastSource.SWORD) itemCoolDownModifer = ServerConfigs.SWORDS_CD_MULTIPLIER.get().floatValue();
        
        return (int) (2040 * ((double) 2 - Utils.softCapFormula(playerCooldownModifier)) * itemCoolDownModifer);
    }

    private int getBuffDuration(int spellLevel) {
        int[] durations = {50, 55, 60};
        return durations[Math.min(spellLevel - 1, durations.length - 1)] * 20;
    }

    @Override
    public void castSpell(Level world, int spellLevel, ServerPlayer serverPlayer, CastSource castSource, boolean triggerCooldown) {
        super.castSpell(world, spellLevel, serverPlayer, castSource, triggerCooldown);
        if (!MagicData.getPlayerMagicData(serverPlayer).getPlayerRecasts().hasRecastForSpell(this.getSpellId()) && triggerCooldown && (!serverPlayer.isCreative() || ServerConfigs.CREATIVE_COOLDOWN.get())) {
            SpellUtils.addCooldown(serverPlayer, this, castSource, getCooldownInTicks(castSource, serverPlayer));
        }
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!level.isClientSide && entity instanceof ServerPlayer player) {
            player.addEffect(new MobEffectInstance(
                EffectRegistry.BLOOD_WAR.get(),
                getBuffDuration(spellLevel),
                spellLevel - 1,
                false,
                false,
                true
            ));
        }

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }
}