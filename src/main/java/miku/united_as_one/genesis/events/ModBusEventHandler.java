package miku.united_as_one.genesis.events;

import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.combat.protectedhealth.ProtectedZombieDummy;
import miku.united_as_one.genesis.registries.EntityRegistry;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Genesis.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModBusEventHandler {
    @SubscribeEvent
    public static void attributes(EntityAttributeCreationEvent event) {
        event.put(EntityRegistry.PROTECTED_ZOMBIE_DUMMY.get(), ProtectedZombieDummy.createAttributes().build());
    }

    private ModBusEventHandler() {
    }
}
