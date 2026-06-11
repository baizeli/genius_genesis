package miku.united_as_one.genesis.compat.terrablender;

import com.mojang.datafixers.util.Pair;
import miku.united_as_one.genesis.worldgen.ModBiomes;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;

import java.util.function.Consumer;

public class SourceForestRegion extends Region {
    public SourceForestRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        addModifiedVanillaOverworldBiomes(mapper, builder -> builder.replaceBiome(
                Biomes.MUSHROOM_FIELDS,
                ModBiomes.SOURCE_FOREST
        ));
    }
}
