package ee.matteus.plaanisepp.gui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TartuPowerCabinetImportServiceTest {
    @Test
    void detailsContainOnlySourceAndUsefulNotes() {
        TartuPowerCabinetImportService.Cabinet cabinet =
                new TartuPowerCabinetImportService.Cabinet(42, "PVK 42", 1, 2, "Võti valvelauas");

        assertEquals("Allikas: Tartu linn, püsivoolukilbi ID 42"
                + System.lineSeparator() + "Märkus: Võti valvelauas", cabinet.details());
    }

    @Test
    void findsSourceIdFromImportedCabinetNotes() {
        assertEquals(42, TartuPowerCabinetImportService.sourceIdFromNotes(
                "Allikas: Tartu linn, püsivoolukilbi ID 42\nMärkus: Võti valvelauas"
        ).orElseThrow());
        assertTrue(TartuPowerCabinetImportService.sourceIdFromNotes("Kohalik märkus").isEmpty());
    }
}
