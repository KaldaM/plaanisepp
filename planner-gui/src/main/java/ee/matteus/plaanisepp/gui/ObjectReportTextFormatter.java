package ee.matteus.plaanisepp.gui;

import ee.matteus.plaanisepp.core.model.AreaObject;
import ee.matteus.plaanisepp.core.model.CustomObject;
import ee.matteus.plaanisepp.core.model.EventPlan;
import ee.matteus.plaanisepp.core.model.FenceRow;
import ee.matteus.plaanisepp.core.model.LineObject;
import ee.matteus.plaanisepp.core.model.MarkerObject;
import ee.matteus.plaanisepp.core.model.PlannerObject;
import ee.matteus.plaanisepp.core.model.PowerSource;
import ee.matteus.plaanisepp.core.model.Tent;
import ee.matteus.plaanisepp.core.model.TextObject;
import ee.matteus.plaanisepp.core.service.InventorySummaryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

final class ObjectReportTextFormatter {
    void appendInventory(StringBuilder builder, EventPlan plan, String lineSeparator) {
        List<InventorySummaryService.NamedItem> items = new InventorySummaryService()
                .summarize(plan)
                .objectInventoryItems();
        Map<String, Integer> totals = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        items.forEach(item -> totals.merge(item.name(), item.count(), Integer::sum));
        plan.standaloneInventoryItems().forEach(item -> totals.merge(item.name(), item.quantity(), Integer::sum));
        if (totals.isEmpty()) {
            return;
        }
        builder.append("Inventar").append(lineSeparator);
        totals.forEach((name, quantity) -> builder.append("  - ")
                .append(name)
                .append(": ")
                .append(quantity)
                .append(" tk")
                .append(lineSeparator));
        builder.append(lineSeparator);

        List<ee.matteus.plaanisepp.core.model.InventoryItem> notedStandaloneItems = plan.standaloneInventoryItems()
                .stream()
                .filter(item -> !item.notes().isBlank())
                .toList();
        if (notedStandaloneItems.isEmpty()) {
            return;
        }
        builder.append("Lisainventari märkmed").append(lineSeparator);
        notedStandaloneItems.forEach(item -> {
            builder.append("  - ")
                    .append(item.name())
                    .append(": ")
                    .append(item.quantity())
                    .append(" tk · ")
                    .append(item.notes())
                    .append(lineSeparator);
        });
        builder.append(lineSeparator);
    }

    void appendGroups(StringBuilder builder, EventPlan plan, String lineSeparator) {
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

    void appendTextNotes(StringBuilder builder, EventPlan plan, String lineSeparator) {
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

    private Map<String, List<PlannerObject>> objectsByGroup(EventPlan plan) {
        Map<String, List<PlannerObject>> objectsByGroup = new TreeMap<>();
        for (PlannerObject object : plan.objects()) {
            String groupName = object.groupName().isBlank() ? "Määramata" : object.groupName();
            objectsByGroup.computeIfAbsent(groupName, ignored -> new ArrayList<>()).add(object);
        }
        return objectsByGroup;
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
}
