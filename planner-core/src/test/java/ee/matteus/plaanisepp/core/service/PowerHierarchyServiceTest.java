package ee.matteus.plaanisepp.core.service;

import ee.matteus.plaanisepp.core.model.ConnectorType;
import ee.matteus.plaanisepp.core.model.Equipment;
import ee.matteus.plaanisepp.core.model.EventPlan;
import ee.matteus.plaanisepp.core.model.Position;
import ee.matteus.plaanisepp.core.model.PowerConnection;
import ee.matteus.plaanisepp.core.model.PowerOutlet;
import ee.matteus.plaanisepp.core.model.PowerSource;
import ee.matteus.plaanisepp.core.model.Tent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PowerHierarchyServiceTest {
    @Test
    void summarizesSourcesOutletsConnectionsEquipmentAndUnconnectedConsumers() {
        EventPlan plan = new EventPlan("Vool");
        PowerSource source = new PowerSource("source", "Põhikapp", new Position(0, 0));
        source.addOutlet(new PowerOutlet("outlet-1", "Lava", ConnectorType.SCHUKO_230V, 3500));
        source.addOutlet(new PowerOutlet("outlet-2", ConnectorType.SCHUKO_230V, 3500));
        Tent connected = new Tent("tent-1", "Kohvik", new Position(20, 0));
        connected.setGroupName("Toitlustus");
        connected.addEquipment(new Equipment("coffee", "Kohvimasin", 1800));
        Tent unconnected = new Tent("tent-2", "Infotelk", new Position(40, 0));
        unconnected.addEquipment(new Equipment("light", "Valgusti", 200));
        plan.addObject(source);
        plan.addObject(connected);
        plan.addObject(unconnected);
        plan.connectToPower(source.id(), connected.id(), ConnectorType.SCHUKO_230V, "outlet-1");

        PowerHierarchyService.Hierarchy hierarchy = new PowerHierarchyService().summarize(plan);

        assertEquals(1, hierarchy.sources().size());
        PowerHierarchyService.SourceRow sourceRow = hierarchy.sources().getFirst();
        assertEquals(1800, sourceRow.usedWatts());
        assertEquals(5200, sourceRow.remainingWatts());
        assertEquals(2, sourceRow.outlets().size());
        assertEquals(1, sourceRow.outlets().getFirst().typeIndex());
        assertEquals(2, sourceRow.outlets().get(1).typeIndex());
        PowerHierarchyService.ConsumerRow consumer = sourceRow.outlets().getFirst().consumers().getFirst();
        assertEquals("Kohvik", consumer.name());
        assertEquals("Toitlustus", consumer.groupName());
        assertEquals(1, consumer.equipment().size());
        assertEquals("Kohvimasin", consumer.equipment().getFirst().name());
        assertEquals(1, hierarchy.unconnectedConsumers().size());
        assertEquals("Infotelk", hierarchy.unconnectedConsumers().getFirst().name());
        assertTrue(sourceRow.directConsumers().isEmpty());
    }
}
