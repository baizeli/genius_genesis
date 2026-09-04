package miku.united_as_one.genesis.client.spellhud;

import java.util.List;

public final class SpellCardEffectEngineTest {
    private SpellCardEffectEngineTest() {
    }

    public static void main(String[] args) {
        clampsProgressValues();
        distinguishesCompletedAndCancelledCasts();
        cooldownShineTriggersOnlyOnReadyEdge();
        removesCooldownStateForMissingSpells();
        toggleTransitionReversesContinuously();
        originalBarReturnsOnlyAfterExit();
        afterimagesAreLimitedAndExpire();
        snapshotsAreDeterministicForTheSameTimeline();
    }

    private static void clampsProgressValues() {
        var engine = new SpellCardEffectEngine(true);
        var snapshot = engine.update(frame(0L, true, 0, true, 4.0F,
                card("fire", -2.0F)));
        assertFloatEquals(1.0F, snapshot.castProgress(), "cast progress is clamped");
        assertFloatEquals(0.0F, snapshot.cards().get(0).cooldownProgress(),
                "cooldown progress is clamped");
    }

    private static void distinguishesCompletedAndCancelledCasts() {
        var completed = new SpellCardEffectEngine(true);
        completed.update(frame(0L, true, 0, true, 0.99F, card("fire", 0.0F)));
        var burst = completed.update(frame(10L, true, 0, false, 0.0F,
                card("fire", 0.0F)));
        assertTrue(burst.castBurstProgress() >= 0.0F,
                "a cast ending after 98 percent starts its completion burst");
        assertTrue(burst.castBurstAlpha() > 0.0F, "completion burst is visible");

        var cancelled = new SpellCardEffectEngine(true);
        cancelled.update(frame(0L, true, 0, true, 0.70F, card("fire", 0.0F)));
        var noBurst = cancelled.update(frame(10L, true, 0, false, 0.0F,
                card("fire", 0.0F)));
        assertFloatEquals(0.0F, noBurst.castBurstAlpha(),
                "a cancelled cast does not create a completion burst");
    }

    private static void cooldownShineTriggersOnlyOnReadyEdge() {
        var engine = new SpellCardEffectEngine(true);
        var initiallyReady = engine.update(frame(0L, true, 0, false, 0.0F,
                card("fire", 0.0F)));
        assertFloatEquals(0.0F, initiallyReady.cards().get(0).shineAlpha(),
                "initially-ready spells must not flash");
        engine.update(frame(10L, true, 0, false, 0.0F, card("fire", 0.6F)));
        var ready = engine.update(frame(20L, true, 0, false, 0.0F,
                card("fire", 0.0F)));
        assertTrue(ready.cards().get(0).shineAlpha() > 0.0F,
                "cooldown reaching zero starts a shine");
        assertTrue(engine.update(frame(340L, true, 0, false, 0.0F,
                card("fire", 0.0F))).cards().get(0).shineAlpha() == 0.0F,
                "shine expires after 320 milliseconds");
    }

    private static void removesCooldownStateForMissingSpells() {
        var engine = new SpellCardEffectEngine(true);
        engine.update(frame(0L, true, 0, false, 0.0F, card("gone", 0.5F)));
        engine.update(frame(1L, true, 0, false, 0.0F));
        var reintroducedReady = engine.update(frame(2L, true, 0, false, 0.0F,
                card("gone", 0.0F)));
        assertFloatEquals(0.0F, reintroducedReady.cards().get(0).shineAlpha(),
                "removed spell IDs are forgotten");
    }

    private static void toggleTransitionReversesContinuously() {
        var engine = new SpellCardEffectEngine(false);
        engine.setTargetVisible(true, 0L);
        float beforeReverse = engine.update(frame(100L, true, 0, false, 0.0F,
                card("fire", 0.0F))).hudVisibility();
        engine.setTargetVisible(false, 100L);
        float atReverse = engine.update(frame(100L, false, 0, false, 0.0F,
                card("fire", 0.0F))).hudVisibility();
        assertFloatEquals(beforeReverse, atReverse,
                "reversing a transition must preserve its current visibility");
    }

    private static void originalBarReturnsOnlyAfterExit() {
        var engine = new SpellCardEffectEngine(true);
        engine.setTargetVisible(false, 0L);
        assertTrue(engine.update(frame(179L, false, 0, false, 0.0F,
                card("fire", 0.0F))).hideOriginalBar(),
                "original bar remains hidden during exit");
        assertFalse(engine.update(frame(180L, false, 0, false, 0.0F,
                card("fire", 0.0F))).hideOriginalBar(),
                "original bar returns when exit completes");
    }

    private static void afterimagesAreLimitedAndExpire() {
        var engine = new SpellCardEffectEngine(true);
        engine.update(frame(0L, true, 0, false, 0.0F,
                card("a", 0.0F), card("b", 0.0F), card("c", 0.0F), card("d", 0.0F)));
        for (int index = 1; index <= 4; index++) {
            engine.update(frame(index * 10L, true, index % 4, false, 0.0F,
                    card("a", 0.0F), card("b", 0.0F), card("c", 0.0F), card("d", 0.0F)));
        }
        assertEquals(3, engine.update(frame(50L, true, 0, false, 0.0F,
                card("a", 0.0F), card("b", 0.0F), card("c", 0.0F), card("d", 0.0F)))
                .afterimages().size(), "at most three afterimages are retained");
        assertEquals(0, engine.update(frame(230L, true, 0, false, 0.0F,
                card("a", 0.0F))).afterimages().size(),
                "afterimages expire after 180 milliseconds");
    }

    private static void snapshotsAreDeterministicForTheSameTimeline() {
        var first = new SpellCardEffectEngine(true);
        var second = new SpellCardEffectEngine(true);
        first.update(frame(10L, true, 0, true, 0.25F, card("fire", 0.2F)));
        second.update(frame(10L, true, 0, true, 0.25F, card("fire", 0.2F)));
        assertEquals(first.update(frame(60L, true, 0, true, 0.5F, card("fire", 0.0F))),
                second.update(frame(60L, true, 0, true, 0.5F, card("fire", 0.0F))),
                "equal input timelines produce equal snapshots");
    }

    private static SpellCardEffectEngine.FrameInput frame(long now, boolean visible,
            int selected, boolean casting, float castProgress,
            SpellCardEffectEngine.CardInput... cards) {
        return new SpellCardEffectEngine.FrameInput(now, visible, selected,
                casting, castProgress, List.of(cards));
    }

    private static SpellCardEffectEngine.CardInput card(String id, float cooldown) {
        return new SpellCardEffectEngine.CardInput(id, 0xFFFF6038, cooldown);
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

    private static void assertFloatEquals(float expected, float actual, String message) {
        if (Math.abs(expected - actual) > 0.0001F) {
            throw new AssertionError(message + ": expected " + expected + ", got " + actual);
        }
    }
}
