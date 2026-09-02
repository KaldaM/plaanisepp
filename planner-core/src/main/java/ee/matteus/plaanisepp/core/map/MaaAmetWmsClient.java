package ee.matteus.plaanisepp.core.map;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

public final class MaaAmetWmsClient {
    public static final String SERVICE_URL = "https://kaart.maaamet.ee/wms/alus";
    public static final String GRAYSCALE_SERVICE_URL = "https://kaart.maaamet.ee/wms/hallkaart";
    public static final String REGULAR_MAP_LAYER = "pohi_vr2";
    public static final String GRAYSCALE_MAP_LAYER = "kaart_ht";
    public static final String ORTHOPHOTO_LAYER = "of10000";
    public static final int MAX_DIMENSION = 8_000;
    public static final long MAX_PIXELS = 40_000_000;
    private static final int MAX_RESPONSE_BYTES = 50 * 1024 * 1024;
    private static final int TILE_SIZE = 4_000;
    private static final double CARTOGRAPHIC_PIXELS_PER_METRE = 6.45;

    private final HttpClient httpClient;

    public MaaAmetWmsClient() {
        this(HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build());
    }

    MaaAmetWmsClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public BaseMapDownload download(BaseMapBounds bounds, int width, int height)
            throws IOException, InterruptedException {
        return download(bounds, width, height, RegularMapStyle.GRAYSCALE);
    }

    public BaseMapDownload download(
            BaseMapBounds bounds,
            int width,
            int height,
            RegularMapStyle regularMapStyle
    ) throws IOException, InterruptedException {
        validateDimensions(width, height);
        String regularLayer = regularMapStyle == RegularMapStyle.GRAYSCALE
                ? GRAYSCALE_MAP_LAYER : REGULAR_MAP_LAYER;
        byte[] regular = downloadCartographicLayer(regularLayer, bounds, width, height);
        byte[] orthophoto = downloadLayer(ORTHOPHOTO_LAYER, bounds, width, height);
        return new BaseMapDownload(bounds, width, height, regular, orthophoto);
    }

    public byte[] downloadPreview(BaseMapBounds bounds, int width, int height)
            throws IOException, InterruptedException {
        return downloadPreview(bounds, width, height, RegularMapStyle.GRAYSCALE);
    }

    public byte[] downloadPreview(
            BaseMapBounds bounds,
            int width,
            int height,
            RegularMapStyle regularMapStyle
    ) throws IOException, InterruptedException {
        validateDimensions(width, height);
        return downloadLayer(regularMapStyle == RegularMapStyle.GRAYSCALE
                ? GRAYSCALE_MAP_LAYER : REGULAR_MAP_LAYER, bounds, width, height);
    }

    URI requestUri(String layer, BaseMapBounds bounds, int width, int height) {
        validateDimensions(width, height);
        Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("SERVICE", "WMS");
        parameters.put("VERSION", "1.1.1");
        parameters.put("REQUEST", "GetMap");
        parameters.put("LAYERS", layer);
        parameters.put("STYLES", "");
        parameters.put("SRS", "EPSG:3301");
        parameters.put("BBOX", bounds.minX() + "," + bounds.minY() + "," + bounds.maxX() + "," + bounds.maxY());
        parameters.put("WIDTH", Integer.toString(width));
        parameters.put("HEIGHT", Integer.toString(height));
        parameters.put("FORMAT", "image/png");
        parameters.put("TRANSPARENT", "FALSE");
        String query = parameters.entrySet().stream()
                .map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue()))
                .reduce((first, second) -> first + "&" + second)
                .orElseThrow();
        String serviceUrl = GRAYSCALE_MAP_LAYER.equals(layer) ? GRAYSCALE_SERVICE_URL : SERVICE_URL;
        return URI.create(serviceUrl + "?" + query);
    }

    private byte[] downloadLayer(String layer, BaseMapBounds bounds, int width, int height)
            throws IOException, InterruptedException {
        if (width > TILE_SIZE || height > TILE_SIZE) {
            return downloadLayerInTiles(layer, bounds, width, height);
        }
        return downloadSingleImage(layer, bounds, width, height);
    }

    private byte[] downloadCartographicLayer(String layer, BaseMapBounds bounds, int width, int height)
            throws IOException, InterruptedException {
        double requestedPixelsPerMetre = width / bounds.widthMetres();
        if (requestedPixelsPerMetre <= CARTOGRAPHIC_PIXELS_PER_METRE) {
            return downloadLayer(layer, bounds, width, height);
        }
        double factor = CARTOGRAPHIC_PIXELS_PER_METRE / requestedPixelsPerMetre;
        int sourceWidth = Math.max(1, (int) Math.round(width * factor));
        int sourceHeight = Math.max(1, (int) Math.round(height * factor));
        BufferedImage source = ImageIO.read(new ByteArrayInputStream(
                downloadLayer(layer, bounds, sourceWidth, sourceHeight)));
        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = scaled.createGraphics();
        try {
            graphics.setRenderingHint(
                    java.awt.RenderingHints.KEY_INTERPOLATION,
                    java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR
            );
            graphics.drawImage(source, 0, 0, width, height, null);
        } finally {
            graphics.dispose();
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(scaled, "png", output);
        return output.toByteArray();
    }

    private byte[] downloadLayerInTiles(String layer, BaseMapBounds bounds, int width, int height)
            throws IOException, InterruptedException {
        BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = combined.createGraphics();
        try {
            for (int y = 0; y < height; y += TILE_SIZE) {
                int tileHeight = Math.min(TILE_SIZE, height - y);
                for (int x = 0; x < width; x += TILE_SIZE) {
                    int tileWidth = Math.min(TILE_SIZE, width - x);
                    BaseMapBounds tileBounds = tileBounds(bounds, width, height, x, y, tileWidth, tileHeight);
                    byte[] data = downloadSingleImage(layer, tileBounds, tileWidth, tileHeight);
                    BufferedImage tile = ImageIO.read(new ByteArrayInputStream(data));
                    graphics.drawImage(tile, x, y, null);
                }
            }
        } finally {
            graphics.dispose();
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        if (!ImageIO.write(combined, "png", output)) {
            throw new IOException("Aluskaardi PNG-pildi koostamine ebaõnnestus.");
        }
        return output.toByteArray();
    }

    private BaseMapBounds tileBounds(
            BaseMapBounds bounds,
            int width,
            int height,
            int x,
            int y,
            int tileWidth,
            int tileHeight
    ) {
        double minX = bounds.minX() + bounds.widthMetres() * x / width;
        double maxX = bounds.minX() + bounds.widthMetres() * (x + tileWidth) / width;
        double maxY = bounds.maxY() - bounds.heightMetres() * y / height;
        double minY = bounds.maxY() - bounds.heightMetres() * (y + tileHeight) / height;
        return new BaseMapBounds(minX, minY, maxX, maxY);
    }

    private byte[] downloadSingleImage(String layer, BaseMapBounds bounds, int width, int height)
            throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(requestUri(layer, bounds, width, height))
                .timeout(Duration.ofSeconds(60))
                .header("User-Agent", "Plaanisepp base-map downloader")
                .GET()
                .build();
        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() != 200) {
            throw new IOException("Kaarditeenus vastas veakoodiga " + response.statusCode() + ".");
        }
        byte[] data = response.body();
        if (data.length == 0 || data.length > MAX_RESPONSE_BYTES) {
            throw new IOException("Kaarditeenus tagastas tühja või liiga suure vastuse.");
        }
        if (ImageIO.read(new ByteArrayInputStream(data)) == null) {
            String serviceMessage = new String(data, StandardCharsets.UTF_8)
                    .replaceAll("<[^>]+>", " ").replaceAll("\\s+", " ").trim();
            if (serviceMessage.length() > 240) serviceMessage = serviceMessage.substring(0, 240) + "…";
            throw new IOException(serviceMessage.isBlank()
                    ? "Kaarditeenus ei tagastanud korrektset pilti."
                    : "Kaarditeenuse teade: " + serviceMessage);
        }
        return data;
    }

    private static void validateDimensions(int width, int height) {
        if (width <= 0 || height <= 0 || width > MAX_DIMENSION || height > MAX_DIMENSION
                || (long) width * height > MAX_PIXELS) {
            throw new IllegalArgumentException("Aluskaardi väljundmõõtmed ei ole toetatud.");
        }
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
