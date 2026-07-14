package miku.united_as_one.genesis.spell.celestial_source;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.entity.spell.celestial_source.MeteorProjectileEntity;
import miku.united_as_one.genesis.registries.SpellSchoolRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class MeteorSpell extends CelestialSourceBaseSpell {
    private static final float PROJECTILE_SPEED = 1.7F;
    private static final float STAR_DAMAGE_MULTIPLIER = 0.5F;
    private static final float STAR_RADIUS = 4.0F;

    private final ResourceLocation spellId = Genesis.id("meteor");
    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(SpellSchoolRegistry.CELESTIAL_SOURCE_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(15)
            .build();

    public MeteorSpell() {
        this.manaCostPerLevel = 15;
        this.baseSpellPower = 8;
        this.spellPowerPerLevel = 0;
        this.castTime = 0;
        this.baseManaCost = 90;
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.damage", Utils.stringTruncation(getDamage(spellLevel, caster), 2)),
                Component.translatable("ui.irons_spellbooks.blast_count", getRecastCount(spellLevel, caster)),
                Component.translatable("ui.irons_spellbooks.radius", Utils.stringTruncation(STAR_RADIUS, 1))
        );
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
        return CastType.INSTANT;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.empty();
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundRegistry.ELDRITCH_BLAST.get());
    }

    @Override
    public int getRecastCount(int spellLevel, LivingEntity entity) {
        return 2 + spellLevel;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!playerMagicData.getPlayerRecasts().hasRecastForSpell(getSpellId())) {
            playerMagicData.getPlayerRecasts().addRecast(
                    new RecastInstance(getSpellId(), spellLevel, getRecastCount(spellLevel, entity), 80, castSource, null),
                    playerMagicData
            );
        }

        if (!level.isClientSide) {
            MeteorProjectileEntity projectile = new MeteorProjectileEntity(
                    level,
                    entity,
                    getDamage(spellLevel, entity),
                    getDamage(spellLevel, entity) * STAR_DAMAGE_MULTIPLIER,
                    STAR_RADIUS
            );
            projectile.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0.0F, PROJECTILE_SPEED, 0.0F);
            level.addFreshEntity(projectile);
        }

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    private float getDamage(int spellLevel, LivingEntity caster) {
        return getSpellPower(spellLevel, caster);
    }

    @Override
    public SpellDamageSource getDamageSource(Entity projectile, Entity attacker) {
        return super.getDamageSource(projectile, attacker).setIFrames(0);
    }
}
