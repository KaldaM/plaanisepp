package ee.matteus.plaanisepp.gui;

import ee.matteus.plaanisepp.core.model.EventPlan;

final class CableReportTextFormatter {
    private final CableInventorySummaryService cableInventorySummaryService;

    CableReportTextFormatter(CableInventorySummaryService cableInventorySummaryService) {
        this.cableInventorySummaryService = cableInventorySummaryService;
    }

    void append(StringBuilder builder, EventPlan plan, String lineSeparator) {
        CableInventorySummaryService.Summary summary = cableInventorySummaryService.summarize(plan);
        if (summary.isEmpty()) {
            return;
        }

        builder.append("Kaablid").append(lineSeparator);
        summary.rows().stream()
                .map(CableInventoryTextFormatter::connectionRow)
                .forEach(row -> builder.append(row).append(lineSeparator));
        if (summary.hasNotedLength()) {
            builder.append("Kokku: %.1f m märgitud, %.1f m kaardil".formatted(
                    summary.totalNotedLengthMeters(), summary.totalMapLengthMeters()
            )).append(lineSeparator);
        } else {
            builder.append("Kokku: %.1f m".formatted(summary.totalMapLengthMeters())).append(lineSeparator);
        }
        for (String row : CableInventoryTextFormatter.typeSummaryRows(summary.byType())) {
            builder.append(row).append(lineSeparator);
        }
        builder.append(lineSeparator);
    }
}
