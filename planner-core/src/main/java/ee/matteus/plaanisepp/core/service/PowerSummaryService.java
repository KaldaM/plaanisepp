package ee.matteus.plaanisepp.core.service;

import ee.matteus.plaanisepp.core.model.EventPlan;
import java.util.List;

public class PowerSummaryService {
    private final PowerHierarchyService powerHierarchyService = new PowerHierarchyService();

    public List<PowerSummary> summaries(EventPlan plan) {
        return powerHierarchyService.summarize(plan).sources().stream()
                .map(source -> new PowerSummary(
                        source.id(), source.name(), source.capacityWatts(), source.usedWatts()
                ))
                .toList();
    }
}
