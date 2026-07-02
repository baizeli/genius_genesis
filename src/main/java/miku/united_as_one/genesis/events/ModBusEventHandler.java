package miku.united_as_one.genesis.events;

import miku.united_as_one.genesis.Genesis;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Genesis.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModBusEventHandler {

    private ModBusEventHandler() {
    }
}
