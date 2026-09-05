package ee.matteus.plaanisepp.gui;

import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

/** Presentation only: deliberately attached to chrome, never the map/export subtree. */
final class UiTheme {
    private UiTheme() {}

    enum LockTone { INDIVIDUAL, GROUP, BOTH }

    static LockTone lockTone(boolean individual, boolean group) {
        return group ? individual ? LockTone.BOTH : LockTone.GROUP : LockTone.INDIVIDUAL;
    }

    static HBox listRow(Button visibility, Button lock, Node swatch, Label title, Label detail) {
        VBox text = new VBox(2, title, detail);
        text.setMinWidth(0);
        HBox.setHgrow(text, javafx.scene.layout.Priority.ALWAYS);
        title.setTooltip(new javafx.scene.control.Tooltip(title.getText()));
        detail.setTooltip(new javafx.scene.control.Tooltip(detail.getText()));
        HBox row = new HBox(visibility, lock, swatch, text);
        row.getStyleClass().add("plan-row");
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    static void install(Parent surface) {
        surface.getStyleClass().add("ui-surface");
        surface.getStylesheets().add(UiTheme.class.getResource("plaanisepp.css").toExternalForm());
    }

    static void state(Node node, String name, boolean active) {
        node.pseudoClassStateChanged(PseudoClass.getPseudoClass(name), active);
    }

    static void row(Node cell, boolean selected, boolean hidden, boolean group) {
        state(cell, "plan-selected", selected);
        state(cell, "plan-hidden", hidden);
        state(cell, "plan-group", group);
        drop(cell, null);
    }

    static void drop(Node cell, Boolean above) {
        state(cell, "drop-above", Boolean.TRUE.equals(above));
        state(cell, "drop-below", Boolean.FALSE.equals(above));
    }
}
