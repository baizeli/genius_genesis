package miku.united_as_one.genesis.combat.protectedhealth;

import sun.misc.Unsafe;
import java.lang.reflect.Field;

public final class ProtectedUnsafe {
    private static final Unsafe INSTANCE = obtain();
    private ProtectedUnsafe() {}
    public static Unsafe instance() { return INSTANCE; }
    public static long objectFieldOffset(Field field) { return INSTANCE.objectFieldOffset(field); }
    public static Object getObject(Object owner, long offset) { return INSTANCE.getObject(owner, offset); }
    public static void putObject(Object owner, long offset, Object value) { INSTANCE.putObject(owner, offset, value); }
    public static long getLong(Object owner, long offset) { return INSTANCE.getLongVolatile(owner, offset); }
    public static void putLong(Object owner, long offset, long value) { INSTANCE.putLongVolatile(owner, offset, value); }
    private static Unsafe obtain() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
