package miku.united_as_one.genesis.workbench.arcane;

import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import miku.united_as_one.genesis.workbench.registry.MenuTypeRegistry;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArcaneWorkbenchTransferInfo implements IRecipeTransferInfo<ArcaneWorkbenchMenu, ArcaneWorkbenchRecipe> {
    @Override
    public Class<? extends ArcaneWorkbenchMenu> getContainerClass() {
        return ArcaneWorkbenchMenu.class;
    }

    @Override
    public Optional<MenuType<ArcaneWorkbenchMenu>> getMenuType() {
        return Optional.of(MenuTypeRegistry.ARCANE_WORKBENCH.get());
    }

    @Override
    public RecipeType<ArcaneWorkbenchRecipe> getRecipeType() {
        return ArcaneWorkbenchRecipeCategory.RECIPE_TYPE;
    }

    @Override
    public boolean canHandle(ArcaneWorkbenchMenu container, ArcaneWorkbenchRecipe recipe) {
        return recipe.getWidth() <= ArcaneWorkbenchBlockEntity.GRID_WIDTH
                && recipe.getHeight() <= ArcaneWorkbenchBlockEntity.GRID_HEIGHT;
    }

    @Override
    public List<Slot> getRecipeSlots(ArcaneWorkbenchMenu container, ArcaneWorkbenchRecipe recipe) {
        List<Slot> slots = new ArrayList<>();
        int offsetX = (ArcaneWorkbenchBlockEntity.GRID_WIDTH - recipe.getWidth()) / 2;
        int offsetY = (ArcaneWorkbenchBlockEntity.GRID_HEIGHT - recipe.getHeight()) / 2;

        for (int recipeY = 0; recipeY < recipe.getHeight(); recipeY++) {
            for (int recipeX = 0; recipeX < recipe.getWidth(); recipeX++) {
                Ingredient ingredient = recipe.getIngredients().get(recipeX + recipeY * recipe.getWidth());
                if (!ingredient.isEmpty()) {
                    int gridIndex = (offsetY + recipeY) * ArcaneWorkbenchBlockEntity.GRID_WIDTH + offsetX + recipeX;
                    slots.add(container.getSlot(ArcaneWorkbenchMenu.GRID_START + gridIndex));
                }
            }
        }

        slots.add(container.getSlot(ArcaneWorkbenchMenu.ESSENCE_MENU_SLOT));
        return slots;
    }

    @Override
    public List<Slot> getInventorySlots(ArcaneWorkbenchMenu container, ArcaneWorkbenchRecipe recipe) {
        List<Slot> slots = new ArrayList<>();
        for (int i = ArcaneWorkbenchMenu.INVENTORY_START; i < ArcaneWorkbenchMenu.HOTBAR_END_EXCLUSIVE; i++) {
            slots.add(container.getSlot(i));
        }
        return slots;
    }

    @Override
    public boolean requireCompleteSets(ArcaneWorkbenchMenu container, ArcaneWorkbenchRecipe recipe) {
        return true;
    }
}
