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

    @Test
    void movesMultipleObjectsToFrontAndBackWithoutChangingTheirOrder() {
        EventPlan plan = planWithObjects("first", "selected-1", "middle", "selected-2", "last");
        Set<String> selected = Set.of("selected-1", "selected-2");

        assertTrue(plan.moveObjectsToLayerBoundary(selected, true));
        assertEquals(List.of("first", "middle", "last", "selected-1", "selected-2"), ids(plan));
        assertFalse(plan.moveObjectsToLayerBoundary(selected, true));

        assertTrue(plan.moveObjectsToLayerBoundary(selected, false));
        assertEquals(List.of("selected-1", "selected-2", "first", "middle", "last"), ids(plan));
        assertFalse(plan.moveObjectsToLayerBoundary(selected, false));
    }

    @Test
    void movesMultipleLayerEntriesToIndexWithoutChangingTheirRelativeOrder() {
        EventPlan plan = planWithObjects("first", "selected-1", "middle", "selected-2", "last");

        assertTrue(plan.moveLayerEntriesToIndex(List.of(
                PlanLayerEntry.object("selected-2"),
                PlanLayerEntry.object("selected-1")
        ), 3));

        assertEquals(
                List.of("first", "middle", "last", "selected-1", "selected-2"),
                plan.layerOrder().stream().map(PlanLayerEntry::id).toList()
        );
    }

    @Test
    void movesCableBetweenMapObjects() {
        EventPlan plan = new EventPlan("Kaablikihid");
        PowerSource source = new PowerSource("source", "Kilp", new Position(0, 0));
        source.addOutlet(new PowerOutlet("outlet", ConnectorType.SCHUKO_230V, 3500));
        Tent tent = new Tent("tent", "Telk", new Position(10, 10));
        CustomObject shape = new CustomObject("shape", "Kujund", new Position(20, 20));
        plan.addObject(source);
        plan.addObject(tent);
        plan.addObject(shape);
        PowerConnection cable = plan.connectToPower(
                source.id(), tent.id(), ConnectorType.SCHUKO_230V, "outlet"
        ).orElseThrow();

        assertEquals(List.of(
                PlanLayerEntry.object(source.id()),
                PlanLayerEntry.object(tent.id()),
                PlanLayerEntry.cable(cable.id()),
                PlanLayerEntry.object(shape.id())
        ), plan.layerOrder());
        assertTrue(plan.moveLayerEntriesToBoundary(List.of(PlanLayerEntry.cable(cable.id())), false));
        assertEquals(PlanLayerEntry.cable(cable.id()), plan.layerOrder().getFirst());
        assertTrue(plan.moveLayerEntries(List.of(PlanLayerEntry.cable(cable.id())), 1));
        assertEquals(PlanLayerEntry.object(source.id()), plan.layerOrder().getFirst());
        assertEquals(PlanLayerEntry.cable(cable.id()), plan.layerOrder().get(1));
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
