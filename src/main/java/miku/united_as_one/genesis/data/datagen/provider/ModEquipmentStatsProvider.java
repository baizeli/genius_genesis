package miku.united_as_one.genesis.data.datagen.provider;

import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import miku.bai_ze_li.genesis.api.equipment.EquipmentStatsConfig;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.data.equipment.EquipmentStatsDefaults;
import miku.united_as_one.genesis.data.equipment.ModEquipmentStatsConfigs;
import net.minecraft.data.DataGenerator;

public class ModEquipmentStatsProvider extends ConfigDataProvider {
    public ModEquipmentStatsProvider(DataGenerator generator) {
        super(generator, Genesis.MOD_ID + " equipment stats");
    }

    @Override
    public void add(Collector map) {
        EquipmentStatsDefaults.all().forEach((id, stats) ->
                map.add(ModEquipmentStatsConfigs.EQUIPMENT_STATS, id, EquipmentStatsConfig.fromStats(stats)));
    }
}
