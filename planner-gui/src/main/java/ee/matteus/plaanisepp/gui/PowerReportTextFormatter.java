package ee.matteus.plaanisepp.gui;

import ee.matteus.plaanisepp.core.model.EventPlan;
import ee.matteus.plaanisepp.core.service.PowerHierarchyService;

import java.util.List;

final class PowerReportTextFormatter {
    private final PowerHierarchyService powerHierarchyService;

    PowerReportTextFormatter(PowerHierarchyService powerHierarchyService) {
        this.powerHierarchyService = powerHierarchyService;
    }

    void append(StringBuilder builder, EventPlan plan, ReportExportScope scope, String lineSeparator) {
        builder.append("Voolu kokkuvõte pesade kaupa").append(lineSeparator);
        builder.append(lineSeparator);
        PowerHierarchyService.Hierarchy hierarchy = powerHierarchyService.summarize(plan);
        for (PowerHierarchyService.SourceRow source : hierarchy.sources()) {
            builder.append(source.name())
                    .append(": ")
                    .append(source.usedWatts())
                    .append(" W kasutusel, ")
                    .append(remainingWattsText(source.remainingWatts()))
                    .append(lineSeparator);
            if (source.outlets().isEmpty()) {
                builder.append("  Väljundeid pole").append(lineSeparator);
            }
            for (PowerHierarchyService.OutletRow outlet : source.outlets()) {
                appendOutlet(builder, outlet, scope, lineSeparator);
            }
            builder.append(lineSeparator);
        }
        appendUnconnectedConsumers(builder, hierarchy.unconnectedConsumers(), lineSeparator);
    }

    private void appendOutlet(
            StringBuilder builder,
            PowerHierarchyService.OutletRow outlet,
            ReportExportScope scope,
            String lineSeparator
    ) {
        if (scope == ReportExportScope.COMPACT && outlet.usedWatts() == 0 && outlet.consumers().isEmpty()) {
            return;
        }
        builder.append("  ")
                .append(outletDisplayName(outlet))
                .append(": ")
                .append(outlet.capacityWatts())
                .append(" W mahutavus, ")
                .append(outlet.usedWatts())
                .append(" W kasutusel, ")
                .append(remainingWattsText(outlet.remainingWatts()))
                .append(lineSeparator);
        if (outlet.consumers().isEmpty()) {
            builder.append("    Tarbijaid pole").append(lineSeparator);
            return;
        }
        for (PowerHierarchyService.ConsumerRow consumer : outlet.consumers()) {
            builder.append("    - ")
                    .append(consumer.name())
                    .append(": ")
                    .append(consumer.usedWatts())
                    .append(" W");
            if (consumer.alternativeConnection()) {
                builder.append(" (seadme erand)");
            }
            if (!consumer.groupName().isBlank()) {
                builder.append(" (").append(consumer.groupName()).append(")");
            }
            builder.append(lineSeparator);
            for (PowerHierarchyService.EquipmentRow equipment : consumer.equipment()) {
                builder.append("      * ")
                        .append(equipment.name())
                        .append(": ")
                        .append(equipment.requiredWatts())
                        .append(" W")
                        .append(lineSeparator);
            }
        }
    }

    private void appendUnconnectedConsumers(
            StringBuilder builder,
            List<PowerHierarchyService.UnconnectedConsumerRow> consumers,
            String lineSeparator
    ) {
        if (consumers.isEmpty()) {
            return;
        }
        builder.append("Ühendamata tarbijad").append(lineSeparator);
        for (PowerHierarchyService.UnconnectedConsumerRow consumer : consumers) {
            builder.append("  - ")
                    .append(consumer.name())
                    .append(": ")
                    .append(consumer.requiredWatts())
                    .append(" W")
                    .append(lineSeparator);
        }
        builder.append(lineSeparator);
    }

    private String outletDisplayName(PowerHierarchyService.OutletRow outlet) {
        if (!outlet.name().isBlank()) {
            return "%s (%s %d)".formatted(outlet.name(), outlet.type().displayName(), outlet.typeIndex());
        }
        return "%s %d".formatted(outlet.type().displayName(), outlet.typeIndex());
    }

    private String remainingWattsText(int remainingWatts) {
        if (remainingWatts < 0) {
            return "ÜLEKOORMUS %d W".formatted(Math.abs(remainingWatts));
        }
        return "%d W alles".formatted(remainingWatts);
    }
}
