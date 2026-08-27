package ee.matteus.plaanisepp.gui;

import ee.matteus.plaanisepp.core.model.ConnectorType;
import ee.matteus.plaanisepp.core.model.AreaObject;
import ee.matteus.plaanisepp.core.model.Equipment;
import ee.matteus.plaanisepp.core.model.EquipmentContainer;
import ee.matteus.plaanisepp.core.model.EventPlan;
import ee.matteus.plaanisepp.core.model.FenceRow;
import ee.matteus.plaanisepp.core.model.LineObject;
import ee.matteus.plaanisepp.core.model.MarkerObject;
import ee.matteus.plaanisepp.core.model.PlannerObject;
import ee.matteus.plaanisepp.core.model.PowerConnection;
import ee.matteus.plaanisepp.core.model.PowerConsumer;
import ee.matteus.plaanisepp.core.model.PowerOutlet;
import ee.matteus.plaanisepp.core.model.PowerSource;
import ee.matteus.plaanisepp.core.model.TextObject;
import ee.matteus.plaanisepp.core.model.Tent;
import ee.matteus.plaanisepp.core.model.CustomObject;
import ee.matteus.plaanisepp.core.service.PowerSummary;
import ee.matteus.plaanisepp.core.service.PowerSummaryService;
import ee.matteus.plaanisepp.core.service.FenceInventoryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

final class ReportTextExporter {
    private final PowerSummaryService powerSummaryService;
    private final FenceInventoryService fenceInventoryService = new FenceInventoryService();
    private final CableInventorySummaryService cableInventorySummaryService = new CableInventorySummaryService();

    ReportTextExporter(PowerSummaryService powerSummaryService) {
        this.powerSummaryService = powerSummaryService;
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
        for (PowerSource source : plan.powerSources()) {
            int sourceUsedWatts = powerSummaryService.summaries(plan).stream()
                    .filter(summary -> summary.sourceId().equals(source.id()))
                    .findFirst()
                    .map(PowerSummary::usedWatts)
                    .orElse(0);
            builder.append(source.name())
                    .append(": ")
                    .append(sourceUsedWatts)
                    .append(" W kasutusel, ")
                    .append(remainingWattsText(source.totalCapacityWatts() - sourceUsedWatts))
                    .append(lineSeparator);

            if (source.outlets().isEmpty()) {
                builder.append("  Väljundeid pole").append(lineSeparator);
            }

            for (int index = 0; index < source.outlets().size(); index++) {
                PowerOutlet outlet = source.outlets().get(index);
                appendOutletReport(builder, plan, source, outlet, index, reportScope, lineSeparator);
            }
            builder.append(lineSeparator);
        }
        appendUnconnectedConsumersReport(builder, plan, lineSeparator);
    }

    private void appendOutletReport(
            StringBuilder builder,
            EventPlan plan,
            PowerSource source,
            PowerOutlet outlet,
            int index,
            ReportExportScope reportScope,
            String lineSeparator
    ) {
        int usedWatts = usedWatts(plan, outlet.id());
        List<PowerConnection> connections = connectedConnections(plan, source.id(), outlet.id());
        if (reportScope == ReportExportScope.COMPACT && usedWatts == 0 && connections.isEmpty()) {
            return;
        }
        builder.append("  ")
                .append(outletDisplayName(outlet, outletTypeIndex(source, outlet, index)))
                .append(": ")
                .append(outlet.capacityWatts())
                .append(" W mahutavus, ")
                .append(usedWatts)
                .append(" W kasutusel, ")
                .append(remainingWattsText(outlet.capacityWatts() - usedWatts))
                .append(lineSeparator);

        if (connections.isEmpty()) {
            builder.append("    Tarbijaid pole").append(lineSeparator);
            return;
        }

        for (PowerConnection connection : connections) {
            PlannerObject consumerObject = plan.findObject(connection.consumerId()).orElse(null);
            if (!(consumerObject instanceof PowerConsumer consumer)) {
                continue;
            }
            builder.append("    - ")
                    .append(consumer.name())
                    .append(": ")
                    .append(plan.powerDemandWatts(connection))
                    .append(" W");
            if (!connection.defaultForConsumer()) {
                builder.append(" (seadme erand)");
            }
            if (!consumerObject.groupName().isBlank()) {
                builder.append(" (").append(consumerObject.groupName()).append(")");
            }
            builder.append(lineSeparator);
            if (!(consumerObject instanceof EquipmentContainer container)) {
                continue;
            }
            for (Equipment equipment : container.equipment()) {
                boolean usesConnection = equipment.usesDefaultPower()
                        ? connection.defaultForConsumer()
                        : connection.id().equals(equipment.powerConnectionId());
                if (!usesConnection) {
                    continue;
                }
                builder.append("      * ")
                        .append(equipment.name())
                        .append(": ")
                        .append(equipment.requiredWatts())
                        .append(" W")
                        .append(lineSeparator);
            }
        }
    }

    private List<PowerConnection> connectedConnections(EventPlan plan, String sourceId, String outletId) {
        return plan.powerConnections().stream()
                .filter(connection -> connection.sourceId().equals(sourceId))
                .filter(connection -> connection.outletId().equals(outletId))
                .toList();
    }

    private void appendUnconnectedConsumersReport(StringBuilder builder, EventPlan plan, String lineSeparator) {
        List<PowerConsumer> unconnectedConsumers = plan.powerConsumers().stream()
                .filter(consumer -> plan.findPowerConnectionForConsumer(consumer.id()).isEmpty())
                .toList();
        if (unconnectedConsumers.isEmpty()) {
            return;
        }

        builder.append("Ühendamata tarbijad").append(lineSeparator);
        for (PowerConsumer consumer : unconnectedConsumers) {
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

    private int usedWatts(EventPlan plan, String outletId) {
        return plan.outletDemandWatts(outletId);
    }

    private String outletDisplayName(PowerOutlet outlet, int matchingIndex) {
        if (!outlet.name().isBlank()) {
            return "%s (%s %d)".formatted(outlet.name(), outlet.type().displayName(), matchingIndex);
        }
        return "%s %d".formatted(outlet.type().displayName(), matchingIndex);
    }

    private int outletTypeIndex(PowerSource source, PowerOutlet targetOutlet, int targetIndex) {
        int matchingIndex = 0;
        for (int index = 0; index <= targetIndex; index++) {
            if (source.outlets().get(index).type() == targetOutlet.type()) {
                matchingIndex++;
            }
        }
        return matchingIndex;
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
