package miku.united_as_one.genesis.registries;

import io.redspace.ironsspellbooks.api.item.IScroll;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainerMutable;
import miku.united_as_one.genesis.Genesis;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;

import static io.redspace.ironsspellbooks.registries.ItemRegistry.SCROLL;

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
                .icon(BlockRegistry.CELESTIAL_SOURCE_BLOCK::asStack));
        Genesis.L2_REGISTRATE.buildModCreativeTab("material", "itemGroup." + Genesis.MOD_ID + ".material", builder -> builder
                .icon(ItemRegistry.CREATE_STAR::asStack));
        Genesis.L2_REGISTRATE.buildModCreativeTab("equipment", "itemGroup." + Genesis.MOD_ID + ".equipment", builder -> builder
                .icon(ItemRegistry.MITHRIL_SWORD::asStack));
        Genesis.L2_REGISTRATE.buildModCreativeTab("spell_scroll", "itemGroup." + Genesis.MOD_ID + ".spell_scroll", builder -> builder
                .icon(() -> createScrollWithSpell(SpellRegistry.METEOR.get(), 1))
                .displayItems((params, output) -> addScrolls(output,
                        SpellRegistry.METEOR.get(),
                        SpellRegistry.I_FLY.get(),
                        SpellRegistry.AMENOFUWARI_SPELL.get(),
                        SpellRegistry.BLOOD_CONTROL_SPELL.get(),
                        SpellRegistry.BLOOD_FRENZY_SPELL.get(),
                        SpellRegistry.BLOOD_RITUAL_SPELL.get(),
                        SpellRegistry.BLOOD_WAR_SPELL.get(),
                        SpellRegistry.GUTRENDER_PUNCTURE.get()
                )));

        Genesis.L2_REGISTRATE.defaultCreativeTab(CreativeModeTabs.SEARCH);
    }

    private static void addScrolls(CreativeModeTab.Output output, AbstractSpell... spells) {
        for (AbstractSpell spell : spells) {
            for (int level = spell.getMinLevel(); level <= spell.getMaxLevel(); level++) {
                output.accept(createScrollWithSpell(spell, level));
            }
        }
    }

    private static ItemStack createScrollWithSpell(AbstractSpell spell, int level) {
        ItemStack scrollStack = new ItemStack(SCROLL.get());
        if (scrollStack.getItem() instanceof IScroll) {
            ISpellContainerMutable container = ISpellContainer.create(1, false, false).mutableCopy();
            container.addSpellAtIndex(spell, level, 0, true);
            ISpellContainer.set(scrollStack, container.toImmutable());
        }
        return scrollStack;
    }
}
