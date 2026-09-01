package miku.united_as_one.genesis.data.datagen.provider;

import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import miku.bai_ze_li.genesis.api.render.outline.GenesisItemOutlineConfig;
import miku.bai_ze_li.genesis.api.render.shader.GenesisItemShaderConfig;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.data.equipment.ModEquipmentStatsConfigs;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;

public class ModGenesisConfigProvider extends ConfigDataProvider {
    public ModGenesisConfigProvider(DataGenerator generator) {
        super(generator, Genesis.MOD_ID + " item render configs");
    }

    @Override
    public void add(Collector map) {
        shader(map, "violet_galaxy_ingot", 0, 0.6F, 0.0F, 0.02F, 0.03F, 1.0F);
        shader(map, "laevatain", 15, 0.6F, 0.1F, 0.1F, 0.1F, 1.0F);
        shader(map, "scroll_celestial_source", 15, 0.6F, 0.1F, 0.1F, 0.1F, 1.0F);
        outline(map, "twisted_chaos_ingot", "black_red");
        outline(map, "mithril_sword", "blue_white");
        outline(map, "mithril_pickaxe", "blue_white");
        outline(map, "disk_spell_book", "blue_white");
        outline(map, "celestial_source_ingot", "rainbow");
    }

    private static void shader(Collector map, String id, int useType, float scale, float red, float green, float blue, float alpha) {
        GenesisItemShaderConfig config = new GenesisItemShaderConfig();
        config.use_type = useType;
        config.scale = scale;
        config.red = red;
        config.green = green;
        config.blue = blue;
        config.alpha = alpha;
        map.add(ModEquipmentStatsConfigs.ITEM_SHADER_EFFECTS, new ResourceLocation(Genesis.MOD_ID, id), config);
    }

    private static void outline(Collector map, String id, String effect) {
        GenesisItemOutlineConfig config = new GenesisItemOutlineConfig();
        config.effect = effect;
        map.add(ModEquipmentStatsConfigs.ITEM_OUTLINE_EFFECTS, new ResourceLocation(Genesis.MOD_ID, id), config);
    }
}
