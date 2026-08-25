package ee.matteus.plaanisepp.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventPlanChecklistTest {
    @Test
    void addsRenamesCompletesMovesAndRemovesChecklistItems() {
        EventPlan plan = new EventPlan("Test");
        ChecklistItem first = plan.addChecklistItem("  Esimene  ");
        ChecklistItem second = plan.addChecklistItem("Teine");

        first.rename("Muudetud");
        first.setCompleted(true);

        assertTrue(plan.moveChecklistItem(second.id(), -1));
        assertEquals(second.id(), plan.checklistItems().getFirst().id());
        assertEquals("Muudetud", plan.checklistItems().getLast().text());
        assertTrue(plan.checklistItems().getLast().completed());
        assertTrue(plan.removeChecklistItem(second.id()));
        assertEquals(1, plan.checklistItems().size());
        assertFalse(plan.moveChecklistItem(first.id(), -1));
    }

    @Test
    void refusesBlankChecklistText() {
        EventPlan plan = new EventPlan("Test");

        assertThrows(IllegalArgumentException.class, () -> plan.addChecklistItem("  "));
    }
}
