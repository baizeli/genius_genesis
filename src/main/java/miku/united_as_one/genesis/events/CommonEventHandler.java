package miku.united_as_one.genesis.events;

import miku.united_as_one.genesis.commands.GenesisCommands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class CommonEventHandler {
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        GenesisCommands.register(event.getDispatcher());
    }
}
