package miku.united_as_one.genesis.registries;

import com.tterrag.registrate.util.entry.BlockEntry;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.content.block.util.SimpleBlockSet;
import miku.united_as_one.genesis.workbench.arcane.ArcaneWorkbenchBlock;
import miku.united_as_one.genesis.workbench.arcane_cauldron.ArcaneCauldronBlock;
import miku.united_as_one.genesis.worldgen.SourceTreeGrower;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;

import java.util.function.Supplier;

public final class BlockRegistry {

    // 装饰石材
    public static final SimpleBlockSet<Block> WEATHERED_SANDSTONE = SimpleBlockSet.buildStone("weathered_sandstone", Blocks.SANDSTONE).simpleStone();
    public static final SimpleBlockSet<Block> WEATHERED_STONE_BRICKS = SimpleBlockSet.buildStone("weathered_stone_bricks", Blocks.STONE_BRICKS).simpleStone();
    public static final SimpleBlockSet<Block> WEATHERED_SAND = SimpleBlockSet.buildStone("weathered_sand", Blocks.SAND);
    public static final SimpleBlockSet<Block> BLOOD_SAND = SimpleBlockSet.buildStone("blood_sand", Blocks.SAND);
    public static final SimpleBlockSet<Block> FIRE_SAND = SimpleBlockSet.buildStone("fire_sand", Blocks.SAND);
    public static final SimpleBlockSet<Block> HEART_SCULPTING = SimpleBlockSet.buildStone("heart_sculpting", Blocks.STONE).simpleStone();
    public static final SimpleBlockSet<Block> FIRE_STONE = SimpleBlockSet.buildStone("fire_stone", Blocks.STONE).simpleStone();
    public static final SimpleBlockSet<Block> DEEP_FEAR_STONE = SimpleBlockSet.buildStone("deep_fear_stone", Blocks.STONE).simpleStone();
    public static final SimpleBlockSet<Block> SMOOTH_DEEP_FEAR_STONE = SimpleBlockSet.buildStone("smooth_deep_fear_stone", Blocks.SMOOTH_STONE).simpleStone();
    public static final SimpleBlockSet<Block> DEEP_FEAR_STONE_BRICKS = SimpleBlockSet.buildStone("deep_fear_stone_bricks", Blocks.STONE_BRICKS).simpleStone();
    public static final SimpleBlockSet<Block> CRACKED_DEEP_FEAR_STONE_BRICKS = SimpleBlockSet.buildStone("cracked_deep_fear_stone_bricks", Blocks.CRACKED_STONE_BRICKS).simpleStone();
    public static final SimpleBlockSet<Block> SWAY_STONE = SimpleBlockSet.buildStone("sway_stone", Blocks.STONE).simpleStone();

    // 木材套装
    public static final SimpleBlockSet<RotatedPillarBlock> SWAY_LOG = SimpleBlockSet.buildLog("sway_log", Blocks.OAK_LOG).addStrippedLog().addWood().addStrippedWood();
    public static final SimpleBlockSet<Block> SWAY_PLANKS = SimpleBlockSet.buildPlanks("sway", Blocks.OAK_PLANKS).simplePlank(BlockSetType.OAK);
    public static final SimpleBlockSet<RotatedPillarBlock> QUIETNESS_LOG = SimpleBlockSet.buildLog("quietness_log", Blocks.OAK_LOG).addStrippedLog().addWood().addStrippedWood();
    public static final SimpleBlockSet<Block> QUIETNESS_PLANKS = SimpleBlockSet.buildPlanks("quietness", Blocks.OAK_PLANKS).simplePlank();

    // 水晶方块
    public static final BlockEntry<Block> ARCANE_CRYSTAL_BLOCK = crystalBlock("arcane_crystal_block");
    public static final BlockEntry<Block> FEAR_CRYSTALS = crystalBlock("fear_crystals");

    // 自然方块
    public static final SimpleBlockSet<Block> GENESIS_DIRT = SimpleBlockSet.buildDirt("genesis", Blocks.DIRT).addGrassVariant("sway").addGrassVariant("quietness");
    public static final BlockEntry<GrassBlock> QUIETNESS_GRASS = GENESIS_DIRT.getGrassVariant("quietness").orElseThrow();
    public static final SimpleBlockSet<Block> SOURCE_DIRT = SimpleBlockSet.buildDirt("source", Blocks.DIRT).addGrass();
    public static final SimpleBlockSet<Block> SOURCE_SAND = SimpleBlockSet.buildSimple("source_sand", Blocks.SAND, BlockTags.SAND, Tags.Items.SAND, BlockTags.MINEABLE_WITH_SHOVEL);
    public static final SimpleBlockSet<Block> SOURCE_STONE = SimpleBlockSet.buildStone("source_stone", Blocks.STONE).simpleStone();
    public static final SimpleBlockSet<Block> SOURCE_BRICKS = SimpleBlockSet.buildStone("source_bricks", Blocks.STONE_BRICKS).simpleStone();
    public static final SimpleBlockSet<RotatedPillarBlock> SOURCE_LOG = SimpleBlockSet.buildLog("source_log", Blocks.OAK_LOG).addStrippedLog().addWood().addStrippedWood();
    public static final SimpleBlockSet<Block> SOURCE_PLANKS = SimpleBlockSet.buildPlanks("source", Blocks.OAK_PLANKS).simplePlank(BlockSetType.OAK);
    public static final BlockEntry<SaplingBlock> SOURCE_SAPLING = Genesis.L2_REGISTRATE
            .block("source_sapling", properties -> new SaplingBlock(new SourceTreeGrower(), properties))
            .initialProperties(() -> Blocks.OAK_SAPLING)
            .addLayer(() -> RenderType::cutout)
            .blockstate((ctx, pvd) -> pvd.simpleBlock(
                    ctx.get(),
                    pvd.models().cross(ctx.getName(), pvd.modLoc("block/source_sapling")).renderType("cutout")
            ))
            .tag(BlockTags.SAPLINGS)
            .item()
            .tab(CreativeTabRegistry.GENIUS_GENESIS_BLOCK)
            .tag(ItemTags.SAPLINGS)
            .model((ctx, pvd) -> pvd.withExistingParent(ctx.getName(), "item/generated")
                    .texture("layer0", pvd.modLoc("block/source_sapling")))
            .build()
            .register();
    public static final BlockEntry<Block> SOURCE_CRYSTAL_BLOCK = Genesis.L2_REGISTRATE
            .block("source_crystal_block", Block::new)
            .initialProperties(() -> Blocks.AMETHYST_BLOCK)
            .properties(properties -> properties.lightLevel(state -> 9).strength(5.0F, 6.0F).requiresCorrectToolForDrops())
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL)
            .item()
            .tab(CreativeTabRegistry.GENIUS_GENESIS_BLOCK)
            .build()
            .register();

    // 奥术水晶矿石
    public static final BlockEntry<DropExperienceBlock> ARCANE_CRYSTAL_ORE =
            ore("arcane_crystal_ore", Blocks.DIAMOND_ORE, 3.0F, 3.0F, SoundType.STONE, ItemRegistry.ARCANE_CRYSTAL);
    public static final BlockEntry<DropExperienceBlock> ARCANE_CRYSTAL_ORE_DEEPSLATE =
            ore("deepslate_arcane_crystal_ore", Blocks.DEEPSLATE_DIAMOND_ORE, 4.5F, 3.0F, SoundType.DEEPSLATE, ItemRegistry.ARCANE_CRYSTAL);
    public static final BlockEntry<DropExperienceBlock> DIVINE_METAL_ORE =
            ore("divine_metal_ore", Blocks.NETHER_QUARTZ_ORE, 3.0F, 3.0F, SoundType.NETHER_ORE, ItemRegistry.DIVINE_METAL_FRAGMENT);
    public static final BlockEntry<DropExperienceBlock> VIOLET_GALAXY_ORE =
            ore("violet_galaxy_ore", Blocks.END_STONE, 3.0F, 9.0F, SoundType.STONE, ItemRegistry.VIOLET_GALAXY_FRAGMENT);
    public static final BlockEntry<Block> CELESTIAL_SOURCE_BLOCK = Genesis.L2_REGISTRATE
            .block("celestial_source_block", Block::new)
            .properties(properties -> properties.requiresCorrectToolForDrops().strength(20.0F, 9999.0F).sound(SoundType.NETHERITE_BLOCK))
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL)
            .item()
            .tab(CreativeTabRegistry.GENIUS_GENESIS_BLOCK)
            .build()
            .register();

    // 工作台和特殊方块
    public static final BlockEntry<ArcaneWorkbenchBlock> ARCANE_WORKBENCH = Genesis.L2_REGISTRATE
            .block("arcane_workbench", ArcaneWorkbenchBlock::new)
            .properties(properties -> properties.lightLevel(state -> 9).strength(3.0F, 9.0F).requiresCorrectToolForDrops().sound(SoundType.STONE))
            .blockstate((ctx, pvd) -> pvd.simpleBlock(ctx.get(), pvd.models().cubeBottomTop(
                    ctx.getName(),
                    pvd.modLoc("block/arcane_workbench_side"),
                    pvd.modLoc("block/arcane_workbench_bottom"),
                    pvd.modLoc("block/arcane_workbench_top")
            )))
            .item()
            .model((ctx, pvd) -> pvd.withExistingParent(ctx.getName(), pvd.modLoc("block/arcane_workbench")))
            .tab(CreativeTabRegistry.GENIUS_GENESIS_BLOCK)
            .build()
            .register();
    public static final BlockEntry<ArcaneCauldronBlock> ARCANE_CAULDRON = Genesis.L2_REGISTRATE
            .block("arcane_cauldron", ArcaneCauldronBlock::new)
            .initialProperties(() -> Blocks.CAULDRON)
            .properties(properties -> properties.lightLevel(state -> 3).noOcclusion())
            .blockstate((ctx, pvd) -> pvd.simpleBlock(ctx.get(), pvd.models().getExistingFile(pvd.modLoc("block/arcane_cauldron"))))
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .item()
            .model((ctx, pvd) -> pvd.withExistingParent(ctx.getName(), pvd.modLoc("block/arcane_cauldron")))
            .tab(CreativeTabRegistry.GENIUS_GENESIS_BLOCK)
            .build()
            .register();
    public static final BlockEntry<Block> CHAOS_PORTAL_FRAME = Genesis.L2_REGISTRATE
            .block("chaos_portal_frame", Block::new)
            .properties(properties -> properties.strength(-1.0F, 9999.0F).noOcclusion())
            .item()
            .tab(CreativeTabRegistry.GENIUS_GENESIS_BLOCK)
            .build()
            .register();
    public static final BlockEntry<Block> CHAOS_PORTAL = Genesis.L2_REGISTRATE
            .block("chaos_portal", Block::new)
            .properties(properties -> properties.noCollission().randomTicks().strength(-1.0F).sound(SoundType.GLASS).lightLevel(state -> 11).noOcclusion())
            .register();

    private BlockRegistry() {
    }

    public static void register() {
    }

    private static BlockEntry<Block> crystalBlock(String name) {
        return Genesis.L2_REGISTRATE
                .block(name, Block::new)
                .initialProperties(() -> Blocks.AMETHYST_BLOCK)
                .properties(properties -> properties.strength(5.0F, 6.0F).requiresCorrectToolForDrops())
                .tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL)
                .item()
                .tab(CreativeTabRegistry.GENIUS_GENESIS_BLOCK)
                .build()
                .register();
    }

    private static BlockEntry<DropExperienceBlock> ore(
            String name,
            Block baseBlock,
            float hardness,
            float resistance,
            SoundType soundType,
            Supplier<? extends Item> drop
    ) {
        return Genesis.L2_REGISTRATE
                .block(name, properties -> new DropExperienceBlock(properties, UniformInt.of(3, 7)))
                .initialProperties(() -> baseBlock)
                .properties(properties -> properties.lightLevel(state -> 9)
                        .strength(hardness, resistance)
                        .requiresCorrectToolForDrops()
                        .sound(soundType))
                .tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL)
                .loot((tables, block) -> tables.add(block, tables.createOreDrop(block, drop.get())))
                .item()
                .tab(CreativeTabRegistry.GENIUS_GENESIS_BLOCK)
                .build()
                .register();
    }
}
