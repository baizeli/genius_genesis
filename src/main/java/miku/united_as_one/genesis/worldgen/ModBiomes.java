package miku.united_as_one.genesis.worldgen;

import miku.united_as_one.genesis.Genesis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.NetherPlacements;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public final class ModBiomes {
    public static final ResourceKey<Biome> SOURCE_FOREST = ResourceKey.create(Registries.BIOME, Genesis.id("source_forest"));
    public static final ResourceKey<Biome> HORROR_FOREST = ResourceKey.create(Registries.BIOME, Genesis.id("horror_forest"));

    private ModBiomes() {
    }

    public static void bootstrap(BootstapContext<Biome> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> worldCarvers = context.lookup(Registries.CONFIGURED_CARVER);

        context.register(SOURCE_FOREST, sourceForest(placedFeatures, worldCarvers));
        context.register(HORROR_FOREST, horrorForest(placedFeatures, worldCarvers));
    }

    private static Biome sourceForest(
            HolderGetter<PlacedFeature> placedFeatures,
            HolderGetter<ConfiguredWorldCarver<?>> worldCarvers
    ) {
        MobSpawnSettings.Builder spawns = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.farmAnimals(spawns);
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 5, 4, 4));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FOX, 5, 2, 4));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 4, 2, 3));
        spawns.addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SQUID, 2, 1, 2));

        BiomeGenerationSettings.Builder generation = new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers);
        addOverworldBaseGeneration(generation);
        generation.addFeature(GenerationStep.Decoration.LAKES, ModWorldgen.SOURCE_POOL_PLACED);
        BiomeDefaultFeatures.addForestFlowers(generation);
        BiomeDefaultFeatures.addDefaultOres(generation);
        generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ModWorldgen.SOURCE_FOREST_TERRAIN_PLACED);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModWorldgen.SOURCE_TREE_PLACED);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModWorldgen.SOURCE_BIG_TREE_PLACED);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModWorldgen.SOURCE_TALL_TREE_PLACED);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModWorldgen.SOURCE_FOREST_ROCK_PLACED);
        BiomeDefaultFeatures.addDefaultFlowers(generation);
        BiomeDefaultFeatures.addForestGrass(generation);
        BiomeDefaultFeatures.addDefaultMushrooms(generation);
        BiomeDefaultFeatures.addDefaultExtraVegetation(generation);

        BiomeSpecialEffects effects = new BiomeSpecialEffects.Builder()
                .waterColor(0x219AFF)
                .waterFogColor(0x0D416F)
                .fogColor(0xD8C7AA)
                .skyColor(0x86BFFF)
                .grassColorOverride(0xFFFFFF)
                .foliageColorOverride(0xFFFFFF)
                .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                .build();

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.6F)
                .downfall(0.5F)
                .specialEffects(effects)
                .mobSpawnSettings(spawns.build())
                .generationSettings(generation.build())
                .build();
    }

    private static Biome horrorForest(
            HolderGetter<PlacedFeature> placedFeatures,
            HolderGetter<ConfiguredWorldCarver<?>> worldCarvers
    ) {
        MobSpawnSettings.Builder spawns = new MobSpawnSettings.Builder();
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ENDERMAN, 20, 4, 4));
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ZOMBIFIED_PIGLIN, 60, 4, 4));
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.PIGLIN, 25, 4, 4));
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.GHAST, 20, 1, 2));
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.MAGMA_CUBE, 10, 2, 4));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.STRIDER, 60, 1, 2));

        BiomeGenerationSettings.Builder generation = new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers);
        generation.addCarver(GenerationStep.Carving.AIR, Carvers.NETHER_CAVE);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MiscOverworldPlacements.SPRING_LAVA);
        BiomeDefaultFeatures.addDefaultMushrooms(generation);
        generation.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, NetherPlacements.SPRING_OPEN);
        generation.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, NetherPlacements.PATCH_FIRE);
        generation.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, NetherPlacements.PATCH_SOUL_FIRE);
        generation.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, NetherPlacements.GLOWSTONE_EXTRA);
        generation.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, NetherPlacements.GLOWSTONE);
        generation.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, OrePlacements.ORE_MAGMA);
        generation.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, NetherPlacements.SPRING_CLOSED);
        generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModWorldgen.QUIETNESS_HORROR_TREE_PLACED);
        BiomeDefaultFeatures.addNetherDefaultOres(generation);

        BiomeSpecialEffects effects = new BiomeSpecialEffects.Builder()
                .waterColor(0x3F76E4)
                .waterFogColor(0x050533)
                .fogColor(0x1A1024)
                .skyColor(0x6EB1FF)
                .ambientParticle(new AmbientParticleSettings(ParticleTypes.WARPED_SPORE, 0.006F))
                .ambientLoopSound(SoundEvents.AMBIENT_WARPED_FOREST_LOOP)
                .ambientMoodSound(new AmbientMoodSettings(SoundEvents.AMBIENT_WARPED_FOREST_MOOD, 6000, 8, 2.0D))
                .ambientAdditionsSound(new AmbientAdditionsSettings(SoundEvents.AMBIENT_WARPED_FOREST_ADDITIONS, 0.0111D))
                .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_WARPED_FOREST))
                .build();

        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(2.0F)
                .downfall(0.0F)
                .specialEffects(effects)
                .mobSpawnSettings(spawns.build())
                .generationSettings(generation.build())
                .build();
    }

    private static void addOverworldBaseGeneration(BiomeGenerationSettings.Builder generation) {
        BiomeDefaultFeatures.addSurfaceFreezing(generation);
    }
}
