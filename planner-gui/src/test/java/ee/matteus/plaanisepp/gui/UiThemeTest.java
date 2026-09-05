package ee.matteus.plaanisepp.gui;

import javafx.css.PseudoClass;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UiThemeTest {
    @Test
    void recycledRowClearsSelectionVisibilityGroupAndDropStates() {
        VBox cell = new VBox();
        UiTheme.row(cell, true, true, true);
        UiTheme.drop(cell, true);
        UiTheme.row(cell, false, false, false);

        for (String state : new String[]{"plan-selected", "plan-hidden", "plan-group", "drop-above", "drop-below"}) {
            assertFalse(cell.getPseudoClassStates().contains(PseudoClass.getPseudoClass(state)), state);
        }
    }

    @Test
    void dropIndicatorPreservesPlanSelectionAndSwitchesDirection() {
        VBox cell = new VBox();
        UiTheme.row(cell, true, false, false);
        UiTheme.drop(cell, true);
        UiTheme.drop(cell, false);

        assertTrue(cell.getPseudoClassStates().contains(PseudoClass.getPseudoClass("plan-selected")));
        assertFalse(cell.getPseudoClassStates().contains(PseudoClass.getPseudoClass("drop-above")));
        assertTrue(cell.getPseudoClassStates().contains(PseudoClass.getPseudoClass("drop-below")));
    }

    @Test
    void themeIsLocalToChromeAndResourceIsPackaged() {
        VBox chrome = new VBox();
        VBox map = new VBox();
        VBox root = new VBox(chrome, map);
        UiTheme.install(chrome);

        assertEquals(1, chrome.getStylesheets().size());
        assertTrue(root.getStylesheets().isEmpty());
        assertTrue(map.getStylesheets().isEmpty());
        assertFalse(map.getStyleClass().contains("ui-surface"));
    }
}
