package miku.united_as_one.genesis.client.gui.manuscript;

public final class SpellLearningBackgroundTest {
    private SpellLearningBackgroundTest() {
    }

    public static void main(String[] args) {
        assertEquals(9, SpellLearningBackground.CELESTIAL_SOURCE.cosmicType(), "celestial source shader type");
        assertEquals(14, SpellLearningBackground.CHAOS.cosmicType(), "chaos shader type");
    }

    private static void assertEquals(int expected, int actual, String behavior) {
        if (expected != actual) {
            throw new AssertionError(behavior + ": expected " + expected + ", got " + actual);
        }
    }
}
