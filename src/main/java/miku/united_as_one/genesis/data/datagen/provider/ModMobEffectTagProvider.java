package miku.united_as_one.genesis.data.datagen.provider;

import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.registries.EffectRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModMobEffectTagProvider extends TagsProvider<MobEffect> {
    public static final TagKey<MobEffect> CELESTIAL_SOURCE_EFFECT = TagKey.create(
            Registries.MOB_EFFECT,
            Genesis.rl("spell_effect/celestial_source")
    );

    public static final TagKey<MobEffect> CHAOS_EFFECT = TagKey.create(
            Registries.MOB_EFFECT,
            Genesis.rl("spell_effect/chaos")
    );

    public ModMobEffectTagProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            ExistingFileHelper existingFileHelper
    ) {
        super(output, Registries.MOB_EFFECT, lookupProvider, Genesis.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(@NotNull HolderLookup.Provider provider) {
        tag(CELESTIAL_SOURCE_EFFECT);
        tag(CHAOS_EFFECT).add(
                EffectRegistry.BLOOD_FRENZY.getKey()
        );
    }
}
