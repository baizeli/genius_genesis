package miku.united_as_one.genesis.combat.autoswing;

/** Server-side continuous-hold gate used to reject repeated click taps. */
final class HoldActivationGate {
    private final int thresholdTicks;
    private int heldTicks;
    private boolean holding;
    private boolean activated;

    HoldActivationGate(int thresholdTicks) {
        this.thresholdTicks = Math.max(1, thresholdTicks);
    }

    void press() {
        holding = true;
    }

    void release() {
        holding = false;
        activated = false;
        heldTicks = 0;
    }

    boolean tick() {
        if (!holding || activated) {
            return false;
        }
        if (++heldTicks < thresholdTicks) {
            return false;
        }
        activated = true;
        return true;
    }

    boolean isActivated() {
        return activated;
    }
}
