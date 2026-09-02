package miku.united_as_one.genesis.compat.curios;

import java.util.concurrent.atomic.AtomicInteger;

public final class TickSnapshotCacheTest {
    private TickSnapshotCacheTest() {
    }

    public static void main(String[] args) {
        TickSnapshotCache<String, String> cache = new TickSnapshotCache<>();
        AtomicInteger loads = new AtomicInteger();

        assertEquals("snapshot-1", cache.get("player", 10L, () -> "snapshot-" + loads.incrementAndGet()));
        assertEquals("snapshot-1", cache.get("player", 10L, () -> "snapshot-" + loads.incrementAndGet()));
        assertEquals(1, loads.get());

        assertEquals("snapshot-2", cache.get("player", 11L, () -> "snapshot-" + loads.incrementAndGet()));
        assertEquals(2, loads.get());

        cache.invalidate("player");
        assertEquals("snapshot-3", cache.get("player", 11L, () -> "snapshot-" + loads.incrementAndGet()));
        assertEquals(3, loads.get());
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("expected " + expected + ", got " + actual);
        }
    }
}
