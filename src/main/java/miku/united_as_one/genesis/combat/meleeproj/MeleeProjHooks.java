package miku.united_as_one.genesis.combat.meleeproj;

import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import java.util.List;
import miku.united_as_one.genesis.content.entity.effect.MeleeDamageTextEntity;
import miku.united_as_one.genesis.registries.EntityRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class MeleeProjHooks {
    private MeleeProjHooks() {
    }

    public static void shakeInRadius(Vec3 center, double radius, float intensity, int durationTicks, ShakeType type,
                                     List<? extends Player> players) {
        if (center == null || radius <= 0.0D || intensity <= 0.0F || durationTicks <= 0) {
            return;
        }
        CameraShakeManager.addCameraShake(new CameraShakeData(durationTicks, center, (float) radius));
    }

    public static void spawnDamageText(LivingEntity target, float actualDamage) {
        if (target == null || actualDamage <= 0.0F || !(target.level() instanceof ServerLevel level)) {
            return;
        }
        MeleeDamageTextEntity text = new MeleeDamageTextEntity(
                EntityRegistry.MELEE_DAMAGE_TEXT.get(),
                level,
                actualDamage
        );
        double offsetX = (target.getRandom().nextDouble() - 0.5D) * Math.max(0.35D, target.getBbWidth());
        double offsetZ = (target.getRandom().nextDouble() - 0.5D) * Math.max(0.35D, target.getBbWidth());
        text.moveTo(
                target.getX() + offsetX,
                target.getY() + target.getBbHeight() + 0.35D,
                target.getZ() + offsetZ,
                target.getYRot(),
                0.0F
        );
        level.addFreshEntity(text);
    }

    public enum ShakeType {
        RANDOM,
        HORIZONTAL,
        VERTICAL,
        CIRCULAR,
        EXPLOSION,
        PULSE,
        WAVE
    }
}
