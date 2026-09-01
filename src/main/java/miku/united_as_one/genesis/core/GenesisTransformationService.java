package miku.united_as_one.genesis.core;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;

import java.util.List;
import java.util.Set;

public final class GenesisTransformationService implements ITransformationService {
    @Override public String name() { return "genius_genesis_enum_extensions"; }
    @Override public void initialize(IEnvironment environment) { }
    @Override public void onLoad(IEnvironment environment, Set<String> otherServices) { }
    @Override public List<ITransformer> transformers() { return List.of(new SpellRarityTransformer()); }
}
