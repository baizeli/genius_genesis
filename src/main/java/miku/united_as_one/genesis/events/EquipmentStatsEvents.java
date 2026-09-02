package miku.united_as_one.genesis.events;

import miku.bai_ze_li.genesis.api.equipment.EquipmentStatsManager;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.compat.curios.GenesisCurios;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

import java.util.HashSet;

@Mod.EventBusSubscriber(modid = Genesis.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class EquipmentStatsEvents {
    private EquipmentStatsEvents() {
    }

    @SubscribeEvent
    public static void syncEquipmentStats(OnDatapackSyncEvent event) {
        EquipmentStatsManager.rebuildFromConfig();
        EquipmentStatsManager.refreshPlayers(event.getPlayers());
        event.getPlayers().forEach(GenesisCurios::refreshConfiguredAttributes);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void modifyItemAttributes(ItemAttributeModifierEvent event) {
        for (Attribute attribute : EquipmentStatsManager.controlledEquipmentAttributes(event.getItemStack(), event.getSlotType())) {
            event.removeAttribute(attribute);
        }
        EquipmentStatsManager.buildEquipmentModifiers(event.getItemStack(), event.getSlotType())
                .forEach(event::addModifier);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void modifyCurioAttributes(CurioAttributeModifierEvent event) {
        if (!EquipmentStatsManager.controlsAccessory(event.getItemStack())) {
            return;
        }
        for (Attribute attribute : new HashSet<>(event.getOriginalModifiers().keySet())) {
            event.removeAttribute(attribute);
        }
        EquipmentStatsManager.buildAccessoryModifiers(event.getItemStack(), GenesisCurios.slotKey(
                        event.getSlotContext().identifier(), event.getSlotContext().index()))
                .forEach(event::addModifier);
    }
}
