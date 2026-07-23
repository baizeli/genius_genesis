package miku.united_as_one.genesis.spell.chaos;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.entity.spell.blood_boss.blood_dagger.BloodDaggerEntity;
import miku.united_as_one.genesis.registries.SpellSchoolRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

@AutoSpellConfig
public class GutrenderPunctureSpell extends ChaosBaseSpell {
    private final ResourceLocation spellId = Genesis.id("gutrender_puncture");
    private final DefaultConfig defaultConfig;

    public GutrenderPunctureSpell() {
        this.defaultConfig = new DefaultConfig()
                .setMinRarity(SpellRarity.COMMON)
                .setSchoolResource(SpellSchoolRegistry.CHAOS_RESOURCE)
                .setMaxLevel(3)
                .setCooldownSeconds(20.0F)
                .build();
        this.manaCostPerLevel = 50;
        this.baseSpellPower = 8;
        this.spellPowerPerLevel = 3;
        this.castTime = 0;
        this.baseManaCost = 50;
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(Component.translatable("ui.irons_spellbooks.damage", Utils.stringTruncation(getSpellPower(spellLevel, caster), 1)));
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
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity caster, MagicData playerMagicData) {
        return Utils.preCastTargetHelper(level, caster, playerMagicData, this, 50, 0.1F);
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
        if (!level.isClientSide && playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData castData) {
            LivingEntity target = castData.getTarget((ServerLevel) level);
            if (target != null) {
                caster.playSound(SoundRegistry.FIRE_CAST.get(), 2.0F, Utils.random.nextIntBetweenInclusive(80, 110) * 0.01F);
                spawnSwords(level, caster, target, getSpellPower(spellLevel, caster));
            }
        }
        super.onCast(level, spellLevel, caster, castSource, playerMagicData);
    }

    private void spawnSwords(Level level, LivingEntity caster, LivingEntity target, float damage) {
        Vec3 origin = caster.position();
        int count = 7;
        int delay = Utils.random.nextIntBetweenInclusive(30, 70);
        float yAngle = -Utils.getAngle(target.getX(), target.getZ(), caster.getX(), caster.getZ()) + Mth.HALF_PI;

        for (int i = 0; i < count; i++) {
            Vec3 offset = new Vec3(1.5F * caster.getScale(), 0.0D, 0.0D)
                    .zRot(Mth.lerp(i / (count - 1.0F), 0.0F, -Mth.PI))
                    .yRot(yAngle)
                    .add(0.0D, caster.getEyeHeight(), 0.0D);
            BloodDaggerEntity sword = new BloodDaggerEntity(level);
            sword.setOwner(caster);
            sword.ownerTrack = offset;
            sword.setTarget(target);
            sword.setPos(origin.add(offset.yRot(caster.getYRot())));
            sword.delay = delay + i * 2;
            sword.setDamage(damage);
            level.addFreshEntity(sword);
        }
    }
}
