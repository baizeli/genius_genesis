package miku.united_as_one.genesis.registries;

import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.workbench.arcane.ArcaneWorkbenchRecipe;
import miku.united_as_one.genesis.workbench.arcane_cauldron.ArcaneCauldronRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class RecipeTypeRegistry {
    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Genesis.MOD_ID);

    public static final RegistryObject<RecipeType<ArcaneCauldronRecipe>> ARCANE_CAULDRON =
            recipeType("arcane_cauldron");
    public static final RegistryObject<RecipeType<ArcaneWorkbenchRecipe>> ARCANE_WORKBENCH =
            recipeType("arcane_workbench");

    private RecipeTypeRegistry() {
    }

    public static void register(IEventBus modBus) {
        RECIPE_TYPES.register(modBus);
    }

    private static <T extends Recipe<?>> RegistryObject<RecipeType<T>> recipeType(String name) {
        return RECIPE_TYPES.register(name, () -> RecipeType.simple(Genesis.id(name)));
    }
}
