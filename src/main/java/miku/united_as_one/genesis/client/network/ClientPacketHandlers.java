package miku.united_as_one.genesis.client.network;

import miku.bai_ze_li.genesis.api.render.effect.SlashEffectAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class ClientPacketHandlers {
    private ClientPacketHandlers() {
    }

    public static void handleSlashPacket(int attackerId, int targetId) {
        handleSlashPacket(attackerId, targetId, 0xFF4AA6FF);
    }

    public static void handleSlashPacket(int attackerId, int targetId, int color) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }

        Entity attacker = minecraft.level.getEntity(attackerId);
        Entity target = minecraft.level.getEntity(targetId);
        if (attacker instanceof LivingEntity livingAttacker && target != null) {
            SlashEffectAPI.spawnOnEntity(livingAttacker, target, color);
        }
    }
}
