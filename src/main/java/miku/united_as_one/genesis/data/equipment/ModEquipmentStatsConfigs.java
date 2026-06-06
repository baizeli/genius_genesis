package miku.united_as_one.genesis.data.equipment;

import dev.xkmc.l2library.serial.config.ConfigTypeEntry;
import dev.xkmc.l2library.serial.config.PacketHandlerWithConfig;
import miku.bai_ze_li.genesis.api.equipment.EquipmentStatsConfig;
import miku.bai_ze_li.genesis.api.equipment.EquipmentStatsManager;
import miku.bai_ze_li.genesis.api.render.outline.GenesisItemOutlineConfig;
import miku.bai_ze_li.genesis.api.render.outline.GenesisItemOutlineRegistry;
import miku.bai_ze_li.genesis.api.render.shader.GenesisItemShaderConfig;
import miku.bai_ze_li.genesis.api.render.shader.GenesisItemShaderRegistry;
import miku.united_as_one.genesis.Genesis;

public final class ModEquipmentStatsConfigs {
    public static final PacketHandlerWithConfig HANDLER = new PacketHandlerWithConfig(Genesis.rl("config"), 1);
    public static final ConfigTypeEntry<EquipmentStatsConfig> EQUIPMENT_STATS =
            new ConfigTypeEntry<>(HANDLER, "equipment_stats", EquipmentStatsConfig.class);
    public static final ConfigTypeEntry<GenesisItemShaderConfig> ITEM_SHADER_EFFECTS =
            new ConfigTypeEntry<>(HANDLER, "item_shader_effects", GenesisItemShaderConfig.class);
    public static final ConfigTypeEntry<GenesisItemOutlineConfig> ITEM_OUTLINE_EFFECTS =
            new ConfigTypeEntry<>(HANDLER, "item_outline_effects", GenesisItemOutlineConfig.class);

    static {
        EquipmentStatsManager.configure(Genesis.MOD_ID, EQUIPMENT_STATS::getAll);
        HANDLER.addAfterReloadListener(EquipmentStatsManager::rebuildFromConfig);
        HANDLER.addAfterReloadListener(() -> GenesisItemShaderRegistry.reloadFromConfigs(ITEM_SHADER_EFFECTS.getAll()));
        HANDLER.addAfterReloadListener(() -> GenesisItemOutlineRegistry.reloadFromConfigs(ITEM_OUTLINE_EFFECTS.getAll()));
    }

    private ModEquipmentStatsConfigs() {
    }

    public static void init() {
    }
}
