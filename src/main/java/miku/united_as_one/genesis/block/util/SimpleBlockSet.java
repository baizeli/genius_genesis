package miku.united_as_one.genesis.block.util;

import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.BlockEntry;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.block.VerticalSlabBlock;
import miku.united_as_one.genesis.registries.CreativeTabRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("UnusedReturnValue")
public class SimpleBlockSet<T extends Block> {
    protected final String name;
    protected BlockEntry<T> base;
    protected ResourceLocation texture;

    @Nullable private BlockEntry<StairBlock> stairs;
    @Nullable private BlockEntry<SlabBlock> slab;
    @Nullable private BlockEntry<VerticalSlabBlock> verticalSlab;
    @Nullable private BlockEntry<TrapDoorBlock> trapDoor;
    @Nullable private BlockEntry<WallBlock> wall;
    @Nullable private BlockEntry<FenceBlock> fence;
    @Nullable private BlockEntry<RotatedPillarBlock> strippedLog;
    @Nullable private BlockEntry<LeavesBlock> leaves;
    @Nullable private BlockEntry<GrassBlock> grass;

    @Nullable private BlockEntry<RotatedPillarBlock> wood;
    @Nullable private BlockEntry<RotatedPillarBlock> strippedWood;
    private final List<BlockEntry<GrassBlock>> grassVariants = new ArrayList<>();
    private final Map<String, BlockEntry<GrassBlock>> grassVariantsByName = new LinkedHashMap<>();

    @Nullable private BlockEntry<DoorBlock> door;

    SimpleBlockSet(String name) {
        this.name = name;
        this.texture = ResourceLocation.fromNamespaceAndPath(Genesis.MOD_ID, "block/" + name);
    }

    public static SimpleBlockSet<Block> buildSimple(String name, Block vanillaCopy, TagKey<Block> blockTag, TagKey<Item> itemTag, TagKey<Block> minable) {
        SimpleBlockSet<Block> simpleBlockSet = new SimpleBlockSet<>(name);
        simpleBlockSet.base = Genesis.L2_REGISTRATE.block(name, Block::new)
                .initialProperties(() -> vanillaCopy)
                .blockstate((ctx, pvd) -> pvd.simpleBlock(ctx.get()))
                .tag(minable, blockTag)
                .item()
                .tab(CreativeTabRegistry.IRON_SPELLS_GENESIS_BLOCK)
                .tag(itemTag)
                .build()
                .register();
        return simpleBlockSet;
    }

    public static SimpleBlockSet<Block> buildSimpleCustom(String name, Block vanillaCopy, String suffix, TagKey<Block> blockTag, TagKey<Item> itemTag, TagKey<Block> minable) {
        SimpleBlockSet<Block> simpleBlockSet = new SimpleBlockSet<>(name);
        simpleBlockSet.base = Genesis.L2_REGISTRATE.block(name + suffix, Block::new)
                .initialProperties(() -> vanillaCopy)
                .blockstate((ctx, pvd) -> pvd.simpleBlock(ctx.get()))
                .tag(minable, blockTag)
                .item()
                .tab(CreativeTabRegistry.IRON_SPELLS_GENESIS_BLOCK)
                .tag(itemTag)
                .build()
                .register();
        simpleBlockSet.texture = ResourceLocation.fromNamespaceAndPath(Genesis.MOD_ID, "block/" + name + suffix);
        return simpleBlockSet;
    }

    public static SimpleBlockSet<Block> buildStone(String name, Block vanillaCopy) {
        return buildSimple(name, vanillaCopy, Tags.Blocks.STONE, Tags.Items.STONE, BlockTags.MINEABLE_WITH_PICKAXE);
    }

    public static SimpleBlockSet<Block> buildPlanks(String name, Block vanillaCopy) {
        return buildSimpleCustom(name, vanillaCopy, "_planks", BlockTags.PLANKS, ItemTags.PLANKS, BlockTags.MINEABLE_WITH_AXE);
    }

    public static SimpleBlockSet<Block> buildDirt(String name, Block vanillaCopy) {
        return buildSimpleCustom(name, vanillaCopy, "_dirt", BlockTags.DIRT, ItemTags.DIRT, BlockTags.MINEABLE_WITH_SHOVEL);
    }

    public static SimpleBlockSet<RotatedPillarBlock> buildLog(String name, Block vanillaCopy) {
        SimpleBlockSet<RotatedPillarBlock> simpleBlockSet = new SimpleBlockSet<>(name);
        simpleBlockSet.base = Genesis.L2_REGISTRATE.block(name, RotatedPillarBlock::new)
                .initialProperties(() -> vanillaCopy)
                .blockstate((ctx, pvd) -> pvd.logBlock(ctx.get()))
                .tag(BlockTags.MINEABLE_WITH_AXE, BlockTags.LOGS)
                .item()
                .tab(CreativeTabRegistry.IRON_SPELLS_GENESIS_BLOCK)
                .tag(ItemTags.LOGS)
                .build()
                .register();
        return simpleBlockSet;
    }

    public SimpleBlockSet<T> simpleBase(TagKey<Block> minable) {
        addStairs(minable).addSlab(minable).addVerticalSlab(minable);
        return this;
    }

    public SimpleBlockSet<T> simplePlank(BlockSetType type) {
        this.texture = ResourceLocation.fromNamespaceAndPath(Genesis.MOD_ID, "block/" + name + "_planks");
        simpleBase(BlockTags.MINEABLE_WITH_AXE).addTrapDoor(type).addDoor(type).addLeaves().addFence();
        return this;
    }

    public SimpleBlockSet<T> simplePlank() {
        return simplePlank(BlockSetType.OAK);
    }

    public SimpleBlockSet<T> simpleStone() {
        simpleBase(BlockTags.MINEABLE_WITH_PICKAXE).addWall();
        return this;
    }

    public SimpleBlockSet<T> addStairs(TagKey<Block> minable) {
        stairs = Genesis.L2_REGISTRATE.block(name + "_stairs", p -> new StairBlock(base::getDefaultState, p))
                .initialProperties(base)
                .blockstate((ctx, pvd) -> pvd.stairsBlock(ctx.get(), texture))
                .tag(minable, BlockTags.STAIRS)
                .item()
                .tab(CreativeTabRegistry.IRON_SPELLS_GENESIS_BLOCK)
                .tag(ItemTags.STAIRS)
                .build()
                .recipe((ctx, prov) -> prov.stairs(DataIngredient.items(base.get()), RecipeCategory.BUILDING_BLOCKS, ctx, null, minable.equals(BlockTags.MINEABLE_WITH_PICKAXE)))
                .register();
        return this;
    }

    public SimpleBlockSet<T> addSlab(TagKey<Block> minable) {
        slab = Genesis.L2_REGISTRATE.block(name + "_slab", SlabBlock::new)
                .initialProperties(base)
                .blockstate((ctx, pvd) -> pvd.slabBlock(ctx.get(), base.getId(), texture))
                .tag(minable, BlockTags.SLABS)
                .item()
                .tab(CreativeTabRegistry.IRON_SPELLS_GENESIS_BLOCK)
                .tag(ItemTags.SLABS)
                .build()
                .recipe((ctx, prov) -> prov.slab(DataIngredient.items(base.get()), RecipeCategory.BUILDING_BLOCKS, ctx, null, minable.equals(BlockTags.MINEABLE_WITH_PICKAXE)))
                .register();
        return this;
    }

    public SimpleBlockSet<T> addVerticalSlab(TagKey<Block> minable) {
        verticalSlab = Genesis.L2_REGISTRATE.block(name + "_vertical_slab", VerticalSlabBlock::new)
                .initialProperties(base)
                .blockstate((ctx, pvd) -> pvd.horizontalBlock(ctx.get(), VerticalSlabBlock.buildModel(ctx, pvd).texture("side", texture).texture("bottom", texture).texture("top", texture)))
                .tag(minable)
                .item()
                .tab(CreativeTabRegistry.IRON_SPELLS_GENESIS_BLOCK)
                .build()
                .recipe((ctx, prov) -> prov.singleItem(DataIngredient.items(base.get()), RecipeCategory.BUILDING_BLOCKS, ctx, 1, 2))
                .register();
        return this;
    }

    public SimpleBlockSet<T> addTrapDoor(BlockSetType type) {
        trapDoor = Genesis.L2_REGISTRATE.block(name + "_trapdoor", p -> new TrapDoorBlock(p, type))
                .initialProperties(base)
                .addLayer(() -> RenderType::cutout)
                .blockstate((ctx, pvd) -> pvd.trapdoorBlockWithRenderType(
                        ctx.get(),
                        pvd.modLoc("block/" + ctx.getName()),
                        true,
                        "cutout_mipped"
                ))
                .tag(BlockTags.MINEABLE_WITH_AXE, BlockTags.WOODEN_TRAPDOORS)
                .item()
                .tab(CreativeTabRegistry.IRON_SPELLS_GENESIS_BLOCK)
                .tag(ItemTags.WOODEN_TRAPDOORS)
                .model((ctx, pvd) -> pvd.trapdoorBottom(ctx.getName(), pvd.modLoc("block/" + ctx.getName())))
                .build()
                .recipe((ctx, prov) -> prov.trapDoor(DataIngredient.items(base.get()), RecipeCategory.REDSTONE, ctx, null))
                .register();
        return this;
    }

    public SimpleBlockSet<T> addDoor(BlockSetType type) {
        door = Genesis.L2_REGISTRATE.block(name + "_door", properties -> new DoorBlock(properties, type))
                .initialProperties(base)
                .addLayer(() -> RenderType::cutout)
                .blockstate((ctx, pvd) -> pvd.doorBlock(
                        ctx.get(),
                        pvd.modLoc("block/" + ctx.getName() + "_bottom"),
                        pvd.modLoc("block/" + ctx.getName() + "_top")
                ))
                .tag(BlockTags.MINEABLE_WITH_AXE, BlockTags.WOODEN_DOORS)
                .item()
                .tab(CreativeTabRegistry.IRON_SPELLS_GENESIS_BLOCK)
                .tag(ItemTags.WOODEN_DOORS)
                .model((ctx, pvd) -> pvd.withExistingParent(ctx.getName(), "item/generated")
                        .texture("layer0", pvd.modLoc("item/" + ctx.getName())))
                .build()
                .recipe((ctx, prov) -> prov.door(DataIngredient.items(base.get()), RecipeCategory.REDSTONE, ctx, null))
                .register();
        return this;
    }

    public SimpleBlockSet<T> addFence() {
        fence = Genesis.L2_REGISTRATE.block(name + "_fence", FenceBlock::new)
                .initialProperties(base)
                .blockstate((ctx, pvd) -> pvd.fenceBlock(ctx.get(), texture))
                .tag(BlockTags.MINEABLE_WITH_AXE, BlockTags.WOODEN_FENCES)
                .item()
                .tab(CreativeTabRegistry.IRON_SPELLS_GENESIS_BLOCK)
                .tag(ItemTags.WOODEN_FENCES)
                .model((ctx, pvd) -> pvd.fenceInventory(ctx.getName(), texture))
                .build()
                .recipe((ctx, prov) -> prov.fence(DataIngredient.items(base.get()), RecipeCategory.DECORATIONS, ctx, null))
                .register();
        return this;
    }

    public SimpleBlockSet<T> addWall() {
        wall = Genesis.L2_REGISTRATE.block(name + "_wall", WallBlock::new)
                .initialProperties(base)
                .blockstate((ctx, pvd) -> pvd.wallBlock(ctx.get(), texture))
                .tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.WALLS)
                .item()
                .tab(CreativeTabRegistry.IRON_SPELLS_GENESIS_BLOCK)
                .tag(ItemTags.WALLS)
                .model((ctx, pvd) -> pvd.wallInventory(ctx.getName(), texture))
                .build()
                .recipe((ctx, prov) -> prov.wall(DataIngredient.items(base.get()), RecipeCategory.BUILDING_BLOCKS, ctx))
                .register();
        return this;
    }

    public SimpleBlockSet<T> addStrippedLog() {
        strippedLog = Genesis.L2_REGISTRATE.block("stripped_" + name, RotatedPillarBlock::new)
                .initialProperties(base)
                .blockstate((ctx, pvd) -> pvd.logBlock(ctx.get()))
                .tag(BlockTags.MINEABLE_WITH_AXE, BlockTags.LOGS)
                .item()
                .tab(CreativeTabRegistry.IRON_SPELLS_GENESIS_BLOCK)
                .tag(ItemTags.LOGS)
                .build()
                .register();
        return this;
    }

    public SimpleBlockSet<T> addWood() {
        wood = Genesis.L2_REGISTRATE.block(name + "_wood", RotatedPillarBlock::new)
                .initialProperties(base)
                .blockstate((ctx, pvd) -> pvd.axisBlock(ctx.get(), pvd.modLoc("block/" + name), pvd.modLoc("block/" + name)))
                .tag(BlockTags.MINEABLE_WITH_AXE, BlockTags.LOGS)
                .item()
                .tab(CreativeTabRegistry.IRON_SPELLS_GENESIS_BLOCK)
                .tag(ItemTags.LOGS)
                .build()
                .register();
        return this;
    }

    public SimpleBlockSet<T> addStrippedWood() {
        strippedWood = Genesis.L2_REGISTRATE.block("stripped_" + name + "_wood", RotatedPillarBlock::new)
                .initialProperties(base)
                .blockstate((ctx, pvd) -> pvd.axisBlock(ctx.get(), pvd.modLoc("block/stripped_" + name), pvd.modLoc("block/stripped_" + name)))
                .tag(BlockTags.MINEABLE_WITH_AXE, BlockTags.LOGS)
                .item()
                .tab(CreativeTabRegistry.IRON_SPELLS_GENESIS_BLOCK)
                .tag(ItemTags.LOGS)
                .build()
                .register();
        return this;
    }

    public SimpleBlockSet<T> addLeaves() {
        leaves = Genesis.L2_REGISTRATE.block(name + "_leaves", LeavesBlock::new)
                .initialProperties(() -> Blocks.OAK_LEAVES)
                .addLayer(() -> RenderType::cutoutMipped)
                .blockstate((ctx, pvd) -> pvd.simpleBlock(ctx.get(), pvd.models().leaves(ctx.getName(), pvd.modLoc("block/" + name + "_leaves"))))
                .tag(BlockTags.LEAVES)
                .item()
                .tab(CreativeTabRegistry.IRON_SPELLS_GENESIS_BLOCK)
                .tag(ItemTags.LEAVES)
                .build()
                .register();
        return this;
    }

    public SimpleBlockSet<T> addGrass() {
        grass = Genesis.L2_REGISTRATE.block(name + "_grass_block", GrassBlock::new)
                .initialProperties(() -> Blocks.GRASS_BLOCK)
                .blockstate((ctx, pvd) -> pvd.simpleBlock(ctx.get(), pvd.models()
                        .withExistingParent(ctx.getName(), "block/grass_block")
                        .texture("top", pvd.modLoc("block/" + name + "_grass_block_top"))
                        .texture("side", pvd.modLoc("block/" + ctx.getName()))
                        .texture("bottom", pvd.modLoc("block/" + base.getId().getPath()))
                        .texture("particle", pvd.modLoc("block/" + base.getId().getPath()))
                        .texture("overlay", pvd.modLoc("block/" + ctx.getName()))
                ))
                .tag(BlockTags.MINEABLE_WITH_SHOVEL, BlockTags.DIRT)
                .item()
                .tab(CreativeTabRegistry.IRON_SPELLS_GENESIS_BLOCK)
                .build()
                .register();
        return this;
    }

    public SimpleBlockSet<T> addGrassVariant(String variantName) {
        BlockEntry<GrassBlock> variant = Genesis.L2_REGISTRATE.block(variantName + "_grass_block", GrassBlock::new)
                .initialProperties(() -> Blocks.GRASS_BLOCK)
                .blockstate((ctx, pvd) -> pvd.simpleBlock(ctx.get(), pvd.models()
                        .withExistingParent(ctx.getName(), "block/grass_block")
                        .texture("top", pvd.modLoc("block/" + variantName + "_grass_block_top"))
                        .texture("side", pvd.modLoc("block/" + ctx.getName()))
                        .texture("bottom", pvd.modLoc("block/" + base.getId().getPath()))
                        .texture("particle", pvd.modLoc("block/" + base.getId().getPath()))
                        .texture("overlay", pvd.modLoc("block/" + ctx.getName()))
                ))
                .tag(BlockTags.MINEABLE_WITH_SHOVEL, BlockTags.DIRT)
                .item()
                .tab(CreativeTabRegistry.IRON_SPELLS_GENESIS_BLOCK)
                .build()
                .register();
        grassVariants.add(variant);
        grassVariantsByName.put(variantName, variant);
        return this;
    }

    public BlockEntry<T> getBase() { return base; }
    public Optional<BlockEntry<StairBlock>> getStairs() { return Optional.ofNullable(stairs); }
    public Optional<BlockEntry<SlabBlock>> getSlab() { return Optional.ofNullable(slab); }
    public Optional<BlockEntry<VerticalSlabBlock>> getVerticalSlab() { return Optional.ofNullable(verticalSlab); }
    public Optional<BlockEntry<TrapDoorBlock>> getTrapDoor() { return Optional.ofNullable(trapDoor); }
    public Optional<BlockEntry<DoorBlock>> getDoor() { return Optional.ofNullable(door); }
    public Optional<BlockEntry<WallBlock>> getWall() { return Optional.ofNullable(wall); }
    public Optional<BlockEntry<FenceBlock>> getFence() { return Optional.ofNullable(fence); }
    public Optional<BlockEntry<RotatedPillarBlock>> getStrippedLog() { return Optional.ofNullable(strippedLog); }
    public Optional<BlockEntry<RotatedPillarBlock>> getWood() { return Optional.ofNullable(wood); }
    public Optional<BlockEntry<RotatedPillarBlock>> getStrippedWood() { return Optional.ofNullable(strippedWood); }
    public Optional<BlockEntry<LeavesBlock>> getLeaves() { return Optional.ofNullable(leaves); }
    public Optional<BlockEntry<GrassBlock>> getGrass() { return Optional.ofNullable(grass); }
    public List<BlockEntry<GrassBlock>> getGrassVariants() { return grassVariants; }
    public Optional<BlockEntry<GrassBlock>> getGrassVariant(String variantName) { return Optional.ofNullable(grassVariantsByName.get(variantName)); }
}
