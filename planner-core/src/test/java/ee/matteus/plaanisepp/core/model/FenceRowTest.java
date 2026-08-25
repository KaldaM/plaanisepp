package ee.matteus.plaanisepp.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FenceRowTest {
    @Test
    void calculatesPhysicalLengthAndEndPosition() {
        FenceRow row = new FenceRow("fence", "Aiarida", new Position(10, 20));
        row.setSegmentCount(4);
        row.setRotationDegrees(90);

        Position end = row.endPosition(10);

        assertEquals(14, row.totalLengthMeters());
        assertEquals(10, end.x(), 0.0001);
        assertEquals(160, end.y(), 0.0001);
    }

    @Test
    void refusesInvalidPhysicalDimensions() {
        FenceRow row = new FenceRow("fence", "Aiarida", new Position(0, 0));

        assertThrows(IllegalArgumentException.class, () -> row.setSegmentCount(0));
        assertThrows(IllegalArgumentException.class, () -> row.setSegmentLengthMeters(0));
    }
}
