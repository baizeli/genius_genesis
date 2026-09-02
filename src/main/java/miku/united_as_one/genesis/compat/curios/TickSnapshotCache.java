package miku.united_as_one.genesis.compat.curios;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

final class TickSnapshotCache<K, V> {
    private final Map<K, Entry<V>> entries = new HashMap<>();

    V get(K key, long tick, Supplier<V> loader) {
        Entry<V> entry = entries.get(key);
        if (entry == null || entry.tick != tick) {
            entry = new Entry<>(tick, loader.get());
            entries.put(key, entry);
        }
        return entry.value;
    }

    void invalidate(K key) {
        entries.remove(key);
    }

    void clear() {
        entries.clear();
    }

    private record Entry<V>(long tick, V value) {
    }
}
