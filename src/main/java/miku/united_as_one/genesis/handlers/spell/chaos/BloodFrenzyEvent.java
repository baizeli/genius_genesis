package miku.united_as_one.genesis.handlers.spell.chaos;

import miku.united_as_one.genesis.registries.EffectRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber
public class BloodFrenzyEvent {

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity.hasEffect(EffectRegistry.BLOOD_FRENZY.get())) {
            if (entity instanceof Player player) {
                // 获取施法者攻击范围
                List<LivingEntity> nearbyEntities = player.level().getEntitiesOfClass(
                    LivingEntity.class, player.getBoundingBox().inflate(player.getEntityReach()),
                    e -> e != player && e.isAlive() && player.canReach(e, 0)
                );

                // 如果附近有实体/找到最近的实体
                if (!nearbyEntities.isEmpty()) {
                    LivingEntity nearestEntity = null;
                    double nearestDistance = Double.MAX_VALUE;

                    for (LivingEntity nearbyEntity : nearbyEntities) {
                        double distance = player.distanceToSqr(nearbyEntity);
                        if (distance < nearestDistance) {
                            nearestDistance = distance;
                            nearestEntity = nearbyEntity;
                        }
                    }

                    // 将施法者推向最近的实体
                    if (nearestEntity != null) {
                        double dx = nearestEntity.getX() - player.getX();
                        double dz = nearestEntity.getZ() - player.getZ();
                        double distance = Math.sqrt(dx * dx + dz * dz);

                        if (distance > 0) {
                            double force = 0.1D;
                            player.push(dx / distance * force, 0, dz / distance * force);
                        }
                    }

                    // 自动攻击
                    if (player.level().isClientSide) {
                        Minecraft mc = Minecraft.getInstance();

                        if (player.getAttackStrengthScale(0) >= 1) {
                            for (LivingEntity target : nearbyEntities) {
                                if (player.canReach(target, 0)) {
                                    if (mc.gameMode != null) {
                                        mc.gameMode.attack(player, target);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        // 攻击的伤害增加
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            if (attacker.hasEffect(EffectRegistry.BLOOD_FRENZY.get())) {
                event.setAmount(event.getAmount() * 2);
            }
        }

        // 受伤的减免
        if (event.getEntity() instanceof LivingEntity) {
            if (event.getEntity().hasEffect(EffectRegistry.BLOOD_FRENZY.get())) {
                event.setAmount(event.getAmount() * 0.5f);
            }
        }
    }
}