package miku.united_as_one.genesis.data.datagen;

import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.data.datagen.provider.*;
import miku.united_as_one.genesis.worldgen.ModWorldgenBiomeTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Mod.EventBusSubscriber(modid = Genesis.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenerators {
    private DataGenerators() {
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        startDatagenExitGuard(event);

        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        ModDatapackEntriesProvider registryProvider = new ModDatapackEntriesProvider(output, lookupProvider);
        CompletableFuture<HolderLookup.Provider> fullLookupProvider = registryProvider.getRegistryProvider();

        generator.addProvider(event.includeServer(), registryProvider);
        generator.addProvider(event.includeServer(), new ModArcaneCauldronRecipeProvider(output));
        generator.addProvider(event.includeServer(), new ModCuriosDataProvider(generator));
        generator.addProvider(event.includeServer(), new ModCuriosItemTagProvider(output, fullLookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModEquipmentStatsProvider(generator));
        generator.addProvider(event.includeServer(), new ModGenesisConfigProvider(generator));
        generator.addProvider(event.includeServer(), new ModMobEffectTagProvider(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModWorldgenBiomeTagsProvider(output, fullLookupProvider, existingFileHelper));
    }

    private static void startDatagenExitGuard(GatherDataEvent event) {
        if (!Boolean.parseBoolean(System.getProperty("genesis.datagen.exitGuard", "true"))) {
            return;
        }

        long delay = Long.getLong("genesis.datagen.exitGuardDelay", 30L);
        Genesis.LOGGER.info("Starting datagen exit guard, process will exit in {} seconds", delay);
        Thread thread = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(delay);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
            Genesis.LOGGER.info("Stopping datagen process");
            System.exit(0);
        }, Genesis.MOD_ID + " Datagen Exit Guard");
        thread.start();
    }
}
