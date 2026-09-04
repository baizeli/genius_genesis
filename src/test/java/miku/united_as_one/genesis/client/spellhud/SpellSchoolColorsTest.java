package miku.united_as_one.genesis.client.spellhud;

public final class SpellSchoolColorsTest {
    private SpellSchoolColorsTest() {
    }

    public static void main(String[] args) {
        assertEquals(0xFFFF6038, SpellSchoolColors.colorFor("irons_spellbooks:fire"),
                "fire uses orange red");
        assertEquals(0xFF713BD8, SpellSchoolColors.colorFor("genius_genesis:chaos"),
                "Genesis chaos uses deep purple");
        assertEquals(0xFF4FAEFF, SpellSchoolColors.colorFor("celestial_source"),
                "unqualified custom school names are supported");
        assertEquals(0xFFC99CFF, SpellSchoolColors.colorFor("other:unknown"),
                "unknown schools use neutral lavender");
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + ": expected " + expected + ", got " + actual);
        }
    }
}
