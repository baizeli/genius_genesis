package miku.united_as_one.genesis.mixin.ironsspellbooks.gui.scroll_forge;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.gui.scroll_forge.ScrollForgeMenu;
import io.redspace.ironsspellbooks.item.InkItem;
import miku.united_as_one.genesis.content.spell.SpellRarityLevelResolver;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ScrollForgeMenu.class, remap = false)
public abstract class ScrollForgeMenuInnateMixin {
    @Shadow @Final private Slot inkSlot;
    @Shadow @Final private Slot resultSlot;

    @Inject(method = "setupResultSlot", at = @At("HEAD"), cancellable = true)
    private void genesis$rejectInnateInkForNonInnateSpells(AbstractSpell spell, CallbackInfo ci) {
        if (inkSlot.getItem().getItem() instanceof InkItem ink
                && SpellRarityLevelResolver.findFirstExactLevel(spell, ink.getRarity()) < 1) {
            resultSlot.set(ItemStack.EMPTY);
            ci.cancel();
        }
    }

    @Redirect(
            method = "setupResultSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lio/redspace/ironsspellbooks/api/spells/AbstractSpell;getMinLevelForRarity(Lio/redspace/ironsspellbooks/api/spells/SpellRarity;)I"
            )
    )
    private int genesis$useExactInkRarityLevel(AbstractSpell spell, io.redspace.ironsspellbooks.api.spells.SpellRarity ignored) {
        return inkSlot.getItem().getItem() instanceof InkItem ink
                ? SpellRarityLevelResolver.findFirstExactLevel(spell, ink.getRarity())
                : 0;
    }
}
