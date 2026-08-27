package ee.matteus.plaanisepp.core.service;

import ee.matteus.plaanisepp.core.model.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class FenceRingGenerator {
    private static final int MINIMUM_RING_SLOTS = 3;

    private FenceRingGenerator() {
    }

    public static Result generate(
            Position center,
            double requestedRadiusMeters,
            double segmentLengthMeters,
            double pixelsPerMeter
    ) {
        if (center == null) {
            throw new IllegalArgumentException("Aiaringi keskpunkt on määramata.");
        }
        if (requestedRadiusMeters <= 0 || segmentLengthMeters <= 0 || pixelsPerMeter <= 0) {
            throw new IllegalArgumentException("Raadius, aialõigu pikkus ja mõõtkava peavad olema positiivsed.");
        }
        int totalSlots = Math.max(
                MINIMUM_RING_SLOTS,
                (int) Math.round(2 * Math.PI * requestedRadiusMeters / segmentLengthMeters)
        );
        int fenceCount = totalSlots;
        double stepRadians = 2 * Math.PI / totalSlots;
        double actualRadiusMeters = segmentLengthMeters / (2 * Math.sin(Math.PI / totalSlots));
        List<Position> points = new ArrayList<>();
        for (int index = 0; index <= fenceCount; index++) {
            double angle = index * stepRadians;
            points.add(new Position(
                    center.x() + Math.cos(angle) * actualRadiusMeters * pixelsPerMeter,
                    center.y() + Math.sin(angle) * actualRadiusMeters * pixelsPerMeter
            ));
        }
        points.set(points.size() - 1, points.getFirst());

        return new Result(
                Collections.unmodifiableList(points),
                totalSlots,
                fenceCount,
                requestedRadiusMeters,
                actualRadiusMeters,
                segmentLengthMeters
        );
    }

    public record Result(
            List<Position> points,
            int totalSlots,
            int fenceCount,
            double requestedRadiusMeters,
            double actualRadiusMeters,
            double segmentLengthMeters
    ) {
        public Result {
            points = List.copyOf(points);
        }

        public double radiusDeviationMeters() {
            return actualRadiusMeters - requestedRadiusMeters;
        }

    }
}
