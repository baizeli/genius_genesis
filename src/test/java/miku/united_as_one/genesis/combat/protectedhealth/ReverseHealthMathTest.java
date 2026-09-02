package miku.united_as_one.genesis.combat.protectedhealth;

public final class ReverseHealthMathTest {
    private ReverseHealthMathTest() {
    }

    public static void main(String[] args) {
        assertClose(-400.0F, ReverseHealthMath.damage(ReverseHealthMath.full(800), 400));
        assertTrue(ReverseHealthMath.isAlive(-0.01F), "negative true health must be alive");
        assertTrue(!ReverseHealthMath.isAlive(0), "zero true health must be dead");
        assertClose(10.0F, ReverseHealthMath.mirror(-400, 800, 20));
        assertClose(9.975F, ReverseHealthMath.mirror(-399, 800, 20));
        assertClose(4.0F, ReverseHealthMath.limitIncomingDamage(4.0F, 10.0F));
        assertClose(10.0F, ReverseHealthMath.limitIncomingDamage(Float.MAX_VALUE, 10.0F));
        assertClose(0.0F, ReverseHealthMath.limitIncomingDamage(-1.0F, 10.0F));
    }

    private static void assertClose(float expected, float actual) {
        if (Math.abs(expected - actual) > 0.0001F) {
            throw new AssertionError("expected " + expected + " but got " + actual);
        }
    }

    private static void assertTrue(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
}
