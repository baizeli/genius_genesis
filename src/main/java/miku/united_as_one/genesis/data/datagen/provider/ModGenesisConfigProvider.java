package miku.united_as_one.genesis.data.datagen.provider;

import com.google.gson.JsonObject;
import miku.united_as_one.genesis.Genesis;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModGenesisConfigProvider implements DataProvider {
    private final PackOutput.PathProvider shaderPathProvider;
    private final PackOutput.PathProvider outlinePathProvider;

    public ModGenesisConfigProvider(PackOutput output) {
        this.shaderPathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, Genesis.MOD_ID + "_config/item_shader_effects");
        this.outlinePathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, Genesis.MOD_ID + "_config/item_outline_effects");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        shader("violet_galaxy_ingot", 0, 0.6F, 0.0F, 0.02F, 0.03F, 1.0F, output, futures);
        shader("laevatain", 15, 0.6F, 0.1F, 0.1F, 0.1F, 1.0F, output, futures);

        outline("twisted_chaos_ingot", "black_red", output, futures);
        outline("mithril_sword", "blue_white", output, futures);
        outline("mithril_pickaxe", "blue_white", output, futures);
        outline("disk_spell_book", "blue_white", output, futures);
        outline("celestial_source_ingot", "rainbow", output, futures);

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return Genesis.MOD_ID + " item render configs";
    }

    private void shader(String id, int useType, float scale, float red, float green, float blue, float alpha,
                        CachedOutput output, List<CompletableFuture<?>> futures) {
        JsonObject json = new JsonObject();
        json.addProperty("use_type", useType);
        json.addProperty("scale", scale);
        json.addProperty("red", red);
        json.addProperty("green", green);
        json.addProperty("blue", blue);
        json.addProperty("alpha", alpha);
        futures.add(DataProvider.saveStable(output, json, shaderPathProvider.json(new ResourceLocation(Genesis.MOD_ID, id))));
    }

    private void outline(String id, String effect, CachedOutput output, List<CompletableFuture<?>> futures) {
        JsonObject json = new JsonObject();
        json.addProperty("effect", effect);
        futures.add(DataProvider.saveStable(output, json, outlinePathProvider.json(new ResourceLocation(Genesis.MOD_ID, id))));
    }
}
