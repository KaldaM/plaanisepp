package ee.matteus.plaanisepp.gui;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GitHubReleaseServiceTest {
    private final GitHubReleaseService service = new GitHubReleaseService();

    @Test
    void readsLatestReleaseVersionAndPageFromGitHubResponse() {
        GitHubReleaseService.LatestRelease release = service.parseLatestRelease("""
                {
                  "tag_name": "v0.1.2",
                  "html_url": "https://github.com/KaldaM/plaanisepp/releases/tag/v0.1.2",
                  "assets": [
                    {
                      "name": "Plaanisepp-0.1.2.exe",
                      "browser_download_url": "https://example.test/Plaanisepp-0.1.2.exe",
                      "digest": "sha256:aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                    },
                    {
                      "name": "plaanisepp-0.1.2-4.x86_64.rpm",
                      "browser_download_url": "https://example.test/plaanisepp-0.1.2-4.x86_64.rpm",
                      "digest": "sha256:bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"
                    }
                  ]
                }
                """);

        assertEquals("0.1.2", release.version().toString());
        assertEquals("https://github.com/KaldaM/plaanisepp/releases/tag/v0.1.2", release.pageUrl().toString());
        assertEquals("Plaanisepp-0.1.2.exe", release.preferredAssetFor(
                GitHubReleaseService.Platform.WINDOWS
        ).orElseThrow().name());
        assertEquals("plaanisepp-0.1.2-4.x86_64.rpm", release.preferredAssetFor(
                GitHubReleaseService.Platform.FEDORA_LINUX
        ).orElseThrow().name());
    }

    @Test
    void comparesNumericVersionPartsInsteadOfText() {
        assertEquals(
                1,
                Integer.signum(GitHubReleaseService.ReleaseVersion.parse("0.1.10")
                        .compareTo(GitHubReleaseService.ReleaseVersion.parse("0.1.9")))
        );
    }

    @Test
    void rejectsIncompleteReleaseMetadata() {
        assertThrows(IllegalArgumentException.class, () -> service.parseLatestRelease("""
                { "tag_name": "v0.1.2", "html_url": "https://example.test" }
                """));
    }

    @Test
    void calculatesSha256ForDownloadedAsset() throws IOException {
        Path file = Files.createTempFile("plaanisepp-release-test", ".txt");
        try {
            Files.writeString(file, "Plaanisepp");

            assertEquals(
                    "fe0c88d606ab6160107e8e6e5f80443f2d45cd2456cd664b8895f4a199e1656c",
                    ReleaseAssetDownloadService.sha256(file)
            );
        } finally {
            Files.deleteIfExists(file);
        }
    }
}
