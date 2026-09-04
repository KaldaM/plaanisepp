package ee.matteus.plaanisepp.core.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventPlanObjectLayerTest {

    @Test
    void movesObjectOneLayerAtATime() {
        EventPlan plan = planWithObjects("first", "middle", "last");

        assertTrue(plan.moveObjectsByLayer(Set.of("middle"), 1));
        assertEquals(List.of("first", "last", "middle"), ids(plan));
        assertFalse(plan.moveObjectsByLayer(Set.of("middle"), 1));

        assertTrue(plan.moveObjectsByLayer(Set.of("middle"), -1));
        assertEquals(List.of("first", "middle", "last"), ids(plan));
    }

    @Test
    void movesLogicalObjectRowsAsOneLayerGroup() {
        EventPlan plan = planWithObjects("fence-1", "fence-2", "tent");

        assertTrue(plan.moveObjectsByLayer(Set.of("fence-1", "fence-2"), 1));

        assertEquals(List.of("tent", "fence-1", "fence-2"), ids(plan));
    }

    private EventPlan planWithObjects(String... ids) {
        EventPlan plan = new EventPlan("Kihid");
        for (String id : ids) {
            plan.addObject(new CustomObject(id, id, new Position(0, 0)));
        }
        return plan;
    }

    private List<String> ids(EventPlan plan) {
        return plan.objects().stream().map(PlannerObject::id).toList();
    }
}
