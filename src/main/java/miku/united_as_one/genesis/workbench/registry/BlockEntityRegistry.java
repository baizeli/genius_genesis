package miku.united_as_one.genesis.workbench.registry;

import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.registries.BlockRegistry;
import miku.united_as_one.genesis.workbench.arcane.ArcaneWorkbenchBlockEntity;
import miku.united_as_one.genesis.workbench.arcane_cauldron.ArcaneCauldronBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class BlockEntityRegistry {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Genesis.MOD_ID);

    public static final RegistryObject<BlockEntityType<ArcaneCauldronBlockEntity>> ARCANE_CAULDRON =
            BLOCK_ENTITIES.register("arcane_cauldron", () -> BlockEntityType.Builder
                    .of(ArcaneCauldronBlockEntity::new, BlockRegistry.ARCANE_CAULDRON.get())
                    .build(null));

    public static final RegistryObject<BlockEntityType<ArcaneWorkbenchBlockEntity>> ARCANE_WORKBENCH =
            BLOCK_ENTITIES.register("arcane_workbench", () -> BlockEntityType.Builder
                    .of(ArcaneWorkbenchBlockEntity::new, BlockRegistry.ARCANE_WORKBENCH.get())
                    .build(null));

    private BlockEntityRegistry() {
    }

    public static void register(IEventBus modBus) {
        BLOCK_ENTITIES.register(modBus);
    }
}
