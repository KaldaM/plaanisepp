package ee.matteus.plaanisepp.gui;

import ee.matteus.plaanisepp.core.model.EventPlan;
import ee.matteus.plaanisepp.core.model.PowerSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class FestivalSummaryTextFormatter {
    String format(String festivalName, List<EventPlan> plans, List<String> failedPlanNames) {
        StringBuilder text = new StringBuilder();
        text.append(festivalName).append('\n');
        text.append("Plaane kokku: ").append(plans.size()).append('\n').append('\n');
        text.append("Kasutatavad Tartu linna püsivoolukilbid").append('\n');

        Map<Long, CabinetUse> cabinets = new LinkedHashMap<>();
        for (EventPlan plan : plans) {
            Set<String> usedSourceIds = new LinkedHashSet<>();
            plan.powerConnections().forEach(connection -> usedSourceIds.add(connection.sourceId()));
            plan.objects().stream()
                    .filter(PowerSource.class::isInstance)
                    .map(PowerSource.class::cast)
                    .filter(source -> usedSourceIds.contains(source.id()))
                    .forEach(source -> TartuPowerCabinetImportService.sourceIdFromNotes(source.notes())
                            .ifPresent(sourceId -> cabinets
                                    .computeIfAbsent(sourceId, ignored -> new CabinetUse(source.name()))
                                    .planNames.add(plan.name())));
        }

        if (cabinets.isEmpty()) {
            text.append("  Üheski plaanis pole Tartu linna kilbist vooluühendust.").append('\n');
        } else {
            cabinets.values().stream()
                    .sorted((left, right) -> String.CASE_INSENSITIVE_ORDER.compare(left.name, right.name))
                    .forEach(cabinet -> text.append("  • ")
                            .append(cabinet.name)
                            .append(" — ")
                            .append(String.join(", ", cabinet.planNames))
                            .append('\n'));
        }

        if (!failedPlanNames.isEmpty()) {
            text.append('\n').append("Avamata jäänud plaanid").append('\n');
            failedPlanNames.forEach(name -> text.append("  • ").append(name).append('\n'));
        }
        return text.toString();
    }

    private static final class CabinetUse {
        private final String name;
        private final List<String> planNames = new ArrayList<>();

        private CabinetUse(String name) {
            this.name = name;
        }
    }
}
