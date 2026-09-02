package ee.matteus.plaanisepp.core.map;

import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MaaAmetWmsClientTest {
    @Test
    void buildsEpsg3301RequestWithExactBoundsAndDimensions() {
        BaseMapBounds bounds = new BaseMapBounds(658_500, 6_473_500, 659_500, 6_474_250);

        URI uri = new MaaAmetWmsClient().requestUri(
                MaaAmetWmsClient.REGULAR_MAP_LAYER, bounds, 800, 600);

        String value = uri.toString();
        assertTrue(value.startsWith(MaaAmetWmsClient.SERVICE_URL + "?"));
        assertTrue(value.contains("SRS=EPSG%3A3301"));
        assertTrue(value.contains("BBOX=658500.0%2C6473500.0%2C659500.0%2C6474250.0"));
        assertTrue(value.contains("WIDTH=800"));
        assertTrue(value.contains("HEIGHT=600"));
    }

    @Test
    void usesDedicatedServiceForGrayscaleMap() {
        URI uri = new MaaAmetWmsClient().requestUri(
                MaaAmetWmsClient.GRAYSCALE_MAP_LAYER,
                new BaseMapBounds(658_500, 6_473_500, 659_500, 6_474_250),
                900,
                600
        );

        assertTrue(uri.toString().startsWith(MaaAmetWmsClient.GRAYSCALE_SERVICE_URL + "?"));
        assertTrue(uri.toString().contains("LAYERS=kaart_ht"));
    }

    @Test
    void calculatesPixelsPerMetreFromGeographicWidth() {
        BaseMapDownload download = new BaseMapDownload(
                new BaseMapBounds(0, 0, 1_000, 500),
                6_450,
                3_225,
                new byte[]{1},
                new byte[]{2}
        );

        assertEquals(6.45, download.pixelsPerMetre(), 0.000001);
    }

    @Test
    void rejectsOversizedRequestsBeforeNetworkAccess() {
        MaaAmetWmsClient client = new MaaAmetWmsClient();
        BaseMapBounds bounds = new BaseMapBounds(0, 0, 100, 100);

        assertThrows(IllegalArgumentException.class,
                () -> client.requestUri(MaaAmetWmsClient.ORTHOPHOTO_LAYER, bounds, 8_001, 100));
    }
}
