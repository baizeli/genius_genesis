package miku.united_as_one.genesis.worldgen;

import com.mojang.datafixers.util.Pair;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.fluid.FluidRegistry;
import miku.united_as_one.genesis.registries.BlockRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.AttachedToLeavesDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.CountOnEveryLayerPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.placement.SurfaceWaterDepthFilter;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

public final class ModWorldgen {
    private static final int DESERT_TOWER_POOL_WEIGHT = 1;
    private static final int DESERT_TOWER_STRUCTURE_WEIGHT = 1;
    private static final int DESERT_TOWER_SPACING = 32;
    private static final int DESERT_TOWER_SEPARATION = 12;
    private static final int DESERT_TOWER_SALT = 1837429147;

    public static final ResourceKey<StructureTemplatePool> DESERT_TOWER_POOL = templatePool("desert_tower");
    public static final ResourceKey<Structure> DESERT_TOWER_STRUCTURE = structure("desert_tower");
    public static final ResourceKey<StructureSet> DESERT_TOWER_STRUCTURE_SET = structureSet("desert_tower");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SOURCE_TREE = configuredFeature("source_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SOURCE_BIG_TREE = configuredFeature("source_big_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SOURCE_TALL_TREE = configuredFeature("source_tall_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SOURCE_FOREST_ROCK = configuredFeature("source_forest_rock");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SOURCE_POOL = configuredFeature("source_pool");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SOURCE_FOREST_TERRAIN = configuredFeature("source_forest_terrain");
    public static final ResourceKey<ConfiguredFeature<?, ?>> QUIETNESS_HORROR_TREE = configuredFeature("quietness_horror_tree");
    public static final ResourceKey<PlacedFeature> SOURCE_TREE_PLACED = placedFeature("source_tree");
    public static final ResourceKey<PlacedFeature> SOURCE_BIG_TREE_PLACED = placedFeature("source_big_tree");
    public static final ResourceKey<PlacedFeature> SOURCE_TALL_TREE_PLACED = placedFeature("source_tall_tree");
    public static final ResourceKey<PlacedFeature> SOURCE_FOREST_ROCK_PLACED = placedFeature("source_forest_rock");
    public static final ResourceKey<PlacedFeature> SOURCE_POOL_PLACED = placedFeature("source_pool");
    public static final ResourceKey<PlacedFeature> SOURCE_FOREST_TERRAIN_PLACED = placedFeature("source_forest_terrain");
    public static final ResourceKey<PlacedFeature> QUIETNESS_HORROR_TREE_PLACED = placedFeature("quietness_horror_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_ARCANE_CRYSTAL_SMALL = configuredFeature("ore_arcane_crystal_small");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_ARCANE_CRYSTAL_LARGE = configuredFeature("ore_arcane_crystal_large");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_ARCANE_CRYSTAL_BURIED = configuredFeature("ore_arcane_crystal_buried");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_DIVINE_METAL = configuredFeature("ore_divine_metal");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_VIOLET_GALAXY = configuredFeature("ore_violet_galaxy");
    public static final ResourceKey<PlacedFeature> ORE_ARCANE_CRYSTAL_PLACED = placedFeature("ore_arcane_crystal");
    public static final ResourceKey<PlacedFeature> ORE_ARCANE_CRYSTAL_LARGE_PLACED = placedFeature("ore_arcane_crystal_large");
    public static final ResourceKey<PlacedFeature> ORE_ARCANE_CRYSTAL_BURIED_PLACED = placedFeature("ore_arcane_crystal_buried");
    public static final ResourceKey<PlacedFeature> ORE_DIVINE_METAL_PLACED = placedFeature("ore_divine_metal");
    public static final ResourceKey<PlacedFeature> ORE_VIOLET_GALAXY_PLACED = placedFeature("ore_violet_galaxy");
    public static final TagKey<Biome> HAS_DESERT_TOWER = TagKey.create(Registries.BIOME, Genesis.id("has_structure/desert_tower"));

    private ModWorldgen() {
    }

    public static void bootstrapConfiguredFeatures(BootstapContext<ConfiguredFeature<?, ?>> context) {
        BlockState sourceFluid = FluidRegistry.SOURCE_FLUID.getSource().defaultFluidState().createLegacyBlock();
        RuleTest stoneOreReplaceables = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateOreReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        RuleTest netherOreReplaceables = new TagMatchTest(BlockTags.BASE_STONE_NETHER);
        RuleTest endOreReplaceables = new BlockMatchTest(Blocks.END_STONE);
        List<OreConfiguration.TargetBlockState> arcaneCrystalTargets = List.of(
                OreConfiguration.target(stoneOreReplaceables, BlockRegistry.ARCANE_CRYSTAL_ORE.get().defaultBlockState()),
                OreConfiguration.target(deepslateOreReplaceables, BlockRegistry.ARCANE_CRYSTAL_ORE_DEEPSLATE.get().defaultBlockState())
        );

        context.register(SOURCE_FOREST_TERRAIN, new ConfiguredFeature<>(
                ModFeatures.SOURCE_FOREST_TERRAIN.get(),
                NoneFeatureConfiguration.INSTANCE
        ));
        context.register(SOURCE_TREE, new ConfiguredFeature<>(
                ModFeatures.SOURCE_SKYRIS_TREE.get(),
                NoneFeatureConfiguration.INSTANCE
        ));
        context.register(SOURCE_BIG_TREE, new ConfiguredFeature<>(
                ModFeatures.SOURCE_SKYRIS_TREE.get(),
                NoneFeatureConfiguration.INSTANCE
        ));
        context.register(SOURCE_TALL_TREE, new ConfiguredFeature<>(
                ModFeatures.SOURCE_SKYRIS_TREE.get(),
                NoneFeatureConfiguration.INSTANCE
        ));
        context.register(SOURCE_FOREST_ROCK, new ConfiguredFeature<>(
                Feature.FOREST_ROCK,
                new BlockStateConfiguration(BlockRegistry.SOURCE_STONE.getBase().get().defaultBlockState())
        ));
        context.register(SOURCE_POOL, new ConfiguredFeature<>(
                Feature.LAKE,
                new LakeFeature.Configuration(
                        BlockStateProvider.simple(sourceFluid),
                        BlockStateProvider.simple(BlockRegistry.SOURCE_STONE.getBase().get())
                )
        ));
        context.register(QUIETNESS_HORROR_TREE, new ConfiguredFeature<>(
                ModFeatures.QUIETNESS_HORROR_TREE.get(),
                NoneFeatureConfiguration.INSTANCE
        ));
        context.register(ORE_ARCANE_CRYSTAL_SMALL, new ConfiguredFeature<>(
                Feature.ORE,
                new OreConfiguration(arcaneCrystalTargets, 4, 0.5F)
        ));
        context.register(ORE_ARCANE_CRYSTAL_LARGE, new ConfiguredFeature<>(
                Feature.ORE,
                new OreConfiguration(arcaneCrystalTargets, 12, 0.7F)
        ));
        context.register(ORE_ARCANE_CRYSTAL_BURIED, new ConfiguredFeature<>(
                Feature.ORE,
                new OreConfiguration(arcaneCrystalTargets, 8, 1.0F)
        ));
        context.register(ORE_DIVINE_METAL, new ConfiguredFeature<>(
                Feature.ORE,
                new OreConfiguration(netherOreReplaceables, BlockRegistry.DIVINE_METAL_ORE.get().defaultBlockState(), 3, 1.0F)
        ));
        context.register(ORE_VIOLET_GALAXY, new ConfiguredFeature<>(
                Feature.ORE,
                new OreConfiguration(endOreReplaceables, BlockRegistry.VIOLET_GALAXY_ORE.get().defaultBlockState(), 3, 1.0F)
        ));
    }

    public static void bootstrapPlacedFeatures(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        BlockPredicateFilter sourceForestGround = BlockPredicateFilter.forPredicate(sourceForestTreeGround());

        context.register(SOURCE_FOREST_TERRAIN_PLACED, new PlacedFeature(
                configuredFeatures.getOrThrow(SOURCE_FOREST_TERRAIN),
                List.of()
        ));
        context.register(SOURCE_TREE_PLACED, new PlacedFeature(
                configuredFeatures.getOrThrow(SOURCE_TREE),
                List.of(
                        CountPlacement.of(1),
                        InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(0),
                        PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                        sourceForestGround,
                        BiomeFilter.biome()
                )
        ));
        context.register(SOURCE_BIG_TREE_PLACED, new PlacedFeature(
                configuredFeatures.getOrThrow(SOURCE_BIG_TREE),
                List.of(
                        RarityFilter.onAverageOnceEvery(8),
                        InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(0),
                        PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                        sourceForestGround,
                        BiomeFilter.biome()
                )
        ));
        context.register(SOURCE_TALL_TREE_PLACED, new PlacedFeature(
                configuredFeatures.getOrThrow(SOURCE_TALL_TREE),
                List.of(
                        RarityFilter.onAverageOnceEvery(5),
                        InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(0),
                        PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                        sourceForestGround,
                        BiomeFilter.biome()
                )
        ));
        context.register(SOURCE_FOREST_ROCK_PLACED, new PlacedFeature(
                configuredFeatures.getOrThrow(SOURCE_FOREST_ROCK),
                List.of(
                        RarityFilter.onAverageOnceEvery(4),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                )
        ));
        context.register(SOURCE_POOL_PLACED, new PlacedFeature(
                configuredFeatures.getOrThrow(SOURCE_POOL),
                List.of(
                        RarityFilter.onAverageOnceEvery(8),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                )
        ));
        context.register(QUIETNESS_HORROR_TREE_PLACED, new PlacedFeature(
                configuredFeatures.getOrThrow(QUIETNESS_HORROR_TREE),
                List.of(
                        CountOnEveryLayerPlacement.of(3),
                        BiomeFilter.biome()
                )
        ));
        context.register(ORE_ARCANE_CRYSTAL_PLACED, new PlacedFeature(
                configuredFeatures.getOrThrow(ORE_ARCANE_CRYSTAL_SMALL),
                commonOrePlacement(4, diamondHeight())
        ));
        context.register(ORE_ARCANE_CRYSTAL_LARGE_PLACED, new PlacedFeature(
                configuredFeatures.getOrThrow(ORE_ARCANE_CRYSTAL_LARGE),
                rareOrePlacement(18, diamondHeight())
        ));
        context.register(ORE_ARCANE_CRYSTAL_BURIED_PLACED, new PlacedFeature(
                configuredFeatures.getOrThrow(ORE_ARCANE_CRYSTAL_BURIED),
                commonOrePlacement(2, diamondHeight())
        ));
        context.register(ORE_DIVINE_METAL_PLACED, new PlacedFeature(
                configuredFeatures.getOrThrow(ORE_DIVINE_METAL),
                commonOrePlacement(7, HeightRangePlacement.uniform(VerticalAnchor.absolute(8), VerticalAnchor.absolute(33)))
        ));
        context.register(ORE_VIOLET_GALAXY_PLACED, new PlacedFeature(
                configuredFeatures.getOrThrow(ORE_VIOLET_GALAXY),
                commonOrePlacement(7, HeightRangePlacement.uniform(VerticalAnchor.absolute(30), VerticalAnchor.absolute(80)))
        ));
    }

    private static PlacementModifier diamondHeight() {
        return HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80));
    }

    private static List<PlacementModifier> commonOrePlacement(int count, PlacementModifier heightRange) {
        return orePlacement(CountPlacement.of(count), heightRange);
    }

    private static List<PlacementModifier> rareOrePlacement(int chance, PlacementModifier heightRange) {
        return orePlacement(RarityFilter.onAverageOnceEvery(chance), heightRange);
    }

    private static List<PlacementModifier> orePlacement(PlacementModifier count, PlacementModifier heightRange) {
        return List.of(count, InSquarePlacement.spread(), heightRange, BiomeFilter.biome());
    }

    private static BlockPredicate sourceForestTreeGround() {
        return BlockPredicate.matchesBlocks(
                Direction.DOWN.getNormal(),
                Blocks.GRASS_BLOCK,
                Blocks.DIRT,
                Blocks.COARSE_DIRT,
                Blocks.PODZOL,
                Blocks.ROOTED_DIRT,
                BlockRegistry.SOURCE_DIRT.getGrass().orElseThrow().get(),
                BlockRegistry.SOURCE_DIRT.getBase().get()
        );
    }

    public static void bootstrapTemplatePools(BootstapContext<StructureTemplatePool> context) {
        HolderGetter<StructureTemplatePool> templatePools = context.lookup(Registries.TEMPLATE_POOL);
        Holder<StructureTemplatePool> emptyPool = templatePools.getOrThrow(Pools.EMPTY);

        context.register(DESERT_TOWER_POOL, new StructureTemplatePool(
                emptyPool,
                List.of(Pair.of(StructurePoolElement.single(Genesis.id("desert_tower").toString()), DESERT_TOWER_POOL_WEIGHT)),
                StructureTemplatePool.Projection.RIGID
        ));
    }

    public static void bootstrapStructures(BootstapContext<Structure> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<StructureTemplatePool> templatePools = context.lookup(Registries.TEMPLATE_POOL);

        context.register(DESERT_TOWER_STRUCTURE, new JigsawStructure(
                new Structure.StructureSettings(
                        biomes.getOrThrow(HAS_DESERT_TOWER),
                        Map.of(),
                        GenerationStep.Decoration.SURFACE_STRUCTURES,
                        TerrainAdjustment.BEARD_THIN
                ),
                templatePools.getOrThrow(DESERT_TOWER_POOL),
                1,
                ConstantHeight.of(VerticalAnchor.absolute(0)),
                false,
                Heightmap.Types.WORLD_SURFACE_WG
        ));
    }

    public static void bootstrapStructureSets(BootstapContext<StructureSet> context) {
        HolderGetter<Structure> structures = context.lookup(Registries.STRUCTURE);

        context.register(DESERT_TOWER_STRUCTURE_SET, new StructureSet(
                List.of(StructureSet.entry(structures.getOrThrow(DESERT_TOWER_STRUCTURE), DESERT_TOWER_STRUCTURE_WEIGHT)),
                new RandomSpreadStructurePlacement(
                        DESERT_TOWER_SPACING,
                        DESERT_TOWER_SEPARATION,
                        RandomSpreadType.LINEAR,
                        DESERT_TOWER_SALT
                )
        ));
    }

    private static ResourceKey<StructureTemplatePool> templatePool(String name) {
        return ResourceKey.create(Registries.TEMPLATE_POOL, Genesis.id(name));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> configuredFeature(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, Genesis.id(name));
    }

    private static ResourceKey<PlacedFeature> placedFeature(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Genesis.id(name));
    }

    private static ResourceKey<Structure> structure(String name) {
        return ResourceKey.create(Registries.STRUCTURE, Genesis.id(name));
    }

    private static ResourceKey<StructureSet> structureSet(String name) {
        return ResourceKey.create(Registries.STRUCTURE_SET, Genesis.id(name));
    }
}
