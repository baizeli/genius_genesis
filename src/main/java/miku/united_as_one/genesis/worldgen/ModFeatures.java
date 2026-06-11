package miku.united_as_one.genesis.worldgen;

import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.worldgen.feature.QuietnessHorrorTreeFeature;
import miku.united_as_one.genesis.worldgen.feature.SourceForestTerrainFeature;
import miku.united_as_one.genesis.worldgen.feature.SourceSkyrisTreeFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModFeatures {
    private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, Genesis.MOD_ID);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> SOURCE_FOREST_TERRAIN = FEATURES.register(
            "source_forest_terrain",
            () -> new SourceForestTerrainFeature(NoneFeatureConfiguration.CODEC)
    );
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> SOURCE_SKYRIS_TREE = FEATURES.register(
            "source_skyris_tree",
            () -> new SourceSkyrisTreeFeature(NoneFeatureConfiguration.CODEC)
    );
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> QUIETNESS_HORROR_TREE = FEATURES.register(
            "quietness_horror_tree",
            () -> new QuietnessHorrorTreeFeature(NoneFeatureConfiguration.CODEC)
    );

    private ModFeatures() {
    }

    public static void register(IEventBus modBus) {
        FEATURES.register(modBus);
    }
}
