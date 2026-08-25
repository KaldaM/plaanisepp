package ee.matteus.plaanisepp.gui;

import ee.matteus.plaanisepp.core.model.ConnectorType;
import ee.matteus.plaanisepp.core.model.AreaObject;
import ee.matteus.plaanisepp.core.model.CustomObject;
import ee.matteus.plaanisepp.core.model.CustomObjectShape;
import ee.matteus.plaanisepp.core.model.DistributionPanel;
import ee.matteus.plaanisepp.core.model.Equipment;
import ee.matteus.plaanisepp.core.model.EquipmentContainer;
import ee.matteus.plaanisepp.core.model.EquipmentPowerAssignmentResult;
import ee.matteus.plaanisepp.core.model.EventPlan;
import ee.matteus.plaanisepp.core.model.LineObject;
import ee.matteus.plaanisepp.core.model.MarkerObject;
import ee.matteus.plaanisepp.core.model.MarkerType;
import ee.matteus.plaanisepp.core.model.PlannerObject;
import ee.matteus.plaanisepp.core.model.Position;
import ee.matteus.plaanisepp.core.model.PowerConnection;
import ee.matteus.plaanisepp.core.model.PowerConnectionValidationResult;
import ee.matteus.plaanisepp.core.model.PowerConnectable;
import ee.matteus.plaanisepp.core.model.PowerConsumer;
import ee.matteus.plaanisepp.core.model.PowerOutlet;
import ee.matteus.plaanisepp.core.model.PowerSource;
import ee.matteus.plaanisepp.core.model.TextObject;
import ee.matteus.plaanisepp.core.model.Tent;
import ee.matteus.plaanisepp.core.service.PlanFactory;
import ee.matteus.plaanisepp.core.service.PlanSnapshot;
import ee.matteus.plaanisepp.core.service.PlanSnapshotService;
import ee.matteus.plaanisepp.core.service.GeometryCalculator;
import ee.matteus.plaanisepp.core.service.PowerSummary;
import ee.matteus.plaanisepp.core.service.PowerSummaryService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.TreeMap;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaaniseppApp extends Application {
    private static final String DEFAULT_MAP_PATH = "classpath:/maps/tavakaart.png";
    private static final String ORTHOPHOTO_MAP_PATH = "classpath:/maps/ortofoto.png";
    private static final String APPLICATION_ICON_PATH = "/icons/plaanisepp.png";
    private static final String SELECTED_OBJECT_SECTION = "selectedObject";
    private static final String OBJECT_LIST_SECTION = "objectList";
    private static final String MAP_LAYERS_SECTION = "mapLayers";
    private static final String SUMMARY_SECTION = "summary";
    private static final String EQUIPMENT_SECTION = "equipment";
    private static final String OUTLET_SECTION = "outlet";
    private static final String SIDEBAR_SECTION_ORDER_PREFERENCE = "sidebarSectionOrder";
    private static final String PLACEMENT_SHOW_MAP_LABEL_PREFERENCE = "placementShowMapLabel";
    private static final String MAP_LAYOUT_LOCKED_PREFERENCE = "mapLayoutLocked";
    private static final List<String> DEFAULT_SIDEBAR_SECTION_ORDER = List.of(
            OBJECT_LIST_SECTION,
            SELECTED_OBJECT_SECTION,
            MAP_LAYERS_SECTION,
            SUMMARY_SECTION
    );
    private static final Pattern CABLE_LENGTH_PATTERN = Pattern.compile("\\d+(?:[,.]\\d+)?");
    private static final Comparator<CableSummaryRow> CABLE_SUMMARY_ROW_COMPARATOR = Comparator
            .comparing((CableSummaryRow row) -> row.connection().connectorType())
            .thenComparing(row -> row.consumer().name(), String.CASE_INSENSITIVE_ORDER)
            .thenComparing(row -> row.source().name(), String.CASE_INSENSITIVE_ORDER);
    private static final double MIN_MAP_WIDTH = 760.0;
    private static final double MIN_MAP_HEIGHT = 560.0;
    private static final double MAP_CLICK_DRAG_TOLERANCE_PX = 6.0;
    private static final double MIN_OBJECT_LIST_HEIGHT = 90.0;
    private static final double MAX_OBJECT_LIST_HEIGHT = 800.0;
    private static final double DEFAULT_OBJECT_LIST_HEIGHT = 180.0;
    private static final double MIN_FONT_SIZE_PIXELS = 6.0;
    private static final double MAX_FONT_SIZE_PIXELS = 120.0;
    private static final String OBJECT_LIST_HEIGHT_PREFERENCE = "objectListHeight";
    private static final long DOUBLE_SHIFT_INTERVAL_NANOS = 500_000_000L;
    private static final int MAX_PLAN_HISTORY_STEPS = 50;

    private final PlanFactory planFactory = new PlanFactory();
    private final PowerSummaryService powerSummaryService = new PowerSummaryService();
    private final ReportTextExporter reportTextExporter = new ReportTextExporter(powerSummaryService);
    private final PlanFileSession planFileSession = new PlanFileSession();
    private final PlanDocumentState planDocumentState = new PlanDocumentState();
    private final PlanSnapshotService planSnapshotService = new PlanSnapshotService();
    private final PlanHistory<PlanSnapshot> planHistory = new PlanHistory<>(MAX_PLAN_HISTORY_STEPS);
    private final Preferences preferences = ApplicationPreferences.open();
    private final RecentPlanFiles recentPlanFiles = new RecentPlanFiles(preferences);

    private EventPlan plan;
    private PlanSnapshot savedPlanSnapshot;
    private Pane mapPane;
    private Pane mapContentPane;
    private ScrollPane mapScrollPane;
    private Scale mapScale;
    private ImageView mapImageView;
    private double zoomLevel = 1.0;
    private Slider zoomSlider;
    private Button zoomPercentButton;
    private double mapWidth = MIN_MAP_WIDTH;
    private double mapHeight = MIN_MAP_HEIGHT;
    private double objectListHeight;
    private boolean measuringActive;
    private boolean addingCablePoint;
    private String editingCableConnectionId;
    private boolean mapDraggedSincePress;
    private boolean planDragInProgress;
    private boolean planDragRecorded;
    private boolean synchronizingSidebarSelection;
    private boolean startupPlanFileProvided;
    private boolean quickObjectSearchActive;
    private boolean shiftKeyPressed;
    private long lastShiftPressNanos;
    private boolean objectSearchPreviousExpanded;
    private String objectSearchPreviousText = "";
    private Timeline objectSearchHighlightTimeline;
    private Node objectSearchHighlight;
    private ContextMenu activeContextMenu;
    private double mapPressSceneX;
    private double mapPressSceneY;
    private Position measurementStart;
    private final List<Node> measurementNodes = new ArrayList<>();
    private final List<Node> powerConnectionAnchorMarkers = new ArrayList<>();
    private final List<MeasurementView> measurements = new ArrayList<>();
    private final List<Position> pendingShapePoints = new ArrayList<>();
    private final Set<String> visibleGroups = new HashSet<>();
    private final Set<String> collapsedPowerSummaryKeys = new HashSet<>();
    private final Set<String> collapsedObjectGroups = new HashSet<>();
    private final Map<String, Boolean> sidebarSectionStates = new HashMap<>();
    private final Map<String, TitledPane> sidebarSections = new HashMap<>();
    private final Map<String, Node> mapObjectNodes = new HashMap<>();
    private Set<String> knownGroups = new HashSet<>();
    private ListView<SummaryListItem> summaryList;
    private VBox sidebar;
    private CheckBox showPowerSummaryCheckBox;
    private CheckBox showCableSummaryCheckBox;
    private CheckBox showGroupSummaryCheckBox;
    private Label mapToolStatusLabel;
    private Label planTitleLabel;
    private Label saveStatusLabel;
    private TextField objectSearchField;
    private ListView<ObjectListEntry> objectList;
    private Button revealObjectButton;
    private TitledPane objectListSection;
    private TitledPane selectedObjectSection;
    private TitledPane powerSummarySection;
    private TitledPane equipmentSection;
    private TitledPane outletSection;
    private TextField planNameField;
    private TextField pixelsPerMeterField;
    private Label selectedTypeLabel;
    private TextField nameField;
    private ComboBox<String> groupField;
    private CheckBox lockedCheckBox;
    private CheckBox showMapLabelCheckBox;
    private Button resetMapLabelButton;
    private TextField tentWidthField;
    private TextField tentHeightField;
    private TextField tentRotationField;
    private ColorPicker tentColorPicker;
    private Slider tentOpacitySlider;
    private ComboBox<CustomObjectShape> customObjectShapeComboBox;
    private ColorPicker customObjectColorPicker;
    private Slider customObjectOpacitySlider;
    private ColorPicker textObjectColorPicker;
    private Slider textObjectFontSizeSlider;
    private ComboBox<MarkerType> markerTypeComboBox;
    private ColorPicker markerColorPicker;
    private ColorPicker areaColorPicker;
    private Slider areaOpacitySlider;
    private Label areaSizeLabel;
    private Label areaPerimeterLabel;
    private ColorPicker lineColorPicker;
    private Slider lineWidthSlider;
    private Label lineLengthLabel;
    private Label customObjectWidthLabel;
    private Label customObjectHeightLabel;
    private TextField customObjectWidthField;
    private TextField customObjectHeightField;
    private Label customObjectRotationLabel;
    private TextField customObjectRotationField;
    private Label customObjectAreaLabel;
    private Label customObjectPerimeterLabel;
    private ComboBox<PowerConnectionChoice> powerConnectionComboBox;
    private ComboBox<PowerSourceChoice> powerSourceComboBox;
    private ComboBox<OutletChoice> connectionOutletComboBox;
    private TextField cableLengthNotesField;
    private TextField cableNotesField;
    private CheckBox showSelectedCableLabelCheckBox;
    private Button resetCableLabelButton;
    private Button removePowerConnectionButton;
    private Button makeDefaultPowerConnectionButton;
    private TextArea notesArea;
    private ListView<String> equipmentList;
    private Button addEquipmentButton;
    private Button addAlternativePowerConnectionButton;
    private ListView<OutletChoice> outletList;
    private TextField outletNameField;
    private ComboBox<ConnectorType> outletTypeComboBox;
    private TextField outletCapacityWattsField;
    private Button addOutletButton;
    private Button updateOutletButton;
    private Button removeOutletButton;
    private VBox customObjectPanel;
    private VBox textObjectPanel;
    private VBox markerPanel;
    private VBox areaPanel;
    private VBox linePanel;
    private VBox tentPanel;
    private VBox powerConnectionPanel;
    private VBox equipmentPanel;
    private VBox outletPanel;
    private Button deleteObjectButton;
    private Button choosePowerSourceButton;
    private ToggleButton mapLayoutLockButton;
    private ToggleButton measureButton;
    private ToggleButton addCablePointButton;
    private Button clearCableRouteButton;
    private ToggleButton showCablesButton;
    private ToggleButton showCableLabelsButton;
    private ToggleButton show230VCablesButton;
    private ToggleButton show16ACablesButton;
    private ToggleButton show32ACablesButton;
    private ToggleButton show63ACablesButton;
    private ToggleButton showObjectLabelsButton;
    private ToggleButton showTentsButton;
    private ToggleButton showPowerSourcesButton;
    private ToggleButton showCustomObjectsButton;
    private ToggleButton showTextObjectsButton;
    private ToggleButton showMarkerObjectsButton;
    private ToggleButton showAreaObjectsButton;
    private ToggleButton showLineObjectsButton;
    private ComboBox<PlacementType> placementTypeComboBox;
    private Button addPlacementButton;
    private PlannerObject selectedObject;
    private PlannerObject pendingPowerSourceConsumer;
    private String pendingPlacementName;
    private String pendingPlacementGroupName;
    private String pendingPlacementColorHex;
    private Double pendingPlacementWidthMeters;
    private Double pendingPlacementHeightMeters;
    private Double pendingPlacementOpacity;
    private Double pendingPlacementLineWidthPixels;
    private Double pendingPlacementFontSizePixels;
    private Boolean pendingPlacementShowMapLabel;
    private CustomObjectShape pendingPlacementShape;
    private boolean pendingTentPlacement;
    private boolean pendingPowerSourcePlacement;
    private PlacementType pendingPowerSourcePlacementType;
    private boolean pendingCustomObjectPlacement;
    private boolean pendingTextObjectPlacement;
    private boolean pendingMarkerPlacement;
    private boolean pendingLineObjectPlacement;
    private boolean pendingAreaObjectPlacement;
    private MarkerType pendingPlacementMarkerType;
    private PlannerObject copiedObject;
    private int keyboardPasteCount;
    private boolean updatingOpacityControls;
    private boolean opacityDragChanged;
    private boolean updatingDetailControls;
    private boolean detailSliderDragChanged;
    private boolean mapLayoutLocked;
    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        String startupPlanError = initializePlan();
        objectListHeight = loadObjectListHeightPreference();

        BorderPane root = new BorderPane();
        root.setTop(createToolbar());
        root.setCenter(createContent());

        refreshGroupFilters();
        redrawMap();
        refreshSummary();
        refreshDetails();

        Scene scene = new Scene(root, 1200, 760);
        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN),
                this::newPlan
        );
        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN),
                this::openPlan
        );
        scene.getAccelerators().put(
                new KeyCodeCombination(
                        KeyCode.P,
                        KeyCombination.CONTROL_DOWN,
                        KeyCombination.SHIFT_DOWN
                ),
                this::showPlanSettingsDialog
        );
        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN),
                this::savePlan
        );
        scene.getAccelerators().put(
                new KeyCodeCombination(
                        KeyCode.S,
                        KeyCombination.CONTROL_DOWN,
                        KeyCombination.SHIFT_DOWN
                ),
                this::savePlanAs
        );
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            handlePlanHistoryShortcut(event, scene);
            if (event.isConsumed()) {
                return;
            }
            if (quickObjectSearchActive) {
                handleQuickObjectSearchKey(event);
                if (event.isConsumed()) {
                    return;
                }
            }
            handlePlacementShortcut(event, scene);
            if (event.isConsumed()) {
                return;
            }
            handleSelectedObjectShortcut(event, scene);
            if (event.isConsumed()) {
                return;
            }
            handleDoubleShift(event, scene);
        });
        scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.SHIFT) {
                shiftKeyPressed = false;
            }
        });
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (quickObjectSearchActive
                    && event.getTarget() instanceof Node target
                    && !isInsideObjectListSection(target)) {
                deactivateQuickObjectSearch();
            }
        });
        scene.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            planDragInProgress = false;
            planDragRecorded = false;
        });
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && isShapePlacementPending()) {
                finishPendingShapePlacement();
                event.consume();
            } else if (event.getCode() == KeyCode.ESCAPE && isPlacementPending()) {
                cancelPlacement();
                event.consume();
            } else if (event.getCode() == KeyCode.ESCAPE && addingCablePoint) {
                finishEditingCableRoute();
                event.consume();
            }
        });
        stage.setScene(scene);
        applyApplicationIcon(stage);
        stage.setMaximized(true);
        stage.setOnCloseRequest(event -> {
            if (!confirmDiscardUnsavedChanges()) {
                event.consume();
            }
        });
        markClean();
        stage.show();
        if (startupPlanError != null) {
            showError("Plaanifaili avamine ebaõnnestus", startupPlanError);
        } else if (!startupPlanFileProvided) {
            Platform.runLater(this::showStartupPlanDialog);
        }
    }

    private void handleDoubleShift(KeyEvent event, Scene scene) {
        if (event.getCode() != KeyCode.SHIFT) {
            lastShiftPressNanos = 0;
            return;
        }
        if (shiftKeyPressed) {
            return;
        }
        shiftKeyPressed = true;
        if (scene.getFocusOwner() instanceof TextInputControl) {
            lastShiftPressNanos = 0;
            return;
        }
        if (isPlacementPending() || addingCablePoint || measuringActive) {
            lastShiftPressNanos = 0;
            return;
        }
        long now = System.nanoTime();
        if (!quickObjectSearchActive
                && lastShiftPressNanos > 0
                && now - lastShiftPressNanos <= DOUBLE_SHIFT_INTERVAL_NANOS) {
            lastShiftPressNanos = 0;
            openQuickObjectSearch();
            event.consume();
            return;
        }
        lastShiftPressNanos = now;
    }

    private void handlePlanHistoryShortcut(KeyEvent event, Scene scene) {
        if (event.getCode() != KeyCode.Z
                || !event.isControlDown()
                || event.isShiftDown()
                || scene.getFocusOwner() instanceof TextInputControl) {
            return;
        }
        if (event.isAltDown()) {
            redoPlanChange();
        } else {
            undoPlanChange();
        }
        event.consume();
    }

    private void handleSelectedObjectShortcut(KeyEvent event, Scene scene) {
        if (scene.getFocusOwner() instanceof TextInputControl
                || isPlacementPending()
                || addingCablePoint
                || measuringActive) {
            return;
        }
        if (event.getCode() == KeyCode.V
                && event.isControlDown()
                && !event.isAltDown()
                && !event.isShiftDown()
                && copiedObject != null) {
            pasteCopiedObjectWithOffset();
            event.consume();
            return;
        }
        if (selectedObject == null) {
            return;
        }
        if (event.getCode() == KeyCode.L
                && event.isControlDown()
                && !event.isAltDown()
                && !event.isShiftDown()) {
            lockedCheckBox.setSelected(!selectedObject.locked());
            updateSelectedLock();
            event.consume();
        } else if (event.getCode() == KeyCode.C
                && event.isControlDown()
                && !event.isAltDown()
                && !event.isShiftDown()) {
            copySelectedObject();
            event.consume();
        } else if (event.getCode() == KeyCode.H
                && event.isControlDown()
                && !event.isAltDown()
                && !event.isShiftDown()) {
            setObjectHidden(selectedObject, !selectedObject.hidden());
            event.consume();
        } else if (event.getCode() == KeyCode.DELETE
                && !event.isControlDown()
                && !event.isAltDown()
                && !event.isShiftDown()) {
            deleteSelectedObject();
            event.consume();
        }
    }

    private void handlePlacementShortcut(KeyEvent event, Scene scene) {
        if (!event.isControlDown()
                || !event.isShiftDown()
                || event.isAltDown()
                || scene.getFocusOwner() instanceof TextInputControl
                || mapLayoutLocked
                || isPlacementPending()
                || addingCablePoint
                || measuringActive) {
            return;
        }
        PlacementType placementType = placementTypeForShortcut(event.getCode());
        if (placementType == null) {
            return;
        }
        startPlacement(placementType);
        event.consume();
    }

    private PlacementType placementTypeForShortcut(KeyCode keyCode) {
        return switch (keyCode) {
            case DIGIT1, NUMPAD1 -> PlacementType.TENT;
            case DIGIT2, NUMPAD2 -> PlacementType.POWER_SOURCE;
            case DIGIT3, NUMPAD3 -> PlacementType.DISTRIBUTION_PANEL;
            case DIGIT4, NUMPAD4 -> PlacementType.CUSTOM_OBJECT;
            case DIGIT5, NUMPAD5 -> PlacementType.TEXT_OBJECT;
            case DIGIT6, NUMPAD6 -> PlacementType.MARKER_OBJECT;
            case DIGIT7, NUMPAD7 -> PlacementType.LINE_OBJECT;
            case DIGIT8, NUMPAD8 -> PlacementType.AREA_OBJECT;
            default -> null;
        };
    }

    private void undoPlanChange() {
        planHistory.undo().ifPresent(this::restorePlanSnapshot);
    }

    private void redoPlanChange() {
        planHistory.redo().ifPresent(this::restorePlanSnapshot);
    }

    private void restorePlanSnapshot(PlanSnapshot snapshot) {
        planDragInProgress = false;
        planDragRecorded = false;
        String selectedObjectId = selectedObject == null ? null : selectedObject.id();
        String pendingConsumerId = pendingPowerSourceConsumer == null ? null : pendingPowerSourceConsumer.id();
        plan = planSnapshotService.restore(snapshot);
        selectedObject = selectedObjectId == null
                ? null
                : plan.findObject(selectedObjectId).orElse(null);
        pendingPowerSourceConsumer = pendingConsumerId == null
                ? null
                : plan.findObject(pendingConsumerId).orElse(null);
        if (editingCableConnectionId != null && plan.powerConnections().stream()
                .noneMatch(connection -> connection.id().equals(editingCableConnectionId))) {
            editingCableConnectionId = null;
            addingCablePoint = false;
            if (addCablePointButton != null) {
                addCablePointButton.setSelected(false);
            }
        }
        knownGroups.clear();
        visibleGroups.clear();
        if (planNameField != null) {
            planNameField.setText(plan.name());
        }
        if (pixelsPerMeterField != null) {
            pixelsPerMeterField.setText(formatMeters(plan.pixelsPerMeter()));
        }
        applyMapLayerControlsFromPlan();
        refreshPlacementButtons();
        updateMapToolStatus();
        refreshGroupFilters();
        refreshObjectList();
        redrawMap();
        refreshSummary();
        refreshDetails();
        if (snapshot.equals(savedPlanSnapshot)) {
            planDocumentState.markClean();
        } else {
            planDocumentState.markDirty();
        }
        updateWindowTitle();
    }

    private void openQuickObjectSearch() {
        if (objectSearchField == null || objectListSection == null) {
            return;
        }
        quickObjectSearchActive = true;
        objectSearchPreviousExpanded = objectListSection.isExpanded();
        objectSearchPreviousText = objectSearchField.getText();
        boolean animated = objectListSection.isAnimated();
        objectListSection.setAnimated(false);
        objectListSection.setExpanded(true);
        objectSearchField.clear();
        selectFirstQuickSearchResult();
        Platform.runLater(() -> {
            objectListSection.setAnimated(animated);
            objectSearchField.requestFocus();
            objectSearchField.selectAll();
        });
    }

    private void finishQuickObjectSearch() {
        if (!quickObjectSearchActive) {
            return;
        }
        quickObjectSearchActive = false;
        objectSearchField.setText(objectSearchPreviousText);
        objectListSection.setExpanded(objectSearchPreviousExpanded);
        mapPane.requestFocus();
    }

    private void deactivateQuickObjectSearch() {
        quickObjectSearchActive = false;
        lastShiftPressNanos = 0;
        mapPane.requestFocus();
    }

    private boolean isInsideObjectListSection(Node node) {
        Node current = node;
        while (current != null) {
            if (current == objectListSection) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }

    private String initializePlan() {
        plan = planFactory.createEmptyPlan();
        Optional<Path> startupPlanFile = StartupPlanFileResolver.resolve(getParameters().getRaw());
        startupPlanFileProvided = startupPlanFile.isPresent();
        if (startupPlanFile.isEmpty()) {
            return null;
        }

        Path file = startupPlanFile.orElseThrow();
        try {
            plan = planFileSession.load(file.toFile());
            recentPlanFiles.remember(file.toFile());
            return null;
        } catch (IOException | RuntimeException exception) {
            String message = exception.getMessage();
            return message == null || message.isBlank()
                    ? "Valitud plaanifaili ei saanud avada: " + file
                    : message;
        }
    }

    private void applyApplicationIcon(Stage targetStage) {
        try (InputStream input = PlaaniseppApp.class.getResourceAsStream(APPLICATION_ICON_PATH)) {
            if (input == null) {
                return;
            }
            Image icon = new Image(input);
            if (!icon.isError()) {
                targetStage.getIcons().add(icon);
            }
        } catch (IOException ignored) {
            // Ikooni puudumine ei tohi takistada rakenduse käivitumist.
        }
    }

    private ToolBar createToolbar() {
        Button newPlanButton = new Button("Uus plaan");
        newPlanButton.setOnAction(event -> newPlan());

        placementTypeComboBox = new ComboBox<>();
        placementTypeComboBox.getItems().addAll(PlacementType.values());
        placementTypeComboBox.getSelectionModel().select(PlacementType.TENT);
        placementTypeComboBox.setPrefWidth(120);

        addPlacementButton = new Button("Lisa");
        addPlacementButton.setTooltip(new Tooltip(
                "Vali tüüp ja vajuta kaardile, kuhu objekt lisada (kiirvalikud Ctrl+Shift+1…8)"
        ));
        addPlacementButton.setOnAction(event -> toggleSelectedPlacement());

        Button saveButton = new Button("Salvesta");
        saveButton.setOnAction(event -> savePlan());

        Button saveAsButton = new Button("Salvesta kui");
        saveAsButton.setOnAction(event -> savePlanAs());

        Button exportSummaryButton = new Button("Ekspordi TXT");
        exportSummaryButton.setTooltip(new Tooltip("Ekspordib tekstiraporti .txt failina"));
        exportSummaryButton.setOnAction(event -> exportSummary());

        Button exportMapImageButton = new Button("Ekspordi pilt");
        exportMapImageButton.setTooltip(new Tooltip("Ekspordib kaardi .png pildina"));
        exportMapImageButton.setOnAction(event -> exportMapImage());

        Button exportPdfButton = new Button("Ekspordi PDF");
        exportPdfButton.setTooltip(new Tooltip("Ekspordib kaardi ja raporti .pdf failina"));
        exportPdfButton.setOnAction(event -> exportPdf());

        Button openButton = new Button("Ava");
        openButton.setOnAction(event -> openPlan());

        Button planSettingsButton = new Button("Plaani andmed");
        planSettingsButton.setOnAction(event -> showPlanSettingsDialog());

        zoomSlider = new Slider(25, 400, zoomLevel * 100);
        zoomSlider.setPrefWidth(140);
        zoomSlider.setBlockIncrement(5);
        zoomSlider.setTooltip(new Tooltip("Kaardi suum 25–400%"));
        zoomSlider.valueProperty().addListener((observable, oldValue, newValue) ->
                setZoom(newValue.doubleValue() / 100.0)
        );

        zoomPercentButton = new Button(zoomPercentText());
        zoomPercentButton.setTooltip(new Tooltip("Taasta 100% suum"));
        zoomPercentButton.setOnAction(event -> setZoom(1.0));

        showCablesButton = new ToggleButton("Kaablid");
        showCablesButton.setSelected(true);
        showCablesButton.setTooltip(new Tooltip("Näitab või peidab kaardil voolukaablid"));
        showCablesButton.setOnAction(event -> updateMapLayerVisibility());

        showCableLabelsButton = new ToggleButton("Sildid");
        showCableLabelsButton.setSelected(true);
        showCableLabelsButton.setTooltip(new Tooltip("Näitab või peidab kaablite tekstisildid"));
        showCableLabelsButton.setOnAction(event -> updateMapLayerVisibility());

        show230VCablesButton = cableTypeToggle("230V", ConnectorType.SCHUKO_230V);
        show16ACablesButton = cableTypeToggle("16A", ConnectorType.INDUSTRIAL_16A);
        show32ACablesButton = cableTypeToggle("32A", ConnectorType.INDUSTRIAL_32A);
        show63ACablesButton = cableTypeToggle("63A", ConnectorType.INDUSTRIAL_63A);
        showObjectLabelsButton = objectTypeToggle("Nimed", "Naitab voi peidab kaardil objektide nimesildid");
        showTentsButton = objectTypeToggle("Telgid", "Näitab või peidab kaardil telgid");
        showPowerSourcesButton = objectTypeToggle("Kapid", "Näitab või peidab kaardil elektrikapid");
        showCustomObjectsButton = objectTypeToggle("Objektid", "Näitab või peidab kaardil tavalised objektid");
        showTextObjectsButton = objectTypeToggle("Tekstid", "Näitab või peidab kaardil tekstobjektid");
        showMarkerObjectsButton = objectTypeToggle("Markerid", "Näitab või peidab kaardil markerid");
        showAreaObjectsButton = objectTypeToggle("Alad", "Näitab või peidab kaardil alaobjektid");
        showLineObjectsButton = objectTypeToggle("Jooned", "Näitab või peidab kaardil jooneobjektid");
        applyMapLayerControlsFromPlan();

        measureButton = new ToggleButton("Mõõdulint");
        measureButton.setTooltip(new Tooltip("Mõõda kaardil vahemaid"));
        measureButton.setOnAction(event -> setMeasuringActive(measureButton.isSelected()));

        Button clearMeasurementsButton = new Button("Puhasta mõõdulint");
        clearMeasurementsButton.setTooltip(new Tooltip("Eemaldab mõõdulindi jooned kaardilt"));
        clearMeasurementsButton.setOnAction(event -> clearMeasurements());

        addCablePointButton = new ToggleButton("Kaabli punkt");
        addCablePointButton.setTooltip(new Tooltip("Lisa valitud objekti voolukaablile vahepunkt"));
        addCablePointButton.setOnAction(event -> {
            if (addCablePointButton.isSelected()) {
                editingCableConnectionId = null;
            }
            setAddingCablePoint(addCablePointButton.isSelected());
        });

        clearCableRouteButton = new Button("Puhasta trajektoor");
        clearCableRouteButton.setTooltip(new Tooltip("Eemaldab valitud objekti voolukaabli vahepunktid"));
        clearCableRouteButton.setOnAction(event -> clearSelectedCableRoute());

        mapLayoutLocked = preferences.getBoolean(MAP_LAYOUT_LOCKED_PREFERENCE, false);
        mapLayoutLockButton = new ToggleButton("Paigutus lukus");
        mapLayoutLockButton.setSelected(mapLayoutLocked);
        mapLayoutLockButton.setTooltip(new Tooltip(
                "Takistab objektide, siltide, kaablite ja kujupunktide kogemata liigutamist"
        ));
        mapLayoutLockButton.setOnAction(event -> setMapLayoutLocked(mapLayoutLockButton.isSelected()));

        mapToolStatusLabel = new Label();
        mapToolStatusLabel.setStyle("-fx-text-fill: #374151;");
        updateMapToolStatus();

        saveStatusLabel = new Label("Salvestatud");
        saveStatusLabel.setStyle("-fx-text-fill: #166534; -fx-font-weight: bold;");
        planTitleLabel = new Label();
        planTitleLabel.setStyle("-fx-font-weight: bold;");
        updatePlanTitleLabel();

        return new ToolBar(
                newPlanButton,
                saveButton,
                saveAsButton,
                openButton,
                exportSummaryButton,
                exportMapImageButton,
                exportPdfButton,
                planSettingsButton,
                new Separator(),
                new Label("Lisa"),
                placementTypeComboBox,
                addPlacementButton,
                new Separator(),
                zoomSlider,
                zoomPercentButton,
                mapLayoutLockButton,
                measureButton,
                clearMeasurementsButton,
                addCablePointButton,
                clearCableRouteButton,
                new Separator(),
                mapToolStatusLabel,
                new Separator(),
                planTitleLabel,
                saveStatusLabel
        );
    }

    private ToggleButton cableTypeToggle(String text, ConnectorType connectorType) {
        ToggleButton button = new ToggleButton(text);
        button.setSelected(true);
        button.setTooltip(new Tooltip("Näitab või peidab kaardil %s kaablid".formatted(CableDisplayHelper.shortTypeName(connectorType))));
        button.setOnAction(event -> updateMapLayerVisibility());
        return button;
    }

    private void setMapLayoutLocked(boolean locked) {
        mapLayoutLocked = locked;
        preferences.putBoolean(MAP_LAYOUT_LOCKED_PREFERENCE, locked);
        if (locked) {
            cancelPlacement();
            finishEditingCableRoute();
        }
        refreshPlacementButtons();
        refreshDetails();
        redrawMap();
    }

    private void showMapLayoutLockedMessage() {
        showError(
                "Paigutus on lukus",
                "Lülita tööriistaribal „Paigutus lukus” välja, et kaardi geomeetriat muuta."
        );
    }

    private ToggleButton objectTypeToggle(String text, String tooltip) {
        ToggleButton button = new ToggleButton(text);
        button.setSelected(true);
        button.setTooltip(new Tooltip(tooltip));
        button.setOnAction(event -> updateMapLayerVisibility());
        return button;
    }

    private void applyMapLayerControlsFromPlan() {
        if (plan == null || showCablesButton == null) {
            return;
        }
        showCablesButton.setSelected(plan.showCables());
        showCableLabelsButton.setSelected(plan.showCableLabels());
        show230VCablesButton.setSelected(plan.showCableType(ConnectorType.SCHUKO_230V));
        show16ACablesButton.setSelected(plan.showCableType(ConnectorType.INDUSTRIAL_16A));
        show32ACablesButton.setSelected(plan.showCableType(ConnectorType.INDUSTRIAL_32A));
        show63ACablesButton.setSelected(plan.showCableType(ConnectorType.INDUSTRIAL_63A));
        showObjectLabelsButton.setSelected(plan.showObjectLabels());
        showTentsButton.setSelected(plan.showTents());
        showPowerSourcesButton.setSelected(plan.showPowerSources());
        showCustomObjectsButton.setSelected(plan.showCustomObjects());
        showTextObjectsButton.setSelected(plan.showTextObjects());
        showMarkerObjectsButton.setSelected(plan.showMarkerObjects());
        showAreaObjectsButton.setSelected(plan.showAreaObjects());
        showLineObjectsButton.setSelected(plan.showLineObjects());
    }

    private void updateMapLayerVisibility() {
        if (plan == null || showCablesButton == null) {
            return;
        }
        plan.setShowCables(showCablesButton.isSelected());
        plan.setShowCableLabels(showCableLabelsButton.isSelected());
        plan.setShowCableType(ConnectorType.SCHUKO_230V, show230VCablesButton.isSelected());
        plan.setShowCableType(ConnectorType.INDUSTRIAL_16A, show16ACablesButton.isSelected());
        plan.setShowCableType(ConnectorType.INDUSTRIAL_32A, show32ACablesButton.isSelected());
        plan.setShowCableType(ConnectorType.INDUSTRIAL_63A, show63ACablesButton.isSelected());
        plan.setShowObjectLabels(showObjectLabelsButton.isSelected());
        plan.setShowTents(showTentsButton.isSelected());
        plan.setShowPowerSources(showPowerSourcesButton.isSelected());
        plan.setShowCustomObjects(showCustomObjectsButton.isSelected());
        plan.setShowTextObjects(showTextObjectsButton.isSelected());
        plan.setShowMarkerObjects(showMarkerObjectsButton.isSelected());
        plan.setShowAreaObjects(showAreaObjectsButton.isSelected());
        plan.setShowLineObjects(showLineObjectsButton.isSelected());
        redrawMap();
        refreshObjectList();
        markDirty();
    }

    private void updateMapDragState(double sceneX, double sceneY) {
        if (mapDraggedSincePress) {
            return;
        }
        double deltaX = sceneX - mapPressSceneX;
        double deltaY = sceneY - mapPressSceneY;
        mapDraggedSincePress = deltaX * deltaX + deltaY * deltaY
                > MAP_CLICK_DRAG_TOLERANCE_PX * MAP_CLICK_DRAG_TOLERANCE_PX;
    }

    private SplitPane createContent() {
        mapPane = new Pane();
        mapPane.setFocusTraversable(true);
        mapPane.setMinSize(MIN_MAP_WIDTH, MIN_MAP_HEIGHT);
        mapPane.setPrefSize(MIN_MAP_WIDTH, MIN_MAP_HEIGHT);
        mapPane.setStyle("-fx-background-color: #eef1ec;");
        mapScale = new Scale(1.0, 1.0, 0.0, 0.0);
        mapPane.getTransforms().add(mapScale);
        mapImageView = new ImageView();
        mapImageView.setPreserveRatio(true);
        mapPane.setOnMousePressed(event -> {
            mapDraggedSincePress = false;
            mapPressSceneX = event.getSceneX();
            mapPressSceneY = event.getSceneY();
        });
        mapPane.setOnMouseDragged(event -> updateMapDragState(event.getSceneX(), event.getSceneY()));
        mapPane.setOnContextMenuRequested(event -> {
            if (event.getTarget() != mapPane && event.getTarget() != mapImageView) {
                return;
            }
            Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
            showMapContextMenu(
                    new Position(mapPoint.getX(), mapPoint.getY()),
                    event.getScreenX(),
                    event.getScreenY()
            );
            event.consume();
        });
        mapPane.setOnMouseClicked(event -> {
            if (pendingTentPlacement && !mapDraggedSincePress) {
                placeTent(new Position(event.getX(), event.getY()));
                return;
            }
            if (pendingPowerSourcePlacement && !mapDraggedSincePress) {
                placePowerSource(new Position(event.getX(), event.getY()));
                return;
            }
            if (pendingCustomObjectPlacement && !mapDraggedSincePress) {
                placeCustomObject(new Position(event.getX(), event.getY()));
                return;
            }
            if (pendingTextObjectPlacement && !mapDraggedSincePress) {
                placeTextObject(new Position(event.getX(), event.getY()));
                return;
            }
            if (pendingMarkerPlacement && !mapDraggedSincePress) {
                placeMarkerObject(new Position(event.getX(), event.getY()));
                return;
            }
            if (pendingLineObjectPlacement && !mapDraggedSincePress) {
                addPendingShapePoint(new Position(event.getX(), event.getY()));
                return;
            }
            if (pendingAreaObjectPlacement && !mapDraggedSincePress) {
                addPendingShapePoint(new Position(event.getX(), event.getY()));
                return;
            }
            if (addingCablePoint && !mapDraggedSincePress) {
                addCableRoutePoint(new Position(event.getX(), event.getY()));
                return;
            }
            if (measuringActive && !mapDraggedSincePress) {
                handleMeasureClick(new Position(event.getX(), event.getY()));
            }
        });
        mapContentPane = new Pane(mapPane);
        updateZoomContentSize();

        mapScrollPane = new ScrollPane(mapContentPane);
        mapScrollPane.setPannable(true);
        mapScrollPane.setFitToWidth(false);
        mapScrollPane.setFitToHeight(false);
        mapScrollPane.setStyle("-fx-background: #eef1ec;");
        mapScrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (!event.isAltDown() || event.getDeltaY() == 0) {
                return;
            }
            zoomAtPointer(
                    event.getDeltaY() > 0 ? 1.1 : 1 / 1.1,
                    event.getSceneX(),
                    event.getSceneY()
            );
            event.consume();
        });

        sidebar = new VBox(10);
        sidebar.setPadding(new Insets(12));
        objectListSection = collapsibleSection(OBJECT_LIST_SECTION, "Objektid", createObjectListPanel(), false);
        selectedObjectSection = collapsibleSection(
                SELECTED_OBJECT_SECTION, "Valitud objekt", createDetailPanel(), true
        );
        TitledPane mapLayersSection = collapsibleSection(
                MAP_LAYERS_SECTION, "Kaardi kihid", createMapLayersPanel(), false
        );

        showPowerSummaryCheckBox = new CheckBox("Vool");
        showPowerSummaryCheckBox.setSelected(true);
        showPowerSummaryCheckBox.setOnAction(event -> refreshSummary());
        showCableSummaryCheckBox = new CheckBox("Kaablid");
        showCableSummaryCheckBox.setSelected(true);
        showCableSummaryCheckBox.setOnAction(event -> refreshSummary());
        showGroupSummaryCheckBox = new CheckBox("Grupid");
        showGroupSummaryCheckBox.setSelected(true);
        showGroupSummaryCheckBox.setOnAction(event -> refreshSummary());
        HBox summaryFilters = new HBox(10, showPowerSummaryCheckBox, showCableSummaryCheckBox, showGroupSummaryCheckBox);
        VBox cableLegend = new VBox(
                4,
                cableLegendRow(ConnectorType.SCHUKO_230V),
                cableLegendRow(ConnectorType.INDUSTRIAL_16A),
                cableLegendRow(ConnectorType.INDUSTRIAL_32A),
                cableLegendRow(ConnectorType.INDUSTRIAL_63A)
        );

        summaryList = new ListView<>();
        summaryList.setMinHeight(180);
        summaryList.setPrefHeight(260);
        summaryList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(SummaryListItem item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                setGraphic(null);
                setStyle("");
                setCursor(Cursor.DEFAULT);
                if (empty || item == null) {
                    return;
                }
                if (item.hasTarget()) {
                    setCursor(Cursor.HAND);
                }
                if (!item.hasLoad()) {
                    setText(item.text());
                    setStyle(item.text().contains("ÜLEKOORMUS")
                            ? "-fx-text-fill: #b91c1c; -fx-font-weight: bold;"
                            : "");
                    return;
                }

                PowerLoadLevel loadLevel = PowerLoadLevel.from(item.usedWatts(), item.capacityWatts());
                Label loadLabel = new Label(item.displayText());
                if (loadLevel == PowerLoadLevel.OVERLOADED) {
                    loadLabel.setStyle("-fx-text-fill: #b91c1c; -fx-font-weight: bold;");
                }
                ProgressBar loadBar = new ProgressBar(item.progress());
                loadBar.setMaxWidth(Double.MAX_VALUE);
                loadBar.setStyle("-fx-accent: %s;".formatted(loadLevel.colorHex()));
                VBox loadContent = new VBox(3, loadLabel, loadBar);
                if (!item.isExpandable()) {
                    setGraphic(loadContent);
                    return;
                }

                Button toggleButton = new Button(item.expanded() ? "▾" : "▸");
                toggleButton.setFocusTraversable(false);
                toggleButton.setMinWidth(28);
                toggleButton.setStyle("-fx-background-color: transparent; -fx-padding: 2 5 2 5;");
                toggleButton.setTooltip(new Tooltip(item.expanded() ? "Peida alamread" : "Näita alamridu"));
                toggleButton.setOnAction(event -> togglePowerSummaryItem(item.hierarchyKey()));
                HBox row = new HBox(4, toggleButton, loadContent);
                row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                row.setPadding(new Insets(0, 0, 0, item.depth() * 16.0));
                HBox.setHgrow(loadContent, Priority.ALWAYS);
                setGraphic(row);
            }
        });
        summaryList.setTooltip(new Tooltip("Klõps valib objekti, topeltklõps viib selle juurde kaardil"));
        summaryList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (synchronizingSidebarSelection || newValue == null || !newValue.hasTarget()) {
                return;
            }
            plan.findObject(newValue.targetObjectId()).ifPresent(object -> {
                if (!isSelected(object)) {
                    selectObject(object);
                }
            });
        });
        summaryList.setOnMouseClicked(event -> {
            SummaryListItem selectedItem = summaryList.getSelectionModel().getSelectedItem();
            if (selectedItem == null
                    || !selectedItem.hasTarget()
                    || event.getButton() != MouseButton.PRIMARY) {
                return;
            }
            plan.findObject(selectedItem.targetObjectId()).ifPresent(object -> {
                if (!isSelected(object)) {
                    selectObject(object);
                }
                if (event.getClickCount() == 2) {
                    centerMapOnObject(object);
                }
            });
            event.consume();
        });
        powerSummarySection = collapsibleSection(
                SUMMARY_SECTION,
                "Voolu kokkuvõte",
                new VBox(8, summaryFilters, cableLegend, summaryList),
                true
        );
        registerSidebarSection(OBJECT_LIST_SECTION, objectListSection);
        registerSidebarSection(SELECTED_OBJECT_SECTION, selectedObjectSection);
        registerSidebarSection(MAP_LAYERS_SECTION, mapLayersSection);
        registerSidebarSection(SUMMARY_SECTION, powerSummarySection);
        applySidebarSectionOrder(loadSidebarSectionOrder());
        ScrollPane sidebarScrollPane = new ScrollPane(sidebar);
        sidebarScrollPane.setFitToWidth(true);

        SplitPane splitPane = new SplitPane(mapScrollPane, sidebarScrollPane);
        splitPane.setDividerPositions(0.72);
        return splitPane;
    }

    private VBox createObjectListPanel() {
        objectSearchField = new TextField();
        objectSearchField.setPromptText("Otsi nime, tüübi või grupi järgi");
        objectSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            refreshObjectList();
            if (quickObjectSearchActive) {
                selectFirstQuickSearchResult();
            }
        });
        objectList = new ListView<>();
        objectList.setPrefHeight(objectListHeight);
        objectList.setTooltip(new Tooltip("Topeltklõps viib kaardil objektini"));
        objectList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(ObjectListEntry entry, boolean empty) {
                super.updateItem(entry, empty);
                if (empty || entry == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                    setOnContextMenuRequested(null);
                    return;
                }
                if (entry.isGroup()) {
                    Button toggleButton = new Button(entry.expanded() ? "▾" : "▸");
                    toggleButton.setFocusTraversable(false);
                    toggleButton.setMinWidth(28);
                    toggleButton.setStyle("-fx-background-color: transparent; -fx-padding: 2 5 2 5;");
                    boolean searchActive = objectSearchField != null && !objectSearchField.getText().isBlank();
                    toggleButton.setDisable(searchActive);
                    toggleButton.setTooltip(new Tooltip(searchActive
                            ? "Otsingu ajal kuvatakse kõik sobivad objektid"
                            : entry.expanded() ? "Peida grupi objektid" : "Näita grupi objekte"));
                    toggleButton.setOnAction(event -> toggleObjectGroup(entry.groupName()));
                    CheckBox visibilityCheckBox = new CheckBox();
                    visibilityCheckBox.setSelected(visibleGroups.contains(entry.groupName()));
                    visibilityCheckBox.setTooltip(new Tooltip("Kuva grupp kaardil"));
                    visibilityCheckBox.setOnAction(event -> setGroupVisible(
                            entry.groupName(), visibilityCheckBox.isSelected()
                    ));
                    Label groupLabel = new Label("%s (%d)".formatted(entry.groupName(), entry.objectCount()));
                    groupLabel.setStyle("-fx-font-weight: bold;");
                    HBox groupRow = new HBox(6, toggleButton, visibilityCheckBox, groupLabel);
                    groupRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                    setText(null);
                    setGraphic(groupRow);
                    setStyle("-fx-background-color: rgba(148,163,184,0.12);");
                    setOnContextMenuRequested(null);
                    return;
                }

                ObjectListItem item = entry.objectItem();
                Label nameLabel = new Label(item.object().name());
                nameLabel.setStyle(item.visible()
                        ? "-fx-font-weight: bold;"
                        : "-fx-font-weight: bold; -fx-text-fill: #6b7280; -fx-font-style: italic;");
                Label detailLabel = new Label(item.detailText());
                detailLabel.setStyle(item.visible()
                        ? "-fx-text-fill: #6b7280; -fx-font-size: 11;"
                        : "-fx-text-fill: #6b7280; -fx-font-size: 11; -fx-font-style: italic;");
                Rectangle colorSwatch = new Rectangle(12, 12);
                colorSwatch.setArcWidth(3);
                colorSwatch.setArcHeight(3);
                colorSwatch.setFill(Color.web(objectListColorHex(item.object())));
                colorSwatch.setStroke(Color.web("#111827"));
                colorSwatch.setStrokeWidth(0.7);
                colorSwatch.setOpacity(item.visible() ? 1.0 : 0.45);
                CheckBox visibilityCheckBox = new CheckBox();
                visibilityCheckBox.setSelected(!item.object().hidden());
                visibilityCheckBox.setTooltip(new Tooltip("Kuva objekt kaardil"));
                visibilityCheckBox.setOnAction(event -> setObjectHidden(
                        item.object(), !visibilityCheckBox.isSelected()
                ));
                VBox textBox = new VBox(2, nameLabel, detailLabel);
                HBox row = new HBox(8, visibilityCheckBox, colorSwatch, textBox);
                row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                row.setPadding(new Insets(0, 0, 0, 34));
                setText(null);
                setGraphic(row);
                setStyle(item.visible()
                        ? ""
                        : "-fx-text-fill: #6b7280; -fx-font-style: italic;");
                setOnContextMenuRequested(event -> {
                    showObjectContextMenu(
                            item.object(), event.getScreenX(), event.getScreenY()
                    );
                    event.consume();
                });
            }
        });
        objectList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (synchronizingSidebarSelection) {
                return;
            }
            if (newValue == null) {
                updateRevealObjectButton();
                return;
            }
            if (newValue.isGroup()) {
                return;
            }
            if (quickObjectSearchActive) {
                return;
            }
            PlannerObject object = newValue.objectItem().object();
            if (selectedObject != null && selectedObject.id().equals(object.id())) {
                updateRevealObjectButton();
                return;
            }
            selectObject(object);
        });
        objectList.setOnMouseClicked(event -> {
            ObjectListEntry selectedEntry = objectList.getSelectionModel().getSelectedItem();
            if (selectedEntry == null || selectedEntry.isGroup() || event.getButton() != MouseButton.PRIMARY) {
                return;
            }
            if (quickObjectSearchActive) {
                activateQuickObjectSearchResult(selectedEntry);
                event.consume();
            } else if (event.getClickCount() == 2) {
                centerMapOnObject(selectedEntry.objectItem().object());
                event.consume();
            }
        });
        revealObjectButton = new Button("Näita kaardil");
        revealObjectButton.setOnAction(event -> revealSelectedObjectOnMap());
        updateRevealObjectButton();
        refreshObjectList();
        return new VBox(8, objectSearchField, objectList, createObjectListResizeHandle(), revealObjectButton);
    }

    private void handleQuickObjectSearchKey(KeyEvent event) {
        if (!quickObjectSearchActive) {
            return;
        }
        if (event.getCode() == KeyCode.ESCAPE) {
            finishQuickObjectSearch();
            event.consume();
            return;
        }
        if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.UP) {
            moveQuickSearchSelection(event.getCode() == KeyCode.DOWN ? 1 : -1);
            event.consume();
            return;
        }
        if (event.getCode() == KeyCode.ENTER) {
            ObjectListEntry entry = objectList.getSelectionModel().getSelectedItem();
            if (entry != null && !entry.isGroup()) {
                activateQuickObjectSearchResult(entry);
            }
            event.consume();
        }
    }

    private void activateQuickObjectSearchResult(ObjectListEntry entry) {
        PlannerObject object = entry.objectItem().object();
        finishQuickObjectSearch();
        selectObject(object);
        centerMapOnObject(object);
        highlightObjectSearchResult(object);
    }

    private void selectFirstQuickSearchResult() {
        objectList.getItems().stream()
                .filter(entry -> !entry.isGroup())
                .findFirst()
                .ifPresent(entry -> selectSidebarItem(objectList, entry, objectListSection));
    }

    private void moveQuickSearchSelection(int offset) {
        List<ObjectListEntry> results = objectList.getItems().stream()
                .filter(entry -> !entry.isGroup())
                .toList();
        if (results.isEmpty()) {
            return;
        }
        ObjectListEntry selected = objectList.getSelectionModel().getSelectedItem();
        int currentIndex = results.indexOf(selected);
        int targetIndex = Math.clamp(currentIndex + offset, 0, results.size() - 1);
        selectSidebarItem(objectList, results.get(targetIndex), objectListSection);
    }

    private Label createObjectListResizeHandle() {
        Label handle = new Label("|||");
        handle.setMaxWidth(Double.MAX_VALUE);
        handle.setAlignment(javafx.geometry.Pos.CENTER);
        handle.setCursor(Cursor.V_RESIZE);
        handle.setTooltip(new Tooltip("Lohista objektide nimekirja kõrguse muutmiseks"));
        handle.setStyle("""
                -fx-background-color: #e5e7eb;
                -fx-text-fill: #6b7280;
                -fx-font-size: 9;
                -fx-padding: 1 0 1 0;
                """);
        Delta dragStart = new Delta();
        handle.setOnMousePressed(event -> {
            dragStart.x = event.getSceneY();
            dragStart.y = objectList.getPrefHeight();
            event.consume();
        });
        handle.setOnMouseDragged(event -> {
            objectListHeight = clamp(
                    dragStart.y + event.getSceneY() - dragStart.x,
                    MIN_OBJECT_LIST_HEIGHT,
                    MAX_OBJECT_LIST_HEIGHT
            );
            objectList.setPrefHeight(objectListHeight);
            event.consume();
        });
        handle.setOnMouseReleased(event -> {
            saveObjectListHeightPreference();
            event.consume();
        });
        return handle;
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private double loadObjectListHeightPreference() {
        return clamp(
                preferences.getDouble(OBJECT_LIST_HEIGHT_PREFERENCE, DEFAULT_OBJECT_LIST_HEIGHT),
                MIN_OBJECT_LIST_HEIGHT,
                MAX_OBJECT_LIST_HEIGHT
        );
    }

    private void saveObjectListHeightPreference() {
        preferences.putDouble(OBJECT_LIST_HEIGHT_PREFERENCE, objectListHeight);
    }

    private VBox createMapLayersPanel() {
        Button showAllLayersButton = new Button("Kõik sisse");
        showAllLayersButton.setOnAction(event -> setAllMapLayersVisible(true));
        Button hideAllLayersButton = new Button("Kõik välja");
        hideAllLayersButton.setOnAction(event -> setAllMapLayersVisible(false));
        FlowPane bulkActionsRow = new FlowPane(8, 6, showAllLayersButton, hideAllLayersButton);
        FlowPane cableVisibilityRow = new FlowPane(8, 6, showCablesButton, showCableLabelsButton);
        FlowPane cableTypeRow = new FlowPane(
                8,
                6,
                show230VCablesButton,
                show16ACablesButton,
                show32ACablesButton,
                show63ACablesButton
        );
        FlowPane objectTypeRow = new FlowPane(
                8,
                6,
                showObjectLabelsButton,
                showTentsButton,
                showPowerSourcesButton,
                showCustomObjectsButton,
                showTextObjectsButton,
                showMarkerObjectsButton,
                showAreaObjectsButton,
                showLineObjectsButton
        );
        return new VBox(
                8,
                bulkActionsRow,
                new Label("Kaablid"),
                cableVisibilityRow,
                cableTypeRow,
                new Label("Objektid"),
                objectTypeRow
        );
    }

    private void setAllMapLayersVisible(boolean visible) {
        showCablesButton.setSelected(visible);
        showCableLabelsButton.setSelected(visible);
        show230VCablesButton.setSelected(visible);
        show16ACablesButton.setSelected(visible);
        show32ACablesButton.setSelected(visible);
        show63ACablesButton.setSelected(visible);
        showObjectLabelsButton.setSelected(visible);
        showTentsButton.setSelected(visible);
        showPowerSourcesButton.setSelected(visible);
        showCustomObjectsButton.setSelected(visible);
        showTextObjectsButton.setSelected(visible);
        showMarkerObjectsButton.setSelected(visible);
        showAreaObjectsButton.setSelected(visible);
        showLineObjectsButton.setSelected(visible);
        updateMapLayerVisibility();
    }

    private void refreshObjectList() {
        if (objectList == null || plan == null) {
            return;
        }
        String query = objectSearchField == null ? "" : objectSearchField.getText().trim().toLowerCase();
        List<ObjectListItem> objectItems = plan.objects().stream()
                .map(object -> new ObjectListItem(
                        object,
                        objectTypeName(object),
                        groupNameForFilter(object),
                        objectMeasurementText(object),
                        isObjectVisibleOnMap(object)
                ))
                .filter(item -> objectListItemMatches(item, query))
                .sorted(Comparator
                        .comparing(ObjectListItem::visible).reversed()
                        .thenComparing(ObjectListItem::type, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(item -> item.object().name(), String.CASE_INSENSITIVE_ORDER))
                .toList();

        Map<String, List<ObjectListItem>> itemsByGroup = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (ObjectListItem item : objectItems) {
            itemsByGroup.computeIfAbsent(item.groupName(), ignored -> new ArrayList<>()).add(item);
        }
        List<ObjectListEntry> entries = new ArrayList<>();
        for (Map.Entry<String, List<ObjectListItem>> groupEntry : itemsByGroup.entrySet()) {
            String groupName = groupEntry.getKey();
            boolean expanded = !query.isBlank()
                    || !collapsedObjectGroups.contains(groupName);
            entries.add(ObjectListEntry.group(groupName, groupEntry.getValue().size(), expanded));
            if (expanded) {
                groupEntry.getValue().stream()
                        .map(ObjectListEntry::object)
                        .forEach(entries::add);
            }
        }

        objectList.getItems().setAll(entries);
        revealObjectInObjectList(selectedObject);
        updateRevealObjectButton();
    }

    private void toggleObjectGroup(String groupName) {
        if (!collapsedObjectGroups.add(groupName)) {
            collapsedObjectGroups.remove(groupName);
        }
        refreshObjectList();
    }

    private void setGroupVisible(String groupName, boolean visible) {
        if (visible) {
            visibleGroups.add(groupName);
            plan.setGroupHidden(groupName, false);
        } else {
            visibleGroups.remove(groupName);
            plan.setGroupHidden(groupName, true);
        }
        redrawMap();
        refreshObjectList();
        refreshSummary();
        markDirty();
    }

    private boolean objectListItemMatches(ObjectListItem item, String query) {
        if (query.isBlank()) {
            return true;
        }
        return item.object().name().toLowerCase().contains(query)
                || item.type().toLowerCase().contains(query)
                || item.groupName().toLowerCase().contains(query)
                || (!item.visible() && "peidetud".contains(query));
    }

    private String objectListColorHex(PlannerObject object) {
        if (object instanceof Tent tent) {
            return tent.colorHex();
        }
        if (object instanceof PowerSource) {
            return "#2563eb";
        }
        if (object instanceof CustomObject customObject) {
            return customObject.colorHex();
        }
        if (object instanceof TextObject textObject) {
            return textObject.colorHex();
        }
        if (object instanceof MarkerObject markerObject) {
            return markerObject.colorHex();
        }
        if (object instanceof AreaObject areaObject) {
            return areaObject.colorHex();
        }
        if (object instanceof LineObject lineObject) {
            return lineObject.colorHex();
        }
        return "#9ca3af";
    }

    private String objectMeasurementText(PlannerObject object) {
        if (object instanceof LineObject lineObject) {
            return "pikkus %.1f m".formatted(
                    GeometryCalculator.lineLengthMeters(lineObject.points(), pixelsPerMeter())
            );
        }
        if (object instanceof AreaObject areaObject) {
            return "pindala %.1f m² · ümbermõõt %.1f m".formatted(
                    GeometryCalculator.polygonAreaSquareMeters(areaObject.points(), pixelsPerMeter()),
                    GeometryCalculator.polygonPerimeterMeters(areaObject.points(), pixelsPerMeter())
            );
        }
        if (object instanceof CustomObject customObject) {
            return "pindala %.1f m² · ümbermõõt %.1f m".formatted(
                    GeometryCalculator.customObjectAreaSquareMeters(customObject),
                    GeometryCalculator.customObjectPerimeterMeters(customObject)
            );
        }
        return "";
    }

    private void centerMapOnObject(PlannerObject object) {
        if (mapScrollPane == null || object == null) {
            return;
        }
        Platform.runLater(() -> {
            Position center = CablePathHelper.objectCenter(object, pixelsPerMeter());
            Bounds viewportBounds = mapScrollPane.getViewportBounds();
            double contentWidth = Math.max(mapWidth * zoomLevel, viewportBounds.getWidth());
            double contentHeight = Math.max(mapHeight * zoomLevel, viewportBounds.getHeight());
            double horizontalRange = contentWidth - viewportBounds.getWidth();
            double verticalRange = contentHeight - viewportBounds.getHeight();

            if (horizontalRange > 0) {
                double targetX = center.x() * zoomLevel - viewportBounds.getWidth() / 2;
                mapScrollPane.setHvalue(clamp(targetX / horizontalRange, 0, 1));
            }
            if (verticalRange > 0) {
                double targetY = center.y() * zoomLevel - viewportBounds.getHeight() / 2;
                mapScrollPane.setVvalue(clamp(targetY / verticalRange, 0, 1));
            }
        });
    }

    private void highlightObjectSearchResult(PlannerObject object) {
        if (objectSearchHighlightTimeline != null) {
            objectSearchHighlightTimeline.stop();
        }
        if (objectSearchHighlight != null) {
            mapPane.getChildren().remove(objectSearchHighlight);
        }
        Node objectNode = mapObjectNodes.get(object.id());
        if (objectNode != null) {
            objectNode.applyCss();
            objectNode.autosize();
        }
        Bounds bounds = objectNode == null ? null : objectNode.getBoundsInParent();
        Position fallbackCenter = CablePathHelper.objectCenter(object, pixelsPerMeter());
        double padding = 12;
        double x = bounds == null ? fallbackCenter.x() - 24 : bounds.getMinX() - padding;
        double y = bounds == null ? fallbackCenter.y() - 24 : bounds.getMinY() - padding;
        double width = bounds == null ? 48 : Math.max(48, bounds.getWidth() + padding * 2);
        double height = bounds == null ? 48 : Math.max(48, bounds.getHeight() + padding * 2);
        Rectangle highlight = new Rectangle(x, y, width, height);
        highlight.setArcWidth(18);
        highlight.setArcHeight(18);
        highlight.setFill(Color.TRANSPARENT);
        highlight.setStroke(Color.web("#d946ef"));
        highlight.setStrokeWidth(6);
        highlight.setMouseTransparent(true);
        objectSearchHighlight = highlight;
        mapPane.getChildren().add(highlight);
        highlight.toFront();

        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(highlight.scaleXProperty(), 1.0),
                        new KeyValue(highlight.scaleYProperty(), 1.0),
                        new KeyValue(highlight.opacityProperty(), 1.0)
                ),
                new KeyFrame(
                        Duration.millis(320),
                        new KeyValue(highlight.scaleXProperty(), 1.12),
                        new KeyValue(highlight.scaleYProperty(), 1.12),
                        new KeyValue(highlight.opacityProperty(), 0.25)
                )
        );
        timeline.setAutoReverse(true);
        timeline.setCycleCount(6);
        timeline.setOnFinished(event -> {
            mapPane.getChildren().remove(highlight);
            if (objectSearchHighlight == highlight) {
                objectSearchHighlight = null;
                objectSearchHighlightTimeline = null;
            }
        });
        objectSearchHighlightTimeline = timeline;
        timeline.play();
    }

    private void updateRevealObjectButton() {
        if (revealObjectButton == null) {
            return;
        }
        boolean hiddenSelection = selectedObject != null
                && !isObjectVisibleOnMap(selectedObject);
        revealObjectButton.setDisable(!hiddenSelection);
        revealObjectButton.setTooltip(new Tooltip(hiddenSelection
                ? "Lülitab valitud objekti, grupi ja tüübi kaardil nähtavaks"
                : "Valitud objekt on juba kaardil nähtav"));
    }

    private void revealSelectedObjectOnMap() {
        if (selectedObject == null) {
            return;
        }
        selectedObject.setHidden(false);
        setObjectTypeVisible(selectedObject, true);
        String groupName = groupNameForFilter(selectedObject);
        visibleGroups.add(groupName);
        plan.setGroupHidden(groupName, false);
        updateMapLayerVisibility();
        refreshGroupFilters();
        refreshObjectList();
        redrawMap();
        markDirty();
    }

    private void setObjectTypeVisible(PlannerObject object, boolean visible) {
        if (object instanceof Tent) {
            showTentsButton.setSelected(visible);
        } else if (object instanceof PowerSource) {
            showPowerSourcesButton.setSelected(visible);
        } else if (object instanceof TextObject) {
            showTextObjectsButton.setSelected(visible);
        } else if (object instanceof MarkerObject) {
            showMarkerObjectsButton.setSelected(visible);
        } else if (object instanceof AreaObject) {
            showAreaObjectsButton.setSelected(visible);
        } else if (object instanceof LineObject) {
            showLineObjectsButton.setSelected(visible);
        } else if (object instanceof CustomObject) {
            showCustomObjectsButton.setSelected(visible);
        }
    }

    private void changeZoom(double factor) {
        setZoom(zoomLevel * factor);
    }

    private void zoomAtPointer(double factor, double sceneX, double sceneY) {
        Node viewport = mapScrollPane.lookup(".viewport");
        if (viewport == null) {
            changeZoom(factor);
            return;
        }
        Point2D mapPoint = mapPane.sceneToLocal(sceneX, sceneY);
        Bounds viewportSceneBounds = viewport.localToScene(viewport.getBoundsInLocal());
        double pointerViewportX = sceneX - viewportSceneBounds.getMinX();
        double pointerViewportY = sceneY - viewportSceneBounds.getMinY();

        setZoom(zoomLevel * factor);
        Platform.runLater(() -> {
            mapScrollPane.applyCss();
            mapScrollPane.layout();
            Bounds viewportBounds = mapScrollPane.getViewportBounds();
            double horizontalRange = Math.max(0, mapWidth * zoomLevel - viewportBounds.getWidth());
            double verticalRange = Math.max(0, mapHeight * zoomLevel - viewportBounds.getHeight());
            if (horizontalRange > 0) {
                double targetOffsetX = mapPoint.getX() * zoomLevel - pointerViewportX;
                mapScrollPane.setHvalue(Math.clamp(targetOffsetX / horizontalRange, 0.0, 1.0));
            }
            if (verticalRange > 0) {
                double targetOffsetY = mapPoint.getY() * zoomLevel - pointerViewportY;
                mapScrollPane.setVvalue(Math.clamp(targetOffsetY / verticalRange, 0.0, 1.0));
            }
        });
    }

    private void setZoom(double zoomLevel) {
        this.zoomLevel = Math.max(0.25, Math.min(4.0, zoomLevel));
        if (mapScale != null) {
            mapScale.setX(this.zoomLevel);
            mapScale.setY(this.zoomLevel);
        }
        if (zoomSlider != null && Math.abs(zoomSlider.getValue() - this.zoomLevel * 100) > 0.001) {
            zoomSlider.setValue(this.zoomLevel * 100);
        }
        if (zoomPercentButton != null) {
            zoomPercentButton.setText(zoomPercentText());
        }
        updateZoomContentSize();
    }

    private String zoomPercentText() {
        return Math.round(zoomLevel * 100) + "%";
    }

    private void updateZoomContentSize() {
        if (mapPane != null) {
            mapPane.setMinSize(mapWidth, mapHeight);
            mapPane.setPrefSize(mapWidth, mapHeight);
        }
        if (mapContentPane != null) {
            mapContentPane.setMinSize(mapWidth * zoomLevel, mapHeight * zoomLevel);
            mapContentPane.setPrefSize(mapWidth * zoomLevel, mapHeight * zoomLevel);
        }
    }

    private VBox createDetailPanel() {
        planNameField = new TextField(plan.name());
        Button applyPlanNameButton = new Button("Rakenda nimi");
        applyPlanNameButton.setOnAction(event -> applyPlanName());
        pixelsPerMeterField = new TextField(formatMeters(plan.pixelsPerMeter()));
        pixelsPerMeterField.setPromptText("px/m");
        Button applyScaleButton = new Button("Rakenda mõõtkava");
        applyScaleButton.setOnAction(event -> applyPixelsPerMeter());

        GridPane planForm = new GridPane();
        planForm.setHgap(8);
        planForm.setVgap(8);
        planForm.addRow(0, new Label("Plaani nimi"), planNameField);
        planForm.addRow(1, new Label(""), applyPlanNameButton);
        planForm.addRow(2, new Label("Piksleid meetri kohta"), pixelsPerMeterField);
        planForm.addRow(3, new Label(""), applyScaleButton);

        selectedTypeLabel = new Label("Vali kaardilt objekt");
        nameField = new TextField();
        groupField = new ComboBox<>();
        groupField.setEditable(true);
        groupField.setMaxWidth(Double.MAX_VALUE);
        lockedCheckBox = new CheckBox("Lukus");
        lockedCheckBox.setTooltip(new Tooltip("Lülita valitud objekti lukustust (Ctrl+L)"));
        lockedCheckBox.setOnAction(event -> updateSelectedLock());
        showMapLabelCheckBox = new CheckBox("Näita nime");
        showMapLabelCheckBox.setTooltip(new Tooltip(
                "Rakendub kohe; valitud objekti nimi jääb valiku ajal siiski nähtavaks"
        ));
        showMapLabelCheckBox.setOnAction(event -> updateSelectedMapLabelVisibility());
        resetMapLabelButton = new Button("Lähtesta nime asukoht");
        resetMapLabelButton.setOnAction(event -> resetSelectedMapLabelPosition());
        tentWidthField = new TextField();
        tentHeightField = new TextField();
        tentRotationField = new TextField();
        tentColorPicker = new ColorPicker();
        tentOpacitySlider = createOpacitySlider(Tent.DEFAULT_OPACITY * 100.0);
        configureOpacityPreview(tentOpacitySlider);
        customObjectShapeComboBox = new ComboBox<>();
        customObjectShapeComboBox.getItems().addAll(CustomObjectShape.values());
        customObjectShapeComboBox.setConverter(customObjectShapeConverter());
        customObjectShapeComboBox.getSelectionModel().select(CustomObjectShape.SQUARE);
        customObjectColorPicker = new ColorPicker();
        customObjectOpacitySlider = createOpacitySlider(CustomObject.DEFAULT_OPACITY * 100.0);
        configureOpacityPreview(customObjectOpacitySlider);
        textObjectColorPicker = new ColorPicker();
        customObjectWidthLabel = new Label("Objekti laius m");
        customObjectHeightLabel = new Label("Objekti pikkus m");
        customObjectWidthField = new TextField();
        customObjectHeightField = new TextField();
        customObjectRotationLabel = new Label("Objekti pööre °");
        customObjectRotationField = new TextField();
        customObjectAreaLabel = new Label("-");
        customObjectPerimeterLabel = new Label("-");
        customObjectShapeComboBox.setOnAction(event -> updateCustomObjectSizeFields());
        powerConnectionComboBox = new ComboBox<>();
        powerConnectionComboBox.setMaxWidth(Double.MAX_VALUE);
        powerConnectionComboBox.setOnAction(event -> {
            if (!updatingDetailControls) {
                refreshDetails();
            }
        });
        powerSourceComboBox = new ComboBox<>();
        powerSourceComboBox.setCellFactory(list -> createPowerSourceChoiceCell());
        powerSourceComboBox.setButtonCell(createPowerSourceChoiceCell());
        connectionOutletComboBox = new ComboBox<>();
        connectionOutletComboBox.setCellFactory(list -> createOutletChoiceCell());
        connectionOutletComboBox.setButtonCell(createOutletChoiceCell());
        cableLengthNotesField = new TextField();
        cableLengthNotesField.setPromptText("nt 20 + 10 + 10");
        cableLengthNotesField.setOnAction(event -> autoApplyCableLengthNotes());
        cableLengthNotesField.focusedProperty().addListener((observable, wasFocused, isFocused) -> {
            if (wasFocused && !isFocused) {
                autoApplyCableLengthNotes();
            }
        });
        cableNotesField = new TextField();
        cableNotesField.setPromptText("Kaabli märkmed");
        cableNotesField.setOnAction(event -> autoApplyCableNotes());
        cableNotesField.focusedProperty().addListener((observable, wasFocused, isFocused) -> {
            if (wasFocused && !isFocused) {
                autoApplyCableNotes();
            }
        });
        resetCableLabelButton = new Button("Lähtesta kaablisilt");
        resetCableLabelButton.setOnAction(event -> resetSelectedCableLabelPosition());
        showSelectedCableLabelCheckBox = new CheckBox("Näita kaablisilti");
        showSelectedCableLabelCheckBox.setSelected(true);
        showSelectedCableLabelCheckBox.setOnAction(event -> updateSelectedCableLabelVisibility());
        removePowerConnectionButton = new Button("Eemalda ühendus");
        removePowerConnectionButton.setOnAction(event -> removeSelectedPowerConnection());
        makeDefaultPowerConnectionButton = new Button("Määra põhiühenduseks");
        makeDefaultPowerConnectionButton.setOnAction(event -> makeSelectedPowerConnectionDefault());
        textObjectFontSizeSlider = createPixelSlider(
                MIN_FONT_SIZE_PIXELS,
                MAX_FONT_SIZE_PIXELS,
                TextObject.DEFAULT_FONT_SIZE
        );
        notesArea = new TextArea();
        notesArea.setPrefRowCount(3);
        notesArea.focusedProperty().addListener((observable, wasFocused, isFocused) -> {
            if (wasFocused && !isFocused) {
                autoApplyNotes();
            }
        });
        equipmentList = new ListView<>();
        equipmentList.setPrefHeight(220);
        equipmentList.setCellFactory(list -> createEquipmentListCell());
        addEquipmentButton = new Button("Lisa seade");
        addEquipmentButton.setOnAction(event -> showEquipmentDialog(null));
        addAlternativePowerConnectionButton = new Button("Lisa alternatiivne ühendus");
        addAlternativePowerConnectionButton.setTooltip(new Tooltip(
                "Loo valitud ühenduse põhjal uus seadistatav alternatiivühendus"
        ));
        addAlternativePowerConnectionButton.setOnAction(event -> addAlternativePowerConnection());
        outletList = new ListView<>();
        outletList.setPrefHeight(160);
        outletList.setCellFactory(list -> createOutletChoiceCell());
        outletList.getSelectionModel().selectedIndexProperty()
                .addListener((observable, oldValue, newValue) -> loadSelectedOutletDetails());
        outletNameField = new TextField();
        outletNameField.setPromptText("Väljundi nimi");
        outletTypeComboBox = new ComboBox<>();
        outletTypeComboBox.getItems().addAll(ConnectorType.values());
        outletTypeComboBox.setConverter(connectorTypeConverter());
        outletTypeComboBox.getSelectionModel().select(ConnectorType.SCHUKO_230V);
        outletTypeComboBox.setOnAction(event -> updateDefaultOutletCapacity());
        outletCapacityWattsField = new TextField();
        outletCapacityWattsField.setPromptText("W");
        updateDefaultOutletCapacity();
        addOutletButton = new Button("Lisa väljund");
        addOutletButton.setOnAction(event -> addOutletToSelectedPowerSource());
        updateOutletButton = new Button("Muuda valitud väljundit");
        updateOutletButton.setOnAction(event -> updateSelectedOutlet());
        removeOutletButton = new Button("Eemalda valitud");
        removeOutletButton.setOnAction(event -> removeSelectedOutlet());

        GridPane baseForm = detailGrid();
        baseForm.addRow(0, new Label("Tüüp"), selectedTypeLabel);
        baseForm.addRow(1, new Label("Nimi"), nameField);
        baseForm.addRow(2, new Label("Grupp"), groupField);
        baseForm.addRow(3, new Label("Lukustus"), lockedCheckBox);
        baseForm.addRow(4, new Label("Kaardil"), showMapLabelCheckBox);
        baseForm.addRow(5, new Label("Nime asukoht"), resetMapLabelButton);

        GridPane customObjectForm = detailGrid();
        customObjectForm.addRow(0, new Label("Kuju"), customObjectShapeComboBox);
        customObjectForm.addRow(1, new Label("Värv"), customObjectColorPicker);
        customObjectForm.addRow(2, new Label("Läbipaistvus"), opacityControl(customObjectOpacitySlider));
        customObjectForm.addRow(3, customObjectWidthLabel, customObjectWidthField);
        customObjectForm.addRow(4, customObjectHeightLabel, customObjectHeightField);
        customObjectForm.addRow(5, customObjectRotationLabel, customObjectRotationField);
        customObjectForm.addRow(6, new Label("Pindala"), customObjectAreaLabel);
        customObjectForm.addRow(7, new Label("Ümbermõõt"), customObjectPerimeterLabel);
        customObjectPanel = new VBox(8, sectionLabel("Objekt"), customObjectForm);

        GridPane textObjectForm = detailGrid();
        textObjectForm.addRow(0, new Label("Värv"), textObjectColorPicker);
        textObjectForm.addRow(1, new Label("Suurus"), pixelControl(textObjectFontSizeSlider));
        textObjectPanel = new VBox(8, sectionLabel("Tekst"), textObjectForm);

        markerTypeComboBox = new ComboBox<>();
        markerTypeComboBox.getItems().addAll(MarkerType.values());
        markerTypeComboBox.setConverter(markerTypeConverter());
        markerTypeComboBox.getSelectionModel().select(MarkerType.WC);
        markerColorPicker = new ColorPicker();
        GridPane markerForm = detailGrid();
        markerForm.addRow(0, new Label("Tüüp"), markerTypeComboBox);
        markerForm.addRow(1, new Label("Värv"), markerColorPicker);
        markerPanel = new VBox(8, sectionLabel("Marker"), markerForm);

        areaColorPicker = new ColorPicker();
        areaOpacitySlider = createOpacitySlider(AreaObject.DEFAULT_OPACITY * 100.0);
        configureOpacityPreview(areaOpacitySlider);
        areaSizeLabel = new Label("-");
        areaPerimeterLabel = new Label("-");
        GridPane areaForm = detailGrid();
        areaForm.addRow(0, new Label("Värv"), areaColorPicker);
        areaForm.addRow(1, new Label("Läbipaistvus"), opacityControl(areaOpacitySlider));
        areaForm.addRow(2, new Label("Pindala"), areaSizeLabel);
        areaForm.addRow(3, new Label("Ümbermõõt"), areaPerimeterLabel);
        areaPanel = new VBox(8, sectionLabel("Ala"), areaForm);

        lineColorPicker = new ColorPicker();
        lineWidthSlider = createPixelSlider(1, 50, LineObject.DEFAULT_WIDTH_PIXELS);
        configureAutoApplyingDetailControls();
        lineLengthLabel = new Label("-");
        GridPane lineForm = detailGrid();
        lineForm.addRow(0, new Label("Värv"), lineColorPicker);
        lineForm.addRow(1, new Label("Paksus"), pixelControl(lineWidthSlider));
        lineForm.addRow(2, new Label("Pikkus"), lineLengthLabel);
        linePanel = new VBox(8, sectionLabel("Joon"), lineForm);

        GridPane tentForm = detailGrid();
        tentForm.addRow(0, new Label("Laius m"), tentWidthField);
        tentForm.addRow(1, new Label("Pikkus m"), tentHeightField);
        tentForm.addRow(2, new Label("Pööre °"), tentRotationField);
        tentForm.addRow(3, new Label("Värv"), tentColorPicker);
        tentForm.addRow(4, new Label("Läbipaistvus"), opacityControl(tentOpacitySlider));
        tentPanel = new VBox(8, sectionLabel("Telk"), tentForm);

        choosePowerSourceButton = new Button("Vali kapp kaardilt");
        choosePowerSourceButton.setOnAction(event -> startPowerSourceSelectionFromMap());
        powerSourceComboBox.setMaxWidth(Double.MAX_VALUE);
        HBox powerSourceSelection = new HBox(8, powerSourceComboBox, choosePowerSourceButton);
        HBox.setHgrow(powerSourceComboBox, Priority.ALWAYS);

        GridPane powerConnectionForm = detailGrid();
        powerConnectionForm.addRow(0, new Label("Muudetav ühendus"), powerConnectionComboBox);
        powerConnectionForm.addRow(1, new Label("Vooluallikas"), powerSourceSelection);
        powerConnectionForm.addRow(2, new Label("Väljund"), connectionOutletComboBox);
        GridPane cableDetailsForm = detailGrid();
        cableDetailsForm.addRow(0, new Label("Kaabli tükid"), cableLengthNotesField);
        cableDetailsForm.addRow(1, new Label("Kaabli märkmed"), cableNotesField);
        cableDetailsForm.addRow(2, new Label("Kaablisilt"), showSelectedCableLabelCheckBox);
        cableDetailsForm.addRow(3, new Label("Sildi asukoht"), resetCableLabelButton);
        TitledPane cableDetailsPane = new TitledPane("Kaabli lisainfo", cableDetailsForm);
        cableDetailsPane.setExpanded(false);
        HBox powerConnectionActions = new HBox(
                8, addAlternativePowerConnectionButton, makeDefaultPowerConnectionButton, removePowerConnectionButton
        );
        powerConnectionPanel = new VBox(
                8,
                sectionLabel("Vool"),
                powerConnectionForm,
                cableDetailsPane,
                powerConnectionActions
        );

        GridPane notesForm = detailGrid();
        notesForm.addRow(0, new Label("Märkmed"), notesArea);

        deleteObjectButton = new Button("Kustuta objekt");
        deleteObjectButton.setTooltip(new Tooltip("Kustuta valitud objekt (Delete)"));
        deleteObjectButton.setOnAction(event -> deleteSelectedObject());

        equipmentPanel = new VBox(
                8,
                equipmentList,
                addEquipmentButton
        );
        equipmentSection = collapsibleSection(EQUIPMENT_SECTION, "Seadmed", equipmentPanel, false);
        outletPanel = new VBox(
                8,
                outletList,
                outletNameField,
                outletTypeComboBox,
                outletCapacityWattsField,
                addOutletButton,
                updateOutletButton,
                removeOutletButton
        );
        outletSection = collapsibleSection(OUTLET_SECTION, "Kapi väljundid", outletPanel, false);
        VBox detailPanel = new VBox(
                10,
                baseForm,
                customObjectPanel,
                textObjectPanel,
                markerPanel,
                areaPanel,
                linePanel,
                tentPanel,
                powerConnectionPanel,
                equipmentSection,
                outletSection,
                new VBox(8, sectionLabel("Märkmed"), notesForm),
                deleteObjectButton
        );
        detailPanel.setPadding(new Insets(0, 0, 12, 0));
        return detailPanel;
    }

    private Label sectionLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold; -fx-padding: 8 0 0 0;");
        return label;
    }

    private GridPane detailGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        return grid;
    }

    private Slider createOpacitySlider(double initialPercentage) {
        Slider slider = new Slider(0, 100, initialPercentage);
        slider.setMajorTickUnit(25);
        slider.setMinorTickCount(4);
        slider.setBlockIncrement(5);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setSnapToTicks(true);
        slider.setPrefWidth(210);
        return slider;
    }

    private HBox opacityControl(Slider slider) {
        Label valueLabel = new Label(opacityPercentageText(slider.getValue()));
        valueLabel.setMinWidth(42);
        slider.valueProperty().addListener((observable, oldValue, newValue) ->
                valueLabel.setText(opacityPercentageText(newValue.doubleValue()))
        );
        HBox control = new HBox(8, slider, valueLabel);
        control.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        return control;
    }

    private void configureOpacityPreview(Slider slider) {
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (updatingOpacityControls || !previewSelectedObjectOpacity(slider, newValue.doubleValue())) {
                return;
            }
            redrawMap();
            if (slider.isValueChanging()) {
                opacityDragChanged = true;
            } else {
                markDirty();
            }
        });
        slider.valueChangingProperty().addListener((observable, wasChanging, isChanging) -> {
            if (wasChanging && !isChanging && opacityDragChanged) {
                opacityDragChanged = false;
                markDirty();
            }
        });
    }

    private boolean previewSelectedObjectOpacity(Slider slider, double percentage) {
        double opacity = percentage / 100.0;
        if (slider == tentOpacitySlider && selectedObject instanceof Tent tent) {
            tent.setOpacity(opacity);
            return true;
        }
        if (slider == customObjectOpacitySlider && selectedObject instanceof CustomObject customObject) {
            customObject.setOpacity(opacity);
            return true;
        }
        if (slider == areaOpacitySlider && selectedObject instanceof AreaObject areaObject) {
            areaObject.setOpacity(opacity);
            return true;
        }
        return false;
    }

    private void setOpacitySliderValue(Slider slider, double percentage) {
        updatingOpacityControls = true;
        try {
            slider.setValue(percentage);
        } finally {
            updatingOpacityControls = false;
        }
    }

    private void configureAutoApplyingDetailControls() {
        configureTextCommit(nameField, this::autoApplySelectedName);
        configureTextCommit(groupField.getEditor(), this::autoApplySelectedGroup);
        groupField.setOnAction(event -> autoApplySelectedGroup());
        configureTextCommit(tentWidthField, this::autoApplyTentSize);
        configureTextCommit(tentHeightField, this::autoApplyTentSize);
        configureTextCommit(tentRotationField, this::autoApplyTentRotation);
        configureTextCommit(customObjectWidthField, this::autoApplyCustomObjectSize);
        configureTextCommit(customObjectHeightField, this::autoApplyCustomObjectSize);
        configureTextCommit(customObjectRotationField, this::autoApplyCustomObjectRotation);

        tentColorPicker.setOnAction(event -> autoApplySelectedColor());
        customObjectColorPicker.setOnAction(event -> autoApplySelectedColor());
        textObjectColorPicker.setOnAction(event -> autoApplySelectedColor());
        markerColorPicker.setOnAction(event -> autoApplySelectedColor());
        areaColorPicker.setOnAction(event -> autoApplySelectedColor());
        lineColorPicker.setOnAction(event -> autoApplySelectedColor());
        markerTypeComboBox.setOnAction(event -> autoApplyMarkerType());
        customObjectShapeComboBox.setOnAction(event -> {
            updateCustomObjectSizeFields();
            autoApplyCustomObjectShape();
        });
        powerSourceComboBox.setOnAction(event -> {
            boolean applyUserChange = !updatingDetailControls;
            boolean previouslyUpdating = updatingDetailControls;
            updatingDetailControls = true;
            try {
                refreshConnectionOutletChoices(null);
            } finally {
                updatingDetailControls = previouslyUpdating;
            }
            if (applyUserChange) {
                autoApplySelectedPowerConnection();
            }
        });
        connectionOutletComboBox.setOnAction(event -> autoApplySelectedPowerConnection());
        configureDetailSliderPreview(textObjectFontSizeSlider);
        configureDetailSliderPreview(lineWidthSlider);
    }

    private void configureTextCommit(TextField field, Runnable action) {
        field.setOnAction(event -> action.run());
        field.focusedProperty().addListener((observable, wasFocused, isFocused) -> {
            if (wasFocused && !isFocused) {
                action.run();
            }
        });
    }

    private void configureDetailSliderPreview(Slider slider) {
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (updatingDetailControls || !previewSelectedDetailSlider(slider, newValue.doubleValue())) {
                return;
            }
            redrawMap();
            if (slider.isValueChanging()) {
                detailSliderDragChanged = true;
            } else {
                markDirty();
            }
        });
        slider.valueChangingProperty().addListener((observable, wasChanging, isChanging) -> {
            if (wasChanging && !isChanging && detailSliderDragChanged) {
                detailSliderDragChanged = false;
                markDirty();
            }
        });
    }

    private boolean previewSelectedDetailSlider(Slider slider, double value) {
        if (slider == textObjectFontSizeSlider && selectedObject instanceof TextObject textObject) {
            textObject.setFontSize(value);
            return true;
        }
        if (slider == lineWidthSlider && selectedObject instanceof LineObject lineObject) {
            lineObject.setWidthPixels(value);
            return true;
        }
        return false;
    }

    private String opacityPercentageText(double percentage) {
        return "%.0f%%".formatted(percentage);
    }

    private Slider createPixelSlider(double min, double max, double initialValue) {
        Slider slider = new Slider(min, max, initialValue);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setBlockIncrement(1);
        slider.setSnapToTicks(true);
        slider.setPrefWidth(210);
        return slider;
    }

    private HBox pixelControl(Slider slider) {
        Label valueLabel = new Label(pixelValueText(slider.getValue()));
        valueLabel.setMinWidth(46);
        slider.valueProperty().addListener((observable, oldValue, newValue) ->
                valueLabel.setText(pixelValueText(newValue.doubleValue()))
        );
        HBox control = new HBox(8, slider, valueLabel);
        control.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        return control;
    }

    private String pixelValueText(double value) {
        return "%.0f px".formatted(value);
    }

    private TitledPane collapsibleSection(String stateKey, String title, Node content, boolean expandedByDefault) {
        TitledPane pane = new TitledPane(title, content);
        pane.setExpanded(sidebarSectionStates.getOrDefault(stateKey, expandedByDefault));
        pane.setCollapsible(true);
        pane.expandedProperty().addListener((observable, oldValue, newValue) ->
                sidebarSectionStates.put(stateKey, newValue)
        );
        return pane;
    }

    private void registerSidebarSection(String stateKey, TitledPane pane) {
        sidebarSections.put(stateKey, pane);
        MenuItem moveUpItem = new MenuItem("Liiguta üles");
        moveUpItem.setOnAction(event -> moveSidebarSection(stateKey, -1));
        MenuItem moveDownItem = new MenuItem("Liiguta alla");
        moveDownItem.setOnAction(event -> moveSidebarSection(stateKey, 1));
        MenuItem resetOrderItem = new MenuItem("Taasta vaikejärjestus");
        resetOrderItem.setOnAction(event -> resetSidebarSectionOrder());
        ContextMenu contextMenu = new ContextMenu(moveUpItem, moveDownItem, resetOrderItem);
        contextMenu.setOnShowing(event -> {
            List<String> order = currentSidebarSectionOrder();
            int index = order.indexOf(stateKey);
            moveUpItem.setDisable(index <= 0);
            moveDownItem.setDisable(index < 0 || index >= order.size() - 1);
        });
        pane.setOnContextMenuRequested(event -> {
            showContextMenu(contextMenu, pane, event.getScreenX(), event.getScreenY());
            event.consume();
        });
    }

    private List<String> loadSidebarSectionOrder() {
        List<String> order = new ArrayList<>();
        String storedOrder = preferences.get(SIDEBAR_SECTION_ORDER_PREFERENCE, "");
        for (String stateKey : storedOrder.split(",")) {
            if (DEFAULT_SIDEBAR_SECTION_ORDER.contains(stateKey) && !order.contains(stateKey)) {
                order.add(stateKey);
            }
        }
        for (String stateKey : DEFAULT_SIDEBAR_SECTION_ORDER) {
            if (!order.contains(stateKey)) {
                order.add(stateKey);
            }
        }
        return order;
    }

    private List<String> currentSidebarSectionOrder() {
        return sidebar.getChildren().stream()
                .map(node -> sidebarSections.entrySet().stream()
                        .filter(entry -> entry.getValue() == node)
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElse(""))
                .filter(stateKey -> !stateKey.isBlank())
                .toList();
    }

    private void moveSidebarSection(String stateKey, int offset) {
        List<String> order = new ArrayList<>(currentSidebarSectionOrder());
        int currentIndex = order.indexOf(stateKey);
        int targetIndex = currentIndex + offset;
        if (currentIndex < 0 || targetIndex < 0 || targetIndex >= order.size()) {
            return;
        }
        order.remove(currentIndex);
        order.add(targetIndex, stateKey);
        applySidebarSectionOrder(order);
        saveSidebarSectionOrder(order);
    }

    private void resetSidebarSectionOrder() {
        applySidebarSectionOrder(DEFAULT_SIDEBAR_SECTION_ORDER);
        preferences.remove(SIDEBAR_SECTION_ORDER_PREFERENCE);
    }

    private void applySidebarSectionOrder(List<String> order) {
        sidebar.getChildren().setAll(order.stream()
                .map(sidebarSections::get)
                .filter(java.util.Objects::nonNull)
                .toList());
    }

    private void saveSidebarSectionOrder(List<String> order) {
        preferences.put(SIDEBAR_SECTION_ORDER_PREFERENCE, String.join(",", order));
    }

    private void toggleSelectedPlacement() {
        if (isPlacementPending()) {
            if (isShapePlacementPending() && canFinishPendingShapePlacement()) {
                finishPendingShapePlacement();
            } else {
                cancelPlacement();
            }
            return;
        }

        PlacementType selectedType = placementTypeComboBox.getSelectionModel().getSelectedItem();
        if (selectedType == null) {
            selectedType = PlacementType.TENT;
        }

        startPlacement(selectedType);
    }

    private boolean startPlacement(PlacementType selectedType) {
        if (mapLayoutLocked) {
            showMapLayoutLockedMessage();
            return false;
        }

        PlacementDetails placementDetails = askPlacementDetails(selectedType);
        if (placementDetails == null) {
            return false;
        }
        pendingPlacementName = placementDetails.name();
        pendingPlacementGroupName = placementDetails.groupName();
        pendingPlacementColorHex = placementDetails.colorHex();
        pendingPlacementWidthMeters = placementDetails.widthMeters();
        pendingPlacementHeightMeters = placementDetails.heightMeters();
        pendingPlacementOpacity = placementDetails.opacity();
        pendingPlacementLineWidthPixels = placementDetails.lineWidthPixels();
        pendingPlacementFontSizePixels = placementDetails.fontSizePixels();
        pendingPlacementShape = placementDetails.shape();
        pendingPlacementMarkerType = placementDetails.markerType();
        pendingPlacementShowMapLabel = placementDetails.showMapLabel();
        preferences.putBoolean(PLACEMENT_SHOW_MAP_LABEL_PREFERENCE, placementDetails.showMapLabel());

        switch (selectedType) {
            case TENT -> addTent();
            case POWER_SOURCE, DISTRIBUTION_PANEL -> addPowerSource(selectedType);
            case CUSTOM_OBJECT -> addCustomObject();
            case TEXT_OBJECT -> addTextObject();
            case MARKER_OBJECT -> addMarkerObject();
            case LINE_OBJECT -> addLineObject();
            case AREA_OBJECT -> addAreaObject();
        }
        return true;
    }

    private void showMapContextMenu(Position position, double screenX, double screenY) {
        if (isPlacementPending() || measuringActive || addingCablePoint) {
            return;
        }
        Menu addMenu = new Menu("Lisa");
        addMenu.setDisable(mapLayoutLocked);
        for (PlacementType placementType : PlacementType.values()) {
            MenuItem addItem = new MenuItem(placementType.toString());
            addItem.setOnAction(event -> startPlacementAt(placementType, position));
            addMenu.getItems().add(addItem);
        }
        MenuItem pasteItem = new MenuItem("Kleebi");
        pasteItem.setDisable(copiedObject == null || mapLayoutLocked);
        pasteItem.setOnAction(event -> pasteCopiedObject(position));
        showContextMenu(new ContextMenu(addMenu, pasteItem), mapPane, screenX, screenY);
    }

    private void startPlacementAt(PlacementType placementType, Position position) {
        if (!startPlacement(placementType)) {
            return;
        }
        switch (placementType) {
            case TENT -> placeTent(position);
            case POWER_SOURCE, DISTRIBUTION_PANEL -> placePowerSource(position);
            case CUSTOM_OBJECT -> placeCustomObject(position);
            case TEXT_OBJECT -> placeTextObject(position);
            case MARKER_OBJECT -> placeMarkerObject(position);
            case LINE_OBJECT, AREA_OBJECT -> addPendingShapePoint(position);
        }
    }

    private PlacementDetails askPlacementDetails(PlacementType placementType) {
        return PlacementDetailsDialog.show(
                stage,
                placementType,
                existingGroupNames(),
                preferences.getBoolean(PLACEMENT_SHOW_MAP_LABEL_PREFERENCE, true),
                MIN_FONT_SIZE_PIXELS,
                MAX_FONT_SIZE_PIXELS
        ).orElse(null);
    }

    private List<String> existingGroupNames() {
        Set<String> groupNames = new HashSet<>(knownGroups);
        if (plan != null) {
            for (PlannerObject object : plan.objects()) {
                groupNames.add(groupNameForFilter(object));
            }
        }
        groupNames.add("Määramata");
        return groupNames.stream()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    private void refreshGroupChoices(String groupName) {
        groupField.getItems().setAll(existingGroupNames());
        groupField.getSelectionModel().clearSelection();
        groupField.getEditor().setText(groupName == null ? "" : groupName);
    }

    private void addTent() {
        pendingTentPlacement = !pendingTentPlacement;
        pendingPowerSourcePlacement = false;
        pendingCustomObjectPlacement = false;
        pendingTextObjectPlacement = false;
        pendingMarkerPlacement = false;
        pendingLineObjectPlacement = false;
        pendingAreaObjectPlacement = false;
        pendingPowerSourceConsumer = null;
        refreshPlacementButtons();
        updateMapToolStatus();
    }

    private void placeTent(Position position) {
        Tent tent = new Tent(planFactory.newId(), placementNameOrDefault(PlacementType.TENT), position);
        tent.setGroupName(placementGroupNameOrDefault());
        tent.setShowMapLabel(pendingPlacementShowMapLabelOrDefault());
        tent.setColorHex(placementColorHexOrDefault(PlacementType.TENT));
        tent.setOpacity(pendingTentOpacityOrDefault());
        tent.setSizeMeters(pendingPlacementWidthMetersOrDefault(), pendingPlacementHeightMetersOrDefault());
        plan.addObject(tent);
        clearPendingPlacementDetails();
        pendingTentPlacement = false;
        refreshPlacementButtons();
        updateMapToolStatus();
        refreshGroupFilters();
        selectObject(tent);
        refreshSummary();
        markDirty();
    }

    private void addPowerSource(PlacementType placementType) {
        pendingPowerSourcePlacement = !pendingPowerSourcePlacement;
        pendingPowerSourcePlacementType = pendingPowerSourcePlacement ? placementType : null;
        pendingTentPlacement = false;
        pendingCustomObjectPlacement = false;
        pendingTextObjectPlacement = false;
        pendingMarkerPlacement = false;
        pendingLineObjectPlacement = false;
        pendingAreaObjectPlacement = false;
        pendingPowerSourceConsumer = null;
        refreshPlacementButtons();
        updateMapToolStatus();
    }

    private void placePowerSource(Position position) {
        PlacementType placementType = pendingPowerSourcePlacementType == PlacementType.DISTRIBUTION_PANEL
                ? PlacementType.DISTRIBUTION_PANEL
                : PlacementType.POWER_SOURCE;
        PowerSource source = placementType == PlacementType.DISTRIBUTION_PANEL
                ? new DistributionPanel(planFactory.newId(), placementNameOrDefault(placementType), position)
                : new PowerSource(planFactory.newId(), placementNameOrDefault(placementType), position);
        source.addOutlet(new PowerOutlet(
                planFactory.newId(),
                ConnectorType.SCHUKO_230V,
                ConnectorType.SCHUKO_230V.defaultCapacityWatts()
        ));
        source.setGroupName(placementGroupNameOrDefault());
        source.setShowMapLabel(pendingPlacementShowMapLabelOrDefault());
        plan.addObject(source);
        clearPendingPlacementDetails();
        pendingPowerSourcePlacement = false;
        pendingPowerSourcePlacementType = null;
        refreshPlacementButtons();
        updateMapToolStatus();
        refreshGroupFilters();
        selectObject(source);
        refreshSummary();
        markDirty();
    }

    private void addCustomObject() {
        pendingCustomObjectPlacement = !pendingCustomObjectPlacement;
        pendingTentPlacement = false;
        pendingPowerSourcePlacement = false;
        pendingTextObjectPlacement = false;
        pendingMarkerPlacement = false;
        pendingLineObjectPlacement = false;
        pendingAreaObjectPlacement = false;
        pendingPowerSourceConsumer = null;
        refreshPlacementButtons();
        updateMapToolStatus();
    }

    private void placeCustomObject(Position position) {
        CustomObject object = new CustomObject(planFactory.newId(), placementNameOrDefault(PlacementType.CUSTOM_OBJECT), position);
        object.setGroupName(placementGroupNameOrDefault());
        object.setShowMapLabel(pendingPlacementShowMapLabelOrDefault());
        object.setColorHex(placementColorHexOrDefault(PlacementType.CUSTOM_OBJECT));
        object.setOpacity(pendingCustomObjectOpacityOrDefault());
        object.setShape(placementShapeOrDefault());
        object.setSizeMeters(pendingCustomObjectWidthMetersOrDefault(), pendingCustomObjectHeightMetersOrDefault());
        plan.addObject(object);
        clearPendingPlacementDetails();
        pendingCustomObjectPlacement = false;
        pendingMarkerPlacement = false;
        pendingLineObjectPlacement = false;
        pendingAreaObjectPlacement = false;
        refreshPlacementButtons();
        updateMapToolStatus();
        refreshGroupFilters();
        selectObject(object);
        refreshSummary();
        markDirty();
    }

    private void addTextObject() {
        pendingTextObjectPlacement = !pendingTextObjectPlacement;
        pendingTentPlacement = false;
        pendingPowerSourcePlacement = false;
        pendingCustomObjectPlacement = false;
        pendingMarkerPlacement = false;
        pendingLineObjectPlacement = false;
        pendingAreaObjectPlacement = false;
        pendingPowerSourceConsumer = null;
        refreshPlacementButtons();
        updateMapToolStatus();
    }

    private void placeTextObject(Position position) {
        TextObject object = new TextObject(planFactory.newId(), placementNameOrDefault(PlacementType.TEXT_OBJECT), position);
        object.setGroupName(placementGroupNameOrDefault());
        object.setShowMapLabel(pendingPlacementShowMapLabelOrDefault());
        object.setColorHex(placementColorHexOrDefault(PlacementType.TEXT_OBJECT));
        object.setFontSize(pendingPlacementFontSizePixelsOrDefault());
        plan.addObject(object);
        clearPendingPlacementDetails();
        pendingTextObjectPlacement = false;
        refreshPlacementButtons();
        updateMapToolStatus();
        refreshGroupFilters();
        selectObject(object);
        refreshSummary();
        markDirty();
    }

    private void addMarkerObject() {
        pendingMarkerPlacement = !pendingMarkerPlacement;
        pendingTentPlacement = false;
        pendingPowerSourcePlacement = false;
        pendingCustomObjectPlacement = false;
        pendingTextObjectPlacement = false;
        pendingLineObjectPlacement = false;
        pendingAreaObjectPlacement = false;
        pendingPowerSourceConsumer = null;
        refreshPlacementButtons();
        updateMapToolStatus();
    }

    private void placeMarkerObject(Position position) {
        MarkerObject object = new MarkerObject(planFactory.newId(), placementNameOrDefault(PlacementType.MARKER_OBJECT), position);
        object.setGroupName(placementGroupNameOrDefault());
        object.setShowMapLabel(pendingPlacementShowMapLabelOrDefault());
        object.setColorHex(placementColorHexOrDefault(PlacementType.MARKER_OBJECT));
        object.setMarkerType(placementMarkerTypeOrDefault());
        plan.addObject(object);
        clearPendingPlacementDetails();
        pendingMarkerPlacement = false;
        refreshPlacementButtons();
        updateMapToolStatus();
        refreshGroupFilters();
        selectObject(object);
        refreshSummary();
        markDirty();
    }

    private void addLineObject() {
        pendingLineObjectPlacement = !pendingLineObjectPlacement;
        pendingTentPlacement = false;
        pendingPowerSourcePlacement = false;
        pendingCustomObjectPlacement = false;
        pendingTextObjectPlacement = false;
        pendingMarkerPlacement = false;
        pendingAreaObjectPlacement = false;
        pendingPowerSourceConsumer = null;
        pendingShapePoints.clear();
        refreshPlacementButtons();
        updateMapToolStatus();
        redrawMap();
    }

    private void placeLineObject(List<Position> points) {
        LineObject object = new LineObject(planFactory.newId(), placementNameOrDefault(PlacementType.LINE_OBJECT), points.getFirst());
        object.setGroupName(placementGroupNameOrDefault());
        object.setShowMapLabel(pendingPlacementShowMapLabelOrDefault());
        object.setColorHex(placementColorHexOrDefault(PlacementType.LINE_OBJECT));
        object.setWidthPixels(pendingLineWidthPixelsOrDefault());
        object.setPoints(points);
        plan.addObject(object);
        clearPendingPlacementDetails();
        pendingLineObjectPlacement = false;
        pendingShapePoints.clear();
        refreshPlacementButtons();
        updateMapToolStatus();
        refreshGroupFilters();
        selectObject(object);
        refreshSummary();
        markDirty();
    }

    private void addAreaObject() {
        pendingAreaObjectPlacement = !pendingAreaObjectPlacement;
        pendingTentPlacement = false;
        pendingPowerSourcePlacement = false;
        pendingCustomObjectPlacement = false;
        pendingTextObjectPlacement = false;
        pendingMarkerPlacement = false;
        pendingLineObjectPlacement = false;
        pendingPowerSourceConsumer = null;
        pendingShapePoints.clear();
        refreshPlacementButtons();
        updateMapToolStatus();
        redrawMap();
    }

    private void placeAreaObject(List<Position> points) {
        AreaObject object = new AreaObject(planFactory.newId(), placementNameOrDefault(PlacementType.AREA_OBJECT), points.getFirst());
        object.setGroupName(placementGroupNameOrDefault());
        object.setShowMapLabel(pendingPlacementShowMapLabelOrDefault());
        object.setColorHex(placementColorHexOrDefault(PlacementType.AREA_OBJECT));
        object.setOpacity(pendingPlacementOpacityOrDefault());
        object.setPoints(points);
        plan.addObject(object);
        clearPendingPlacementDetails();
        pendingAreaObjectPlacement = false;
        pendingShapePoints.clear();
        refreshPlacementButtons();
        updateMapToolStatus();
        refreshGroupFilters();
        selectObject(object);
        refreshSummary();
        markDirty();
    }

    private void addPendingShapePoint(Position point) {
        pendingShapePoints.add(point);
        redrawMap();
        refreshPlacementButtons();
        updateMapToolStatus();
    }

    private boolean isShapePlacementPending() {
        return pendingLineObjectPlacement || pendingAreaObjectPlacement;
    }

    private boolean canFinishPendingShapePlacement() {
        return pendingLineObjectPlacement && pendingShapePoints.size() >= 2
                || pendingAreaObjectPlacement && pendingShapePoints.size() >= 3;
    }

    private void finishPendingShapePlacement() {
        if (!canFinishPendingShapePlacement()) {
            updateMapToolStatus();
            return;
        }
        List<Position> points = List.copyOf(pendingShapePoints);
        if (pendingLineObjectPlacement) {
            placeLineObject(points);
        } else if (pendingAreaObjectPlacement) {
            placeAreaObject(points);
        }
    }

    private String placementNameOrDefault(PlacementType placementType) {
        if (pendingPlacementName == null || pendingPlacementName.isBlank()) {
            return placementType.defaultName();
        }
        return pendingPlacementName;
    }

    private String placementGroupNameOrDefault() {
        if (pendingPlacementGroupName == null || pendingPlacementGroupName.isBlank()) {
            return "Määramata";
        }
        return pendingPlacementGroupName;
    }

    private String placementColorHexOrDefault(PlacementType placementType) {
        if (pendingPlacementColorHex == null || pendingPlacementColorHex.isBlank()) {
            return placementType.defaultColorHex();
        }
        return pendingPlacementColorHex;
    }

    private double pendingPlacementWidthMetersOrDefault() {
        return pendingPlacementWidthMeters == null ? 3.0 : pendingPlacementWidthMeters;
    }

    private double pendingPlacementHeightMetersOrDefault() {
        return pendingPlacementHeightMeters == null ? 3.0 : pendingPlacementHeightMeters;
    }

    private double pendingCustomObjectWidthMetersOrDefault() {
        return pendingPlacementWidthMeters == null ? 1.0 : pendingPlacementWidthMeters;
    }

    private double pendingCustomObjectHeightMetersOrDefault() {
        return pendingPlacementHeightMeters == null ? 1.0 : pendingPlacementHeightMeters;
    }

    private double pendingPlacementOpacityOrDefault() {
        return pendingPlacementOpacity == null ? AreaObject.DEFAULT_OPACITY : pendingPlacementOpacity;
    }

    private double pendingCustomObjectOpacityOrDefault() {
        return pendingPlacementOpacity == null ? CustomObject.DEFAULT_OPACITY : pendingPlacementOpacity;
    }

    private double pendingTentOpacityOrDefault() {
        return pendingPlacementOpacity == null ? Tent.DEFAULT_OPACITY : pendingPlacementOpacity;
    }

    private double pendingLineWidthPixelsOrDefault() {
        return pendingPlacementLineWidthPixels == null
                ? LineObject.DEFAULT_WIDTH_PIXELS
                : pendingPlacementLineWidthPixels;
    }

    private double pendingPlacementFontSizePixelsOrDefault() {
        return pendingPlacementFontSizePixels == null
                ? TextObject.DEFAULT_FONT_SIZE
                : pendingPlacementFontSizePixels;
    }

    private CustomObjectShape placementShapeOrDefault() {
        return pendingPlacementShape == null ? CustomObjectShape.SQUARE : pendingPlacementShape;
    }

    private MarkerType placementMarkerTypeOrDefault() {
        return pendingPlacementMarkerType == null ? MarkerType.WC : pendingPlacementMarkerType;
    }

    private boolean pendingPlacementShowMapLabelOrDefault() {
        return pendingPlacementShowMapLabel == null || pendingPlacementShowMapLabel;
    }

    private void clearPendingPlacementDetails() {
        pendingPlacementName = null;
        pendingPlacementGroupName = null;
        pendingPlacementColorHex = null;
        pendingPlacementWidthMeters = null;
        pendingPlacementHeightMeters = null;
        pendingPlacementOpacity = null;
        pendingPlacementLineWidthPixels = null;
        pendingPlacementFontSizePixels = null;
        pendingPlacementShape = null;
        pendingPlacementMarkerType = null;
        pendingPlacementShowMapLabel = null;
        pendingPowerSourcePlacementType = null;
        pendingShapePoints.clear();
    }

    private void applyPlanName() {
        String planName = planNameField.getText().trim();
        if (planName.isBlank()) {
            showError("Plaani nime ei muudetud", "Sisesta plaani nimi.");
            planNameField.setText(plan.name());
            return;
        }

        plan.rename(planName);
        refreshSummary();
        markDirty();
    }

    private void applyPixelsPerMeter() {
        try {
            double pixelsPerMeter = Double.parseDouble(pixelsPerMeterField.getText().trim().replace(',', '.'));
            plan.setPixelsPerMeter(pixelsPerMeter);
            pixelsPerMeterField.setText(formatMeters(plan.pixelsPerMeter()));
            refreshMeasurementLabels();
            redrawMap();
            refreshSummary();
            refreshDetails();
            refreshObjectList();
            markDirty();
        } catch (NumberFormatException exception) {
            showError("Mõõtkava ei muudetud", "Sisesta pikslite arv meetri kohta arvuna.");
            pixelsPerMeterField.setText(formatMeters(plan.pixelsPerMeter()));
        } catch (IllegalArgumentException exception) {
            showError("Mõõtkava ei muudetud", exception.getMessage());
            pixelsPerMeterField.setText(formatMeters(plan.pixelsPerMeter()));
        }
    }

    private void showPlanSettingsDialog() {
        showPlanSettingsDialog(false);
    }

    private void showNewPlanSettingsDialog() {
        showPlanSettingsDialog(true);
    }

    private void showPlanSettingsDialog(boolean creatingNewPlan) {
        PlanSettingsDialog.Settings initialSettings = new PlanSettingsDialog.Settings(
                plan.name(),
                formatMeters(plan.pixelsPerMeter()),
                plan.objectLabelFontSize(),
                plan.cableLabelFontSize(),
                plan.mapImagePath(),
                DEFAULT_MAP_PATH,
                ORTHOPHOTO_MAP_PATH
        );
        PlanSettingsDialog.show(
                stage,
                initialSettings,
                planFileSession.initialDirectory(),
                MIN_FONT_SIZE_PIXELS,
                MAX_FONT_SIZE_PIXELS,
                () -> setScaleFromLastMeasurement()
                        ? Optional.of(formatMeters(plan.pixelsPerMeter()))
                        : Optional.empty(),
                planFileSession::rememberDirectory,
                creatingNewPlan
        ).ifPresent(settings -> {
            String planName = creatingNewPlan && settings.planName().isBlank()
                    ? plan.name()
                    : settings.planName();
            String pixelsPerMeter = creatingNewPlan && settings.pixelsPerMeterText().isBlank()
                    ? formatMeters(plan.pixelsPerMeter())
                    : settings.pixelsPerMeterText();
            applyPlanSettings(
                    planName,
                    pixelsPerMeter,
                    settings.objectLabelFontSize(),
                    settings.cableLabelFontSize(),
                    settings.mapImagePath()
            );
        });
    }

    private void applyPlanSettings(
            String planName,
            String pixelsPerMeterText,
            double objectLabelFontSize,
            double cableLabelFontSize,
            String mapImagePath
    ) {
        String trimmedPlanName = planName == null ? "" : planName.trim();
        if (trimmedPlanName.isBlank()) {
            showError("Plaani andmeid ei muudetud", "Sisesta plaani nimi.");
            return;
        }

        try {
            double pixelsPerMeter = Double.parseDouble(pixelsPerMeterText.trim().replace(',', '.'));
            plan.rename(trimmedPlanName);
            plan.setPixelsPerMeter(pixelsPerMeter);
            plan.setObjectLabelFontSize(objectLabelFontSize);
            plan.setCableLabelFontSize(cableLabelFontSize);
            if (!plan.mapImagePath().equals(mapImagePath)) {
                plan.setMapImagePath(mapImagePath);
            }
            if (planNameField != null) {
                planNameField.setText(plan.name());
            }
            if (pixelsPerMeterField != null) {
                pixelsPerMeterField.setText(formatMeters(plan.pixelsPerMeter()));
            }
            refreshMeasurementLabels();
            redrawMap();
            refreshSummary();
            refreshDetails();
            refreshObjectList();
            markDirty();
        } catch (NumberFormatException exception) {
            showError("Plaani andmeid ei muudetud", "Sisesta mõõtkava arvuna.");
        } catch (IllegalArgumentException exception) {
            showError("Plaani andmeid ei muudetud", exception.getMessage());
        }
    }

    private void newPlan() {
        if (!confirmDiscardUnsavedChanges()) {
            return;
        }

        plan = planFactory.createEmptyPlan();
        planFileSession.clearCurrentFile();
        resetPlanViewState();
        showNewPlanSettingsDialog();
    }

    private void resetPlanViewState() {
        resetPlanViewState(true);
    }

    private void resetPlanViewState(boolean resetHistory) {
        selectedObject = null;
        pendingPowerSourceConsumer = null;
        clearPendingPlacementDetails();
        pendingTentPlacement = false;
        pendingPowerSourcePlacement = false;
        pendingCustomObjectPlacement = false;
        pendingTextObjectPlacement = false;
        pendingMarkerPlacement = false;
        pendingLineObjectPlacement = false;
        pendingAreaObjectPlacement = false;
        measuringActive = false;
        addingCablePoint = false;
        editingCableConnectionId = null;
        if (measureButton != null) {
            measureButton.setSelected(false);
        }
        if (addCablePointButton != null) {
            addCablePointButton.setSelected(false);
        }
        measurementStart = null;
        measurementNodes.clear();
        measurements.clear();
        visibleGroups.clear();
        knownGroups.clear();
        collapsedObjectGroups.clear();
        collapsedPowerSummaryKeys.clear();
        if (planNameField != null) {
            planNameField.setText(plan.name());
        }
        if (pixelsPerMeterField != null) {
            pixelsPerMeterField.setText(formatMeters(plan.pixelsPerMeter()));
        }
        applyMapLayerControlsFromPlan();
        refreshPlacementButtons();
        updateMapToolStatus();
        refreshGroupFilters();
        refreshObjectList();
        redrawMap();
        refreshSummary();
        refreshDetails();
        if (resetHistory) {
            resetPlanHistory();
        }
    }

    private void markDirty() {
        planDragInProgress = false;
        planDragRecorded = false;
        planHistory.record(planSnapshotService.create(plan));
        planDocumentState.markDirty();
        updateWindowTitle();
    }

    private void beginPlanDrag() {
        planDragInProgress = true;
        planDragRecorded = false;
    }

    private void recordPlanDragChange() {
        PlanSnapshot snapshot = planSnapshotService.create(plan);
        if (!planDragInProgress || !planDragRecorded) {
            planHistory.record(snapshot);
            planDragInProgress = true;
            planDragRecorded = true;
        } else {
            planHistory.replaceCurrent(snapshot);
        }
        planDocumentState.markDirty();
        updateWindowTitle();
    }

    private void markClean() {
        PlanSnapshot snapshot = planSnapshotService.create(plan);
        savedPlanSnapshot = snapshot;
        planHistory.replaceCurrent(snapshot);
        planDocumentState.markClean();
        updateWindowTitle();
    }

    private void resetPlanHistory() {
        PlanSnapshot snapshot = planSnapshotService.create(plan);
        planHistory.reset(snapshot);
        savedPlanSnapshot = snapshot;
        planDocumentState.markClean();
        updateWindowTitle();
    }

    private void updateWindowTitle() {
        if (stage == null) {
            return;
        }
        stage.setTitle(planDocumentState.windowTitle(planFileSession.currentFile()));
        updatePlanTitleLabel();
        updateSaveStatusLabel();
    }

    private void updatePlanTitleLabel() {
        if (planTitleLabel == null || plan == null) {
            return;
        }
        planTitleLabel.setText(plan.name());
    }

    private void updateSaveStatusLabel() {
        if (saveStatusLabel == null) {
            return;
        }
        saveStatusLabel.setText(planDocumentState.saveStatusText());
        saveStatusLabel.setStyle(planDocumentState.hasUnsavedChanges()
                ? "-fx-text-fill: #b45309; -fx-font-weight: bold;"
                : "-fx-text-fill: #166534; -fx-font-weight: bold;");
    }

    private void updateMapToolStatus() {
        if (mapToolStatusLabel == null) {
            return;
        }
        if (pendingPowerSourceConsumer != null) {
            mapToolStatusLabel.setText("Vali kaardilt elektrikapp");
            return;
        }
        if (pendingTentPlacement) {
            mapToolStatusLabel.setText("Paiguta telk kaardile");
            return;
        }
        if (pendingPowerSourcePlacement) {
            mapToolStatusLabel.setText(pendingPowerSourcePlacementType == PlacementType.DISTRIBUTION_PANEL
                    ? "Paiguta alajaotuskilp kaardile"
                    : "Paiguta elektrikapp kaardile");
            return;
        }
        if (pendingCustomObjectPlacement) {
            mapToolStatusLabel.setText("Paiguta objekt kaardile");
            return;
        }
        if (pendingTextObjectPlacement) {
            mapToolStatusLabel.setText("Paiguta tekst kaardile");
            return;
        }
        if (pendingMarkerPlacement) {
            mapToolStatusLabel.setText("Paiguta marker kaardile");
            return;
        }
        if (pendingLineObjectPlacement) {
            mapToolStatusLabel.setText(canFinishPendingShapePlacement()
                    ? "Lisa joone punkte või lõpeta Enteriga"
                    : "Lisa joone punkte kaardile (vähemalt 2)");
            return;
        }
        if (pendingAreaObjectPlacement) {
            mapToolStatusLabel.setText(canFinishPendingShapePlacement()
                    ? "Lisa ala punkte või lõpeta Enteriga"
                    : "Lisa ala punkte kaardile (vähemalt 3)");
            return;
        }
        if (addingCablePoint) {
            mapToolStatusLabel.setText(editingCableConnectionId == null
                    ? "Lisa kaabli punkt kaardile"
                    : "Lisa valitud kaabli punkte kaardile");
            return;
        }
        if (measuringActive) {
            mapToolStatusLabel.setText("Mõõdulint aktiivne");
            return;
        }
        mapToolStatusLabel.setText("Vali tööriist või objekt");
    }

    private boolean confirmDiscardUnsavedChanges() {
        if (!planDocumentState.hasUnsavedChanges()) {
            return true;
        }
        return switch (PlanFileDialogs.confirmUnsavedChanges(stage)) {
            case SAVE -> savePlan();
            case DISCARD -> true;
            case CANCEL -> false;
        };
    }

    private void redrawMap() {
        mapPane.getChildren().clear();
        mapObjectNodes.clear();
        powerConnectionAnchorMarkers.clear();
        addMapImage();
        if (showCables()) {
            drawPowerConnections();
        }
        for (PlannerObject object : plan.objects()) {
            if (object instanceof AreaObject areaObject && isObjectVisibleOnMap(object)) {
                drawAreaObject(areaObject);
            }
        }
        for (PlannerObject object : plan.objects()) {
            if (!isObjectVisibleOnMap(object)) {
                continue;
            }
            if (object instanceof Tent tent) {
                drawTent(tent);
            } else if (object instanceof PowerSource source) {
                drawPowerSource(source);
            } else if (object instanceof TextObject textObject) {
                drawTextObject(textObject);
            } else if (object instanceof MarkerObject markerObject) {
                drawMarkerObject(markerObject);
            } else if (object instanceof LineObject lineObject) {
                drawLineObject(lineObject);
            } else if (object instanceof CustomObject customObject) {
                drawCustomObject(customObject);
            }
        }
        powerConnectionAnchorMarkers.forEach(Node::toFront);
        drawPendingShapePreview();
        mapPane.getChildren().addAll(measurementNodes);
    }

    private void drawPendingShapePreview() {
        if (!isShapePlacementPending() || pendingShapePoints.isEmpty()) {
            return;
        }
        Color color = Color.web(placementColorHexOrDefault(
                pendingAreaObjectPlacement ? PlacementType.AREA_OBJECT : PlacementType.LINE_OBJECT
        ));
        if (pendingAreaObjectPlacement && pendingShapePoints.size() >= 3) {
            Polygon polygon = new Polygon();
            for (Position point : pendingShapePoints) {
                polygon.getPoints().addAll(point.x(), point.y());
            }
            polygon.setFill(Color.web(toHex(color), pendingPlacementOpacityOrDefault()));
            polygon.setStroke(color);
            polygon.setStrokeWidth(2);
            polygon.getStrokeDashArray().addAll(8.0, 6.0);
            polygon.setMouseTransparent(true);
            mapPane.getChildren().add(polygon);
        }
        if (pendingShapePoints.size() >= 2) {
            Polyline line = CablePolylineHelper.create(pendingShapePoints);
            line.setFill(null);
            line.setStroke(color);
            line.setStrokeWidth(pendingLineObjectPlacement ? pendingLineWidthPixelsOrDefault() : 2.0);
            line.getStrokeDashArray().addAll(8.0, 6.0);
            line.setMouseTransparent(true);
            mapPane.getChildren().add(line);
        }
        for (Position point : pendingShapePoints) {
            Circle marker = shapePointHandle(point, color);
            marker.setMouseTransparent(true);
            mapPane.getChildren().add(marker);
        }
    }

    private void addMapImage() {
        Image image = loadImage(plan.mapImagePath());
        if (image == null) {
            return;
        }

        mapImageView.setImage(image);
        mapImageView.setFitWidth(image.getWidth());
        mapWidth = Math.max(MIN_MAP_WIDTH, image.getWidth());
        mapHeight = Math.max(MIN_MAP_HEIGHT, image.getHeight());
        updateZoomContentSize();
        mapPane.getChildren().add(mapImageView);
    }

    private boolean showCables() {
        return showCablesButton == null || showCablesButton.isSelected();
    }

    private boolean showCableLabels() {
        return showCableLabelsButton == null || showCableLabelsButton.isSelected();
    }

    private boolean showCableType(ConnectorType connectorType) {
        return switch (connectorType) {
            case SCHUKO_230V -> show230VCablesButton == null || show230VCablesButton.isSelected();
            case INDUSTRIAL_16A -> show16ACablesButton == null || show16ACablesButton.isSelected();
            case INDUSTRIAL_32A -> show32ACablesButton == null || show32ACablesButton.isSelected();
            case INDUSTRIAL_63A -> show63ACablesButton == null || show63ACablesButton.isSelected();
        };
    }

    private boolean showObjectLabels() {
        return showObjectLabelsButton == null || showObjectLabelsButton.isSelected();
    }

    private boolean isObjectTypeVisible(PlannerObject object) {
        if (object instanceof Tent) {
            return showTentsButton == null || showTentsButton.isSelected();
        }
        if (object instanceof PowerSource) {
            return showPowerSourcesButton == null || showPowerSourcesButton.isSelected();
        }
        if (object instanceof TextObject) {
            return showTextObjectsButton == null || showTextObjectsButton.isSelected();
        }
        if (object instanceof MarkerObject) {
            return showMarkerObjectsButton == null || showMarkerObjectsButton.isSelected();
        }
        if (object instanceof AreaObject) {
            return showAreaObjectsButton == null || showAreaObjectsButton.isSelected();
        }
        if (object instanceof LineObject) {
            return showLineObjectsButton == null || showLineObjectsButton.isSelected();
        }
        if (object instanceof CustomObject) {
            return showCustomObjectsButton == null || showCustomObjectsButton.isSelected();
        }
        return true;
    }

    private HBox cableLegendRow(ConnectorType connectorType) {
        Line sample = new Line(0, 0, 34, 0);
        sample.setStroke(CableDisplayHelper.color(connectorType));
        sample.setStrokeWidth(CableDisplayHelper.width(connectorType));
        if (connectorType == ConnectorType.SCHUKO_230V) {
            sample.getStrokeDashArray().addAll(8.0, 6.0);
        }

        Label label = new Label(CableDisplayHelper.shortTypeName(connectorType));
        HBox row = new HBox(8, sample, label);
        row.setStyle("-fx-alignment: center-left;");
        return row;
    }

    private void drawPowerConnections() {
        for (PlannerObject consumer : plan.objects()) {
            if (!(consumer instanceof PowerConsumer)
                    || !isObjectVisibleOnMap(consumer)) {
                continue;
            }
            plan.findPowerConnectionsForConsumer(consumer.id()).stream()
                    .filter(connection -> showCableType(connection.connectorType()))
                    .map(connection -> plan.findObject(connection.sourceId())
                            .filter(PowerSource.class::isInstance)
                            .map(PowerSource.class::cast)
                            .filter(this::isObjectVisibleOnMap)
                            .map(source -> new PowerCableView(consumer, source, connection)))
                    .flatMap(Optional::stream)
                    .forEach(this::drawPowerConnection);
        }
    }

    private void drawPowerConnection(PowerCableView cable) {
        List<Position> path = cablePath(cable);
        Color cableColor = CableDisplayHelper.color(cable.connection().connectorType());
        boolean selectedCable = cable.connection().id().equals(selectedPowerConnectionId())
                || addingCablePoint && cable.connection().id().equals(editingCableConnectionId);
        double strokeWidth = CableDisplayHelper.width(cable.connection().connectorType()) + (selectedCable ? 2.0 : 0.0);

        Polyline line = CablePolylineHelper.create(path);
        line.setStroke(cableColor);
        line.setStrokeWidth(strokeWidth);
        line.setOpacity(selectedCable ? 1.0 : 0.85);
        line.setMouseTransparent(true);
        if (cable.connection().connectorType() == ConnectorType.SCHUKO_230V) {
            line.getStrokeDashArray().addAll(8.0, 6.0);
        }

        Polyline highlightLine = CablePolylineHelper.create(path);
        highlightLine.setStroke(Color.web("#111827"));
        highlightLine.setStrokeWidth(strokeWidth + 4.0);
        highlightLine.setOpacity(selectedCable ? 0.28 : 0);
        highlightLine.setMouseTransparent(true);
        if (cable.connection().connectorType() == ConnectorType.SCHUKO_230V) {
            highlightLine.getStrokeDashArray().addAll(8.0, 6.0);
        }

        Polyline hitLine = CablePolylineHelper.create(path);
        hitLine.setStroke(Color.TRANSPARENT);
        hitLine.setStrokeWidth(Math.max(12.0, strokeWidth + 8.0));
        makeCableSelectable(hitLine, cable);

        mapPane.getChildren().addAll(highlightLine, line, hitLine);
        Label distanceLabel = null;
        if (selectedCable || showCableLabels() && plan.showCableLabel(cable.connection().id())) {
            Position labelPosition = CableDisplayHelper.labelPosition(cable.connection(), path);
            distanceLabel = new Label(CableDisplayHelper.mapLabel(
                    cable.connection(),
                    CableDisplayHelper.lengthMeters(path, pixelsPerMeter())
            ));
            distanceLabel.setStyle("-fx-background-color: rgba(255,255,255,%s); -fx-padding: 2 5 2 5; -fx-border-color: %s; -fx-font-weight: %s; -fx-font-size: %spx;".formatted(
                    selectedCable ? "0.96" : "0.88",
                    toHex(selectedCable ? Color.web("#111827") : cableColor),
                    selectedCable ? "bold" : "normal",
                    Double.toString(plan.cableLabelFontSize())
            ));
            distanceLabel.setLayoutX(labelPosition.x());
            distanceLabel.setLayoutY(labelPosition.y());
            makeCableSelectable(distanceLabel, cable);
            makeCableLabelDraggable(distanceLabel, cable);
            mapPane.getChildren().add(distanceLabel);
        }
        if (selectedCable && !mapLayoutLocked) {
            for (int index = 0; index < cable.connection().routePoints().size(); index++) {
                Position routePoint = cable.connection().routePoints().get(index);
                Circle marker = new Circle(routePoint.x(), routePoint.y(), 4);
                marker.setFill(Color.WHITE);
                marker.setStroke(cableColor);
                marker.setStrokeWidth(2);
                Tooltip.install(marker, new Tooltip("Lohista punkti muutmiseks, paremklõps avab valikud"));
                makeCableSelectable(marker, cable.consumer());
                makeCableRoutePointDraggable(marker, cable, index, line, highlightLine, hitLine, distanceLabel);
                mapPane.getChildren().add(marker);
            }
            if (cable.consumer() instanceof PowerConnectable) {
                Position endpoint = path.getLast();
                Circle anchorMarker = new Circle(endpoint.x(), endpoint.y(), 6);
                anchorMarker.setFill(Color.web("#fef3c7"));
                anchorMarker.setStroke(Color.web("#111827"));
                anchorMarker.setStrokeWidth(2);
                Tooltip.install(anchorMarker, new Tooltip("Lohista voolu ühenduspunkti, paremklõps lähtestab"));
                makePowerConnectionAnchorDraggable(
                        anchorMarker,
                        cable,
                        line,
                        highlightLine,
                        hitLine,
                        distanceLabel
                );
                mapPane.getChildren().add(anchorMarker);
                powerConnectionAnchorMarkers.add(anchorMarker);
            }
        }
    }

    private List<Position> cablePath(PowerCableView cable) {
        return CablePathHelper.cablePath(cable.consumer(), cable.source(), cable.connection(), pixelsPerMeter());
    }

    private List<Position> cablePath(PlannerObject consumer, PowerSource source, PowerConnection connection) {
        return cablePath(new PowerCableView(consumer, source, connection));
    }

    private void makeCableSelectable(Node node, PlannerObject consumer) {
        makeCableSelectable(node, new PowerCableView(consumer, null, null));
    }

    private void makeCableSelectable(Node node, PowerCableView cable) {
        if (cable.connection() != null) {
            node.setOnContextMenuRequested(event -> {
                if (!isPlacementPending() && !measuringActive) {
                    showCableContextMenu(cable, event.getScreenX(), event.getScreenY());
                }
                event.consume();
            });
        }
        node.setOnMouseClicked(event -> {
            PlannerObject consumer = cable.consumer();
            if (pendingTentPlacement) {
                Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
                placeTent(new Position(mapPoint.getX(), mapPoint.getY()));
                event.consume();
                return;
            }
            if (pendingPowerSourcePlacement) {
                Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
                placePowerSource(new Position(mapPoint.getX(), mapPoint.getY()));
                event.consume();
                return;
            }
            if (pendingCustomObjectPlacement) {
                Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
                placeCustomObject(new Position(mapPoint.getX(), mapPoint.getY()));
                event.consume();
                return;
            }
            if (pendingTextObjectPlacement) {
                Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
                placeTextObject(new Position(mapPoint.getX(), mapPoint.getY()));
                event.consume();
                return;
            }
            if (pendingMarkerPlacement) {
                Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
                placeMarkerObject(new Position(mapPoint.getX(), mapPoint.getY()));
                event.consume();
                return;
            }
            if (pendingLineObjectPlacement) {
                Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
                addPendingShapePoint(new Position(mapPoint.getX(), mapPoint.getY()));
                event.consume();
                return;
            }
            if (pendingAreaObjectPlacement) {
                Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
                addPendingShapePoint(new Position(mapPoint.getX(), mapPoint.getY()));
                event.consume();
                return;
            }
            if (addingCablePoint) {
                Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
                Position point = new Position(mapPoint.getX(), mapPoint.getY());
                if (editingCableConnectionId != null) {
                    addCableRoutePoint(point);
                } else if (cable.source() == null || cable.connection() == null) {
                    addCableRoutePoint(point);
                } else {
                    if (!isSelected(consumer)) {
                        selectObject(consumer);
                        event.consume();
                        return;
                    }
                    insertCableRoutePoint(cable, point);
                }
                event.consume();
                return;
            }
            if (measuringActive) {
                Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
                handleMeasureClick(new Position(mapPoint.getX(), mapPoint.getY()));
                event.consume();
                return;
            }
            selectObject(consumer);
            if (cable.connection() != null) {
                selectPowerConnection(cable.connection().id());
            }
            event.consume();
        });
    }

    private void showCableContextMenu(PowerCableView cable, double screenX, double screenY) {
        boolean editingThisCable = addingCablePoint
                && cable.connection().id().equals(editingCableConnectionId);
        MenuItem routeItem = new MenuItem(editingThisCable
                ? "Lõpeta trajektoori muutmine"
                : "Muuda trajektoori");
        routeItem.setDisable(mapLayoutLocked);
        routeItem.setOnAction(event -> {
            if (editingThisCable) {
                finishEditingCableRoute();
            } else {
                startEditingCableRoute(cable);
            }
        });
        showContextMenu(new ContextMenu(routeItem), mapPane, screenX, screenY);
    }

    private void startEditingCableRoute(PowerCableView cable) {
        if (mapLayoutLocked) {
            showMapLayoutLockedMessage();
            return;
        }
        selectObject(cable.consumer());
        selectPowerConnection(cable.connection().id());
        editingCableConnectionId = cable.connection().id();
        if (addCablePointButton != null) {
            addCablePointButton.setSelected(true);
        }
        setAddingCablePoint(true);
        redrawMap();
    }

    private void finishEditingCableRoute() {
        setAddingCablePoint(false);
        if (addCablePointButton != null) {
            addCablePointButton.setSelected(false);
        }
        redrawMap();
    }

    private void makeCableLabelDraggable(Label label, PowerCableView cable) {
        final Delta dragDelta = new Delta();
        final boolean[] dragged = {false};
        label.setOnMousePressed(event -> {
            if (measuringActive || addingCablePoint || mapLayoutLocked) {
                event.consume();
                return;
            }
            selectedObject = cable.consumer();
            refreshDetails();
            dragged[0] = false;
            Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
            dragDelta.x = mapPoint.getX() - label.getLayoutX();
            dragDelta.y = mapPoint.getY() - label.getLayoutY();
            event.consume();
        });
        label.setOnMouseDragged(event -> {
            if (measuringActive || addingCablePoint || mapLayoutLocked) {
                event.consume();
                return;
            }
            Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
            double labelX = mapPoint.getX() - dragDelta.x;
            double labelY = mapPoint.getY() - dragDelta.y;
            label.setLayoutX(labelX);
            label.setLayoutY(labelY);
            Position defaultPosition = CableDisplayHelper.defaultLabelPosition(cablePath(cable));
            plan.updateCableLabelOffsetForConnection(cable.connection().id(), new Position(
                    labelX - defaultPosition.x(),
                    labelY - defaultPosition.y()
            ));
            dragged[0] = true;
            event.consume();
        });
        label.setOnMouseReleased(event -> {
            if (dragged[0]) {
                redrawMap();
                refreshSummary();
                markDirty();
            }
            event.consume();
        });
    }

    private void makeCableRoutePointDraggable(
            Circle marker,
            PowerCableView cable,
            int routePointIndex,
            Polyline line,
            Polyline highlightLine,
            Polyline hitLine,
            Label distanceLabel
    ) {
        final boolean[] dragged = {false};
        marker.setOnMousePressed(event -> {
            if (measuringActive || mapLayoutLocked || !canDragCableRoutePoint(cable)) {
                event.consume();
                return;
            }
            dragged[0] = false;
            event.consume();
        });
        marker.setOnMouseDragged(event -> {
            if (measuringActive || mapLayoutLocked || !canDragCableRoutePoint(cable)) {
                event.consume();
                return;
            }
            Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
            Position updatedPoint = new Position(mapPoint.getX(), mapPoint.getY());
            List<Position> routePoints = CableRouteEditor.replacePoint(
                    plan,
                    cable.connection().id(),
                    routePointIndex,
                    updatedPoint
            ).orElse(null);
            if (routePoints == null) {
                event.consume();
                return;
            }
            marker.setCenterX(mapPoint.getX());
            marker.setCenterY(mapPoint.getY());
            CablePolylineHelper.updateRoutePoint(line, routePointIndex, mapPoint);
            CablePolylineHelper.updateRoutePoint(highlightLine, routePointIndex, mapPoint);
            CablePolylineHelper.updateRoutePoint(hitLine, routePointIndex, mapPoint);
            updateCableLabel(distanceLabel, cable, routePoints);
            dragged[0] = true;
            event.consume();
        });
        marker.setOnMouseReleased(event -> {
            if (dragged[0]) {
                redrawMap();
                refreshSummary();
                markDirty();
            }
            event.consume();
        });
        marker.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                showCableRoutePointContextMenu(marker, cable, routePointIndex, event.getScreenX(), event.getScreenY());
                event.consume();
                return;
            }
            if (!dragged[0]) {
                selectObject(cable.consumer());
            }
            event.consume();
        });
    }

    private boolean canDragCableRoutePoint(PowerCableView cable) {
        return !addingCablePoint
                || cable.connection().id().equals(editingCableConnectionId);
    }

    private void makePowerConnectionAnchorDraggable(
            Circle marker,
            PowerCableView cable,
            Polyline line,
            Polyline highlightLine,
            Polyline hitLine,
            Label distanceLabel
    ) {
        final boolean[] dragged = {false};
        marker.setOnMousePressed(event -> {
            if (measuringActive || addingCablePoint || mapLayoutLocked) {
                event.consume();
                return;
            }
            dragged[0] = false;
            event.consume();
        });
        marker.setOnMouseDragged(event -> {
            if (measuringActive || addingCablePoint || mapLayoutLocked) {
                event.consume();
                return;
            }
            if (!(cable.consumer() instanceof PowerConnectable connectable)) {
                event.consume();
                return;
            }
            Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
            Position defaultPoint = CablePathHelper.objectCenter(cable.consumer(), pixelsPerMeter());
            connectable.setPowerConnectionOffset(new Position(
                    mapPoint.getX() - defaultPoint.x(),
                    mapPoint.getY() - defaultPoint.y()
            ));
            marker.setCenterX(mapPoint.getX());
            marker.setCenterY(mapPoint.getY());
            CablePolylineHelper.updateLastPoint(line, mapPoint);
            CablePolylineHelper.updateLastPoint(highlightLine, mapPoint);
            CablePolylineHelper.updateLastPoint(hitLine, mapPoint);
            updateCableLabel(distanceLabel, cable, cable.connection().routePoints());
            dragged[0] = true;
            event.consume();
        });
        marker.setOnMouseReleased(event -> {
            if (dragged[0]) {
                redrawMap();
                refreshSummary();
                markDirty();
            }
            event.consume();
        });
        marker.setOnMouseClicked(event -> {
            if (measuringActive || addingCablePoint) {
                event.consume();
                return;
            }
            if (event.getButton() == MouseButton.SECONDARY) {
                showPowerConnectionAnchorContextMenu(marker, cable, event.getScreenX(), event.getScreenY());
            } else if (!dragged[0]) {
                selectObject(cable.consumer());
            }
            event.consume();
        });
    }

    private void showPowerConnectionAnchorContextMenu(
            Circle marker,
            PowerCableView cable,
            double screenX,
            double screenY
    ) {
        MenuItem resetItem = new MenuItem("Lähtesta ühenduspunkt");
        resetItem.setDisable(mapLayoutLocked);
        resetItem.setOnAction(event -> resetPowerConnectionAnchor(cable.consumer()));
        showContextMenu(new ContextMenu(resetItem), marker, screenX, screenY);
    }

    private void resetPowerConnectionAnchor(PlannerObject consumer) {
        if (mapLayoutLocked) {
            showMapLayoutLockedMessage();
            return;
        }
        if (!(consumer instanceof PowerConnectable connectable)) {
            return;
        }
        connectable.resetPowerConnectionOffset();
        redrawMap();
        refreshSummary();
        markDirty();
    }

    private void showCableRoutePointContextMenu(
            Circle marker,
            PowerCableView cable,
            int routePointIndex,
            double screenX,
            double screenY
    ) {
        MenuItem removePointItem = new MenuItem("Eemalda punkt");
        removePointItem.setDisable(mapLayoutLocked);
        removePointItem.setOnAction(event -> removeCableRoutePoint(cable, routePointIndex));

        ContextMenu contextMenu = new ContextMenu(removePointItem);
        showContextMenu(contextMenu, marker, screenX, screenY);
    }

    private void removeCableRoutePoint(PowerCableView cable, int routePointIndex) {
        if (mapLayoutLocked) {
            showMapLayoutLockedMessage();
            return;
        }
        if (!CableRouteEditor.removePoint(plan, cable.connection().id(), routePointIndex)) {
            return;
        }
        redrawMap();
        refreshSummary();
        markDirty();
    }

    private void updateCableLabel(Label distanceLabel, PowerCableView cable, List<Position> routePoints) {
        if (distanceLabel == null) {
            return;
        }
        List<Position> path = CablePathHelper.cablePath(
                cable.consumer(),
                cable.source(),
                cable.connection(),
                routePoints,
                pixelsPerMeter()
        );

        Position labelPosition = CableDisplayHelper.labelPosition(cable.connection(), path);
        distanceLabel.setText(CableDisplayHelper.mapLabel(
                cable.connection(),
                CableDisplayHelper.lengthMeters(path, pixelsPerMeter())
        ));
        distanceLabel.setLayoutX(labelPosition.x());
        distanceLabel.setLayoutY(labelPosition.y());
    }

    private record PowerCableView(PlannerObject consumer, PowerSource source, PowerConnection connection) {
    }

    private Image loadImage(String imagePath) {
        if (plan.hasPackagedMapImage()) {
            try (InputStream input = new ByteArrayInputStream(plan.packagedMapImage())) {
                return new Image(input);
            } catch (RuntimeException | IOException exception) {
                return null;
            }
        }
        if (imagePath == null || imagePath.isBlank()) {
            return null;
        }

        try {
            if (imagePath.startsWith("classpath:")) {
                String resourcePath = imagePath.substring("classpath:".length());
                try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
                    return inputStream == null ? null : new Image(inputStream);
                }
            }

            File imageFile = new File(imagePath);
            return imageFile.exists() ? new Image(imageFile.toURI().toString()) : null;
        } catch (RuntimeException | IOException exception) {
            return null;
        }
    }

    private void drawTent(Tent tent) {
        double widthPixels = metersToPixels(tent.widthMeters());
        double heightPixels = metersToPixels(tent.heightMeters());
        Position rotationOffset = rotationOffset(widthPixels, heightPixels, tent.rotationDegrees());
        Rectangle rectangle = new Rectangle(
                tent.position().x(),
                tent.position().y(),
                widthPixels,
                heightPixels
        );
        rectangle.setRotate(tent.rotationDegrees());
        rectangle.setTranslateX(rotationOffset.x());
        rectangle.setTranslateY(rotationOffset.y());
        rectangle.setArcWidth(4);
        rectangle.setArcHeight(4);
        rectangle.setFill(Color.web(tent.colorHex(), tent.opacity()));
        rectangle.setStroke(Color.web("#222222"));
        rectangle.setStrokeWidth(isSelected(tent) ? 4 : 1);
        applyLockedStroke(rectangle, tent);
        makeSelectable(rectangle, tent);
        makeDraggable(rectangle, tent);

        mapPane.getChildren().add(rectangle);
        addMapLabel(tent, tent.position().x(), tent.position().y() - 24);
    }

    private void drawPowerSource(PowerSource source) {
        Circle circle = new Circle(source.position().x(), source.position().y(), 12);
        circle.setFill(Color.web("#2563eb"));
        circle.setStroke(Color.web("#111827"));
        circle.setStrokeWidth(isSelected(source) ? 4 : 1);
        applyLockedStroke(circle, source);
        makeSelectable(circle, source);
        makeDraggable(circle, source);

        mapPane.getChildren().add(circle);
        addMapLabel(source, source.position().x() + 16, source.position().y() - 12);
    }

    private void drawCustomObject(CustomObject object) {
        javafx.scene.shape.Shape shape;
        double widthPixels = metersToPixels(object.widthMeters());
        double heightPixels = metersToPixels(object.heightMeters());
        if (object.shape() == CustomObjectShape.CIRCLE) {
            shape = new Circle(object.position().x(), object.position().y(), widthPixels / 2);
        } else {
            Rectangle rectangle = new Rectangle(
                    object.position().x() - widthPixels / 2,
                    object.position().y() - heightPixels / 2,
                    widthPixels,
                    heightPixels
            );
            rectangle.setArcWidth(4);
            rectangle.setArcHeight(4);
            rectangle.setRotate(object.rotationDegrees());
            shape = rectangle;
        }
        shape.setFill(Color.web(object.colorHex(), object.opacity()));
        shape.setStroke(Color.web("#111827"));
        shape.setStrokeWidth(isSelected(object) ? 4 : 1);
        applyLockedStroke(shape, object);
        makeSelectable(shape, object);
        makeDraggable(shape, object);

        mapPane.getChildren().add(shape);
        addMapLabel(object, object.position().x() + 16, object.position().y() - 12);
    }

    private void drawAreaObject(AreaObject object) {
        if (object.points().size() < 3) {
            return;
        }
        Polygon polygon = new Polygon();
        for (Position point : object.points()) {
            polygon.getPoints().addAll(point.x(), point.y());
        }
        polygon.setFill(Color.web(object.colorHex(), object.opacity()));
        polygon.setStroke(Color.web(object.colorHex()));
        polygon.setStrokeWidth(isSelected(object) ? 4 : 1.5);
        applyLockedStroke(polygon, object);
        makeSelectable(polygon, object);
        makeDraggable(polygon, object);

        mapPane.getChildren().add(polygon);
        Position labelPosition = averagePosition(object.points());
        addMapLabel(object, labelPosition.x() + 8, labelPosition.y() + 8);
        if (isSelected(object) && !mapLayoutLocked) {
            addAreaMidpointHandles(object, polygon);
            addAreaPointHandles(object, polygon);
        }
    }

    private void drawLineObject(LineObject object) {
        if (object.points().size() < 2) {
            return;
        }
        Polyline polyline = CablePolylineHelper.create(object.points());
        polyline.setFill(null);
        polyline.setStroke(Color.web(object.colorHex()));
        polyline.setStrokeWidth(object.widthPixels() + (isSelected(object) ? 2.0 : 0.0));
        polyline.setOpacity(isSelected(object) ? 1.0 : 0.9);
        applyLockedStroke(polyline, object);
        makeSelectable(polyline, object);
        makeDraggable(polyline, object);

        mapPane.getChildren().add(polyline);
        Position labelPosition = averagePosition(object.points());
        addMapLabel(object, labelPosition.x() + 8, labelPosition.y() + 8);
        if (isSelected(object) && !mapLayoutLocked) {
            addLineMidpointHandles(object, polyline);
            addLinePointHandles(object, polyline);
        }
    }

    private void addAreaMidpointHandles(AreaObject object, Polygon polygon) {
        for (int index = 0; index < object.points().size(); index++) {
            Position start = object.points().get(index);
            Position end = object.points().get((index + 1) % object.points().size());
            Circle marker = shapeMidpointHandle(midpoint(start, end), Color.web(object.colorHex()));
            Tooltip.install(marker, new Tooltip("Lohista siit uue ala punkti lisamiseks"));
            makeAreaMidpointDraggable(marker, object, index + 1, polygon);
            mapPane.getChildren().add(marker);
        }
    }

    private void addLineMidpointHandles(LineObject object, Polyline polyline) {
        for (int index = 0; index < object.points().size() - 1; index++) {
            Position start = object.points().get(index);
            Position end = object.points().get(index + 1);
            Circle marker = shapeMidpointHandle(midpoint(start, end), Color.web(object.colorHex()));
            Tooltip.install(marker, new Tooltip("Lohista siit uue joone punkti lisamiseks"));
            makeLineMidpointDraggable(marker, object, index + 1, polyline);
            mapPane.getChildren().add(marker);
        }
    }

    private void addAreaPointHandles(AreaObject object, Polygon polygon) {
        for (int index = 0; index < object.points().size(); index++) {
            Position point = object.points().get(index);
            Circle marker = shapePointHandle(point, Color.web(object.colorHex()));
            Tooltip.install(marker, new Tooltip("Lohista ala punkti muutmiseks"));
            makeAreaPointDraggable(marker, object, index, polygon);
            mapPane.getChildren().add(marker);
        }
    }

    private void addLinePointHandles(LineObject object, Polyline polyline) {
        for (int index = 0; index < object.points().size(); index++) {
            Position point = object.points().get(index);
            Circle marker = shapePointHandle(point, Color.web(object.colorHex()));
            Tooltip.install(marker, new Tooltip("Lohista joone punkti muutmiseks"));
            makeLinePointDraggable(marker, object, index, polyline);
            mapPane.getChildren().add(marker);
        }
    }

    private Circle shapePointHandle(Position point, Color color) {
        Circle marker = new Circle(point.x(), point.y(), 5);
        marker.setFill(Color.WHITE);
        marker.setStroke(color);
        marker.setStrokeWidth(2.5);
        return marker;
    }

    private Circle shapeMidpointHandle(Position point, Color color) {
        Circle marker = new Circle(point.x(), point.y(), 4);
        marker.setFill(Color.web(toHex(color), 0.45));
        marker.setStroke(color);
        marker.setStrokeWidth(1.5);
        marker.setCursor(Cursor.CROSSHAIR);
        return marker;
    }

    private void makeAreaPointDraggable(Circle marker, AreaObject object, int pointIndex, Polygon polygon) {
        final boolean[] dragged = {false};
        marker.setOnMousePressed(event -> {
            if (measuringActive || addingCablePoint) {
                event.consume();
                return;
            }
            dragged[0] = false;
            event.consume();
        });
        marker.setOnMouseDragged(event -> {
            if (measuringActive || addingCablePoint || mapLayoutLocked || object.locked()) {
                event.consume();
                return;
            }
            Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
            Position updatedPoint = new Position(mapPoint.getX(), mapPoint.getY());
            object.setPoints(replacePoint(object.points(), pointIndex, updatedPoint));
            marker.setCenterX(updatedPoint.x());
            marker.setCenterY(updatedPoint.y());
            updatePolygonPoint(polygon, pointIndex, updatedPoint);
            refreshAreaMeasurements(object);
            dragged[0] = true;
            event.consume();
        });
        marker.setOnMouseReleased(event -> {
            if (dragged[0]) {
                refreshEditedShapeObject();
            }
            event.consume();
        });
        marker.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                showAreaPointContextMenu(marker, object, pointIndex, event.getScreenX(), event.getScreenY());
                event.consume();
                return;
            }
            event.consume();
        });
    }

    private void makeAreaMidpointDraggable(Circle marker, AreaObject object, int insertIndex, Polygon polygon) {
        final boolean[] inserted = {false};
        final boolean[] dragged = {false};
        marker.setOnMousePressed(event -> {
            if (measuringActive || addingCablePoint || mapLayoutLocked || object.locked()) {
                event.consume();
                return;
            }
            inserted[0] = false;
            dragged[0] = false;
            event.consume();
        });
        marker.setOnMouseDragged(event -> {
            if (measuringActive || addingCablePoint || mapLayoutLocked || object.locked()) {
                event.consume();
                return;
            }
            Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
            Position updatedPoint = new Position(mapPoint.getX(), mapPoint.getY());
            if (!inserted[0]) {
                object.setPoints(insertPoint(object.points(), insertIndex, updatedPoint));
                insertPolygonPoint(polygon, insertIndex, updatedPoint);
                inserted[0] = true;
            } else {
                object.setPoints(replacePoint(object.points(), insertIndex, updatedPoint));
                updatePolygonPoint(polygon, insertIndex, updatedPoint);
            }
            marker.setCenterX(updatedPoint.x());
            marker.setCenterY(updatedPoint.y());
            refreshAreaMeasurements(object);
            dragged[0] = true;
            event.consume();
        });
        marker.setOnMouseReleased(event -> {
            if (dragged[0]) {
                refreshEditedShapeObject();
            }
            event.consume();
        });
        marker.setOnMouseClicked(event -> event.consume());
    }

    private void makeLinePointDraggable(Circle marker, LineObject object, int pointIndex, Polyline polyline) {
        final boolean[] dragged = {false};
        marker.setOnMousePressed(event -> {
            if (measuringActive || addingCablePoint) {
                event.consume();
                return;
            }
            dragged[0] = false;
            event.consume();
        });
        marker.setOnMouseDragged(event -> {
            if (measuringActive || addingCablePoint || mapLayoutLocked || object.locked()) {
                event.consume();
                return;
            }
            Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
            Position updatedPoint = new Position(mapPoint.getX(), mapPoint.getY());
            object.setPoints(replacePoint(object.points(), pointIndex, updatedPoint));
            marker.setCenterX(updatedPoint.x());
            marker.setCenterY(updatedPoint.y());
            updatePolylinePoint(polyline, pointIndex, updatedPoint);
            refreshLineLengthLabel(object);
            dragged[0] = true;
            event.consume();
        });
        marker.setOnMouseReleased(event -> {
            if (dragged[0]) {
                refreshEditedShapeObject();
            }
            event.consume();
        });
        marker.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                showLinePointContextMenu(marker, object, pointIndex, event.getScreenX(), event.getScreenY());
                event.consume();
                return;
            }
            event.consume();
        });
    }

    private void makeLineMidpointDraggable(Circle marker, LineObject object, int insertIndex, Polyline polyline) {
        final boolean[] inserted = {false};
        final boolean[] dragged = {false};
        marker.setOnMousePressed(event -> {
            if (measuringActive || addingCablePoint || mapLayoutLocked || object.locked()) {
                event.consume();
                return;
            }
            inserted[0] = false;
            dragged[0] = false;
            event.consume();
        });
        marker.setOnMouseDragged(event -> {
            if (measuringActive || addingCablePoint || mapLayoutLocked || object.locked()) {
                event.consume();
                return;
            }
            Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
            Position updatedPoint = new Position(mapPoint.getX(), mapPoint.getY());
            if (!inserted[0]) {
                object.setPoints(insertPoint(object.points(), insertIndex, updatedPoint));
                insertPolylinePoint(polyline, insertIndex, updatedPoint);
                inserted[0] = true;
            } else {
                object.setPoints(replacePoint(object.points(), insertIndex, updatedPoint));
                updatePolylinePoint(polyline, insertIndex, updatedPoint);
            }
            marker.setCenterX(updatedPoint.x());
            marker.setCenterY(updatedPoint.y());
            refreshLineLengthLabel(object);
            dragged[0] = true;
            event.consume();
        });
        marker.setOnMouseReleased(event -> {
            if (dragged[0]) {
                refreshEditedShapeObject();
            }
            event.consume();
        });
        marker.setOnMouseClicked(event -> event.consume());
    }

    private void showAreaPointContextMenu(
            Circle marker,
            AreaObject object,
            int pointIndex,
            double screenX,
            double screenY
    ) {
        MenuItem removePointItem = new MenuItem("Eemalda punkt");
        removePointItem.setOnAction(event -> removeAreaPoint(object, pointIndex));
        removePointItem.setDisable(mapLayoutLocked || object.locked() || object.points().size() <= 3);

        ContextMenu contextMenu = new ContextMenu(removePointItem);
        showContextMenu(contextMenu, marker, screenX, screenY);
    }

    private void showLinePointContextMenu(
            Circle marker,
            LineObject object,
            int pointIndex,
            double screenX,
            double screenY
    ) {
        MenuItem removePointItem = new MenuItem("Eemalda punkt");
        removePointItem.setOnAction(event -> removeLinePoint(object, pointIndex));
        removePointItem.setDisable(mapLayoutLocked || object.locked() || object.points().size() <= 2);

        ContextMenu contextMenu = new ContextMenu(removePointItem);
        showContextMenu(contextMenu, marker, screenX, screenY);
    }

    private void removeAreaPoint(AreaObject object, int pointIndex) {
        if (mapLayoutLocked) {
            showMapLayoutLockedMessage();
            return;
        }
        if (object.points().size() <= 3) {
            return;
        }
        object.setPoints(removePoint(object.points(), pointIndex));
        refreshEditedShapeObject();
    }

    private void removeLinePoint(LineObject object, int pointIndex) {
        if (mapLayoutLocked) {
            showMapLayoutLockedMessage();
            return;
        }
        if (object.points().size() <= 2) {
            return;
        }
        object.setPoints(removePoint(object.points(), pointIndex));
        refreshEditedShapeObject();
    }

    private void refreshEditedShapeObject() {
        redrawMap();
        refreshSummary();
        refreshDetails();
        refreshObjectList();
        markDirty();
    }

    private List<Position> replacePoint(List<Position> points, int pointIndex, Position updatedPoint) {
        List<Position> updatedPoints = new ArrayList<>(points);
        if (pointIndex >= 0 && pointIndex < updatedPoints.size()) {
            updatedPoints.set(pointIndex, updatedPoint);
        }
        return updatedPoints;
    }

    private List<Position> insertPoint(List<Position> points, int pointIndex, Position point) {
        List<Position> updatedPoints = new ArrayList<>(points);
        int safeIndex = Math.max(0, Math.min(pointIndex, updatedPoints.size()));
        updatedPoints.add(safeIndex, point);
        return updatedPoints;
    }

    private List<Position> removePoint(List<Position> points, int pointIndex) {
        List<Position> updatedPoints = new ArrayList<>(points);
        if (pointIndex >= 0 && pointIndex < updatedPoints.size()) {
            updatedPoints.remove(pointIndex);
        }
        return updatedPoints;
    }

    private Position midpoint(Position first, Position second) {
        return new Position(
                (first.x() + second.x()) / 2.0,
                (first.y() + second.y()) / 2.0
        );
    }

    private void updatePolygonPoint(Polygon polygon, int pointIndex, Position point) {
        int coordinateIndex = pointIndex * 2;
        if (coordinateIndex + 1 >= polygon.getPoints().size()) {
            return;
        }
        polygon.getPoints().set(coordinateIndex, point.x());
        polygon.getPoints().set(coordinateIndex + 1, point.y());
    }

    private void insertPolygonPoint(Polygon polygon, int pointIndex, Position point) {
        int coordinateIndex = pointIndex * 2;
        if (coordinateIndex < 0 || coordinateIndex > polygon.getPoints().size()) {
            return;
        }
        polygon.getPoints().add(coordinateIndex, point.x());
        polygon.getPoints().add(coordinateIndex + 1, point.y());
    }

    private void updatePolylinePoint(Polyline polyline, int pointIndex, Position point) {
        int coordinateIndex = pointIndex * 2;
        if (coordinateIndex + 1 >= polyline.getPoints().size()) {
            return;
        }
        polyline.getPoints().set(coordinateIndex, point.x());
        polyline.getPoints().set(coordinateIndex + 1, point.y());
    }

    private void insertPolylinePoint(Polyline polyline, int pointIndex, Position point) {
        int coordinateIndex = pointIndex * 2;
        if (coordinateIndex < 0 || coordinateIndex > polyline.getPoints().size()) {
            return;
        }
        polyline.getPoints().add(coordinateIndex, point.x());
        polyline.getPoints().add(coordinateIndex + 1, point.y());
    }

    private void drawTextObject(TextObject object) {
        VBox textBox = new VBox(3);
        textBox.setLayoutX(object.position().x());
        textBox.setLayoutY(object.position().y());
        textBox.setMaxWidth(260);
        textBox.setStyle("""
                -fx-background-color: rgba(255,255,255,0.88);
                -fx-border-color: %s;
                -fx-border-width: %s;
                -fx-background-radius: 4;
                -fx-border-radius: 4;
                -fx-padding: 4 7 5 7;
                """.formatted(object.colorHex(), isSelected(object) ? "2" : "1"));

        Label titleLabel = new Label(object.name());
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(246);
        titleLabel.setTextFill(Color.web(object.colorHex()));
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: %spx;".formatted(Double.toString(object.fontSize())));
        textBox.getChildren().add(titleLabel);

        if (!object.notes().isBlank()) {
            Label contentLabel = new Label(object.notes());
            contentLabel.setWrapText(true);
            contentLabel.setMaxWidth(246);
            contentLabel.setTextFill(Color.web("#111827"));
            contentLabel.setStyle("-fx-font-size: %spx;".formatted(Double.toString(object.fontSize())));
            textBox.getChildren().add(contentLabel);
        }

        makeSelectable(textBox, object);
        makeDraggable(textBox, object);
        mapPane.getChildren().add(textBox);
    }

    private void drawMarkerObject(MarkerObject object) {
        Pane markerIcon = MarkerIconFactory.create(object.markerType());
        markerIcon.setLayoutX(object.position().x());
        markerIcon.setLayoutY(object.position().y());
        markerIcon.setStyle("-fx-background-color: %s; -fx-background-radius: 6; -fx-border-radius: 6;%s".formatted(
                object.colorHex(),
                isSelected(object) ? " -fx-border-color: #111827; -fx-border-width: 2;" : " -fx-border-color: #111827; -fx-border-width: 1;"
        ));
        makeSelectable(markerIcon, object);
        makeDraggable(markerIcon, object);

        mapPane.getChildren().add(markerIcon);
        addMapLabel(object, object.position().x() + 34, object.position().y() + 4);
    }

    private void addMapLabel(PlannerObject object, double x, double y) {
        if (!isSelected(object) && (!showObjectLabels() || !object.showMapLabel())) {
            return;
        }
        double labelX = object.customMapLabelPosition() ? x + object.mapLabelOffset().x() : x;
        double labelY = object.customMapLabelPosition() ? y + object.mapLabelOffset().y() : y;
        Label label = new Label(mapLabel(object));
        label.setLayoutX(labelX);
        label.setLayoutY(labelY);
        label.setStyle("""
                -fx-background-color: rgba(255,255,255,0.82);
                -fx-border-color: rgba(17,24,39,0.35);
                -fx-border-width: 1;
                -fx-background-radius: 3;
                -fx-border-radius: 3;
                -fx-padding: 2 5 2 5;
                -fx-text-fill: #111827;
                -fx-font-size: %spx;
                """.formatted(Double.toString(plan.objectLabelFontSize())));
        makeSelectable(label, object);
        makeMapLabelDraggable(label, object, x, y);
        mapPane.getChildren().add(label);
    }

    private void makeMapLabelDraggable(Label label, PlannerObject object, double defaultX, double defaultY) {
        final Delta dragDelta = new Delta();
        label.setOnMousePressed(event -> {
            if (event.getButton() != MouseButton.PRIMARY || measuringActive || addingCablePoint || mapLayoutLocked) {
                return;
            }
            selectedObject = object;
            refreshDetails();
            beginPlanDrag();
            Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
            dragDelta.x = mapPoint.getX() - label.getLayoutX();
            dragDelta.y = mapPoint.getY() - label.getLayoutY();
            event.consume();
        });
        label.setOnMouseDragged(event -> {
            if (measuringActive || addingCablePoint || mapLayoutLocked) {
                return;
            }
            Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
            double labelX = mapPoint.getX() - dragDelta.x;
            double labelY = mapPoint.getY() - dragDelta.y;
            label.setLayoutX(labelX);
            label.setLayoutY(labelY);
            object.setMapLabelOffset(new Position(labelX - defaultX, labelY - defaultY));
            refreshDetails();
            recordPlanDragChange();
            event.consume();
        });
    }


    private void makeSelectable(Node node, PlannerObject object) {
        mapObjectNodes.putIfAbsent(object.id(), node);
        node.setOnContextMenuRequested(event -> {
            if (!isPlacementPending() && !measuringActive && !addingCablePoint) {
                showObjectContextMenu(object, event.getScreenX(), event.getScreenY());
            }
            event.consume();
        });
        node.setOnMouseClicked(event -> {
            if (event.getButton() != MouseButton.PRIMARY) {
                return;
            }
            if (pendingTentPlacement) {
                Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
                placeTent(new Position(mapPoint.getX(), mapPoint.getY()));
                event.consume();
                return;
            }
            if (pendingPowerSourcePlacement) {
                Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
                placePowerSource(new Position(mapPoint.getX(), mapPoint.getY()));
                event.consume();
                return;
            }
            if (pendingCustomObjectPlacement) {
                Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
                placeCustomObject(new Position(mapPoint.getX(), mapPoint.getY()));
                event.consume();
                return;
            }
            if (pendingTextObjectPlacement) {
                Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
                placeTextObject(new Position(mapPoint.getX(), mapPoint.getY()));
                event.consume();
                return;
            }
            if (pendingMarkerPlacement) {
                Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
                placeMarkerObject(new Position(mapPoint.getX(), mapPoint.getY()));
                event.consume();
                return;
            }
            if (pendingLineObjectPlacement) {
                Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
                addPendingShapePoint(new Position(mapPoint.getX(), mapPoint.getY()));
                event.consume();
                return;
            }
            if (pendingAreaObjectPlacement) {
                Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
                addPendingShapePoint(new Position(mapPoint.getX(), mapPoint.getY()));
                event.consume();
                return;
            }
            if (addingCablePoint) {
                Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
                addCableRoutePoint(new Position(mapPoint.getX(), mapPoint.getY()));
                event.consume();
                return;
            }
            if (measuringActive) {
                Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
                handleMeasureClick(new Position(mapPoint.getX(), mapPoint.getY()));
                event.consume();
                return;
            }
            if (pendingPowerSourceConsumer != null && object instanceof PowerSource source) {
                connectPowerSourceFromMap(source);
                event.consume();
                return;
            }
            selectObject(object);
            event.consume();
        });
    }

    private void makeDraggable(Node node, PlannerObject object) {
        final Delta dragDelta = new Delta();
        node.setOnMousePressed(event -> {
            if (event.getButton() != MouseButton.PRIMARY || measuringActive || addingCablePoint) {
                return;
            }
            if (pendingPowerSourceConsumer != null && object instanceof PowerSource source) {
                connectPowerSourceFromMap(source);
                event.consume();
                return;
            }
            selectObject(object);
            if (mapLayoutLocked) {
                event.consume();
                return;
            }
            beginPlanDrag();
            Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
            dragDelta.x = mapPoint.getX() - object.position().x();
            dragDelta.y = mapPoint.getY() - object.position().y();
            event.consume();
        });
        node.setOnMouseDragged(event -> {
            if (measuringActive || addingCablePoint || mapLayoutLocked) {
                return;
            }
            if (object.locked()) {
                return;
            }
            Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
            object.moveTo(object.position().moveTo(mapPoint.getX() - dragDelta.x, mapPoint.getY() - dragDelta.y));
            redrawMap();
            refreshSummary();
            recordPlanDragChange();
            event.consume();
        });
    }

    private void showObjectContextMenu(PlannerObject object, double screenX, double screenY) {
        selectObject(object);
        MenuItem editItem = new MenuItem("Muuda");
        editItem.setOnAction(event -> editObject(object));
        MenuItem copyItem = new MenuItem("Kopeeri");
        copyItem.setOnAction(event -> copyObject(object));
        MenuItem visibilityItem = new MenuItem(object.hidden() ? "Kuva" : "Peida");
        visibilityItem.setOnAction(event -> setObjectHidden(object, !object.hidden()));
        MenuItem deleteItem = new MenuItem("Kustuta");
        deleteItem.setDisable(mapLayoutLocked);
        deleteItem.setOnAction(event -> deleteObject(object));
        showContextMenu(
                new ContextMenu(editItem, copyItem, visibilityItem, deleteItem),
                mapPane,
                screenX,
                screenY
        );
    }

    private void showContextMenu(
            ContextMenu contextMenu,
            Node owner,
            double screenX,
            double screenY
    ) {
        if (activeContextMenu != null) {
            activeContextMenu.hide();
        }
        activeContextMenu = contextMenu;
        contextMenu.setOnHidden(event -> {
            if (activeContextMenu == contextMenu) {
                activeContextMenu = null;
            }
        });
        contextMenu.show(owner, screenX, screenY);
    }

    private void editObject(PlannerObject object) {
        selectObject(object);
        if (selectedObjectSection != null) {
            selectedObjectSection.setExpanded(true);
        }
    }

    private void deleteObject(PlannerObject object) {
        selectObject(object);
        deleteSelectedObject();
    }

    private void setObjectHidden(PlannerObject object, boolean hidden) {
        object.setHidden(hidden);
        redrawMap();
        refreshObjectList();
        updateRevealObjectButton();
        markDirty();
    }

    private void selectObject(PlannerObject object) {
        if (selectedObject != null
                && object != null
                && !selectedObject.id().equals(object.id())
                && !updatingDetailControls) {
            commitPendingDetailFieldsBeforeSelectionChange();
        }
        selectedObject = object;
        clearObjectSearchIfItHides(object);
        refreshDetails();
        revealObjectInObjectList(object);
        revealObjectInPowerSummary(object);
        redrawMap();
    }

    private void commitPendingDetailFieldsBeforeSelectionChange() {
        PlanSnapshot before = planSnapshotService.create(plan);
        selectedObject.rename(nameField.getText());
        selectedObject.setGroupName(groupField.getEditor().getText());
        selectedObject.setNotes(notesArea.getText());
        if (selectedObject instanceof Tent tent) {
            applyTentSize(tent);
            applyTentRotation(tent);
        } else if (selectedObject instanceof CustomObject customObject) {
            applyCustomObjectSize(customObject);
            applyCustomObjectRotation(customObject);
        }
        autoApplyCableLengthNotes();
        autoApplyCableNotes();
        if (!before.equals(planSnapshotService.create(plan))) {
            refreshGroupFilters();
            refreshObjectList();
            refreshSummary();
            markDirty();
        }
    }

    private void clearObjectSearchIfItHides(PlannerObject object) {
        if (objectSearchField == null) {
            return;
        }
        String query = objectSearchField.getText().trim().toLowerCase();
        if (query.isBlank()) {
            return;
        }
        if (!object.name().toLowerCase().contains(query)
                && !objectTypeName(object).toLowerCase().contains(query)
                && !groupNameForFilter(object).toLowerCase().contains(query)) {
            objectSearchField.clear();
        }
    }

    private void revealObjectInPowerSummary(PlannerObject object) {
        if (summaryList == null || object == null) {
            return;
        }
        SummaryListItem target = null;
        if (object instanceof PowerSource source) {
            target = findPowerSummaryItem(powerSourceSummaryKey(source.id())).orElse(null);
        }
        if (target == null) {
            PowerConnection connection = plan.findPowerConnectionForConsumer(object.id()).orElse(null);
            if (connection != null) {
                SummaryListItem sourceItem = findPowerSummaryItem(
                        powerSourceSummaryKey(connection.sourceId())
                ).orElse(null);
                if (sourceItem == null) {
                    return;
                }
                target = sourceItem;
                if (!collapsedPowerSummaryKeys.contains(sourceItem.hierarchyKey())) {
                    if (connection.outletId().isBlank()) {
                        target = findSummaryTarget(object.id()).orElse(sourceItem);
                    } else {
                        SummaryListItem outletItem = findPowerSummaryItem(
                                powerOutletSummaryKey(connection.outletId())
                        ).orElse(null);
                        if (outletItem != null) {
                            target = outletItem;
                            if (!collapsedPowerSummaryKeys.contains(outletItem.hierarchyKey())) {
                                target = findSummaryTarget(object.id()).orElse(outletItem);
                            }
                        }
                    }
                }
            }
        }
        if (target == null) {
            return;
        }
        selectSidebarItem(summaryList, target, powerSummarySection);
    }

    private void revealObjectInObjectList(PlannerObject object) {
        if (objectList == null || object == null) {
            return;
        }
        ObjectListEntry target = objectList.getItems().stream()
                .filter(entry -> !entry.isGroup())
                .filter(entry -> entry.objectItem().object().id().equals(object.id()))
                .findFirst()
                .orElseGet(() -> objectList.getItems().stream()
                        .filter(ObjectListEntry::isGroup)
                        .filter(entry -> entry.groupName().equals(groupNameForFilter(object)))
                        .findFirst()
                        .orElse(null));
        if (target != null) {
            selectSidebarItem(objectList, target, objectListSection);
            updateRevealObjectButton();
        }
    }

    private Optional<SummaryListItem> findPowerSummaryItem(String hierarchyKey) {
        return summaryList.getItems().stream()
                .filter(item -> hierarchyKey.equals(item.hierarchyKey()))
                .findFirst();
    }

    private Optional<SummaryListItem> findSummaryTarget(String objectId) {
        return summaryList.getItems().stream()
                .filter(item -> objectId.equals(item.targetObjectId()))
                .findFirst();
    }

    private <T> void selectSidebarItem(ListView<T> listView, T item, TitledPane section) {
        synchronizingSidebarSelection = true;
        try {
            listView.getSelectionModel().select(item);
        } finally {
            synchronizingSidebarSelection = false;
        }
        Platform.runLater(() -> {
            if ((section == null || section.isExpanded()) && !isListItemVisible(listView, item)) {
                listView.scrollTo(item);
            }
        });
    }

    private <T> boolean isListItemVisible(ListView<T> listView, T item) {
        Bounds listBounds = listView.localToScene(listView.getBoundsInLocal());
        for (Node node : listView.lookupAll(".list-cell")) {
            if (!(node instanceof ListCell<?> cell)
                    || !item.equals(cell.getItem())
                    || !cell.isVisible()) {
                continue;
            }
            Bounds cellBounds = cell.localToScene(cell.getBoundsInLocal());
            return cellBounds.getMaxY() >= listBounds.getMinY()
                    && cellBounds.getMinY() <= listBounds.getMaxY();
        }
        return false;
    }

    private boolean isSelected(PlannerObject object) {
        return selectedObject != null && selectedObject.id().equals(object.id());
    }

    private String mapLabel(PlannerObject object) {
        if (object.groupName().isBlank()) {
            return object.name();
        }
        return "%s [%s]".formatted(object.name(), object.groupName());
    }

    private boolean isGroupVisible(PlannerObject object) {
        return knownGroups.isEmpty() || visibleGroups.contains(groupNameForFilter(object));
    }

    private boolean isObjectVisibleOnMap(PlannerObject object) {
        return !object.hidden() && isGroupVisible(object) && isObjectTypeVisible(object);
    }

    private String groupNameForFilter(PlannerObject object) {
        return object.groupName().isBlank() ? "Määramata" : object.groupName();
    }

    private void refreshGroupFilters() {
        if (plan == null) {
            return;
        }

        Set<String> currentGroups = new HashSet<>();
        for (PlannerObject object : plan.objects()) {
            currentGroups.add(groupNameForFilter(object));
        }
        Set<String> hiddenGroups = new HashSet<>(plan.hiddenGroups());
        hiddenGroups.retainAll(currentGroups);
        plan.clearHiddenGroups();
        for (String hiddenGroup : hiddenGroups) {
            plan.setGroupHidden(hiddenGroup, true);
        }

        visibleGroups.retainAll(currentGroups);
        for (String groupName : currentGroups) {
            if (hiddenGroups.contains(groupName)) {
                visibleGroups.remove(groupName);
            } else if (!knownGroups.contains(groupName)) {
                visibleGroups.add(groupName);
            }
        }
        knownGroups = currentGroups;
        refreshObjectList();
    }

    private void applyLockedStroke(javafx.scene.shape.Shape shape, PlannerObject object) {
        shape.getStrokeDashArray().clear();
        if (object.locked()) {
            shape.getStrokeDashArray().addAll(8.0, 5.0);
        }
    }

    private String mapLabelResetTooltip(boolean hasSelection, boolean textObjectSelected, boolean customMapLabelPosition) {
        if (!hasSelection) {
            return "Vali objekt, mille nime asukohta lähtestada";
        }
        if (textObjectSelected) {
            return "Tekstobjekt on ise kaardil olev tekst";
        }
        if (!customMapLabelPosition) {
            return "Nime asukoht on automaatne";
        }
        return "Viib nime tagasi automaatsesse asukohta";
    }

    private String cableLabelResetTooltip(boolean consumerSelected, boolean consumerHasPowerConnection, boolean customCableLabelPosition) {
        if (!consumerSelected) {
            return "Vali elektritarbija, mille kaablisilti lähtestada";
        }
        if (!consumerHasPowerConnection) {
            return "Valitud objektil pole vooluühendust";
        }
        if (!customCableLabelPosition) {
            return "Kaablisildi asukoht on automaatne";
        }
        return "Viib kaablisildi tagasi automaatsesse asukohta";
    }

    private void refreshDetails() {
        updatingDetailControls = true;
        try {
            refreshDetailControls();
        } finally {
            updatingDetailControls = false;
        }
    }

    private void refreshDetailControls() {
        boolean hasSelection = selectedObject != null;
        boolean tentSelected = selectedObject instanceof Tent;
        boolean powerSourceSelected = selectedObject instanceof PowerSource;
        boolean customObjectSelected = selectedObject instanceof CustomObject;
        boolean textObjectSelected = selectedObject instanceof TextObject;
        boolean markerSelected = selectedObject instanceof MarkerObject;
        boolean areaSelected = selectedObject instanceof AreaObject;
        boolean lineSelected = selectedObject instanceof LineObject;
        boolean powerConsumerSelected = selectedObject instanceof PowerConsumer;
        boolean equipmentContainerSelected = selectedObject instanceof EquipmentContainer;
        nameField.setDisable(!hasSelection);
        groupField.setDisable(!hasSelection);
        notesArea.setDisable(!hasSelection);
        lockedCheckBox.setDisable(!hasSelection);
        showMapLabelCheckBox.setDisable(!hasSelection || textObjectSelected);
        boolean customMapLabelPosition = hasSelection && !textObjectSelected && selectedObject.customMapLabelPosition();
        resetMapLabelButton.setDisable(!customMapLabelPosition || mapLayoutLocked);
        resetMapLabelButton.setTooltip(new Tooltip(mapLabelResetTooltip(hasSelection, textObjectSelected, customMapLabelPosition)));
        boolean lockedSelection = selectedObject != null && selectedObject.locked();
        deleteObjectButton.setDisable(!hasSelection || lockedSelection || mapLayoutLocked);
        deleteObjectButton.setTooltip(mapLayoutLocked
                ? new Tooltip("Paigutuse muutmiseks eemalda tööriistaribal paigutuslukk")
                : lockedSelection
                ? new Tooltip("Lukustatud objekti kustutamiseks eemalda enne lukustus")
                : new Tooltip("Kustuta valitud objekt (Delete)"));
        customObjectShapeComboBox.setDisable(!customObjectSelected);
        customObjectColorPicker.setDisable(!customObjectSelected);
        customObjectOpacitySlider.setDisable(!customObjectSelected);
        textObjectColorPicker.setDisable(!textObjectSelected);
        textObjectFontSizeSlider.setDisable(!textObjectSelected);
        markerTypeComboBox.setDisable(!markerSelected);
        markerColorPicker.setDisable(!markerSelected);
        areaColorPicker.setDisable(!areaSelected);
        areaOpacitySlider.setDisable(!areaSelected);
        lineColorPicker.setDisable(!lineSelected);
        lineWidthSlider.setDisable(!lineSelected);
        tentWidthField.setDisable(!tentSelected);
        tentHeightField.setDisable(!tentSelected);
        tentRotationField.setDisable(!tentSelected);
        tentColorPicker.setDisable(!tentSelected);
        tentOpacitySlider.setDisable(!tentSelected);
        powerConnectionComboBox.setDisable(!powerConsumerSelected);
        powerSourceComboBox.setDisable(!powerConsumerSelected);
        connectionOutletComboBox.setDisable(!powerConsumerSelected);
        cableLengthNotesField.setDisable(!powerConsumerSelected);
        cableNotesField.setDisable(!powerConsumerSelected);
        PowerConnection editedPowerConnection = powerConsumerSelected ? selectedPowerConnection() : null;
        showSelectedCableLabelCheckBox.setDisable(editedPowerConnection == null);
        boolean consumerHasPowerConnection = powerConsumerSelected
                && !plan.findPowerConnectionsForConsumer(selectedObject.id()).isEmpty();
        boolean customCableLabelPosition = powerConsumerSelected
                && editedPowerConnection != null
                && editedPowerConnection.customCableLabelPosition();
        resetCableLabelButton.setDisable(!customCableLabelPosition || mapLayoutLocked);
        resetCableLabelButton.setTooltip(new Tooltip(cableLabelResetTooltip(
                powerConsumerSelected,
                consumerHasPowerConnection,
                customCableLabelPosition
        )));
        equipmentList.setDisable(!equipmentContainerSelected);
        addEquipmentButton.setDisable(!equipmentContainerSelected);
        addAlternativePowerConnectionButton.setDisable(!equipmentContainerSelected || !consumerHasPowerConnection);
        removePowerConnectionButton.setDisable(editedPowerConnection == null);
        makeDefaultPowerConnectionButton.setDisable(
                editedPowerConnection == null || editedPowerConnection.defaultForConsumer()
        );
        outletList.setDisable(!powerSourceSelected);
        outletNameField.setDisable(!powerSourceSelected);
        outletTypeComboBox.setDisable(!powerSourceSelected);
        outletCapacityWattsField.setDisable(!powerSourceSelected);
        addOutletButton.setDisable(!powerSourceSelected);
        boolean outletSelected = powerSourceSelected && outletList.getSelectionModel().getSelectedIndex() >= 0;
        updateOutletButton.setDisable(!outletSelected);
        removeOutletButton.setDisable(!outletSelected);
        choosePowerSourceButton.setDisable(!powerConsumerSelected);
        if (addCablePointButton != null) {
            addCablePointButton.setDisable(!consumerHasPowerConnection || mapLayoutLocked);
            addCablePointButton.setText(editingCableConnectionId != null
                    ? "Lõpeta trajektoor"
                    : addingCablePoint ? "Lõpeta kaabli punktid" : "Kaabli punkt");
            if (!consumerHasPowerConnection) {
                addCablePointButton.setSelected(false);
                addingCablePoint = false;
                editingCableConnectionId = null;
                updateMapToolStatus();
            }
        }
        if (clearCableRouteButton != null) {
            clearCableRouteButton.setDisable(!consumerHasPowerConnection || mapLayoutLocked);
        }
        boolean selectingPowerSourceForThisConsumer = powerConsumerSelected
                && pendingPowerSourceConsumer != null
                && pendingPowerSourceConsumer.id().equals(selectedObject.id());
        choosePowerSourceButton.setText(selectingPowerSourceForThisConsumer
                ? "Tühista kapi valik"
                : "Vali kapp kaardilt");
        setSectionVisible(customObjectPanel, customObjectSelected);
        setSectionVisible(textObjectPanel, textObjectSelected);
        setSectionVisible(markerPanel, markerSelected);
        setSectionVisible(areaPanel, areaSelected);
        setSectionVisible(linePanel, lineSelected);
        setSectionVisible(tentPanel, tentSelected);
        setSectionVisible(powerConnectionPanel, powerConsumerSelected);
        setSectionVisible(equipmentSection, equipmentContainerSelected);
        setSectionVisible(outletSection, powerSourceSelected);
        setSectionVisible(choosePowerSourceButton, powerConsumerSelected);
        setSectionVisible(deleteObjectButton, hasSelection);

        if (!hasSelection) {
            selectedTypeLabel.setText("Vali kaardilt objekt");
            nameField.clear();
            refreshGroupChoices("");
            notesArea.clear();
            lockedCheckBox.setSelected(false);
            showMapLabelCheckBox.setSelected(false);
            tentWidthField.clear();
            tentHeightField.clear();
            tentRotationField.clear();
            tentColorPicker.setValue(Color.web("#e74c3c"));
            setOpacitySliderValue(tentOpacitySlider, Tent.DEFAULT_OPACITY * 100.0);
            customObjectShapeComboBox.getSelectionModel().select(CustomObjectShape.SQUARE);
            customObjectColorPicker.setValue(Color.web("#9ca3af"));
            textObjectColorPicker.setValue(Color.web("#111827"));
            textObjectFontSizeSlider.setValue(TextObject.DEFAULT_FONT_SIZE);
            markerTypeComboBox.getSelectionModel().select(MarkerType.WC);
            markerColorPicker.setValue(Color.web(MarkerType.WC.defaultColorHex()));
            areaColorPicker.setValue(Color.web("#f59e0b"));
            setOpacitySliderValue(areaOpacitySlider, AreaObject.DEFAULT_OPACITY * 100.0);
            areaSizeLabel.setText("-");
            areaPerimeterLabel.setText("-");
            lineColorPicker.setValue(Color.web("#0f766e"));
            lineWidthSlider.setValue(LineObject.DEFAULT_WIDTH_PIXELS);
            lineLengthLabel.setText("-");
            customObjectWidthField.clear();
            customObjectHeightField.clear();
            customObjectRotationField.clear();
            setOpacitySliderValue(customObjectOpacitySlider, CustomObject.DEFAULT_OPACITY * 100.0);
            customObjectAreaLabel.setText("-");
            customObjectPerimeterLabel.setText("-");
            cableLengthNotesField.clear();
            cableNotesField.clear();
            refreshPowerConnectionChoices(null);
            refreshPowerSourceChoices();
            refreshEquipmentList();
            refreshOutletList();
            return;
        }

        selectedTypeLabel.setText(objectTypeName(selectedObject));
        nameField.setText(selectedObject.name());
        refreshGroupChoices(selectedObject.groupName());
        notesArea.setText(selectedObject.notes());
        lockedCheckBox.setSelected(selectedObject.locked());
        showMapLabelCheckBox.setSelected(selectedObject.showMapLabel());
        if (selectedObject instanceof Tent tent) {
            tentWidthField.setText(formatMeters(tent.widthMeters()));
            tentHeightField.setText(formatMeters(tent.heightMeters()));
            tentRotationField.setText(formatDegrees(tent.rotationDegrees()));
            tentColorPicker.setValue(Color.web(tent.colorHex()));
            setOpacitySliderValue(tentOpacitySlider, tent.opacity() * 100.0);
            customObjectShapeComboBox.getSelectionModel().select(CustomObjectShape.SQUARE);
            customObjectColorPicker.setValue(Color.web("#9ca3af"));
            textObjectColorPicker.setValue(Color.web("#111827"));
            textObjectFontSizeSlider.setValue(TextObject.DEFAULT_FONT_SIZE);
            markerTypeComboBox.getSelectionModel().select(MarkerType.WC);
            markerColorPicker.setValue(Color.web(MarkerType.WC.defaultColorHex()));
            customObjectWidthField.clear();
            customObjectHeightField.clear();
            customObjectRotationField.clear();
            cableLengthNotesField.setText(plan.findPowerConnectionForConsumer(tent.id())
                    .map(PowerConnection::cableLengthNotes)
                    .orElse(""));
            cableNotesField.setText(plan.findPowerConnectionForConsumer(tent.id())
                    .map(PowerConnection::cableNotes)
                    .orElse(""));
        } else if (selectedObject instanceof CustomObject customObject) {
            tentWidthField.clear();
            tentHeightField.clear();
            tentRotationField.clear();
            tentColorPicker.setValue(Color.web("#2563eb"));
            customObjectShapeComboBox.getSelectionModel().select(customObject.shape());
            customObjectColorPicker.setValue(Color.web(customObject.colorHex()));
            setOpacitySliderValue(customObjectOpacitySlider, customObject.opacity() * 100.0);
            textObjectFontSizeSlider.setValue(TextObject.DEFAULT_FONT_SIZE);
            customObjectWidthField.setText(formatMeters(customObject.widthMeters()));
            customObjectHeightField.setText(formatMeters(customObject.heightMeters()));
            customObjectRotationField.setText(formatDegrees(customObject.rotationDegrees()));
            refreshCustomObjectMeasurements(customObject);
            cableLengthNotesField.clear();
            cableNotesField.clear();
        } else if (selectedObject instanceof TextObject textObject) {
            tentWidthField.clear();
            tentHeightField.clear();
            tentRotationField.clear();
            tentColorPicker.setValue(Color.web("#2563eb"));
            customObjectShapeComboBox.getSelectionModel().select(CustomObjectShape.SQUARE);
            customObjectColorPicker.setValue(Color.web("#9ca3af"));
            textObjectColorPicker.setValue(Color.web(textObject.colorHex()));
            textObjectFontSizeSlider.setValue(textObject.fontSize());
            markerTypeComboBox.getSelectionModel().select(MarkerType.WC);
            markerColorPicker.setValue(Color.web(MarkerType.WC.defaultColorHex()));
            customObjectWidthField.clear();
            customObjectHeightField.clear();
            customObjectRotationField.clear();
            cableLengthNotesField.clear();
            cableNotesField.clear();
        } else if (selectedObject instanceof MarkerObject markerObject) {
            tentWidthField.clear();
            tentHeightField.clear();
            tentRotationField.clear();
            tentColorPicker.setValue(Color.web("#2563eb"));
            customObjectShapeComboBox.getSelectionModel().select(CustomObjectShape.SQUARE);
            customObjectColorPicker.setValue(Color.web("#9ca3af"));
            textObjectColorPicker.setValue(Color.web("#111827"));
            textObjectFontSizeSlider.setValue(TextObject.DEFAULT_FONT_SIZE);
            markerTypeComboBox.getSelectionModel().select(markerObject.markerType());
            markerColorPicker.setValue(Color.web(markerObject.colorHex()));
            customObjectWidthField.clear();
            customObjectHeightField.clear();
            customObjectRotationField.clear();
            cableLengthNotesField.clear();
            cableNotesField.clear();
        } else if (selectedObject instanceof AreaObject areaObject) {
            tentWidthField.clear();
            tentHeightField.clear();
            tentRotationField.clear();
            tentColorPicker.setValue(Color.web("#2563eb"));
            customObjectShapeComboBox.getSelectionModel().select(CustomObjectShape.SQUARE);
            customObjectColorPicker.setValue(Color.web("#9ca3af"));
            textObjectColorPicker.setValue(Color.web("#111827"));
            textObjectFontSizeSlider.setValue(TextObject.DEFAULT_FONT_SIZE);
            markerTypeComboBox.getSelectionModel().select(MarkerType.WC);
            markerColorPicker.setValue(Color.web(MarkerType.WC.defaultColorHex()));
            areaColorPicker.setValue(Color.web(areaObject.colorHex()));
            setOpacitySliderValue(areaOpacitySlider, areaObject.opacity() * 100.0);
            refreshAreaMeasurements(areaObject);
            customObjectWidthField.clear();
            customObjectHeightField.clear();
            customObjectRotationField.clear();
            cableLengthNotesField.clear();
            cableNotesField.clear();
        } else if (selectedObject instanceof LineObject lineObject) {
            tentWidthField.clear();
            tentHeightField.clear();
            tentRotationField.clear();
            tentColorPicker.setValue(Color.web("#2563eb"));
            customObjectShapeComboBox.getSelectionModel().select(CustomObjectShape.SQUARE);
            customObjectColorPicker.setValue(Color.web("#9ca3af"));
            textObjectColorPicker.setValue(Color.web("#111827"));
            textObjectFontSizeSlider.setValue(TextObject.DEFAULT_FONT_SIZE);
            markerTypeComboBox.getSelectionModel().select(MarkerType.WC);
            markerColorPicker.setValue(Color.web(MarkerType.WC.defaultColorHex()));
            areaColorPicker.setValue(Color.web("#f59e0b"));
            setOpacitySliderValue(areaOpacitySlider, AreaObject.DEFAULT_OPACITY * 100.0);
            lineColorPicker.setValue(Color.web(lineObject.colorHex()));
            lineWidthSlider.setValue(lineObject.widthPixels());
            refreshLineLengthLabel(lineObject);
            customObjectWidthField.clear();
            customObjectHeightField.clear();
            customObjectRotationField.clear();
            cableLengthNotesField.clear();
            cableNotesField.clear();
        } else {
            tentWidthField.clear();
            tentHeightField.clear();
            tentRotationField.clear();
            tentColorPicker.setValue(Color.web("#2563eb"));
            customObjectShapeComboBox.getSelectionModel().select(CustomObjectShape.SQUARE);
            customObjectColorPicker.setValue(Color.web("#9ca3af"));
            textObjectColorPicker.setValue(Color.web("#111827"));
            textObjectFontSizeSlider.setValue(TextObject.DEFAULT_FONT_SIZE);
            markerTypeComboBox.getSelectionModel().select(MarkerType.WC);
            markerColorPicker.setValue(Color.web(MarkerType.WC.defaultColorHex()));
            customObjectWidthField.clear();
            customObjectHeightField.clear();
            customObjectRotationField.clear();
            cableLengthNotesField.clear();
            cableNotesField.clear();
        }
        refreshPowerConnectionChoices(null);
        refreshSelectedPowerConnectionFields();
        refreshPowerSourceChoices();
        refreshEquipmentList();
        refreshOutletList();
        updateCustomObjectSizeFields();
    }

    private void refreshSelectedPowerConnectionFields() {
        if (!(selectedObject instanceof PowerConsumer)) {
            cableLengthNotesField.clear();
            cableNotesField.clear();
            showSelectedCableLabelCheckBox.setSelected(true);
            return;
        }
        Optional.ofNullable(selectedPowerConnection()).ifPresentOrElse(connection -> {
            cableLengthNotesField.setText(connection.cableLengthNotes());
            cableNotesField.setText(connection.cableNotes());
            showSelectedCableLabelCheckBox.setSelected(plan.showCableLabel(connection.id()));
        }, () -> {
            cableLengthNotesField.clear();
            cableNotesField.clear();
            showSelectedCableLabelCheckBox.setSelected(true);
        });
    }

    private void updateCustomObjectSizeFields() {
        boolean customObjectSelected = selectedObject instanceof CustomObject;
        CustomObjectShape selectedShape = customObjectShapeComboBox.getSelectionModel().getSelectedItem();
        boolean circleSelected = selectedShape == CustomObjectShape.CIRCLE;
        customObjectWidthLabel.setText(circleSelected ? "Objekti läbimõõt m" : "Objekti laius m");
        customObjectWidthLabel.setVisible(true);
        customObjectWidthLabel.setManaged(true);
        customObjectWidthField.setVisible(true);
        customObjectWidthField.setManaged(true);
        customObjectHeightLabel.setVisible(!circleSelected);
        customObjectHeightLabel.setManaged(!circleSelected);
        customObjectHeightField.setVisible(!circleSelected);
        customObjectHeightField.setManaged(!circleSelected);
        customObjectWidthField.setDisable(!customObjectSelected);
        customObjectHeightField.setDisable(!customObjectSelected || circleSelected);
        customObjectRotationLabel.setVisible(!circleSelected);
        customObjectRotationLabel.setManaged(!circleSelected);
        customObjectRotationField.setVisible(!circleSelected);
        customObjectRotationField.setManaged(!circleSelected);
        customObjectRotationField.setDisable(!customObjectSelected || circleSelected);
        if (customObjectSelected && !circleSelected && customObjectRotationField.getText().isBlank()) {
            customObjectRotationField.setText("0");
        }
    }

    private void setSectionVisible(Node node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }

    private void startPowerSourceSelectionFromMap() {
        if (!(selectedObject instanceof PowerConsumer)) {
            return;
        }
        PlannerObject consumer = selectedObject;
        pendingTentPlacement = false;
        pendingPowerSourcePlacement = false;
        pendingCustomObjectPlacement = false;
        pendingTextObjectPlacement = false;
        pendingMarkerPlacement = false;
        pendingLineObjectPlacement = false;
        pendingAreaObjectPlacement = false;
        clearPendingPlacementDetails();
        refreshPlacementButtons();
        updateMapToolStatus();
        if (pendingPowerSourceConsumer != null && pendingPowerSourceConsumer.id().equals(consumer.id())) {
            pendingPowerSourceConsumer = null;
            updateMapToolStatus();
            refreshDetails();
            return;
        }
        pendingPowerSourceConsumer = consumer;
        updateMapToolStatus();
        refreshDetails();
    }

    private void connectPowerSourceFromMap(PowerSource source) {
        PlannerObject consumer = pendingPowerSourceConsumer;
        if (!(consumer instanceof PowerConsumer)) {
            return;
        }

        ConnectorType preferredType = selectedConnectionType();
        List<PowerOutlet> candidateOutlets = source.outlets().stream()
                .sorted(Comparator.comparing(outlet -> outlet.type() != preferredType))
                .toList();
        PowerOutlet selectedOutlet = candidateOutlets.stream()
                .filter(outlet -> plan.validatePowerConnection(
                        source.id(), consumer.id(), outlet.type(), outlet.id()
                ) == PowerConnectionValidationResult.VALID)
                .findFirst()
                .orElse(null);
        if (selectedOutlet == null) {
            PowerConnectionValidationResult validationResult = candidateOutlets.isEmpty()
                    ? plan.validatePowerConnection(source.id(), consumer.id(), preferredType, "")
                    : plan.validatePowerConnection(
                            source.id(), consumer.id(), candidateOutlets.getFirst().type(), candidateOutlets.getFirst().id()
                    );
            showError("Vooluallikat ei valitud", powerConnectionValidationMessage(validationResult));
            return;
        }
        PowerConnection editedConnection = selectedPowerConnection();
        boolean connected = editedConnection == null
                ? plan.connectToPower(
                        source.id(), consumer.id(), selectedOutlet.type(), selectedOutlet.id(),
                        cableNotesField.getText(), cableLengthNotesField.getText()
                ).isPresent()
                : plan.updatePowerConnection(
                        editedConnection.id(), source.id(), selectedOutlet.type(), selectedOutlet.id(),
                        cableNotesField.getText(), cableLengthNotesField.getText()
                );
        if (!connected) {
            showError("Vooluallikat ei valitud", "Vooluühendust ei õnnestunud luua.");
            return;
        }
        pendingPowerSourceConsumer = null;
        updateMapToolStatus();
        selectedObject = consumer;
        refreshDetails();
        redrawMap();
        refreshSummary();
        markDirty();
    }

    private void updateSelectedLock() {
        if (selectedObject == null) {
            return;
        }
        selectedObject.setLocked(lockedCheckBox.isSelected());
        redrawMap();
        markDirty();
    }

    private void updateSelectedMapLabelVisibility() {
        if (selectedObject == null || selectedObject instanceof TextObject) {
            return;
        }
        selectedObject.setShowMapLabel(showMapLabelCheckBox.isSelected());
        redrawMap();
        markDirty();
    }

    private void resetSelectedMapLabelPosition() {
        if (mapLayoutLocked) {
            showMapLayoutLockedMessage();
            return;
        }
        if (selectedObject == null || selectedObject instanceof TextObject) {
            return;
        }
        selectedObject.resetMapLabelPosition();
        redrawMap();
        refreshDetails();
        markDirty();
    }

    private void resetSelectedCableLabelPosition() {
        if (mapLayoutLocked) {
            showMapLayoutLockedMessage();
            return;
        }
        if (!(selectedObject instanceof PowerConsumer)) {
            return;
        }
        PowerConnection connection = selectedPowerConnection();
        if (connection == null) {
            return;
        }
        plan.resetCableLabelOffsetForConnection(connection.id());
        redrawMap();
        refreshDetails();
        markDirty();
    }

    private void updateSelectedCableLabelVisibility() {
        if (updatingDetailControls) {
            return;
        }
        PowerConnection connection = selectedPowerConnection();
        if (connection == null) {
            return;
        }
        boolean visible = showSelectedCableLabelCheckBox.isSelected();
        if (plan.showCableLabel(connection.id()) == visible) {
            return;
        }
        plan.setShowCableLabel(connection.id(), visible);
        redrawMap();
        markDirty();
    }

    private void autoApplyNotes() {
        if (selectedObject == null) {
            return;
        }
        String notes = notesArea.getText();
        if (selectedObject.notes().equals(notes)) {
            return;
        }
        selectedObject.setNotes(notes);
        if (selectedObject instanceof TextObject) {
            redrawMap();
        }
        markDirty();
    }

    private void autoApplyCableNotes() {
        if (!(selectedObject instanceof PowerConsumer)) {
            return;
        }
        String cableNotes = cableNotesField.getText();
        PowerConnection connection = selectedPowerConnection();
        if (connection == null || connection.cableNotes().equals(cableNotes.trim())) {
            return;
        }
        plan.updateCableNotesForConnection(connection.id(), cableNotes);
        cableNotesField.setText(plan.findPowerConnection(connection.id())
                .map(PowerConnection::cableNotes)
                .orElse(""));
        redrawMap();
        refreshSummary();
        markDirty();
    }

    private void autoApplyCableLengthNotes() {
        if (!(selectedObject instanceof PowerConsumer)) {
            return;
        }
        String cableLengthNotes = cableLengthNotesField.getText();
        PowerConnection connection = selectedPowerConnection();
        if (connection == null || connection.cableLengthNotes().equals(cableLengthNotes.trim())) {
            return;
        }
        plan.updateCableLengthNotesForConnection(connection.id(), cableLengthNotes);
        cableLengthNotesField.setText(plan.findPowerConnection(connection.id())
                .map(PowerConnection::cableLengthNotes)
                .orElse(""));
        redrawMap();
        refreshSummary();
        markDirty();
    }

    private String objectTypeName(PlannerObject object) {
        if (object instanceof Tent) {
            return "Telk";
        }
        if (object instanceof DistributionPanel) {
            return "Alajaotuskilp";
        }
        if (object instanceof PowerSource) {
            return "Elektrikapp";
        }
        if (object instanceof TextObject) {
            return "Tekst";
        }
        if (object instanceof MarkerObject) {
            return "Marker";
        }
        if (object instanceof AreaObject) {
            return "Ala";
        }
        if (object instanceof LineObject) {
            return "Joon";
        }
        if (object instanceof CustomObject) {
            return "Objekt";
        }
        return "Objekt";
    }

    private void autoApplySelectedName() {
        if (updatingDetailControls || selectedObject == null
                || selectedObject.name().equals(nameField.getText())) {
            return;
        }
        selectedObject.rename(nameField.getText());
        finishAutoAppliedDetailsChange(false);
    }

    private void autoApplySelectedGroup() {
        if (updatingDetailControls || selectedObject == null) {
            return;
        }
        String groupName = groupField.getEditor().getText();
        if (selectedObject.groupName().equals(groupName)) {
            return;
        }
        selectedObject.setGroupName(groupName);
        finishAutoAppliedDetailsChange(true);
    }

    private void autoApplyTentSize() {
        if (updatingDetailControls || !(selectedObject instanceof Tent tent)) {
            return;
        }
        PlanSnapshot before = planSnapshotService.create(plan);
        if (applyTentSize(tent)) {
            finishAutoAppliedDetailsChange(before, false);
        }
    }

    private void autoApplyTentRotation() {
        if (updatingDetailControls || !(selectedObject instanceof Tent tent)) {
            return;
        }
        PlanSnapshot before = planSnapshotService.create(plan);
        if (applyTentRotation(tent)) {
            finishAutoAppliedDetailsChange(before, false);
        }
    }

    private void autoApplyCustomObjectSize() {
        if (updatingDetailControls || !(selectedObject instanceof CustomObject customObject)) {
            return;
        }
        PlanSnapshot before = planSnapshotService.create(plan);
        if (applyCustomObjectSize(customObject)) {
            finishAutoAppliedDetailsChange(before, false);
        }
    }

    private void autoApplyCustomObjectRotation() {
        if (updatingDetailControls || !(selectedObject instanceof CustomObject customObject)) {
            return;
        }
        PlanSnapshot before = planSnapshotService.create(plan);
        if (applyCustomObjectRotation(customObject)) {
            finishAutoAppliedDetailsChange(before, false);
        }
    }

    private void autoApplyCustomObjectShape() {
        if (updatingDetailControls || !(selectedObject instanceof CustomObject customObject)) {
            return;
        }
        CustomObjectShape shape = customObjectShapeComboBox.getSelectionModel().getSelectedItem();
        if (shape == null || shape == customObject.shape()) {
            return;
        }
        PlanSnapshot before = planSnapshotService.create(plan);
        customObject.setShape(shape);
        if (shape == CustomObjectShape.CIRCLE) {
            customObject.setRotationDegrees(0);
        }
        if (applyCustomObjectSize(customObject)) {
            finishAutoAppliedDetailsChange(before, false);
        }
    }

    private void autoApplySelectedColor() {
        if (updatingDetailControls || selectedObject == null) {
            return;
        }
        PlanSnapshot before = planSnapshotService.create(plan);
        if (selectedObject instanceof Tent tent) {
            tent.setColorHex(toHex(tentColorPicker.getValue()));
        } else if (selectedObject instanceof CustomObject customObject) {
            customObject.setColorHex(toHex(customObjectColorPicker.getValue()));
        } else if (selectedObject instanceof TextObject textObject) {
            textObject.setColorHex(toHex(textObjectColorPicker.getValue()));
        } else if (selectedObject instanceof MarkerObject markerObject) {
            markerObject.setColorHex(toHex(markerColorPicker.getValue()));
        } else if (selectedObject instanceof AreaObject areaObject) {
            areaObject.setColorHex(toHex(areaColorPicker.getValue()));
        } else if (selectedObject instanceof LineObject lineObject) {
            lineObject.setColorHex(toHex(lineColorPicker.getValue()));
        }
        finishAutoAppliedDetailsChange(before, false);
    }

    private void autoApplyMarkerType() {
        if (updatingDetailControls || !(selectedObject instanceof MarkerObject markerObject)) {
            return;
        }
        MarkerType markerType = markerTypeComboBox.getSelectionModel().getSelectedItem();
        if (markerType == null || markerType == markerObject.markerType()) {
            return;
        }
        markerObject.setMarkerType(markerType);
        finishAutoAppliedDetailsChange(false);
    }

    private void autoApplySelectedPowerConnection() {
        if (updatingDetailControls || !(selectedObject instanceof PowerConsumer)) {
            return;
        }
        PlanSnapshot before = planSnapshotService.create(plan);
        if (applySelectedPowerSource(selectedObject)) {
            finishAutoAppliedDetailsChange(before, false);
        }
    }

    private void finishAutoAppliedDetailsChange(boolean refreshGroups) {
        finishAutoAppliedDetailsChange(null, refreshGroups);
    }

    private void finishAutoAppliedDetailsChange(PlanSnapshot before, boolean refreshGroups) {
        if (before != null && before.equals(planSnapshotService.create(plan))) {
            return;
        }
        if (refreshGroups) {
            refreshGroupFilters();
        }
        refreshObjectList();
        redrawMap();
        refreshSummary();
        refreshDetails();
        markDirty();
    }

    private void copySelectedObject() {
        if (selectedObject == null) {
            return;
        }
        copyObject(selectedObject);
    }

    private void copyObject(PlannerObject object) {
        copiedObject = copyObjectAt(object, object.position(), object.name());
        keyboardPasteCount = 0;
    }

    private void pasteCopiedObjectWithOffset() {
        if (copiedObject == null) {
            return;
        }
        keyboardPasteCount++;
        pasteCopiedObject(new Position(
                copiedObject.position().x() + 32.0 * keyboardPasteCount,
                copiedObject.position().y() + 32.0 * keyboardPasteCount
        ));
    }

    private void pasteCopiedObject(Position position) {
        if (copiedObject == null) {
            return;
        }
        if (mapLayoutLocked) {
            showMapLayoutLockedMessage();
            return;
        }

        PlannerObject copy = copyObjectAt(copiedObject, position, duplicateName(copiedObject));
        if (copy == null) {
            return;
        }

        plan.addObject(copy);
        refreshGroupFilters();
        selectObject(copy);
        refreshSummary();
        markDirty();
    }

    private PlannerObject copyObjectAt(PlannerObject original, Position copyPosition, String copyName) {
        PlannerObject copy;
        if (original instanceof Tent tent) {
            Tent tentCopy = new Tent(planFactory.newId(), copyName, copyPosition);
            tentCopy.setSizeMeters(tent.widthMeters(), tent.heightMeters());
            tentCopy.setRotationDegrees(tent.rotationDegrees());
            tentCopy.setColorHex(tent.colorHex());
            tentCopy.setOpacity(tent.opacity());
            copy = tentCopy;
        } else if (original instanceof PowerSource source) {
            PowerSource sourceCopy = original instanceof DistributionPanel
                    ? new DistributionPanel(planFactory.newId(), copyName, copyPosition)
                    : new PowerSource(planFactory.newId(), copyName, copyPosition);
            for (PowerOutlet outlet : source.outlets()) {
                sourceCopy.addOutlet(new PowerOutlet(
                        planFactory.newId(),
                        outlet.name(),
                        outlet.type(),
                        outlet.capacityWatts()
                ));
            }
            copy = sourceCopy;
        } else if (original instanceof CustomObject customObject) {
            CustomObject objectCopy = new CustomObject(planFactory.newId(), copyName, copyPosition);
            objectCopy.setShape(customObject.shape());
            objectCopy.setColorHex(customObject.colorHex());
            objectCopy.setOpacity(customObject.opacity());
            objectCopy.setSizeMeters(customObject.widthMeters(), customObject.heightMeters());
            objectCopy.setRotationDegrees(customObject.rotationDegrees());
            copy = objectCopy;
        } else if (original instanceof TextObject textObject) {
            TextObject textCopy = new TextObject(planFactory.newId(), copyName, copyPosition);
            textCopy.setColorHex(textObject.colorHex());
            textCopy.setFontSize(textObject.fontSize());
            copy = textCopy;
        } else if (original instanceof MarkerObject markerObject) {
            MarkerObject markerCopy = new MarkerObject(planFactory.newId(), copyName, copyPosition);
            markerCopy.setMarkerType(markerObject.markerType());
            markerCopy.setColorHex(markerObject.colorHex());
            copy = markerCopy;
        } else if (original instanceof AreaObject areaObject) {
            AreaObject areaCopy = new AreaObject(planFactory.newId(), copyName, copyPosition);
            areaCopy.setColorHex(areaObject.colorHex());
            areaCopy.setOpacity(areaObject.opacity());
            areaCopy.setPoints(offsetPoints(areaObject.points(), copyPosition.x() - areaObject.position().x(), copyPosition.y() - areaObject.position().y()));
            copy = areaCopy;
        } else if (original instanceof LineObject lineObject) {
            LineObject lineCopy = new LineObject(planFactory.newId(), copyName, copyPosition);
            lineCopy.setColorHex(lineObject.colorHex());
            lineCopy.setWidthPixels(lineObject.widthPixels());
            lineCopy.setPoints(offsetPoints(lineObject.points(), copyPosition.x() - lineObject.position().x(), copyPosition.y() - lineObject.position().y()));
            copy = lineCopy;
        } else {
            return null;
        }

        copyCommonDetails(original, copy);
        copyEquipment(original, copy);
        return copy;
    }

    private void copyEquipment(PlannerObject original, PlannerObject copy) {
        if (!(original instanceof EquipmentContainer originalContainer)
                || !(copy instanceof EquipmentContainer copyContainer)) {
            return;
        }
        for (Equipment item : originalContainer.equipment()) {
            copyContainer.addEquipment(new Equipment(item.name(), item.requiredWatts()));
        }
    }

    private void copyCommonDetails(PlannerObject original, PlannerObject copy) {
        copy.setGroupName(original.groupName());
        copy.setNotes(original.notes());
        copy.setShowMapLabel(original.showMapLabel());
        copy.setLocked(false);
        if (original.customMapLabelPosition()) {
            copy.setMapLabelOffset(original.mapLabelOffset());
        }
    }

    private List<Position> offsetPoints(List<Position> points, double deltaX, double deltaY) {
        return points.stream()
                .map(point -> new Position(point.x() + deltaX, point.y() + deltaY))
                .toList();
    }

    private String duplicateName(PlannerObject object) {
        String name = object.name() == null ? "" : object.name().trim();
        if (name.isBlank()) {
            return "Koopia";
        }
        return name + " koopia";
    }

    private void deleteSelectedObject() {
        if (selectedObject == null) {
            return;
        }
        if (mapLayoutLocked) {
            showMapLayoutLockedMessage();
            return;
        }
        if (selectedObject.locked()) {
            showError("Objekti ei kustutatud", "Eemalda enne lukustus ja proovi uuesti.");
            return;
        }
        if (!confirmDeleteSelectedObject()) {
            return;
        }

        plan.removeObject(selectedObject.id());
        selectedObject = null;
        pendingPowerSourceConsumer = null;
        refreshGroupFilters();
        refreshObjectList();
        redrawMap();
        refreshSummary();
        refreshDetails();
        markDirty();
    }

    private boolean confirmDeleteSelectedObject() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(stage);
        alert.setTitle("Kustuta objekt");
        alert.setHeaderText("Kas kustutada \"%s\"?".formatted(selectedObject.name()));
        alert.setContentText(deleteConfirmationText(selectedObject));
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private String deleteConfirmationText(PlannerObject object) {
        List<String> warnings = new ArrayList<>();
        if (object instanceof EquipmentContainer container && !container.equipment().isEmpty()) {
            warnings.add("Objekti seadmed kustutatakse samuti.");
        }
        if (object instanceof PowerConsumer && plan.findPowerConnectionForConsumer(object.id()).isPresent()) {
            warnings.add("Objekti vooluühendus ja kaabli trajektoor kustutatakse samuti.");
        }
        if (object instanceof PowerSource source) {
            int connectionCount = (int) plan.powerConnections().stream()
                    .filter(connection -> connection.sourceId().equals(source.id()))
                    .count();
            if (connectionCount > 0) {
                warnings.add("%d selle kapiga seotud vooluühendus(t) kustutatakse samuti.".formatted(connectionCount));
            }
        }
        if (warnings.isEmpty()) {
            return "Seda tegevust ei saa tagasi võtta.";
        }
        return "%s%n%nSeda tegevust ei saa tagasi võtta.".formatted(String.join(System.lineSeparator(), warnings));
    }

    private void setMeasuringActive(boolean measuringActive) {
        this.measuringActive = measuringActive;
        if (measuringActive) {
            addingCablePoint = false;
            editingCableConnectionId = null;
            if (addCablePointButton != null) {
                addCablePointButton.setSelected(false);
            }
            pendingTentPlacement = false;
            pendingPowerSourcePlacement = false;
            pendingCustomObjectPlacement = false;
            pendingTextObjectPlacement = false;
            pendingMarkerPlacement = false;
            pendingLineObjectPlacement = false;
            pendingAreaObjectPlacement = false;
            clearPendingPlacementDetails();
            refreshPlacementButtons();
        }
        measurementStart = null;
        updateMapToolStatus();
    }

    private void setAddingCablePoint(boolean addingCablePoint) {
        this.addingCablePoint = addingCablePoint;
        if (!addingCablePoint) {
            editingCableConnectionId = null;
        }
        if (addingCablePoint) {
            measuringActive = false;
            if (measureButton != null) {
                measureButton.setSelected(false);
            }
            pendingTentPlacement = false;
            pendingPowerSourcePlacement = false;
            pendingCustomObjectPlacement = false;
            pendingTextObjectPlacement = false;
            pendingMarkerPlacement = false;
            pendingLineObjectPlacement = false;
            pendingAreaObjectPlacement = false;
            clearPendingPlacementDetails();
            pendingPowerSourceConsumer = null;
            refreshPlacementButtons();
        }
        measurementStart = null;
        refreshDetails();
        updateMapToolStatus();
    }

    private void addCableRoutePoint(Position point) {
        if (!(selectedObject instanceof PowerConsumer)) {
            showError("Kaabli punkti ei lisatud", "Vali enne elektritarbija, mille voolukaablile punkt lisada.");
            setAddingCablePoint(false);
            if (addCablePointButton != null) {
                addCablePointButton.setSelected(false);
            }
            return;
        }
        if (plan.findPowerConnectionForConsumer(selectedObject.id()).isEmpty()) {
            showError("Kaabli punkti ei lisatud", "Valitud objektil ei ole veel vooluühendust.");
            setAddingCablePoint(false);
            if (addCablePointButton != null) {
                addCablePointButton.setSelected(false);
            }
            return;
        }
        PowerCableView editedCable = editingCableConnectionId == null
                ? null
                : powerCableView(editingCableConnectionId).orElse(null);
        if (editingCableConnectionId != null && editedCable == null) {
            showError("Kaabli punkti ei lisatud", "Valitud vooluühendust ei leitud.");
            setAddingCablePoint(false);
            if (addCablePointButton != null) {
                addCablePointButton.setSelected(false);
            }
            return;
        }
        if (editedCable != null) {
            insertCableRoutePoint(editedCable, point);
            return;
        }
        if (!CableRouteEditor.addPoint(plan, selectedObject.id(), point)) {
            return;
        }
        redrawMap();
        refreshSummary();
        markDirty();
    }

    private Optional<PowerCableView> powerCableView(String connectionId) {
        return plan.powerConnections().stream()
                .filter(connection -> connection.id().equals(connectionId))
                .findFirst()
                .flatMap(connection -> plan.findObject(connection.consumerId())
                        .flatMap(consumer -> plan.findObject(connection.sourceId())
                                .filter(PowerSource.class::isInstance)
                                .map(PowerSource.class::cast)
                                .map(source -> new PowerCableView(consumer, source, connection))));
    }

    private void insertCableRoutePoint(PowerCableView cable, Position point) {
        if (!CableRouteEditor.insertPointForConnection(
                plan,
                cable.connection().id(),
                cablePath(cable),
                point
        )) {
            return;
        }
        selectedObject = cable.consumer();
        redrawMap();
        refreshSummary();
        markDirty();
    }

    private void clearSelectedCableRoute() {
        if (mapLayoutLocked) {
            showMapLayoutLockedMessage();
            return;
        }
        if (!(selectedObject instanceof PowerConsumer)) {
            return;
        }
        if (plan.findPowerConnectionForConsumer(selectedObject.id()).isEmpty()) {
            return;
        }
        if (!CableRouteEditor.clearRoute(plan, selectedObject.id())) {
            return;
        }
        redrawMap();
        refreshSummary();
        markDirty();
    }

    private void refreshPlacementButtons() {
        boolean placementPending = isPlacementPending();
        if (placementTypeComboBox != null) {
            placementTypeComboBox.setDisable(placementPending || mapLayoutLocked);
            if (pendingTentPlacement) {
                placementTypeComboBox.getSelectionModel().select(PlacementType.TENT);
            } else if (pendingPowerSourcePlacement) {
                placementTypeComboBox.getSelectionModel().select(PlacementType.POWER_SOURCE);
            } else if (pendingCustomObjectPlacement) {
                placementTypeComboBox.getSelectionModel().select(PlacementType.CUSTOM_OBJECT);
            } else if (pendingTextObjectPlacement) {
                placementTypeComboBox.getSelectionModel().select(PlacementType.TEXT_OBJECT);
            } else if (pendingMarkerPlacement) {
                placementTypeComboBox.getSelectionModel().select(PlacementType.MARKER_OBJECT);
            } else if (pendingLineObjectPlacement) {
                placementTypeComboBox.getSelectionModel().select(PlacementType.LINE_OBJECT);
            } else if (pendingAreaObjectPlacement) {
                placementTypeComboBox.getSelectionModel().select(PlacementType.AREA_OBJECT);
            }
        }
        if (addPlacementButton != null) {
            addPlacementButton.setText(placementButtonText(placementPending));
            addPlacementButton.setDisable(mapLayoutLocked);
        }
    }

    private String placementButtonText(boolean placementPending) {
        if (!placementPending) {
            return "Lisa";
        }
        if (isShapePlacementPending() && canFinishPendingShapePlacement()) {
            return "Lõpeta";
        }
        return "Tühista";
    }

    private boolean isPlacementPending() {
        return pendingTentPlacement
                || pendingPowerSourcePlacement
                || pendingCustomObjectPlacement
                || pendingTextObjectPlacement
                || pendingMarkerPlacement
                || pendingLineObjectPlacement
                || pendingAreaObjectPlacement;
    }

    private void cancelPlacement() {
        pendingTentPlacement = false;
        pendingPowerSourcePlacement = false;
        pendingCustomObjectPlacement = false;
        pendingTextObjectPlacement = false;
        pendingMarkerPlacement = false;
        pendingLineObjectPlacement = false;
        pendingAreaObjectPlacement = false;
        pendingPowerSourceConsumer = null;
        clearPendingPlacementDetails();
        refreshPlacementButtons();
        updateMapToolStatus();
        redrawMap();
    }

    private void handleMeasureClick(Position point) {
        if (measurementStart == null) {
            measurementStart = point;
            Circle marker = createMeasurementMarker(point);
            measurementNodes.add(marker);
            mapPane.getChildren().add(marker);
            return;
        }

        Position end = point;
        Line line = new Line(measurementStart.x(), measurementStart.y(), end.x(), end.y());
        line.setStroke(Color.web("#111827"));
        line.setStrokeWidth(2);

        Circle endMarker = createMeasurementMarker(end);
        Label distanceLabel = new Label("%.2f m".formatted(distanceMeters(measurementStart, end)));
        distanceLabel.setStyle("-fx-background-color: white; -fx-padding: 2 4 2 4;");
        distanceLabel.setLayoutX((measurementStart.x() + end.x()) / 2 + 6);
        distanceLabel.setLayoutY((measurementStart.y() + end.y()) / 2 + 6);

        measurementNodes.add(line);
        measurementNodes.add(endMarker);
        measurementNodes.add(distanceLabel);
        measurements.add(new MeasurementView(measurementStart, end, distanceLabel));
        mapPane.getChildren().addAll(line, endMarker, distanceLabel);
        measurementStart = null;
    }

    private void refreshMeasurementLabels() {
        for (MeasurementView measurement : measurements) {
            measurement.distanceLabel().setText("%.2f m".formatted(distanceMeters(measurement.start(), measurement.end())));
        }
    }

    private boolean setScaleFromLastMeasurement() {
        if (measurements.isEmpty()) {
            showError("Mõõtkava ei muudetud", "Tee enne mõõdulindiga üks mõõtmine.");
            return false;
        }

        MeasurementView measurement = measurements.getLast();
        TextInputDialog dialog = new TextInputDialog("%.2f".formatted(distanceMeters(measurement.start(), measurement.end())));
        dialog.initOwner(stage);
        dialog.setTitle("Määra mõõtkava");
        dialog.setHeaderText("Sisesta viimase mõõdulindi joone tegelik pikkus meetrites");
        dialog.setContentText("Tegelik pikkus m:");
        String value = dialog.showAndWait().orElse(null);
        if (value == null) {
            return false;
        }

        try {
            double realLengthMeters = Double.parseDouble(value.trim().replace(',', '.'));
            if (realLengthMeters <= 0) {
                throw new IllegalArgumentException("Tegelik pikkus peab olema positiivne.");
            }
            double pixelLength = distancePixels(measurement.start(), measurement.end());
            if (pixelLength <= 0) {
                showError("Mõõtkava ei muudetud", "Mõõdulindi pikkus peab olema suurem kui 0.");
                return false;
            }
            plan.setPixelsPerMeter(pixelLength / realLengthMeters);
            if (pixelsPerMeterField != null) {
                pixelsPerMeterField.setText(formatMeters(plan.pixelsPerMeter()));
            }
            refreshMeasurementLabels();
            redrawMap();
            refreshSummary();
            refreshDetails();
            refreshObjectList();
            markDirty();
            return true;
        } catch (NumberFormatException exception) {
            showError("Mõõtkava ei muudetud", "Sisesta tegelik pikkus arvuna meetrites.");
        } catch (IllegalArgumentException exception) {
            showError("Mõõtkava ei muudetud", exception.getMessage());
        }
        return false;
    }

    private Circle createMeasurementMarker(Position point) {
        Circle marker = new Circle(point.x(), point.y(), 4);
        marker.setFill(Color.web("#111827"));
        marker.setStroke(Color.WHITE);
        return marker;
    }

    private double distanceMeters(Position start, Position end) {
        return distancePixels(start, end) / pixelsPerMeter();
    }

    private void refreshAreaMeasurements(AreaObject object) {
        areaSizeLabel.setText("%.1f m²".formatted(
                GeometryCalculator.polygonAreaSquareMeters(object.points(), pixelsPerMeter())
        ));
        areaPerimeterLabel.setText("%.1f m".formatted(
                GeometryCalculator.polygonPerimeterMeters(object.points(), pixelsPerMeter())
        ));
    }

    private void refreshLineLengthLabel(LineObject object) {
        lineLengthLabel.setText("%.1f m".formatted(
                GeometryCalculator.lineLengthMeters(object.points(), pixelsPerMeter())
        ));
    }

    private void refreshCustomObjectMeasurements(CustomObject object) {
        customObjectAreaLabel.setText("%.1f m²".formatted(
                GeometryCalculator.customObjectAreaSquareMeters(object)
        ));
        customObjectPerimeterLabel.setText("%.1f m".formatted(
                GeometryCalculator.customObjectPerimeterMeters(object)
        ));
    }

    private double distancePixels(Position start, Position end) {
        double deltaX = end.x() - start.x();
        double deltaY = end.y() - start.y();
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    private double metersToPixels(double meters) {
        return meters * pixelsPerMeter();
    }

    private double pixelsPerMeter() {
        return plan == null ? EventPlan.DEFAULT_PIXELS_PER_METER : plan.pixelsPerMeter();
    }

    private void clearMeasurements() {
        measurementNodes.clear();
        measurements.clear();
        measurementStart = null;
        redrawMap();
    }

    private boolean applyTentSize(Tent tent) {
        try {
            double widthMeters = Double.parseDouble(tentWidthField.getText().trim().replace(',', '.'));
            double heightMeters = Double.parseDouble(tentHeightField.getText().trim().replace(',', '.'));
            tent.setSizeMeters(widthMeters, heightMeters);
            return true;
        } catch (NumberFormatException exception) {
            showError("Mõõte ei muudetud", "Sisesta telgi laius ja pikkus arvuna meetrites.");
            return false;
        } catch (IllegalArgumentException exception) {
            showError("Mõõte ei muudetud", exception.getMessage());
            return false;
        }
    }

    private boolean applyTentRotation(Tent tent) {
        try {
            double rotationDegrees = Double.parseDouble(tentRotationField.getText().trim().replace(',', '.'));
            tent.setRotationDegrees(rotationDegrees);
            return true;
        } catch (NumberFormatException exception) {
            showError("Pööret ei muudetud", "Sisesta telgi pööre arvuna kraadides.");
            return false;
        }
    }

    private boolean applyCustomObjectSize(CustomObject object) {
        try {
            CustomObjectShape selectedShape = customObjectShapeComboBox.getSelectionModel().getSelectedItem();
            double widthMeters = Double.parseDouble(customObjectWidthField.getText().trim().replace(',', '.'));
            if (selectedShape == CustomObjectShape.CIRCLE) {
                object.setSizeMeters(widthMeters, widthMeters);
                return true;
            }
            double heightMeters = Double.parseDouble(customObjectHeightField.getText().trim().replace(',', '.'));
            object.setSizeMeters(widthMeters, heightMeters);
            return true;
        } catch (NumberFormatException exception) {
            if (customObjectShapeComboBox.getSelectionModel().getSelectedItem() == CustomObjectShape.CIRCLE) {
                showError("Mõõte ei muudetud", "Sisesta objekti läbimõõt arvuna meetrites.");
            } else {
                showError("Mõõte ei muudetud", "Sisesta objekti laius ja pikkus arvuna meetrites.");
            }
            return false;
        } catch (IllegalArgumentException exception) {
            showError("Mõõte ei muudetud", exception.getMessage());
            return false;
        }
    }

    private boolean applyCustomObjectRotation(CustomObject object) {
        CustomObjectShape selectedShape = customObjectShapeComboBox.getSelectionModel().getSelectedItem();
        if (selectedShape == CustomObjectShape.CIRCLE) {
            object.setRotationDegrees(0);
            return true;
        }
        try {
            double rotationDegrees = Double.parseDouble(customObjectRotationField.getText().trim().replace(',', '.'));
            object.setRotationDegrees(rotationDegrees);
            return true;
        } catch (NumberFormatException exception) {
            showError("Pööret ei muudetud", "Sisesta objekti pööre arvuna kraadides.");
            return false;
        }
    }

    private String formatMeters(double meters) {
        if (meters == Math.rint(meters)) {
            return "%.0f".formatted(meters);
        }
        return "%.2f".formatted(meters);
    }

    private String formatDegrees(double degrees) {
        if (degrees == Math.rint(degrees)) {
            return "%.0f".formatted(degrees);
        }
        return "%.2f".formatted(degrees);
    }

    private String formatNumber(double value) {
        if (value == Math.rint(value)) {
            return "%.0f".formatted(value);
        }
        return "%.1f".formatted(value);
    }

    private Position averagePosition(List<Position> points) {
        if (points.isEmpty()) {
            return new Position(0, 0);
        }
        double totalX = 0;
        double totalY = 0;
        for (Position point : points) {
            totalX += point.x();
            totalY += point.y();
        }
        return new Position(totalX / points.size(), totalY / points.size());
    }

    private Position rotationOffset(double width, double height, double degrees) {
        double radians = Math.toRadians(degrees);
        double sin = Math.abs(Math.sin(radians));
        double cos = Math.abs(Math.cos(radians));
        double rotatedWidth = width * cos + height * sin;
        double rotatedHeight = width * sin + height * cos;
        return new Position((rotatedWidth - width) / 2, (rotatedHeight - height) / 2);
    }

    private void refreshPowerSourceChoices() {
        powerSourceComboBox.getItems().clear();
        powerSourceComboBox.getItems().add(PowerSourceChoice.none());
        Map<String, PowerSummary> summariesBySource = powerSummaryService.summaries(plan).stream()
                .collect(java.util.stream.Collectors.toMap(PowerSummary::sourceId, summary -> summary));
        for (PowerSource source : plan.powerSources()) {
            if (selectedObject != null && source.id().equals(selectedObject.id())) {
                continue;
            }
            PowerSummary summary = summariesBySource.get(source.id());
            powerSourceComboBox.getItems().add(new PowerSourceChoice(
                    source.id(),
                    source.name(),
                    summary == null ? 0 : summary.usedWatts(),
                    summary == null ? 0 : summary.capacityWatts()
            ));
        }

        powerSourceComboBox.getSelectionModel().selectFirst();
        refreshConnectionOutletChoices(null);
        if (!(selectedObject instanceof PowerConsumer)) {
            return;
        }

        Optional.ofNullable(selectedPowerConnection()).ifPresent(connection -> {
            powerSourceComboBox.getItems().stream()
                    .filter(choice -> choice.sourceId().equals(connection.sourceId()))
                    .findFirst()
                    .ifPresent(choice -> powerSourceComboBox.getSelectionModel().select(choice));
            refreshConnectionOutletChoices(connection.outletId());
        });
    }

    private ListCell<PowerSourceChoice> createPowerSourceChoiceCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(PowerSourceChoice choice, boolean empty) {
                super.updateItem(choice, empty);
                setText(null);
                setGraphic(null);
                if (empty || choice == null) {
                    return;
                }
                if (choice.isNone()) {
                    setText(choice.name());
                    return;
                }
                PowerLoadLevel loadLevel = PowerLoadLevel.from(choice.usedWatts(), choice.capacityWatts());
                Label label = new Label(choice.displayText());
                label.setStyle(loadLevel == PowerLoadLevel.OVERLOADED
                        ? "-fx-text-fill: #b91c1c; -fx-font-weight: bold;"
                        : "-fx-text-fill: #1f2937;");
                ProgressBar bar = new ProgressBar(choice.progress());
                bar.setMaxWidth(Double.MAX_VALUE);
                bar.setStyle("-fx-accent: %s;".formatted(loadLevel.colorHex()));
                setGraphic(new VBox(3, label, bar));
            }
        };
    }

    private void refreshPowerConnectionChoices(String preferredConnectionId) {
        String currentConnectionId = preferredConnectionId != null
                ? preferredConnectionId
                : selectedPowerConnectionId();
        powerConnectionComboBox.getItems().clear();
        if (!(selectedObject instanceof PowerConsumer)) {
            return;
        }
        for (PowerConnection connection : plan.findPowerConnectionsForConsumer(selectedObject.id())) {
            powerConnectionComboBox.getItems().add(new PowerConnectionChoice(
                    connection.id(), powerConnectionChoiceName(connection), connection.defaultForConsumer()
            ));
        }
        if (powerConnectionComboBox.getItems().isEmpty()) {
            return;
        }
        powerConnectionComboBox.getItems().stream()
                .filter(choice -> choice.connectionId().equals(currentConnectionId))
                .findFirst()
                .ifPresentOrElse(
                        choice -> powerConnectionComboBox.getSelectionModel().select(choice),
                        () -> powerConnectionComboBox.getSelectionModel().selectFirst()
                );
    }

    private String powerConnectionChoiceName(PowerConnection connection) {
        PowerSource source = plan.findObject(connection.sourceId())
                .filter(PowerSource.class::isInstance)
                .map(PowerSource.class::cast)
                .orElse(null);
        if (source == null) {
            return connection.defaultForConsumer() ? "Põhiühendus" : "Alternatiivühendus";
        }
        PowerOutlet outlet = source.outlets().stream()
                .filter(item -> item.id().equals(connection.outletId()))
                .findFirst()
                .orElse(null);
        String outletName = outlet == null
                ? connection.connectorType().displayName()
                : outletDisplayName(outlet, outletTypeIndex(source, outlet, source.outlets().indexOf(outlet)));
        return "%s — %s / %s".formatted(
                connection.defaultForConsumer() ? "Põhiühendus" : "Alternatiiv",
                source.name(),
                outletName
        );
    }

    private void refreshConnectionOutletChoices(String preferredOutletId) {
        String currentOutletId = preferredOutletId != null
                ? preferredOutletId
                : selectedConnectionOutletId();
        connectionOutletComboBox.getItems().clear();

        PowerSource selectedSource = selectedPowerSource();
        if (selectedSource == null) {
            return;
        }

        connectionOutletComboBox.getItems().addAll(outletChoices(selectedSource));

        if (connectionOutletComboBox.getItems().isEmpty()) {
            return;
        }
        connectionOutletComboBox.getItems().stream()
                .filter(choice -> choice.outletId().equals(currentOutletId))
                .findFirst()
                .ifPresentOrElse(
                        choice -> connectionOutletComboBox.getSelectionModel().select(choice),
                        () -> connectionOutletComboBox.getSelectionModel().selectFirst()
                );
    }

    private List<OutletChoice> outletChoices(PowerSource source) {
        List<OutletChoice> choices = new ArrayList<>();
        Map<ConnectorType, Integer> typeIndexes = new EnumMap<>(ConnectorType.class);
        for (PowerOutlet outlet : source.outlets()) {
            int matchingIndex = typeIndexes.merge(outlet.type(), 1, Integer::sum);
            int usedWatts = usedWatts(outlet.id());
            choices.add(new OutletChoice(
                    outlet.id(),
                    outlet.type(),
                    outletLabel(outlet, matchingIndex),
                    usedWatts,
                    outlet.capacityWatts()
            ));
        }
        return choices;
    }

    private ListCell<OutletChoice> createOutletChoiceCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(OutletChoice choice, boolean empty) {
                super.updateItem(choice, empty);
                setText(null);
                setGraphic(null);
                if (empty || choice == null) {
                    return;
                }

                PowerLoadLevel loadLevel = PowerLoadLevel.from(choice.usedWatts(), choice.capacityWatts());
                Label loadLabel = new Label(choice.displayText());
                loadLabel.setStyle(loadLevel == PowerLoadLevel.OVERLOADED
                        ? "-fx-text-fill: #b91c1c; -fx-font-weight: bold;"
                        : "-fx-text-fill: #1f2937;");
                ProgressBar loadBar = new ProgressBar(choice.progress());
                loadBar.setMaxWidth(Double.MAX_VALUE);
                loadBar.setStyle("-fx-accent: %s;".formatted(loadLevel.colorHex()));
                VBox loadContent = new VBox(3, loadLabel, loadBar);
                setGraphic(loadContent);
            }
        };
    }

    private String outletLabel(PowerOutlet outlet, int matchingIndex) {
        int usedWatts = usedWatts(outlet.id());
        return "%s - %d W kasutusel, %s".formatted(
                outletDisplayName(outlet, matchingIndex),
                usedWatts,
                remainingWattsText(outlet.capacityWatts() - usedWatts)
        );
    }

    private String outletDisplayName(PowerOutlet outlet, int matchingIndex) {
        String automaticName = "%s %d".formatted(outlet.type().displayName(), matchingIndex);
        return outlet.name().isBlank()
                ? automaticName
                : "%s (%s)".formatted(outlet.name(), automaticName);
    }

    private String remainingWattsText(int remainingWatts) {
        if (remainingWatts < 0) {
            return "ÜLEKOORMUS %d W".formatted(Math.abs(remainingWatts));
        }
        return "%d W alles".formatted(remainingWatts);
    }

    private int outletTypeIndex(PowerSource source, PowerOutlet targetOutlet, int targetIndex) {
        int matchingIndex = 0;
        for (int index = 0; index <= targetIndex; index++) {
            PowerOutlet outlet = source.outlets().get(index);
            if (outlet.type() == targetOutlet.type()) {
                matchingIndex++;
            }
        }
        return matchingIndex;
    }

    private PowerSource selectedPowerSource() {
        PowerSourceChoice selectedSource = powerSourceComboBox.getSelectionModel().getSelectedItem();
        if (selectedSource == null || selectedSource.isNone()) {
            return null;
        }
        return plan.findObject(selectedSource.sourceId())
                .filter(PowerSource.class::isInstance)
                .map(PowerSource.class::cast)
                .orElse(null);
    }

    private boolean applySelectedPowerSource(PlannerObject consumer) {
        PowerConnection editedConnection = selectedPowerConnection();
        PowerSourceChoice selectedSource = powerSourceComboBox.getSelectionModel().getSelectedItem();
        if (selectedSource == null || selectedSource.isNone()) {
            if (editedConnection != null) {
                plan.disconnectPowerConnection(editedConnection.id());
            } else {
                plan.disconnectPower(consumer.id());
            }
            return true;
        }

        PowerConnectionValidationResult validationResult = plan.validatePowerConnection(
                selectedSource.sourceId(),
                consumer.id(),
                selectedConnectionType(),
                selectedConnectionOutletId()
        );
        if (validationResult != PowerConnectionValidationResult.VALID) {
            showError("Vooluallikat ei rakendatud", powerConnectionValidationMessage(validationResult));
            return false;
        }

        boolean connected = editedConnection == null
                ? plan.connectToPower(
                        selectedSource.sourceId(), consumer.id(), selectedConnectionType(),
                        selectedConnectionOutletId(), cableNotesField.getText(), cableLengthNotesField.getText()
                ).isPresent()
                : plan.updatePowerConnection(
                        editedConnection.id(), selectedSource.sourceId(), selectedConnectionType(),
                        selectedConnectionOutletId(), cableNotesField.getText(), cableLengthNotesField.getText()
                );
        if (!connected) {
            showError("Vooluallikat ei rakendatud", "Vooluühendust ei õnnestunud luua.");
            return false;
        }
        return true;
    }

    private String powerConnectionValidationMessage(PowerConnectionValidationResult validationResult) {
        return switch (validationResult) {
            case SOURCE_NOT_FOUND -> "Valitud vooluallikat ei leitud.";
            case CONSUMER_NOT_FOUND -> "Valitud elektritarbijat ei leitud.";
            case SELF_CONNECTION -> "Alajaotuskilpi ei saa ühendada iseendaga.";
            case CYCLE_DETECTED -> "Seda ühendust ei saa luua, sest see tekitaks elektrikilpide vahel tsükli.";
            case NO_COMPATIBLE_OUTLET -> "Valitud kapis ei ole selle ühenduse jaoks sobivat väljundit.";
            case VALID -> "";
        };
    }

    private ConnectorType selectedConnectionType() {
        OutletChoice selectedOutlet = connectionOutletComboBox.getSelectionModel().getSelectedItem();
        return selectedOutlet == null ? ConnectorType.SCHUKO_230V : selectedOutlet.connectorType();
    }

    private String selectedConnectionOutletId() {
        OutletChoice selectedOutlet = connectionOutletComboBox.getSelectionModel().getSelectedItem();
        return selectedOutlet == null ? "" : selectedOutlet.outletId();
    }

    private String selectedPowerConnectionId() {
        if (powerConnectionComboBox == null) {
            return "";
        }
        PowerConnectionChoice choice = powerConnectionComboBox.getSelectionModel().getSelectedItem();
        return choice == null ? "" : choice.connectionId();
    }

    private PowerConnection selectedPowerConnection() {
        String connectionId = selectedPowerConnectionId();
        return connectionId.isBlank() ? null : plan.findPowerConnection(connectionId).orElse(null);
    }

    private void selectPowerConnection(String connectionId) {
        if (connectionId == null || connectionId.isBlank()) {
            return;
        }
        powerConnectionComboBox.getItems().stream()
                .filter(choice -> choice.connectionId().equals(connectionId))
                .findFirst()
                .ifPresent(choice -> powerConnectionComboBox.getSelectionModel().select(choice));
        redrawMap();
    }

    private ListCell<String> createEquipmentListCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                setGraphic(null);
                if (empty) {
                    setContextMenu(null);
                    return;
                }
                Label equipmentLabel = new Label(item);
                equipmentAt(getIndex())
                        .flatMap(PlaaniseppApp.this::equipmentPowerConnection)
                        .flatMap(connection -> plan.findObject(connection.sourceId())
                                .filter(PowerSource.class::isInstance)
                                .map(PowerSource.class::cast)
                                .flatMap(source -> source.outlets().stream()
                                        .filter(outlet -> outlet.id().equals(connection.outletId()))
                                        .findFirst()
                                        .map(outlet -> new EquipmentSupplyView(
                                                powerConnectionDisplayName(connection),
                                                usedWatts(outlet.id()),
                                                outlet.capacityWatts()
                                        ))))
                        .ifPresentOrElse(supply -> {
                            PowerLoadLevel loadLevel = PowerLoadLevel.from(
                                    supply.usedWatts(), supply.capacityWatts());
                            Label supplyLabel = new Label(supply.displayText());
                            supplyLabel.setStyle(loadLevel == PowerLoadLevel.OVERLOADED
                                    ? "-fx-text-fill: #b91c1c; -fx-font-weight: bold;"
                                    : "-fx-text-fill: #4b5563;");
                            ProgressBar bar = new ProgressBar(supply.progress());
                            bar.setMaxWidth(Double.MAX_VALUE);
                            bar.setStyle("-fx-accent: %s;".formatted(loadLevel.colorHex()));
                            setGraphic(new VBox(3, equipmentLabel, supplyLabel, bar));
                        }, () -> setGraphic(equipmentLabel));
                MenuItem editItem = new MenuItem("Muuda");
                editItem.setOnAction(event -> equipmentAt(getIndex()).ifPresent(equipment -> {
                    equipmentList.getSelectionModel().select(getIndex());
                    showEquipmentDialog(equipment);
                }));
                MenuItem removeItem = new MenuItem("Eemalda");
                removeItem.setOnAction(event -> equipmentAt(getIndex()).ifPresent(PlaaniseppApp.this::removeEquipment));
                setContextMenu(new ContextMenu(editItem, removeItem));
            }
        };
    }

    private Optional<PowerConnection> equipmentPowerConnection(Equipment equipment) {
        if (selectedObject == null) {
            return Optional.empty();
        }
        if (equipment.usesDefaultPower()) {
            return plan.findPowerConnectionForConsumer(selectedObject.id());
        }
        return plan.findPowerConnection(equipment.powerConnectionId());
    }

    private Optional<Equipment> equipmentAt(int index) {
        EquipmentContainer container = selectedEquipmentContainer();
        if (container == null || index < 0 || index >= container.equipment().size()) {
            return Optional.empty();
        }
        return Optional.of(container.equipment().get(index));
    }

    private void showEquipmentDialog(Equipment equipment) {
        EquipmentContainer container = selectedEquipmentContainer();
        if (container == null) {
            return;
        }
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(stage);
        dialog.setTitle(equipment == null ? "Lisa seade" : "Muuda seadet");
        dialog.setHeaderText(equipment == null ? "Sisesta lisatava seadme andmed" : "Muuda seadme andmeid");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField nameField = new TextField(equipment == null ? "" : equipment.name());
        TextField wattsField = new TextField(equipment == null ? "" : Integer.toString(equipment.requiredWatts()));
        ComboBox<EquipmentPowerChoice> powerChoiceBox = new ComboBox<>();
        powerChoiceBox.setMaxWidth(Double.MAX_VALUE);
        powerChoiceBox.setCellFactory(list -> createEquipmentPowerChoiceCell());
        powerChoiceBox.setButtonCell(createEquipmentPowerChoiceCell());
        List<EquipmentPowerChoice> powerChoices = equipmentPowerChoices();
        powerChoiceBox.getItems().addAll(powerChoices);
        powerChoices.stream()
                .filter(choice -> equipment == null || equipment.usesDefaultPower()
                        ? choice.isDefault()
                        : choice.connectionId().equals(equipment.powerConnectionId()))
                .findFirst()
                .ifPresentOrElse(
                        choice -> powerChoiceBox.getSelectionModel().select(choice),
                        () -> powerChoiceBox.getSelectionModel().selectFirst()
                );

        GridPane form = detailGrid();
        form.addRow(0, new Label("Nimi"), nameField);
        form.addRow(1, new Label("Võimsus W"), wattsField);
        form.addRow(2, new Label("Toide"), powerChoiceBox);
        dialog.getDialogPane().setContent(form);

        while (dialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            String name = nameField.getText().trim();
            if (name.isBlank()) {
                showError("Seadet ei salvestatud", "Sisesta seadme nimi.");
                continue;
            }
            int watts;
            try {
                watts = Integer.parseInt(wattsField.getText().trim());
            } catch (NumberFormatException exception) {
                showError("Seadet ei salvestatud", "Sisesta võimsus täisarvuna vattides.");
                continue;
            }
            if (watts < 0) {
                showError("Seadet ei salvestatud", "Vooluvajadus ei saa olla negatiivne.");
                continue;
            }
            Equipment savedEquipment = equipment;
            if (savedEquipment == null) {
                savedEquipment = new Equipment(name, watts);
                container.addEquipment(savedEquipment);
            } else {
                savedEquipment.rename(name);
                savedEquipment.setRequiredWatts(watts);
            }
            applyEquipmentPower(savedEquipment, powerChoiceBox.getSelectionModel().getSelectedItem());
            refreshEquipmentList();
            redrawMap();
            refreshSummary();
            markDirty();
            return;
        }
    }

    private void removeEquipment(Equipment equipment) {
        EquipmentContainer container = selectedEquipmentContainer();
        if (container == null) {
            return;
        }
        for (int index = 0; index < container.equipment().size(); index++) {
            if (container.equipment().get(index).id().equals(equipment.id())) {
                container.removeEquipment(index);
                refreshEquipmentList();
                refreshSummary();
                markDirty();
                return;
            }
        }
    }

    private void addAlternativePowerConnection() {
        if (!(selectedObject instanceof EquipmentContainer) || !(selectedObject instanceof PowerConsumer)) {
            return;
        }
        PowerConnection template = selectedPowerConnection();
        if (template == null) {
            showError("Alternatiivühendust ei lisatud", "Objektil peab enne olema põhiühendus.");
            return;
        }
        PowerConnection createdConnection = plan.addAlternativePowerConnection(
                template.sourceId(),
                template.consumerId(),
                template.connectorType(),
                template.outletId(),
                "",
                "",
                ""
        ).orElse(null);
        if (createdConnection == null) {
            showError("Alternatiivühendust ei lisatud", "Objektil peab enne olema vaiketoide.");
            return;
        }

        refreshDetails();
        powerConnectionComboBox.getItems().stream()
                .filter(item -> item.connectionId().equals(createdConnection.id()))
                .findFirst()
                .ifPresent(item -> powerConnectionComboBox.getSelectionModel().select(item));
        refreshDetails();
        redrawMap();
        refreshSummary();
        markDirty();
    }

    private void applyEquipmentPower(Equipment equipment, EquipmentPowerChoice selectedPower) {
        if (selectedObject == null || equipment == null || selectedPower == null) {
            return;
        }

        EquipmentPowerAssignmentResult result = selectedPower.isDefault()
                ? plan.useDefaultPowerForEquipment(selectedObject.id(), equipment.id())
                : plan.assignEquipmentToPowerConnection(
                        selectedObject.id(), equipment.id(), selectedPower.connectionId()
                );
        if (result != EquipmentPowerAssignmentResult.SUCCESS) {
            throw new IllegalStateException(equipmentPowerAssignmentError(result));
        }
    }

    private void removeSelectedPowerConnection() {
        PowerConnection connection = selectedPowerConnection();
        if (connection == null || !plan.disconnectPowerConnection(connection.id())) {
            return;
        }
        refreshDetails();
        redrawMap();
        refreshSummary();
        markDirty();
    }

    private void makeSelectedPowerConnectionDefault() {
        PowerConnection connection = selectedPowerConnection();
        if (connection == null || connection.defaultForConsumer()
                || !plan.makeDefaultPowerConnection(connection.id())) {
            return;
        }
        refreshPowerConnectionChoices(connection.id());
        refreshDetails();
        redrawMap();
        refreshSummary();
        markDirty();
    }

    private String equipmentPowerAssignmentError(EquipmentPowerAssignmentResult result) {
        return switch (result) {
            case CONTAINER_NOT_FOUND -> "Valitud objekti ei leitud.";
            case EQUIPMENT_NOT_FOUND -> "Valitud seadet ei leitud.";
            case CONNECTION_NOT_FOUND -> "Valitud vooluühendust ei leitud.";
            case CONNECTION_BELONGS_TO_ANOTHER_CONSUMER ->
                    "Valitud vooluühendus ei kuulu sellele objektile.";
            case SUCCESS -> "";
        };
    }

    private void addOutletToSelectedPowerSource() {
        if (!(selectedObject instanceof PowerSource source)) {
            return;
        }

        ConnectorType selectedType = outletTypeComboBox.getSelectionModel().getSelectedItem();
        if (selectedType == null) {
            showError("Väljundit ei lisatud", "Vali väljundi tüüp.");
            return;
        }

        int capacityWatts;
        try {
            capacityWatts = Integer.parseInt(outletCapacityWattsField.getText().trim());
        } catch (NumberFormatException exception) {
            showError("Väljundit ei lisatud", "Sisesta võimsus täisarvuna vattides.");
            return;
        }

        if (capacityWatts <= 0) {
            showError("Väljundit ei lisatud", "Võimsus peab olema positiivne.");
            return;
        }

        source.addOutlet(new PowerOutlet(planFactory.newId(), outletNameField.getText(), selectedType, capacityWatts));
        outletNameField.clear();
        updateDefaultOutletCapacity();
        refreshOutletList();
        refreshSummary();
        markDirty();
    }

    private void updateSelectedOutlet() {
        PowerOutlet outlet = selectedOutlet();
        if (outlet == null) {
            return;
        }

        ConnectorType selectedType = outletTypeComboBox.getSelectionModel().getSelectedItem();
        if (selectedType == null) {
            showError("Väljundit ei muudetud", "Vali väljundi tüüp.");
            return;
        }

        int capacityWatts;
        try {
            capacityWatts = Integer.parseInt(outletCapacityWattsField.getText().trim());
        } catch (NumberFormatException exception) {
            showError("Väljundit ei muudetud", "Sisesta võimsus täisarvuna vattides.");
            return;
        }

        if (capacityWatts <= 0) {
            showError("Väljundit ei muudetud", "Võimsus peab olema positiivne.");
            return;
        }

        List<PowerConsumer> connectedConsumers = connectedConsumers(outlet.id());
        if (outlet.type() != selectedType
                && !connectedConsumers.isEmpty()
                && !confirmOutletTypeChange(outlet, selectedType, connectedConsumers)) {
            return;
        }

        outlet.rename(outletNameField.getText());
        outlet.setType(selectedType);
        outlet.setCapacityWatts(capacityWatts);
        plan.updateConnectorTypeForOutlet(outlet.id(), selectedType);
        refreshAfterOutletChange(outlet.id());
        markDirty();
    }

    private void removeSelectedOutlet() {
        if (!(selectedObject instanceof PowerSource source)) {
            return;
        }

        int selectedIndex = outletList.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= source.outlets().size()) {
            return;
        }

        PowerOutlet outlet = source.outlets().get(selectedIndex);
        List<PowerConsumer> connectedConsumers = connectedConsumers(outlet.id());
        if (!connectedConsumers.isEmpty() && !confirmRemoveConnectedOutlet(outlet, connectedConsumers)) {
            return;
        }

        plan.disconnectPowerFromOutlet(outlet.id());
        source.removeOutlet(selectedIndex);
        outletNameField.clear();
        refreshAfterOutletChange("");
        markDirty();
    }

    private void refreshAfterOutletChange(String preferredOutletId) {
        refreshOutletList();
        selectOutletInList(preferredOutletId);
        refreshPowerSourceChoices();
        refreshConnectionOutletChoices(preferredOutletId);
        redrawMap();
        refreshSummary();
        refreshOutletActionButtons();
    }

    private void selectOutletInList(String outletId) {
        if (outletId == null || outletId.isBlank() || !(selectedObject instanceof PowerSource source)) {
            return;
        }
        for (int index = 0; index < source.outlets().size(); index++) {
            if (source.outlets().get(index).id().equals(outletId)) {
                outletList.getSelectionModel().select(index);
                return;
            }
        }
    }

    private List<PowerConsumer> connectedConsumers(String outletId) {
        return plan.powerConnections().stream()
                .filter(connection -> connection.outletId().equals(outletId))
                .map(connection -> plan.findObject(connection.consumerId()))
                .flatMap(optional -> optional.stream())
                .filter(PowerConsumer.class::isInstance)
                .map(PowerConsumer.class::cast)
                .toList();
    }

    private boolean confirmRemoveConnectedOutlet(PowerOutlet outlet, List<PowerConsumer> connectedConsumers) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(stage);
        alert.setTitle("Eemalda väljund");
        alert.setHeaderText("See väljund on kasutusel");
        String consumerRows = connectedConsumers.stream()
                .map(consumer -> "- " + consumer.name())
                .reduce("", (rows, row) -> rows + row + System.lineSeparator());
        alert.setContentText("%s kustutamisel eemaldatakse nende objektide vooluühendused:%n%n%s".formatted(
                outlet.name().isBlank() ? outlet.type().displayName() : outlet.name(),
                consumerRows
        ));
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private boolean confirmOutletTypeChange(PowerOutlet outlet, ConnectorType selectedType, List<PowerConsumer> connectedConsumers) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(stage);
        alert.setTitle("Muuda väljundi tüüpi");
        alert.setHeaderText("See väljund on kasutusel");
        String consumerRows = connectedConsumers.stream()
                .map(consumer -> "- " + consumer.name())
                .reduce("", (rows, row) -> rows + row + System.lineSeparator());
        alert.setContentText("%s tüüp muutub: %s -> %s.%n%nNende objektide ühenduse tüüp muutub samuti:%n%n%s".formatted(
                outlet.name().isBlank() ? outlet.type().displayName() : outlet.name(),
                outlet.type().displayName(),
                selectedType.displayName(),
                consumerRows
        ));
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void loadSelectedOutletDetails() {
        PowerOutlet outlet = selectedOutlet();
        if (outlet == null) {
            return;
        }
        outletNameField.setText(outlet.name());
        outletTypeComboBox.getSelectionModel().select(outlet.type());
        outletCapacityWattsField.setText(Integer.toString(outlet.capacityWatts()));
        refreshOutletActionButtons();
    }

    private PowerOutlet selectedOutlet() {
        if (!(selectedObject instanceof PowerSource source)) {
            return null;
        }
        int selectedIndex = outletList.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= source.outlets().size()) {
            return null;
        }
        return source.outlets().get(selectedIndex);
    }

    private void refreshOutletActionButtons() {
        boolean outletSelected = selectedOutlet() != null;
        updateOutletButton.setDisable(!outletSelected);
        removeOutletButton.setDisable(!outletSelected);
    }

    private void updateDefaultOutletCapacity() {
        if (outletCapacityWattsField == null || outletTypeComboBox == null) {
            return;
        }
        ConnectorType selectedType = outletTypeComboBox.getSelectionModel().getSelectedItem();
        if (selectedType != null) {
            outletCapacityWattsField.setText(Integer.toString(selectedType.defaultCapacityWatts()));
        }
    }

    private StringConverter<ConnectorType> connectorTypeConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(ConnectorType type) {
                return type == null ? "" : type.displayName();
            }

            @Override
            public ConnectorType fromString(String text) {
                for (ConnectorType type : ConnectorType.values()) {
                    if (type.displayName().equals(text) || type.name().equals(text)) {
                        return type;
                    }
                }
                return ConnectorType.SCHUKO_230V;
            }
        };
    }

    private StringConverter<CustomObjectShape> customObjectShapeConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(CustomObjectShape shape) {
                return shape == null ? "" : shape.displayName();
            }

            @Override
            public CustomObjectShape fromString(String text) {
                for (CustomObjectShape shape : CustomObjectShape.values()) {
                    if (shape.displayName().equals(text) || shape.name().equals(text)) {
                        return shape;
                    }
                }
                return CustomObjectShape.SQUARE;
            }
        };
    }

    private StringConverter<MarkerType> markerTypeConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(MarkerType markerType) {
                return markerType == null ? "" : markerType.displayName();
            }

            @Override
            public MarkerType fromString(String text) {
                for (MarkerType markerType : MarkerType.values()) {
                    if (markerType.displayName().equals(text)) {
                        return markerType;
                    }
                }
                return MarkerType.WC;
            }
        };
    }

    private void refreshEquipmentList() {
        int selectedIndex = equipmentList.getSelectionModel().getSelectedIndex();
        equipmentList.getItems().clear();
        EquipmentContainer container = selectedEquipmentContainer();
        if (container == null) {
            return;
        }

        for (Equipment item : container.equipment()) {
            equipmentList.getItems().add("%s - %d W · %s".formatted(
                    item.name(),
                    item.requiredWatts(),
                    equipmentPowerDisplayName(item)
            ));
        }
        if (!container.equipment().isEmpty() && selectedIndex >= 0) {
            equipmentList.getSelectionModel().select(Math.min(selectedIndex, container.equipment().size() - 1));
        }
    }

    private List<EquipmentPowerChoice> equipmentPowerChoices() {
        List<EquipmentPowerChoice> choices = new ArrayList<>();
        if (selectedObject == null) {
            return choices;
        }
        PowerConnection defaultConnection = plan.findPowerConnectionForConsumer(selectedObject.id()).orElse(null);
        EquipmentPowerChoice defaultChoice = defaultConnection == null
                ? EquipmentPowerChoice.defaultPower("Objekti vaiketoide (määramata)")
                : equipmentPowerChoice(defaultConnection, "Objekti vaiketoide", true);
        choices.add(defaultChoice);
        for (PowerConnection connection : plan.findPowerConnectionsForConsumer(selectedObject.id())) {
            if (!connection.defaultForConsumer()) {
                choices.add(equipmentPowerChoice(connection, "Erand", false));
            }
        }
        return choices;
    }

    private EquipmentPowerChoice equipmentPowerChoice(
            PowerConnection connection,
            String role,
            boolean defaultChoice
    ) {
        PowerSource source = plan.findObject(connection.sourceId())
                .filter(PowerSource.class::isInstance)
                .map(PowerSource.class::cast)
                .orElse(null);
        PowerOutlet outlet = source == null ? null : source.outlets().stream()
                .filter(item -> item.id().equals(connection.outletId()))
                .findFirst()
                .orElse(null);
        int capacityWatts = outlet == null ? 0 : outlet.capacityWatts();
        return new EquipmentPowerChoice(
                defaultChoice ? "" : connection.id(),
                role + " — " + powerConnectionDisplayName(connection),
                usedWatts(connection.outletId()),
                capacityWatts,
                outlet != null
        );
    }

    private ListCell<EquipmentPowerChoice> createEquipmentPowerChoiceCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(EquipmentPowerChoice choice, boolean empty) {
                super.updateItem(choice, empty);
                setText(null);
                setGraphic(null);
                if (empty || choice == null) {
                    return;
                }
                if (!choice.hasLoad()) {
                    setText(choice.name());
                    return;
                }
                PowerLoadLevel loadLevel = PowerLoadLevel.from(choice.usedWatts(), choice.capacityWatts());
                Label label = new Label(choice.displayText());
                label.setStyle(loadLevel == PowerLoadLevel.OVERLOADED
                        ? "-fx-text-fill: #b91c1c; -fx-font-weight: bold;"
                        : "-fx-text-fill: #1f2937;");
                ProgressBar bar = new ProgressBar(choice.progress());
                bar.setMaxWidth(Double.MAX_VALUE);
                bar.setStyle("-fx-accent: %s;".formatted(loadLevel.colorHex()));
                setGraphic(new VBox(3, label, bar));
            }
        };
    }

    private String powerConnectionDisplayName(PowerConnection connection) {
        PowerSource source = plan.findObject(connection.sourceId())
                .filter(PowerSource.class::isInstance)
                .map(PowerSource.class::cast)
                .orElse(null);
        String sourceName = source == null ? "Puuduv vooluallikas" : source.name();
        String outletName = source == null
                ? connection.connectorType().displayName()
                : source.outlets().stream()
                .filter(outlet -> outlet.id().equals(connection.outletId()))
                .findFirst()
                .map(outlet -> outlet.name().isBlank() ? outlet.type().displayName() : outlet.name())
                .orElse(connection.connectorType().displayName());
        return "%s / %s".formatted(sourceName, outletName);
    }

    private String equipmentPowerDisplayName(Equipment equipment) {
        if (equipment.usesDefaultPower()) {
            return equipmentPowerConnection(equipment)
                    .map(connection -> "vaiketoide — " + powerConnectionDisplayName(connection))
                    .orElse("vaiketoide (määramata)");
        }
        return plan.powerConnections().stream()
                .filter(connection -> connection.id().equals(equipment.powerConnectionId()))
                .findFirst()
                .map(this::powerConnectionDisplayName)
                .orElse("puuduv toide");
    }

    private EquipmentContainer selectedEquipmentContainer() {
        return selectedObject instanceof EquipmentContainer container ? container : null;
    }

    private void refreshOutletList() {
        outletList.getItems().clear();
        if (!(selectedObject instanceof PowerSource source)) {
            outletNameField.clear();
            refreshOutletActionButtons();
            return;
        }

        outletList.getItems().addAll(outletChoices(source));
        refreshOutletActionButtons();
    }

    private String toHex(Color color) {
        int red = (int) Math.round(color.getRed() * 255);
        int green = (int) Math.round(color.getGreen() * 255);
        int blue = (int) Math.round(color.getBlue() * 255);
        return "#%02x%02x%02x".formatted(red, green, blue);
    }

    private void refreshSummary() {
        summaryList.getItems().clear();
        if (showPowerSummary()) {
            for (PowerSummary summary : powerSummaryService.summaries(plan)) {
                String hierarchyKey = powerSourceSummaryKey(summary.sourceId());
                boolean expanded = isPowerSummaryItemExpanded(hierarchyKey);
                summaryList.getItems().add(SummaryListItem.expandableLoad("%s: %d W kasutusel, %s".formatted(
                        summary.sourceName(),
                        summary.usedWatts(),
                        remainingWattsText(summary.remainingWatts())
                ), summary.usedWatts(), summary.capacityWatts(), summary.sourceId(), hierarchyKey, expanded, 0));
                if (expanded) {
                    addConnectedConsumers(summary.sourceId());
                }
            }
        }
        if (showCableSummary()) {
            addCableSummary();
        }
        if (showGroupSummary()) {
            addGroupSummary();
        }
    }

    private boolean showPowerSummary() {
        return showPowerSummaryCheckBox == null || showPowerSummaryCheckBox.isSelected();
    }

    private boolean showCableSummary() {
        return showCableSummaryCheckBox == null || showCableSummaryCheckBox.isSelected();
    }

    private boolean showGroupSummary() {
        return showGroupSummaryCheckBox == null || showGroupSummaryCheckBox.isSelected();
    }

    private void addConnectedConsumers(String sourceId) {
        PowerSource source = plan.findObject(sourceId)
                .filter(PowerSource.class::isInstance)
                .map(PowerSource.class::cast)
                .orElse(null);
        if (source == null) {
            return;
        }

        for (int index = 0; index < source.outlets().size(); index++) {
            PowerOutlet outlet = source.outlets().get(index);
            int usedWatts = usedWatts(outlet.id());
            String hierarchyKey = powerOutletSummaryKey(outlet.id());
            boolean expanded = isPowerSummaryItemExpanded(hierarchyKey);
            summaryList.getItems().add(SummaryListItem.expandableLoad("  %s: %d W kasutusel, %s".formatted(
                    outletDisplayName(outlet, outletTypeIndex(source, outlet, index)),
                    usedWatts,
                    remainingWattsText(outlet.capacityWatts() - usedWatts)
            ), usedWatts, outlet.capacityWatts(), source.id(), hierarchyKey, expanded, 1));
            if (expanded) {
                addConnectedConsumers(sourceId, outlet.id(), "    ");
            }
        }
        addConnectedConsumers(sourceId, "", "  ");
    }

    private String powerSourceSummaryKey(String sourceId) {
        return "source:" + sourceId;
    }

    private String powerOutletSummaryKey(String outletId) {
        return "outlet:" + outletId;
    }

    private boolean isPowerSummaryItemExpanded(String hierarchyKey) {
        return !collapsedPowerSummaryKeys.contains(hierarchyKey);
    }

    private void togglePowerSummaryItem(String hierarchyKey) {
        if (!collapsedPowerSummaryKeys.add(hierarchyKey)) {
            collapsedPowerSummaryKeys.remove(hierarchyKey);
        }
        refreshSummary();
    }

    private void addConnectedConsumers(String sourceId, String outletId, String rowPrefix) {
        for (PowerConnection connection : plan.powerConnections()) {
            if (!connection.sourceId().equals(sourceId)) {
                continue;
            }
            if (!connection.outletId().equals(outletId)) {
                continue;
            }
            plan.findObject(connection.consumerId())
                    .filter(PowerConsumer.class::isInstance)
                    .map(PowerConsumer.class::cast)
                    .ifPresent(consumer -> summaryList.getItems().add(SummaryListItem.target("%s- %s: %d W (%s)".formatted(
                            rowPrefix,
                            consumer.name(),
                            plan.powerDemandWatts(connection),
                            connection.connectorType().displayName()
                    ), consumer.id())));
        }
    }

    private int usedWatts(String outletId) {
        return plan.outletDemandWatts(outletId);
    }

    private void addCableSummary() {
        if (plan.powerConnections().isEmpty()) {
            return;
        }

        List<CableSummaryRow> cableRows = new ArrayList<>();
        double totalLengthMeters = 0.0;
        double totalNotedLengthMeters = 0.0;
        boolean hasNotedLength = false;
        Map<ConnectorType, CableTypeSummary> summariesByType = new EnumMap<>(ConnectorType.class);

        for (PowerConnection connection : plan.powerConnections()) {
            PlannerObject consumer = plan.findObject(connection.consumerId()).orElse(null);
            if (!(consumer instanceof PowerConsumer)) {
                continue;
            }

            PowerSource source = plan.findObject(connection.sourceId())
                    .filter(PowerSource.class::isInstance)
                    .map(PowerSource.class::cast)
                    .orElse(null);
            if (source == null) {
                continue;
            }

            double lengthMeters = CableDisplayHelper.lengthMeters(cablePath(consumer, source, connection), pixelsPerMeter());
            totalLengthMeters += lengthMeters;
            OptionalDouble notedLengthMeters = notedCableLengthMeters(connection);
            CableTypeSummary typeSummary = summariesByType.computeIfAbsent(
                    connection.connectorType(),
                    ignored -> new CableTypeSummary()
            );
            typeSummary.addMapLength(lengthMeters);
            if (notedLengthMeters.isPresent()) {
                totalNotedLengthMeters += notedLengthMeters.getAsDouble();
                typeSummary.addNotedLength(notedLengthMeters.getAsDouble());
                typeSummary.addPieces(cableLengthPieces(connection));
                hasNotedLength = true;
            }
            cableRows.add(new CableSummaryRow(consumer, source, connection, lengthMeters, notedLengthMeters));
        }

        if (cableRows.isEmpty()) {
            return;
        }

        addSummarySpacerIfNeeded();
        summaryList.getItems().add(SummaryListItem.text("Kaablid"));
        summaryList.getItems().addAll(cableRows.stream()
                .sorted(CABLE_SUMMARY_ROW_COMPARATOR)
                .map(this::cableSummaryRow)
                .map(SummaryListItem::text)
                .toList());
        if (hasNotedLength) {
            summaryList.getItems().add(SummaryListItem.text("Kokku: %.1f m märgitud, %.1f m kaardil".formatted(totalNotedLengthMeters, totalLengthMeters)));
        } else {
            summaryList.getItems().add(SummaryListItem.text("Kokku: %.1f m".formatted(totalLengthMeters)));
        }
        for (String row : cableTypeSummaryRows(summariesByType)) {
            summaryList.getItems().add(SummaryListItem.text(row));
        }
    }

    private void addGroupSummary() {
        if (plan.objects().isEmpty()) {
            return;
        }

        addSummarySpacerIfNeeded();
        summaryList.getItems().add(SummaryListItem.text("Grupid"));
        for (Map.Entry<String, List<PlannerObject>> entry : objectsByGroup().entrySet()) {
            summaryList.getItems().add(SummaryListItem.text(entry.getKey()));
            for (PlannerObject object : entry.getValue()) {
                summaryList.getItems().add(SummaryListItem.text("  - %s (%s)".formatted(object.name(), objectTypeName(object))));
            }
        }
    }

    private void addSummarySpacerIfNeeded() {
        if (!summaryList.getItems().isEmpty()) {
            summaryList.getItems().add(SummaryListItem.text(""));
        }
    }

    private Map<String, List<PlannerObject>> objectsByGroup() {
        Map<String, List<PlannerObject>> objectsByGroup = new TreeMap<>();
        for (PlannerObject object : plan.objects()) {
            String groupName = object.groupName().isBlank() ? "Määramata" : object.groupName();
            objectsByGroup.computeIfAbsent(groupName, ignored -> new ArrayList<>()).add(object);
        }
        return objectsByGroup;
    }

    private boolean savePlan() {
        File currentPlanFile = planFileSession.currentFile();
        if (currentPlanFile == null) {
            return savePlanAs();
        }

        return savePlanToFile(currentPlanFile);
    }

    private boolean savePlanAs() {
        Optional<File> selectedFile = PlanFileDialogs.choosePlanToSave(
                stage,
                planFileSession.initialDirectory(),
                planFileSession.currentFile()
        );
        if (selectedFile.isEmpty()) {
            return false;
        }

        return savePlanToFile(selectedFile.get());
    }

    private boolean savePlanToFile(File file) {
        try {
            planFileSession.save(plan, file);
            recentPlanFiles.remember(file);
            markClean();
            return true;
        } catch (IOException exception) {
            showError("Salvestamine ebaõnnestus", exception.getMessage());
            return false;
        }
    }

    private void exportSummary() {
        refreshSummary();

        Optional<ReportExportScope> selectedReportScope = ExportOptionsDialog.chooseReportExportScope(stage);
        if (selectedReportScope.isEmpty()) {
            return;
        }

        Optional<File> selectedFile = ExportFileChooser.chooseSummaryFile(
                stage,
                planFileSession.initialDirectory(),
                plan.name(),
                planFileSession.currentFile()
        );
        if (selectedFile.isEmpty()) {
            return;
        }

        File file = selectedFile.get();
        try {
            Files.writeString(file.toPath(), summaryText(selectedReportScope.get()), StandardCharsets.UTF_8);
            planFileSession.rememberDirectory(file);
        } catch (IOException exception) {
            showError("Eksportimine ebaõnnestus", exception.getMessage());
        }
    }

    private void exportMapImage() {
        redrawMap();

        Optional<MapImageExportScope> selectedScope = ExportOptionsDialog.chooseMapImageExportScope(stage);
        if (selectedScope.isEmpty()) {
            return;
        }

        Optional<File> selectedFile = ExportFileChooser.chooseMapImageFile(
                stage,
                planFileSession.initialDirectory(),
                plan.name(),
                planFileSession.currentFile()
        );
        if (selectedFile.isEmpty()) {
            return;
        }

        File file = selectedFile.get();
        try {
            WritableImage image = snapshotMapImage(selectedScope.get());
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            planFileSession.rememberDirectory(file);
            saveStatusLabel.setText("Pilt eksporditud");
            saveStatusLabel.setStyle("-fx-text-fill: #166534; -fx-font-weight: bold;");
        } catch (IOException exception) {
            showError("Pildi eksportimine ebaõnnestus", exception.getMessage());
        }
    }

    private void exportPdf() {
        redrawMap();

        Optional<PdfExportOptions> selectedOptions = ExportOptionsDialog.choosePdfExportOptions(stage);
        if (selectedOptions.isEmpty()) {
            return;
        }
        PdfExportOptions options = selectedOptions.get();

        Optional<File> selectedFile = ExportFileChooser.choosePdfFile(
                stage,
                planFileSession.initialDirectory(),
                plan.name(),
                planFileSession.currentFile()
        );
        if (selectedFile.isEmpty()) {
            return;
        }

        File file = selectedFile.get();
        try {
            WritableImage image = snapshotMapImage(options.mapScope());
            PdfReportExporter.export(
                    file,
                    plan.name(),
                    SwingFXUtils.fromFXImage(image, null),
                    summaryText(options.reportScope())
            );

            planFileSession.rememberDirectory(file);
            saveStatusLabel.setText("PDF eksporditud");
            saveStatusLabel.setStyle("-fx-text-fill: #166534; -fx-font-weight: bold;");
        } catch (IOException exception) {
            showError("PDF eksportimine ebaõnnestus", exception.getMessage());
        }
    }

    private WritableImage snapshotMapImage(MapImageExportScope scope) {
        return MapImageSnapshotter.snapshot(
                mapPane,
                mapScrollPane,
                mapScale,
                scope,
                mapWidth,
                mapHeight,
                zoomLevel,
                this::updateZoomContentSize
        );
    }

    private String summaryText(ReportExportScope reportScope) {
        return reportTextExporter.export(plan, reportScope, showPowerSummary(), showCableSummary(), showGroupSummary());
    }

    private String cableNotesText(PowerConnection connection) {
        return connection.cableNotes().isBlank() ? "" : " [%s]".formatted(connection.cableNotes());
    }

    private List<String> cableTypeSummaryRows(Map<ConnectorType, CableTypeSummary> summariesByType) {
        List<String> rows = new ArrayList<>();
        if (!summariesByType.isEmpty()) {
            rows.add("Tüübi kaupa:");
        }
        for (ConnectorType connectorType : ConnectorType.values()) {
            CableTypeSummary summary = summariesByType.get(connectorType);
            if (summary == null) {
                continue;
            }
            rows.add(cableTypeSummaryRow(connectorType, summary));
            if (summary.hasPieces()) {
                rows.add("    tükid: %s".formatted(cablePieceCountText(summary.pieceCounts())));
            }
        }
        return rows;
    }

    private String cableTypeSummaryRow(ConnectorType connectorType, CableTypeSummary summary) {
        if (summary.hasNotedLength()) {
            return "  %s: %.1f m märgitud, %.1f m kaardil".formatted(
                    CableDisplayHelper.shortTypeName(connectorType),
                    summary.notedLengthMeters(),
                    summary.mapLengthMeters()
            );
        }
        return "  %s: %.1f m kaardil".formatted(CableDisplayHelper.shortTypeName(connectorType), summary.mapLengthMeters());
    }

    private String cableSummaryRow(CableSummaryRow row) {
        String lengthText = row.notedLengthMeters().isPresent()
                ? "%.1f m kaardil, %.1f m märgitud".formatted(row.mapLengthMeters(), row.notedLengthMeters().getAsDouble())
                : "%.1f m".formatted(row.mapLengthMeters());
        String connectionRole = row.connection().defaultForConsumer() ? "" : ", seadme erand";
        return "  - %s -> %s (%s%s): %s%s".formatted(
                row.consumer().name(),
                row.source().name(),
                row.connection().connectorType().displayName(),
                connectionRole,
                lengthText,
                cableNotesText(row.connection()) + cableNoteWarningText(row.connection())
        );
    }

    private record CableSummaryRow(
            PlannerObject consumer,
            PowerSource source,
            PowerConnection connection,
            double mapLengthMeters,
            OptionalDouble notedLengthMeters
    ) {
    }

    private record SummaryListItem(
            String text,
            Integer usedWatts,
            Integer capacityWatts,
            String targetObjectId,
            String hierarchyKey,
            boolean expanded,
            int depth
    ) {
        private static SummaryListItem text(String text) {
            return new SummaryListItem(text, null, null, "", "", false, 0);
        }

        private static SummaryListItem target(String text, String targetObjectId) {
            return new SummaryListItem(text, null, null, targetObjectId, "", false, 0);
        }

        private static SummaryListItem expandableLoad(
                String text,
                int usedWatts,
                int capacityWatts,
                String targetObjectId,
                String hierarchyKey,
                boolean expanded,
                int depth
        ) {
            return new SummaryListItem(
                    text, usedWatts, capacityWatts, targetObjectId, hierarchyKey, expanded, depth
            );
        }

        private boolean hasLoad() {
            return usedWatts != null && capacityWatts != null;
        }

        private boolean hasTarget() {
            return targetObjectId != null && !targetObjectId.isBlank();
        }

        private boolean isExpandable() {
            return hierarchyKey != null && !hierarchyKey.isBlank();
        }

        private double progress() {
            if (capacityWatts <= 0) {
                return usedWatts > 0 ? 1.0 : 0.0;
            }
            return Math.clamp((double) usedWatts / capacityWatts, 0.0, 1.0);
        }

        private String displayText() {
            if (capacityWatts <= 0) {
                return text + " · —";
            }
            return "%s · %.0f%%".formatted(text, (double) usedWatts * 100 / capacityWatts);
        }
    }

    private record MeasurementView(Position start, Position end, Label distanceLabel) {
    }

    private String cableNoteWarningText(PowerConnection connection) {
        return cableNoteNeedsReview(connection) ? " (tükid kontrollida)" : "";
    }

    private boolean cableNoteNeedsReview(PowerConnection connection) {
        String notes = connection.cableLengthNotes();
        if (notes.isBlank() || !notes.contains("+")) {
            return false;
        }

        for (String part : notes.split("\\+")) {
            if (!part.isBlank() && !CABLE_LENGTH_PATTERN.matcher(part).find()) {
                return true;
            }
        }
        return false;
    }

    private OptionalDouble notedCableLengthMeters(PowerConnection connection) {
        List<Double> pieces = cableLengthPieces(connection);
        if (pieces.isEmpty()) {
            return OptionalDouble.empty();
        }

        double totalLengthMeters = 0.0;
        for (double piece : pieces) {
            totalLengthMeters += piece;
        }
        return OptionalDouble.of(totalLengthMeters);
    }

    private List<Double> cableLengthPieces(PowerConnection connection) {
        List<Double> pieces = new ArrayList<>();
        Matcher matcher = CABLE_LENGTH_PATTERN.matcher(connection.cableLengthNotes());
        while (matcher.find()) {
            pieces.add(Double.parseDouble(matcher.group().replace(',', '.')));
        }
        return pieces;
    }

    private String cablePieceCountText(Map<Double, Integer> pieceCounts) {
        List<String> rows = new ArrayList<>();
        for (Map.Entry<Double, Integer> entry : pieceCounts.entrySet()) {
            rows.add("%s m x %d".formatted(formatCablePieceLength(entry.getKey()), entry.getValue()));
        }
        return String.join(", ", rows);
    }

    private String formatCablePieceLength(double lengthMeters) {
        if (Math.abs(lengthMeters - Math.rint(lengthMeters)) < 0.0001) {
            return Integer.toString((int) Math.rint(lengthMeters));
        }
        return "%.1f".formatted(lengthMeters);
    }

    private static class CableTypeSummary {
        private double mapLengthMeters;
        private double notedLengthMeters;
        private boolean hasNotedLength;
        private final Map<Double, Integer> pieceCounts = new TreeMap<>();

        void addMapLength(double lengthMeters) {
            mapLengthMeters += lengthMeters;
        }

        void addNotedLength(double lengthMeters) {
            notedLengthMeters += lengthMeters;
            hasNotedLength = true;
        }

        void addPieces(List<Double> pieces) {
            for (double piece : pieces) {
                pieceCounts.merge(piece, 1, Integer::sum);
            }
        }

        double mapLengthMeters() {
            return mapLengthMeters;
        }

        double notedLengthMeters() {
            return notedLengthMeters;
        }

        boolean hasNotedLength() {
            return hasNotedLength;
        }

        boolean hasPieces() {
            return !pieceCounts.isEmpty();
        }

        Map<Double, Integer> pieceCounts() {
            return pieceCounts;
        }
    }

    private void loadMapImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Vali kaardipilt");
        applyInitialDirectory(fileChooser);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pildifail", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            return;
        }

        planFileSession.rememberDirectory(file);
        setMapImage(file.getAbsolutePath());
    }

    private void setMapImage(String imagePath) {
        plan.setMapImagePath(imagePath);
        redrawMap();
        markDirty();
    }

    private void openPlan() {
        if (!confirmDiscardUnsavedChanges()) {
            return;
        }

        Optional<File> selectedFile = PlanFileDialogs.choosePlanToOpen(stage, planFileSession.initialDirectory());
        if (selectedFile.isEmpty()) {
            return;
        }

        openPlanFile(selectedFile.get());
    }

    private void showStartupPlanDialog() {
        StartupPlanDialog.show(stage, recentPlanFiles.load()).ifPresent(choice -> {
            switch (choice.action()) {
                case NEW_PLAN -> newPlan();
                case OPEN_RECENT -> openPlanFile(choice.path().toFile());
                case OPEN_OTHER -> openPlan();
            }
        });
    }

    private boolean openPlanFile(File file) {
        try {
            plan = planFileSession.load(file);
            recentPlanFiles.remember(file);
            resetPlanViewState();
            return true;
        } catch (IOException | RuntimeException exception) {
            showError("Faili avamine ebaõnnestus", exception.getMessage());
            return false;
        }
    }

    private void applyInitialDirectory(FileChooser fileChooser) {
        File directory = planFileSession.initialDirectory();
        if (directory != null && directory.isDirectory()) {
            fileChooser.setInitialDirectory(directory);
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(stage);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message == null || message.isBlank() ? "Tundmatu viga." : message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private record ObjectListItem(
            PlannerObject object,
            String type,
            String groupName,
            String measurementText,
            boolean visible
    ) {
        private String detailText() {
            String measurement = measurementText.isBlank() ? "" : " · " + measurementText;
            String visibilityText = visible ? "" : " · peidetud";
            return "%s%s%s".formatted(type, measurement, visibilityText);
        }

        @Override
        public String toString() {
            String measurement = measurementText.isBlank() ? "" : ", " + measurementText;
            String visibilityText = visible ? "" : ", peidetud";
            return "%s (%s, %s%s%s)".formatted(object.name(), type, groupName, measurement, visibilityText);
        }
    }

    private record ObjectListEntry(
            String groupName,
            ObjectListItem objectItem,
            int objectCount,
            boolean expanded
    ) {
        private static ObjectListEntry group(String groupName, int objectCount, boolean expanded) {
            return new ObjectListEntry(groupName, null, objectCount, expanded);
        }

        private static ObjectListEntry object(ObjectListItem objectItem) {
            return new ObjectListEntry(objectItem.groupName(), objectItem, 0, false);
        }

        private boolean isGroup() {
            return objectItem == null;
        }
    }

    private record PowerSourceChoice(String sourceId, String name, int usedWatts, int capacityWatts) {
        private static PowerSourceChoice none() {
            return new PowerSourceChoice("", "Määramata", 0, 0);
        }

        private boolean isNone() {
            return sourceId.isBlank();
        }

        private double progress() {
            if (capacityWatts <= 0) {
                return usedWatts > 0 ? 1.0 : 0.0;
            }
            return Math.clamp((double) usedWatts / capacityWatts, 0.0, 1.0);
        }

        private String displayText() {
            int remainingWatts = capacityWatts - usedWatts;
            String remainingText = capacityWatts <= 0
                    ? "võimsus määramata"
                    : remainingWatts < 0
                    ? "ÜLEKOORMUS %d W".formatted(Math.abs(remainingWatts))
                    : "%d W alles".formatted(remainingWatts);
            return "%s — %d W kasutusel, %s · %.0f%%".formatted(
                    name,
                    usedWatts,
                    remainingText,
                    capacityWatts <= 0 ? 0.0 : (double) usedWatts * 100 / capacityWatts
            );
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private record PowerConnectionChoice(String connectionId, String name, boolean defaultConnection) {
        @Override
        public String toString() {
            return name;
        }
    }

    private record OutletChoice(
            String outletId,
            ConnectorType connectorType,
            String name,
            int usedWatts,
            int capacityWatts
    ) {
        private double progress() {
            if (capacityWatts <= 0) {
                return usedWatts > 0 ? 1.0 : 0.0;
            }
            return Math.clamp((double) usedWatts / capacityWatts, 0.0, 1.0);
        }

        private String displayText() {
            if (capacityWatts <= 0) {
                return name + " · —";
            }
            return "%s · %.0f%%".formatted(name, (double) usedWatts * 100 / capacityWatts);
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private record EquipmentPowerChoice(
            String connectionId,
            String name,
            int usedWatts,
            int capacityWatts,
            boolean hasLoad
    ) {
        private static EquipmentPowerChoice defaultPower(String name) {
            return new EquipmentPowerChoice("", name, 0, 0, false);
        }

        private boolean isDefault() {
            return connectionId.isBlank();
        }

        private double progress() {
            return capacityWatts <= 0
                    ? usedWatts > 0 ? 1.0 : 0.0
                    : Math.clamp((double) usedWatts / capacityWatts, 0.0, 1.0);
        }

        private String displayText() {
            int remainingWatts = capacityWatts - usedWatts;
            String remainingText = remainingWatts < 0
                    ? "ÜLEKOORMUS %d W".formatted(Math.abs(remainingWatts))
                    : "%d W alles".formatted(remainingWatts);
            return "%s · %d W kasutusel, %s · %.0f%%".formatted(
                    name, usedWatts, remainingText,
                    capacityWatts <= 0 ? 0.0 : (double) usedWatts * 100 / capacityWatts
            );
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private record EquipmentSupplyView(String name, int usedWatts, int capacityWatts) {
        private double progress() {
            if (capacityWatts <= 0) {
                return usedWatts > 0 ? 1.0 : 0.0;
            }
            return Math.clamp((double) usedWatts / capacityWatts, 0.0, 1.0);
        }

        private String displayText() {
            int remainingWatts = capacityWatts - usedWatts;
            String remainingText = capacityWatts <= 0
                    ? "võimsus määramata"
                    : remainingWatts < 0
                    ? "ÜLEKOORMUS %d W".formatted(Math.abs(remainingWatts))
                    : "%d W alles".formatted(remainingWatts);
            return "%s — %s · %.0f%%".formatted(
                    name,
                    remainingText,
                    capacityWatts <= 0 ? 0.0 : (double) usedWatts * 100 / capacityWatts
            );
        }
    }

    private static class Delta {
        private double x;
        private double y;
    }
}
