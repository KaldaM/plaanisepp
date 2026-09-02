package ee.matteus.plaanisepp.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextObjectTest {
    @Test
    void clearingSourceTurnsLinkedTextIntoStaticTextWithoutChangingContent() {
        TextObject text = new TextObject("text", "Inventar", new Position(10, 20));
        text.setNotes("Lauad: 5 tk");
        text.setSourceObjectId("source");
        text.setSourceType(TextObjectSourceType.INVENTORY);
        text.setSyncSourceNotes(true);
        text.setShowReferenceLine(true);

        text.setSourceObjectId("");

        assertEquals("Inventar", text.name());
        assertEquals("Lauad: 5 tk", text.notes());
        assertEquals(TextObjectSourceType.NONE, text.sourceType());
        assertFalse(text.syncSourceNotes());
        assertFalse(text.showReferenceLine());
        assertTrue(text.sourceObjectId().isBlank());
    }
}
