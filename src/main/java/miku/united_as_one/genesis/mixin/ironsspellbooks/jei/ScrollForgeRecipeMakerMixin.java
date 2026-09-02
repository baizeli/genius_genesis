package miku.united_as_one.genesis.mixin.ironsspellbooks.jei;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.jei.ScrollForgeRecipeMaker;
import miku.united_as_one.genesis.content.spell.SpellRarityLevelResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ScrollForgeRecipeMaker.class, remap = false)
public abstract class ScrollForgeRecipeMakerMixin {
    @Redirect(
            method = "lambda$getRecipes$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lio/redspace/ironsspellbooks/api/spells/AbstractSpell;getMinLevelForRarity(Lio/redspace/ironsspellbooks/api/spells/SpellRarity;)I"
            )
    )
    private static int genesis$showOnlyExactInkRarity(AbstractSpell spell, SpellRarity rarity) {
        return SpellRarityLevelResolver.findFirstExactLevel(spell, rarity);
    }
}
