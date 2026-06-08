package miku.united_as_one.genesis.registries;

import miku.united_as_one.genesis.Genesis;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class GenesisParticles {
    private static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Genesis.MOD_ID);

    public static final RegistryObject<SimpleParticleType> GLOW_CUBE =
            PARTICLE_TYPES.register("glow_cube", () -> new SimpleParticleType(false));

    private GenesisParticles() {
    }

    public static void register(IEventBus modBus) {
        PARTICLE_TYPES.register(modBus);
    }
}
