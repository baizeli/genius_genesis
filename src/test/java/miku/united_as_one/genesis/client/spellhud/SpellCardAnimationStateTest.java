package miku.united_as_one.genesis.client.spellhud;

public final class SpellCardAnimationStateTest {
    private SpellCardAnimationStateTest() {
    }

    public static void main(String[] args) {
        selectionSourceIsConsumedOnlyOnce();
        staleSelectionSignalIsIgnored();
        shortCastKeepsItsMinimumAnimationWindow();
    }

    private static void selectionSourceIsConsumedOnlyOnce() {
        SpellCardAnimationState state = new SpellCardAnimationState();
        state.signalSelection(SpellCardAnimationState.SelectionSource.WHEEL,
                1, 4, 6, 1_000L);

        var first = state.consumeSelection(1, 4, 6, 1_100L);
        var second = state.consumeSelection(1, 4, 6, 1_101L);

        assertEquals(SpellCardAnimationState.SelectionSource.WHEEL, first.source(),
                "matching wheel selection must retain its source");
        assertEquals(1, first.direction(), "forward wheel selection direction");
        assertEquals(SpellCardAnimationState.SelectionSource.UNKNOWN, second.source(),
                "selection metadata must be consumed once");
    }

    private static void staleSelectionSignalIsIgnored() {
        SpellCardAnimationState state = new SpellCardAnimationState();
        state.signalSelection(SpellCardAnimationState.SelectionSource.SCROLL,
                0, 5, 6, 1_000L);

        var transition = state.consumeSelection(0, 5, 6, 2_001L);

        assertEquals(SpellCardAnimationState.SelectionSource.UNKNOWN, transition.source(),
                "signals older than one second must not animate the wrong selection");
        assertEquals(-1, transition.direction(), "wrapped selection must take the shortest path");
    }

    private static void shortCastKeepsItsMinimumAnimationWindow() {
        SpellCardAnimationState state = new SpellCardAnimationState();
        state.signalCastingStarted(5_000L);
        state.signalCastingStopped();

        assertTrue(state.castingTransform(false, 5_419L).active(),
                "a short cast must still animate for 420 ms");
        assertFalse(state.castingTransform(false, 5_420L).active(),
                "the minimum window ends after 420 ms");
    }

    private static void assertTrue(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }

    private static void assertFalse(boolean value, String message) {
        assertTrue(!value, message);
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + ": expected " + expected + ", got " + actual);
        }
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + ": expected " + expected + ", got " + actual);
        }
    }
}
