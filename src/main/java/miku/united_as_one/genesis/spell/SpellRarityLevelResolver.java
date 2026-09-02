package miku.united_as_one.genesis.spell;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;

public final class SpellRarityLevelResolver {
    private SpellRarityLevelResolver() { }

    public static int findFirstExactLevel(AbstractSpell spell, SpellRarity rarity) {
        for (int level = spell.getMinLevel(); level <= spell.getMaxLevel(); level++) {
            if (spell.getRarity(level) == rarity) return level;
        }
        return -1;
    }
}
