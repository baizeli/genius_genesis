package miku.united_as_one.genesis.content.item.tooltip;

import io.redspace.ironsspellbooks.api.item.curios.AffinityData;
import io.redspace.ironsspellbooks.util.TooltipsUtils;
import java.util.List;
import miku.united_as_one.genesis.Genesis;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class SpellBookTooltips {
    private SpellBookTooltips() {
    }

    public static void addAffinity(ItemStack stack, List<Component> tooltip) {
        AffinityData affinityData = AffinityData.getAffinityData(stack);
        if (affinityData.affinityData().isEmpty()) {
            return;
        }
        int index = TooltipsUtils.indexOfComponent(tooltip, "tooltip.irons_spellbooks.spellbook_spell_count");
        tooltip.addAll(index < 0 ? tooltip.size() : index + 1, affinityData.getDescriptionComponent());
    }

    public static void addLightningDescription(List<Component> tooltip) {
        tooltip.add(Component.translatable("tooltip." + Genesis.MOD_ID + ".lightning_spell_book.description_1"));
        tooltip.add(Component.translatable("tooltip." + Genesis.MOD_ID + ".lightning_spell_book.description_2"));
    }

    public static void addDiskDescription(List<Component> tooltip) {
        tooltip.add(Component.translatable("tooltip." + Genesis.MOD_ID + ".disk_spell_book.description"));
    }
}
