package miku.united_as_one.genesis.client.render.cosmic;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

public final class CosmicModelLoader implements IGeometryLoader<CosmicModelLoader.CosmicGeometry> {
    public static final CosmicModelLoader INSTANCE = new CosmicModelLoader();

    private CosmicModelLoader() {
    }

    @Override
    public CosmicGeometry read(JsonObject modelContents, JsonDeserializationContext deserializationContext) throws JsonParseException {
        JsonObject cosmic = modelContents.getAsJsonObject("cosmic");
        if (cosmic == null) {
            throw new JsonParseException("Missing 'cosmic' object.");
        }

        List<String> masks = new ArrayList<>();
        if (cosmic.has("mask") && cosmic.get("mask").isJsonArray()) {
            JsonArray maskArray = cosmic.getAsJsonArray("mask");
            for (int i = 0; i < maskArray.size(); i++) {
                masks.add(maskArray.get(i).getAsString());
            }
        } else {
            masks.add(GsonHelper.getAsString(cosmic, "mask"));
        }

        JsonObject baseModelJson = modelContents.deepCopy();
        baseModelJson.remove("cosmic");
        baseModelJson.remove("loader");
        BlockModel baseModel = deserializationContext.deserialize(baseModelJson, BlockModel.class);
        return new CosmicGeometry(baseModel, masks);
    }

    public record CosmicGeometry(BlockModel baseModel, List<String> masks) implements IUnbakedGeometry<CosmicGeometry> {
        @Override
        public BakedModel bake(IGeometryBakingContext context, ModelBaker baker,
                               Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState,
                               ItemOverrides overrides, ResourceLocation modelLocation) {
            BakedModel baked = baseModel.bake(baker, baseModel, spriteGetter, modelState, modelLocation, true);
            List<ResourceLocation> maskTextures = masks.stream().map(ResourceLocation::parse).toList();
            return new CosmicBakedModel(baked, maskTextures);
        }

        @Override
        public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
            baseModel.resolveParents(modelGetter);
        }
    }
}
