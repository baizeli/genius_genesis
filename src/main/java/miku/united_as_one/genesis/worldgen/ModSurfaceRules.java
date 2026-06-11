package miku.united_as_one.genesis.worldgen;

import miku.united_as_one.genesis.registries.BlockRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.SurfaceRules;

public final class ModSurfaceRules {
    private ModSurfaceRules() {
    }

    public static SurfaceRules.RuleSource sourceForest() {
        SurfaceRules.RuleSource grass = state(BlockRegistry.SOURCE_DIRT.getGrass().orElseThrow().get());
        SurfaceRules.RuleSource dirt = state(BlockRegistry.SOURCE_DIRT.getBase().get());
        SurfaceRules.RuleSource sand = state(BlockRegistry.SOURCE_SAND.getBase().get());
        SurfaceRules.RuleSource stone = state(BlockRegistry.SOURCE_STONE.getBase().get());

        return SurfaceRules.ifTrue(
                SurfaceRules.isBiome(ModBiomes.SOURCE_FOREST),
                SurfaceRules.sequence(
                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(0, 0), grass),
                                sand
                        )),
                        SurfaceRules.ifTrue(SurfaceRules.VERY_DEEP_UNDER_FLOOR, stone),
                        SurfaceRules.ifTrue(SurfaceRules.DEEP_UNDER_FLOOR, stone),
                        SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, dirt)
                )
        );
    }

    public static SurfaceRules.RuleSource horrorForest() {
        SurfaceRules.RuleSource grass = state(BlockRegistry.QUIETNESS_GRASS.get());
        SurfaceRules.RuleSource dirt = state(BlockRegistry.GENESIS_DIRT.getBase().get());

        return SurfaceRules.ifTrue(
                SurfaceRules.isBiome(ModBiomes.HORROR_FOREST),
                SurfaceRules.sequence(
                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, grass),
                        SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, dirt)
                )
        );
    }

    private static SurfaceRules.RuleSource state(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }
}
