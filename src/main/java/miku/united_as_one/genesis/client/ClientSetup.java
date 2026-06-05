package miku.united_as_one.genesis.client;

import miku.united_as_one.genesis.client.render.cosmic.CosmicModelLoader;
import miku.united_as_one.genesis.client.render.effect.SlashEffectEvents;
import miku.united_as_one.genesis.registries.ItemRegistry;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.stream.Stream;

public final class ClientSetup {

    private ClientSetup() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(ClientSetup::clientSetup);
        modBus.addListener(ClientSetup::registerGeometryLoaders);
        MinecraftForge.EVENT_BUS.addListener(SlashEffectEvents::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(SlashEffectEvents::onRenderLevelStage);
        MinecraftForge.EVENT_BUS.addListener(SlashEffectEvents::onLevelUnload);
    }

    private static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> Stream.of(
                ItemRegistry.THUNDER_LONGBOW.get(),
                ItemRegistry.FROST_LONGBOW.get(),
                ItemRegistry.WITCHCRAFT_BOW.get(),
                ItemRegistry.FLAME_BOW.get()
        ).forEach(ClientSetup::registerBowProperties));
    }

    private static void registerBowProperties(Item bow) {
        ItemProperties.register(bow, new ResourceLocation("pull"), (stack, level, livingEntity, seed) -> {
            if (livingEntity == null || livingEntity.getUseItem() != stack) {
                return 0.0F;
            }
            return (float) (stack.getUseDuration() - livingEntity.getUseItemRemainingTicks()) / 20.0F;
        });

        ItemProperties.register(bow, new ResourceLocation("pulling"), (stack, level, livingEntity, seed) ->
                livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == stack ? 1.0F : 0.0F);
    }

    private static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register("cosmic", CosmicModelLoader.INSTANCE);
    }
}
