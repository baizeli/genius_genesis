package miku.united_as_one.genesis.fluid;

import com.tterrag.registrate.util.entry.FluidEntry;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.registries.CreativeTabRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import io.redspace.ironsspellbooks.fluids.NoopFluid;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class FluidRegistry {
    private static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, Genesis.MOD_ID);
    private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, Genesis.MOD_ID);
    public static final RegistryObject<FluidType> INNATE_INK_TYPE = FLUID_TYPES.register("innate_ink", InnateInkFluidType::new);
    public static final RegistryObject<Fluid> INNATE_INK = registerNoop("innate_ink", INNATE_INK_TYPE);

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

    public static void register(IEventBus bus) {
        FLUID_TYPES.register(bus);
        FLUIDS.register(bus);
    }

    private static RegistryObject<Fluid> registerNoop(String name, RegistryObject<FluidType> type) {
        RegistryObject<Fluid> reference = RegistryObject.create(Genesis.id(name), ForgeRegistries.FLUIDS);
        ForgeFlowingFluid.Properties properties = new ForgeFlowingFluid.Properties(type, reference, reference).bucket(() -> Items.AIR);
        FLUIDS.register(name, () -> new NoopFluid(properties));
        return reference;
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
