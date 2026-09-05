package ee.matteus.plaanisepp.gui;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlanDocumentStateTest {
    @Test
    void failedSavePreservesLastSuccessAndDirtyState() {
        PlanDocumentState state = new PlanDocumentState();
        state.saveSucceeded();
        String details = state.saveDetails();
        state.markDirty();
        state.beginSave();
        assertTrue(state.saveStatusText().startsWith("Salvestamine…"));
        state.saveFailed();
        assertTrue(state.hasUnsavedChanges());
        assertTrue(state.hasSaveError());
        assertEquals(details, state.saveDetails());
        state.resetSaveInfo();
        assertTrue(state.saveDetails().contains("selles seansis puudub"));
        assertFalse(state.hasSaveError());
    }

    @Test
    void newDocumentIsClean() {
        PlanDocumentState state = new PlanDocumentState();

        assertFalse(state.hasUnsavedChanges());
        assertEquals("Plaanisepp", state.windowTitle(null));
        assertEquals("Muudatusi pole", state.saveStatusText());
    }

    @Test
    void dirtyDocumentIncludesMarkerAndFileNameInTitle() {
        PlanDocumentState state = new PlanDocumentState();

        state.markDirty();

        assertTrue(state.hasUnsavedChanges());
        assertEquals(
                "* Plaanisepp - test.pplan",
                state.windowTitle(new File("test.pplan"))
        );
        assertEquals("Salvestamata muudatused", state.saveStatusText());
    }

    @Test
    void markingDocumentCleanRemovesUnsavedState() {
        PlanDocumentState state = new PlanDocumentState();
        state.markDirty();

        state.markClean();

        assertFalse(state.hasUnsavedChanges());
        assertEquals("Plaanisepp - test.pplan", state.windowTitle(new File("test.pplan")));
        assertEquals("Muudatusi pole", state.saveStatusText());
    }
}
