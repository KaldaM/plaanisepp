package ee.matteus.plaanisepp.core.service;

import ee.matteus.plaanisepp.core.model.CustomObject;
import ee.matteus.plaanisepp.core.model.EventPlan;
import ee.matteus.plaanisepp.core.model.FenceRow;
import ee.matteus.plaanisepp.core.model.PlannerObject;
import ee.matteus.plaanisepp.core.model.Tent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
        List<CustomObject> customObjects = plan.objects().stream()
                .filter(CustomObject.class::isInstance)
                .map(CustomObject.class::cast)
                .toList();
        Map<String, Long> otherCounts = customObjects.stream()
                .collect(Collectors.groupingBy(
                        PlannerObject::name,
                        () -> new TreeMap<>(String.CASE_INSENSITIVE_ORDER),
                        Collectors.counting()
                ));
        List<NamedItem> otherItems = new ArrayList<>();
        otherCounts.forEach((name, count) -> otherItems.add(new NamedItem(name, Math.toIntExact(count))));
        FenceInventoryService.Summary fences = fenceInventoryService.summarize(plan);
        List<FenceStoneNetwork> fenceStoneNetworks = fences.byNetwork().stream()
                .map(network -> fenceStoneNetwork(plan, network))
                .toList();
        return new Summary(
                fences,
                tentCount,
                fenceStoneNetworks,
                plan.standaloneGardenStoneCount(),
                otherItems
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
            int tentCount,
            List<FenceStoneNetwork> fenceStoneNetworks,
            int standaloneGardenStoneCount,
            List<NamedItem> otherCustomItems
    ) {
        public Summary {
            fenceStoneNetworks = List.copyOf(fenceStoneNetworks);
            otherCustomItems = List.copyOf(otherCustomItems);
        }

        public boolean isEmpty() {
            return fences.totalCount() == 0
                    && tentCount == 0
                    && gardenStoneCount() == 0
                    && otherCustomItems.isEmpty();
        }

        public int gardenStoneCount() {
            return fenceStoneNetworks.stream().mapToInt(FenceStoneNetwork::totalCount).sum()
                    + standaloneGardenStoneCount;
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
}
