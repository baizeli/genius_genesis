package miku.united_as_one.genesis.combat.autoswing;

public final class HoldActivationGateTest {
    private HoldActivationGateTest() {
    }

    public static void main(String[] args) {
        shortClicksNeverActivate();
        continuousHoldActivatesOnce();
        releaseAllowsASecondActivation();
    }

    private static void shortClicksNeverActivate() {
        HoldActivationGate gate = new HoldActivationGate(3);
        for (int click = 0; click < 10; click++) {
            gate.press();
            assertFalse(gate.tick(), "a one-tick click must not activate");
            gate.release();
        }
    }

    private static void continuousHoldActivatesOnce() {
        HoldActivationGate gate = new HoldActivationGate(3);
        gate.press();
        assertFalse(gate.tick(), "first held tick");
        assertFalse(gate.tick(), "second held tick");
        assertTrue(gate.tick(), "third held tick must activate");
        assertFalse(gate.tick(), "holding after activation must not activate again");
    }

    private static void releaseAllowsASecondActivation() {
        HoldActivationGate gate = new HoldActivationGate(2);
        gate.press();
        gate.tick();
        assertTrue(gate.tick(), "initial hold must activate");
        gate.release();
        gate.press();
        assertFalse(gate.tick(), "new hold must satisfy the threshold again");
        assertTrue(gate.tick(), "new continuous hold must activate");
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
