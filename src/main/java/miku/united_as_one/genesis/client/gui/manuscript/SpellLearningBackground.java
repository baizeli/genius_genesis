package miku.united_as_one.genesis.client.gui.manuscript;

public enum SpellLearningBackground {
    CELESTIAL_SOURCE(9),
    CHAOS(14);

    private final int cosmicType;

    SpellLearningBackground(int cosmicType) {
        this.cosmicType = cosmicType;
    }

    public int cosmicType() {
        return cosmicType;
    }
}
