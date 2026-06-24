package miku.united_as_one.genesis.workbench.arcane;

import io.redspace.ironsspellbooks.registries.ItemRegistry;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.registries.BlockRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public class ArcaneWorkbenchRecipeCategory implements IRecipeCategory<ArcaneWorkbenchRecipe> {
    public static final RecipeType<ArcaneWorkbenchRecipe> RECIPE_TYPE =
            RecipeType.create(Genesis.MOD_ID, "arcane_workbench", ArcaneWorkbenchRecipe.class);
    private static final ResourceLocation BACKGROUND = Genesis.id("textures/gui/jei/arcane_workbench_jei.png");

    private final IDrawable background;
    private final IDrawable icon;

    public ArcaneWorkbenchRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(BACKGROUND, 0, 0, 176, 119);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockRegistry.ARCANE_WORKBENCH.get()));
    }

    @Override
    public RecipeType<ArcaneWorkbenchRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return ArcaneWorkbenchBlockEntity.TITLE;
    }

    @Nullable
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Nullable
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ArcaneWorkbenchRecipe recipe, IFocusGroup focuses) {
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        int offsetX = (ArcaneWorkbenchBlockEntity.GRID_WIDTH - recipe.getWidth()) / 2;
        int offsetY = (ArcaneWorkbenchBlockEntity.GRID_HEIGHT - recipe.getHeight()) / 2;
        int xShift = 3;
        int yShift = -4;

        for (int recipeY = 0; recipeY < recipe.getHeight(); recipeY++) {
            for (int recipeX = 0; recipeX < recipe.getWidth(); recipeX++) {
                int ingredientIndex = recipeY * recipe.getWidth() + recipeX;
                Ingredient ingredient = ingredients.get(ingredientIndex);
                if (!ingredient.isEmpty()) {
                    builder.addSlot(RecipeIngredientRole.INPUT, 8 + (offsetX + recipeX) * 18 + xShift, 18 + (offsetY + recipeY) * 18 + yShift)
                            .addIngredients(ingredient);
                }
            }
        }

        int essenceCost = recipe.getIngredients().stream()
                .mapToInt(ingredient -> ingredient.isEmpty() ? 0 : ArcaneWorkbenchBlockEntity.ESSENCE_PER_INGREDIENT)
                .sum();
        int outputX = 139 + xShift;
        int outputY = 54 + yShift;

        builder.addSlot(RecipeIngredientRole.OUTPUT, outputX, outputY)
                .addItemStack(recipe.getResultItem(null))
                .addTooltipCallback((recipeSlotView, tooltip) ->
                        tooltip.add(Component.translatable("jei.tooltip.recipe.arcane_workbench")));

        builder.addSlot(RecipeIngredientRole.INPUT, outputX, outputY + 24)
                .addItemStack(new ItemStack(ItemRegistry.ARCANE_ESSENCE.get(), Math.min(essenceCost, 64)))
                .addTooltipCallback((recipeSlotView, tooltip) ->
                        tooltip.add(Component.translatable("jei.tooltip.arcane_essence_consumption", essenceCost)));
    }
}
