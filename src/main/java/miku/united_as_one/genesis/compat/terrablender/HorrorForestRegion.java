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

public class HorrorForestRegion extends Region {
    public HorrorForestRegion(ResourceLocation name, int weight) {
        super(name, RegionType.NETHER, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        addNetherBiome(mapper, 0.0F, 0.0F, 0.0F, Biomes.NETHER_WASTES);
        addNetherBiome(mapper, 0.0F, -0.5F, 0.0F, Biomes.SOUL_SAND_VALLEY);
        addNetherBiome(mapper, 0.4F, 0.0F, 0.0F, Biomes.CRIMSON_FOREST);
        addNetherBiome(mapper, 0.0F, 0.5F, 0.375F, Biomes.WARPED_FOREST);
        addNetherBiome(mapper, -0.25F, 1.0F, 0.375F, ModBiomes.HORROR_FOREST);
        addNetherBiome(mapper, -0.5F, 0.0F, 0.175F, Biomes.BASALT_DELTAS);
    }

    private void addNetherBiome(
            Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper,
            float temperature,
            float humidity,
            float offset,
            ResourceKey<Biome> biome
    ) {
        addBiome(
                mapper,
                Climate.Parameter.point(temperature),
                Climate.Parameter.point(humidity),
                Climate.Parameter.point(0.0F),
                Climate.Parameter.point(0.0F),
                Climate.Parameter.point(0.0F),
                Climate.Parameter.point(0.0F),
                offset,
                biome
        );
    }
}
