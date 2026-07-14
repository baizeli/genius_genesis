package miku.united_as_one.genesis.nbt;

import net.minecraft.resources.ResourceLocation;

public record PersistentDataKey(String value) {
    public static PersistentDataKey of(String namespace, String path) {
        return new PersistentDataKey(ResourceLocation.fromNamespaceAndPath(namespace, path).toString());
    }
}
