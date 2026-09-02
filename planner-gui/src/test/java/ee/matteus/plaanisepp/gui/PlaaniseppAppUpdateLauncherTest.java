package ee.matteus.plaanisepp.gui;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlaaniseppAppUpdateLauncherTest {

    @Test
    void windowsInstallerIsStartedDirectlyInsteadOfOpenedAsDesktopDocument() {
        Path installer = Path.of("C:\\Temp\\Plaanisepp-0.6.0.exe");

        assertEquals(
                List.of(installer.toString()),
                PlaaniseppApp.downloadedReleaseOpenCommand(
                        GitHubReleaseService.Platform.WINDOWS,
                        installer,
                        true
                )
        );
    }

    @Test
    void windowsDownloadDirectoryIsOpenedWithExplorer() {
        Path directory = Path.of("C:\\Temp\\plaanisepp-update");

        assertEquals(
                List.of("explorer.exe", directory.toString()),
                PlaaniseppApp.downloadedReleaseOpenCommand(
                        GitHubReleaseService.Platform.WINDOWS,
                        directory,
                        false
                )
        );
    }
}
