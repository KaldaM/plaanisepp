package ee.matteus.plaanisepp.gui;

import ee.matteus.plaanisepp.core.model.Position;
import ee.matteus.plaanisepp.core.model.PowerSource;
import org.junit.jupiter.api.Test;

import java.util.List;

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

    @Test
    void renamedImportedCabinetIsNotImportedAgain() {
        PowerSource existing = new PowerSource("cabinet", "Ümber nimetatud", new Position(1, 2));
        existing.setNotes("Allikas: Tartu linn, püsivoolukilbi ID 42");
        TartuPowerCabinetImportService.Cabinet sameSource =
                new TartuPowerCabinetImportService.Cabinet(42, "PVK 42", 1, 2, "");

        assertTrue(TartuPowerCabinetImportService.newCabinets(
                List.of(sameSource), List.of(existing)
        ).isEmpty());
    }

    @Test
    void distinctSourceIdsWithSameNameAreBothImportable() {
        PowerSource existing = new PowerSource("cabinet", "PVK", new Position(1, 2));
        existing.setNotes("Allikas: Tartu linn, püsivoolukilbi ID 41");
        TartuPowerCabinetImportService.Cabinet otherSource =
                new TartuPowerCabinetImportService.Cabinet(42, "PVK", 3, 4, "");

        assertEquals(
                List.of(otherSource),
                TartuPowerCabinetImportService.newCabinets(List.of(otherSource), List.of(existing))
        );
    }
}
