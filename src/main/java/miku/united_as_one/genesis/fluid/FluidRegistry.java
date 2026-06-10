package miku.united_as_one.genesis.fluid;

import com.tterrag.registrate.util.entry.FluidEntry;
import com.mojang.blaze3d.shaders.FogShape;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.registries.CreativeTabRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public final class FluidRegistry {

    public static final FluidEntry<ForgeFlowingFluid.Flowing> SOURCE_FLUID = fluid(
            "source_fluid", new GenesisFluidType.Visual(33.0F / 255.0F, 154.0F / 255.0F, 1.0F, -8.0F, 15.0F, FogShape.CYLINDER));
    public static final FluidEntry<ForgeFlowingFluid.Flowing> BLACKWATER_FLUID = fluid(
            "blackwater_fluid", new GenesisFluidType.Visual(5.0F / 255.0F, 5.0F / 255.0F, 5.0F / 255.0F, -8.0F, 3.0F, FogShape.SPHERE));
    public static final FluidEntry<ForgeFlowingFluid.Flowing> BLOOD_FLUID = fluid(
            "blood_fluid", new GenesisFluidType.Visual(150.0F / 255.0F, 0.0F, 0.0F, -8.0F, 12.0F, FogShape.CYLINDER));

    private FluidRegistry() {
    }

    public static void register() {
    }

    private static FluidEntry<ForgeFlowingFluid.Flowing> fluid(String name, GenesisFluidType.Visual visual) {
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
                .tag(FluidTags.WATER)
                .renderType(RenderType::translucent)
                .bucket()
                .tab(CreativeTabRegistry.GENIUS_GENESIS_BLOCK)
                .build()
                .register();
    }
}
