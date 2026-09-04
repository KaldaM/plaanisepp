package ee.matteus.plaanisepp.gui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.matteus.plaanisepp.core.map.BaseMapBounds;
import ee.matteus.plaanisepp.core.model.PlannerObject;
import ee.matteus.plaanisepp.core.model.PowerSource;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.OptionalLong;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class TartuPowerCabinetImportService {
    private static final String LAYER_URL = "https://gis.tartulv.ee/arcgis/rest/services/"
            + "Tanavavalgustus/TV_projekteerimiseks/FeatureServer/2";
    private static final String QUERY_URL = LAYER_URL + "/query";
    private static final Pattern SOURCE_ID_PATTERN = Pattern.compile(
            "(?m)^Allikas: Tartu linn, püsivoolukilbi ID (\\d+)\\s*$"
    );
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    List<Cabinet> load(BaseMapBounds bounds) throws IOException, InterruptedException {
        String geometry = bounds.minX() + "," + bounds.minY() + "," + bounds.maxX() + "," + bounds.maxY();
        String query = "where=1%3D1"
                + "&outFields=" + encode("OBJECTID,Nimi,Markus")
                + "&geometry=" + encode(geometry)
                + "&geometryType=esriGeometryEnvelope&inSR=3301&outSR=3301"
                + "&returnGeometry=true&f=json";
        HttpRequest request = HttpRequest.newBuilder(URI.create(QUERY_URL + "?" + query))
                .timeout(Duration.ofSeconds(45))
                .header("User-Agent", "Plaanisepp Tartu power-cabinet importer")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Tartu kaarditeenus vastas veakoodiga " + response.statusCode() + ".");
        }
        JsonNode root = objectMapper.readTree(response.body());
        if (root.has("error")) {
            throw new IOException("Tartu kaarditeenuse päring ebaõnnestus: "
                    + root.path("error").path("message").asText("tundmatu viga"));
        }
        List<Cabinet> result = new ArrayList<>();
        for (JsonNode feature : root.path("features")) {
            JsonNode attributes = feature.path("attributes");
            JsonNode geometryNode = feature.path("geometry");
            String name = attributes.path("Nimi").asText("").trim();
            if (name.isBlank() || !geometryNode.has("x") || !geometryNode.has("y")) {
                continue;
            }
            result.add(new Cabinet(
                    attributes.path("OBJECTID").asLong(),
                    name,
                    geometryNode.path("x").asDouble(),
                    geometryNode.path("y").asDouble(),
                    attributes.path("Markus").asText("").trim()
            ));
        }
        return List.copyOf(result);
    }

    List<Attachment> loadAttachments(long sourceId) throws IOException, InterruptedException {
        if (sourceId <= 0) {
            throw new IllegalArgumentException("Püsivoolukilbi ID peab olema positiivne.");
        }
        String attachmentsUrl = LAYER_URL + "/" + sourceId + "/attachments";
        HttpRequest request = HttpRequest.newBuilder(URI.create(attachmentsUrl + "?f=json"))
                .timeout(Duration.ofSeconds(45))
                .header("User-Agent", "Plaanisepp Tartu power-cabinet attachment viewer")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Tartu kaarditeenus vastas veakoodiga " + response.statusCode() + ".");
        }
        JsonNode root = objectMapper.readTree(response.body());
        if (root.has("error")) {
            throw new IOException("Tartu kaarditeenuse päring ebaõnnestus: "
                    + root.path("error").path("message").asText("tundmatu viga"));
        }
        List<Attachment> result = new ArrayList<>();
        for (JsonNode info : root.path("attachmentInfos")) {
            long attachmentId = info.path("id").asLong(-1);
            String name = info.path("name").asText("").trim();
            if (attachmentId <= 0 || name.isBlank()) {
                continue;
            }
            result.add(new Attachment(
                    attachmentId,
                    name,
                    info.path("contentType").asText("").trim(),
                    Math.max(0, info.path("size").asLong()),
                    attachmentsUrl + "/" + attachmentId
            ));
        }
        result.sort(Comparator.comparing(Attachment::name, String.CASE_INSENSITIVE_ORDER));
        return List.copyOf(result);
    }

    static OptionalLong sourceIdFromNotes(String notes) {
        if (notes == null || notes.isBlank()) {
            return OptionalLong.empty();
        }
        Matcher matcher = SOURCE_ID_PATTERN.matcher(notes);
        if (!matcher.find()) {
            return OptionalLong.empty();
        }
        try {
            return OptionalLong.of(Long.parseLong(matcher.group(1)));
        } catch (NumberFormatException exception) {
            return OptionalLong.empty();
        }
    }

    static List<Cabinet> newCabinets(List<Cabinet> cabinets, List<PlannerObject> existingObjects) {
        Set<Long> importedSourceIds = existingObjects.stream()
                .filter(PowerSource.class::isInstance)
                .map(PlannerObject::notes)
                .map(TartuPowerCabinetImportService::sourceIdFromNotes)
                .filter(OptionalLong::isPresent)
                .mapToLong(OptionalLong::getAsLong)
                .boxed()
                .collect(java.util.stream.Collectors.toSet());
        Set<String> namesWithoutSourceId = existingObjects.stream()
                .filter(PowerSource.class::isInstance)
                .filter(object -> sourceIdFromNotes(object.notes()).isEmpty())
                .map(object -> object.name().trim().toLowerCase(Locale.ROOT))
                .collect(java.util.stream.Collectors.toSet());
        return cabinets.stream()
                .filter(cabinet -> !importedSourceIds.contains(cabinet.sourceId()))
                .filter(cabinet -> !namesWithoutSourceId.contains(cabinet.name().toLowerCase(Locale.ROOT)))
                .toList();
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    record Cabinet(
            long sourceId,
            String name,
            double easting,
            double northing,
            String notes
    ) {
        String details() {
            List<String> lines = new ArrayList<>();
            lines.add("Allikas: Tartu linn, püsivoolukilbi ID " + sourceId);
            if (!notes.isBlank()) lines.add("Märkus: " + notes);
            return String.join(System.lineSeparator(), lines);
        }
    }

    record Attachment(long id, String name, String contentType, long sizeBytes, String url) {
    }
}
