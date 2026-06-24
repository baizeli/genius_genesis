package miku.united_as_one.genesis.data.datagen.provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.workbench.registry.RecipeSerializerRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ModArcaneCauldronRecipeProvider extends RecipeProvider {
    public ModArcaneCauldronRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> output) {
        output.accept(new TestDiamondRecipe(Genesis.id("arcane_cauldron/test_diamond")));
        output.accept(new TestWorkbenchRecipe(Genesis.id("arcane_workbench/test_recipe")));
    }

    private record TestDiamondRecipe(ResourceLocation id) implements FinishedRecipe {
        @Override
        public void serializeRecipeData(JsonObject json) {
            JsonArray ingredients = new JsonArray();
            JsonObject diamond = new JsonObject();
            diamond.addProperty("item", "minecraft:diamond");
            ingredients.add(diamond);
            json.add("ingredients", ingredients);

            JsonArray fluids = new JsonArray();
            JsonObject water = new JsonObject();
            water.addProperty("Amount", 250);
            water.addProperty("FluidName", "minecraft:water");
            fluids.add(water);
            json.add("fluids", fluids);

            json.addProperty("essence", 1);

            JsonObject result = new JsonObject();
            result.addProperty("item", "minecraft:emerald");
            json.add("result", result);
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return RecipeSerializerRegistry.ARCANE_CAULDRON.get();
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return null;
        }
    }

    private record TestWorkbenchRecipe(ResourceLocation id) implements FinishedRecipe {
        @Override
        public void serializeRecipeData(JsonObject json) {
            json.addProperty("category", "misc");

            JsonArray pattern = new JsonArray();
            pattern.add("  A  ");
            pattern.add(" ABA ");
            pattern.add("ACBCA");
            pattern.add(" ABA ");
            pattern.add("  A  ");
            json.add("pattern", pattern);

            JsonObject key = new JsonObject();
            key.add("A", ingredient("minecraft:iron_ingot"));
            key.add("B", ingredient("minecraft:diamond"));
            key.add("C", ingredient("minecraft:emerald"));
            json.add("key", key);

            JsonObject result = new JsonObject();
            result.addProperty("item", "minecraft:netherite_ingot");
            json.add("result", result);
            json.addProperty("show_notification", true);
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return RecipeSerializerRegistry.ARCANE_WORKBENCH.get();
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return null;
        }

        private static JsonObject ingredient(String item) {
            JsonObject json = new JsonObject();
            json.addProperty("item", item);
            return json;
        }
    }
}
