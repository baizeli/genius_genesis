package miku.united_as_one.genesis.handlers.armor;

import io.redspace.ironsspellbooks.api.events.SpellCooldownAddedEvent;
import io.redspace.ironsspellbooks.api.events.SpellDamageEvent;
import io.redspace.ironsspellbooks.api.events.ChangeManaEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.network.casting.SyncCooldownPacket;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.item.GenesisArmorMaterials;
import miku.united_as_one.genesis.util.ArmorSetUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Genesis.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class GenesisArmorEvents {
    private static final float MANA_PER_DURABILITY = 10.0F;
    private static final Map<UUID, Float> MANA_REPAIR_PROGRESS = new HashMap<>();
    private static boolean processingReactiveDamage;

    private GenesisArmorEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }

        Player player = event.player;
        applyNightVision(player);
    }

    @SubscribeEvent
    public static void onSpellDamage(SpellDamageEvent event) {
        LivingEntity target = event.getEntity();
        if (ArmorSetUtil.hasFullSet(target, GenesisArmorMaterials.VIOLET_ZENITH)) {
            event.setAmount(event.getAmount() * 0.5F);
            return;
        }

        if (!ArmorSetUtil.hasFullSet(target, GenesisArmorMaterials.DIVINE_METAL)) {
            return;
        }

        if (event.getSpellDamageSource().spell().getSchoolType() == SchoolRegistry.HOLY.get()) {
            event.setAmount(event.getAmount() * 0.1F);
        } else if (event.getSpellDamageSource().spell().getSchoolType() == SchoolRegistry.BLOOD.get()) {
            event.setAmount(event.getAmount() * 0.5F);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();

        if (ArmorSetUtil.hasFullSet(target, GenesisArmorMaterials.CELESTIAL_SOURCE_SPELL)) {
            float maxDamage = target.getMaxHealth() * 0.7F;
            if (event.getAmount() > maxDamage) {
                event.setAmount(maxDamage);
            }
        }

        if (processingReactiveDamage || target.level().isClientSide) {
            return;
        }

        if (ArmorSetUtil.hasFullSet(target, GenesisArmorMaterials.DIVINE_METAL)) {
            reflectUndeadDamage(event, target);
        }

        if (ArmorSetUtil.hasFullSet(target, GenesisArmorMaterials.VIOLET_ZENITH)) {
            reflectVioletWave(event, target);
        }
    }

    @SubscribeEvent
    public static void onLivingKnockBack(LivingKnockBackEvent event) {
        LivingEntity entity = event.getEntity();
        if (ArmorSetUtil.hasFullSet(entity, GenesisArmorMaterials.DIVINE_METAL)
                || ArmorSetUtil.hasFullSet(entity, GenesisArmorMaterials.VIOLET_ZENITH)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onSpellCooldownAdded(SpellCooldownAddedEvent.Pre event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || !ArmorSetUtil.hasFullSet(player, GenesisArmorMaterials.VIOLET_ZENITH)
                || player.getRandom().nextFloat() >= 0.3F) {
            return;
        }

        event.setEffectiveCooldown(0);
        MagicData.getPlayerMagicData(player).getPlayerCooldowns().removeCooldown(event.getSpell().getSpellId());
        PacketDistributor.sendToPlayer(player, new SyncCooldownPacket(event.getSpell().getSpellId(), 0));
    }

    @SubscribeEvent
    public static void onChangeMana(ChangeManaEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || player.level().isClientSide) {
            return;
        }

        float gainedMana = event.getNewMana() - event.getOldMana();
        if (gainedMana <= 0.0F) {
            return;
        }

        float consumedMana = repairGenesisArmorFromMana(player, gainedMana);
        if (consumedMana > 0.0F) {
            event.setNewMana(Math.max(event.getOldMana(), event.getNewMana() - consumedMana));
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        MANA_REPAIR_PROGRESS.remove(event.getEntity().getUUID());
    }

    private static void applyNightVision(Player player) {
        if (ArmorSetUtil.isWearing(player, GenesisArmorMaterials.DIVINE_METAL, EquipmentSlot.HEAD)
                || ArmorSetUtil.isWearing(player, GenesisArmorMaterials.VIOLET_ZENITH, EquipmentSlot.HEAD)
                || ArmorSetUtil.isWearing(player, GenesisArmorMaterials.CHAOS_SPELL, EquipmentSlot.HEAD)) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 240, 0, false, false));
        }
    }

    private static float repairGenesisArmorFromMana(ServerPlayer player, float gainedMana) {
        List<ItemStack> damagedArmor = new ArrayList<>();
        int totalDamage = 0;
        for (ItemStack stack : player.getInventory().armor) {
            if (ArmorSetUtil.isGenesisArmor(stack) && stack.getDamageValue() > 0) {
                damagedArmor.add(stack);
                totalDamage += stack.getDamageValue();
            }
        }

        if (damagedArmor.isEmpty()) {
            MANA_REPAIR_PROGRESS.remove(player.getUUID());
            return 0.0F;
        }

        UUID playerId = player.getUUID();
        float storedMana = MANA_REPAIR_PROGRESS.getOrDefault(playerId, 0.0F);
        float neededMana = Math.max(0.0F, totalDamage * MANA_PER_DURABILITY - storedMana);
        float consumedMana = Math.min(gainedMana, neededMana);
        if (consumedMana <= 0.0F) {
            return 0.0F;
        }

        storedMana += consumedMana;
        while (storedMana >= MANA_PER_DURABILITY && !damagedArmor.isEmpty()) {
            ItemStack stack = damagedArmor.get(player.getRandom().nextInt(damagedArmor.size()));
            stack.setDamageValue(stack.getDamageValue() - 1);
            storedMana -= MANA_PER_DURABILITY;
            if (stack.getDamageValue() <= 0) {
                damagedArmor.remove(stack);
            }
        }

        if (damagedArmor.isEmpty()) {
            MANA_REPAIR_PROGRESS.remove(playerId);
        } else {
            MANA_REPAIR_PROGRESS.put(playerId, storedMana);
        }
        return consumedMana;
    }

    private static void reflectUndeadDamage(LivingHurtEvent event, LivingEntity target) {
        Entity attacker = event.getSource().getEntity();
        if (!(attacker instanceof LivingEntity livingAttacker)
                || livingAttacker.getMobType() != MobType.UNDEAD
                || !livingAttacker.isAlive()) {
            return;
        }

        processingReactiveDamage = true;
        try {
            livingAttacker.addEffect(new MobEffectInstance(MobEffectRegistry.GUIDING_BOLT.get(), 100, 0));
            livingAttacker.hurt(DamageSources.get(
                    target.level(),
                    SchoolRegistry.HOLY.get().getDamageType()
            ), event.getAmount());
        } finally {
            processingReactiveDamage = false;
        }
    }

    private static void reflectVioletWave(LivingHurtEvent event, LivingEntity target) {
        if (!(target.level() instanceof ServerLevel level)) {
            return;
        }

        spawnVioletWave(level, target);

        processingReactiveDamage = true;
        try {
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(3.0D))) {
                if (entity == target || !entity.isAlive()) {
                    continue;
                }
                entity.hurt(target.damageSources().magic(), event.getAmount());
            }
        } finally {
            processingReactiveDamage = false;
        }
    }

    private static void spawnVioletWave(ServerLevel level, LivingEntity center) {
        double x = center.getX();
        double y = center.getBoundingBox().getCenter().y;
        double z = center.getZ();

        MagicManager.spawnParticles(level, ParticleHelper.UNSTABLE_ENDER, x, y, z,
                25, 0.0D, 0.0D, 0.0D, 0.18D, false);
        MagicManager.spawnParticles(level, new BlastwaveParticleOptions(
                        SpellRegistry.ECHOING_STRIKES_SPELL.get().getSchoolType().getTargetingColor(),
                        2.7F
                ), x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D, true);
    }
}
