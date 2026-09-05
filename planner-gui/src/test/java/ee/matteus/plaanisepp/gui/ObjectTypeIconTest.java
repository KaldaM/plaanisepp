package ee.matteus.plaanisepp.gui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ObjectTypeIconTest {
    @Test void legendTitlesAndApplicationAliasesResolveToTheSameSymbols() {
        for (ObjectTypeIcon icon : ObjectTypeIcon.values()) {
            assertEquals(icon, ObjectTypeIcon.forType(icon.title));
        }
        assertEquals(ObjectTypeIcon.FENCE, ObjectTypeIcon.forType("Aiarida"));
        assertEquals(ObjectTypeIcon.POWER, ObjectTypeIcon.forType("Elektrikapp"));
        assertNotEquals(ObjectTypeIcon.POWER, ObjectTypeIcon.forType("Alajaotuskilp"));
        assertNotEquals(ObjectTypeIcon.CIRCLE, ObjectTypeIcon.forType("Ristkülik"));
    }
}
