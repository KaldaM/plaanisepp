package ee.matteus.plaanisepp.gui;

import ee.matteus.plaanisepp.core.model.Position;
import ee.matteus.plaanisepp.core.model.PowerSource;
import ee.matteus.plaanisepp.core.model.Tent;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlaaniseppAppOrganizerViewTest {
    @Test
    void organizerSelectionExcludesHiddenPowerSources() {
        Tent tent = new Tent("tent-1", "Telk", new Position(10, 20));
        PowerSource cabinet = new PowerSource("cabinet-1", "Kilp", new Position(30, 40));

        assertEquals(
                Set.of(tent.id()),
                PlaaniseppApp.organizerObjectIds(List.of(tent, cabinet))
        );
    }
}
