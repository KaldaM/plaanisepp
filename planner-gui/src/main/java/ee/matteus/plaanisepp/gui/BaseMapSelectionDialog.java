package ee.matteus.plaanisepp.gui;

import ee.matteus.plaanisepp.core.map.BaseMapBounds;
import ee.matteus.plaanisepp.core.map.BaseMapDownload;
import ee.matteus.plaanisepp.core.map.MaaAmetWmsClient;
import ee.matteus.plaanisepp.core.map.RegularMapStyle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.application.Platform;
import javafx.animation.PauseTransition;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

final class BaseMapSelectionDialog {
    private static final int PREVIEW_WIDTH = 900;
    private static final int PREVIEW_HEIGHT = 600;
    private static final double PREVIEW_BUFFER_FACTOR = 2.0;
    private static final double INITIAL_CENTER_X = 659_266.421;
    private static final double INITIAL_CENTER_Y = 6_474_323.917;
    private static final double INITIAL_WIDTH_METRES = 1_000;

    private final MaaAmetWmsClient client = new MaaAmetWmsClient();
    private final ImageView preview = new ImageView();
    private final Label areaLabel = new Label();
    private final Label outputLabel = new Label();
    private final TextField pixelsPerMetreField = new TextField("6.45");
    private final CheckBox automaticResolution = new CheckBox("Automaatne (suurim lubatud)");
    private final ComboBox<RegularMapStyle> regularMapStyle = new ComboBox<>();
    private final ToggleButton regularPreviewButton = new ToggleButton("Tavakaart");
    private final ToggleButton orthophotoPreviewButton = new ToggleButton("Ortofoto");
    private final AtomicLong previewRequestVersion = new AtomicLong();
    private final PauseTransition previewLoadDebounce = new PauseTransition(Duration.millis(180));
    private double centerX = INITIAL_CENTER_X;
    private double centerY = INITIAL_CENTER_Y;
    private double widthMetres = INITIAL_WIDTH_METRES;
    private double dragStartX;
    private double dragStartY;
    private double dragCenterX;
    private double dragCenterY;
    private double dragStartTranslateX;
    private double dragStartTranslateY;

    static Optional<BaseMapDownload> show(Window owner) {
        return new BaseMapSelectionDialog().showDialog(owner);
    }

    static Optional<BaseMapDownload> show(Window owner, BaseMapBounds initialBounds) {
        BaseMapSelectionDialog dialog = new BaseMapSelectionDialog();
        if (initialBounds != null) {
            dialog.centerX = (initialBounds.minX() + initialBounds.maxX()) / 2;
            dialog.centerY = (initialBounds.minY() + initialBounds.maxY()) / 2;
            dialog.widthMetres = initialBounds.widthMetres();
        }
        return dialog.showDialog(owner);
    }

    private Optional<BaseMapDownload> showDialog(Window owner) {
        Dialog<BaseMapDownload> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.setTitle("Määra aluskaart päriskaardilt");
        dialog.setHeaderText("Lohista ja suumi kaarti. Raami sees olev ala lisatakse plaani.");
        ButtonType downloadType = new ButtonType("Laadi aluskaardid", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(downloadType, ButtonType.CANCEL);

        preview.setFitWidth(PREVIEW_WIDTH * PREVIEW_BUFFER_FACTOR);
        preview.setFitHeight(PREVIEW_HEIGHT * PREVIEW_BUFFER_FACTOR);
        preview.setPreserveRatio(false);
        preview.setCursor(Cursor.OPEN_HAND);
        Rectangle frame = new Rectangle(PREVIEW_WIDTH - 4, PREVIEW_HEIGHT - 4, Color.TRANSPARENT);
        frame.setStroke(Color.web("#2563eb"));
        frame.setStrokeWidth(3);
        frame.setMouseTransparent(true);
        Label hint = new Label("Valitud ala");
        hint.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-padding: 4 8;");
        StackPane map = new StackPane(preview, frame, hint);
        StackPane.setAlignment(hint, Pos.TOP_LEFT);
        StackPane.setMargin(hint, new Insets(8));
        map.setMinSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
        map.setPrefSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
        map.setMaxSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
        map.setClip(new Rectangle(PREVIEW_WIDTH, PREVIEW_HEIGHT));
        installNavigation(map, dialog);

        pixelsPerMetreField.setPrefColumnCount(7);
        pixelsPerMetreField.textProperty().addListener((observable, oldValue, newValue) -> refreshLabels());
        automaticResolution.setSelected(true);
        automaticResolution.setTooltip(new javafx.scene.control.Tooltip(
                "Valib valitud ala jaoks suurima eraldusvõime, mis mahub 8000 px ja 40 MP piiridesse"
        ));
        automaticResolution.setOnAction(event -> {
            pixelsPerMetreField.setDisable(automaticResolution.isSelected());
            if (automaticResolution.isSelected()) updateOptimalResolution();
            refreshLabels();
        });
        pixelsPerMetreField.setDisable(true);
        regularMapStyle.getItems().addAll(RegularMapStyle.values());
        regularMapStyle.setValue(RegularMapStyle.GRAYSCALE);
        regularMapStyle.setOnAction(event -> loadPreview(dialog));
        ToggleGroup previewType = new ToggleGroup();
        regularPreviewButton.setToggleGroup(previewType);
        orthophotoPreviewButton.setToggleGroup(previewType);
        regularPreviewButton.setSelected(true);
        regularPreviewButton.setOnAction(event -> {
            regularMapStyle.setDisable(false);
            loadPreview(dialog);
        });
        orthophotoPreviewButton.setOnAction(event -> {
            regularMapStyle.setDisable(true);
            loadPreview(dialog);
        });
        GridPane settings = new GridPane();
        settings.setHgap(10);
        settings.setVgap(6);
        settings.addRow(0, new Label("Eelvaade:"), new HBox(0, regularPreviewButton, orthophotoPreviewButton));
        settings.addRow(1, new Label("Tavakaardi stiil:"), regularMapStyle);
        settings.addRow(2, new Label("Eraldusvõime:"), pixelsPerMetreField,
                new Label("pikslit meetri kohta"), automaticResolution);
        settings.addRow(3, new Label("Valitud ala:"), areaLabel);
        settings.addRow(4, new Label("Väljund:"), outputLabel);
        Label attribution = new Label("Kaardiandmed: Maa- ja Ruumiamet. Alla laaditakse nii põhikaart kui ortofoto.");
        attribution.setStyle("-fx-text-fill: #4b5563;");
        BorderPane content = new BorderPane(map);
        content.setBottom(new javafx.scene.layout.VBox(8, settings, attribution));
        BorderPane.setMargin(content.getBottom(), new Insets(12, 0, 0, 0));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefWidth(PREVIEW_WIDTH + 40);
        updateOptimalResolution();
        refreshLabels();
        loadPreview(dialog);

        dialog.setResultConverter(button -> {
            if (button != downloadType) {
                return null;
            }
            try {
                int[] dimensions = outputDimensions();
                dialog.getDialogPane().setCursor(Cursor.WAIT);
                return client.download(bounds(), dimensions[0], dimensions[1], regularMapStyle.getValue());
            } catch (IOException | InterruptedException | IllegalArgumentException exception) {
                if (exception instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
                showError("Aluskaartide allalaadimine ebaõnnestus", exception.getMessage());
                return null;
            } finally {
                dialog.getDialogPane().setCursor(Cursor.DEFAULT);
            }
        });
        return dialog.showAndWait();
    }

    private void installNavigation(StackPane map, Dialog<?> dialog) {
        map.setOnMousePressed(event -> {
            if (event.getButton() != MouseButton.PRIMARY) return;
            previewLoadDebounce.stop();
            previewRequestVersion.incrementAndGet();
            dragStartX = event.getX();
            dragStartY = event.getY();
            dragCenterX = centerX;
            dragCenterY = centerY;
            dragStartTranslateX = preview.getTranslateX();
            dragStartTranslateY = preview.getTranslateY();
            preview.setCursor(Cursor.CLOSED_HAND);
        });
        map.setOnMouseDragged(event -> {
            double deltaX = event.getX() - dragStartX;
            double deltaY = event.getY() - dragStartY;
            double metresPerPixel = widthMetres / PREVIEW_WIDTH;
            centerX = dragCenterX - deltaX * metresPerPixel;
            centerY = dragCenterY + deltaY * metresPerPixel;
            preview.setTranslateX(dragStartTranslateX + deltaX);
            preview.setTranslateY(dragStartTranslateY + deltaY);
            if (automaticResolution.isSelected()) updateOptimalResolution();
            refreshLabels();
        });
        map.setOnMouseReleased(event -> {
            preview.setCursor(Cursor.OPEN_HAND);
            if (event.getButton() == MouseButton.PRIMARY) {
                loadPreview(null);
            }
        });
        map.setOnScroll(event -> {
            double previousWidthMetres = widthMetres;
            double pointerOffsetX = event.getX() - PREVIEW_WIDTH / 2.0;
            double pointerOffsetY = event.getY() - PREVIEW_HEIGHT / 2.0;
            double zoomFactor = event.getDeltaY() > 0 ? 0.75 : 1.3333333333;
            widthMetres *= zoomFactor;
            widthMetres = Math.max(100, Math.min(50_000, widthMetres));
            double widthChange = previousWidthMetres - widthMetres;
            centerX += pointerOffsetX * widthChange / PREVIEW_WIDTH;
            centerY -= pointerOffsetY * widthChange / PREVIEW_WIDTH;
            double visualScale = previousWidthMetres / widthMetres;
            preview.setTranslateX(pointerOffsetX
                    - visualScale * (pointerOffsetX - preview.getTranslateX()));
            preview.setTranslateY(pointerOffsetY
                    - visualScale * (pointerOffsetY - preview.getTranslateY()));
            preview.setScaleX(preview.getScaleX() * visualScale);
            preview.setScaleY(preview.getScaleY() * visualScale);
            if (automaticResolution.isSelected()) updateOptimalResolution();
            refreshLabels();
            schedulePreviewLoad(dialog);
            event.consume();
        });
    }

    private void schedulePreviewLoad(Dialog<?> dialog) {
        previewRequestVersion.incrementAndGet();
        previewLoadDebounce.setOnFinished(event -> loadPreview(dialog));
        previewLoadDebounce.playFromStart();
    }

    private BaseMapBounds bounds() {
        double heightMetres = widthMetres * PREVIEW_HEIGHT / PREVIEW_WIDTH;
        return new BaseMapBounds(
                centerX - widthMetres / 2,
                centerY - heightMetres / 2,
                centerX + widthMetres / 2,
                centerY + heightMetres / 2
        );
    }

    private BaseMapBounds previewBounds() {
        double previewWidthMetres = widthMetres * PREVIEW_BUFFER_FACTOR;
        double previewHeightMetres = previewWidthMetres * PREVIEW_HEIGHT / PREVIEW_WIDTH;
        return new BaseMapBounds(
                centerX - previewWidthMetres / 2,
                centerY - previewHeightMetres / 2,
                centerX + previewWidthMetres / 2,
                centerY + previewHeightMetres / 2
        );
    }

    private int[] outputDimensions() {
        double pixelsPerMetre = Double.parseDouble(pixelsPerMetreField.getText().trim().replace(',', '.'));
        if (!Double.isFinite(pixelsPerMetre) || pixelsPerMetre <= 0) {
            throw new IllegalArgumentException("Eraldusvõime peab olema positiivne arv.");
        }
        BaseMapBounds bounds = bounds();
        int width = Math.max(1, (int) Math.round(bounds.widthMetres() * pixelsPerMetre));
        int height = Math.max(1, (int) Math.round(bounds.heightMetres() * pixelsPerMetre));
        if (width > MaaAmetWmsClient.MAX_DIMENSION || height > MaaAmetWmsClient.MAX_DIMENSION
                || (long) width * height > MaaAmetWmsClient.MAX_PIXELS) {
            throw new IllegalArgumentException("Valitud ala ja eraldusvõime annavad liiga suure pildi. "
                    + "Vähenda ala või pikslite arvu meetri kohta.");
        }
        return new int[]{width, height};
    }

    private void updateOptimalResolution() {
        BaseMapBounds bounds = bounds();
        double byWidth = MaaAmetWmsClient.MAX_DIMENSION / bounds.widthMetres();
        double byHeight = MaaAmetWmsClient.MAX_DIMENSION / bounds.heightMetres();
        double byPixels = Math.sqrt(MaaAmetWmsClient.MAX_PIXELS
                / (bounds.widthMetres() * bounds.heightMetres()));
        double optimal = Math.min(25.0, Math.min(byWidth, Math.min(byHeight, byPixels))) * 0.98;
        optimal = Math.max(0.1, Math.floor(optimal * 100.0) / 100.0);
        pixelsPerMetreField.setText(String.format(java.util.Locale.ROOT, "%.2f", optimal));
    }

    private void refreshLabels() {
        BaseMapBounds bounds = bounds();
        areaLabel.setText(String.format("%.0f × %.0f m", bounds.widthMetres(), bounds.heightMetres()));
        try {
            int[] dimensions = outputDimensions();
            double megapixels = (long) dimensions[0] * dimensions[1] / 1_000_000.0;
            outputLabel.setText(String.format("%d × %d px (%.1f MP), kaks pilti", dimensions[0], dimensions[1], megapixels));
            outputLabel.setStyle("");
        } catch (RuntimeException exception) {
            outputLabel.setText(exception.getMessage());
            outputLabel.setStyle("-fx-text-fill: #b91c1c;");
        }
    }

    private void loadPreview(Dialog<?> dialog) {
        long requestVersion = previewRequestVersion.incrementAndGet();
        BaseMapBounds requestedBounds = previewBounds();
        RegularMapStyle requestedStyle = regularMapStyle.getValue();
        boolean orthophoto = orthophotoPreviewButton.isSelected();
        if (dialog != null) dialog.getDialogPane().setCursor(Cursor.WAIT);
        Thread.startVirtualThread(() -> {
            try {
                byte[] data = orthophoto
                        ? client.downloadOrthophotoPreview(
                                requestedBounds,
                                (int) (PREVIEW_WIDTH * PREVIEW_BUFFER_FACTOR),
                                (int) (PREVIEW_HEIGHT * PREVIEW_BUFFER_FACTOR)
                        )
                        : client.downloadPreview(
                                requestedBounds,
                                (int) (PREVIEW_WIDTH * PREVIEW_BUFFER_FACTOR),
                                (int) (PREVIEW_HEIGHT * PREVIEW_BUFFER_FACTOR),
                                requestedStyle
                        );
                Platform.runLater(() -> {
                    if (previewRequestVersion.get() != requestVersion) return;
                    preview.setImage(new Image(new ByteArrayInputStream(data)));
                    preview.setTranslateX(0);
                    preview.setTranslateY(0);
                    preview.setScaleX(1);
                    preview.setScaleY(1);
                    if (dialog != null) dialog.getDialogPane().setCursor(Cursor.DEFAULT);
                });
            } catch (IOException | InterruptedException exception) {
                if (exception instanceof InterruptedException) Thread.currentThread().interrupt();
                Platform.runLater(() -> {
                    if (previewRequestVersion.get() != requestVersion) return;
                    if (dialog != null) dialog.getDialogPane().setCursor(Cursor.DEFAULT);
                    showError("Kaardi laadimine ebaõnnestus", exception.getMessage());
                });
            }
        });
    }

    private static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message == null ? "Tundmatu viga." : message);
        alert.showAndWait();
    }
}
