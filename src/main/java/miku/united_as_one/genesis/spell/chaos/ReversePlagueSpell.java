package miku.united_as_one.genesis.spell.chaos;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.registries.SpellSchoolRegistry;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@AutoSpellConfig
@Mod.EventBusSubscriber(modid = Genesis.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ReversePlagueSpell extends ChaosBaseSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(Genesis.MOD_ID, "reverse_plague");
    private final DefaultConfig defaultConfig;
    public static Map<UUID, UUID> entityMap = new HashMap<>();

    public ReversePlagueSpell() {
        this.defaultConfig = new DefaultConfig()
                .setMinRarity(SpellRarity.LEGENDARY)
                .setSchoolResource(SpellSchoolRegistry.CHAOS_RESOURCE)
                .setMaxLevel(1)
                .setCooldownSeconds(90F)
                .build();
        this.manaCostPerLevel = 0;
        this.baseSpellPower = 0;
        this.spellPowerPerLevel = 0;
        this.castTime = 0;
        this.baseManaCost = 10;
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of();
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
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        return Utils.preCastTargetHelper(level, entity, playerMagicData, this, 100, 0.35F);
    }

    @Override
    public void castSpell(Level world, int spellLevel, ServerPlayer serverPlayer, CastSource castSource, boolean triggerCooldown) {
        super.castSpell(world, spellLevel, serverPlayer, castSource, triggerCooldown);
    }

    @Override
    public void onServerCastComplete(Level serverLevel, int spellLevel, LivingEntity livingEntity, MagicData playerMagicData, boolean cancelled) {
        Entity entity = serverLevel.getEntities().get(((TargetEntityCastData) Objects.requireNonNull(playerMagicData.getAdditionalCastData())).getTargetUUID());
        if (entity instanceof LivingEntity living) {
            for (MobEffectInstance effectInstance : living.getActiveEffects().stream().toList()) {
                if (effectInstance.getEffect().getCategory() == MobEffectCategory.BENEFICIAL) {
                    effectInstance.duration = Math.min(effectInstance.getDuration(), 2400);
                    livingEntity.addEffect(effectInstance);
                    living.removeEffect(effectInstance.getEffect());
                }
            }
            for (MobEffectInstance effectInstance : livingEntity.getActiveEffects().stream().toList()) {
                if (effectInstance.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
                    living.addEffect(effectInstance);
                    livingEntity.removeEffect(effectInstance.getEffect());
                }
            }
            living.getPersistentData().putLong(Genesis.MOD_ID + "remaining_time", serverLevel.getGameTime() + 600);
            entityMap.put(livingEntity.getUUID(), living.getUUID());
        }
        super.onServerCastComplete(serverLevel, spellLevel, livingEntity, playerMagicData, cancelled);
    }
}
