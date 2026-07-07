package miku.united_as_one.genesis.data.datagen.provider;

import io.redspace.ironsspellbooks.registries.UpgradeOrbTypeRegistry;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.data.damage.DamageTypes;
import miku.united_as_one.genesis.worldgen.ModBiomeModifiers;
import miku.united_as_one.genesis.worldgen.ModBiomes;
import miku.united_as_one.genesis.worldgen.ModWorldgen;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModDatapackEntriesProvider extends DatapackBuiltinEntriesProvider {
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, DamageTypes::bootstrap)
            .add(UpgradeOrbTypeRegistry.UPGRADE_ORB_REGISTRY_KEY, ModUpgradeOrbTypeProvider::bootstrap)
            .add(Registries.CONFIGURED_FEATURE, ModWorldgen::bootstrapConfiguredFeatures)
            .add(Registries.PLACED_FEATURE, ModWorldgen::bootstrapPlacedFeatures)
            .add(ForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap)
            .add(Registries.BIOME, ModBiomes::bootstrap)
            .add(Registries.TEMPLATE_POOL, ModWorldgen::bootstrapTemplatePools)
            .add(Registries.STRUCTURE, ModWorldgen::bootstrapStructures)
            .add(Registries.STRUCTURE_SET, ModWorldgen::bootstrapStructureSets);

    public ModDatapackEntriesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, BUILDER, Set.of("minecraft", "forge", Genesis.MOD_ID, "irons_spellbooks"));
    }
}
