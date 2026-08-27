package ee.matteus.plaanisepp.core.service;

import ee.matteus.plaanisepp.core.model.EventPlan;
import ee.matteus.plaanisepp.core.model.FenceRow;
import ee.matteus.plaanisepp.core.model.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FenceInventoryServiceTest {
    @Test
    void summarizesFencesByGroupAndPhysicalLength() {
        EventPlan plan = new EventPlan("Aiad");
        FenceRow entrance = row("entrance", "Sissepääs", 4, 3.5);
        FenceRow continuation = row("continuation", "Sissepääs", 2, 3.5);
        FenceRow special = row("special", "Lava", 3, 2.0);
        plan.addObject(entrance);
        plan.addObject(continuation);
        plan.addObject(special);

        FenceInventoryService.Summary summary = new FenceInventoryService().summarize(plan);

        assertEquals(9, summary.totalCount());
        assertEquals(27, summary.totalLengthMeters(), 0.000001);
        assertEquals(2, summary.byGroup().size());
        assertEquals("Lava", summary.byGroup().get(0).groupName());
        assertEquals(3, summary.byGroup().get(0).count());
        assertEquals("Sissepääs", summary.byGroup().get(1).groupName());
        assertEquals(6, summary.byGroup().get(1).count());
        assertEquals(2.0, summary.byLength().get(0).segmentLengthMeters());
        assertEquals(3, summary.byLength().get(0).count());
        assertEquals(3.5, summary.byLength().get(1).segmentLengthMeters());
        assertEquals(6, summary.byLength().get(1).count());
    }

    private FenceRow row(String id, String group, int count, double segmentLengthMeters) {
        FenceRow row = new FenceRow(id, id, new Position(0, 0));
        row.setGroupName(group);
        row.setSegmentCount(count);
        row.setSegmentLengthMeters(segmentLengthMeters);
        return row;
    }
}
