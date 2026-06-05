package miku.united_as_one.genesis.client.tooltip;

import miku.united_as_one.genesis.item.GenesisArmorMaterials;
import miku.united_as_one.genesis.item.armor.GenesisGeoArmorItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;

import java.util.List;

public final class GenesisArmorTooltips {
    private static final ChatFormatting DIVINE = ChatFormatting.GOLD;
    private static final ChatFormatting VIOLET = ChatFormatting.LIGHT_PURPLE;
    private static final ChatFormatting CHAOS = ChatFormatting.DARK_PURPLE;
    private static final ChatFormatting CELESTIAL = ChatFormatting.AQUA;
    private static final ChatFormatting ARCANE = ChatFormatting.BLUE;

    private GenesisArmorTooltips() {
    }

    public static void append(GenesisGeoArmorItem armor, List<Component> tooltip) {
        GenesisArmorMaterials material = armor.genesisMaterial();
        ChatFormatting color = color(material);

        tooltip.add(Component.empty());
        if (hasPieceEffect(armor)) {
            tooltip.add(Component.translatable("tooltip.genius_genesis.armor.passive").withStyle(color));
            tooltip.add(effect("tooltip.genius_genesis.armor.night_vision", color));
            tooltip.add(Component.empty());
        }

        tooltip.add(Component.translatable("tooltip.genius_genesis.armor.mana_repair").withStyle(color));
        tooltip.add(effect("tooltip.genius_genesis.armor.mana_repair.line", color));
        tooltip.add(effect("tooltip.genius_genesis.armor.mana_repair.line_2", color));
        tooltip.add(Component.empty());

        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.genius_genesis.armor.set_effects").withStyle(color));
            appendSetEffects(material, tooltip, color);
        } else {
            tooltip.add(Component.translatable("tooltip.genius_genesis.armor.hold_shift").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    private static boolean hasPieceEffect(GenesisGeoArmorItem armor) {
        return armor.getType() == ArmorItem.Type.HELMET
                && (armor.genesisMaterial() == GenesisArmorMaterials.DIVINE_METAL
                || armor.genesisMaterial() == GenesisArmorMaterials.VIOLET_ZENITH
                || armor.genesisMaterial() == GenesisArmorMaterials.CHAOS_SPELL);
    }

    private static void appendSetEffects(GenesisArmorMaterials material, List<Component> tooltip, ChatFormatting color) {
        switch (material) {
            case DIVINE_METAL -> {
                tooltip.add(effect("tooltip.genius_genesis.armor.divine.set_1", color));
                tooltip.add(effect("tooltip.genius_genesis.armor.divine.set_2", color));
                tooltip.add(effect("tooltip.genius_genesis.armor.divine.set_3", color));
                tooltip.add(effect("tooltip.genius_genesis.armor.divine.set_4", color));
            }
            case VIOLET_ZENITH -> {
                tooltip.add(effect("tooltip.genius_genesis.armor.violet.set_1", color));
                tooltip.add(effect("tooltip.genius_genesis.armor.violet.set_2", color));
                tooltip.add(effect("tooltip.genius_genesis.armor.violet.set_3", color));
                tooltip.add(effect("tooltip.genius_genesis.armor.violet.set_4", color));
            }
            case CELESTIAL_SOURCE_SPELL -> {
                tooltip.add(effect("tooltip.genius_genesis.armor.celestial.set_1", color));
                tooltip.add(effect("tooltip.genius_genesis.armor.celestial.set_2", color));
            }
            case CHAOS_SPELL -> tooltip.add(effect("tooltip.genius_genesis.armor.chaos.set_1", color));
            case ARCANE_CRYSTAL -> tooltip.add(effect("tooltip.genius_genesis.armor.arcane.set_1", color));
        }
    }

    private static Component effect(String key, ChatFormatting color) {
        return Component.literal("- ").withStyle(color)
                .append(Component.translatable(key).withStyle(ChatFormatting.GRAY));
    }

    private static ChatFormatting color(GenesisArmorMaterials material) {
        return switch (material) {
            case DIVINE_METAL -> DIVINE;
            case VIOLET_ZENITH -> VIOLET;
            case CHAOS_SPELL -> CHAOS;
            case CELESTIAL_SOURCE_SPELL -> CELESTIAL;
            case ARCANE_CRYSTAL -> ARCANE;
        };
    }
}
