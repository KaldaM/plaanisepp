package ee.matteus.plaanisepp.gui;

import ee.matteus.plaanisepp.core.model.EventPlan;
import ee.matteus.plaanisepp.core.model.FenceRow;
import ee.matteus.plaanisepp.core.service.FenceInventoryService;

final class FenceReportTextFormatter {
    private final FenceInventoryService fenceInventoryService;

    FenceReportTextFormatter(FenceInventoryService fenceInventoryService) {
        this.fenceInventoryService = fenceInventoryService;
    }

    void append(StringBuilder builder, EventPlan plan, String lineSeparator) {
        boolean hasFences = plan.objects().stream().anyMatch(FenceRow.class::isInstance);
        if (!hasFences) {
            return;
        }

        builder.append("Aiad").append(lineSeparator);
        FenceInventoryService.Summary summary = fenceInventoryService.summarize(plan);
        for (FenceInventoryService.NetworkBreakdown network : summary.byNetwork()) {
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
        }
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
        builder.append(lineSeparator);
    }

    private String formatMeters(double meters) {
        if (meters == Math.rint(meters)) {
            return "%.0f".formatted(meters);
        }
        return "%.2f".formatted(meters);
    }
}
