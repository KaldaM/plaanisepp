package ee.matteus.plaanisepp.gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/** Downloads a release asset to a temporary file and verifies it before making it available. */
final class ReleaseAssetDownloadService {
    private static final Duration REQUEST_TIMEOUT = Duration.ofMinutes(5);

    private final HttpClient httpClient;

    ReleaseAssetDownloadService() {
        this(HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build());
    }

    ReleaseAssetDownloadService(HttpClient httpClient) {
        this.httpClient = Objects.requireNonNull(httpClient);
    }

    CompletableFuture<Path> downloadAndVerify(GitHubReleaseService.ReleaseAsset asset, Path destination) {
        Objects.requireNonNull(asset);
        Objects.requireNonNull(destination);
        Path absoluteDestination = destination.toAbsolutePath();
        Path temporaryFile = absoluteDestination.resolveSibling(absoluteDestination.getFileName() + ".part");
        try {
            Path parent = absoluteDestination.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (IOException exception) {
            return CompletableFuture.failedFuture(exception);
        }

        HttpRequest request = HttpRequest.newBuilder(asset.downloadUrl())
                .timeout(REQUEST_TIMEOUT)
                .header("Accept", "application/octet-stream")
                .header("User-Agent", "Plaanisepp")
                .GET()
                .build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofFile(
                        temporaryFile,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING,
                        StandardOpenOption.WRITE
                ))
                .thenApply(response -> {
                    if (response.statusCode() != 200) {
                        throw new CompletionException(new IOException(
                                "Allalaadimine vastas olekukoodiga " + response.statusCode() + "."
                        ));
                    }
                    try {
                        String actualDigest = sha256(temporaryFile);
                        if (!actualDigest.equals(asset.digest().value())) {
                            throw new IOException("Allalaaditud faili SHA-256 kontrollsumma ei klapi.");
                        }
                        moveToDestination(temporaryFile, absoluteDestination);
                        return absoluteDestination;
                    } catch (IOException exception) {
                        throw new CompletionException(exception);
                    }
                })
                .whenComplete((ignored, exception) -> {
                    if (exception != null) {
                        try {
                            Files.deleteIfExists(temporaryFile);
                        } catch (IOException ignoredException) {
                            // A failed cleanup does not make an unverified download usable.
                        }
                    }
                });
    }

    static String sha256(Path file) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (InputStream input = Files.newInputStream(file)) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = input.read(buffer)) != -1) {
                    digest.update(buffer, 0, read);
                }
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Java keskkonnas puudub SHA-256 tugi.", exception);
        }
    }

    private static void moveToDestination(Path temporaryFile, Path destination) throws IOException {
        try {
            Files.move(temporaryFile, destination,
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
