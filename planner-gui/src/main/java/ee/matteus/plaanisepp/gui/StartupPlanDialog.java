package ee.matteus.plaanisepp.gui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

final class StartupPlanDialog {
    private StartupPlanDialog() {
    }

    static Optional<Choice> show(Window owner, List<Path> recentFiles) {
        Dialog<Choice> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.setTitle("Plaanisepp");
        dialog.setHeaderText("Vali plaan või alusta uut");

        ListView<Path> recentFileList = new ListView<>();
        recentFileList.getItems().setAll(recentFiles);
        recentFileList.setPrefSize(560, 240);
        recentFileList.setPlaceholder(new Label("Hiljutisi plaane pole"));
        recentFileList.setCellFactory(ignored -> new ListCell<>() {
            @Override
            protected void updateItem(Path path, boolean empty) {
                super.updateItem(path, empty);
                if (empty || path == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                Label fileName = new Label(path.getFileName().toString());
                fileName.setStyle("-fx-font-weight: bold;");
                Label directory = new Label(path.getParent() == null ? "" : path.getParent().toString());
                directory.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11;");
                setText(null);
                setGraphic(new VBox(2, fileName, directory));
            }
        });

        VBox content = new VBox(8, new Label("Hiljutised plaanid"), recentFileList);
        content.setPadding(new Insets(4));
        dialog.getDialogPane().setContent(content);

        ButtonType newPlanButton = new ButtonType("Uus plaan", ButtonBar.ButtonData.LEFT);
        ButtonType openOtherButton = new ButtonType("Ava muu fail…", ButtonBar.ButtonData.OTHER);
        ButtonType openRecentButton = new ButtonType("Ava valitud", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(
                newPlanButton,
                openOtherButton,
                ButtonType.CANCEL,
                openRecentButton
        );

        Button openRecent = (Button) dialog.getDialogPane().lookupButton(openRecentButton);
        openRecent.setDisable(true);
        recentFileList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> openRecent.setDisable(newValue == null)
        );
        if (!recentFiles.isEmpty()) {
            recentFileList.getSelectionModel().selectFirst();
        }
        recentFileList.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY
                    && event.getClickCount() == 2
                    && recentFileList.getSelectionModel().getSelectedItem() != null) {
                dialog.setResult(Choice.openRecent(recentFileList.getSelectionModel().getSelectedItem()));
                dialog.close();
            }
        });

        dialog.setResultConverter(button -> {
            if (button == newPlanButton) {
                return Choice.newPlan();
            }
            if (button == openOtherButton) {
                return Choice.openOther();
            }
            if (button == openRecentButton) {
                Path selected = recentFileList.getSelectionModel().getSelectedItem();
                return selected == null ? null : Choice.openRecent(selected);
            }
            return null;
        });
        return dialog.showAndWait();
    }

    enum Action {
        NEW_PLAN,
        OPEN_RECENT,
        OPEN_OTHER
    }

    record Choice(Action action, Path path) {
        static Choice newPlan() {
            return new Choice(Action.NEW_PLAN, null);
        }

        static Choice openRecent(Path path) {
            return new Choice(Action.OPEN_RECENT, path);
        }

        static Choice openOther() {
            return new Choice(Action.OPEN_OTHER, null);
        }
    }
}
