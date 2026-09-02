package miku.united_as_one.genesis.events.item;

import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.content.item.tool.DivineMetalAxe;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Genesis.MOD_ID)
public final class DivineMetalEvents {
    private DivineMetalEvents() {
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof WitherSkeleton witherSkeleton)) {
            return;
        }
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }

        ItemStack weapon = player.getItemBySlot(EquipmentSlot.MAINHAND);
        if (weapon.isEmpty()) {
            weapon = player.getItemBySlot(EquipmentSlot.OFFHAND);
        }
        if (weapon.isEmpty() || !(weapon.getItem() instanceof SwordItem || weapon.getItem() instanceof DivineMetalAxe)) {
            return;
        }
        if (!weapon.is(Items.AIR) && !(weapon.getItem() instanceof DivineMetalAxe) && !weapon.is(miku.united_as_one.genesis.registries.ItemRegistry.DIVINE_METAL_SWORD.get())) {
            return;
        }

        for (ItemEntity drop : event.getDrops()) {
            if (drop.getItem().is(Items.WITHER_SKELETON_SKULL)) {
                drop.getItem().setCount(1);
                return;
            }
        }

        event.getDrops().add(new ItemEntity(
                witherSkeleton.level(),
                witherSkeleton.getX(),
                witherSkeleton.getY(),
                witherSkeleton.getZ(),
                new ItemStack(Items.WITHER_SKELETON_SKULL)
        ));
    }
}
