package ee.matteus.plaanisepp.gui;

import ee.matteus.plaanisepp.core.model.ConnectorType;
import ee.matteus.plaanisepp.core.model.AreaObject;
import ee.matteus.plaanisepp.core.model.EventPlan;
import ee.matteus.plaanisepp.core.model.FenceRow;
import ee.matteus.plaanisepp.core.model.LineObject;
import ee.matteus.plaanisepp.core.model.MarkerObject;
import ee.matteus.plaanisepp.core.model.PlannerObject;
import ee.matteus.plaanisepp.core.model.PowerConnection;
import ee.matteus.plaanisepp.core.model.PowerConsumer;
import ee.matteus.plaanisepp.core.model.PowerSource;
import ee.matteus.plaanisepp.core.model.TextObject;
import ee.matteus.plaanisepp.core.model.Tent;
import ee.matteus.plaanisepp.core.model.CustomObject;
import ee.matteus.plaanisepp.core.service.FenceInventoryService;
import ee.matteus.plaanisepp.core.service.PowerHierarchyService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

final class ReportTextExporter {
    private final FenceInventoryService fenceInventoryService = new FenceInventoryService();
    private final CableInventorySummaryService cableInventorySummaryService = new CableInventorySummaryService();
    private final PowerHierarchyService powerHierarchyService = new PowerHierarchyService();

    ReportTextExporter() {
    }

    String export(EventPlan plan, ReportExportScope reportScope, boolean includePower, boolean includeCables, boolean includeGroups) {
        String lineSeparator = System.lineSeparator();
        StringBuilder builder = new StringBuilder();
        builder.append(plan.name()).append(lineSeparator);
        builder.append("=".repeat(plan.name().length())).append(lineSeparator);
        builder.append(lineSeparator);
        appendPlanInfoReport(builder, plan, lineSeparator);

        if (includePower) {
            appendPowerReport(builder, plan, reportScope, lineSeparator);
        }

        if (includeCables) {
            appendCableReport(builder, plan, lineSeparator);
        }

        if (includeGroups) {
            appendGroupReport(builder, plan, lineSeparator);
        }
        appendFenceReport(builder, plan, lineSeparator);
        appendTextObjectReport(builder, plan, lineSeparator);
        return builder.toString();
    }

    private void appendFenceReport(StringBuilder builder, EventPlan plan, String lineSeparator) {
        List<FenceRow> fenceRows = plan.objects().stream()
                .filter(FenceRow.class::isInstance)
                .map(FenceRow.class::cast)
                .toList();
        if (fenceRows.isEmpty()) {
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

    private void appendPlanInfoReport(StringBuilder builder, EventPlan plan, String lineSeparator) {
        builder.append("Plaani andmed").append(lineSeparator);
        builder.append("  Plaan: ").append(plan.name()).append(lineSeparator);
        builder.append("  Mõõtkava: ").append(formatMeters(plan.pixelsPerMeter())).append(" px/m").append(lineSeparator);
        builder.append("  Kaart: ").append(plan.mapImagePath().isBlank() ? "määramata" : plan.mapImagePath()).append(lineSeparator);
        builder.append("  Objekte: ").append(plan.objects().size()).append(lineSeparator);
        builder.append("  Vooluühendusi: ").append(plan.powerConnections().size()).append(lineSeparator);
        builder.append(lineSeparator);
    }

    private void appendPowerReport(StringBuilder builder, EventPlan plan, ReportExportScope reportScope, String lineSeparator) {
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
                appendOutletReport(builder, outlet, reportScope, lineSeparator);
            }
            builder.append(lineSeparator);
        }
        appendUnconnectedConsumersReport(builder, hierarchy.unconnectedConsumers(), lineSeparator);
    }

    private void appendOutletReport(
            StringBuilder builder,
            PowerHierarchyService.OutletRow outlet,
            ReportExportScope reportScope,
            String lineSeparator
    ) {
        if (reportScope == ReportExportScope.COMPACT && outlet.usedWatts() == 0 && outlet.consumers().isEmpty()) {
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

    private void appendUnconnectedConsumersReport(
            StringBuilder builder,
            List<PowerHierarchyService.UnconnectedConsumerRow> unconnectedConsumers,
            String lineSeparator
    ) {
        if (unconnectedConsumers.isEmpty()) {
            return;
        }

        builder.append("Ühendamata tarbijad").append(lineSeparator);
        for (PowerHierarchyService.UnconnectedConsumerRow consumer : unconnectedConsumers) {
            builder.append("  - ")
                    .append(consumer.name())
                    .append(": ")
                    .append(consumer.requiredWatts())
                    .append(" W")
                    .append(lineSeparator);
        }
        builder.append(lineSeparator);
    }

    private void appendCableReport(StringBuilder builder, EventPlan plan, String lineSeparator) {
        if (plan.powerConnections().isEmpty()) {
            return;
        }

        List<CableInventorySummaryService.Input> inputs = new ArrayList<>();
        for (PowerConnection connection : plan.powerConnections()) {
            PlannerObject consumer = plan.findObject(connection.consumerId()).orElse(null);
            if (!(consumer instanceof PowerConsumer)) {
                continue;
            }
            PowerSource source = plan.findObject(connection.sourceId())
                    .filter(PowerSource.class::isInstance)
                    .map(PowerSource.class::cast)
                    .orElse(null);
            if (source == null) {
                continue;
            }

            double lengthMeters = CableDisplayHelper.lengthMeters(
                    CablePathHelper.cablePath(consumer, source, connection, plan.pixelsPerMeter()),
                    plan.pixelsPerMeter()
            );
            inputs.add(new CableInventorySummaryService.Input(
                    consumer.name(), source.name(), connection, lengthMeters
            ));
        }

        CableInventorySummaryService.Summary summary = cableInventorySummaryService.summarize(inputs);
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


    private void appendGroupReport(StringBuilder builder, EventPlan plan, String lineSeparator) {
        if (plan.objects().isEmpty()) {
            return;
        }

        builder.append("Grupid").append(lineSeparator);
        for (Map.Entry<String, List<PlannerObject>> entry : objectsByGroup(plan).entrySet()) {
            builder.append(entry.getKey()).append(lineSeparator);
            for (PlannerObject object : entry.getValue()) {
                builder.append("  - ")
                        .append(object.name())
                        .append(" (")
                        .append(objectTypeName(object))
                        .append(")")
                        .append(lineSeparator);
            }
        }
        builder.append(lineSeparator);
    }

    private Map<String, List<PlannerObject>> objectsByGroup(EventPlan plan) {
        Map<String, List<PlannerObject>> objectsByGroup = new TreeMap<>();
        for (PlannerObject object : plan.objects()) {
            String groupName = object.groupName().isBlank() ? "Määramata" : object.groupName();
            objectsByGroup.computeIfAbsent(groupName, ignored -> new ArrayList<>()).add(object);
        }
        return objectsByGroup;
    }

    private void appendTextObjectReport(StringBuilder builder, EventPlan plan, String lineSeparator) {
        List<TextObject> textObjects = plan.objects().stream()
                .filter(TextObject.class::isInstance)
                .map(TextObject.class::cast)
                .filter(textObject -> !textObject.notes().isBlank())
                .toList();
        if (textObjects.isEmpty()) {
            return;
        }

        builder.append("Tekstimärkmed").append(lineSeparator);
        for (TextObject textObject : textObjects) {
            builder.append(textObject.name());
            if (!textObject.groupName().isBlank()) {
                builder.append(" (").append(textObject.groupName()).append(")");
            }
            builder.append(lineSeparator);
            for (String line : textObject.notes().split("\\R")) {
                builder.append("  ").append(line).append(lineSeparator);
            }
            builder.append(lineSeparator);
        }
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

    private String objectTypeName(PlannerObject object) {
        if (object instanceof Tent) {
            return "Telk";
        }
        if (object instanceof PowerSource) {
            return "Elektrikapp";
        }
        if (object instanceof TextObject) {
            return "Tekst";
        }
        if (object instanceof MarkerObject) {
            return "Marker";
        }
        if (object instanceof AreaObject) {
            return "Ala";
        }
        if (object instanceof FenceRow) {
            return "Aiarida";
        }
        if (object instanceof LineObject) {
            return "Joon";
        }
        if (object instanceof CustomObject) {
            return "Objekt";
        }
        return "Objekt";
    }

    private String formatMeters(double meters) {
        if (meters == Math.rint(meters)) {
            return "%.0f".formatted(meters);
        }
        return "%.2f".formatted(meters);
    }

}
