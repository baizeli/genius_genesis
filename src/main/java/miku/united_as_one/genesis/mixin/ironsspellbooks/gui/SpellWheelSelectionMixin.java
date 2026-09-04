package miku.united_as_one.genesis.mixin.ironsspellbooks.gui;

import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.gui.overlays.SpellWheelOverlay;
import miku.united_as_one.genesis.client.spellhud.SpellCardAnimationState;
import miku.united_as_one.genesis.client.spellhud.SpellCardHudClientEvents;
import miku.united_as_one.genesis.client.spellhud.SpellCardHudOverlay;
import net.minecraft.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = SpellWheelOverlay.class, remap = false)
public abstract class SpellWheelSelectionMixin {
    @ModifyArg(method = "close()V", at = @At(value = "INVOKE",
            target = "Lio/redspace/ironsspellbooks/api/magic/SpellSelectionManager;makeSelection(I)V"),
            index = 0, require = 1, remap = false)
    private int geniusGenesis$recordWheelSelection(int nextIndex) {
        var manager = ClientMagicData.getSpellSelectionManager();
        int previousIndex = manager.getGlobalSelectionIndex();
        SpellCardHudClientEvents.animation().signalSelection(
                SpellCardAnimationState.SelectionSource.WHEEL,
                previousIndex, nextIndex, manager.getSpellCount(), Util.getMillis());
        SpellCardHudOverlay.playSelectionSound(previousIndex, nextIndex, manager.getSpellCount());
        return nextIndex;
    }
}
