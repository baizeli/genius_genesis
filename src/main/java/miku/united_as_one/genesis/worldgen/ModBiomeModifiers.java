package miku.united_as_one.genesis.worldgen;

import miku.united_as_one.genesis.Genesis;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModBiomeModifiers {
    public static final ResourceKey<BiomeModifier> ADD_ARCANE_CRYSTAL_ORE = biomeModifier("add_arcane_crystal_ore");
    public static final ResourceKey<BiomeModifier> ADD_DIVINE_METAL_ORE = biomeModifier("add_divine_metal_ore");
    public static final ResourceKey<BiomeModifier> ADD_VIOLET_GALAXY_ORE = biomeModifier("add_violet_galaxy_ore");

    private ModBiomeModifiers() {
    }

    public static void bootstrap(BootstapContext<BiomeModifier> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);

        context.register(ADD_ARCANE_CRYSTAL_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(
                        placedFeatures.getOrThrow(ModWorldgen.ORE_ARCANE_CRYSTAL_PLACED),
                        placedFeatures.getOrThrow(ModWorldgen.ORE_ARCANE_CRYSTAL_LARGE_PLACED),
                        placedFeatures.getOrThrow(ModWorldgen.ORE_ARCANE_CRYSTAL_BURIED_PLACED)
                ),
                GenerationStep.Decoration.UNDERGROUND_ORES
        ));
        context.register(ADD_DIVINE_METAL_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_NETHER),
                HolderSet.direct(placedFeatures.getOrThrow(ModWorldgen.ORE_DIVINE_METAL_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES
        ));
        context.register(ADD_VIOLET_GALAXY_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_END),
                HolderSet.direct(placedFeatures.getOrThrow(ModWorldgen.ORE_VIOLET_GALAXY_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES
        ));
    }

    private static ResourceKey<BiomeModifier> biomeModifier(String name) {
        return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, Genesis.id(name));
    }
}
