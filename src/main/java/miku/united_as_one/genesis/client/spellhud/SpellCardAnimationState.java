package miku.united_as_one.genesis.client.spellhud;

public final class SpellCardAnimationState {
    private static final long SELECTION_EXPIRY_MILLIS = 1_000L;
    private static final long MINIMUM_CAST_MILLIS = 420L;

    private SelectionSource pendingSource = SelectionSource.UNKNOWN;
    private int pendingPrevious = Integer.MIN_VALUE;
    private int pendingSelected = Integer.MIN_VALUE;
    private int pendingDirection;
    private long pendingStartedAt = Long.MIN_VALUE;
    private long pendingExpiresAt = Long.MIN_VALUE;
    private long castingStartedAt = Long.MIN_VALUE;
    private long castingMinimumEndAt = Long.MIN_VALUE;
    private boolean castingSignaled;

    public void signalSelection(SelectionSource source, int previous, int selected,
                                int spellCount, long now) {
        pendingSource = source;
        pendingPrevious = previous;
        pendingSelected = selected;
        pendingDirection = signedDistance(selected, previous, spellCount) < 0 ? -1 : 1;
        pendingStartedAt = now;
        pendingExpiresAt = now + SELECTION_EXPIRY_MILLIS;
    }

    public SelectionTransition consumeSelection(int previous, int selected,
                                                int spellCount, long now) {
        boolean matching = pendingPrevious == previous && pendingSelected == selected;
        SelectionSource source = matching && now <= pendingExpiresAt
                ? pendingSource : SelectionSource.UNKNOWN;
        int direction = source == SelectionSource.UNKNOWN
                ? (signedDistance(selected, previous, spellCount) < 0 ? -1 : 1)
                : pendingDirection;
        long startedAt = source == SelectionSource.UNKNOWN ? now : pendingStartedAt;
        clearPendingSelection();
        return new SelectionTransition(source, direction, startedAt);
    }

    public void signalCastingStarted(long now) {
        if (castingStartedAt == Long.MIN_VALUE) castingStartedAt = now;
        castingSignaled = true;
        castingMinimumEndAt = Math.max(castingMinimumEndAt, now + MINIMUM_CAST_MILLIS);
    }

    public void signalCastingStopped() {
        castingSignaled = false;
    }

    public CastingTransform castingTransform(boolean castingHint, long now) {
        boolean active = castingHint || castingSignaled || now < castingMinimumEndAt;
        if (!active) {
            castingStartedAt = Long.MIN_VALUE;
            castingMinimumEndAt = Long.MIN_VALUE;
            return CastingTransform.IDENTITY;
        }
        if (castingStartedAt == Long.MIN_VALUE) {
            castingStartedAt = now;
            castingMinimumEndAt = Math.max(castingMinimumEndAt, now + MINIMUM_CAST_MILLIS);
        }
        long elapsed = Math.max(0L, now - castingStartedAt);
        float entrance = smootherStep(elapsed / 150.0F);
        float pulse = 0.5F + 0.5F * (float) Math.sin(elapsed * 0.012F);
        return new CastingTransform(true,
                1.0F + entrance * (0.085F + pulse * 0.032F),
                (elapsed % 1_400L) * (360.0F / 1_400.0F));
    }

    public void reset() {
        clearPendingSelection();
        castingStartedAt = Long.MIN_VALUE;
        castingMinimumEndAt = Long.MIN_VALUE;
        castingSignaled = false;
    }

    private void clearPendingSelection() {
        pendingSource = SelectionSource.UNKNOWN;
        pendingPrevious = Integer.MIN_VALUE;
        pendingSelected = Integer.MIN_VALUE;
        pendingDirection = 0;
        pendingStartedAt = Long.MIN_VALUE;
        pendingExpiresAt = Long.MIN_VALUE;
    }

    static int signedDistance(int index, int center, int count) {
        if (count <= 0) return 0;
        int distance = index - center;
        int half = count / 2;
        if (distance > half) distance -= count;
        else if (distance < -half) distance += count;
        return distance;
    }

    private static float smootherStep(float value) {
        float clamped = Math.max(0.0F, Math.min(1.0F, value));
        return clamped * clamped * clamped
                * (clamped * (clamped * 6.0F - 15.0F) + 10.0F);
    }

    public enum SelectionSource { UNKNOWN, SCROLL, WHEEL }

    public record SelectionTransition(SelectionSource source, int direction,
                                      long startedAtMillis) {
    }

    public record CastingTransform(boolean active, float scale, float rotationDegrees) {
        public static final CastingTransform IDENTITY =
                new CastingTransform(false, 1.0F, 0.0F);
    }
}
