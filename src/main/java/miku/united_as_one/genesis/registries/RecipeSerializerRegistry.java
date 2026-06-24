package miku.united_as_one.genesis.registries;

import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.workbench.arcane.ArcaneWorkbenchRecipe;
import miku.united_as_one.genesis.workbench.arcane_cauldron.ArcaneCauldronRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class RecipeSerializerRegistry {
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Genesis.MOD_ID);

    public static final RegistryObject<RecipeSerializer<ArcaneCauldronRecipe>> ARCANE_CAULDRON =
            RECIPE_SERIALIZERS.register("arcane_cauldron", ArcaneCauldronRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<ArcaneWorkbenchRecipe>> ARCANE_WORKBENCH =
            RECIPE_SERIALIZERS.register("arcane_workbench_recipe", ArcaneWorkbenchRecipe.Serializer::new);

    private RecipeSerializerRegistry() {
    }

    public static void register(IEventBus modBus) {
        RECIPE_SERIALIZERS.register(modBus);
    }
}
