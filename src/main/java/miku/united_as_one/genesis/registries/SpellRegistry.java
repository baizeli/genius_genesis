package miku.united_as_one.genesis.registries;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.spell.chaos.*;
import miku.united_as_one.genesis.spell.celestial_source.IFlySpell;
import miku.united_as_one.genesis.spell.MeteorSpell;
import miku.united_as_one.genesis.spell.eldritch.SilentAbyssalRealmSpell;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static io.redspace.ironsspellbooks.api.registry.SpellRegistry.SPELL_REGISTRY_KEY;

public final class SpellRegistry {
    private static final DeferredRegister<AbstractSpell> SPELLS =
            DeferredRegister.create(SPELL_REGISTRY_KEY, Genesis.MOD_ID);

    // 星源
    public static final RegistryObject<AbstractSpell> I_FLY = register(new IFlySpell());
    public static final RegistryObject<AbstractSpell> METEOR = register(new MeteorSpell());

    // 混沌
    public static final RegistryObject<AbstractSpell> AMENOFUWARI_SPELL= register(new AmenofuwariSpell());
    public static final RegistryObject<AbstractSpell> BLOOD_CONTROL_SPELL = register(new BloodControlSpell());
    public static final RegistryObject<AbstractSpell> BLOOD_FRENZY_SPELL = register(new BloodFrenzySpell());
    public static final RegistryObject<AbstractSpell> BLOOD_RITUAL_SPELL = register(new BloodRitualSpell());
    public static final RegistryObject<AbstractSpell> BLOOD_WAR_SPELL = register(new BloodWarSpell());
    public static final RegistryObject<AbstractSpell> CONFUSION_SPELL = register(new ConfusionSpell());
    public static final RegistryObject<AbstractSpell> GUTRENDER_PUNCTURE = register(new GutrenderPunctureSpell());
    public static final RegistryObject<AbstractSpell> REVERSE_PLAGUE_SPELL = register(new ReversePlagueSpell());
    public static final RegistryObject<AbstractSpell> SHATTER_FIST = register(new ShatterFistSpell());
    public static final RegistryObject<AbstractSpell> SIPHON_SPELL = register(new SiphonSpell());
    public static final RegistryObject<AbstractSpell> WARPED_BARRIER_SPELL = register(new WarpedBarrierSpell());

    // 邪术
    public static final RegistryObject<AbstractSpell> SILENT_ABYSSAL_REALM = register(new SilentAbyssalRealmSpell());

    private SpellRegistry() {
    }

    public static void register(IEventBus modBus) {
        SPELLS.register(modBus);
    }

    private static RegistryObject<AbstractSpell> register(AbstractSpell spell) {
        return SPELLS.register(spell.getSpellName(), () -> spell);
    }
}
