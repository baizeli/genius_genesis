package miku.united_as_one.genesis.workbench.registry;

import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.workbench.arcane.ArcaneWorkbenchBlockEntity;
import miku.united_as_one.genesis.workbench.arcane.ArcaneWorkbenchMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class MenuTypeRegistry {
    private static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, Genesis.MOD_ID);

    public static final RegistryObject<MenuType<ArcaneWorkbenchMenu>> ARCANE_WORKBENCH =
            MENU_TYPES.register("arcane_workbench_menu", () -> IForgeMenuType.create((windowId, inventory, data) -> {
                BlockPos pos = data.readBlockPos();
                if (inventory.player.level().getBlockEntity(pos) instanceof ArcaneWorkbenchBlockEntity workbench) {
                    return new ArcaneWorkbenchMenu(MenuTypeRegistry.ARCANE_WORKBENCH.get(), windowId, inventory, workbench);
                }
                return new ArcaneWorkbenchMenu(MenuTypeRegistry.ARCANE_WORKBENCH.get(), windowId, inventory);
            }));

    private MenuTypeRegistry() {
    }

    public static void register(IEventBus modBus) {
        MENU_TYPES.register(modBus);
    }
}
