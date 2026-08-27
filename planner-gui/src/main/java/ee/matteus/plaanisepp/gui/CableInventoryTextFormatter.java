package ee.matteus.plaanisepp.gui;

import ee.matteus.plaanisepp.core.model.ConnectorType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class CableInventoryTextFormatter {
    private CableInventoryTextFormatter() {
    }

    static String connectionRow(CableInventorySummaryService.Row row) {
        String lengthText = row.notedLengthMeters().isPresent()
                ? "%.1f m kaardil, %.1f m märgitud".formatted(
                        row.mapLengthMeters(), row.notedLengthMeters().getAsDouble()
                )
                : "%.1f m".formatted(row.mapLengthMeters());
        String connectionRole = row.alternativeConnection() ? ", seadme erand" : "";
        return "  - %s -> %s (%s%s): %s%s%s".formatted(
                row.consumerName(),
                row.sourceName(),
                row.connectorType().displayName(),
                connectionRole,
                lengthText,
                row.cableNotes().isBlank() ? "" : " [%s]".formatted(row.cableNotes()),
                row.noteNeedsReview() ? " (tükid kontrollida)" : ""
        );
    }

    static List<String> typeSummaryRows(
            Map<ConnectorType, CableInventorySummaryService.TypeSummary> summariesByType
    ) {
        List<String> rows = new ArrayList<>();
        if (!summariesByType.isEmpty()) {
            rows.add("Tüübi kaupa:");
        }
        for (ConnectorType connectorType : ConnectorType.values()) {
            CableInventorySummaryService.TypeSummary summary = summariesByType.get(connectorType);
            if (summary == null) {
                continue;
            }
            rows.add(typeSummaryRow(connectorType, summary));
            if (!summary.pieceCounts().isEmpty()) {
                rows.add("    tükid: %s".formatted(pieceCountText(summary.pieceCounts())));
            }
        }
        return rows;
    }

    private static String typeSummaryRow(
            ConnectorType connectorType,
            CableInventorySummaryService.TypeSummary summary
    ) {
        if (summary.hasNotedLength()) {
            return "  %s: %.1f m märgitud, %.1f m kaardil".formatted(
                    CableDisplayHelper.shortTypeName(connectorType),
                    summary.notedLengthMeters(),
                    summary.mapLengthMeters()
            );
        }
        return "  %s: %.1f m kaardil".formatted(
                CableDisplayHelper.shortTypeName(connectorType), summary.mapLengthMeters()
        );
    }

    private static String pieceCountText(Map<Double, Integer> pieceCounts) {
        List<String> rows = new ArrayList<>();
        for (Map.Entry<Double, Integer> entry : pieceCounts.entrySet()) {
            rows.add("%s m x %d".formatted(formatPieceLength(entry.getKey()), entry.getValue()));
        }
        return String.join(", ", rows);
    }

    private static String formatPieceLength(double lengthMeters) {
        if (Math.abs(lengthMeters - Math.rint(lengthMeters)) < 0.0001) {
            return Integer.toString((int) Math.rint(lengthMeters));
        }
        return "%.1f".formatted(lengthMeters);
    }
}
