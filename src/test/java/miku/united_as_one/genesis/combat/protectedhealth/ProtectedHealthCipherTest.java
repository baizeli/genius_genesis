package miku.united_as_one.genesis.combat.protectedhealth;

public final class ProtectedHealthCipherTest {
    private ProtectedHealthCipherTest() {
    }

    public static void main(String[] args) {
        String cipher = ProtectedHealthCipher.encrypt("-400.0");
        assertTrue(!"-400.0".equals(cipher), "ciphertext must differ from plaintext");
        assertTrue(!cipher.contains("400"), "ciphertext must not expose the health digits");
        assertEquals("-400.0", ProtectedHealthCipher.decrypt(cipher));
        assertEquals("", ProtectedHealthCipher.decrypt("bad"));
    }

    private static void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("expected " + expected + " but got " + actual);
        }
    }

    private static void assertTrue(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
}
