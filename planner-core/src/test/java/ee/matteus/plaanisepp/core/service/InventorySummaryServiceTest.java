package ee.matteus.plaanisepp.core.service;

import ee.matteus.plaanisepp.core.model.CustomObject;
import ee.matteus.plaanisepp.core.model.EventPlan;
import ee.matteus.plaanisepp.core.model.FenceRow;
import ee.matteus.plaanisepp.core.model.Position;
import ee.matteus.plaanisepp.core.model.Tent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InventorySummaryServiceTest {
    @Test
    void summarizesLogicalFenceNetworksAndPhysicalObjects() {
        EventPlan plan = new EventPlan("Inventar");
        FenceRow first = fence("fence-1", "Peaaed", new Position(0, 0), 2);
        FenceRow second = fence("fence-2", "Peaaed", first.endPosition(plan.pixelsPerMeter()), 3);
        plan.addObject(first);
        plan.addObject(second);
        plan.setFenceRowJoints(second, first.endJointId(), second.endJointId());
        plan.addObject(new Tent("tent-1", "Telk", new Position(0, 100)));
        plan.addObject(new Tent("tent-2", "Telk", new Position(100, 100)));
        plan.addObject(custom("stone-1", "Aiakivi"));
        plan.addObject(custom("stone-2", "Aiakivi 2"));
        plan.addObject(custom("table-1", "Laud"));
        plan.addObject(custom("table-2", "laud"));

        InventorySummaryService.Summary summary = new InventorySummaryService().summarize(plan);

        assertEquals(5, summary.fences().totalCount());
        assertEquals(1, summary.fences().byNetwork().size());
        assertEquals("Peaaed", summary.fences().byNetwork().getFirst().name());
        assertEquals(5, summary.fences().byNetwork().getFirst().count());
        assertTrue(summary.fences().byNetwork().getFirst().uniformSegmentLength());
        assertEquals(3.5, summary.fences().byNetwork().getFirst().segmentLengthMeters());
        assertEquals(2, summary.tentCount());
        assertEquals(2, summary.gardenStoneCount());
        assertEquals(1, summary.otherCustomItems().size());
        assertEquals("Laud", summary.otherCustomItems().getFirst().name());
        assertEquals(2, summary.otherCustomItems().getFirst().count());
    }

    @Test
    void emptyPlanHasEmptyInventory() {
        assertTrue(new InventorySummaryService().summarize(new EventPlan("Tühi")).isEmpty());
    }

    private FenceRow fence(String id, String name, Position position, int count) {
        FenceRow row = new FenceRow(id, name, position);
        row.setSegmentCount(count);
        return row;
    }

    private CustomObject custom(String id, String name) {
        return new CustomObject(id, name, new Position(0, 0));
    }
}
