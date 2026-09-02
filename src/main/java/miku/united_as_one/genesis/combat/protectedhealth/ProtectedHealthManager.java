package miku.united_as_one.genesis.combat.protectedhealth;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.security.SecureRandom;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public final class ProtectedHealthManager {
    private static final String TRUSTED = "miku.united_as_one.genesis.combat.protectedhealth.";
    private static final ClassLoader LOADER = ProtectedHealthManager.class.getClassLoader();
    private static final StackWalker WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final long TAG_KEY = 0x6A09E667F3BCC909L;
    private static final long NONCE_OFFSET;
    private static final MethodHandle READ, WRITE;
    static {
        ProtectedUnsafe.instance();
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            Class<?> gate = lookup.defineHiddenClass(gateBytes(), true, MethodHandles.Lookup.ClassOption.NESTMATE).lookupClass();
            READ = lookup.findStatic(gate, "read", MethodType.methodType(float.class, Object.class));
            WRITE = lookup.findStatic(gate, "write", MethodType.methodType(void.class, Object.class, float.class));
            Field nonce = ProtectedZombieDummy.class.getDeclaredField("protectedNonce");
            NONCE_OFFSET = ProtectedUnsafe.objectFieldOffset(nonce);
        } catch (ReflectiveOperationException e) { throw new ExceptionInInitializerError(e); }
    }
    private ProtectedHealthManager() {}
    public static float read(Object entity) { try { return (float) READ.invokeExact(entity); } catch(Throwable e){ throw fail(e); } }
    public static void initialize(Object entity, float max) {
        if (entity instanceof ProtectedZombieDummy dummy) {
            ProtectedUnsafe.putLong(dummy, NONCE_OFFSET, newNonce());
        }
        write(entity, ReverseHealthMath.full(max));
    }
    public static void damage(Object entity, float amount) { write(entity, ReverseHealthMath.damage(read(entity), amount)); }
    public static void heal(Object entity, float amount, float max) { write(entity, ReverseHealthMath.heal(read(entity), amount, max)); }
    private static void write(Object entity,float value){try{WRITE.invokeExact(entity,value);}catch(Throwable e){throw fail(e);}}
    private static float read0(Object entity){
        if(!trusted() || !(entity instanceof ProtectedHealthCarrier c)) return 0;
        try {
            String[] fields = ProtectedHealthCipher.decrypt(c.acq()).split(":", -1);
            if (fields.length != 3) return recover(c, entity);
            long nonce = Long.parseUnsignedLong(fields[0], 16);
            int bits = Integer.parseUnsignedInt(fields[1], 16);
            long tag = Long.parseUnsignedLong(fields[2], 16);
            if (nonce != nonceOf(entity) || tag != tag(nonce, bits)) return recover(c, entity);
            float value = Float.intBitsToFloat(bits);
            return Float.isFinite(value) && value <= 0 && value >= -c.cap() ? value : recover(c, entity);
        } catch (RuntimeException e) {
            return recover(c, entity);
        }
    }
    private static void write0(Object entity,float value){
        if(trusted() && entity instanceof ProtectedHealthCarrier c) {
            long nonce = nonceOf(entity);
            if (nonce == 0) {
                nonce = newNonce();
                nonceOf(entity, nonce);
            }
            int bits = Float.floatToRawIntBits(value);
            String plain = Long.toUnsignedString(nonce, 16) + ':' + Integer.toUnsignedString(bits, 16) + ':' + Long.toUnsignedString(tag(nonce, bits), 16);
            c.upd(ProtectedHealthCipher.encrypt(plain));
        }
    }
    private static float recover(ProtectedHealthCarrier carrier, Object entity) {
        long nonce = newNonce();
        nonceOf(entity, nonce);
        float full = ReverseHealthMath.full(carrier.cap());
        int bits = Float.floatToRawIntBits(full);
        String plain = Long.toUnsignedString(nonce, 16) + ':' + Integer.toUnsignedString(bits, 16) + ':' + Long.toUnsignedString(tag(nonce, bits), 16);
        carrier.upd(ProtectedHealthCipher.encrypt(plain));
        return full;
    }
    private static long nonceOf(Object entity) { return entity instanceof ProtectedZombieDummy ? ProtectedUnsafe.getLong(entity, NONCE_OFFSET) : 0; }
    private static void nonceOf(Object entity, long nonce) { if (entity instanceof ProtectedZombieDummy) ProtectedUnsafe.putLong(entity, NONCE_OFFSET, nonce); }
    private static long newNonce() { long value; do value = RANDOM.nextLong(); while (value == 0); return value; }
    private static long tag(long nonce, int bits) {
        long z = nonce ^ TAG_KEY ^ Integer.toUnsignedLong(bits);
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }
    private static boolean trusted(){return WALKER.walk(s->s.map(StackWalker.StackFrame::getDeclaringClass).filter(t->t!=ProtectedHealthManager.class&&!t.isHidden()&&!t.getName().startsWith("java.lang.invoke.")).findFirst().map(t->t.getName().startsWith(TRUSTED)&&t.getClassLoader()==LOADER).orElse(false));}
    private static byte[] gateBytes() {
        String gate = "miku/united_as_one/genesis/combat/protectedhealth/ProtectedHealthGate";
        String owner = "miku/united_as_one/genesis/combat/protectedhealth/ProtectedHealthManager";
        ClassWriter writer = new ClassWriter(0);
        writer.visit(Opcodes.V17, Opcodes.ACC_FINAL | Opcodes.ACC_SUPER | Opcodes.ACC_SYNTHETIC, gate, null, "java/lang/Object", null);
        MethodVisitor read = writer.visitMethod(Opcodes.ACC_STATIC, "read", "(Ljava/lang/Object;)F", null, null);
        read.visitCode(); read.visitVarInsn(Opcodes.ALOAD, 0); read.visitMethodInsn(Opcodes.INVOKESTATIC, owner, "read0", "(Ljava/lang/Object;)F", false); read.visitInsn(Opcodes.FRETURN); read.visitMaxs(1, 1); read.visitEnd();
        MethodVisitor write = writer.visitMethod(Opcodes.ACC_STATIC, "write", "(Ljava/lang/Object;F)V", null, null);
        write.visitCode(); write.visitVarInsn(Opcodes.ALOAD, 0); write.visitVarInsn(Opcodes.FLOAD, 1); write.visitMethodInsn(Opcodes.INVOKESTATIC, owner, "write0", "(Ljava/lang/Object;F)V", false); write.visitInsn(Opcodes.RETURN); write.visitMaxs(2, 2); write.visitEnd();
        writer.visitEnd();
        return writer.toByteArray();
    }
    private static RuntimeException fail(Throwable e){if(e instanceof RuntimeException r)return r;if(e instanceof Error x)throw x;return new IllegalStateException(e);}
}
