package miku.united_as_one.genesis.content.spell;

import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import miku.united_as_one.genesis.Genesis;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Arrays;

public final class DevSpellRarityFallback {
    private static volatile boolean attempted;

    private DevSpellRarityFallback() { }

    public static synchronized void ensureInnateExists() {
        if (hasInnate() || attempted) return;
        attempted = true;
        try {
            Unsafe unsafe = unsafe();
            SpellRarity innate = (SpellRarity) unsafe.allocateInstance(SpellRarity.class);
            putObject(unsafe, innate, Enum.class.getDeclaredField("name"), "INNATE");
            putInt(unsafe, innate, Enum.class.getDeclaredField("ordinal"), 5);
            putInt(unsafe, innate, SpellRarity.class.getDeclaredField("value"), 5);

            Field displays = SpellRarity.class.getDeclaredField("DISPLAYS");
            Object displayArray = unsafe.getObject(SpellRarity.LEGENDARY, unsafe.objectFieldOffset(displays));
            putObject(unsafe, innate, displays, displayArray);

            Field valuesField = Arrays.stream(SpellRarity.class.getDeclaredFields())
                    .filter(field -> field.getType() == SpellRarity[].class)
                    .filter(field -> field.isSynthetic() || field.getName().contains("VALUES"))
                    .findFirst()
                    .orElseThrow();
            Object base = unsafe.staticFieldBase(valuesField);
            long offset = unsafe.staticFieldOffset(valuesField);
            SpellRarity[] current = (SpellRarity[]) unsafe.getObject(base, offset);
            SpellRarity[] extended = Arrays.copyOf(current, current.length + 1);
            extended[current.length] = innate;
            unsafe.putObject(base, offset, extended);
            clearEnumCaches(unsafe);
            Genesis.LOGGER.info("Installed INNATE spell rarity through the Genesis mixin compatibility path");
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to install development INNATE rarity fallback", exception);
        }
    }

    private static boolean hasInnate() {
        return Arrays.stream(SpellRarity.values()).anyMatch(rarity -> rarity.name().equals("INNATE"));
    }

    private static Unsafe unsafe() throws ReflectiveOperationException {
        Field field = Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        return (Unsafe) field.get(null);
    }

    private static void putObject(Unsafe unsafe, Object owner, Field field, Object value) {
        unsafe.putObject(owner, unsafe.objectFieldOffset(field), value);
    }

    private static void putInt(Unsafe unsafe, Object owner, Field field, int value) {
        unsafe.putInt(owner, unsafe.objectFieldOffset(field), value);
    }

    private static void clearEnumCaches(Unsafe unsafe) throws ReflectiveOperationException {
        for (String name : new String[]{"enumConstants", "enumConstantDirectory"}) {
            Field field = Class.class.getDeclaredField(name);
            unsafe.putObject(SpellRarity.class, unsafe.objectFieldOffset(field), null);
        }
    }
}
