package miku.united_as_one.genesis.client.spellhud;

import java.util.List;

public record SpellCardEffectSnapshot(
        float hudVisibility,
        float horizontalOffset,
        boolean hideOriginalBar,
        float castProgress,
        float castRotationDegrees,
        float castBurstProgress,
        float castBurstAlpha,
        int selectedColor,
        List<CardEffect> cards,
        List<Afterimage> afterimages) {

    public SpellCardEffectSnapshot {
        cards = List.copyOf(cards);
        afterimages = List.copyOf(afterimages);
    }

    public record CardEffect(String spellId, int color, float cooldownProgress,
                             float shineProgress, float shineAlpha) {
    }

    public record Afterimage(String spellId, int color, int sourceIndex,
                             float progress, float alpha) {
    }
}
