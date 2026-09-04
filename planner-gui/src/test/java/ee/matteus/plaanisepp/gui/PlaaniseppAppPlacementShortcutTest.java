package ee.matteus.plaanisepp.gui;

import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PlaaniseppAppPlacementShortcutTest {

    private static final List<PlacementType> TECHNICIAN_TYPES = List.of(PlacementType.values());

    private static final List<PlacementType> ORGANIZER_TYPES = List.of(
            PlacementType.TENT,
            PlacementType.CUSTOM_OBJECT,
            PlacementType.LINE_OBJECT,
            PlacementType.AREA_OBJECT,
            PlacementType.FENCE_ROW,
            PlacementType.FENCE_RING,
            PlacementType.TEXT_OBJECT,
            PlacementType.MARKER_OBJECT,
            PlacementType.DJ_TRUCK
    );

    @Test
    void numberShortcutsFollowPlacementMenuOrder() {
        assertEquals(PlacementType.TENT, shortcut(KeyCode.DIGIT1, TECHNICIAN_TYPES));
        assertEquals(PlacementType.CUSTOM_OBJECT, shortcut(KeyCode.DIGIT2, TECHNICIAN_TYPES));
        assertEquals(PlacementType.LINE_OBJECT, shortcut(KeyCode.DIGIT3, TECHNICIAN_TYPES));
        assertEquals(PlacementType.AREA_OBJECT, shortcut(KeyCode.DIGIT4, TECHNICIAN_TYPES));
        assertEquals(PlacementType.FENCE_ROW, shortcut(KeyCode.DIGIT5, TECHNICIAN_TYPES));
        assertEquals(PlacementType.FENCE_RING, shortcut(KeyCode.DIGIT6, TECHNICIAN_TYPES));
        assertEquals(PlacementType.TEXT_OBJECT, shortcut(KeyCode.DIGIT7, TECHNICIAN_TYPES));
        assertEquals(PlacementType.MARKER_OBJECT, shortcut(KeyCode.DIGIT8, TECHNICIAN_TYPES));
        assertEquals(PlacementType.POWER_SOURCE, shortcut(KeyCode.DIGIT9, TECHNICIAN_TYPES));
        assertEquals(PlacementType.DISTRIBUTION_PANEL, shortcut(KeyCode.DIGIT0, TECHNICIAN_TYPES));
    }

    @Test
    void numpadUsesTheSamePlacementOrder() {
        assertEquals(PlacementType.TENT, shortcut(KeyCode.NUMPAD1, TECHNICIAN_TYPES));
        assertEquals(PlacementType.CUSTOM_OBJECT, shortcut(KeyCode.NUMPAD2, TECHNICIAN_TYPES));
        assertEquals(PlacementType.LINE_OBJECT, shortcut(KeyCode.NUMPAD3, TECHNICIAN_TYPES));
        assertEquals(PlacementType.AREA_OBJECT, shortcut(KeyCode.NUMPAD4, TECHNICIAN_TYPES));
        assertEquals(PlacementType.FENCE_ROW, shortcut(KeyCode.NUMPAD5, TECHNICIAN_TYPES));
        assertEquals(PlacementType.FENCE_RING, shortcut(KeyCode.NUMPAD6, TECHNICIAN_TYPES));
        assertEquals(PlacementType.TEXT_OBJECT, shortcut(KeyCode.NUMPAD7, TECHNICIAN_TYPES));
        assertEquals(PlacementType.MARKER_OBJECT, shortcut(KeyCode.NUMPAD8, TECHNICIAN_TYPES));
        assertEquals(PlacementType.POWER_SOURCE, shortcut(KeyCode.NUMPAD9, TECHNICIAN_TYPES));
        assertEquals(PlacementType.DISTRIBUTION_PANEL, shortcut(KeyCode.NUMPAD0, TECHNICIAN_TYPES));
    }

    @Test
    void organizerNinthShortcutFollowsItsShorterMenu() {
        assertEquals(PlacementType.DJ_TRUCK, shortcut(KeyCode.DIGIT9, ORGANIZER_TYPES));
        assertNull(shortcut(KeyCode.DIGIT0, ORGANIZER_TYPES));
    }

    @Test
    void shortcutUsesProvidedMenuOrder() {
        List<PlacementType> reorderedTypes = List.of(PlacementType.MARKER_OBJECT, PlacementType.TENT);

        assertEquals(PlacementType.MARKER_OBJECT, shortcut(KeyCode.DIGIT1, reorderedTypes));
        assertEquals(PlacementType.TENT, shortcut(KeyCode.DIGIT2, reorderedTypes));
        assertNull(shortcut(KeyCode.DIGIT3, reorderedTypes));
    }

    @Test
    void helpUsesVisibleMenuOrderAndOnlyListsAvailableShortcuts() {
        String organizerHelp = PlaaniseppApp.placementShortcutHelp(ORGANIZER_TYPES);
        String technicianHelp = PlaaniseppApp.placementShortcutHelp(TECHNICIAN_TYPES);

        assertEquals("Ctrl+Shift+9           Red Bull DJ Truck", organizerHelp.lines().toList().get(8));
        assertEquals(9, organizerHelp.lines().count());
        assertEquals("Ctrl+Shift+9           Elektrikapp", technicianHelp.lines().toList().get(8));
        assertEquals("Ctrl+Shift+0           Alajaotuskilp", technicianHelp.lines().toList().get(9));
        assertEquals(10, technicianHelp.lines().count());
    }

    private PlacementType shortcut(KeyCode keyCode, List<PlacementType> availableTypes) {
        return PlaaniseppApp.placementTypeForShortcut(keyCode, availableTypes);
    }
}
