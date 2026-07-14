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
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.entity.spell.celestial_source.MeteorShockwaveEntity;
import miku.united_as_one.genesis.registries.EffectRegistry;
import miku.united_as_one.genesis.registries.SpellSchoolRegistry;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class ConfusionSpell extends ChaosBaseSpell {
    private static final float RADIUS = 7.0F;
    private static final float BASE_DURATION_SECONDS = 3.0F;
    private static final float DURATION_SECONDS_PER_POWER = 2.0F;

    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(Genesis.MOD_ID, "confusion");
    private final DefaultConfig defaultConfig = new DefaultConfig()
        .setMinRarity(SpellRarity.LEGENDARY)
        .setSchoolResource(SpellSchoolRegistry.CHAOS_RESOURCE)
        .setMaxLevel(3)
        .setCooldownSeconds(60)
        .build();

    public ConfusionSpell() {
        this.manaCostPerLevel = 10;
        this.baseManaCost = 20;
        this.castTime = 10;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 1;
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
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundEvents.WITHER_SPAWN);
    }

    @Override
    public boolean canBeInterrupted(Player player) {
        return false;
    }

    @Override
    public int getEffectiveCastTime(int spellLevel, LivingEntity entity) {
        return getCastTime(spellLevel);
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.STOMP;
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return AnimationHolder.pass();
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
            Component.translatable(
                "ui.irons_spellbooks.duration",
                Utils.timeFromTicks(getDuration(spellLevel, caster), 1)
            )
        );
    }

    private int getDuration(int spellLevel, LivingEntity caster) {
        return Mth.floor((BASE_DURATION_SECONDS + getSpellPower(spellLevel, caster) * DURATION_SECONDS_PER_POWER) * 20.0F);
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!level.isClientSide) {
            spawnShockwave(level, entity);
            applyConfusionToNearby(level, spellLevel, entity);
        }
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    private void spawnShockwave(Level level, LivingEntity caster) {
        level.addFreshEntity(new MeteorShockwaveEntity(
                level,
                caster.position().add(0.0D, 0.05D, 0.0D),
                RADIUS,
                caster.getRandom().nextInt()
        ));
    }

    private void applyConfusionToNearby(Level level, int spellLevel, LivingEntity caster) {
        for (LivingEntity target : level.getEntitiesOfClass(
                LivingEntity.class, caster.getBoundingBox().inflate(RADIUS))) {
            if (target instanceof Player || caster.distanceTo(target) > RADIUS)
                continue;

            target.addEffect(new MobEffectInstance(
                    EffectRegistry.CONFUSION.get(),
                    getDuration(spellLevel, caster),
                    0, false, false, false
            ));
        }
    }
}
