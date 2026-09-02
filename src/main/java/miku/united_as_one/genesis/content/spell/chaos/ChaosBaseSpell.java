package miku.united_as_one.genesis.content.spell.chaos;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import miku.united_as_one.genesis.registries.SpellSchoolRegistry;

public abstract class ChaosBaseSpell extends AbstractSpell {
    @Override
    public SchoolType getSchoolType() {
        return SpellSchoolRegistry.CHAOS.get();
    }
}
