package miku.united_as_one.genesis.data.damage;

import miku.united_as_one.genesis.Genesis;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

public final class DamageTypes {
    public static final ResourceKey<DamageType> CHAOS_MAGIC = create("chaos_magic");
    public static final ResourceKey<DamageType> CELESTIAL_SOURCE_MAGIC = create("celestial_source_magic");

    private DamageTypes() {
    }

    public static void bootstrap(BootstapContext<DamageType> context) {
        register(context, CHAOS_MAGIC);
        register(context, CELESTIAL_SOURCE_MAGIC);
    }

    private static ResourceKey<DamageType> create(String id) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, Genesis.rl(id));
    }

    private static void register(BootstapContext<DamageType> context, ResourceKey<DamageType> key) {
        context.register(key, new DamageType(key.location().getPath(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
    }
}
