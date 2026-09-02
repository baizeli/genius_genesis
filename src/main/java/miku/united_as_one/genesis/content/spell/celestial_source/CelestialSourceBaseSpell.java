package miku.united_as_one.genesis.content.spell.celestial_source;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import miku.united_as_one.genesis.registries.SpellSchoolRegistry;

public abstract class CelestialSourceBaseSpell extends AbstractSpell {
    @Override
    public SchoolType getSchoolType() {
        return SpellSchoolRegistry.CELESTIAL_SOURCE.get();
    }
}
