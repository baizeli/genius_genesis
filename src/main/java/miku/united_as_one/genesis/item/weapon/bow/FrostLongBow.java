package miku.united_as_one.genesis.item.weapon.bow;

import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.spells.ice_block.IceBlockProjectile;
import io.redspace.ironsspellbooks.entity.spells.ray_of_frost.RayOfFrostVisualEntity;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;

public class FrostLongBow extends SpellBowItem {
    public FrostLongBow(Properties properties) {
        super(properties, "frost_longbow", 3);
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

        HitResult hitResult = Utils.raycastForEntity(level, player, 100, true, 0.15F);
        level.addFreshEntity(new RayOfFrostVisualEntity(level, player.getEyePosition(), hitResult.getLocation(), player));

        IceBlockProjectile iceBlock = new IceBlockProjectile(level, player, null);
        iceBlock.moveTo(hitResult.getLocation().x, hitResult.getLocation().y + 5, hitResult.getLocation().z);
        iceBlock.setOwner(player);
        iceBlock.setDamage(power * 8);
        iceBlock.setAirTime(10);

        if (hitResult.getType() == HitResult.Type.ENTITY) {
            DamageSources.applyDamage(((EntityHitResult) hitResult).getEntity(), power * 15,
                    SpellDamageSource.source(player, SpellRegistry.RAY_OF_FROST_SPELL.get()).setFreezeTicks((int) (power * 5 * 20)));
            level.addFreshEntity(iceBlock);
        } else if (hitResult.getType() == HitResult.Type.BLOCK) {
            MagicManager.spawnParticles(level, ParticleHelper.ICY_FOG,
                    hitResult.getLocation().x, hitResult.getLocation().y, hitResult.getLocation().z,
                    4, 0, 0, 0, 0.3, true);
        }

        MagicManager.spawnParticles(level, ParticleHelper.SNOWFLAKE,
                hitResult.getLocation().x, hitResult.getLocation().y, hitResult.getLocation().z,
                50, 0, 0, 0, 0.3, false);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundRegistry.RAY_OF_FROST.get(), player.getSoundSource(),
                1, level.random.nextFloat() + 1.3F);

        if (!player.getAbilities().instabuild) {
            stack.hurtAndBreak(10, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
            MagicData magicData = MagicData.getPlayerMagicData(player);
            SpellOnCastEvent event = new SpellOnCastEvent(player, "ray_of_frost", 1, 50, SchoolRegistry.ICE.get(), CastSource.SWORD);
            MinecraftForge.EVENT_BUS.post(event);
            magicData.setMana(Math.max(magicData.getMana() - event.getManaCost(), 0));
            PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncManaPacket(magicData));
        }
    }
}
