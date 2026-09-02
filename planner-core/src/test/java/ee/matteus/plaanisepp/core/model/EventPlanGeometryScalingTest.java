package ee.matteus.plaanisepp.core.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventPlanGeometryScalingTest {
    @Test
    void scalesLockedObjectsShapePointsAndLabelOffsets() {
        EventPlan plan = new EventPlan("Test");
        Tent tent = new Tent("tent", "Telk", new Position(10, 20));
        tent.setMapLabelOffset(new Position(3, 4));
        tent.setLocked(true);
        LineObject line = new LineObject("line", "Joon", new Position(5, 6));
        line.setPoints(List.of(new Position(5, 6), new Position(15, 16)));
        plan.addObject(tent);
        plan.addObject(line);

        plan.scalePixelGeometry(4);

        assertEquals(new Position(40, 80), tent.position());
        assertEquals(new Position(12, 16), tent.mapLabelOffset());
        assertTrue(tent.locked());
        assertEquals(new Position(20, 24), line.position());
        assertEquals(List.of(new Position(20, 24), new Position(60, 64)), line.points());
    }
}
