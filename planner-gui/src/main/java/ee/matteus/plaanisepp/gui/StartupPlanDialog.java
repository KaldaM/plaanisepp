package ee.matteus.plaanisepp.gui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

final class StartupPlanDialog {
    private StartupPlanDialog() {
    }

    static Optional<Choice> show(
            Window owner,
            List<RecentPlan> recentPlans,
            Consumer<String> festivalSummaryHandler
    ) {
        Dialog<Choice> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.setTitle("Plaanisepp");
        dialog.setHeaderText("Vali plaan või alusta uut");

        TreeItem<RecentPlan> root = new TreeItem<>();
        Map<String, TreeItem<RecentPlan>> festivalGroups = new LinkedHashMap<>();
        for (RecentPlan recentPlan : recentPlans) {
            String groupName = recentPlan.festivalName().isBlank()
                    ? "Festivalita plaanid"
                    : recentPlan.festivalName();
            TreeItem<RecentPlan> group = festivalGroups.computeIfAbsent(groupName, name -> {
                TreeItem<RecentPlan> item = new TreeItem<>(RecentPlan.group(name, recentPlan.festivalName()));
                item.setExpanded(true);
                root.getChildren().add(item);
                return item;
            });
            group.getChildren().add(new TreeItem<>(recentPlan));
        }

        TreeView<RecentPlan> recentFileTree = new TreeView<>(root);
        recentFileTree.setShowRoot(false);
        recentFileTree.setPrefSize(560, 280);
        recentFileTree.setCellFactory(ignored -> new TreeCell<>() {
            @Override
            protected void updateItem(RecentPlan recentPlan, boolean empty) {
                super.updateItem(recentPlan, empty);
                if (empty || recentPlan == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                if (recentPlan.group()) {
                    setText(recentPlan.planName());
                    setStyle("-fx-font-weight: bold;");
                    setGraphic(null);
                    return;
                }
                setStyle("");
                Label planName = new Label(recentPlan.planName());
                planName.setStyle("-fx-font-weight: bold;");
                Path path = recentPlan.path();
                Label directory = new Label(path.getFileName() + " · "
                        + (path.getParent() == null ? "" : path.getParent()));
                directory.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11;");
                setText(null);
                setGraphic(new VBox(2, planName, directory));
            }
        });

        Button festivalSummaryButton = new Button("Festivali kokkuvõte…");
        festivalSummaryButton.setDisable(true);
        festivalSummaryButton.setOnAction(event -> selectedFestival(
                recentFileTree.getSelectionModel().getSelectedItem()
        ).ifPresent(festivalSummaryHandler));

        VBox content = recentPlans.isEmpty()
                ? new VBox(8, new Label("Hiljutised plaanid"), new Label("Hiljutisi plaane pole"))
                : new VBox(
                        8,
                        new Label("Hiljutised plaanid festivalide kaupa"),
                        recentFileTree,
                        festivalSummaryButton
                );
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
        recentFileTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            openRecent.setDisable(selectedPath(newValue).isEmpty());
            festivalSummaryButton.setDisable(selectedFestival(newValue).isEmpty());
        });
        if (!root.getChildren().isEmpty() && !root.getChildren().getFirst().getChildren().isEmpty()) {
            recentFileTree.getSelectionModel().select(root.getChildren().getFirst().getChildren().getFirst());
        }
        recentFileTree.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY
                    && event.getClickCount() == 2) {
                selectedPath(recentFileTree.getSelectionModel().getSelectedItem()).ifPresent(path -> {
                    dialog.setResult(Choice.openRecent(path));
                    dialog.close();
                });
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
                return selectedPath(recentFileTree.getSelectionModel().getSelectedItem())
                        .map(Choice::openRecent)
                        .orElse(null);
            }
            return null;
        });
        return dialog.showAndWait();
    }

    private static Optional<Path> selectedPath(TreeItem<RecentPlan> selectedItem) {
        if (selectedItem == null || selectedItem.getValue() == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(selectedItem.getValue().path());
    }

    private static Optional<String> selectedFestival(TreeItem<RecentPlan> selectedItem) {
        if (selectedItem == null || selectedItem.getValue() == null) {
            return Optional.empty();
        }
        String festivalName = selectedItem.getValue().festivalName();
        return festivalName == null || festivalName.isBlank()
                ? Optional.empty()
                : Optional.of(festivalName);
    }

    record RecentPlan(Path path, String planName, String festivalName, boolean group) {
        RecentPlan(Path path, String planName, String festivalName) {
            this(path, planName, festivalName, false);
        }

        static RecentPlan group(String name, String festivalName) {
            return new RecentPlan(null, name, festivalName, true);
        }
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
