package ee.matteus.plaanisepp.gui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlanHistoryTest {
    @Test
    void movesBackwardAndForwardBetweenRecordedStates() {
        PlanHistory<String> history = new PlanHistory<>(10);
        history.reset("initial");
        history.record("first");
        history.record("second");

        assertEquals("first", history.undo().orElseThrow());
        assertEquals("initial", history.undo().orElseThrow());
        assertFalse(history.canUndo());
        assertEquals("first", history.redo().orElseThrow());
        assertEquals("second", history.redo().orElseThrow());
        assertFalse(history.canRedo());
    }

    @Test
    void newChangeClearsRedoStates() {
        PlanHistory<String> history = new PlanHistory<>(10);
        history.reset("initial");
        history.record("first");
        history.record("second");
        history.undo();

        history.record("replacement");

        assertFalse(history.canRedo());
        assertEquals("first", history.undo().orElseThrow());
    }

    @Test
    void ignoresRepeatedEquivalentState() {
        PlanHistory<String> history = new PlanHistory<>(10);
        history.reset("initial");

        history.record("initial");

        assertFalse(history.canUndo());
    }

    @Test
    void discardsOldestUndoStatesAboveLimit() {
        PlanHistory<String> history = new PlanHistory<>(2);
        history.reset("initial");
        history.record("first");
        history.record("second");
        history.record("third");

        assertEquals("second", history.undo().orElseThrow());
        assertEquals("first", history.undo().orElseThrow());
        assertTrue(history.undo().isEmpty());
    }

    @Test
    void replacesCurrentStateWithoutCreatingAnUndoStep() {
        PlanHistory<String> history = new PlanHistory<>(10);
        history.reset("initial");
        history.record("changed");

        history.replaceCurrent("saved");

        assertEquals("initial", history.undo().orElseThrow());
        assertEquals("saved", history.redo().orElseThrow());
    }
}
