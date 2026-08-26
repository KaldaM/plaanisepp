package ee.matteus.plaanisepp.gui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GitHubReleaseServiceTest {
    private final GitHubReleaseService service = new GitHubReleaseService();

    @Test
    void readsLatestReleaseVersionAndPageFromGitHubResponse() {
        GitHubReleaseService.LatestRelease release = service.parseLatestRelease("""
                {
                  "tag_name": "v0.1.2",
                  "html_url": "https://github.com/KaldaM/plaanisepp/releases/tag/v0.1.2"
                }
                """);

        assertEquals("0.1.2", release.version().toString());
        assertEquals("https://github.com/KaldaM/plaanisepp/releases/tag/v0.1.2", release.pageUrl().toString());
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
                { "tag_name": "v0.1.2" }
                """));
    }
}
