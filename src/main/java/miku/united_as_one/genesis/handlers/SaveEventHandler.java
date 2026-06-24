package miku.united_as_one.genesis.handlers;

import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.data.save.SaveManager;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;

@Mod.EventBusSubscriber(modid = Genesis.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SaveEventHandler {
    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        if (Arrays.toString(Thread.currentThread().getStackTrace()).contains("doLoad")) return;
        SaveManager.init(event.getServer());
        SaveManager.save();
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        SaveManager.init(event.getServer());
        SaveManager.load();
    }
}
