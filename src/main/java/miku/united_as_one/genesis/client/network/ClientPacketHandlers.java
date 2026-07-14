package miku.united_as_one.genesis.client.network;

import miku.bai_ze_li.genesis.api.render.effect.slash.SlashEffectAPI;
import miku.united_as_one.genesis.client.render.player.PlayerShadowRenderer;
import miku.united_as_one.genesis.registries.GenesisParticles;
import miku.united_as_one.genesis.util.SlashColors;
import net.minecraft.client.Minecraft;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class ClientPacketHandlers {
    private static final int[] METEOR_CUBE_COLORS = {
            0xFF1E1E, 0xFF8C00, 0xFFE600, 0x20FF40, 0x00E5FF, 0x245CFF, 0xB020FF, 0xFF22C8
    };

    private ClientPacketHandlers() {
    }

    public static void handleSlashPacket(int attackerId, int targetId) {
        handleSlashPacket(attackerId, targetId, SlashColors.MITHRIL_LIGHT_BLUE);
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

    public static void handleMeteorGlowCubePacket(double x, double y, double z, int count, long seed) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || count <= 0) {
            return;
        }

        RandomSource random = RandomSource.create(seed);
        for (int i = 0; i < count; i++) {
            float[] color = colorFromInt(METEOR_CUBE_COLORS[random.nextInt(METEOR_CUBE_COLORS.length)]);
            minecraft.level.addParticle(GenesisParticles.GLOW_CUBE.get(),
                    x + (random.nextDouble() - 0.5D) * 0.65D,
                    y + random.nextDouble() * 0.5D,
                    z + (random.nextDouble() - 0.5D) * 0.65D,
                    color[0],
                    color[1],
                    color[2]);
        }
    }

    public static void handlePlayerShadowPacket(int entityId, int durationTicks) {
        PlayerShadowRenderer.updateClientState(entityId, durationTicks);
    }

    private static float[] colorFromInt(int color) {
        return new float[]{
                ((color >> 16) & 0xFF) / 255.0F,
                ((color >> 8) & 0xFF) / 255.0F,
                (color & 0xFF) / 255.0F
        };
    }
}
