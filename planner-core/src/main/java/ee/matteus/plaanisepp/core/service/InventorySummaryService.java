package ee.matteus.plaanisepp.core.service;

import ee.matteus.plaanisepp.core.model.EventPlan;
import ee.matteus.plaanisepp.core.model.FenceRow;
import ee.matteus.plaanisepp.core.model.InventoryContainer;
import ee.matteus.plaanisepp.core.model.InventoryItemNames;
import ee.matteus.plaanisepp.core.model.PlannerObject;
import ee.matteus.plaanisepp.core.model.Tent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public final class InventorySummaryService {
    private final FenceInventoryService fenceInventoryService;

    public InventorySummaryService() {
        this(new FenceInventoryService());
    }

    InventorySummaryService(FenceInventoryService fenceInventoryService) {
        this.fenceInventoryService = fenceInventoryService;
    }

    public Summary summarize(EventPlan plan) {
        int tentCount = (int) plan.objects().stream().filter(Tent.class::isInstance).count();
        FenceInventoryService.Summary fences = fenceInventoryService.summarize(plan);
        List<FenceStoneNetwork> fenceStoneNetworks = fences.byNetwork().stream()
                .map(network -> fenceStoneNetwork(plan, network))
                .toList();
        int standaloneFenceCount = plan.standaloneInventoryItems().stream()
                .filter(item -> InventoryItemNames.isFence(item.name()))
                .mapToInt(item -> item.quantity())
                .sum();
        int standaloneGardenStoneCount = plan.standaloneGardenStoneCount()
                + plan.standaloneInventoryItems().stream()
                .filter(item -> InventoryItemNames.isGardenStone(item.name()))
                .mapToInt(item -> item.quantity())
                .sum();
        Map<String, Integer> inventoryCounts = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        Map<String, List<ObjectInventoryContribution>> inventoryContributions =
                new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        plan.objects().stream()
                .filter(InventoryContainer.class::isInstance)
                .forEach(object -> {
                    List<ee.matteus.plaanisepp.core.model.InventoryItem> items =
                            ((InventoryContainer) object).inventoryItems();
                    for (int itemIndex = 0; itemIndex < items.size(); itemIndex++) {
                        var item = items.get(itemIndex);
                        if (item.name().isBlank() || item.quantity() <= 0) {
                            continue;
                        }
                            inventoryCounts.merge(item.name(), item.quantity(), Integer::sum);
                            inventoryContributions.computeIfAbsent(item.name(), ignored -> new ArrayList<>())
                                    .add(new ObjectInventoryContribution(
                                            object.id(), object.name(), objectTypeName(object), itemIndex,
                                            item.quantity(), item.notes()
                                    ));
                    }
                });
        List<NamedItem> objectInventoryItems = inventoryCounts.entrySet().stream()
                .map(entry -> new NamedItem(entry.getKey(), entry.getValue()))
                .toList();
        List<ObjectInventoryGroup> objectInventoryGroups = inventoryCounts.entrySet().stream()
                .map(entry -> new ObjectInventoryGroup(
                        entry.getKey(), entry.getValue(), inventoryContributions.get(entry.getKey())
                ))
                .toList();
        return new Summary(
                fences,
                standaloneFenceCount,
                tentCount,
                fenceStoneNetworks,
                standaloneGardenStoneCount,
                List.of(),
                objectInventoryItems,
                objectInventoryGroups
        );
    }

    private FenceStoneNetwork fenceStoneNetwork(
            EventPlan plan,
            FenceInventoryService.NetworkBreakdown network
    ) {
        List<FenceRow> fenceRows = plan.fenceNetworkRows(network.representativeId());
        Set<String> jointIds = new HashSet<>();
        int internalSegmentBoundaries = 0;
        for (FenceRow row : fenceRows) {
            internalSegmentBoundaries += Math.max(0, row.segmentCount() - 1);
            if (!row.startJointId().isBlank()) {
                jointIds.add(row.startJointId());
            }
            if (!row.endJointId().isBlank()) {
                jointIds.add(row.endJointId());
            }
        }
        int automaticCount = internalSegmentBoundaries + jointIds.size();
        int requestedAdjustment = plan.fenceNetworkGardenStoneAdjustment(network.representativeId());
        int totalCount = Math.max(0, automaticCount + requestedAdjustment);
        return new FenceStoneNetwork(
                network.representativeId(),
                network.name(),
                automaticCount,
                totalCount - automaticCount,
                totalCount
        );
    }

    public record Summary(
            FenceInventoryService.Summary fences,
            int standaloneFenceCount,
            int tentCount,
            List<FenceStoneNetwork> fenceStoneNetworks,
            int standaloneGardenStoneCount,
            List<NamedItem> otherCustomItems,
            List<NamedItem> objectInventoryItems,
            List<ObjectInventoryGroup> objectInventoryGroups
    ) {
        public Summary {
            fenceStoneNetworks = List.copyOf(fenceStoneNetworks);
            otherCustomItems = List.copyOf(otherCustomItems);
            objectInventoryItems = List.copyOf(objectInventoryItems);
            objectInventoryGroups = List.copyOf(objectInventoryGroups);
        }

        public boolean isEmpty() {
            return fences.totalCount() == 0
                    && standaloneFenceCount == 0
                    && tentCount == 0
                    && gardenStoneCount() == 0
                    && otherCustomItems.isEmpty()
                    && objectInventoryItems.isEmpty();
        }

        public int gardenStoneCount() {
            return fenceStoneNetworks.stream().mapToInt(FenceStoneNetwork::totalCount).sum()
                    + standaloneGardenStoneCount;
        }

        public int totalFenceCount() {
            return fences.totalCount() + standaloneFenceCount;
        }

        public int automaticGardenStoneCount() {
            return fenceStoneNetworks.stream().mapToInt(FenceStoneNetwork::automaticCount).sum();
        }

        public int gardenStoneAdjustment() {
            return fenceStoneNetworks.stream().mapToInt(FenceStoneNetwork::adjustment).sum();
        }
    }

    public record FenceStoneNetwork(
            String representativeId,
            String name,
            int automaticCount,
            int adjustment,
            int totalCount
    ) {
    }

    public record NamedItem(String name, int count) {
    }

    public record ObjectInventoryGroup(
            String name,
            int totalCount,
            List<ObjectInventoryContribution> contributions
    ) {
        public ObjectInventoryGroup {
            contributions = List.copyOf(contributions);
        }
    }

    public record ObjectInventoryContribution(
            String objectId,
            String objectName,
            String objectType,
            int itemIndex,
            int quantity,
            String notes
    ) {
    }

    private String objectTypeName(PlannerObject object) {
        if (object instanceof Tent) {
            return "Telk";
        }
        if (object instanceof ee.matteus.plaanisepp.core.model.AreaObject) {
            return "Ala";
        }
        return "Objekt";
    }
}
