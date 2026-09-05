package ee.matteus.plaanisepp.gui;

import javafx.css.PseudoClass;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.collections.ListChangeListener;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

/** Presentation only: deliberately attached to chrome, never the map/export subtree. */
final class UiTheme {
    private UiTheme() {}

    enum LockTone { INDIVIDUAL, GROUP, BOTH }

    static void configureSidebarWidth(SplitPane splitPane, Region sidebar) {
        sidebar.setMinWidth(280);
        sidebar.setPrefWidth(360);
        SplitPane.setResizableWithParent(sidebar, false);
        // Set the initial width once layout knows the actual window width. Later
        // window resizes preserve the width, including a user's divider adjustment.
        splitPane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> value, Number oldWidth, Number width) {
                if (width.doubleValue() <= 0) return;
                splitPane.setDividerPositions(sidebar.getPrefWidth() / width.doubleValue());
                splitPane.widthProperty().removeListener(this);
            }
        });
    }

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
        if (!surface.getStyleClass().contains("ui-surface")) {
            surface.getStyleClass().add("ui-surface");
        }
        String stylesheet = UiTheme.class.getResource("plaanisepp.css").toExternalForm();
        if (!surface.getStylesheets().contains(stylesheet)) {
            surface.getStylesheets().add(stylesheet);
        }
    }

    static <T, D extends Dialog<T>> D dialog(D dialog) {
        install(dialog.getDialogPane());
        localizeDialogButtons(dialog);
        dialog.getDialogPane().getButtonTypes().addListener((ListChangeListener<ButtonType>) change ->
                localizeDialogButtons(dialog));
        return dialog;
    }

    static ContextMenu contextMenu(ContextMenu menu) {
        menu.addEventHandler(WindowEvent.WINDOW_SHOWN, event -> stylePopup(menu));
        return menu;
    }

    static void watchOwnedPopups(Stage owner) {
        // JavaFX creates submenu, text-field and colour-picker windows internally.
        // Their stylesheets are replaced from the root owner during show(), so theme
        // only the owned transient window after it joins the visible window list.
        ListChangeListener<Window> listener = change -> {
            while (change.next()) {
                for (Window window : change.getAddedSubList()) {
                    if (!isOwnedBy(window, owner)) continue;
                    if (window instanceof PopupControl popup) {
                        stylePopup(popup);
                    } else if (window.getScene() != null
                            && window.getScene().getRoot().getStyleClass().contains("custom-color-dialog")) {
                        install(window.getScene().getRoot());
                    }
                }
            }
        };
        Window.getWindows().addListener(listener);
        owner.addEventHandler(WindowEvent.WINDOW_HIDDEN, event -> Window.getWindows().removeListener(listener));
    }

    private static boolean isOwnedBy(Window window, Stage owner) {
        Window parent = window instanceof PopupWindow popup ? popup.getOwnerWindow()
                : window instanceof Stage stage ? stage.getOwner() : null;
        return parent != null && (parent == owner || isOwnedBy(parent, owner));
    }

    private static void stylePopup(PopupControl popup) {
        if (!popup.getStyleClass().contains("ui-surface")) popup.getStyleClass().add("ui-surface");
        String stylesheet = UiTheme.class.getResource("plaanisepp.css").toExternalForm();
        if (!popup.getScene().getStylesheets().contains(stylesheet)) {
            popup.getScene().getStylesheets().add(stylesheet);
        }
        popup.getScene().getRoot().applyCss();
    }

    private static void localizeDialogButtons(Dialog<?> dialog) {
        for (ButtonType type : dialog.getDialogPane().getButtonTypes()) {
            String text = type == ButtonType.CANCEL ? "Tühista"
                    : type == ButtonType.CLOSE ? "Sulge"
                    : type == ButtonType.YES ? "Jah"
                    : type == ButtonType.NO ? "Ei"
                    : type == ButtonType.APPLY ? "Rakenda"
                    : type == ButtonType.FINISH ? "Valmis"
                    : type == ButtonType.NEXT ? "Edasi"
                    : type == ButtonType.PREVIOUS ? "Tagasi" : null;
            if (text != null && dialog.getDialogPane().lookupButton(type) instanceof Button button) {
                // Keep the original ButtonType identity and its result/cancel semantics.
                button.setText(text);
            }
        }
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
