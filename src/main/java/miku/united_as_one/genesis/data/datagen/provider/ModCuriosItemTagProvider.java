package miku.united_as_one.genesis.data.datagen.provider;

import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.registries.ItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ModCuriosItemTagProvider extends ItemTagsProvider {
    private static final TagKey<Item> SPELLBOOK = curios("spellbook");
    private static final TagKey<Item> RING = curios("ring");
    private static final TagKey<Item> MAGIC_GUIDE = curios("magic_guide");
    private static final TagKey<Item> CROWN = curios("crown");

    public ModCuriosItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, CompletableFuture.completedFuture(TagLookup.empty()), Genesis.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(SPELLBOOK)
                .add(
                        ItemRegistry.CHAOS_SPELL_BOOK.get(),
                        ItemRegistry.CELESTIAL_SOURCE_SPELL_BOOK.get(),
                        ItemRegistry.LIGHTNING_SPELL_BOOK.get(),
                        ItemRegistry.DISK_SPELL_BOOK.get()
                );

        tag(RING)
                .add(ItemRegistry.ETERNAL_RING.get());

        tag(MAGIC_GUIDE)
                .add(
                        ItemRegistry.ENDER_RUNE_PLUS.get(),
                        ItemRegistry.FIRE_RUNE_PLUS.get(),
                        ItemRegistry.HOLY_RUNE_PLUS.get(),
                        ItemRegistry.ICE_RUNE_PLUS.get(),
                        ItemRegistry.BLOOD_RUNE_PLUS.get(),
                        ItemRegistry.LIGHTNING_RUNE_PLUS.get(),
                        ItemRegistry.NATURE_RUNE_PLUS.get(),
                        ItemRegistry.ELDRITCH_RUNE_PLUS.get(),
                        ItemRegistry.LAO_WANG_237.get()
                );

        tag(CROWN)
                .add(ItemRegistry.GENESIS_CURSE.get());
    }

    private static TagKey<Item> curios(String name) {
        return TagKey.create(Registries.ITEM, new ResourceLocation("curios", name));
    }
}
