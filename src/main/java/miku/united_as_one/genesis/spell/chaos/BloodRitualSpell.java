package miku.united_as_one.genesis.spell.chaos;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.registries.SpellSchoolRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

@AutoSpellConfig
public class BloodRitualSpell extends ChaosBaseSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(Genesis.MOD_ID, "blood_ritual");
    private final DefaultConfig defaultConfig = new DefaultConfig()
        .setMinRarity(SpellRarity.LEGENDARY)
        .setSchoolResource(SpellSchoolRegistry.CHAOS_RESOURCE)
        .setMaxLevel(1)
        .setCooldownSeconds(120)
        .build();

    public BloodRitualSpell() {
        this.manaCostPerLevel = 0;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 0;
        this.castTime = 0;
        this.baseManaCost = 0;
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
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!level.isClientSide) {
            entity.setHealth(1);

            // 1混沌/通用法术强度=(+)1点法力值+0.5倍转化
            playerMagicData.addMana(entity.getHealth() - 1 * 10 + (getSpellPower(spellLevel, entity) - 1) * 0.5f);
            
            entity.removeAllEffects();
        }

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }
}