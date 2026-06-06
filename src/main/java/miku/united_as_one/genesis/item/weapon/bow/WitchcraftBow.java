package miku.united_as_one.genesis.item.weapon.bow;

import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.eldritch_blast.EldritchBlastVisualEntity;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import io.redspace.ironsspellbooks.spells.eldritch.EldritchBlastSpell;
import java.util.function.Predicate;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;

public class WitchcraftBow extends BowItem {
    public WitchcraftBow(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull Predicate<ItemStack> getAllSupportedProjectiles() {
        return stack -> stack.getItem() instanceof ArrowItem;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment == Enchantments.POWER_ARROWS;
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

        float range = 22;
        Vec3 start = player.getEyePosition();
        Vec3 end = start.add(player.getForward().scale(range));

        for (Entity target : level.getEntities(player, player.getBoundingBox().expandTowards(end.subtract(start)))) {
            if (Utils.checkEntityIntersecting(target, start, end, 0.4F).getType() != HitResult.Type.MISS) {
                target.invulnerableTime = 0;
                DamageSources.applyDamage(target, power * 16, level.damageSources().sonicBoom(player));

                Vec3 targetPos = target.position();
                Vec3 targetUp = new Vec3(0, 5, 0);
                Vec3 perpendicularRight = targetPos.subtract(player.position()).normalize().cross(new Vec3(0, 1, 0)).normalize();
                Vec3 leftStart = targetPos.add(targetUp).subtract(perpendicularRight.scale(2));
                Vec3 rightStart = targetPos.add(targetUp).add(perpendicularRight.scale(2));
                Vec3 down = targetPos.add(0, -5, 0);

                addEldritchVisual(level, leftStart, down, player);
                target.invulnerableTime = 0;
                DamageSources.applyDamage(target, power * 8, new EldritchBlastSpell().getDamageSource(target, player));

                addEldritchVisual(level, rightStart, down, player);
                target.invulnerableTime = 0;
                DamageSources.applyDamage(target, power * 8, new EldritchBlastSpell().getDamageSource(target, player));
            }
        }

        Vec3 beamStart = start.add(0, -0.5, 0);
        Vec3 beamEnd = beamStart.add(player.getForward().scale(range));
        level.addFreshEntity(new EldritchBlastVisualEntity(level, beamStart, beamEnd, player));

        for (Entity target : level.getEntities(player, player.getBoundingBox().expandTowards(beamEnd.subtract(beamStart)))) {
            if (Utils.checkEntityIntersecting(target, beamStart, beamEnd, 0.4F).getType() != HitResult.Type.MISS) {
                target.invulnerableTime = 0;
                DamageSources.applyDamage(target, power * 8, new EldritchBlastSpell().getDamageSource(target, player));
            }
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.WARDEN_SONIC_BOOM, player.getSoundSource(), 3.5F, 0.9F + level.random.nextFloat() * 0.2F);

        for (int i = 0; i < range; i++) {
            Vec3 pos = player.getLookAngle().normalize().scale(i).add(player.getEyePosition());
            MagicManager.spawnParticles(level, ParticleTypes.SONIC_BOOM, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0, false);
        }

        CameraShakeManager.addCameraShake(new CameraShakeData(30, player.position(), range));

        if (!player.getAbilities().instabuild) {
            stack.hurtAndBreak(100, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
            MagicData magicData = MagicData.getPlayerMagicData(player);
            SpellOnCastEvent event = new SpellOnCastEvent(player, "sonic_boom", 1, 100, SchoolRegistry.ELDRITCH.get(), CastSource.SWORD);
            MinecraftForge.EVENT_BUS.post(event);
            magicData.setMana(Math.max(magicData.getMana() - event.getManaCost(), 0));
            PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncManaPacket(magicData));
        }
    }

    private static void addEldritchVisual(Level level, Vec3 start, Vec3 end, Player player) {
        EldritchBlastVisualEntity visual = new EldritchBlastVisualEntity(level, start, end, player);
        Vec3 direction = end.subtract(start);
        visual.setYRot((float) (Math.atan2(direction.z, direction.x) * 180 / Math.PI) - 90);
        visual.setXRot((float) (Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)) * -160 / Math.PI));
        level.addFreshEntity(visual);
    }
}
