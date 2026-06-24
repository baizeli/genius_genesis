package miku.united_as_one.genesis.workbench.arcane;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import miku.united_as_one.genesis.registries.RecipeSerializerRegistry;
import miku.united_as_one.genesis.registries.RecipeTypeRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.crafting.CraftingHelper;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ArcaneWorkbenchRecipe implements Recipe<CraftingContainer> {
    private static final int MAX_WIDTH = 5;
    private static final int MAX_HEIGHT = 5;

    private final ResourceLocation id;
    private final String group;
    private final CraftingBookCategory category;
    private final int width;
    private final int height;
    private final NonNullList<Ingredient> recipeItems;
    private final ItemStack result;
    private final boolean showNotification;

    public ArcaneWorkbenchRecipe(ResourceLocation id, String group, CraftingBookCategory category, int width, int height,
                                 NonNullList<Ingredient> recipeItems, ItemStack result, boolean showNotification) {
        this.id = id;
        this.group = group;
        this.category = category;
        this.width = width;
        this.height = height;
        this.recipeItems = recipeItems;
        this.result = result;
        this.showNotification = showNotification;
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        for (int x = 0; x <= container.getWidth() - width; x++) {
            for (int y = 0; y <= container.getHeight() - height; y++) {
                if (matches(container, x, y, true) || matches(container, x, y, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean matches(CraftingContainer container, int xOffset, int yOffset, boolean mirrored) {
        for (int x = 0; x < container.getWidth(); x++) {
            for (int y = 0; y < container.getHeight(); y++) {
                int recipeX = x - xOffset;
                int recipeY = y - yOffset;
                Ingredient ingredient = Ingredient.EMPTY;
                if (recipeX >= 0 && recipeY >= 0 && recipeX < width && recipeY < height) {
                    int index = mirrored ? width - recipeX - 1 + recipeY * width : recipeX + recipeY * width;
                    ingredient = recipeItems.get(index);
                }
                if (!ingredient.test(container.getItem(x + y * container.getWidth()))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= this.width && height >= this.height;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return result.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializerRegistry.ARCANE_WORKBENCH.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeTypeRegistry.ARCANE_WORKBENCH.get();
    }

    @Override
    public String getGroup() {
        return group;
    }

    public CraftingBookCategory category() {
        return category;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return recipeItems;
    }

    @Override
    public boolean showNotification() {
        return showNotification;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean isIncomplete() {
        return recipeItems.isEmpty()
                || recipeItems.stream().filter(ingredient -> !ingredient.isEmpty()).anyMatch(ForgeHooks::hasNoElements);
    }

    private static NonNullList<Ingredient> dissolvePattern(String[] pattern, Map<String, Ingredient> keys, int width, int height) {
        NonNullList<Ingredient> ingredients = NonNullList.withSize(width * height, Ingredient.EMPTY);
        Set<String> unusedKeys = Sets.newHashSet(keys.keySet());
        unusedKeys.remove(" ");

        for (int row = 0; row < pattern.length; row++) {
            for (int col = 0; col < pattern[row].length(); col++) {
                String symbol = pattern[row].substring(col, col + 1);
                Ingredient ingredient = keys.get(symbol);
                if (ingredient == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + symbol + "' but it is not defined in the key");
                }
                unusedKeys.remove(symbol);
                ingredients.set(col + width * row, ingredient);
            }
        }

        if (!unusedKeys.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that are not used in pattern: " + unusedKeys);
        }
        return ingredients;
    }

    @VisibleForTesting
    static String[] shrink(String... lines) {
        int min = Integer.MAX_VALUE;
        int max = 0;
        int leadingEmpty = 0;
        int trailingEmpty = 0;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            min = Math.min(min, firstNonSpace(line));
            int last = lastNonSpace(line);
            max = Math.max(max, last);
            if (last < 0) {
                if (leadingEmpty == i) {
                    leadingEmpty++;
                }
                trailingEmpty++;
            } else {
                trailingEmpty = 0;
            }
        }

        if (lines.length == trailingEmpty) {
            return new String[0];
        }

        String[] result = new String[lines.length - trailingEmpty - leadingEmpty];
        for (int i = 0; i < result.length; i++) {
            result[i] = lines[i + leadingEmpty].substring(min, max + 1);
        }
        return result;
    }

    private static int firstNonSpace(String line) {
        int i = 0;
        while (i < line.length() && line.charAt(i) == ' ') {
            i++;
        }
        return i;
    }

    private static int lastNonSpace(String line) {
        int i = line.length() - 1;
        while (i >= 0 && line.charAt(i) == ' ') {
            i--;
        }
        return i;
    }

    private static String[] patternFromJson(JsonArray patternArray) {
        String[] pattern = new String[patternArray.size()];
        if (pattern.length > MAX_HEIGHT) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, " + MAX_HEIGHT + " is maximum");
        }
        if (pattern.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        }

        for (int i = 0; i < pattern.length; i++) {
            String line = GsonHelper.convertToString(patternArray.get(i), "pattern[" + i + "]");
            if (line.length() > MAX_WIDTH) {
                throw new JsonSyntaxException("Invalid pattern: too many columns, " + MAX_WIDTH + " is maximum");
            }
            if (i > 0 && pattern[0].length() != line.length()) {
                throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
            }
            pattern[i] = line;
        }
        return pattern;
    }

    private static Map<String, Ingredient> keyFromJson(JsonObject keyJson) {
        Map<String, Ingredient> key = Maps.newHashMap();
        Iterator<Map.Entry<String, JsonElement>> iterator = keyJson.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonElement> entry = iterator.next();
            if (entry.getKey().length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol");
            }
            if (" ".equals(entry.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is reserved");
            }
            key.put(entry.getKey(), Ingredient.fromJson(entry.getValue(), false));
        }
        key.put(" ", Ingredient.EMPTY);
        return key;
    }

    public static class Serializer implements RecipeSerializer<ArcaneWorkbenchRecipe> {
        @Override
        public ArcaneWorkbenchRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            String group = GsonHelper.getAsString(json, "group", "");
            CraftingBookCategory category = CraftingBookCategory.CODEC.byName(
                    GsonHelper.getAsString(json, "category", null),
                    CraftingBookCategory.MISC
            );
            Map<String, Ingredient> key = keyFromJson(GsonHelper.getAsJsonObject(json, "key"));
            String[] pattern = shrink(patternFromJson(GsonHelper.getAsJsonArray(json, "pattern")));
            int width = pattern[0].length();
            int height = pattern.length;
            NonNullList<Ingredient> ingredients = dissolvePattern(pattern, key, width, height);
            ItemStack result = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true, true);
            boolean showNotification = GsonHelper.getAsBoolean(json, "show_notification", true);
            return new ArcaneWorkbenchRecipe(recipeId, group, category, width, height, ingredients, result, showNotification);
        }

        @Override
        public ArcaneWorkbenchRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int width = buffer.readVarInt();
            int height = buffer.readVarInt();
            String group = buffer.readUtf();
            CraftingBookCategory category = buffer.readEnum(CraftingBookCategory.class);
            NonNullList<Ingredient> ingredients = NonNullList.withSize(width * height, Ingredient.EMPTY);
            for (int i = 0; i < ingredients.size(); i++) {
                ingredients.set(i, Ingredient.fromNetwork(buffer));
            }
            ItemStack result = buffer.readItem();
            boolean showNotification = buffer.readBoolean();
            return new ArcaneWorkbenchRecipe(recipeId, group, category, width, height, ingredients, result, showNotification);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ArcaneWorkbenchRecipe recipe) {
            buffer.writeVarInt(recipe.width);
            buffer.writeVarInt(recipe.height);
            buffer.writeUtf(recipe.group);
            buffer.writeEnum(recipe.category);
            for (Ingredient ingredient : recipe.recipeItems) {
                ingredient.toNetwork(buffer);
            }
            buffer.writeItem(recipe.result);
            buffer.writeBoolean(recipe.showNotification);
        }
    }
}
