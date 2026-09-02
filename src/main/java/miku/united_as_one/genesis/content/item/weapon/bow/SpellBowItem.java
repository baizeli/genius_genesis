package miku.united_as_one.genesis.content.item.weapon.bow;

import miku.united_as_one.genesis.Genesis;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public abstract class SpellBowItem extends BowItem {
    private final String tooltipKey;
    private final int tooltipLines;

    protected SpellBowItem(Properties properties, String tooltipKey, int tooltipLines) {
        super(properties);
        this.tooltipKey = tooltipKey;
        this.tooltipLines = tooltipLines;
    }

    @Override
    public @NotNull Predicate<ItemStack> getAllSupportedProjectiles() {
        return stack -> stack.getItem() instanceof ArrowItem;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment == Enchantments.POWER_ARROWS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        for (int i = 1; i <= this.tooltipLines; i++) {
            tooltip.add(Component.translatable("item." + Genesis.MOD_ID + "." + this.tooltipKey + ".tooltip." + i)
                    .withStyle(ChatFormatting.GRAY));
        }
    }
}
