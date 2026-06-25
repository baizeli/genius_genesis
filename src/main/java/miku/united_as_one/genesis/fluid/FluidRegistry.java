package miku.united_as_one.genesis.fluid;

import com.tterrag.registrate.util.entry.FluidEntry;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.registries.CreativeTabRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public final class FluidRegistry {

    public static final TagKey<Fluid> SOURCE_FLUID_TAG = FluidTags.create(Genesis.id("source_fluid"));
    public static final FluidEntry<ForgeFlowingFluid.Flowing> SOURCE_FLUID = fluid(
            "source_fluid",
            new GenesisFluidType.Visual(33.0F / 255.0F, 154.0F / 255.0F, 1.0F, -8.0F, 15.0F, GenesisFluidType.FogShapeType.CYLINDER),
            FluidTags.WATER,
            SOURCE_FLUID_TAG);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> BLACKWATER_FLUID = fluid(
            "blackwater_fluid",
            new GenesisFluidType.Visual(5.0F / 255.0F, 5.0F / 255.0F, 5.0F / 255.0F, -8.0F, 3.0F, GenesisFluidType.FogShapeType.SPHERE),
            FluidTags.WATER);
    public static final FluidEntry<ForgeFlowingFluid.Flowing> BLOOD_FLUID = fluid(
            "blood_fluid",
            new GenesisFluidType.Visual(150.0F / 255.0F, 0.0F, 0.0F, -8.0F, 12.0F, GenesisFluidType.FogShapeType.CYLINDER),
            FluidTags.WATER);

    private FluidRegistry() {
    }

    public static void register() {
    }

    @SafeVarargs
    private static FluidEntry<ForgeFlowingFluid.Flowing> fluid(
            String name,
            GenesisFluidType.Visual visual,
            TagKey<Fluid>... tags
    ) {
        ResourceLocation stillTexture = Genesis.id("block/" + name + "_still");
        ResourceLocation flowingTexture = Genesis.id("block/" + name + "_flow");
        return Genesis.L2_REGISTRATE
                .fluid(name, stillTexture, flowingTexture, (properties, still, flowing) -> new GenesisFluidType(properties, still, flowing, visual))
                .fluidProperties(properties -> properties
                        .levelDecreasePerBlock(1)
                        .tickRate(5)
                        .slopeFindDistance(4)
                        .explosionResistance(100.0F))
                .source(ForgeFlowingFluid.Source::new)
                .tag(tags)
                .bucket()
                .tab(CreativeTabRegistry.GENIUS_GENESIS_BLOCK)
                .build()
                .register();
    }
}
