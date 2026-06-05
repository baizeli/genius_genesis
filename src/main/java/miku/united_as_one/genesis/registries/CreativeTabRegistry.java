package miku.united_as_one.genesis.registries;

import miku.united_as_one.genesis.Genesis;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;

public final class CreativeTabRegistry {

    public static final ResourceKey<CreativeModeTab> GENIUS_GENESIS_BLOCK = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB, new ResourceLocation(Genesis.MOD_ID, "block"));
    public static final ResourceKey<CreativeModeTab> GENIUS_GENESIS_MATERIAL = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB, new ResourceLocation(Genesis.MOD_ID, "material"));
    public static final ResourceKey<CreativeModeTab> GENIUS_GENESIS_EQUIPMENT = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB, new ResourceLocation(Genesis.MOD_ID, "equipment"));
    public static final ResourceKey<CreativeModeTab> GENIUS_GENESIS_SPELL_SCROLL = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB, new ResourceLocation(Genesis.MOD_ID, "spell_scroll"));

    // 兼容旧工具类里的命名，等 SimpleBlockSet 完全本地化后再统一改名。
    public static final ResourceKey<CreativeModeTab> IRON_SPELLS_GENESIS_BLOCK = GENIUS_GENESIS_BLOCK;

    private CreativeTabRegistry() {
    }

    public static void register() {
        Genesis.L2_REGISTRATE.buildModCreativeTab("block", "itemGroup." + Genesis.MOD_ID + ".block", builder -> builder
                .icon(() -> BlockRegistry.CELESTIAL_SOURCE_BLOCK.asStack()));
        Genesis.L2_REGISTRATE.buildModCreativeTab("material", "itemGroup." + Genesis.MOD_ID + ".material", builder -> builder
                .icon(() -> ItemRegistry.CREATE_STAR.asStack()));
        Genesis.L2_REGISTRATE.buildModCreativeTab("equipment", "itemGroup." + Genesis.MOD_ID + ".equipment", builder -> builder
                .icon(() -> ItemRegistry.MITHRIL_SWORD.asStack()));
        Genesis.L2_REGISTRATE.buildModCreativeTab("spell_scroll", "itemGroup." + Genesis.MOD_ID + ".spell_scroll", builder -> builder
                .icon(() -> ItemRegistry.CHAOS_SPELL_BOOK.asStack()));

        Genesis.L2_REGISTRATE.defaultCreativeTab(CreativeModeTabs.SEARCH);
    }
}
