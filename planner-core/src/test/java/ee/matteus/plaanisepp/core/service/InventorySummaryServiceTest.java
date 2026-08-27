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
        assertEquals(6, summary.automaticGardenStoneCount());
        assertEquals(2, summary.additionalGardenStoneCount());
        assertEquals(8, summary.gardenStoneCount());
        assertEquals(1, summary.otherCustomItems().size());
        assertEquals("Laud", summary.otherCustomItems().getFirst().name());
        assertEquals(2, summary.otherCustomItems().getFirst().count());
    }

    @Test
    void emptyPlanHasEmptyInventory() {
        assertTrue(new InventorySummaryService().summarize(new EventPlan("Tühi")).isEmpty());
    }

    @Test
    void countsOneStonePerJointInClosedFenceNetwork() {
        EventPlan plan = new EventPlan("Suletud ring");
        FenceRow first = fence("first", "Ring", new Position(0, 0), 1);
        FenceRow second = fence("second", "Ring", new Position(20, 0), 1);
        FenceRow third = fence("third", "Ring", new Position(10, 20), 1);
        plan.addObject(first);
        plan.addObject(second);
        plan.addObject(third);
        plan.setFenceRowJoints(second, first.endJointId(), second.endJointId());
        plan.setFenceRowJoints(third, second.endJointId(), first.startJointId());

        InventorySummaryService.Summary summary = new InventorySummaryService().summarize(plan);

        assertEquals(3, summary.fences().totalCount());
        assertEquals(3, summary.automaticGardenStoneCount());
    }

    @Test
    void countsSharedBranchJointOnlyOnce() {
        EventPlan plan = new EventPlan("Hargnev aed");
        FenceRow first = fence("first", "Haru", new Position(0, 0), 1);
        FenceRow second = fence("second", "Haru", new Position(0, 0), 1);
        FenceRow third = fence("third", "Haru", new Position(0, 0), 1);
        plan.addObject(first);
        plan.addObject(second);
        plan.addObject(third);
        plan.setFenceRowJoints(second, first.startJointId(), second.endJointId());
        plan.setFenceRowJoints(third, first.startJointId(), third.endJointId());

        InventorySummaryService.Summary summary = new InventorySummaryService().summarize(plan);

        assertEquals(3, summary.fences().totalCount());
        assertEquals(4, summary.automaticGardenStoneCount());
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
