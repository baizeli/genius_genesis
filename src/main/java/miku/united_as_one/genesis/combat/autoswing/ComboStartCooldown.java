package miku.united_as_one.genesis.combat.autoswing;

/** Cooldown that guards starting a new sequence without blocking its remaining stages. */
final class ComboStartCooldown {
    private int remainingTicks;

    boolean tryStart(int cooldownTicks) {
        if (remainingTicks > 0) {
            return false;
        }
        remainingTicks = Math.max(0, cooldownTicks);
        return true;
    }

    void tick() {
        if (remainingTicks > 0) {
            remainingTicks--;
        }
    }
}
