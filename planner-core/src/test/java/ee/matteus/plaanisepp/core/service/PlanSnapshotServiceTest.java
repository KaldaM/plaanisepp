package ee.matteus.plaanisepp.core.service;

import ee.matteus.plaanisepp.core.model.ConnectorType;
import ee.matteus.plaanisepp.core.model.Equipment;
import ee.matteus.plaanisepp.core.model.EventPlan;
import ee.matteus.plaanisepp.core.model.Position;
import ee.matteus.plaanisepp.core.model.PowerOutlet;
import ee.matteus.plaanisepp.core.model.PowerSource;
import ee.matteus.plaanisepp.core.model.Tent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlanSnapshotServiceTest {
    @Test
    void restoresAnIndependentPlanWithObjectsPowerAndPackagedMap() {
        EventPlan original = new EventPlan("Testplaan");
        original.setPixelsPerMeter(30);
        original.setPackagedMapImage("map/map.png", new byte[]{1, 2, 3, 4});

        PowerSource source = new PowerSource("source", "Kapp", new Position(10, 20));
        source.addOutlet(new PowerOutlet("outlet", "Pesa", ConnectorType.SCHUKO_230V, 3500));
        Tent tent = new Tent("tent", "Telk", new Position(100, 120));
        tent.setSizeMeters(4, 6);
        tent.addEquipment(new Equipment("equipment", "Kohvimasin", 1800, ""));
        original.addObject(source);
        original.addObject(tent);
        assertTrue(original.connectToPower(
                source.id(), tent.id(), ConnectorType.SCHUKO_230V, "outlet"
        ).isPresent());

        PlanSnapshotService service = new PlanSnapshotService();
        PlanSnapshot snapshot = service.create(original);
        tent.rename("Muudetud telk");
        tent.equipment().getFirst().setRequiredWatts(900);
        original.rename("Muudetud plaan");

        EventPlan restored = service.restore(snapshot);

        assertNotSame(original, restored);
        assertEquals("Testplaan", restored.name());
        assertEquals(30, restored.pixelsPerMeter());
        assertEquals("Telk", restored.findObject("tent").orElseThrow().name());
        Tent restoredTent = (Tent) restored.findObject("tent").orElseThrow();
        assertEquals(1800, restoredTent.equipment().getFirst().requiredWatts());
        assertEquals(1, restored.powerConnections().size());
        assertArrayEquals(new byte[]{1, 2, 3, 4}, restored.packagedMapImage());
    }

    @Test
    void equivalentSnapshotsCompareEqualAndReusePackagedMapAsset() {
        EventPlan plan = new EventPlan("Testplaan");
        plan.setPackagedMapImage("map/map.png", new byte[]{4, 3, 2, 1});
        PlanSnapshotService service = new PlanSnapshotService();

        PlanSnapshot first = service.create(plan);
        PlanSnapshot second = service.create(plan);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertSame(first.mapImageAsset(), second.mapImageAsset());
    }
}
