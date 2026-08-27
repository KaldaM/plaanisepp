package ee.matteus.plaanisepp.core.service;

import ee.matteus.plaanisepp.core.model.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FenceRingGeneratorTest {
    private static final double EPSILON = 0.000001;

    @Test
    void generatesClosedRingFromExactLengthFenceSegments() {
        FenceRingGenerator.Result result = FenceRingGenerator.generate(
                new Position(100, 200), 8, 3.5, 10
        );

        assertEquals(14, result.totalSlots());
        assertEquals(14, result.fenceCount());
        assertEquals(15, result.points().size());
        assertEquals(result.points().getFirst(), result.points().getLast());
        assertSegmentLengths(result, 35);
    }

    @Test
    void reportsActualRadiusAndDeviation() {
        FenceRingGenerator.Result result = FenceRingGenerator.generate(
                new Position(0, 0), 8, 3.5, 1
        );

        assertEquals(8, result.requestedRadiusMeters(), EPSILON);
        assertEquals(
                3.5 / (2 * Math.sin(Math.PI / result.totalSlots())),
                result.actualRadiusMeters(),
                EPSILON
        );
        assertEquals(
                result.actualRadiusMeters() - 8,
                result.radiusDeviationMeters(),
                EPSILON
        );
    }

    @Test
    void rejectsInvalidInputs() {
        assertThrows(IllegalArgumentException.class, () -> FenceRingGenerator.generate(
                new Position(0, 0), 0, 3.5, 10
        ));
    }

    private void assertSegmentLengths(FenceRingGenerator.Result result, double expectedPixels) {
        for (int index = 1; index < result.points().size(); index++) {
            Position previous = result.points().get(index - 1);
            Position current = result.points().get(index);
            assertEquals(expectedPixels, Math.hypot(
                    current.x() - previous.x(),
                    current.y() - previous.y()
            ), EPSILON);
        }
    }
}
