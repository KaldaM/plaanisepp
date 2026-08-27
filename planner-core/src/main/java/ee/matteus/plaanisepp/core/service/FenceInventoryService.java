package ee.matteus.plaanisepp.core.service;

import ee.matteus.plaanisepp.core.model.EventPlan;
import ee.matteus.plaanisepp.core.model.FenceRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class FenceInventoryService {
    public Summary summarize(EventPlan plan) {
        List<FenceRow> rows = plan.objects().stream()
                .filter(FenceRow.class::isInstance)
                .map(FenceRow.class::cast)
                .toList();
        Map<String, MutableBreakdown> groups = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        Map<Double, MutableBreakdown> lengths = new TreeMap<>();
        int totalCount = 0;
        double totalLengthMeters = 0;
        for (FenceRow row : rows) {
            int count = row.segmentCount();
            double length = row.totalLengthMeters();
            totalCount += count;
            totalLengthMeters += length;
            groups.computeIfAbsent(groupName(row), ignored -> new MutableBreakdown()).add(count, length);
            lengths.computeIfAbsent(row.segmentLengthMeters(), ignored -> new MutableBreakdown()).add(count, length);
        }
        List<GroupBreakdown> byGroup = new ArrayList<>();
        groups.forEach((group, breakdown) -> byGroup.add(new GroupBreakdown(
                group, breakdown.count, breakdown.totalLengthMeters
        )));
        List<LengthBreakdown> byLength = new ArrayList<>();
        lengths.forEach((segmentLength, breakdown) -> byLength.add(new LengthBreakdown(
                segmentLength, breakdown.count, breakdown.totalLengthMeters
        )));
        return new Summary(totalCount, totalLengthMeters, byGroup, byLength);
    }

    private String groupName(FenceRow row) {
        return row.groupName() == null || row.groupName().isBlank() ? "Määramata" : row.groupName();
    }

    public record Summary(
            int totalCount,
            double totalLengthMeters,
            List<GroupBreakdown> byGroup,
            List<LengthBreakdown> byLength
    ) {
        public Summary {
            byGroup = List.copyOf(byGroup);
            byLength = List.copyOf(byLength);
        }
    }

    public record GroupBreakdown(String groupName, int count, double totalLengthMeters) {
    }

    public record LengthBreakdown(double segmentLengthMeters, int count, double totalLengthMeters) {
    }

    private static final class MutableBreakdown {
        private int count;
        private double totalLengthMeters;

        private void add(int addedCount, double addedLengthMeters) {
            count += addedCount;
            totalLengthMeters += addedLengthMeters;
        }
    }
}
