package miku.united_as_one.genesis.data.save;

import com.google.gson.*;
import miku.united_as_one.genesis.Genesis;
import miku.united_as_one.genesis.spell.chaos.ReversePlagueSpell;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

public class SaveManager {
    private static Path SAVE_PATH;
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(UUID.class, new UUIDTypeAdapter())
            .create();

    public static void init(MinecraftServer server) {
        SAVE_PATH = server.getWorldPath(LevelResource.ROOT)
                .resolve(Genesis.MOD_ID + "_save.json");
    }


    public static void save() {
        JsonObject root = new JsonObject();

        JsonObject entityMap = new JsonObject();
        for (Map.Entry<UUID, UUID> entry : ReversePlagueSpell.entityMap.entrySet()) {
            entityMap.addProperty(entry.getKey().toString(), entry.getValue().toString());
        }
        root.add("entity", entityMap);

        try (Writer writer = Files.newBufferedWriter(SAVE_PATH)) {
            GSON.toJson(root, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        if (!Files.exists(SAVE_PATH)) return;

        try (Reader reader = Files.newBufferedReader(SAVE_PATH)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            JsonObject entityMap = root.getAsJsonObject("entity");
            for (String key : entityMap.keySet()) {
                UUID uuid = UUID.fromString(key);
                UUID uuid1 = UUID.fromString(entityMap.get(key).getAsString());
                ReversePlagueSpell.entityMap.put(uuid, uuid1);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class UUIDTypeAdapter implements JsonSerializer<UUID>, JsonDeserializer<UUID> {
        public JsonElement serialize(UUID src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }

        public UUID deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return UUID.fromString(json.getAsString());
        }
    }
}