package miku.united_as_one.genesis.registries;

import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.data.damage.DamageTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class SpellSchoolRegistry {
    private static final DeferredRegister<SchoolType> SCHOOLS =
            DeferredRegister.create(SchoolRegistry.SCHOOL_REGISTRY_KEY, Genesis.MOD_ID);

    public static final ResourceLocation CHAOS_RESOURCE = Genesis.id("chaos");
    public static final TagKey<Item> CHAOS_FOCUS = ItemTags.create(Genesis.id("chaos_focus"));
    public static final ResourceLocation CELESTIAL_SOURCE_RESOURCE = Genesis.id("celestial_source");
    public static final TagKey<Item> CELESTIAL_SOURCE_FOCUS = ItemTags.create(Genesis.id("celestial_source_focus"));

    public static final RegistryObject<SchoolType> CHAOS = SCHOOLS.register("chaos", () ->
            new SchoolType(
                    CHAOS_RESOURCE,
                    CHAOS_FOCUS,
                    Component.translatable("school." + Genesis.MOD_ID + ".chaos").withStyle(ChatFormatting.DARK_PURPLE),
                    SpellAttributesRegistry.CHAOS_SPELL_POWER,
                    SpellAttributesRegistry.CHAOS_MAGIC_RESIST,
                    SoundRegistry.CHAOS_CAST,
                    DamageTypes.CHAOS_MAGIC,
                    true,
                    false
            ));

    public static final RegistryObject<SchoolType> CELESTIAL_SOURCE = SCHOOLS.register("celestial_source", () ->
            new SchoolType(
                    CELESTIAL_SOURCE_RESOURCE,
                    CELESTIAL_SOURCE_FOCUS,
                    Component.translatable("school." + Genesis.MOD_ID + ".celestial_source").withStyle(ChatFormatting.DARK_AQUA),
                    SpellAttributesRegistry.CELESTIAL_SOURCE_SPELL_POWER,
                    SpellAttributesRegistry.CELESTIAL_SOURCE_MAGIC_RESIST,
                    SoundRegistry.CELESTIAL_SOURCE_CAST,
                    DamageTypes.CELESTIAL_SOURCE_MAGIC,
                    true,
                    false
            ));

    private SpellSchoolRegistry() {
    }

    public static void register(IEventBus modBus) {
        SCHOOLS.register(modBus);
    }
}
