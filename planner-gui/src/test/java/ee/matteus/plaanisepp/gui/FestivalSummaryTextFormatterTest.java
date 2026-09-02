package ee.matteus.plaanisepp.gui;

import ee.matteus.plaanisepp.core.model.ConnectorType;
import ee.matteus.plaanisepp.core.model.EventPlan;
import ee.matteus.plaanisepp.core.model.Position;
import ee.matteus.plaanisepp.core.model.PowerOutlet;
import ee.matteus.plaanisepp.core.model.PowerSource;
import ee.matteus.plaanisepp.core.model.Tent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FestivalSummaryTextFormatterTest {
    @Test
    void listsOnlyTartuCabinetsThatSupplyPowerAndCombinesTheirPlans() {
        EventPlan firstPlan = connectedPlan("Avapidu", "PVK 7", 42);
        EventPlan secondPlan = connectedPlan("Pannkoogihommik", "PVK 7", 42);
        PowerSource unused = tartuCabinet("unused", "PVK 9", 99);
        firstPlan.addObject(unused);

        String result = new FestivalSummaryTextFormatter().format(
                "Tartu Tudengipäevad",
                List.of(firstPlan, secondPlan),
                List.of()
        );

        assertTrue(result.contains("Plaane kokku: 2"));
        assertTrue(result.contains("PVK 7 — Avapidu, Pannkoogihommik"));
        assertFalse(result.contains("PVK 9"));
    }

    private EventPlan connectedPlan(String planName, String cabinetName, long sourceId) {
        EventPlan plan = new EventPlan(planName);
        PowerSource source = tartuCabinet("source-" + planName, cabinetName, sourceId);
        source.addOutlet(new PowerOutlet("outlet-" + planName, ConnectorType.SCHUKO_230V, 3500));
        Tent tent = new Tent("tent-" + planName, "Telk", new Position(10, 10));
        plan.addObject(source);
        plan.addObject(tent);
        plan.connectToPower(
                source.id(),
                tent.id(),
                ConnectorType.SCHUKO_230V,
                source.outlets().getFirst().id(),
                "",
                ""
        ).orElseThrow();
        return plan;
    }

    private PowerSource tartuCabinet(String id, String name, long sourceId) {
        PowerSource source = new PowerSource(id, name, new Position(0, 0));
        source.setNotes("Allikas: Tartu linn, püsivoolukilbi ID " + sourceId);
        return source;
    }
}
