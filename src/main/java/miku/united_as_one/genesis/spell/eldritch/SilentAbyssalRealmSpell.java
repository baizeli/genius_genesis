package miku.united_as_one.genesis.spell.eldritch;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.FireBossEntity;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import miku.united_as_one.genesis.Genesis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

@AutoSpellConfig
public class SilentAbyssalRealmSpell extends AbstractSpell {
    private final ResourceLocation spellId = Genesis.id("silent_abyssal_realm");
    private final DefaultConfig defaultConfig;

    public SilentAbyssalRealmSpell() {
        this.defaultConfig = new DefaultConfig()
                .setMinRarity(SpellRarity.LEGENDARY)
                .setSchoolResource(SchoolRegistry.ELDRITCH_RESOURCE)
                .setMaxLevel(1)
                .setCooldownSeconds(0.0F)
                .build();
        this.castTime = 200;
        this.baseManaCost = 10000;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return this.spellId;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return this.defaultConfig;
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
        if (!(level instanceof ServerLevel serverLevel) || level.getLevelData().isHardcore()) {
            super.onCast(level, spellLevel, caster, castSource, playerMagicData);
            return;
        }
        FireBossEntity fireBoss = new FireBossEntity(EntityRegistry.FIRE_BOSS.get(), serverLevel);
        fireBoss.setPos(caster.getX(), caster.getY(), caster.getZ());
        fireBoss.setTarget(caster);
        fireBoss.getPersistentData().putInt(Genesis.KEY_LIFE_TICKS, 400);
        AttributeInstance attackDamageInstance = fireBoss.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamageInstance != null) {
            double result = attackDamageInstance.getValue() + 999999999999.0;
            if (Double.isInfinite(result) || Double.isNaN(result))
                result = Double.POSITIVE_INFINITY;
            attackDamageInstance.setBaseValue(result);
        }
        serverLevel.addFreshEntity(fireBoss);
        super.onCast(level, spellLevel, caster, castSource, playerMagicData);
    }
}
