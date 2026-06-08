package miku.united_as_one.genesis.handlers.curios;

import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.entity.spells.blood_slash.BloodSlashProjectile;
import io.redspace.ironsspellbooks.entity.spells.devour_jaw.DevourJaw;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import io.redspace.ironsspellbooks.spells.blood.DevourSpell;
import miku.bai_ze_li.genesis.api.curios.ModCurios;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.item.curios.EternalRing;
import miku.united_as_one.genesis.item.curios.RunePlusItem;
import miku.united_as_one.genesis.registries.ItemRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Genesis.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CurioEvents {
    public static final Map<UUID, Integer> BLOOD_STEP_USED = new HashMap<>();
    private static final Random RANDOM = new Random();
    private static final float LAO_WANG_DROP_CHANCE = 0.02F;

    private CurioEvents() {
    }

    @SubscribeEvent
    public static void onSpellCast(SpellOnCastEvent event) {
        Player player = event.getEntity();
        if (ModCurios.hasCurios(player, EternalRing::test)) {
            event.setSpellLevel(event.getSpellLevel() + 1);
        }
        if (ModCurios.hasCurios(player, RunePlusItem::isNature)
                && SpellRegistry.ACID_ORB_SPELL.get().getSpellId().equals(event.getSpellId())) {
            event.setManaCost(Mth.ceil((float) event.getManaCost() / 2.0F));
        }
        if (ModCurios.hasCurios(player, RunePlusItem::isBlood)
                && SpellRegistry.BLOOD_STEP_SPELL.get().getSpellId().equals(event.getSpellId())) {
            BLOOD_STEP_USED.put(player.getUUID(), event.getSpellLevel());
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        LivingEntity target = event.getEntity();
        DamageSource source = event.getSource();
        if (ModCurios.hasCurios(target, EternalRing::test)
                && (source.is(DamageTypeTags.IS_LIGHTNING)
                || source.is(DamageTypeTags.IS_FREEZING)
                || source.is(DamageTypeTags.IS_FIRE))) {
            EternalRing.clearElementalState(target);
            event.setCanceled(true);
        }

        if (source.getEntity() instanceof ServerPlayer player
                && ModCurios.hasCurios(player, RunePlusItem::isBlood)
                && source.getDirectEntity() instanceof BloodSlashProjectile) {
            MagicData data = MagicData.getPlayerMagicData(player);
            AttributeInstance maxMana = player.getAttribute(AttributeRegistry.MAX_MANA.get());
            if (maxMana != null) {
                data.addMana(event.getAmount());
                if (data.getMana() > maxMana.getValue()) {
                    data.setMana((float) maxMana.getValue());
                }
                PacketDistributor.sendToPlayer(player, new SyncManaPacket(data));
            }
        }

        if (source.getEntity() instanceof ServerPlayer player && BLOOD_STEP_USED.containsKey(player.getUUID())) {
            int spellLevel = BLOOD_STEP_USED.remove(player.getUUID());
            DevourJaw devourJaw = new DevourJaw(player.level(), player, target);
            devourJaw.vigorLevel = new DevourSpell().getHpBonus(spellLevel, player);
            devourJaw.setPos(target.position());
            devourJaw.setYRot(target.getYRot());
            devourJaw.setDamage(event.getAmount() * 0.5F);
            player.level().addFreshEntity(devourJaw);
        }
    }

    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        LivingEntity entity = event.getEntity();
        if (ModCurios.hasCurios(entity, EternalRing::test) && EternalRing.immuneEffect(event.getEffectInstance())) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        LivingEntity entity = event.getEntity();
        if (ModCurios.hasCurios(entity, EternalRing::test) && EternalRing.immuneEffect(event.getEffectInstance())) {
            entity.removeEffect(event.getEffectInstance().getEffect());
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (ModCurios.hasCurios(entity, EternalRing::test)) {
            EternalRing.clearElementalState(entity);
        }
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.getType() == EntityType.PIG && RANDOM.nextFloat() < LAO_WANG_DROP_CHANCE) {
            event.getDrops().add(new ItemEntity(
                    entity.level(),
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    new ItemStack(ItemRegistry.LAO_WANG_237.get())
            ));
        }

        if (event.getSource().getEntity() instanceof Player player && ModCurios.hasCurios(player, stack -> stack.is(ItemRegistry.LAO_WANG_237.get()))) {
            event.getDrops().add(new ItemEntity(
                    entity.level(),
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    new ItemStack(Items.PORKCHOP)
            ));
        }
    }
}
