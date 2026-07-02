package miku.united_as_one.genesis.events.item;

import miku.united_as_one.genesis.Genesis;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Genesis.MOD_ID)
public final class FlyingSwallowThroughWillowEvent {
    private FlyingSwallowThroughWillowEvent() {
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player
                && player.level().getGameTime() < player.getPersistentData().getLong("FlyingSwallowFallImmunity")) {
            event.setCanceled(true);
        }
    }
}
