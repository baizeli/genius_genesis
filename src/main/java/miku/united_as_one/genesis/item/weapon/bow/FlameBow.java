package miku.united_as_one.genesis.item.weapon.bow;

import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.entity.spells.fiery_dagger.FieryDaggerEntity;
import io.redspace.ironsspellbooks.entity.spells.fire_arrow.FireArrowProjectile;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;

public class FlameBow extends SpellBowItem {
    public FlameBow(Properties properties) {
        super(properties, "flame_bow", 3);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entityLiving, int timeLeft) {
        if (!(entityLiving instanceof Player player) || level.isClientSide) {
            return;
        }

        float power = getPowerForTime(getUseDuration(stack) - timeLeft);
        if (power < 0.5F) {
            return;
        }

        if (!player.isShiftKeyDown()) {
            FireArrowProjectile fireArrow = new FireArrowProjectile(level, player);
            fireArrow.setPos(player.position().add(0, player.getEyeHeight(), 0).add(player.getForward()));
            fireArrow.shoot(player.getLookAngle());
            fireArrow.setDamage(power * 25);
            fireArrow.setExplosionRadius(5);
            fireArrow.getPersistentData().putBoolean("FlameBow_Arrow", true);
            level.addFreshEntity(fireArrow);
        } else {
            FieryDaggerEntity fieryDagger = new FieryDaggerEntity(level);
            fieryDagger.setOwner(player);
            fieryDagger.setPos(player.position().add(0, player.getEyeHeight(), 0).add(player.getForward()));
            fieryDagger.shoot(player.getLookAngle());
            fieryDagger.setDamage(power * 8);
            fieryDagger.setExplosionRadius(5);
            fieryDagger.tickCount = 100;
            fieryDagger.delay = 10;
            level.addFreshEntity(fieryDagger);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.FIRECHARGE_USE, player.getSoundSource(),
                1, 1 / (level.random.nextFloat() * 0.4F + 1.2F) + power * 0.5F);

        if (!player.getAbilities().instabuild) {
            stack.hurtAndBreak(10, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
            MagicData magicData = MagicData.getPlayerMagicData(player);
            SpellOnCastEvent event = new SpellOnCastEvent(player, "fire_arrow", 1, 50, SchoolRegistry.FIRE.get(), CastSource.SWORD);
            MinecraftForge.EVENT_BUS.post(event);
            magicData.setMana(Math.max(magicData.getMana() - event.getManaCost(), 0));
            PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncManaPacket(magicData));
        }
    }
}
