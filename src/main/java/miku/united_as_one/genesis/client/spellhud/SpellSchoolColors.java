package miku.united_as_one.genesis.client.spellhud;

import java.util.Locale;

public final class SpellSchoolColors {
    private static final int FALLBACK = 0xFFC99CFF;

    private SpellSchoolColors() {
    }

    public static int colorFor(String schoolId) {
        if (schoolId == null) return FALLBACK;
        String path = schoolId.toLowerCase(Locale.ROOT);
        int separator = path.lastIndexOf(':');
        if (separator >= 0) path = path.substring(separator + 1);
        return switch (path) {
            case "fire" -> 0xFFFF6038;
            case "ice" -> 0xFF71E7FF;
            case "lightning" -> 0xFF796CFF;
            case "holy" -> 0xFFFFE7A3;
            case "nature" -> 0xFF69C85B;
            case "ender" -> 0xFFC455FF;
            case "blood" -> 0xFFC53242;
            case "evocation" -> 0xFF45D7B0;
            case "eldritch" -> 0xFFD04A9D;
            case "chaos" -> 0xFF713BD8;
            case "celestial_source" -> 0xFF4FAEFF;
            default -> FALLBACK;
        };
    }
}
