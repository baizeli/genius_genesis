package miku.united_as_one.genesis.item.weapon.bow;

import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.entity.spells.lightning_lance.LightningLanceProjectile;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;

public class ThunderLongBow extends SpellBowItem {
    public static final String PROJECTILE_MARKER = "GeniusGenesis_ThunderLongBow";

    public ThunderLongBow(Properties properties) {
        super(properties, "thunder_longbow", 3);
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

        LightningLanceProjectile projectile = new LightningLanceProjectile(level, player);
        projectile.setPos(player.position().add(0, player.getEyeHeight(), 0).add(player.getForward()));
        projectile.shoot(player.getLookAngle());
        projectile.setDamage(16 * (1 + stack.getEnchantmentLevel(Enchantments.POWER_ARROWS) * 0.05F) + power * 4);
        projectile.getPersistentData().putBoolean(PROJECTILE_MARKER, true);
        level.addFreshEntity(projectile);

        if (!player.getAbilities().instabuild) {
            stack.hurtAndBreak(10, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
            MagicData magicData = MagicData.getPlayerMagicData(player);
            SpellOnCastEvent event = new SpellOnCastEvent(player, "lightning_lance", 1, 20, SchoolRegistry.LIGHTNING.get(), CastSource.SWORD);
            MinecraftForge.EVENT_BUS.post(event);
            magicData.setMana(Math.max(magicData.getMana() - event.getManaCost(), 0));
            PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncManaPacket(magicData));
        }
    }
}
