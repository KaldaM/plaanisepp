package ee.matteus.plaanisepp.core.service;

import ee.matteus.plaanisepp.core.model.EventPlan;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public final class PlanSnapshotService {
    private final PlanFileService planFileService = new PlanFileService();
    private PlanSnapshot.MapImageAsset cachedMapImageAsset;

    public PlanSnapshot create(EventPlan plan) {
        Properties properties = planFileService.createPlanProperties(plan, plan.mapImagePath());
        Map<String, String> values = properties.stringPropertyNames().stream()
                .collect(Collectors.toUnmodifiableMap(name -> name, properties::getProperty));
        return new PlanSnapshot(values, mapImageAsset(plan));
    }

    public EventPlan restore(PlanSnapshot snapshot) {
        Properties properties = new Properties();
        properties.putAll(snapshot.properties());
        EventPlan plan = planFileService.readPlan(properties);
        PlanSnapshot.MapImageAsset mapImageAsset = snapshot.mapImageAsset();
        if (mapImageAsset != null) {
            plan.setPackagedMapImage(mapImageAsset.entryName(), mapImageAsset.data());
        }
        return plan;
    }

    private PlanSnapshot.MapImageAsset mapImageAsset(EventPlan plan) {
        if (!plan.hasPackagedMapImage()) {
            return null;
        }
        byte[] imageData = plan.packagedMapImage();
        if (cachedMapImageAsset != null
                && cachedMapImageAsset.entryName().equals(plan.packagedMapImageEntry())
                && Arrays.equals(cachedMapImageAsset.data(), imageData)) {
            return cachedMapImageAsset;
        }
        cachedMapImageAsset = new PlanSnapshot.MapImageAsset(plan.packagedMapImageEntry(), imageData);
        return cachedMapImageAsset;
    }
}
