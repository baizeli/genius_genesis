package miku.united_as_one.genesis.client.spellhud;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class SpellCardEffectEngine {
    public static final long ENTER_MILLIS = 220L;
    public static final long EXIT_MILLIS = 180L;
    public static final long CAST_BURST_MILLIS = 220L;
    public static final long COOLDOWN_SHINE_MILLIS = 320L;
    public static final long AFTERIMAGE_MILLIS = 180L;
    private static final int MAX_AFTERIMAGES = 3;

    private final Map<String, CooldownState> cooldowns = new HashMap<>();
    private final ArrayDeque<AfterimageState> afterimages = new ArrayDeque<>();
    private boolean targetVisible;
    private float transitionFrom;
    private float transitionTo;
    private long transitionStartedAt;
    private boolean wasCasting;
    private float maximumCastProgress;
    private long castBurstStartedAt = Long.MIN_VALUE;
    private String selectedSpellId;
    private int selectedIndex = -1;

    public SpellCardEffectEngine(boolean initiallyVisible) {
        targetVisible = initiallyVisible;
        transitionFrom = initiallyVisible ? 1.0F : 0.0F;
        transitionTo = transitionFrom;
        transitionStartedAt = 0L;
    }

    public void setTargetVisible(boolean visible, long nowMillis) {
        if (visible == targetVisible) return;
        float current = visibilityAt(nowMillis);
        targetVisible = visible;
        transitionFrom = current;
        transitionTo = visible ? 1.0F : 0.0F;
        transitionStartedAt = nowMillis;
    }

    public boolean shouldRender(long nowMillis) {
        return targetVisible || visibilityAt(nowMillis) > 0.0001F;
    }

    public boolean shouldHideOriginalBar(long nowMillis) {
        return targetVisible || visibilityAt(nowMillis) > 0.0001F;
    }

    public SpellCardEffectSnapshot update(FrameInput rawInput) {
        FrameInput input = rawInput.sanitized();
        setTargetVisible(input.targetVisible(), input.nowMillis());
        float visibility = visibilityAt(input.nowMillis());
        updateCasting(input);
        updateSelection(input);
        List<SpellCardEffectSnapshot.CardEffect> cardEffects = updateCooldowns(input);
        List<SpellCardEffectSnapshot.Afterimage> imageEffects = updateAfterimages(input.nowMillis());
        int selectedColor = input.cards().isEmpty() ? 0xFFC99CFF
                : input.cards().get(input.selectedIndex()).color();
        float burstProgress = progressSince(castBurstStartedAt, input.nowMillis(),
                CAST_BURST_MILLIS);
        float burstAlpha = castBurstStartedAt == Long.MIN_VALUE
                || burstProgress >= 1.0F ? 0.0F : 1.0F - smootherStep(burstProgress);
        if (burstProgress >= 1.0F) castBurstStartedAt = Long.MIN_VALUE;
        float castProgress = input.casting() ? input.castProgress() : 0.0F;
        float rotation = input.casting() ? (input.nowMillis() % 1_400L)
                * (360.0F / 1_400.0F) : 0.0F;
        return new SpellCardEffectSnapshot(visibility, (1.0F - visibility) * 34.0F,
                targetVisible || visibility > 0.0001F, castProgress, rotation,
                burstProgress, burstAlpha, selectedColor, cardEffects, imageEffects);
    }

    public void resetTransientState() {
        cooldowns.clear();
        afterimages.clear();
        wasCasting = false;
        maximumCastProgress = 0.0F;
        castBurstStartedAt = Long.MIN_VALUE;
        selectedSpellId = null;
        selectedIndex = -1;
    }

    private void updateCasting(FrameInput input) {
        if (input.casting()) {
            maximumCastProgress = wasCasting
                    ? Math.max(maximumCastProgress, input.castProgress()) : input.castProgress();
        } else if (wasCasting) {
            if (maximumCastProgress >= 0.98F) castBurstStartedAt = input.nowMillis();
            maximumCastProgress = 0.0F;
        }
        wasCasting = input.casting();
    }

    private void updateSelection(FrameInput input) {
        if (input.cards().isEmpty()) {
            selectedSpellId = null;
            selectedIndex = -1;
            afterimages.clear();
            return;
        }
        CardInput selected = input.cards().get(input.selectedIndex());
        if (selectedSpellId != null && !selectedSpellId.equals(selected.spellId())) {
            afterimages.addLast(new AfterimageState(selectedSpellId,
                    findColor(input.cards(), selectedSpellId), selectedIndex, input.nowMillis()));
            while (afterimages.size() > MAX_AFTERIMAGES) afterimages.removeFirst();
        }
        selectedSpellId = selected.spellId();
        selectedIndex = input.selectedIndex();
    }

    private List<SpellCardEffectSnapshot.CardEffect> updateCooldowns(FrameInput input) {
        Set<String> liveIds = new HashSet<>();
        List<SpellCardEffectSnapshot.CardEffect> result = new ArrayList<>(input.cards().size());
        for (CardInput card : input.cards()) {
            liveIds.add(card.spellId());
            CooldownState state = cooldowns.get(card.spellId());
            if (state == null) {
                state = new CooldownState(card.cooldownProgress(), Long.MIN_VALUE);
                cooldowns.put(card.spellId(), state);
            } else {
                if (state.previousProgress > 0.0F && card.cooldownProgress() <= 0.0F) {
                    state.shineStartedAt = input.nowMillis();
                }
                state.previousProgress = card.cooldownProgress();
            }
            float shineProgress = progressSince(state.shineStartedAt, input.nowMillis(),
                    COOLDOWN_SHINE_MILLIS);
            float shineAlpha = state.shineStartedAt == Long.MIN_VALUE || shineProgress >= 1.0F
                    ? 0.0F : 1.0F - smootherStep(shineProgress);
            result.add(new SpellCardEffectSnapshot.CardEffect(card.spellId(), card.color(),
                    card.cooldownProgress(), shineProgress, shineAlpha));
        }
        cooldowns.keySet().removeIf(id -> !liveIds.contains(id));
        return result;
    }

    private List<SpellCardEffectSnapshot.Afterimage> updateAfterimages(long nowMillis) {
        afterimages.removeIf(image -> nowMillis - image.startedAt >= AFTERIMAGE_MILLIS);
        List<SpellCardEffectSnapshot.Afterimage> result = new ArrayList<>(afterimages.size());
        for (AfterimageState image : afterimages) {
            float progress = progressSince(image.startedAt, nowMillis, AFTERIMAGE_MILLIS);
            result.add(new SpellCardEffectSnapshot.Afterimage(image.spellId, image.color,
                    image.sourceIndex, progress, 1.0F - smootherStep(progress)));
        }
        return result;
    }

    private float visibilityAt(long nowMillis) {
        if (transitionFrom == transitionTo) return transitionTo;
        long duration = transitionTo > transitionFrom ? ENTER_MILLIS : EXIT_MILLIS;
        float progress = clamp((nowMillis - transitionStartedAt) / (float) duration);
        float eased = transitionTo > transitionFrom ? easeOutCubic(progress) : easeInCubic(progress);
        return transitionFrom + (transitionTo - transitionFrom) * eased;
    }

    private static int findColor(List<CardInput> cards, String spellId) {
        for (CardInput card : cards) {
            if (card.spellId().equals(spellId)) return card.color();
        }
        return 0xFFC99CFF;
    }

    private static float progressSince(long startedAt, long now, long duration) {
        if (startedAt == Long.MIN_VALUE) return 1.0F;
        return clamp((now - startedAt) / (float) duration);
    }

    private static float clamp(float value) {
        if (!Float.isFinite(value)) return 0.0F;
        return Math.max(0.0F, Math.min(1.0F, value));
    }

    private static float smootherStep(float value) {
        float clamped = clamp(value);
        return clamped * clamped * clamped
                * (clamped * (clamped * 6.0F - 15.0F) + 10.0F);
    }

    private static float easeOutCubic(float value) {
        float inverse = 1.0F - value;
        return 1.0F - inverse * inverse * inverse;
    }

    private static float easeInCubic(float value) {
        return value * value * value;
    }

    public record FrameInput(long nowMillis, boolean targetVisible, int selectedIndex,
                             boolean casting, float castProgress, List<CardInput> cards) {
        public FrameInput {
            cards = cards == null ? List.of() : List.copyOf(cards);
        }

        private FrameInput sanitized() {
            int safeIndex = cards.isEmpty() ? 0
                    : Math.max(0, Math.min(cards.size() - 1, selectedIndex));
            List<CardInput> safeCards = cards.stream().map(CardInput::sanitized).toList();
            return new FrameInput(nowMillis, targetVisible, safeIndex, casting,
                    clamp(castProgress), safeCards);
        }
    }

    public record CardInput(String spellId, int color, float cooldownProgress) {
        private CardInput sanitized() {
            String safeId = spellId == null || spellId.isBlank() ? "unknown" : spellId;
            return new CardInput(safeId, color | 0xFF000000, clamp(cooldownProgress));
        }
    }

    private static final class CooldownState {
        private float previousProgress;
        private long shineStartedAt;

        private CooldownState(float previousProgress, long shineStartedAt) {
            this.previousProgress = previousProgress;
            this.shineStartedAt = shineStartedAt;
        }
    }

    private record AfterimageState(String spellId, int color, int sourceIndex,
                                   long startedAt) {
    }
}
