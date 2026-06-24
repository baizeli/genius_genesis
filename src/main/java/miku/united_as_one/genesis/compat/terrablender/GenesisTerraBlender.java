package miku.united_as_one.genesis.compat.terrablender;

import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.worldgen.ModSurfaceRules;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;

public final class GenesisTerraBlender {
    private GenesisTerraBlender() {
    }

    public static void registerBiomes() {
        Regions.register(new SourceForestRegion(Genesis.id("source_forest_overworld"), 1));
        Regions.register(new HorrorForestRegion(Genesis.id("horror_forest_nether"), 1));
        SurfaceRuleManager.addSurfaceRules(
                SurfaceRuleManager.RuleCategory.NETHER,
                Genesis.MOD_ID,
                ModSurfaceRules.horrorForest()
        );
    }
}
