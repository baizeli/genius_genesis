package miku.united_as_one.genesis.spell.chaos;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.*;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.registries.SpellSchoolRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
    public CastResult canBeCastedBy(int spellLevel, CastSource castSource, MagicData playerMagicData, Player player) {
        if (castSource != CastSource.SCROLL)
            return new CastResult(CastResult.Type.FAILURE,
                    Component.translatable("ui.genius_genesis.cast_error_non_scroll", this.getDisplayName(player)).withStyle(ChatFormatting.RED));
        return super.canBeCastedBy(spellLevel, castSource, playerMagicData, player);
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (entity instanceof ServerPlayer serverPlayer) {
            MagicData casterMagicData = MagicData.getPlayerMagicData(serverPlayer);

            // 回转全部法术
            casterMagicData.getPlayerCooldowns().clearCooldowns();
            casterMagicData.getPlayerCooldowns().syncToPlayer(serverPlayer);

            // 回满法力值
            casterMagicData.setMana(Float.POSITIVE_INFINITY);

            // 设置血量到最大生命值的5%
            serverPlayer.setHealth((float) (serverPlayer.getMaxHealth() * 0.05));
        }

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }
}