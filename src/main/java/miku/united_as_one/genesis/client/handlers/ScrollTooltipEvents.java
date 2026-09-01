package miku.united_as_one.genesis.client.handlers;

import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.item.Scroll;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.client.render.ScrollTooltipBackgroundRenderer;
import miku.united_as_one.genesis.registries.SpellSchoolRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Genesis.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ScrollTooltipEvents {
    private ScrollTooltipEvents() {
    }

    @SubscribeEvent
    public static void onTooltipColor(RenderTooltipEvent.Color event) {
        int cosmicType = resolveCosmicType(event.getItemStack());
        if (cosmicType == 0 || !ScrollTooltipBackgroundRenderer.isAvailable()) {
            return;
        }

        int width = 0;
        int height = event.getComponents().size() == 1 ? -2 : 0;
        for (ClientTooltipComponent component : event.getComponents()) {
            width = Math.max(width, component.getWidth(event.getFont()));
            height += component.getHeight();
        }
        if (width <= 0 || height <= 0) {
            return;
        }

        event.setBackground(0);
        GuiGraphics graphics = event.getGraphics();
        graphics.pose().pushPose();
        graphics.pose().translate(0.0F, 0.0F, 400.0F);
        ScrollTooltipBackgroundRenderer.render(
                graphics.pose(), graphics.bufferSource(), width + 3, height + 3,
                event.getX() + width / 2.0, event.getY() + height / 2.0,
                15728880, cosmicType);
        graphics.pose().popPose();
    }

    private static int resolveCosmicType(ItemStack stack) {
        if (!(stack.getItem() instanceof Scroll)) {
            return 0;
        }
        try {
            ISpellContainer container = ISpellContainer.getOrCreate(stack);
            for (var slot : container.getActiveSpells()) {
                var school = slot.getSpell().getSchoolType();
                if (school.equals(SpellSchoolRegistry.CHAOS.get())) {
                    return 14;
                }
                if (school.equals(SpellSchoolRegistry.CELESTIAL_SOURCE.get())) {
                    return 9;
                }
            }
        } catch (RuntimeException ignored) {
            // Invalid or incomplete scroll data keeps the vanilla tooltip background.
        }
        return 0;
    }
}
