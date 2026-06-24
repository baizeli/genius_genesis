package miku.united_as_one.genesis.handlers.spell.celestial_source;

import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.registries.EffectRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Abilities;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Genesis.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class IFlyEvent {
    private static final String GRANTED_FLIGHT_TAG = Genesis.MOD_ID + ".i_fly_granted_flight";

    private IFlyEvent() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) {
            return;
        }

        if (player.hasEffect(EffectRegistry.I_FLY.get())) {
            grantFlight(player);
        } else {
            revokeFlightIfGranted(player);
        }
    }

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        if (event.getEntity() instanceof ServerPlayer player
                && event.getEffectInstance() != null
                && event.getEffectInstance().getEffect() == EffectRegistry.I_FLY.get()) {
            grantFlight(player);
        }
    }

    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        if (event.getEntity() instanceof ServerPlayer player && event.getEffect() == EffectRegistry.I_FLY.get()) {
            revokeFlightIfGranted(player);
        }
    }

    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        if (event.getEntity() instanceof ServerPlayer player
                && event.getEffectInstance() != null
                && event.getEffectInstance().getEffect() == EffectRegistry.I_FLY.get()) {
            revokeFlightIfGranted(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            revokeFlightIfGranted(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            revokeFlightIfGranted(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && !player.hasEffect(EffectRegistry.I_FLY.get())) {
            revokeFlightIfGranted(player);
        }
    }

    private static void grantFlight(ServerPlayer player) {
        Abilities abilities = player.getAbilities();
        CompoundTag data = player.getPersistentData();
        boolean changed = false;

        if (!abilities.mayfly) {
            abilities.mayfly = true;
            data.putBoolean(GRANTED_FLIGHT_TAG, true);
            changed = true;
        }

        if (changed) {
            player.onUpdateAbilities();
        }
    }

    private static void revokeFlightIfGranted(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        if (!data.getBoolean(GRANTED_FLIGHT_TAG)) {
            return;
        }

        data.remove(GRANTED_FLIGHT_TAG);

        if (player.isCreative() || player.isSpectator()) {
            return;
        }

        Abilities abilities = player.getAbilities();
        boolean changed = false;

        if (abilities.flying) {
            abilities.flying = false;
            changed = true;
        }

        if (abilities.mayfly) {
            abilities.mayfly = false;
            changed = true;
        }

        if (changed) {
            player.onUpdateAbilities();
        }
    }
}
