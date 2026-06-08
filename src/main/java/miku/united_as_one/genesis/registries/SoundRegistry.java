package miku.united_as_one.genesis.registries;

import miku.united_as_one.genesis.Genesis;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class SoundRegistry {
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Genesis.MOD_ID);

    public static final RegistryObject<SoundEvent> GIRL_A = sound("girl_a");
    public static final RegistryObject<SoundEvent> EVASION = sound("evasion");
    public static final RegistryObject<SoundEvent> VAN_SH_HIT = sound("van_sh_hit");
    public static final RegistryObject<SoundEvent> CUTE_HIT = sound("cute_hit");

    public static final RegistryObject<SoundEvent> CHAOS_CAST = sound("chaos_cast");
    public static final RegistryObject<SoundEvent> CELESTIAL_SOURCE_CAST = sound("celestia_source");
    public static final RegistryObject<SoundEvent> CULINARY_CAST = sound("culinary");

    public static final RegistryObject<SoundEvent> BLOOD_BOSS_MUSIC = sound("battle_sanctuary");

    private SoundRegistry() {
    }

    public static void register(IEventBus modBus) {
        SOUND_EVENTS.register(modBus);
    }

    private static RegistryObject<SoundEvent> sound(String id) {
        return SOUND_EVENTS.register(id, () -> SoundEvent.createVariableRangeEvent(Genesis.id(id)));
    }
}
