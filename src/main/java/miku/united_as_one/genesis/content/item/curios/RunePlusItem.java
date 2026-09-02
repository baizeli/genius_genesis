package miku.united_as_one.genesis.content.item.curios;

import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;

public class RunePlusItem extends GenesisCurioItem {
    private final Type type;

    public RunePlusItem(Type type, Properties properties) {
        super(properties);
        this.type = type;
    }

    public Type type() {
        return type;
    }

    public static boolean isLightning(ItemStack stack) {
        return is(stack, Type.LIGHTNING);
    }

    public static boolean isNature(ItemStack stack) {
        return is(stack, Type.NATURE);
    }

    public static boolean isEnder(ItemStack stack) {
        return is(stack, Type.ENDER);
    }

    public static boolean isHoly(ItemStack stack) {
        return is(stack, Type.HOLY);
    }

    public static boolean isIce(ItemStack stack) {
        return is(stack, Type.ICE);
    }

    public static boolean isBlood(ItemStack stack) {
        return is(stack, Type.BLOOD);
    }

    public static boolean isFire(ItemStack stack) {
        return is(stack, Type.FIRE);
    }

    public static boolean isEldritch(ItemStack stack) {
        return is(stack, Type.ELDRITCH);
    }

    private static boolean is(ItemStack stack, Type type) {
        return stack.getItem() instanceof RunePlusItem item && item.type == type;
    }

    public enum Type {
        LIGHTNING,
        NATURE,
        ENDER,
        HOLY,
        ICE,
        BLOOD,
        FIRE,
        ELDRITCH
    }
}
