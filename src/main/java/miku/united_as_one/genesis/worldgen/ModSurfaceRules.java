package miku.united_as_one.genesis.worldgen;

import miku.united_as_one.genesis.registries.BlockRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.SurfaceRules;

public final class ModSurfaceRules {
    private ModSurfaceRules() {
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
