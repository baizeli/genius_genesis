package miku.united_as_one.genesis.utils;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import miku.united_as_one.genesis.Genesis;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class GenesisEntityUtil {
    public static final String DAMAGE_CLEANER = Genesis.MOD_ID + ":damage_cleaner";
    public static final String SPELL_POWER_CLEANER = Genesis.MOD_ID + ":spell_power_cleaner";

    public static final UUID DAMAGE_CLEANER_UUID = UUID.fromString("093bc6f5-60ed-4c0e-9ffb-dcc654248270");
    public static final UUID SPELL_POWER_CLEANER_UUID = UUID.fromString("cd0e2cd9-2ba1-4bcb-ae3d-d7690bf3e495");

    public static void applyWeakness(LivingEntity living) {
        // 应用虚弱效果
        AttributeInstance attackDamageInstance = living.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance spellPowerInstance = living.getAttribute(AttributeRegistry.SPELL_POWER.get());

        if (attackDamageInstance == null || spellPowerInstance == null)
            return;

        // 清空原有修饰符
        attackDamageInstance.removeModifiers();
        spellPowerInstance.removeModifiers();

        // 将攻击力设为0
        attackDamageInstance.addPermanentModifier(
                new AttributeModifier(DAMAGE_CLEANER_UUID, DAMAGE_CLEANER,
                        -1, AttributeModifier.Operation.MULTIPLY_TOTAL)
        );

        // 将法术能效设为0
        spellPowerInstance.addPermanentModifier(
                new AttributeModifier(SPELL_POWER_CLEANER_UUID, SPELL_POWER_CLEANER,
                        -1, AttributeModifier.Operation.MULTIPLY_TOTAL)
        );
    }

    public static void removeWeakness(LivingEntity living) {
        AttributeInstance attackDamageInstance = living.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance spellPowerInstance = living.getAttribute(AttributeRegistry.SPELL_POWER.get());

        if (attackDamageInstance == null || spellPowerInstance == null)
            return;

        // 移除用于虚弱的属性修饰符
        attackDamageInstance.removeModifier(DAMAGE_CLEANER_UUID);
        spellPowerInstance.removeModifier(SPELL_POWER_CLEANER_UUID);
    }

    public record TeleportTarget(
            ServerLevel level,
            BlockPos anchorPos,   // 床/锚/世界出生点的方块坐标
            Vec3 position,        // 实际可站立位置
            float yaw,
            boolean forced
    ) {}

    public static TeleportTarget resolveSafeSpawn(ServerPlayer player, boolean consumeAnchorCharge) {
        // 尝试获取玩家重生点
        BlockPos respawnPos = player.getRespawnPosition();
        float respawnAngle = player.getRespawnAngle();
        boolean forced = player.isRespawnForced();
        ResourceKey<Level> dimension = player.getRespawnDimension();

        // 回退到世界出生点
        ServerLevel targetLevel = player.server.getLevel(dimension);
        if (targetLevel == null || respawnPos == null) {
            targetLevel = player.server.overworld();
            respawnPos = targetLevel.getSharedSpawnPos();
            respawnAngle = targetLevel.getSharedSpawnAngle();
            forced = false; // 回退后不再是强制重生
        }

        // 计算可站立位置
        Optional<Vec3> pos = Player.findRespawnPositionAndUseSpawnBlock(
                targetLevel, respawnPos, respawnAngle, forced, consumeAnchorCharge);

        if (pos.isEmpty()) {
            targetLevel = player.server.overworld();
            respawnPos = targetLevel.getSharedSpawnPos();
            respawnAngle = targetLevel.getSharedSpawnAngle();
            forced = false;
            pos = Optional.of(Vec3.atBottomCenterOf(respawnPos));
        }

        return new TeleportTarget(targetLevel, respawnPos, pos.get(), respawnAngle, forced);
    }

    public static Entity teleportEntityTo(Entity entity, TeleportTarget target, float finalYaw) {
        if (entity.level().dimension() != target.level().dimension()) {
            return entity.changeDimension(target.level(), new ITeleporter() {
                @Override
                public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld,
                                          float yaw, Function<Boolean, Entity> repositionEntity) {
                    Entity moved = repositionEntity.apply(false);
                    moved.moveTo(target.position().x, target.position().y, target.position().z, finalYaw, 0.0F);
                    return moved;
                }
            });
        } else {
            entity.teleportTo(target.position().x, target.position().y, target.position().z);
            entity.setYRot(finalYaw);
            entity.setXRot(0.0F);
            return entity;
        }
    }
}
