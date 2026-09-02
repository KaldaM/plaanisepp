package ee.matteus.plaanisepp.gui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.matteus.plaanisepp.core.map.BaseMapBounds;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

final class TartuPowerCabinetImportService {
    private static final String QUERY_URL = "https://gis.tartulv.ee/arcgis/rest/services/"
            + "Tanavavalgustus/TV_projekteerimiseks/FeatureServer/2/query";
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    List<Cabinet> load(BaseMapBounds bounds) throws IOException, InterruptedException {
        String geometry = bounds.minX() + "," + bounds.minY() + "," + bounds.maxX() + "," + bounds.maxY();
        String query = "where=1%3D1"
                + "&outFields=" + encode("OBJECTID,Nimi,Markus,Mark,PVK_maxvool,N_vool")
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
                    attributes.path("Markus").asText("").trim(),
                    attributes.path("Mark").asText("").trim(),
                    nullableInteger(attributes.get("PVK_maxvool")),
                    nullableInteger(attributes.get("N_vool"))
            ));
        }
        return List.copyOf(result);
    }

    private static Integer nullableInteger(JsonNode node) {
        return node == null || node.isNull() || !node.isNumber() ? null : node.intValue();
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    record Cabinet(
            long sourceId,
            String name,
            double easting,
            double northing,
            String notes,
            String model,
            Integer maximumCurrentAmps,
            Integer nominalCurrentAmps
    ) {
        String details() {
            List<String> lines = new ArrayList<>();
            lines.add("Allikas: Tartu linn, püsivoolukilbi ID " + sourceId);
            if (!model.isBlank()) lines.add("Mark: " + model);
            if (maximumCurrentAmps != null) lines.add("Maksimumvool: " + maximumCurrentAmps + " A");
            if (nominalCurrentAmps != null) lines.add("Nimivool: " + nominalCurrentAmps + " A");
            if (!notes.isBlank()) lines.add("Märkus: " + notes);
            return String.join(System.lineSeparator(), lines);
        }
    }
}
