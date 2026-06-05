package miku.united_as_one.genesis.data.datagen.provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import miku.united_as_one.genesis.Genesis;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModCuriosDataProvider implements DataProvider {
    private static final ResourceLocation CURIOS_TAG_VALIDATOR = new ResourceLocation("curios", "tag");

    private final PackOutput.PathProvider entitiesPathProvider;
    private final PackOutput.PathProvider slotsPathProvider;

    public ModCuriosDataProvider(PackOutput output) {
        this.entitiesPathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "curios/entities");
        this.slotsPathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "curios/slots");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        entities("genius_genesis_entities")
                .entity("player")
                .slots("spellbook", "ring", "necklace", "magic_guide", "crown")
                .save(output, futures);

        slot("crown")
                .icon(new ResourceLocation("curios", "slot/crown_slot"))
                .order(41)
                .size(1)
                .validator(CURIOS_TAG_VALIDATOR)
                .replace(false)
                .save(output, futures);

        slot("magic_guide")
                .icon(new ResourceLocation("curios", "slot/magic_guide_slot"))
                .order(201)
                .size(2)
                .validator(CURIOS_TAG_VALIDATOR)
                .replace(false)
                .save(output, futures);

        slot("ring")
                .size(2)
                .save(output, futures);

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Curios data for " + Genesis.MOD_ID;
    }

    private EntitiesBuilder entities(String name) {
        return new EntitiesBuilder(name);
    }

    private SlotBuilder slot(String name) {
        return new SlotBuilder(name);
    }

    private final class EntitiesBuilder {
        private final String name;
        private final List<String> entities = new ArrayList<>();
        private final List<String> slots = new ArrayList<>();

        private EntitiesBuilder(String name) {
            this.name = name;
        }

        private EntitiesBuilder entity(String entity) {
            entities.add(entity);
            return this;
        }

        private EntitiesBuilder slots(String... slots) {
            this.slots.addAll(List.of(slots));
            return this;
        }

        private void save(CachedOutput output, List<CompletableFuture<?>> futures) {
            JsonObject json = new JsonObject();
            json.add("entities", toJsonArray(entities));
            json.add("slots", toJsonArray(slots));
            futures.add(DataProvider.saveStable(output, json, entitiesPathProvider.json(new ResourceLocation(Genesis.MOD_ID, name))));
        }
    }

    private final class SlotBuilder {
        private final String name;
        private final List<ResourceLocation> validators = new ArrayList<>();
        private ResourceLocation icon;
        private Integer order;
        private Integer size;
        private Boolean replace;

        private SlotBuilder(String name) {
            this.name = name;
        }

        private SlotBuilder icon(ResourceLocation icon) {
            this.icon = icon;
            return this;
        }

        private SlotBuilder order(int order) {
            this.order = order;
            return this;
        }

        private SlotBuilder size(int size) {
            this.size = size;
            return this;
        }

        private SlotBuilder replace(boolean replace) {
            this.replace = replace;
            return this;
        }

        private SlotBuilder validator(ResourceLocation validator) {
            validators.add(validator);
            return this;
        }

        private void save(CachedOutput output, List<CompletableFuture<?>> futures) {
            JsonObject json = new JsonObject();
            if (icon != null) {
                json.addProperty("icon", icon.toString());
            }
            if (order != null) {
                json.addProperty("order", order);
            }
            if (replace != null) {
                json.addProperty("replace", replace);
            }
            if (size != null) {
                json.addProperty("size", size);
            }
            if (!validators.isEmpty()) {
                JsonArray validatorArray = new JsonArray();
                validators.stream().map(ResourceLocation::toString).forEach(validatorArray::add);
                json.add("validators", validatorArray);
            }
            futures.add(DataProvider.saveStable(output, json, slotsPathProvider.json(new ResourceLocation(Genesis.MOD_ID, name))));
        }
    }

    private static JsonArray toJsonArray(List<String> values) {
        JsonArray array = new JsonArray();
        values.forEach(array::add);
        return array;
    }
}
