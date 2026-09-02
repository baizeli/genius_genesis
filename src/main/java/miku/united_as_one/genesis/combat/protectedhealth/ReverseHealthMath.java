package miku.united_as_one.genesis.combat.protectedhealth;

public final class ReverseHealthMath {
    private ReverseHealthMath() {}
    public static float full(float max) { return -Math.abs(max); }
    public static float damage(float value, float amount) { return value + Math.max(0, amount); }
    public static float limitIncomingDamage(float amount, float limit) {
        if (!Float.isFinite(amount) || amount <= 0 || limit <= 0) {
            return amount == Float.POSITIVE_INFINITY ? Math.max(0, limit) : 0;
        }
        return Math.min(amount, limit);
    }
    public static float heal(float value, float amount, float max) { return Math.max(full(max), value - Math.max(0, amount)); }
    public static float remaining(float value, float max) { return Math.max(0, Math.min(Math.abs(max), -value)); }
    public static boolean isAlive(float value) { return value < 0; }
    public static float mirror(float value, float max, float mirrorMax) {
        return max == 0 ? 0 : mirrorMax * remaining(value, max) / Math.abs(max);
    }
}
