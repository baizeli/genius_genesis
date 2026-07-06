package miku.united_as_one.genesis.client.render.slash;

import miku.united_as_one.genesis.util.SlashColors;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SlashEffectAPI {
    public static final int DEFAULT_COLOR = SlashColors.MITHRIL_LIGHT_BLUE;

    @OnlyIn(Dist.CLIENT)
    public static void spawnOnEntity(LivingEntity attacker, Entity target) {
        spawnOnEntity(attacker, target, DEFAULT_COLOR);
    }

    @OnlyIn(Dist.CLIENT)
    public static void spawnOnEntity(LivingEntity attacker, Entity target, int argbColor) {
        Vec3 targetCenter = new Vec3(
                target.getX(),
                target.getY() + target.getBbHeight() / 2.0F,
                target.getZ()
        );
        SlashEffectManager.add(new BaiZeLiSlashEffect(targetCenter, attacker.getYRot(), attacker.getXRot(), argbColor));
    }

    @OnlyIn(Dist.CLIENT)
    public static void spawnForward(LivingEntity player, double distance) {
        spawnForward(player, distance, DEFAULT_COLOR);
    }

    @OnlyIn(Dist.CLIENT)
    public static void spawnForward(LivingEntity player, double distance, int argbColor) {
        Vec3 center = player.getEyePosition().add(player.getLookAngle().scale(distance));
        SlashEffectManager.add(new BaiZeLiSlashEffect(center, player.getYRot(), player.getXRot(), argbColor));
    }
}
