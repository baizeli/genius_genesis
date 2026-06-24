package miku.united_as_one.genesis.workbench.arcane_cauldron;

import io.redspace.ironsspellbooks.api.backwards_compat.FluidHelper;
import io.redspace.ironsspellbooks.fluids.PotionFluid;
import io.redspace.ironsspellbooks.recipe_types.alchemist_cauldron.EmptyAlchemistCauldronRecipe;
import io.redspace.ironsspellbooks.recipe_types.alchemist_cauldron.FillAlchemistCauldronRecipe;
import io.redspace.ironsspellbooks.registries.RecipeRegistry;
import io.redspace.ironsspellbooks.util.ModTags;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.workbench.registry.BlockEntityRegistry;
import miku.united_as_one.genesis.registries.GenesisParticles;
import miku.united_as_one.genesis.workbench.registry.RecipeTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static io.redspace.ironsspellbooks.registries.ItemRegistry.ARCANE_ESSENCE;

public class ArcaneCauldronBlockEntity extends BlockEntity implements WorldlyContainer {
    public static final int INPUT_SIZE = 9;
    public static final int COOK_TIME = 100;
    private static final int TOTAL_FLUID_CAPACITY = 1000;
    private static final int[] SLOTS = {0, 1, 2, 3, 4, 5, 6, 7, 8};

    public final NonNullList<ItemStack> inputItems = NonNullList.withSize(INPUT_SIZE, ItemStack.EMPTY);
    public final ArcaneCauldronFluidHandler fluidInventory = new ArcaneCauldronFluidHandler();
    private final int[] cookTimes = new int[INPUT_SIZE];
    private final boolean[] cooked = new boolean[INPUT_SIZE];
    private final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> fluidInventory);

    public ArcaneCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.ARCANE_CAULDRON.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ArcaneCauldronBlockEntity cauldron) {
        boolean changed = false;
        boolean completedCook = false;
        boolean boiling = cauldron.isBoiling();

        for (int i = 0; i < cauldron.inputItems.size(); i++) {
            ItemStack stack = cauldron.inputItems.get(i);
            if (stack.isEmpty()) {
                if (cauldron.cookTimes[i] != 0 || cauldron.cooked[i]) {
                    cauldron.resetCookState(i);
                    changed = true;
                }
            } else if (boiling && !cauldron.cooked[i]) {
                cauldron.cookTimes[i]++;
                changed = true;
                if (cauldron.cookTimes[i] >= COOK_TIME) {
                    cauldron.cookTimes[i] = COOK_TIME;
                    cauldron.cooked[i] = true;
                    completedCook = true;
                }
            } else if (!boiling && !cauldron.cooked[i] && cauldron.cookTimes[i] != 0) {
                cauldron.cookTimes[i] = 0;
                changed = true;
            }
        }

        if (boiling && level instanceof ServerLevel serverLevel) {
            float fluidLevel = Mth.lerp(cauldron.getFluidAmount() / 1000.0F, 0.25F, 0.9F);
            serverLevel.sendParticles(
                    ParticleTypes.BUBBLE_POP,
                    pos.getX() + Mth.randomBetween(level.random, 0.2F, 0.8F),
                    pos.getY() + fluidLevel,
                    pos.getZ() + Mth.randomBetween(level.random, 0.2F, 0.8F),
                    1,
                    0.0D,
                    0.0D,
                    0.0D,
                    0.0D
            );
        }

        if (completedCook) {
            level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 0.7F, 1.2F);
            cauldron.setChanged();
        } else if (changed && level.getGameTime() % 20L == 0L) {
            cauldron.markDataDirty();
        }
    }

    public InteractionResult handleUse(Level level, Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);

        if (held.is(ARCANE_ESSENCE.get()) && canCraftWith(held)) {
            if (!level.isClientSide) {
                craftWith(held, player);
            }
            return InteractionResult.SUCCESS;
        }

        ItemStack recipeResult = tryExecuteRecipeInteractions(level, held);
        if (!recipeResult.isEmpty()) {
            player.setItemInHand(hand, ItemUtils.createFilledResult(held, player, recipeResult));
            return InteractionResult.SUCCESS;
        }

        if (isValidInput(held)) {
            if (!level.isClientSide) {
                insertInput(held, player);
            }
            return InteractionResult.SUCCESS;
        }

        if ((held.isEmpty() || player.isCrouching()) && hand == InteractionHand.MAIN_HAND) {
            for (int i = 0; i < inputItems.size(); i++) {
                if (!inputItems.get(i).isEmpty()) {
                    if (!level.isClientSide) {
                        takeInput(i, player, hand);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.CONSUME;
    }

    public ItemStack tryExecuteRecipeInteractions(Level level, ItemStack itemStack) {
        SimpleContainer fillInput = new SimpleContainer(itemStack);
        Optional<FillAlchemistCauldronRecipe> fillRecipe = level.getRecipeManager()
                .getRecipeFor(RecipeRegistry.ALCHEMIST_CAULDRON_FILL_TYPE.get(), fillInput, level);

        if (fillRecipe.isEmpty() && FluidHelper.hasPotionContents(itemStack)) {
            FluidStack fluid = FluidHelper.isWater(itemStack) ? new FluidStack(Fluids.WATER, 250) : PotionFluid.from(itemStack);
            fillRecipe = Optional.of(new FillAlchemistCauldronRecipe(
                    Genesis.id("generated"),
                    Ingredient.of(itemStack),
                    new ItemStack(Items.GLASS_BOTTLE),
                    fluid,
                    true,
                    BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.BOTTLE_EMPTY)
            ));
        }

        if (fillRecipe.isPresent()) {
            FillAlchemistCauldronRecipe recipe = fillRecipe.get();
            int fit = fluidInventory.fill(recipe.result(), IFluidHandler.FluidAction.SIMULATE);
            if (fit != 0 && (!recipe.mustFitAll() || fit == recipe.result().getAmount())) {
                fluidInventory.fill(recipe.result(), IFluidHandler.FluidAction.EXECUTE);
                setChanged();
                level.playSound(null, worldPosition, recipe.fillSound().value(), SoundSource.BLOCKS);
                return recipe.assemble(fillInput, level.registryAccess());
            }
        }

        FluidStack topFluid = fluidInventory.drain(TOTAL_FLUID_CAPACITY, IFluidHandler.FluidAction.SIMULATE);
        EmptyAlchemistCauldronRecipe.Input emptyInput = new EmptyAlchemistCauldronRecipe.Input(itemStack, topFluid);
        Optional<EmptyAlchemistCauldronRecipe> emptyRecipe = level.getRecipeManager()
                .getRecipeFor(RecipeRegistry.ALCHEMIST_CAULDRON_EMPTY_TYPE.get(), emptyInput, level);

        if (emptyRecipe.isEmpty() && itemStack.is(Items.GLASS_BOTTLE)) {
            ItemStack potion = PotionFluid.from(topFluid);
            if (!potion.isEmpty()) {
                emptyRecipe = Optional.of(new EmptyAlchemistCauldronRecipe(
                        Genesis.id("generated"),
                        Ingredient.EMPTY,
                        potion,
                        FluidHelper.copyWithAmount(topFluid, 250),
                        BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.BOTTLE_FILL)
                ));
            }
        }

        if (emptyRecipe.isPresent()) {
            EmptyAlchemistCauldronRecipe recipe = emptyRecipe.get();
            fluidInventory.drain(recipe.fluid(), IFluidHandler.FluidAction.EXECUTE);
            level.playSound(null, worldPosition, recipe.emptySound().value(), SoundSource.BLOCKS);
            setChanged();
            return recipe.assemble(emptyInput, level.registryAccess());
        }

        return ItemStack.EMPTY;
    }

    public boolean isValidInput(ItemStack stack) {
        if (stack.isEmpty() || stack.is(ARCANE_ESSENCE.get()) || level == null) {
            return false;
        }
        return level.getRecipeManager()
                .getAllRecipesFor(RecipeTypeRegistry.ARCANE_CAULDRON.get())
                .stream()
                .flatMap(recipe -> recipe.getIngredients().stream())
                .anyMatch(ingredient -> ingredient.test(stack));
    }

    public boolean canCraftWith(ItemStack catalyst) {
        return getCraftableRecipe(catalyst).isPresent();
    }

    private Optional<ArcaneCauldronRecipe> getCraftableRecipe(ItemStack catalyst) {
        if (level == null || !areAllInputItemsCooked()) {
            return Optional.empty();
        }

        ArcaneCauldronRecipe.Input input = new ArcaneCauldronRecipe.Input(copyInputItems(), fluidInventory.fluids(), catalyst);
        return level.getRecipeManager()
                .getAllRecipesFor(RecipeTypeRegistry.ARCANE_CAULDRON.get())
                .stream()
                .filter(recipe -> recipe.matches(input, level))
                .findFirst();
    }

    private void craftWith(ItemStack catalyst, Player player) {
        if (level == null) {
            return;
        }

        Optional<ArcaneCauldronRecipe> recipeOptional = getCraftableRecipe(catalyst);
        if (recipeOptional.isEmpty()) {
            return;
        }

        ArcaneCauldronRecipe recipe = recipeOptional.get();
        Optional<int[]> matchingSlots = recipe.findMatchingSlots(inputItems);
        if (matchingSlots.isEmpty()) {
            return;
        }

        ItemStack result = recipe.assemble(new ArcaneCauldronRecipe.Input(copyInputItems(), fluidInventory.fluids(), catalyst), level.registryAccess());
        if (result.isEmpty()) {
            return;
        }

        if (!player.getAbilities().instabuild) {
            catalyst.shrink(recipe.getEssenceCost());
        }

        for (FluidStack fluid : recipe.getFluids()) {
            fluidInventory.drain(fluid, IFluidHandler.FluidAction.EXECUTE);
        }

        int[] slots = matchingSlots.get();
        int outputSlot = slots[0];
        for (int slot : slots) {
            inputItems.set(slot, ItemStack.EMPTY);
            resetCookState(slot);
        }

        inputItems.set(outputSlot, result.copy());
        resetCookState(outputSlot);
        setChanged();
        spawnCraftParticles();
        level.playSound(null, worldPosition, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    private void spawnCraftParticles() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        int[] colors = {0xFF4FD8, 0x7CFF6B, 0x51E8FF, 0xFFE45C, 0xB66CFF, 0xFF6A3D, 0xFFFFFF};
        for (int i = 0; i < 24; i++) {
            int color = colors[level.random.nextInt(colors.length)];
            double red = ((color >> 16) & 0xFF) / 255.0D;
            double green = ((color >> 8) & 0xFF) / 255.0D;
            double blue = (color & 0xFF) / 255.0D;
            serverLevel.sendParticles(
                    GenesisParticles.GLOW_CUBE.get(),
                    worldPosition.getX() + 0.5D + (level.random.nextDouble() - 0.5D) * 0.35D,
                    worldPosition.getY() + 0.95D + level.random.nextDouble() * 0.15D,
                    worldPosition.getZ() + 0.5D + (level.random.nextDouble() - 0.5D) * 0.35D,
                    0,
                    red,
                    green,
                    blue,
                    1.0D
            );
        }
    }

    private boolean areAllInputItemsCooked() {
        boolean hasItem = false;
        for (int i = 0; i < inputItems.size(); i++) {
            if (!inputItems.get(i).isEmpty()) {
                hasItem = true;
                if (!cooked[i]) {
                    return false;
                }
            }
        }
        return hasItem;
    }

    private NonNullList<ItemStack> copyInputItems() {
        NonNullList<ItemStack> copy = NonNullList.withSize(inputItems.size(), ItemStack.EMPTY);
        for (int i = 0; i < inputItems.size(); i++) {
            copy.set(i, inputItems.get(i).copy());
        }
        return copy;
    }

    private void insertInput(ItemStack stack, Player player) {
        for (int i = 0; i < inputItems.size(); i++) {
            if (inputItems.get(i).isEmpty()) {
                ItemStack input = stack.copy();
                input.setCount(1);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                inputItems.set(i, input);
                resetCookState(i);
                setChanged();
                return;
            }
        }
    }

    private void takeInput(int slot, Player player, InteractionHand hand) {
        ItemStack take = inputItems.get(slot).split(1);
        if (inputItems.get(slot).isEmpty()) {
            inputItems.set(slot, ItemStack.EMPTY);
            resetCookState(slot);
        }

        if (player.getItemInHand(hand).isEmpty()) {
            player.setItemInHand(hand, take);
        } else if (!player.getInventory().add(take)) {
            player.drop(take, false);
        }
        setChanged();
    }

    private void resetCookState(int slot) {
        cookTimes[slot] = 0;
        cooked[slot] = false;
    }

    private void clearItems() {
        for (int i = 0; i < inputItems.size(); i++) {
            inputItems.set(i, ItemStack.EMPTY);
            resetCookState(i);
        }
    }

    private void markDataDirty() {
        super.setChanged();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        clearItems();
        ContainerHelper.loadAllItems(tag, inputItems);
        fluidInventory.clear();
        fluidInventory.load("Fluids", tag);

        int[] savedCookTimes = tag.getIntArray("CookTimes");
        for (int i = 0; i < Math.min(savedCookTimes.length, cookTimes.length); i++) {
            cookTimes[i] = savedCookTimes[i];
        }

        byte[] savedCooked = tag.getByteArray("Cooked");
        for (int i = 0; i < Math.min(savedCooked.length, cooked.length); i++) {
            cooked[i] = savedCooked[i] != 0;
        }
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, inputItems);
        fluidInventory.save("Fluids", tag);
        tag.putIntArray("CookTimes", cookTimes);

        byte[] savedCooked = new byte[cooked.length];
        for (int i = 0; i < cooked.length; i++) {
            savedCooked[i] = (byte) (cooked[i] ? 1 : 0);
        }
        tag.putByteArray("Cooked", savedCooked);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        CompoundTag tag = packet.getTag();
        if (tag != null) {
            handleUpdateTag(tag);
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        fluidHandler.invalidate();
    }

    public void dropContents() {
        SimpleContainer container = new SimpleContainer(inputItems.size());
        for (int i = 0; i < inputItems.size(); i++) {
            container.setItem(i, inputItems.get(i));
        }
        if (level != null) {
            Containers.dropContents(level, worldPosition, container);
        }
    }

    public boolean isBoiling() {
        return getFluidAmount() >= 1;
    }

    public int getFluidAmount() {
        return fluidInventory.fluidAmount();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
        return direction != Direction.DOWN && index >= 0 && index < inputItems.size() && getItem(index).isEmpty() && isValidInput(stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return direction == Direction.DOWN;
    }

    @Override
    public int getContainerSize() {
        return INPUT_SIZE;
    }

    @Override
    public boolean isEmpty() {
        return inputItems.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot >= 0 && slot < inputItems.size() ? inputItems.get(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack removed = ContainerHelper.removeItem(inputItems, slot, amount);
        if (!removed.isEmpty()) {
            if (getItem(slot).isEmpty()) {
                resetCookState(slot);
            }
            setChanged();
        }
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot < 0 || slot >= inputItems.size()) {
            return ItemStack.EMPTY;
        }
        ItemStack removed = ContainerHelper.takeItem(inputItems, slot);
        resetCookState(slot);
        return removed;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot >= 0 && slot < inputItems.size()) {
            ItemStack old = inputItems.get(slot);
            inputItems.set(slot, stack);
            if (!ItemStack.isSameItemSameTags(old, stack)) {
                resetCookState(slot);
            }
            setChanged();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return level != null
                && level.getBlockEntity(worldPosition) == this
                && player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void clearContent() {
        clearItems();
        fluidInventory.clear();
        setChanged();
    }

    public class ArcaneCauldronFluidHandler implements IFluidHandler {
        private IFluidTank[] tanks = {
                new CallbackFluidTank(TOTAL_FLUID_CAPACITY),
                new CallbackFluidTank(TOTAL_FLUID_CAPACITY),
                new CallbackFluidTank(TOTAL_FLUID_CAPACITY),
                new CallbackFluidTank(TOTAL_FLUID_CAPACITY)
        };

        @Override
        public int getTanks() {
            return tanks.length;
        }

        @Override
        public FluidStack getFluidInTank(int tank) {
            return tank < 0 || tank >= tanks.length || tanks[tank].getFluidAmount() == 0 ? FluidStack.EMPTY : tanks[tank].getFluid();
        }

        @Override
        public int getTankCapacity(int tank) {
            return TOTAL_FLUID_CAPACITY;
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return tank >= 0 && tank < tanks.length && tanks[tank].isFluidValid(stack);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (resource.isEmpty() || resource.getFluid().is(ModTags.CAULDRON_FLUID_DISALLOW)) {
                return 0;
            }

            int remainingCapacity = TOTAL_FLUID_CAPACITY - fluidAmount();
            if (remainingCapacity <= 0) {
                return 0;
            }

            int compatibleTank = -1;
            int emptyTank = -1;
            for (int i = 0; i < tanks.length; i++) {
                if (isTankCompatible(tanks[i], resource)) {
                    compatibleTank = i;
                    break;
                }
                if (emptyTank == -1 && tanks[i].getFluid().isEmpty()) {
                    emptyTank = i;
                }
            }

            FluidStack copy = FluidHelper.copyWithAmount(resource, Math.min(remainingCapacity, resource.getAmount()));
            if (compatibleTank >= 0) {
                return tanks[compatibleTank].fill(copy, action);
            }
            if (emptyTank >= 0) {
                return tanks[emptyTank].fill(copy, action);
            }
            return 0;
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            if (resource.isEmpty()) {
                return FluidStack.EMPTY;
            }

            for (IFluidTank tank : tanks) {
                if (isTankCompatible(tank, resource)) {
                    FluidStack result = tank.drain(resource, action);
                    bubbleEmptyTanks();
                    return result;
                }
            }
            return FluidStack.EMPTY;
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            for (int i = tanks.length - 1; i >= 0; i--) {
                if (!tanks[i].getFluid().isEmpty()) {
                    FluidStack result = tanks[i].drain(maxDrain, action);
                    bubbleEmptyTanks();
                    return result;
                }
            }
            return FluidStack.EMPTY;
        }

        public int fluidAmount() {
            return fluids().stream().mapToInt(FluidStack::getAmount).sum();
        }

        public List<FluidStack> fluids() {
            return Arrays.stream(tanks)
                    .map(IFluidTank::getFluid)
                    .filter(fluid -> !fluid.isEmpty())
                    .map(FluidStack::copy)
                    .toList();
        }

        public boolean contains(FluidStack stack, int minAmount) {
            return Arrays.stream(tanks).anyMatch(tank -> isTankCompatible(tank, stack) && tank.getFluidAmount() >= minAmount);
        }

        public boolean contains(Fluid fluid, int minAmount) {
            return Arrays.stream(tanks).anyMatch(tank -> tank.getFluid().getFluid() == fluid && tank.getFluidAmount() >= minAmount);
        }

        public void clear() {
            for (IFluidTank tank : tanks) {
                tank.drain(tank.getCapacity(), FluidAction.EXECUTE);
            }
        }

        public void save(String name, CompoundTag tag) {
            ListTag fluids = new ListTag();
            for (IFluidTank tank : tanks) {
                if (!tank.getFluid().isEmpty()) {
                    fluids.add(tank.getFluid().writeToNBT(new CompoundTag()));
                }
            }
            tag.put(name, fluids);
        }

        public void load(String name, CompoundTag tag) {
            if (!tag.contains(name, Tag.TAG_LIST)) {
                return;
            }

            ListTag fluids = tag.getList(name, Tag.TAG_COMPOUND);
            int index = 0;
            for (Tag fluidTag : fluids) {
                if (index >= tanks.length) {
                    break;
                }
                tanks[index++].fill(FluidStack.loadFluidStackFromNBT((CompoundTag) fluidTag), FluidAction.EXECUTE);
            }
        }

        private boolean isTankCompatible(IFluidTank tank, FluidStack stack) {
            return tank.isFluidValid(stack) && FluidHelper.isSameFluidSameComponents(tank.getFluid(), stack);
        }

        private void onContentsChanged() {
            ArcaneCauldronBlockEntity.this.setChanged();
        }

        private void bubbleEmptyTanks() {
            for (int left = 0; left < tanks.length - 1; left++) {
                for (int right = left + 1; right < tanks.length; right++) {
                    if (tanks[left].getFluid().isEmpty() && !tanks[right].getFluid().isEmpty()) {
                        IFluidTank tmp = tanks[left];
                        tanks[left] = tanks[right];
                        tanks[right] = tmp;
                    }
                }
            }
        }

        private class CallbackFluidTank extends FluidTank {
            private CallbackFluidTank(int capacity) {
                super(capacity);
            }

            @Override
            protected void onContentsChanged() {
                ArcaneCauldronFluidHandler.this.onContentsChanged();
            }
        }
    }
}
