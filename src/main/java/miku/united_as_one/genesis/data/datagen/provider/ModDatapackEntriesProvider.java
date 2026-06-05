package miku.united_as_one.genesis.data.datagen.provider;

import io.redspace.ironsspellbooks.registries.UpgradeOrbTypeRegistry;
import miku.united_as_one.genesis.Genesis;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModDatapackEntriesProvider extends DatapackBuiltinEntriesProvider {
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(UpgradeOrbTypeRegistry.UPGRADE_ORB_REGISTRY_KEY, ModUpgradeOrbTypeProvider::bootstrap);

    public ModDatapackEntriesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, BUILDER, Set.of("minecraft", Genesis.MOD_ID, "irons_spellbooks"));
    }
}
