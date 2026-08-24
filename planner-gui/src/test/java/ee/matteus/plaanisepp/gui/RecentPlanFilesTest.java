package ee.matteus.plaanisepp.gui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecentPlanFilesTest {
    @TempDir
    Path temporaryDirectory;

    private final Preferences preferences = Preferences.userRoot().node(
            "/ee/matteus/plaanisepp/tests/recent-plans-" + UUID.randomUUID()
    );

    @AfterEach
    void removePreferences() throws BackingStoreException {
        preferences.removeNode();
    }

    @Test
    void keepsMostRecentlyUsedExistingFilesFirstWithoutDuplicates() throws IOException {
        Path first = Files.createFile(temporaryDirectory.resolve("first.pplan"));
        Path second = Files.createFile(temporaryDirectory.resolve("second.pplan"));
        RecentPlanFiles recentFiles = new RecentPlanFiles(preferences);

        recentFiles.remember(first.toFile());
        recentFiles.remember(second.toFile());
        recentFiles.remember(first.toFile());

        assertEquals(List.of(first, second), recentFiles.load());
    }

    @Test
    void removesFilesThatNoLongerExist() throws IOException {
        Path removed = Files.createFile(temporaryDirectory.resolve("removed.pplan"));
        Path existing = Files.createFile(temporaryDirectory.resolve("existing.pplan"));
        RecentPlanFiles recentFiles = new RecentPlanFiles(preferences);
        recentFiles.remember(existing.toFile());
        recentFiles.remember(removed.toFile());

        Files.delete(removed);

        assertEquals(List.of(existing), recentFiles.load());
    }

    @Test
    void keepsAtMostTenFiles() throws IOException {
        RecentPlanFiles recentFiles = new RecentPlanFiles(preferences);
        for (int index = 0; index < 12; index++) {
            recentFiles.remember(Files.createFile(
                    temporaryDirectory.resolve("plan-" + index + ".pplan")
            ).toFile());
        }

        List<Path> loaded = recentFiles.load();
        assertEquals(10, loaded.size());
        assertEquals(temporaryDirectory.resolve("plan-11.pplan"), loaded.getFirst());
        assertEquals(temporaryDirectory.resolve("plan-2.pplan"), loaded.getLast());
    }
}
