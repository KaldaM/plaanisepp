package ee.matteus.plaanisepp.core.service;

import ee.matteus.plaanisepp.core.model.CustomObject;
import ee.matteus.plaanisepp.core.model.EventPlan;
import ee.matteus.plaanisepp.core.model.FenceRow;
import ee.matteus.plaanisepp.core.model.InventoryItem;
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
        Tent inventoryTent = new Tent("tent-1", "Telk", new Position(0, 100));
        inventoryTent.addInventoryItem(new InventoryItem("Telgiraskus", 4, "Kaks igasse nurka"));
        inventoryTent.addInventoryItem(new InventoryItem("Laud", 2, ""));
        plan.addObject(inventoryTent);
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
        assertEquals(0, summary.standaloneGardenStoneCount());
        assertEquals(6, summary.gardenStoneCount());
        assertTrue(summary.otherCustomItems().isEmpty());
        assertEquals(4, summary.objectInventoryItems().stream()
                .filter(item -> item.name().equals("Telgiraskus"))
                .findFirst().orElseThrow().count());
        assertEquals(2, summary.objectInventoryItems().stream()
                .filter(item -> item.name().equals("Laud"))
                .findFirst().orElseThrow().count());
        InventorySummaryService.ObjectInventoryGroup weights = summary.objectInventoryGroups().stream()
                .filter(item -> item.name().equals("Telgiraskus"))
                .findFirst().orElseThrow();
        assertEquals(4, weights.totalCount());
        assertEquals(1, weights.contributions().size());
        assertEquals("Telk", weights.contributions().getFirst().objectName());
        assertEquals("Telk", weights.contributions().getFirst().objectType());
        assertEquals(0, weights.contributions().getFirst().itemIndex());
        assertEquals("Kaks igasse nurka", weights.contributions().getFirst().notes());
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

    @Test
    void appliesPerNetworkAdjustmentAndStandaloneStoneCount() {
        EventPlan plan = new EventPlan("Kohandatud aiakivid");
        FenceRow row = fence("fence", "Peaaed", new Position(0, 0), 3);
        plan.addObject(row);
        plan.setFenceNetworkGardenStoneAdjustment(row.id(), -1);
        plan.setStandaloneGardenStoneCount(4);

        InventorySummaryService.Summary summary = new InventorySummaryService().summarize(plan);
        InventorySummaryService.FenceStoneNetwork network = summary.fenceStoneNetworks().getFirst();

        assertEquals(4, network.automaticCount());
        assertEquals(-1, network.adjustment());
        assertEquals(3, network.totalCount());
        assertEquals(4, summary.standaloneGardenStoneCount());
        assertEquals(7, summary.gardenStoneCount());
    }

    @Test
    void networkAdjustmentCannotReduceTotalBelowZero() {
        EventPlan plan = new EventPlan("Liiga suur miinus");
        FenceRow row = fence("fence", "Peaaed", new Position(0, 0), 1);
        plan.addObject(row);
        plan.setFenceNetworkGardenStoneAdjustment(row.id(), -99);

        InventorySummaryService.FenceStoneNetwork network = new InventorySummaryService()
                .summarize(plan)
                .fenceStoneNetworks()
                .getFirst();

        assertEquals(0, network.totalCount());
        assertEquals(-2, network.adjustment());
    }

    @Test
    void combinesAdjustmentsWhenFenceNetworksAreJoined() {
        EventPlan plan = new EventPlan("Ühendatud parandused");
        FenceRow first = fence("first", "Esimene", new Position(0, 0), 1);
        FenceRow second = fence("second", "Teine", new Position(20, 0), 1);
        plan.addObject(first);
        plan.addObject(second);
        plan.setFenceNetworkGardenStoneAdjustment(first.id(), -1);
        plan.setFenceNetworkGardenStoneAdjustment(second.id(), 2);

        plan.setFenceRowJoints(second, first.endJointId(), second.endJointId());
        InventorySummaryService.Summary summary = new InventorySummaryService().summarize(plan);

        assertEquals(1, summary.fenceStoneNetworks().size());
        assertEquals(1, summary.fenceStoneNetworks().getFirst().adjustment());
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
