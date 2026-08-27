package ee.matteus.plaanisepp.core.service;

import ee.matteus.plaanisepp.core.model.CustomObject;
import ee.matteus.plaanisepp.core.model.EventPlan;
import ee.matteus.plaanisepp.core.model.FenceRow;
import ee.matteus.plaanisepp.core.model.PlannerObject;
import ee.matteus.plaanisepp.core.model.Tent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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
        int additionalGardenStoneCount = (int) customObjects.stream()
                .filter(this::isGardenStone)
                .count();
        int automaticGardenStoneCount = automaticGardenStoneCount(plan);
        Map<String, Long> otherCounts = customObjects.stream()
                .filter(object -> !isGardenStone(object))
                .collect(Collectors.groupingBy(
                        PlannerObject::name,
                        () -> new TreeMap<>(String.CASE_INSENSITIVE_ORDER),
                        Collectors.counting()
                ));
        List<NamedItem> otherItems = new ArrayList<>();
        otherCounts.forEach((name, count) -> otherItems.add(new NamedItem(name, Math.toIntExact(count))));
        return new Summary(
                fenceInventoryService.summarize(plan),
                tentCount,
                automaticGardenStoneCount,
                additionalGardenStoneCount,
                otherItems
        );
    }

    private int automaticGardenStoneCount(EventPlan plan) {
        List<FenceRow> fenceRows = plan.objects().stream()
                .filter(FenceRow.class::isInstance)
                .map(FenceRow.class::cast)
                .toList();
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
        return internalSegmentBoundaries + jointIds.size();
    }

    private boolean isGardenStone(CustomObject object) {
        return object.name().toLowerCase(Locale.ROOT).contains("aiakivi");
    }

    public record Summary(
            FenceInventoryService.Summary fences,
            int tentCount,
            int automaticGardenStoneCount,
            int additionalGardenStoneCount,
            List<NamedItem> otherCustomItems
    ) {
        public Summary {
            otherCustomItems = List.copyOf(otherCustomItems);
        }

        public boolean isEmpty() {
            return fences.totalCount() == 0
                    && tentCount == 0
                    && gardenStoneCount() == 0
                    && otherCustomItems.isEmpty();
        }

        public int gardenStoneCount() {
            return automaticGardenStoneCount + additionalGardenStoneCount;
        }
    }

    public record NamedItem(String name, int count) {
    }
}
