package miku.united_as_one.genesis.client.spellhud;

public final class SpellCardHudStateTest {
    private SpellCardHudStateTest() {
    }

    public static void main(String[] args) {
        toggleDisablesAndReenablesTheReplacementHud();
        cardStackAnchorsToTheRightEdge();
        referenceScaleKeepsPhysicalSizeAtThreeTimesGuiPixels();
    }

    private static void toggleDisablesAndReenablesTheReplacementHud() {
        SpellCardHudState state = new SpellCardHudState(true);

        assertTrue(state.isEnabled(), "the replacement HUD starts enabled");
        assertFalse(state.toggle(), "the first X press disables the replacement HUD");
        assertTrue(state.toggle(), "the second X press enables the replacement HUD");
    }

    private static void cardStackAnchorsToTheRightEdge() {
        assertEquals(285, SpellCardHudState.cardX(320, 22, 13),
                "a 22 pixel card with a 13 pixel margin must be right aligned");
        assertEquals(0, SpellCardHudState.cardX(20, 22, 13),
                "very small screens must not place the stack off-screen");
    }

    private static void referenceScaleKeepsPhysicalSizeAtThreeTimesGuiPixels() {
        assertFloatEquals(1.5F, SpellCardHudState.referenceScale(2.0D),
                "GUI scale 2 must enlarge the HUD to its scale-3 physical size");
        assertFloatEquals(1.0F, SpellCardHudState.referenceScale(3.0D),
                "GUI scale 3 is the reference size");
        assertFloatEquals(0.75F, SpellCardHudState.referenceScale(4.0D),
                "GUI scale 4 must shrink logical coordinates to preserve physical size");
        assertEquals(320, SpellCardHudState.referenceViewport(480, 1.5F),
                "layout viewport must be converted into reference-scale coordinates");
    }

    private static void assertTrue(boolean value, String message) {
        if (!value) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean value, String message) {
        assertTrue(!value, message);
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + ": expected " + expected + ", got " + actual);
        }
    }


    private static void assertFloatEquals(float expected, float actual, String message) {
        if (Math.abs(expected - actual) > 0.0001F) {
            throw new AssertionError(message + ": expected " + expected + ", got " + actual);
        }
    }
}
