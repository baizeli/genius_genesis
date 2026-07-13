package miku.united_as_one.genesis.spell.chaos;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.combat.meleeproj.MeleeProjBase;
import miku.united_as_one.genesis.combat.meleeproj.MeleeProjResources;
import miku.united_as_one.genesis.registries.EntityRegistry;
import miku.united_as_one.genesis.registries.GenesisParticles;
import miku.united_as_one.genesis.registries.ItemRegistry;
import miku.united_as_one.genesis.registries.SpellSchoolRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class BloodControlSpell extends ChaosBaseSpell {
    private static final float HEALTH_COST_PERCENT = 0.07F;
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(Genesis.MOD_ID, "blood_control");
    private final DefaultConfig defaultConfig = new DefaultConfig()
        .setMinRarity(SpellRarity.COMMON)
        .setSchoolResource(SpellSchoolRegistry.CHAOS_RESOURCE)
        .setMaxLevel(5)
        .setCooldownSeconds(5)
        .build();

    public BloodControlSpell() {
        this.baseManaCost = 30;
        this.manaCostPerLevel = 15;
        this.baseSpellPower = 5;
        this.spellPowerPerLevel = 2;
        this.castTime = 10;
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
        return List.of(
            Component.translatable(
                "ui.irons_spellbooks.damage",
                Utils.stringTruncation(getDamage(spellLevel, caster), 1)
            ),
            Component.translatable(
                "ui.genius_genesis.health_cost",
                Utils.stringTruncation(HEALTH_COST_PERCENT * 100.0F, 1)
            )
        );
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundRegistry.FLAMING_STRIKE_UPSWING.get());
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundRegistry.FLAMING_STRIKE_SWING.get());
    }

    @Override
    public boolean canBeInterrupted(net.minecraft.world.entity.player.Player player) {
        return false;
    }

    @Override
    public int getEffectiveCastTime(int spellLevel, LivingEntity entity) {
        return getCastTime(spellLevel);
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.ONE_HANDED_HORIZONTAL_SWING_ANIMATION;
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return AnimationHolder.pass();
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!level.isClientSide) {
            entity.hurt(entity.damageSources().genericKill(), entity.getMaxHealth() * HEALTH_COST_PERCENT);
            spawnBloodSlash(level, entity, getDamage(spellLevel, entity));
        }

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    private float getDamage(int spellLevel, LivingEntity caster) {
        return getSpellPower(spellLevel, caster)
                + Utils.getWeaponDamage(caster, MobType.UNDEFINED)
                + EnchantmentHelper.getFireAspect(caster);
    }

    private void spawnBloodSlash(Level level, LivingEntity caster, float damage) {
        MeleeProjBase proj = new MeleeProjBase(EntityRegistry.MELEE_PROJ_BASE.get(), level);
        proj.moveTo(caster.getX(), caster.getY() + caster.getBbHeight() * 0.65D, caster.getZ(), caster.getYRot(), caster.getXRot());
        proj.setOwner(caster);
        proj.initFacingFromOwner(caster);
        proj.setFaceOwnerDirection(true);
        proj.setHeldByOwner(true);
        proj.setUser(caster);
        proj.setDisplayStack(ItemRegistry.FIRE_BOSS_DAGGER.asStack());
        proj.setDisplayTexture(Genesis.id("textures/entity/fiery_dagger.png"));
        proj.setSpellDamageSource(this.getSpellResource());
        proj.setTrailColorTexture(MeleeProjResources.BLOOD_COLOR);
        proj.setTrailTexture(MeleeProjResources.TRAIL_9);
        proj.setTrailWarpTexture(MeleeProjResources.TRAIL_6);
        proj.setTrailLength(6);
        proj.setRemoveTimer(1);
        proj.setFirstPersonNoDepth(true);
        proj.setUseType(1);
        proj.setHitType(1);
        proj.setHitParticle(GenesisParticles.OVERLORD_PARTICLE.get());
        proj.setParticleCount(24);
        proj.setParticleSpeed(0.55F);
        proj.setParticleSpeedRandom(0.08F);
        proj.setShakeIntensity(3.0F);
        proj.setShakeTime(14);
        proj.setHitboxWidth(3.0F);
        proj.setInnerRadius(4.0F);
        proj.setHitboxExtendIn(2.75F);
        proj.setOuterRadius(11.0F);
        proj.setHitboxLength(7.0F);
        proj.setSegment(10);
        proj.setSwingSound(SoundRegistry.FLAMING_STRIKE_SWING.get());
        proj.setEndSound(SoundRegistry.FIRE_CAST.get());
        proj.setDebugHitbox(false);
        proj.setStartAngleDeg(250.0F);
        proj.setEndAngleDeg(0.0F);
        proj.setRotationDuration(2);
        proj.setDamage(damage);
        proj.setPlaneRotXDeg(-45.0F);
        proj.setPlaneRotYDeg(90.0F);
        proj.setPlaneRotZDeg(75.0F);
        level.addFreshEntity(proj);
    }
}
