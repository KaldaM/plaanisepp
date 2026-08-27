package ee.matteus.plaanisepp.gui;

import ee.matteus.plaanisepp.core.model.ConnectorType;
import ee.matteus.plaanisepp.core.model.AreaObject;
import ee.matteus.plaanisepp.core.model.EventPlan;
import ee.matteus.plaanisepp.core.model.FenceRow;
import ee.matteus.plaanisepp.core.model.LineObject;
import ee.matteus.plaanisepp.core.model.MarkerObject;
import ee.matteus.plaanisepp.core.model.PlannerObject;
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
    private final FenceReportTextFormatter fenceReportTextFormatter =
            new FenceReportTextFormatter(new FenceInventoryService());
    private final CableInventorySummaryService cableInventorySummaryService = new CableInventorySummaryService();
    private final PowerHierarchyService powerHierarchyService = new PowerHierarchyService();
    private final PowerReportTextFormatter powerReportTextFormatter = new PowerReportTextFormatter(powerHierarchyService);

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
            powerReportTextFormatter.append(builder, plan, reportScope, lineSeparator);
        }

        if (includeCables) {
            appendCableReport(builder, plan, lineSeparator);
        }

        if (includeGroups) {
            appendGroupReport(builder, plan, lineSeparator);
        }
        fenceReportTextFormatter.append(builder, plan, lineSeparator);
        appendTextObjectReport(builder, plan, lineSeparator);
        return builder.toString();
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

    private void appendCableReport(StringBuilder builder, EventPlan plan, String lineSeparator) {
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
