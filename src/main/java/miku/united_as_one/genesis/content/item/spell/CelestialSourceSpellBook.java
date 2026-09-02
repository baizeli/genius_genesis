package miku.united_as_one.genesis.content.item.spell;

import io.redspace.ironsspellbooks.item.SpellBook;
import java.util.List;
import miku.bai_ze_li.genesis.api.render.tooltip.ITooltipParticleItem;
import miku.bai_ze_li.genesis.api.render.tooltip.TooltipParticleSystem;
import miku.united_as_one.genesis.client.tooltip.GenesisTooltipParticles;
import miku.united_as_one.genesis.content.item.tooltip.SpellBookTooltips;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CelestialSourceSpellBook extends SpellBook implements ITooltipParticleItem {
    public CelestialSourceSpellBook() {
        super(15);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        SpellBookTooltips.addAffinity(stack, tooltip);
    }

    @Override
    public TooltipParticleSystem.ParticleConfig getParticleConfig() {
        return GenesisTooltipParticles.celestialSourceStars();
    }
}
