package miku.united_as_one.genesis.workbench.arcane;

import io.redspace.ironsspellbooks.registries.ItemRegistry;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.registries.BlockEntityRegistry;
import miku.united_as_one.genesis.registries.MenuTypeRegistry;
import miku.united_as_one.genesis.registries.RecipeTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ArcaneWorkbenchBlockEntity extends BaseContainerBlockEntity implements CraftingContainer, RecipeHolder {
    public static final int GRID_WIDTH = 5;
    public static final int GRID_HEIGHT = 5;
    public static final int GRID_SIZE = GRID_WIDTH * GRID_HEIGHT;
    public static final int ESSENCE_SLOT = GRID_SIZE;
    public static final int TOTAL_SLOTS = GRID_SIZE + 1;
    public static final int ESSENCE_PER_INGREDIENT = 2;
    public static final Component TITLE = Component.translatable("container." + Genesis.MOD_ID + ".arcane_workbench");

    private final NonNullList<ItemStack> items = NonNullList.withSize(TOTAL_SLOTS, ItemStack.EMPTY);
    final ResultContainer resultSlots = new ResultContainer();
    private Recipe<?> recipeUsed;

    public ArcaneWorkbenchBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.ARCANE_WORKBENCH.get(), pos, state);
    }

    @Override
    public int getWidth() {
        return GRID_WIDTH;
    }

    @Override
    public int getHeight() {
        return GRID_HEIGHT;
    }

    public ItemStack getEssenceItem() {
        return items.get(ESSENCE_SLOT);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    public boolean hasEnoughEssence() {
        ItemStack stack = getEssenceItem();
        return stack.is(ItemRegistry.ARCANE_ESSENCE.get()) && stack.getCount() >= getRequiredEssence();
    }

    public int getRequiredEssence() {
        return getOccupiedGridSlotCount() * ESSENCE_PER_INGREDIENT;
    }

    public void consumeEssence() {
        ItemStack stack = getEssenceItem();
        if (stack.is(ItemRegistry.ARCANE_ESSENCE.get())) {
            stack.shrink(getRequiredEssence());
            if (stack.isEmpty()) {
                items.set(ESSENCE_SLOT, ItemStack.EMPTY);
            }
            setChanged();
        }
    }

    int getOccupiedGridSlotCount() {
        int count = 0;
        for (int i = 0; i < GRID_SIZE; i++) {
            if (!items.get(i).isEmpty()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int getContainerSize() {
        return TOTAL_SLOTS;
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
        ItemStack removed = ContainerHelper.removeItem(items, slot, amount);
        if (!removed.isEmpty()) {
            onInventoryChanged();
        }
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot < 0 || slot >= items.size()) {
            return;
        }
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        onInventoryChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return level != null
                && level.getBlockEntity(worldPosition) == this
                && player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void clearContent() {
        items.clear();
        onInventoryChanged();
    }

    @Override
    protected Component getDefaultName() {
        return TITLE;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new ArcaneWorkbenchMenu(MenuTypeRegistry.ARCANE_WORKBENCH.get(), containerId, inventory, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, items);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ContainerHelper.loadAllItems(tag, items);
        updateResult();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        CompoundTag tag = packet.getTag();
        if (tag != null) {
            load(tag);
        }
    }

    @Override
    public void setRecipeUsed(@Nullable Recipe<?> recipe) {
        recipeUsed = recipe;
    }

    @Nullable
    @Override
    public Recipe<?> getRecipeUsed() {
        return recipeUsed;
    }

    public void fillStackedContents(StackedContents contents) {
        for (int i = 0; i < GRID_SIZE; i++) {
            contents.accountStack(items.get(i));
        }
    }

    private void onInventoryChanged() {
        setChanged();
        updateResult();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    private void updateResult() {
        if (level == null || level.isClientSide) {
            return;
        }

        RecipeManager recipeManager = level.getRecipeManager();
        Optional<ArcaneWorkbenchRecipe> optional = recipeManager.getRecipeFor(RecipeTypeRegistry.ARCANE_WORKBENCH.get(), this, level);
        if (optional.isPresent() && hasEnoughEssence()) {
            ArcaneWorkbenchRecipe recipe = optional.get();
            resultSlots.setItem(0, recipe.assemble(this, level.registryAccess()));
            setRecipeUsed(recipe);
            return;
        }
        resultSlots.setItem(0, ItemStack.EMPTY);
        setRecipeUsed(null);
    }
}
