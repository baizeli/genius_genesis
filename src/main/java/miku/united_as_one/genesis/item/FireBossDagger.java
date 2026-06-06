package miku.united_as_one.genesis.item;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.spells.fiery_dagger.FieryDaggerEntity;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class FireBossDagger extends SwordItem {
    public FireBossDagger(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        if (attacker.isShiftKeyDown() && attacker instanceof Player player && player.getAttackStrengthScale(0.0F) >= 1.0F) {
            target.playSound(SoundRegistry.FIRE_CAST.get(), 2.0F, Utils.random.nextIntBetweenInclusive(80, 110) * 0.01F);
            Vec3 pos = attacker.position();
            int count = 7;
            int delay = Utils.random.nextIntBetweenInclusive(30, 70);
            float yAngle = -Utils.getAngle(target.getX(), target.getZ(), attacker.getX(), attacker.getZ()) + ((float) Math.PI / 2.0F);

            for (int i = 0; i < count; i++) {
                Vec3 offset = new Vec3(1.5D * attacker.getScale(), 0.0D, 0.0D)
                        .zRot(Mth.lerp((float) i / ((float) count - 1.0F), 0.0F, -(float) Math.PI))
                        .yRot(yAngle)
                        .add(0.0D, attacker.getEyeHeight(), 0.0D);
                FieryDaggerEntity dagger = new FieryDaggerEntity(target.level());
                dagger.setOwner(attacker);
                dagger.ownerTrack = offset;
                dagger.setTarget(target);
                dagger.setPos(pos.add(offset.yRot(attacker.getYRot())));
                dagger.delay = delay + i * 2;
                dagger.setDamage(12.0F * 0.15F);
                attacker.level().addFreshEntity(dagger);
            }

            player.resetAttackStrengthTicker();
            return true;
        }

        target.setSecondsOnFire(5);
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.pass(stack);
        }

        if (!level.isClientSide) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, 4, false, false, true));
            player.getCooldowns().addCooldown(this, 240);
        }

        return InteractionResultHolder.success(stack);
    }
}
