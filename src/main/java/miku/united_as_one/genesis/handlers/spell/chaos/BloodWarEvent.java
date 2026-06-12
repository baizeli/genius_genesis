package miku.united_as_one.genesis.handlers.spell.chaos;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import miku.united_as_one.genesis.registries.EffectRegistry;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber
public class BloodWarEvent {
    private static final Map<Player, Map<UUID, AttributeModifier>> playerModifiers = new HashMap<>();

    public static final double SPELL_POWER_BONUS_PER_THRESHOLD = 0.03; // 法术强度
    public static final double DAMAGE_BONUS_PER_THRESHOLD = 0.05; // 伤害
    public static final double SPEED_BONUS_PER_THRESHOLD = 0.05; // 移速

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.hasEffect(EffectRegistry.BLOOD_WAR.get())) {
                float currentHealth = player.getHealth();
                float maxHealth = player.getMaxHealth();

                // 计算血量变化
                int oldThreshold = (int) (currentHealth / maxHealth * 10);
                float newHealth = currentHealth - event.getAmount();
                if (newHealth < 0) newHealth = 0;
                int newThreshold = (int) (newHealth / maxHealth * 10);

                // 计算每次损失的血量
                if (oldThreshold > newThreshold) {
                    int thresholdsCrossed = oldThreshold - newThreshold;

                    double spellPowerBonus = thresholdsCrossed * SPELL_POWER_BONUS_PER_THRESHOLD; // 法术强度
                    double damageBonus = thresholdsCrossed * DAMAGE_BONUS_PER_THRESHOLD; // 伤害
                    double speedBonus = thresholdsCrossed * SPEED_BONUS_PER_THRESHOLD; // 移速

                    // 清除旧属性修饰符
                    Map<UUID, AttributeModifier> oldModifiers = playerModifiers.get(player);

                    if (oldModifiers != null) {
                        for (UUID modifierId : oldModifiers.keySet()) {
                            AttributeInstance spellPowerInstance = player.getAttribute(AttributeRegistry.SPELL_POWER.get());
                            if (spellPowerInstance != null) {
                                spellPowerInstance.removeModifier(modifierId);
                            }

                            AttributeInstance damageInstance = player.getAttribute(Attributes.ATTACK_DAMAGE);
                            if (damageInstance != null) {
                                damageInstance.removeModifier(modifierId);
                            }

                            AttributeInstance speedInstance = player.getAttribute(Attributes.MOVEMENT_SPEED);
                            if (speedInstance != null) {
                                speedInstance.removeModifier(modifierId);
                            }
                        }

                        oldModifiers.clear();
                    }

                    // 应用新属性修饰符
                    Map<UUID, AttributeModifier> modifiers = new HashMap<>();

                    // 法术强度
                    if (spellPowerBonus > 0) {
                        AttributeModifier spellPowerModifier = new AttributeModifier(
                            UUID.randomUUID(),
                            "Blood War Spell Power",
                            spellPowerBonus,
                            AttributeModifier.Operation.MULTIPLY_BASE
                        );

                        AttributeInstance spellPowerInstance = player.getAttribute(AttributeRegistry.SPELL_POWER.get());
                        if (spellPowerInstance != null) {
                            spellPowerInstance.addTransientModifier(spellPowerModifier);
                            modifiers.put(spellPowerModifier.getId(), spellPowerModifier);
                        }
                    }

                    // 伤害
                    if (damageBonus > 0) {
                        AttributeModifier damageModifier = new AttributeModifier(
                            UUID.randomUUID(),
                            "Blood War Damage",
                            damageBonus,
                            AttributeModifier.Operation.MULTIPLY_BASE
                        );

                        AttributeInstance damageInstance = player.getAttribute(Attributes.ATTACK_DAMAGE);
                        if (damageInstance != null) {
                            damageInstance.addTransientModifier(damageModifier);

                            modifiers.put(damageModifier.getId(), damageModifier);
                        }
                    }

                    // 移速
                    if (speedBonus > 0) {
                        AttributeModifier speedModifier = new AttributeModifier(
                            UUID.randomUUID(),
                            "Blood War Speed",
                            speedBonus,
                            AttributeModifier.Operation.MULTIPLY_BASE
                        );

                        AttributeInstance speedInstance = player.getAttribute(Attributes.MOVEMENT_SPEED);
                        if (speedInstance != null) {
                            speedInstance.addTransientModifier(speedModifier);
                            modifiers.put(speedModifier.getId(), speedModifier);
                        }
                    }

                    playerModifiers.put(player, modifiers);
                }
            }
        }
    }

    // 当buff结束时清除属性修饰符
    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Expired event) {
        if (event.getEffectInstance() != null &&
            event.getEffectInstance().getEffect() == EffectRegistry.BLOOD_WAR.get() &&
            event.getEntity() instanceof Player player
        ) {
            Map<UUID, AttributeModifier> modifiers = playerModifiers.get(player);

            if (modifiers != null) {
                for (UUID modifierId : modifiers.keySet()) {
                    AttributeInstance spellPowerInstance = player.getAttribute(AttributeRegistry.SPELL_POWER.get());
                    if (spellPowerInstance != null) {
                        spellPowerInstance.removeModifier(modifierId);
                    }

                    AttributeInstance damageInstance = player.getAttribute(Attributes.ATTACK_DAMAGE);
                    if (damageInstance != null) {
                        damageInstance.removeModifier(modifierId);
                    }

                    AttributeInstance speedInstance = player.getAttribute(Attributes.MOVEMENT_SPEED);
                    if (speedInstance != null) {
                        speedInstance.removeModifier(modifierId);
                    }
                }

                modifiers.clear();

                playerModifiers.remove(player);
            }
        }
    }
}