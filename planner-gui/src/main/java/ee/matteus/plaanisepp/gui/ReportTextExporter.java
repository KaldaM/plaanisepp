package ee.matteus.plaanisepp.gui;

import ee.matteus.plaanisepp.core.model.EventPlan;
import ee.matteus.plaanisepp.core.service.InventorySummaryService;
import ee.matteus.plaanisepp.core.service.PowerHierarchyService;

final class ReportTextExporter {
    private final FenceReportTextFormatter fenceReportTextFormatter =
            new FenceReportTextFormatter(new InventorySummaryService());
    private final CableReportTextFormatter cableReportTextFormatter =
            new CableReportTextFormatter(new CableInventorySummaryService());
    private final PowerReportTextFormatter powerReportTextFormatter =
            new PowerReportTextFormatter(new PowerHierarchyService());
    private final PlanOverviewTextFormatter planOverviewTextFormatter = new PlanOverviewTextFormatter();
    private final ObjectReportTextFormatter objectReportTextFormatter = new ObjectReportTextFormatter();

    ReportTextExporter() {
    }

    String export(EventPlan plan, ReportExportScope reportScope, boolean includePower, boolean includeCables, boolean includeGroups) {
        String lineSeparator = System.lineSeparator();
        StringBuilder builder = new StringBuilder();
        planOverviewTextFormatter.append(builder, plan, lineSeparator);

        if (includePower) {
            powerReportTextFormatter.append(builder, plan, reportScope, lineSeparator);
        }

        if (includeCables) {
            cableReportTextFormatter.append(builder, plan, lineSeparator);
        }

        if (includeGroups) {
            objectReportTextFormatter.appendGroups(builder, plan, lineSeparator);
        }
        fenceReportTextFormatter.append(builder, plan, lineSeparator);
        objectReportTextFormatter.appendInventory(builder, plan, lineSeparator);
        objectReportTextFormatter.appendTextNotes(builder, plan, lineSeparator);
        return builder.toString();
    }
}
