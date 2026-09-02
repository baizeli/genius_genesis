package miku.united_as_one.genesis.content;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ContentPackageMigrationTest {
    private static final List<String> DOMAINS = List.of("block", "item", "entity", "spell", "fluid");

    private ContentPackageMigrationTest() {
    }

    public static void main(String[] args) throws IOException {
        Path sourceRoot = Path.of(args.length == 0 ? "src" : args[0]);
        List<String> stale = new ArrayList<>();
        try (var paths = Files.walk(sourceRoot)) {
            paths.filter(path -> path.toString().endsWith(".java")).forEach(path -> inspect(path, stale));
        }
        if (!stale.isEmpty()) {
            throw new AssertionError("old content package prefixes remain:\n" + String.join("\n", stale));
        }
    }

    private static void inspect(Path path, List<String> stale) {
        try {
            String source = Files.readString(path);
            String base = "miku.united_as_one.genesis.";
            for (String domain : DOMAINS) {
                String prefix = base + domain;
                if (source.contains("package " + prefix) || source.contains("import " + prefix)) {
                    stale.add(path + " -> " + prefix);
                }
            }
        } catch (IOException exception) {
            throw new IllegalStateException("failed to inspect " + path, exception);
        }
    }
}
