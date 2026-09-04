package miku.united_as_one.genesis.client.spellhud;

public final class SpellCardHudState {
    private static final float REFERENCE_GUI_SCALE = 3.0F;
    private boolean enabled;

    public SpellCardHudState(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean toggle() {
        enabled = !enabled;
        return enabled;
    }

    public static int cardX(int screenWidth, int cardWidth, int margin) {
        return Math.max(0, screenWidth - cardWidth - margin);
    }

    public static float referenceScale(double currentGuiScale) {
        if (currentGuiScale <= 0.0D) {
            return 1.0F;
        }
        return REFERENCE_GUI_SCALE / (float) currentGuiScale;
    }

    public static int referenceViewport(int currentGuiPixels, float referenceScale) {
        if (referenceScale <= 0.0F) {
            return currentGuiPixels;
        }
        return Math.max(1, Math.round(currentGuiPixels / referenceScale));
    }
}
