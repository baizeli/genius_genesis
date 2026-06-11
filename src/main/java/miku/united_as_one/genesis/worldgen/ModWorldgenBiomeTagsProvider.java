package miku.united_as_one.genesis.worldgen;

import miku.united_as_one.genesis.Genesis;
import net.minecraft.tags.BiomeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ModWorldgenBiomeTagsProvider extends BiomeTagsProvider {
    public ModWorldgenBiomeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Genesis.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModWorldgen.HAS_DESERT_TOWER)
                .add(Biomes.DESERT);

        tag(BiomeTags.IS_OVERWORLD)
                .add(ModBiomes.SOURCE_FOREST);
        tag(BiomeTags.IS_FOREST)
                .add(ModBiomes.SOURCE_FOREST);
        tag(BiomeTags.WITHOUT_PATROL_SPAWNS)
                .add(ModBiomes.SOURCE_FOREST);
        tag(BiomeTags.IS_NETHER)
                .add(ModBiomes.HORROR_FOREST);
        tag(BiomeTags.IS_FOREST)
                .add(ModBiomes.HORROR_FOREST);
        tag(BiomeTags.HAS_NETHER_FORTRESS)
                .add(ModBiomes.HORROR_FOREST);
        tag(BiomeTags.HAS_BASTION_REMNANT)
                .add(ModBiomes.HORROR_FOREST);
        tag(BiomeTags.HAS_RUINED_PORTAL_NETHER)
                .add(ModBiomes.HORROR_FOREST);
    }
}
