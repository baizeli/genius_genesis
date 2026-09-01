package miku.united_as_one.genesis.fluid;

import io.redspace.ironsspellbooks.fluids.SimpleTintedClientFluidType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;

import java.util.function.Consumer;

public final class InnateInkFluidType extends FluidType {
    public InnateInkFluidType() { super(Properties.create()); }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new SimpleTintedClientFluidType(ResourceLocation.fromNamespaceAndPath("forge", "block/milk_still"), 0xF7DF91));
    }
}
