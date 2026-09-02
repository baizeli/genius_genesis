package miku.united_as_one.genesis.client.fluid;

import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.content.fluid.FluidRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Genesis.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class FluidClientEvents {

    private FluidClientEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.level().isClientSide || !event.player.isAlive()) {
            return;
        }
        if (event.player.isEyeInFluidType(FluidRegistry.SOURCE_FLUID.getSource().getFluidType())) {
            spawnFluidParticle(event, ParticleTypes.BUBBLE);
        } else if (event.player.isEyeInFluidType(FluidRegistry.BLACKWATER_FLUID.getSource().getFluidType())) {
            spawnFluidParticle(event, ParticleTypes.SQUID_INK);
        } else if (event.player.isEyeInFluidType(FluidRegistry.BLOOD_FLUID.getSource().getFluidType())) {
            spawnFluidParticle(event, ParticleTypes.DAMAGE_INDICATOR);
        }
    }

    private static void spawnFluidParticle(TickEvent.PlayerTickEvent event, ParticleOptions particle) {
        Level level = event.player.level();
        if (level.random.nextInt(10) != 0) {
            return;
        }
        level.addParticle(particle,
                event.player.getX() + level.random.nextDouble() - 0.5D,
                event.player.getEyeY() + level.random.nextDouble() * 0.5D,
                event.player.getZ() + level.random.nextDouble() - 0.5D,
                0.0D, 0.0D, 0.0D);
    }
}
