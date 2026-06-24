package miku.united_as_one.genesis.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.registries.BlockRegistry;
import miku.united_as_one.genesis.workbench.registry.RecipeTypeRegistry;
import miku.united_as_one.genesis.workbench.arcane.ArcaneWorkbenchRecipeCategory;
import miku.united_as_one.genesis.workbench.arcane.ArcaneWorkbenchTransferInfo;
import miku.united_as_one.genesis.workbench.arcane_cauldron.ArcaneCauldronRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

@JeiPlugin
public class GenesisJeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return Genesis.id("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new ArcaneCauldronRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new ArcaneWorkbenchRecipeCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Level level = Minecraft.getInstance().level;
        if (level != null) {
            registration.addRecipes(
                    ArcaneCauldronRecipeCategory.RECIPE_TYPE,
                    level.getRecipeManager().getAllRecipesFor(RecipeTypeRegistry.ARCANE_CAULDRON.get())
            );
            registration.addRecipes(
                    ArcaneWorkbenchRecipeCategory.RECIPE_TYPE,
                    level.getRecipeManager().getAllRecipesFor(RecipeTypeRegistry.ARCANE_WORKBENCH.get())
            );
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(BlockRegistry.ARCANE_CAULDRON.get(), ArcaneCauldronRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(BlockRegistry.ARCANE_WORKBENCH.get(), ArcaneWorkbenchRecipeCategory.RECIPE_TYPE);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(new ArcaneWorkbenchTransferInfo());
    }
}
