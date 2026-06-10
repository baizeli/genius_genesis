package miku.united_as_one.genesis.worldgen;

import com.mojang.datafixers.util.Pair;
import miku.united_as_one.genesis.Genesis;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
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

import java.util.List;
import java.util.Map;

public final class ModWorldgen {
    private static final int DESERT_TOWER_POOL_WEIGHT = 1;
    private static final int DESERT_TOWER_STRUCTURE_WEIGHT = 1;
    private static final int DESERT_TOWER_SPACING = 32;
    private static final int DESERT_TOWER_SEPARATION = 12;
    private static final int DESERT_TOWER_SALT = 1837429147;

    public static final ResourceKey<StructureTemplatePool> DESERT_TOWER_POOL = templatePool("desert_tower");
    public static final ResourceKey<Structure> DESERT_TOWER_STRUCTURE = structure("desert_tower");
    public static final ResourceKey<StructureSet> DESERT_TOWER_STRUCTURE_SET = structureSet("desert_tower");
    public static final TagKey<Biome> HAS_DESERT_TOWER = TagKey.create(Registries.BIOME, Genesis.id("has_structure/desert_tower"));

    private ModWorldgen() {
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

    private static ResourceKey<Structure> structure(String name) {
        return ResourceKey.create(Registries.STRUCTURE, Genesis.id(name));
    }

    private static ResourceKey<StructureSet> structureSet(String name) {
        return ResourceKey.create(Registries.STRUCTURE_SET, Genesis.id(name));
    }
}
