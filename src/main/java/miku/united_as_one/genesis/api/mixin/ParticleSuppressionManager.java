package miku.united_as_one.genesis.api.mixin;

import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.particles.ParticleOptions;

public class ParticleSuppressionManager {
    private static final ThreadLocal<Boolean> suppressEmbers = ThreadLocal.withInitial(() -> false);

    public static void setSuppressEmbers(boolean value) {
        suppressEmbers.set(value);
    }

    public static boolean isSuppressingEmbers(ParticleOptions particle) {
        return suppressEmbers.get() && particle == ParticleHelper.EMBERS;
    }
}