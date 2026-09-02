package miku.united_as_one.genesis.content.item.spell;

import io.redspace.ironsspellbooks.item.SpellBook;
import java.util.List;
import miku.united_as_one.genesis.content.item.tooltip.SpellBookTooltips;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LightningSpellBook extends SpellBook {
    public LightningSpellBook() {
        super(10);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        SpellBookTooltips.addLightningDescription(tooltip);
    }
}
