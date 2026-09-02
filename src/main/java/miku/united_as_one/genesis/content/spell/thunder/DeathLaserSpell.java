package miku.united_as_one.genesis.content.spell.thunder;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.*;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.registries.EntityRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

@AutoSpellConfig
public class DeathLaserSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(Genesis.MOD_ID, "death_laser");
    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(SchoolRegistry.LIGHTNING_RESOURCE)
            .setMaxLevel(3)
            .setCooldownSeconds(16)
            .build();

    public DeathLaserSpell() {
        this.manaCostPerLevel = 25;
        this.baseSpellPower = 5;
        this.spellPowerPerLevel = 2;
        this.castTime = 20 * 4;
        this.baseManaCost = 50;
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable(
                        "ui.irons_spellbooks.damage", Utils.stringTruncation(getDamage(spellLevel, caster), 1)
                ),
                Component.translatable("ui.irons_spellbooks.radius", getLaserRadius()),
                Component.translatable(
                        "ui.irons_spellbooks.duration", Utils.timeFromTicks(getDurationTicks(), 1)
                )
        );
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
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.CHARGE_ANIMATION;
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return SpellAnimations.FINISH_ANIMATION;
    }

    private float getDamage(int spellLevel, LivingEntity caster) {
        return getSpellPower(spellLevel, caster);
    }

    private int getDurationTicks() {
        return 20 * 4;
    }

    private int getLaserRadius() {
        return 20;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!level.isClientSide()) {
            var deathLaser = EntityRegistry.DEATH_LASER.get().create(level);

            if (deathLaser != null) {
                deathLaser.setCaster(entity);
                deathLaser.setDuration(getDurationTicks());
                deathLaser.setFollowPlayer(true);
                deathLaser.setCustomDamage(getDamage(spellLevel, entity));
                deathLaser.setLaserLength(getLaserRadius());
                /*deathLaser.setLaserRadius(20);*/
                deathLaser.setRenderStart(true);
                deathLaser.setRenderEnd(true);
                level.addFreshEntity(deathLaser);
            }
        }

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }
}