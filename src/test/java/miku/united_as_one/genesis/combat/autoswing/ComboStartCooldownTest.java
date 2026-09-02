package miku.united_as_one.genesis.combat.autoswing;

public final class ComboStartCooldownTest {
    private ComboStartCooldownTest() {
    }

    public static void main(String[] args) {
        ComboStartCooldown cooldown = new ComboStartCooldown();
        assertTrue(cooldown.tryStart(70), "first attack must start immediately");
        assertFalse(cooldown.tryStart(70), "a second first attack must be blocked during cooldown");
        for (int tick = 0; tick < 69; tick++) {
            cooldown.tick();
        }
        assertFalse(cooldown.tryStart(70), "cooldown must last all 70 ticks");
        cooldown.tick();
        assertTrue(cooldown.tryStart(70), "a new combo may start after 70 ticks");
    }

    private static void assertTrue(boolean value, String message) {
        if (!value) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean value, String message) {
        assertTrue(!value, message);
    }
}
