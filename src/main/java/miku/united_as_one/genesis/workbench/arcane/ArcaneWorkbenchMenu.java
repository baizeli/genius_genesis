package miku.united_as_one.genesis.workbench.arcane;

import io.redspace.ironsspellbooks.registries.ItemRegistry;
import miku.united_as_one.genesis.workbench.registry.MenuTypeRegistry;
import miku.united_as_one.genesis.workbench.registry.RecipeTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ArcaneWorkbenchMenu extends AbstractContainerMenu {
    public static final int X_SHIFT = 31;
    public static final int Y_SHIFT = 27;

    public static final int RESULT_SLOT = 0;
    public static final int ESSENCE_MENU_SLOT = 1;
    public static final int GRID_START = 2;
    public static final int GRID_END_EXCLUSIVE = GRID_START + ArcaneWorkbenchBlockEntity.GRID_SIZE;
    public static final int INVENTORY_START = GRID_END_EXCLUSIVE;
    public static final int INVENTORY_END_EXCLUSIVE = INVENTORY_START + 27;
    public static final int HOTBAR_START = INVENTORY_END_EXCLUSIVE;
    public static final int HOTBAR_END_EXCLUSIVE = HOTBAR_START + 9;

    private final ArcaneWorkbenchBlockEntity blockEntity;
    private final ContainerLevelAccess access;

    public ArcaneWorkbenchMenu(int containerId, Inventory playerInventory, ArcaneWorkbenchBlockEntity blockEntity) {
        this(MenuTypeRegistry.ARCANE_WORKBENCH.get(), containerId, playerInventory, blockEntity);
    }

    public ArcaneWorkbenchMenu(MenuType<?> type, int containerId, Inventory playerInventory, ArcaneWorkbenchBlockEntity blockEntity) {
        super(type, containerId);
        this.blockEntity = blockEntity;
        this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        addSlot(new ResultSlot(playerInventory.player, blockEntity, blockEntity.resultSlots, 0, 131 + X_SHIFT, 36 + Y_SHIFT));
        addSlot(new EssenceSlot(blockEntity, ArcaneWorkbenchBlockEntity.ESSENCE_SLOT, 131 + X_SHIFT, 70 + Y_SHIFT));

        for (int row = 0; row < ArcaneWorkbenchBlockEntity.GRID_HEIGHT; row++) {
            for (int col = 0; col < ArcaneWorkbenchBlockEntity.GRID_WIDTH; col++) {
                addSlot(new Slot(blockEntity, col + row * ArcaneWorkbenchBlockEntity.GRID_WIDTH, X_SHIFT + col * 18, Y_SHIFT + row * 18));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, X_SHIFT + col * 18, 103 + row * 18 + Y_SHIFT));
            }
        }

        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, X_SHIFT + col * 18, 161 + Y_SHIFT));
        }
    }

    public ArcaneWorkbenchMenu(MenuType<?> type, int containerId, Inventory playerInventory) {
        super(type, containerId);
        this.blockEntity = null;
        this.access = ContainerLevelAccess.NULL;

        Container empty = new SimpleContainer(ArcaneWorkbenchBlockEntity.TOTAL_SLOTS);
        addSlot(new Slot(new SimpleContainer(1), 0, 131 + X_SHIFT, 36 + Y_SHIFT));
        addSlot(new EssenceSlot(empty, ArcaneWorkbenchBlockEntity.ESSENCE_SLOT, 131 + X_SHIFT, 70 + Y_SHIFT));
        for (int row = 0; row < ArcaneWorkbenchBlockEntity.GRID_HEIGHT; row++) {
            for (int col = 0; col < ArcaneWorkbenchBlockEntity.GRID_WIDTH; col++) {
                addSlot(new Slot(empty, col + row * ArcaneWorkbenchBlockEntity.GRID_WIDTH, X_SHIFT + col * 18, Y_SHIFT + row * 18));
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, X_SHIFT + col * 18, 103 + row * 18 + Y_SHIFT));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, X_SHIFT + col * 18, 161 + Y_SHIFT));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity != null && stillValid(access, player, blockEntity.getBlockState().getBlock());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        if (index == RESULT_SLOT) {
            if (!moveItemStackTo(stack, INVENTORY_START, HOTBAR_END_EXCLUSIVE, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(stack, copy);
        } else if (index == ESSENCE_MENU_SLOT || (index >= GRID_START && index < GRID_END_EXCLUSIVE)) {
            if (!moveItemStackTo(stack, INVENTORY_START, HOTBAR_END_EXCLUSIVE, true)) {
                return ItemStack.EMPTY;
            }
        } else if (index >= INVENTORY_START && index < HOTBAR_END_EXCLUSIVE) {
            if (stack.is(ItemRegistry.ARCANE_ESSENCE.get())) {
                if (!moveItemStackTo(stack, ESSENCE_MENU_SLOT, ESSENCE_MENU_SLOT + 1, false)
                        && !moveItemStackTo(stack, GRID_START, GRID_END_EXCLUSIVE, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, GRID_START, GRID_END_EXCLUSIVE, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (stack.getCount() == copy.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, stack);
        return copy;
    }

    public ArcaneWorkbenchBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public static class EssenceSlot extends Slot {
        public EssenceSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.is(ItemRegistry.ARCANE_ESSENCE.get());
        }
    }

    private class ResultSlot extends Slot {
        private final CraftingContainer craftSlots;
        private final Player player;
        private int removeCount;

        private ResultSlot(Player player, CraftingContainer craftSlots, Container container, int slot, int x, int y) {
            super(container, slot, x, y);
            this.player = player;
            this.craftSlots = craftSlots;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public ItemStack remove(int amount) {
            if (hasItem()) {
                removeCount += Math.min(amount, getItem().getCount());
            }
            return super.remove(amount);
        }

        @Override
        protected void onQuickCraft(ItemStack stack, int amount) {
            removeCount += amount;
            checkTakeAchievements(stack);
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            checkTakeAchievements(stack);
            blockEntity.consumeEssence();

            NonNullList<ItemStack> remainingItems = player.level().getRecipeManager()
                    .getRemainingItemsFor(RecipeTypeRegistry.ARCANE_WORKBENCH.get(), craftSlots, player.level());

            for (int i = 0; i < ArcaneWorkbenchBlockEntity.GRID_SIZE; i++) {
                ItemStack current = craftSlots.getItem(i);
                ItemStack remaining = i < remainingItems.size() ? remainingItems.get(i) : ItemStack.EMPTY;

                if (!current.isEmpty()) {
                    craftSlots.removeItem(i, 1);
                    current = craftSlots.getItem(i);
                }

                if (!remaining.isEmpty()) {
                    if (current.isEmpty()) {
                        craftSlots.setItem(i, remaining);
                    } else if (ItemStack.isSameItemSameTags(current, remaining)) {
                        remaining.grow(current.getCount());
                        craftSlots.setItem(i, remaining);
                    } else if (!this.player.getInventory().add(remaining)) {
                        this.player.drop(remaining, false);
                    }
                }
            }
        }
    }
}
