package ee.matteus.plaanisepp.gui;

import ee.matteus.plaanisepp.core.model.EventPlan;

final class PlanOverviewTextFormatter {
    void append(
            StringBuilder builder,
            EventPlan plan,
            String lineSeparator,
            boolean includeTechnicalDetails
    ) {
        builder.append(plan.name()).append(lineSeparator);
        builder.append("=".repeat(plan.name().length())).append(lineSeparator);
        builder.append(lineSeparator);
        builder.append("Plaani andmed").append(lineSeparator);
        builder.append("  Plaan: ").append(plan.name()).append(lineSeparator);
        if (!plan.festivalName().isBlank()) {
            builder.append("  Festival või sündmus: ").append(plan.festivalName()).append(lineSeparator);
        }
        builder.append("  Mõõtkava: ").append(formatNumber(plan.pixelsPerMeter())).append(" px/m").append(lineSeparator);
        builder.append("  Kaart: ").append(plan.mapImagePath().isBlank() ? "määramata" : plan.mapImagePath()).append(lineSeparator);
        builder.append("  Objekte: ").append(plan.objects().size()).append(lineSeparator);
        if (includeTechnicalDetails) {
            builder.append("  Vooluühendusi: ").append(plan.powerConnections().size()).append(lineSeparator);
        }
        builder.append(lineSeparator);
    }

    private String formatNumber(double value) {
        if (value == Math.rint(value)) {
            return "%.0f".formatted(value);
        }
        return "%.2f".formatted(value);
    }
}
