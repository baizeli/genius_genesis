package miku.united_as_one.genesis.spell.chaos;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.damage.DamageSources;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.registries.SpellSchoolRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

@AutoSpellConfig
public class BloodControlSpell extends ChaosBaseSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(Genesis.MOD_ID, "blood_control");
    private final DefaultConfig defaultConfig = new DefaultConfig()
        .setMinRarity(SpellRarity.COMMON)
        .setSchoolResource(SpellSchoolRegistry.CHAOS_RESOURCE)
        .setMaxLevel(3)
        .setCooldownSeconds(10)
        .build();

    public BloodControlSpell() {
        this.manaCostPerLevel = 10;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 1;
        this.castTime = 0;
        this.baseManaCost = 10;
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
        return CastType.INSTANT;
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
            Component.translatable(
                "ui.genius_genesis.damage_multiplier",
                Utils.stringTruncation(getDamageMultiplier(spellLevel, caster), 1)
            ),
            Component.translatable(
                "ui.genius_genesis.health_cost",
                Utils.stringTruncation(getHealthCostPercentage(spellLevel) * 100, 1)
            )
        );
    }

    private float getDamageMultiplier(int spellLevel, LivingEntity caster) {
        return 1.0f + (getSpellPower(spellLevel, caster) - 1.0f) * 0.2f;
    }

    private float getHealthCostPercentage(int spellLevel) {
        return 0.5f + (spellLevel - 1) * 0.1f;
    }

    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        return Utils.preCastTargetHelper(level, entity, playerMagicData, this, 16, 0.1f);
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!level.isClientSide && playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData targetData) {
            LivingEntity targetEntity = targetData.getTarget((ServerLevel) level);
            if (targetEntity != null) {
                float healthToConsume = entity.getMaxHealth() * getHealthCostPercentage(spellLevel);

                entity.hurt(entity.damageSources().genericKill(), healthToConsume);
                DamageSources.applyDamage(
                    targetEntity, 
                    healthToConsume * getDamageMultiplier(spellLevel, entity), 
                    this.getDamageSource(entity)
                );
            }
        }

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }
}