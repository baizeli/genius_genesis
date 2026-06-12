package miku.united_as_one.genesis.registries;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.spell.chaos.*;
import miku.united_as_one.genesis.spell.MeteorSpell;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static io.redspace.ironsspellbooks.api.registry.SpellRegistry.SPELL_REGISTRY_KEY;

public final class SpellRegistry {
    private static final DeferredRegister<AbstractSpell> SPELLS =
            DeferredRegister.create(SPELL_REGISTRY_KEY, Genesis.MOD_ID);

    public static final RegistryObject<AbstractSpell> METEOR = register(new MeteorSpell());
    public static final RegistryObject<AbstractSpell> AMENOFUWARI_SPELL= register(new AmenofuwariSpell());
    public static final RegistryObject<AbstractSpell> BLOOD_CONTROL_SPELL = register(new BloodControlSpell());
    public static final RegistryObject<AbstractSpell> BLOOD_FRENZY_SPELL = register(new BloodFrenzySpell());
    public static final RegistryObject<AbstractSpell> BLOOD_RITUAL_SPELL = register(new BloodRitualSpell());
    public static final RegistryObject<AbstractSpell> BLOOD_WAR_SPELL = register(new BloodWarSpell());
    public static final RegistryObject<AbstractSpell> GUTRENDER_PUNCTURE = register(new GutrenderPunctureSpell());

    private SpellRegistry() {
    }

    public static void register(IEventBus modBus) {
        SPELLS.register(modBus);
    }

    private static RegistryObject<AbstractSpell> register(AbstractSpell spell) {
        return SPELLS.register(spell.getSpellName(), () -> spell);
    }
}
