package miku.united_as_one.genesis.workbench.arcane_cauldron;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import io.redspace.ironsspellbooks.api.backwards_compat.FluidHelper;
import miku.united_as_one.genesis.workbench.registry.RecipeSerializerRegistry;
import miku.united_as_one.genesis.workbench.registry.RecipeTypeRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static io.redspace.ironsspellbooks.registries.ItemRegistry.ARCANE_ESSENCE;

public class ArcaneCauldronRecipe implements Recipe<ArcaneCauldronRecipe.Input> {
    public static final int MAX_INGREDIENTS = 9;
    public static final int MAX_FLUIDS = 4;

    private final ResourceLocation id;
    private final NonNullList<Ingredient> ingredients;
    private final List<FluidStack> fluids;
    private final int essenceCost;
    private final ItemStack result;

    public ArcaneCauldronRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients, List<FluidStack> fluids, int essenceCost, ItemStack result) {
        this.id = id;
        this.ingredients = NonNullList.withSize(ingredients.size(), Ingredient.EMPTY);
        for (int i = 0; i < ingredients.size(); i++) {
            this.ingredients.set(i, ingredients.get(i));
        }
        this.fluids = fluids.stream().map(FluidStack::copy).toList();
        this.essenceCost = essenceCost;
        this.result = result.copy();
    }

    @Override
    public boolean matches(Input input, Level level) {
        return input.catalyst().is(ARCANE_ESSENCE.get())
                && input.catalyst().getCount() >= essenceCost
                && hasRequiredFluids(input.fluids())
                && findMatchingSlots(input.items()).isPresent();
    }

    public Optional<int[]> findMatchingSlots(NonNullList<ItemStack> items) {
        int itemCount = 0;
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                itemCount++;
            }
        }
        if (itemCount != ingredients.size()) {
            return Optional.empty();
        }

        boolean[] used = new boolean[items.size()];
        int[] slots = new int[ingredients.size()];
        return findMatchingSlots(items, used, slots, 0) ? Optional.of(slots) : Optional.empty();
    }

    private boolean findMatchingSlots(NonNullList<ItemStack> items, boolean[] used, int[] slots, int ingredientIndex) {
        if (ingredientIndex >= ingredients.size()) {
            return true;
        }

        Ingredient ingredient = ingredients.get(ingredientIndex);
        for (int slot = 0; slot < items.size(); slot++) {
            if (!used[slot] && ingredient.test(items.get(slot))) {
                used[slot] = true;
                slots[ingredientIndex] = slot;
                if (findMatchingSlots(items, used, slots, ingredientIndex + 1)) {
                    return true;
                }
                used[slot] = false;
            }
        }
        return false;
    }

    public boolean hasRequiredFluids(List<FluidStack> availableFluids) {
        List<FluidStack> remaining = availableFluids.stream()
                .map(FluidStack::copy)
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));

        for (FluidStack required : fluids) {
            int requiredAmount = required.getAmount();
            for (FluidStack available : remaining) {
                if (FluidHelper.isSameFluidSameComponents(required, available)) {
                    int drained = Math.min(requiredAmount, available.getAmount());
                    requiredAmount -= drained;
                    available.setAmount(available.getAmount() - drained);
                    if (requiredAmount <= 0) {
                        break;
                    }
                }
            }
            if (requiredAmount > 0) {
                return false;
            }
        }
        return true;
    }

    public boolean consumesFluid(FluidStack stack) {
        for (FluidStack fluid : fluids) {
            if (FluidHelper.isSameFluidSameComponents(fluid, stack)) {
                return true;
            }
        }
        return false;
    }

    public boolean usesIngredient(ItemStack stack) {
        for (Ingredient ingredient : ingredients) {
            if (ingredient.test(stack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack assemble(Input input, RegistryAccess registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= ingredients.size();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registries) {
        return result.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializerRegistry.ARCANE_CAULDRON.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeTypeRegistry.ARCANE_CAULDRON.get();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> copy = NonNullList.withSize(ingredients.size(), Ingredient.EMPTY);
        for (int i = 0; i < ingredients.size(); i++) {
            copy.set(i, ingredients.get(i));
        }
        return copy;
    }

    public List<FluidStack> getFluids() {
        return fluids.stream().map(FluidStack::copy).toList();
    }

    public int getEssenceCost() {
        return essenceCost;
    }

    public record Input(NonNullList<ItemStack> items, List<FluidStack> fluids, ItemStack catalyst) implements Container {
        @Override
        public int getContainerSize() {
            return items.size();
        }

        @Override
        public boolean isEmpty() {
            return items.stream().allMatch(ItemStack::isEmpty);
        }

        @Override
        public ItemStack getItem(int slot) {
            return slot >= 0 && slot < items.size() ? items.get(slot) : ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
        }

        @Override
        public void setChanged() {
        }

        @Override
        public boolean stillValid(Player player) {
            return false;
        }

        @Override
        public void clearContent() {
        }
    }

    public static class Serializer implements RecipeSerializer<ArcaneCauldronRecipe> {
        @Override
        public ArcaneCauldronRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            JsonArray ingredientArray = GsonHelper.getAsJsonArray(json, "ingredients");
            if (ingredientArray.isEmpty()) {
                throw new JsonSyntaxException("Arcane cauldron recipes require at least one ingredient");
            }
            if (ingredientArray.size() > MAX_INGREDIENTS) {
                throw new JsonSyntaxException("Arcane cauldron recipes support at most " + MAX_INGREDIENTS + " ingredients");
            }

            NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientArray.size(), Ingredient.EMPTY);
            for (int i = 0; i < ingredientArray.size(); i++) {
                ingredients.set(i, Ingredient.fromJson(ingredientArray.get(i), false));
            }

            JsonArray fluidArray = GsonHelper.getAsJsonArray(json, "fluids");
            if (fluidArray.isEmpty()) {
                throw new JsonSyntaxException("Arcane cauldron recipes require at least one fluid");
            }
            if (fluidArray.size() > MAX_FLUIDS) {
                throw new JsonSyntaxException("Arcane cauldron recipes support at most " + MAX_FLUIDS + " fluids");
            }

            List<FluidStack> fluids = new ArrayList<>();
            for (JsonElement element : fluidArray) {
                FluidStack fluid = FluidStack.CODEC.decode(JsonOps.INSTANCE, element)
                        .getOrThrow(false, message -> {
                        })
                        .getFirst();
                if (fluid.isEmpty()) {
                    throw new JsonSyntaxException("Arcane cauldron recipe fluid cannot be empty");
                }
                fluids.add(fluid);
            }

            int essenceCost = GsonHelper.getAsInt(json, "essence", 1);
            if (essenceCost <= 0) {
                throw new JsonSyntaxException("Arcane cauldron essence cost must be positive");
            }

            ItemStack result = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true, true);
            if (result.isEmpty()) {
                throw new JsonSyntaxException("Arcane cauldron recipe result cannot be empty");
            }

            return new ArcaneCauldronRecipe(recipeId, ingredients, fluids, essenceCost, result);
        }

        @Nullable
        @Override
        public ArcaneCauldronRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int ingredientCount = buffer.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);
            for (int i = 0; i < ingredientCount; i++) {
                ingredients.set(i, Ingredient.fromNetwork(buffer));
            }

            int fluidCount = buffer.readVarInt();
            List<FluidStack> fluids = new ArrayList<>();
            for (int i = 0; i < fluidCount; i++) {
                fluids.add(FluidStack.readFromPacket(buffer));
            }

            int essenceCost = buffer.readVarInt();
            ItemStack result = buffer.readItem();
            return new ArcaneCauldronRecipe(recipeId, ingredients, fluids, essenceCost, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ArcaneCauldronRecipe recipe) {
            buffer.writeVarInt(recipe.ingredients.size());
            for (Ingredient ingredient : recipe.ingredients) {
                ingredient.toNetwork(buffer);
            }

            buffer.writeVarInt(recipe.fluids.size());
            for (FluidStack fluid : recipe.fluids) {
                fluid.writeToPacket(buffer);
            }

            buffer.writeVarInt(recipe.essenceCost);
            buffer.writeItem(recipe.result);
        }
    }
}
