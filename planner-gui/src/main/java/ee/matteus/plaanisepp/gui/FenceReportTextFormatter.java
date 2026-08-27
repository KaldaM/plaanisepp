package ee.matteus.plaanisepp.gui;

import ee.matteus.plaanisepp.core.model.EventPlan;
import ee.matteus.plaanisepp.core.service.FenceInventoryService;
import ee.matteus.plaanisepp.core.service.InventorySummaryService;

final class FenceReportTextFormatter {
    private final InventorySummaryService inventorySummaryService;

    FenceReportTextFormatter(InventorySummaryService inventorySummaryService) {
        this.inventorySummaryService = inventorySummaryService;
    }

    void append(StringBuilder builder, EventPlan plan, String lineSeparator) {
        InventorySummaryService.Summary inventory = inventorySummaryService.summarize(plan);
        FenceInventoryService.Summary summary = inventory.fences();
        if (summary.totalCount() == 0 && inventory.standaloneGardenStoneCount() == 0) {
            return;
        }

        builder.append("Aiad ja aiakivid").append(lineSeparator);
        for (FenceInventoryService.NetworkBreakdown network : summary.byNetwork()) {
            InventorySummaryService.FenceStoneNetwork stones = inventory.fenceStoneNetworks().stream()
                    .filter(candidate -> candidate.representativeId().equals(network.representativeId()))
                    .findFirst()
                    .orElseThrow();
            builder.append("  - ")
                    .append(network.name())
                    .append(": ")
                    .append(network.count());
            if (network.uniformSegmentLength()) {
                builder.append(" × ")
                        .append(formatMeters(network.segmentLengthMeters()))
                        .append(" m = ");
            } else {
                builder.append(" aeda = ");
            }
            builder.append(formatMeters(network.totalLengthMeters()))
                    .append(" m")
                    .append(lineSeparator);
            builder.append("    Aiakivid: ")
                    .append(stones.automaticCount())
                    .append(" automaatne, ")
                    .append(signedCount(stones.adjustment()))
                    .append(" parandus, ")
                    .append(stones.totalCount())
                    .append(" kokku")
                    .append(lineSeparator);
        }
        if (summary.totalCount() > 0) {
            builder.append("Kokku: ").append(summary.totalCount()).append(" aeda, ")
                    .append(formatMeters(summary.totalLengthMeters())).append(" m").append(lineSeparator);
            builder.append("Pikkuse järgi:").append(lineSeparator);
            for (FenceInventoryService.LengthBreakdown length : summary.byLength()) {
                builder.append("  - ").append(formatMeters(length.segmentLengthMeters())).append(" m: ")
                        .append(length.count()).append(" aeda, ")
                        .append(formatMeters(length.totalLengthMeters())).append(" m").append(lineSeparator);
            }
            builder.append("Gruppide järgi:").append(lineSeparator);
            for (FenceInventoryService.GroupBreakdown group : summary.byGroup()) {
                builder.append("  - ").append(group.groupName()).append(": ")
                        .append(group.count()).append(" aeda, ")
                        .append(formatMeters(group.totalLengthMeters())).append(" m").append(lineSeparator);
            }
        }
        builder.append("Aiakive kokku: ").append(inventory.gardenStoneCount()).append(" tk")
                .append(" (ilma aiata ").append(inventory.standaloneGardenStoneCount()).append(" tk)")
                .append(lineSeparator);
        builder.append(lineSeparator);
    }

    private String signedCount(int count) {
        return count > 0 ? "+" + count : Integer.toString(count);
    }

    private String formatMeters(double meters) {
        if (meters == Math.rint(meters)) {
            return "%.0f".formatted(meters);
        }
        return "%.2f".formatted(meters);
    }
}
