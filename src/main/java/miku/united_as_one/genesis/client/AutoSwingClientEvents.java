package miku.united_as_one.genesis.client;

import miku.united_as_one.genesis.combat.autoswing.IAutoSwingItem;
import miku.united_as_one.genesis.combat.autoswing.SwingPipeline;
import miku.united_as_one.genesis.combat.meleeproj.MeleeSwingArmHelper;
import miku.united_as_one.genesis.network.GenesisNetwork;
import miku.united_as_one.genesis.network.packet.AutoSwingInputPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public final class AutoSwingClientEvents {
    private static boolean wasDown;

    private AutoSwingClientEvents() {
    }

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        boolean down = minecraft.player != null
                && minecraft.screen == null
                && isAutoHoldItem(minecraft.player.getMainHandItem())
                && minecraft.options.keyAttack.isDown();
        if (down != wasDown) {
            wasDown = down;
            GenesisNetwork.CHANNEL.sendToServer(new AutoSwingInputPacket(down));
        }
    }

    public static void onRenderHand(RenderHandEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (event.getHand() == InteractionHand.MAIN_HAND
                && minecraft.player != null
                && (MeleeSwingArmHelper.hasOwnedProj(minecraft.player) || isAutoAttacking())) {
            event.setCanceled(true);
        }
    }

    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getLevel().isClientSide && isAutoAttacking()) {
            event.setCanceled(true);
        }
    }

    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide && isAutoAttacking()) {
            event.setCanceled(true);
        }
    }

    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide && isAutoAttacking()) {
            event.setCanceled(true);
        }
    }

    public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.getLevel().isClientSide && isAutoAttacking()) {
            event.setCanceled(true);
        }
    }

    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getLevel().isClientSide && isAutoAttacking()) {
            event.setCanceled(true);
        }
    }

    public static boolean isAutoAttacking() {
        Minecraft minecraft = Minecraft.getInstance();
        return wasDown && minecraft.player != null && isAutoHoldItem(minecraft.player.getMainHandItem());
    }

    private static boolean isAutoHoldItem(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof IAutoSwingItem autoSwingItem)) {
            return false;
        }
        return autoSwingItem.getSwingPipeline(stack).swingMode == SwingPipeline.SwingMode.AUTO_HOLD;
    }
}
