package ee.matteus.plaanisepp.gui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Reads the latest public Plaanisepp release without blocking the JavaFX thread. */
final class GitHubReleaseService {
    static final URI LATEST_RELEASE_URI = URI.create(
            "https://api.github.com/repos/KaldaM/plaanisepp/releases/latest"
    );
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    GitHubReleaseService() {
        this(HttpClient.newBuilder()
                .connectTimeout(REQUEST_TIMEOUT)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build(), new ObjectMapper());
    }

    GitHubReleaseService(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = Objects.requireNonNull(httpClient);
        this.objectMapper = Objects.requireNonNull(objectMapper);
    }

    CompletableFuture<LatestRelease> fetchLatestRelease() {
        HttpRequest request = HttpRequest.newBuilder(LATEST_RELEASE_URI)
                .timeout(REQUEST_TIMEOUT)
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .header("User-Agent", "Plaanisepp")
                .GET()
                .build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() != 200) {
                        throw new CompletionException(new IllegalStateException(
                                "GitHub Releases vastas olekukoodiga " + response.statusCode() + "."
                        ));
                    }
                    return parseLatestRelease(response.body());
                });
    }

    LatestRelease parseLatestRelease(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String tagName = requiredText(root, "tag_name");
            String releasePageUrl = requiredText(root, "html_url");
            JsonNode assetNodes = root.get("assets");
            if (assetNodes == null || !assetNodes.isArray()) {
                throw new IllegalArgumentException("GitHub Releasesi vastuses puudub assets.");
            }
            List<ReleaseAsset> assets = new ArrayList<>();
            for (JsonNode asset : assetNodes) {
                String name = requiredText(asset, "name");
                String downloadUrl = requiredText(asset, "browser_download_url");
                String digest = requiredText(asset, "digest");
                assets.add(new ReleaseAsset(name, URI.create(downloadUrl), Sha256Digest.parse(digest)));
            }
            return new LatestRelease(ReleaseVersion.parse(tagName), URI.create(releasePageUrl), assets);
        } catch (JsonProcessingException | IllegalArgumentException exception) {
            throw new IllegalArgumentException("GitHub Releasesi vastust ei saanud lugeda.", exception);
        }
    }

    private String requiredText(JsonNode root, String fieldName) {
        JsonNode node = root.path(fieldName);
        if (!node.isTextual() || node.asText().isBlank()) {
            throw new IllegalArgumentException("GitHub Releasesi vastuses puudub " + fieldName + ".");
        }
        return node.asText();
    }

    record LatestRelease(ReleaseVersion version, URI pageUrl, List<ReleaseAsset> assets) {
        LatestRelease {
            Objects.requireNonNull(version);
            Objects.requireNonNull(pageUrl);
            assets = List.copyOf(assets);
        }

        Optional<ReleaseAsset> preferredAssetFor(Platform platform) {
            return assets.stream()
                    .filter(asset -> asset.matches(platform))
                    .findFirst();
        }
    }

    record ReleaseAsset(String name, URI downloadUrl, Sha256Digest digest) {
        ReleaseAsset {
            Objects.requireNonNull(name);
            Objects.requireNonNull(downloadUrl);
            Objects.requireNonNull(digest);
        }

        boolean matches(Platform platform) {
            String lowercaseName = name.toLowerCase();
            return switch (platform) {
                case WINDOWS -> lowercaseName.endsWith(".exe");
                case FEDORA_LINUX -> lowercaseName.endsWith(".rpm");
                case OTHER_LINUX -> lowercaseName.endsWith(".tar.gz");
                case UNSUPPORTED -> false;
            };
        }
    }

    enum Platform {
        WINDOWS,
        FEDORA_LINUX,
        OTHER_LINUX,
        UNSUPPORTED;

        static Platform current() {
            String operatingSystem = System.getProperty("os.name", "").toLowerCase();
            if (operatingSystem.contains("win")) {
                return WINDOWS;
            }
            if (!operatingSystem.contains("linux")) {
                return UNSUPPORTED;
            }
            try {
                String releaseData = Files.readString(Path.of("/etc/os-release")).toLowerCase();
                return releaseData.contains("id=fedora") || releaseData.contains("id_like=fedora")
                        ? FEDORA_LINUX
                        : OTHER_LINUX;
            } catch (IOException exception) {
                return OTHER_LINUX;
            }
        }
    }

    record Sha256Digest(String value) {
        private static final Pattern FORMAT = Pattern.compile("[0-9a-f]{64}");

        Sha256Digest {
            if (!FORMAT.matcher(value).matches()) {
                throw new IllegalArgumentException("Vigane SHA-256 kontrollsumma.");
            }
        }

        static Sha256Digest parse(String value) {
            String normalizedValue = value == null ? "" : value.trim().toLowerCase();
            if (normalizedValue.startsWith("sha256:")) {
                normalizedValue = normalizedValue.substring("sha256:".length());
            }
            return new Sha256Digest(normalizedValue);
        }
    }

    record ReleaseVersion(int major, int minor, int patch) implements Comparable<ReleaseVersion> {
        private static final Pattern FORMAT = Pattern.compile("v?(\\d+)\\.(\\d+)\\.(\\d+)");

        static ReleaseVersion parse(String value) {
            Matcher matcher = FORMAT.matcher(value == null ? "" : value.trim());
            if (!matcher.matches()) {
                throw new IllegalArgumentException("Toetamata versioonivorming: " + value);
            }
            return new ReleaseVersion(
                    Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3))
            );
        }

        @Override
        public int compareTo(ReleaseVersion other) {
            int majorComparison = Integer.compare(major, other.major);
            if (majorComparison != 0) {
                return majorComparison;
            }
            int minorComparison = Integer.compare(minor, other.minor);
            return minorComparison != 0 ? minorComparison : Integer.compare(patch, other.patch);
        }

        @Override
        public String toString() {
            return major + "." + minor + "." + patch;
        }
    }
}
