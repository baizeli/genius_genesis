package miku.united_as_one.genesis;

import com.mojang.logging.LogUtils;
import dev.xkmc.l2library.base.L2Registrate;
import miku.united_as_one.genesis.client.ClientSetup;
import miku.united_as_one.genesis.data.equipment.ModEquipmentStatsConfigs;
import miku.united_as_one.genesis.handlers.CommonEventHandler;
import miku.united_as_one.genesis.network.GenesisNetwork;
import miku.united_as_one.genesis.registries.BlockRegistry;
import miku.united_as_one.genesis.registries.CreativeTabRegistry;
import miku.united_as_one.genesis.registries.EntityRegistry;
import miku.united_as_one.genesis.registries.GenesisParticles;
import miku.united_as_one.genesis.registries.ItemRegistry;
import miku.united_as_one.genesis.registries.SoundRegistry;
import miku.united_as_one.genesis.registries.SpellAttributesRegistry;
import miku.united_as_one.genesis.registries.SpellRegistry;
import miku.united_as_one.genesis.registries.SpellSchoolRegistry;
import miku.united_as_one.genesis.fluid.FluidRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Genesis.MOD_ID)
public class Genesis {

    public static final String MOD_ID = "genius_genesis";
    public static final String MODID = MOD_ID;
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final L2Registrate L2_REGISTRATE = new L2Registrate(MOD_ID);

    public Genesis() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModEquipmentStatsConfigs.init();
        SoundRegistry.register(modBus);
        SpellAttributesRegistry.register(modBus);
        SpellSchoolRegistry.register(modBus);
        SpellRegistry.register(modBus);
        EntityRegistry.register(modBus);
        GenesisParticles.register(modBus);
        BlockRegistry.register();
        ItemRegistry.register();
        FluidRegistry.register();
        CreativeTabRegistry.register();
        GenesisNetwork.register();

        modBus.addListener(Genesis::commonSetup);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientSetup.register(modBus));

        MinecraftForge.EVENT_BUS.register(new CommonEventHandler());
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static ResourceLocation rl(String path) {
        return id(path);
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Loading Genius' Genesis");
    }
}
