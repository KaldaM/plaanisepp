package ee.matteus.plaanisepp.gui;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.prefs.Preferences;

final class RecentPlanFiles {
    private static final String PREFERENCE_PREFIX = "recentPlanFile.";
    private static final int MAX_FILES = 10;

    private final Preferences preferences;

    RecentPlanFiles(Preferences preferences) {
        this.preferences = preferences;
    }

    List<Path> load() {
        List<Path> existingFiles = new ArrayList<>();
        for (int index = 0; index < MAX_FILES; index++) {
            String value = preferences.get(PREFERENCE_PREFIX + index, "");
            if (value.isBlank()) {
                continue;
            }
            try {
                Path path = Path.of(value).toAbsolutePath().normalize();
                if (Files.isRegularFile(path) && !existingFiles.contains(path)) {
                    existingFiles.add(path);
                }
            } catch (InvalidPathException ignored) {
                // Vigane või teise operatsioonisüsteemi tee eemaldatakse loendist.
            }
        }
        store(existingFiles);
        return List.copyOf(existingFiles);
    }

    void remember(File file) {
        if (file == null) {
            return;
        }
        Path path = file.toPath().toAbsolutePath().normalize();
        LinkedHashSet<Path> orderedFiles = new LinkedHashSet<>();
        orderedFiles.add(path);
        orderedFiles.addAll(load());
        store(orderedFiles.stream().limit(MAX_FILES).toList());
    }

    void remove(Path path) {
        if (path == null) {
            return;
        }
        Path normalizedPath = path.toAbsolutePath().normalize();
        store(load().stream().filter(file -> !file.equals(normalizedPath)).toList());
    }

    private void store(List<Path> files) {
        for (int index = 0; index < MAX_FILES; index++) {
            if (index < files.size()) {
                preferences.put(PREFERENCE_PREFIX + index, files.get(index).toString());
            } else {
                preferences.remove(PREFERENCE_PREFIX + index);
            }
        }
    }
}
