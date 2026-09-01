package miku.united_as_one.genesis.spell;

import io.redspace.ironsspellbooks.api.spells.SpellRarity;

public final class InnateRarity {
    private InnateRarity() { }
    public static SpellRarity get() {
        DevSpellRarityFallback.ensureInnateExists();
        return SpellRarity.valueOf("INNATE");
    }
    public static boolean is(SpellRarity rarity) { return rarity != null && rarity.getValue() == 5; }
}
