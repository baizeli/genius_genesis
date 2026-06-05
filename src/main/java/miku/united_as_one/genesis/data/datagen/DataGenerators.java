package miku.united_as_one.genesis.data.datagen;

import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.data.datagen.provider.ModCuriosDataProvider;
import miku.united_as_one.genesis.data.datagen.provider.ModCuriosItemTagProvider;
import miku.united_as_one.genesis.data.datagen.provider.ModDatapackEntriesProvider;
import miku.united_as_one.genesis.data.datagen.provider.ModEquipmentStatsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = Genesis.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenerators {
    private DataGenerators() {
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        ModDatapackEntriesProvider registryProvider = new ModDatapackEntriesProvider(output, lookupProvider);
        CompletableFuture<HolderLookup.Provider> fullLookupProvider = registryProvider.getRegistryProvider();

        generator.addProvider(event.includeServer(), registryProvider);
        generator.addProvider(event.includeServer(), new ModCuriosDataProvider(output));
        generator.addProvider(event.includeServer(), new ModCuriosItemTagProvider(output, fullLookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModEquipmentStatsProvider(generator));
    }
}
