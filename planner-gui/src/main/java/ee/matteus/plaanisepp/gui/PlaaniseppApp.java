package ee.matteus.plaanisepp.gui;

import ee.matteus.plaanisepp.core.map.BaseMapBounds;
import ee.matteus.plaanisepp.core.map.BaseMapDownload;
import ee.matteus.plaanisepp.core.map.MaaAmetWmsClient;
import ee.matteus.plaanisepp.core.map.RegularMapStyle;
import ee.matteus.plaanisepp.core.model.ConnectorType;
import ee.matteus.plaanisepp.core.model.ChecklistItem;
import ee.matteus.plaanisepp.core.model.ChecklistSuggestionStatus;
import ee.matteus.plaanisepp.core.model.AreaObject;
import ee.matteus.plaanisepp.core.model.CustomObject;
import ee.matteus.plaanisepp.core.model.CustomObjectShape;
import ee.matteus.plaanisepp.core.model.DistributionPanel;
import ee.matteus.plaanisepp.core.model.Equipment;
import ee.matteus.plaanisepp.core.model.EquipmentContainer;
import ee.matteus.plaanisepp.core.model.EquipmentPowerAssignmentResult;
import ee.matteus.plaanisepp.core.model.EventPlan;
import ee.matteus.plaanisepp.core.model.FenceRow;
import ee.matteus.plaanisepp.core.model.FenceJoint;
import ee.matteus.plaanisepp.core.model.InventoryContainer;
import ee.matteus.plaanisepp.core.model.InventoryItem;
import ee.matteus.plaanisepp.core.model.InventoryItemNames;
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
import ee.matteus.plaanisepp.core.model.TextObjectSourceType;
import ee.matteus.plaanisepp.core.model.Tent;
import ee.matteus.plaanisepp.core.model.TentPreset;
import ee.matteus.plaanisepp.core.service.PlanFactory;
import ee.matteus.plaanisepp.core.service.PlanSnapshot;
import ee.matteus.plaanisepp.core.service.PlanSnapshotService;
import ee.matteus.plaanisepp.core.service.FenceRingGenerator;
import ee.matteus.plaanisepp.core.service.GeometryCalculator;
import ee.matteus.plaanisepp.core.service.PowerSummary;
import ee.matteus.plaanisepp.core.service.PowerSummaryService;
import ee.matteus.plaanisepp.core.service.PowerHierarchyService;
import ee.matteus.plaanisepp.core.service.InventorySummaryService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
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
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
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
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
import java.util.LinkedHashSet;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.prefs.Preferences;

public class PlaaniseppApp extends Application {
    private static final String DEFAULT_MAP_PATH = "classpath:/maps/tavakaart.png";
    private static final String ORTHOPHOTO_MAP_PATH = "classpath:/maps/ortofoto.png";
    private static final int DEFAULT_API_MAP_WIDTH = 4_923;
    private static final int DEFAULT_API_MAP_HEIGHT = 2_648;
    private static final double DEFAULT_API_MAP_CENTER_X = 659_266.421;
    private static final double DEFAULT_API_MAP_CENTER_Y = 6_474_323.917;
    private static final double DEFAULT_API_MAP_PIXELS_PER_METRE = 24.5;
    private static final String APPLICATION_ICON_PATH = "/icons/plaanisepp.png";
    private static final String GITHUB_RELEASES_URL = "https://github.com/KaldaM/plaanisepp/releases";
    private static final String SELECTED_OBJECT_SECTION = "selectedObject";
    private static final String OBJECT_LIST_SECTION = "objectList";
    private static final String CHECKLIST_SECTION = "checklist";
    private static final String MAP_LAYERS_SECTION = "mapLayers";
    private static final String SUMMARY_SECTION = "summary";
    private static final String INVENTORY_SECTION = "inventory";
    private static final String EQUIPMENT_SECTION = "equipment";
    private static final String OBJECT_INVENTORY_SECTION = "objectInventory";
    private static final String OUTLET_SECTION = "outlet";
    private static final String SIDEBAR_SECTION_ORDER_PREFERENCE = "sidebarSectionOrder";
    private static final String PLACEMENT_SHOW_MAP_LABEL_PREFERENCE = "placementShowMapLabel";
    private static final String PLACEMENT_SHOW_FENCE_INVENTORY_LABEL_PREFERENCE =
            "placementShowFenceInventoryLabel";
    private static final String PLACEMENT_FENCE_SEGMENT_LENGTH_PREFERENCE =
            "placementFenceSegmentLengthMeters";
    private static final String MAP_LAYOUT_LOCKED_PREFERENCE = "mapLayoutLocked";
    private static final String ORGANIZER_VIEW_PREFERENCE = "organizerView";
    private static final List<String> DEFAULT_SIDEBAR_SECTION_ORDER = List.of(
            OBJECT_LIST_SECTION,
            CHECKLIST_SECTION,
            SELECTED_OBJECT_SECTION,
            MAP_LAYERS_SECTION,
            INVENTORY_SECTION,
            SUMMARY_SECTION
    );
    private static final double MIN_MAP_WIDTH = 760.0;
    private static final double MIN_MAP_HEIGHT = 560.0;
    private static final double MAP_CLICK_DRAG_TOLERANCE_PX = 6.0;
    private static final double MIN_OBJECT_LIST_HEIGHT = 90.0;
    private static final double MAX_OBJECT_LIST_HEIGHT = 800.0;
    private static final double DEFAULT_OBJECT_LIST_HEIGHT = 180.0;
    private static final double MIN_FONT_SIZE_PIXELS = 6.0;
    private static final double MAX_FONT_SIZE_PIXELS = 120.0;
    private static final double DJ_TRUCK_LABEL_HEIGHT_METERS = 0.8;
    private static final double MIN_DJ_TRUCK_LABEL_SIZE_PIXELS = 12.0;
    private static final String OBJECT_LIST_HEIGHT_PREFERENCE = "objectListHeight";
    private static final String FENCE_DRAG_NODE_KEY = "plaanisepp.fenceDragNode";
    private static final long DOUBLE_SHIFT_INTERVAL_NANOS = 500_000_000L;
    private static final int MAX_PLAN_HISTORY_STEPS = 50;
    private static final List<Double> DEFAULT_CABLE_PIECE_LENGTHS = List.of(2.0, 5.0, 10.0, 20.0);
    private static final String INVENTORY_SUMMARY_KEY_PROPERTY = "plaanisepp.inventorySummaryKey";
    private static final String INVENTORY_SUMMARY_SOURCE_PREFIX = "inventory-summary:";
    private static final List<ChecklistSuggestion> CHECKLIST_SUGGESTIONS = List.of(
            new ChecklistSuggestion("technical_tent", "Tehnikatelk"),
            new ChecklistSuggestion("info_tent", "Infotelk"),
            new ChecklistSuggestion("merch", "Merch"),
            new ChecklistSuggestion("emergency_exit", "Emergency exit"),
            new ChecklistSuggestion("pa", "PA"),
            new ChecklistSuggestion("redla_car", "Redla auto"),
            new ChecklistSuggestion("participant_tent", "Osalejate telk"),
            new ChecklistSuggestion("first_aid", "Esmaabi")
    );

    private final PlanFactory planFactory = new PlanFactory();
    private final PowerSummaryService powerSummaryService = new PowerSummaryService();
    private final PowerHierarchyService powerHierarchyService = new PowerHierarchyService();
    private final InventorySummaryService inventorySummaryService = new InventorySummaryService();
    private final CableInventorySummaryService cableInventorySummaryService = new CableInventorySummaryService();
    private final ReportTextExporter reportTextExporter = new ReportTextExporter();
    private final PlanFileSession planFileSession = new PlanFileSession();
    private final PlanDocumentState planDocumentState = new PlanDocumentState();
    private final PlanSnapshotService planSnapshotService = new PlanSnapshotService();
    private final PlanHistory<PlanSnapshot> planHistory = new PlanHistory<>(MAX_PLAN_HISTORY_STEPS);
    private final Preferences preferences = ApplicationPreferences.open();
    private final RecentPlanFiles recentPlanFiles = new RecentPlanFiles(preferences);
    private final GitHubReleaseService gitHubReleaseService = new GitHubReleaseService();
    private final ReleaseAssetDownloadService releaseAssetDownloadService = new ReleaseAssetDownloadService();
    private final TartuPowerCabinetImportService tartuPowerCabinetImportService =
            new TartuPowerCabinetImportService();

    private EventPlan plan;
    private PlanSnapshot savedPlanSnapshot;
    private Pane mapPane;
    private Pane mapContentPane;
    private ScrollPane mapScrollPane;
    private Scale mapScale;
    private ImageView mapImageView;
    private ToggleButton defaultMapButton;
    private ToggleButton orthophotoMapButton;
    private double zoomLevel = 1.0;
    private double zoomPercentage = 1.0;
    private Slider zoomSlider;
    private Button zoomPercentButton;
    private Button undoButton;
    private Button redoButton;
    private MenuItem undoMenuItem;
    private MenuItem redoMenuItem;
    private double mapWidth = MIN_MAP_WIDTH;
    private double mapHeight = MIN_MAP_HEIGHT;
    private double objectListHeight;
    private boolean measuringActive;
    private boolean addingCablePoint;
    private String editingCableConnectionId;
    private String rotatingObjectId;
    private RotationDragState rotationDragState;
    private boolean rotatingMultipleObjects;
    private MultiObjectRotationState multiObjectRotationState;
    private double multiObjectRotationDelta;
    private boolean mapDraggedSincePress;
    private boolean planDragInProgress;
    private boolean planDragRecorded;
    private boolean synchronizingSidebarSelection;
    private boolean preservingSidebarMultiSelection;
    private boolean multiObjectDragInProgress;
    private boolean multiObjectDragChanged;
    private MultiObjectDragState multiObjectDragState;
    private boolean suppressNextObjectClick;
    private Position selectionBoxStart;
    private Rectangle selectionBox;
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
    private Line measurementPreviewLine;
    private Circle measurementPreviewEndMarker;
    private Label measurementPreviewLabel;
    private MeasurementPathView activeMeasurementPath;
    private MeasurementPathView editingMeasurementPath;
    private final List<Node> measurementNodes = new ArrayList<>();
    private final List<Node> powerConnectionAnchorMarkers = new ArrayList<>();
    private final List<MeasurementView> measurements = new ArrayList<>();
    private final List<MeasurementPathView> measurementPaths = new ArrayList<>();
    private final List<MeasurementPathView> selectedMeasurementPaths = new ArrayList<>();
    private final List<Position> pendingShapePoints = new ArrayList<>();
    private final Set<String> visibleGroups = new HashSet<>();
    private final Set<String> collapsedSummaryKeys = new HashSet<>();
    private final Set<String> collapsedObjectGroups = new HashSet<>();
    private final Set<String> expandedObjectInventoryKeys = new HashSet<>();
    private final Set<String> selectedObjectIds = new LinkedHashSet<>();
    private String selectionRangeAnchorObjectId;
    private final Map<String, Boolean> sidebarSectionStates = new HashMap<>();
    private final Map<String, TitledPane> sidebarSections = new HashMap<>();
    private final Map<String, Node> mapObjectNodes = new HashMap<>();
    private final Map<String, List<Node>> mapObjectVisualNodes = new HashMap<>();
    private final Map<String, List<Node>> powerConnectionVisualNodes = new HashMap<>();
    private final Map<String, PowerConnectionVisual> powerConnectionVisuals = new HashMap<>();
    private final Map<String, TextReferenceVisual> textReferenceVisuals = new HashMap<>();
    private final Map<String, FenceRowVisual> fenceRowVisuals = new HashMap<>();
    private final Map<String, List<Circle>> shapeMidpointHandles = new HashMap<>();
    private final Map<String, Line> fenceSelectionHighlights = new HashMap<>();
    private javafx.scene.shape.Shape selectedObjectHighlight;
    private Rectangle multiSelectionBounds;
    private final List<Node> fenceInteractionNodes = new ArrayList<>();
    private Set<String> knownGroups = new HashSet<>();
    private ListView<SummaryListItem> summaryList;
    private VBox sidebar;
    private Label mapToolStatusLabel;
    private Label planTitleLabel;
    private Label saveStatusLabel;
    private TextField objectSearchField;
    private ListView<ObjectListEntry> objectList;
    private ListView<ChecklistItem> checklistList;
    private ListView<ChecklistSuggestion> checklistSuggestionList;
    private TextField checklistItemField;
    private Button revealObjectButton;
    private Label activeSelectionCountLabel;
    private TitledPane objectListSection;
    private TitledPane selectedObjectSection;
    private TitledPane powerSummarySection;
    private TitledPane inventorySection;
    private VBox inventoryContent;
    private boolean fenceInventoryExpanded;
    private boolean gardenStoneInventoryExpanded;
    private boolean tentsInventoryExpanded;
    private boolean cableInventoryExpanded;
    private TitledPane equipmentSection;
    private TitledPane objectInventorySection;
    private TitledPane outletSection;
    private TextField planNameField;
    private TextField pixelsPerMeterField;
    private Label selectedTypeLabel;
    private TextField nameField;
    private ComboBox<String> groupField;
    private CheckBox lockedCheckBox;
    private CheckBox showMapLabelCheckBox;
    private Button resetMapLabelButton;
    private Slider selectedObjectOpacitySlider;
    private Label generalRotationLabel;
    private TextField generalRotationField;
    private TextField tentWidthField;
    private TextField tentHeightField;
    private TextField tentRotationField;
    private ColorPicker tentColorPicker;
    private Slider tentOpacitySlider;
    private ColorPicker powerSourceColorPicker;
    private Slider powerSourceSizeSlider;
    private ComboBox<CustomObjectShape> customObjectShapeComboBox;
    private ColorPicker customObjectColorPicker;
    private Slider customObjectOpacitySlider;
    private ColorPicker textObjectColorPicker;
    private Slider textObjectFontSizeSlider;
    private Slider textObjectTextOpacitySlider;
    private CheckBox textObjectSyncNotesCheckBox;
    private CheckBox textObjectReferenceLineCheckBox;
    private ComboBox<MarkerType> markerTypeComboBox;
    private ColorPicker markerColorPicker;
    private ColorPicker areaColorPicker;
    private Slider areaOpacitySlider;
    private Label areaSizeLabel;
    private Label areaPerimeterLabel;
    private ColorPicker lineColorPicker;
    private Slider lineWidthSlider;
    private Label lineLengthLabel;
    private TextField fenceSegmentCountField;
    private TextField fenceSegmentLengthField;
    private TextField fenceRotationField;
    private Label fenceTotalLengthLabel;
    private Label fenceNetworkSummaryLabel;
    private Label fenceNetworkStoneSummaryLabel;
    private Button decreaseFenceNetworkStonesButton;
    private Button increaseFenceNetworkStonesButton;
    private CheckBox showFenceInventoryLabelCheckBox;
    private ColorPicker fenceColorPicker;
    private Slider fenceWidthSlider;
    private Button resetFenceInventoryLabelButton;
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
    private VBox cablePieceEditor;
    private TextField cableNotesField;
    private Slider cableOpacitySlider;
    private CheckBox showSelectedCableLabelCheckBox;
    private Button resetCableLabelButton;
    private Button removePowerConnectionButton;
    private Button makeDefaultPowerConnectionButton;
    private TextArea notesArea;
    private ListView<String> equipmentList;
    private Button addEquipmentButton;
    private ListView<InventoryItem> objectInventoryList;
    private Button addObjectInventoryButton;
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
    private VBox fenceRowPanel;
    private VBox tentPanel;
    private VBox powerSourcePanel;
    private Button powerSourceAttachmentsButton;
    private VBox powerConnectionPanel;
    private VBox equipmentPanel;
    private VBox outletPanel;
    private Button deleteObjectButton;
    private Button choosePowerSourceButton;
    private ToggleButton measureButton;
    private ToggleButton addCablePointButton;
    private Button clearCableRouteButton;
    private VBox cableLayersPanel;
    private CheckMenuItem cablesLayerMenuItem;
    private CheckMenuItem cableLabelsLayerMenuItem;
    private CheckMenuItem powerSourcesLayerMenuItem;
    private MenuItem importTartuCabinetsMenuItem;
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
    private ToggleButton showFenceInventoryLabelsButton;
    private ComboBox<PlacementType> placementTypeComboBox;
    private Button addPlacementButton;
    private PlannerObject selectedObject;
    private PlannerObject pendingPowerSourceConsumer;
    private boolean loadingTartuCabinetAttachments;
    private String pendingPlacementName;
    private String pendingPlacementGroupName;
    private String pendingPlacementColorHex;
    private Double pendingPlacementWidthMeters;
    private Double pendingPlacementHeightMeters;
    private Double pendingPlacementOpacity;
    private Double pendingPlacementLineWidthPixels;
    private Double pendingPlacementFontSizePixels;
    private Boolean pendingPlacementShowMapLabel;
    private Boolean pendingPlacementShowFenceInventoryLabel;
    private Double pendingFenceSegmentLengthMeters;
    private CustomObjectShape pendingPlacementShape;
    private boolean pendingTentPlacement;
    private PlacementType pendingTentPlacementType;
    private boolean pendingPowerSourcePlacement;
    private PlacementType pendingPowerSourcePlacementType;
    private boolean pendingCustomObjectPlacement;
    private boolean pendingTextObjectPlacement;
    private boolean pendingMarkerPlacement;
    private boolean pendingLineObjectPlacement;
    private boolean pendingFenceRowPlacement;
    private boolean pendingFenceRingPlacement;
    private double pendingFenceRingRadiusMeters;
    private String pendingFenceStartJointId;
    private String pendingFenceTemplateRowId;
    private boolean pendingAreaObjectPlacement;
    private MarkerType pendingPlacementMarkerType;
    private PlannerObject copiedObject;
    private FenceNetworkClipboard copiedFenceNetwork;
    private List<MultiObjectClipboardEntry> copiedObjects = List.of();
    private Position copiedObjectsOrigin;
    private int keyboardPasteCount;
    private boolean updatingOpacityControls;
    private boolean opacityDragChanged;
    private boolean updatingDetailControls;
    private boolean detailSliderDragChanged;
    private boolean mapLayoutLocked;
    private boolean organizerView;
    private boolean objectEditDialogOpen;
    private Stage stage;
    private EventPlan cachedMapPlan;
    private long cachedMapRevision = Long.MIN_VALUE;
    private String cachedMapPath = "";
    private Image cachedMapImage;
    private final PauseTransition zoomRedrawDebounce = new PauseTransition(Duration.millis(120));

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        zoomRedrawDebounce.setOnFinished(event -> redrawMap());
        organizerView = preferences.getBoolean(ORGANIZER_VIEW_PREFERENCE, true);
        String startupPlanError = initializePlan();
        objectListHeight = loadObjectListHeightPreference();

        BorderPane root = new BorderPane();
        root.setTop(new VBox(createMenuBar(), createToolbar()));
        root.setCenter(createContent());

        refreshOrganizerViewControls();
        refreshGroupFilters();
        redrawMap();
        refreshSummary();
        refreshDetails();

        Scene scene = new Scene(root, 1200, 760);
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
        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            if (multiObjectRotationState != null) {
                Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
                multiObjectRotationDelta = normalizeAngleDelta(
                        pointerRotationDegrees(mapPoint, multiObjectRotationState.center())
                                - multiObjectRotationState.pointerStartRotationDegrees()
                );
                applyMultiObjectRotation(multiObjectRotationDelta);
                redrawMap();
                refreshSummary();
                recordPlanDragChange();
                event.consume();
                return;
            }
            if (!multiObjectDragInProgress || multiObjectDragState == null) {
                return;
            }
            Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
            applyMultiObjectDrag(mapPoint);
            multiObjectDragChanged = true;
            updateMultiObjectDragPreview(mapPoint);
            recordPlanDragChange();
            event.consume();
        });
        scene.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            if (multiObjectRotationState != null) {
                finishObjectRotation();
                event.consume();
            }
            if (multiObjectDragInProgress) {
                finishMultiObjectDrag();
                event.consume();
            }
            planDragInProgress = false;
            planDragRecorded = false;
        });
        scene.addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
            if (measuringActive && measurementStart != null && isInsideMapPane(event.getTarget())) {
                Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
                updateMeasurementPreview(new Position(mapPoint.getX(), mapPoint.getY()));
            }
        });
        scene.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (measuringActive && isInsideMapPane(event.getTarget())) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    if (editingMeasurementPath == null) {
                        Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
                        addMeasurementPoint(new Position(mapPoint.getX(), mapPoint.getY()));
                    }
                    if (event.getClickCount() == 2) {
                        finishMeasurementTool();
                    }
                }
                event.consume();
            }
        });
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE && selectionBox != null) {
                cancelSelectionBox();
                event.consume();
            } else if (event.getCode() == KeyCode.ENTER && measuringActive
                    && (activeMeasurementPath != null || editingMeasurementPath != null)) {
                finishMeasurementTool();
                event.consume();
            } else if (event.getCode() == KeyCode.ENTER && isShapePlacementPending()) {
                finishPendingShapePlacement();
                event.consume();
            } else if (event.getCode() == KeyCode.ESCAPE && rotatingObjectId != null) {
                finishObjectRotation();
                event.consume();
            } else if (event.getCode() == KeyCode.ESCAPE && isPlacementPending()) {
                cancelPlacement();
                event.consume();
            } else if (event.getCode() == KeyCode.ESCAPE && addingCablePoint) {
                finishEditingCableRoute();
                event.consume();
            } else if (event.getCode() == KeyCode.ESCAPE && measuringActive) {
                finishMeasurementTool();
                event.consume();
            } else if (event.getCode() == KeyCode.ESCAPE && selectedLogicalObjects().size() > 1) {
                selectObject(null);
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
        Platform.runLater(this::checkForUpdatesOnStartup);
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
                && hasCopiedObjects()) {
            pasteCopiedObjectWithOffset();
            event.consume();
            return;
        }
        if (selectedObject == null) {
            if (event.getCode() == KeyCode.DELETE && !selectedMeasurementPaths.isEmpty()) {
                deleteSelectedMeasurementPaths();
                event.consume();
            }
            return;
        }
        if (event.getCode() == KeyCode.ENTER
                && event.isAltDown()
                && !event.isControlDown()
                && !event.isShiftDown()) {
            editObject(selectedObject);
            event.consume();
        } else if (event.getCode() == KeyCode.L
                && event.isControlDown()
                && !event.isAltDown()
                && !event.isShiftDown()) {
            lockedCheckBox.setSelected(!allSelectedObjectsLocked());
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
            toggleSelectedObjectsHidden();
            event.consume();
        } else if (event.getCode() == KeyCode.R
                && event.isControlDown()
                && !event.isAltDown()
                && !event.isShiftDown()) {
            startObjectRotation(selectedObject);
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
        updatePlanHistoryButtons();
    }

    private void redoPlanChange() {
        planHistory.redo().ifPresent(this::restorePlanSnapshot);
        updatePlanHistoryButtons();
    }

    private void updatePlanHistoryButtons() {
        if (undoButton != null) {
            undoButton.setDisable(!planHistory.canUndo());
        }
        if (redoButton != null) {
            redoButton.setDisable(!planHistory.canRedo());
        }
        if (undoMenuItem != null) {
            undoMenuItem.setDisable(!planHistory.canUndo());
        }
        if (redoMenuItem != null) {
            redoMenuItem.setDisable(!planHistory.canRedo());
        }
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
        refreshChecklist();
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

    private boolean isInsideMapPane(Object target) {
        if (!(target instanceof Node node)) {
            return false;
        }
        Node current = node;
        while (current != null) {
            if (current == mapPane) {
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

    private MenuBar createMenuBar() {
        MenuItem newPlanItem = new MenuItem("Uus plaan");
        newPlanItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        newPlanItem.setOnAction(event -> newPlan());

        MenuItem openPlanItem = new MenuItem("Ava…");
        openPlanItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        openPlanItem.setOnAction(event -> openPlan());

        MenuItem savePlanItem = new MenuItem("Salvesta");
        savePlanItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        savePlanItem.setOnAction(event -> savePlan());

        MenuItem savePlanAsItem = new MenuItem("Salvesta kui…");
        savePlanAsItem.setAccelerator(new KeyCodeCombination(
                KeyCode.S,
                KeyCombination.CONTROL_DOWN,
                KeyCombination.SHIFT_DOWN
        ));
        savePlanAsItem.setOnAction(event -> savePlanAs());

        MenuItem exportTextItem = new MenuItem("Tekstiraport (TXT)…");
        exportTextItem.setOnAction(event -> exportSummary());
        MenuItem exportImageItem = new MenuItem("Kaardipilt (PNG)…");
        exportImageItem.setOnAction(event -> exportMapImage());
        MenuItem exportPdfItem = new MenuItem("Plaan ja raport (PDF)…");
        exportPdfItem.setOnAction(event -> exportPdf());
        Menu exportMenu = new Menu("Ekspordi");
        exportMenu.getItems().addAll(exportTextItem, exportImageItem, exportPdfItem);

        MenuItem planSettingsItem = new MenuItem("Plaani andmed…");
        planSettingsItem.setAccelerator(new KeyCodeCombination(
                KeyCode.P,
                KeyCombination.CONTROL_DOWN,
                KeyCombination.SHIFT_DOWN
        ));
        planSettingsItem.setOnAction(event -> showPlanSettingsDialog());
        importTartuCabinetsMenuItem = new MenuItem("Impordi Tartu püsivoolukilbid…");
        importTartuCabinetsMenuItem.setOnAction(event -> importTartuPowerCabinets());

        Menu fileMenu = new Menu("Fail");
        fileMenu.getItems().addAll(
                newPlanItem,
                openPlanItem,
                new SeparatorMenuItem(),
                savePlanItem,
                savePlanAsItem,
                new SeparatorMenuItem(),
                exportMenu,
                new SeparatorMenuItem(),
                importTartuCabinetsMenuItem,
                planSettingsItem
        );

        undoMenuItem = new MenuItem("Võta tagasi (Ctrl+Z)");
        undoMenuItem.setOnAction(event -> undoPlanChange());
        redoMenuItem = new MenuItem("Tee uuesti (Ctrl+Alt+Z)");
        redoMenuItem.setOnAction(event -> redoPlanChange());
        MenuItem editObjectItem = new MenuItem("Muuda objekti (Alt+Enter)");
        editObjectItem.setOnAction(event -> {
            if (selectedObject != null) {
                editObject(selectedObject);
            }
        });
        MenuItem copyObjectItem = new MenuItem("Kopeeri objekt (Ctrl+C)");
        copyObjectItem.setOnAction(event -> copySelectedObject());
        MenuItem pasteObjectItem = new MenuItem("Kleebi objekt (Ctrl+V)");
        pasteObjectItem.setOnAction(event -> pasteCopiedObjectWithOffset());
        MenuItem lockObjectItem = new MenuItem();
        lockObjectItem.setOnAction(event -> {
            if (selectedObject != null) {
                lockedCheckBox.setSelected(!allSelectedObjectsLocked());
                updateSelectedLock();
            }
        });
        MenuItem visibilityItem = new MenuItem();
        visibilityItem.setOnAction(event -> {
            if (selectedObject != null) {
                toggleSelectedObjectsHidden();
            }
        });
        MenuItem deleteObjectItem = new MenuItem("Kustuta objekt (Delete)");
        deleteObjectItem.setOnAction(event -> deleteSelectedObject());

        Menu editMenu = new Menu("Redigeeri");
        editMenu.getItems().addAll(
                undoMenuItem,
                redoMenuItem,
                new SeparatorMenuItem(),
                editObjectItem,
                copyObjectItem,
                pasteObjectItem,
                new SeparatorMenuItem(),
                lockObjectItem,
                visibilityItem,
                deleteObjectItem
        );
        editMenu.setOnShowing(event -> {
            updatePlanHistoryButtons();
            boolean objectSelected = selectedObject != null;
            editObjectItem.setDisable(!objectSelected);
            copyObjectItem.setDisable(!objectSelected);
            pasteObjectItem.setDisable(!hasCopiedObjects() || mapLayoutLocked);
            lockObjectItem.setDisable(!objectSelected);
            visibilityItem.setDisable(!objectSelected);
            deleteObjectItem.setDisable(!objectSelected || mapLayoutLocked
                    || selectedObjects().stream().anyMatch(this::isObjectEffectivelyLocked));
            lockObjectItem.setText(objectSelected && allSelectedObjectsLocked()
                    ? "Eemalda valitud objektide lukustus (Ctrl+L)"
                    : "Lukusta valitud objektid (Ctrl+L)");
            visibilityItem.setText(objectSelected && allSelectedObjectsHidden()
                    ? "Kuva valitud objektid (Ctrl+H)"
                    : "Peida valitud objektid (Ctrl+H)");
        });

        MenuItem resetZoomItem = new MenuItem("Taasta 100% suum");
        resetZoomItem.setOnAction(event -> setZoom(1.0));
        CheckMenuItem layoutLockItem = new CheckMenuItem("Paigutus lukus");
        layoutLockItem.setOnAction(event -> setMapLayoutLocked(layoutLockItem.isSelected()));
        CheckMenuItem objectLabelsItem = mapLayerMenuItem("Objektide nimed", () -> showObjectLabelsButton);
        cablesLayerMenuItem = mapLayerMenuItem("Kaablid", () -> showCablesButton);
        cableLabelsLayerMenuItem = mapLayerMenuItem("Kaablisildid", () -> showCableLabelsButton);
        CheckMenuItem tentsItem = mapLayerMenuItem("Telgid", () -> showTentsButton);
        powerSourcesLayerMenuItem = mapLayerMenuItem("Elektrikapid", () -> showPowerSourcesButton);
        CheckMenuItem customObjectsItem = mapLayerMenuItem("Objektid", () -> showCustomObjectsButton);
        CheckMenuItem textObjectsItem = mapLayerMenuItem("Tekstid", () -> showTextObjectsButton);
        CheckMenuItem markerObjectsItem = mapLayerMenuItem("Markerid", () -> showMarkerObjectsButton);
        CheckMenuItem areaObjectsItem = mapLayerMenuItem("Alad", () -> showAreaObjectsButton);
        CheckMenuItem lineObjectsItem = mapLayerMenuItem("Jooned", () -> showLineObjectsButton);
        Menu layersMenu = new Menu("Kaardi kihid");
        layersMenu.getItems().addAll(
                objectLabelsItem,
                cablesLayerMenuItem,
                cableLabelsLayerMenuItem,
                new SeparatorMenuItem(),
                tentsItem,
                powerSourcesLayerMenuItem,
                customObjectsItem,
                textObjectsItem,
                markerObjectsItem,
                areaObjectsItem,
                lineObjectsItem
        );
        CheckMenuItem organizerViewItem = new CheckMenuItem("Korraldajavaade");
        organizerViewItem.setOnAction(event -> setOrganizerView(organizerViewItem.isSelected()));
        Menu viewMenu = new Menu("Vaade");
        viewMenu.getItems().addAll(
                resetZoomItem,
                layoutLockItem,
                organizerViewItem,
                new SeparatorMenuItem(),
                layersMenu
        );
        viewMenu.setOnShowing(event -> {
            layoutLockItem.setSelected(mapLayoutLocked);
            organizerViewItem.setSelected(organizerView);
            objectLabelsItem.setSelected(showObjectLabelsButton.isSelected());
            cablesLayerMenuItem.setSelected(showCablesButton.isSelected());
            cableLabelsLayerMenuItem.setSelected(showCableLabelsButton.isSelected());
            tentsItem.setSelected(showTentsButton.isSelected());
            powerSourcesLayerMenuItem.setSelected(showPowerSourcesButton.isSelected());
            customObjectsItem.setSelected(showCustomObjectsButton.isSelected());
            textObjectsItem.setSelected(showTextObjectsButton.isSelected());
            markerObjectsItem.setSelected(showMarkerObjectsButton.isSelected());
            areaObjectsItem.setSelected(showAreaObjectsButton.isSelected());
            lineObjectsItem.setSelected(showLineObjectsButton.isSelected());
        });

        MenuItem shortcutsItem = new MenuItem("Klahvikombinatsioonid");
        shortcutsItem.setOnAction(event -> showKeyboardShortcuts());
        MenuItem versionsItem = new MenuItem("Versioonid");
        versionsItem.setOnAction(event -> showVersionsDialog());
        MenuItem aboutItem = new MenuItem("Plaanisepa kohta");
        aboutItem.setOnAction(event -> showAboutDialog());
        Menu helpMenu = new Menu("Abi");
        helpMenu.getItems().addAll(
                shortcutsItem,
                versionsItem,
                new SeparatorMenuItem(),
                aboutItem
        );

        updatePlanHistoryButtons();
        return new MenuBar(fileMenu, editMenu, viewMenu, helpMenu);
    }

    private CheckMenuItem mapLayerMenuItem(
            String text,
            java.util.function.Supplier<ToggleButton> toggleButtonSupplier
    ) {
        CheckMenuItem item = new CheckMenuItem(text);
        item.setOnAction(event -> {
            ToggleButton toggleButton = toggleButtonSupplier.get();
            if (toggleButton == null) {
                return;
            }
            toggleButton.setSelected(item.isSelected());
            updateMapLayerVisibility();
        });
        return item;
    }

    private void showKeyboardShortcuts() {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.initOwner(stage);
        dialog.setTitle("Klahvikombinatsioonid");
        dialog.setHeaderText("Plaanisepa klahvikombinatsioonid");
        dialog.setContentText("""
                Failid
                Ctrl+N                 uus plaan
                Ctrl+O                 ava plaan
                Ctrl+S                 salvesta
                Ctrl+Shift+S           salvesta kui
                Ctrl+Shift+P           plaani andmed

                Muutmine
                Ctrl+Z                 võta tagasi
                Ctrl+Alt+Z             tee uuesti
                Alt+Enter              muuda valitud objekti
                Ctrl+C / Ctrl+V        kopeeri / kleebi objekt
                Ctrl+L                 lukusta või eemalda lukustus
                Ctrl+H                 peida või kuva objekt
                Delete                 kustuta objekt

                Kaart
                Ctrl+Shift+1…8         lisa valitud tüüpi objekt
                kaks korda Shift       otsi objekti
                Alt+hiirerull          suumi kursori asukoha järgi
                Escape                 lõpeta aktiivne tööriist või otsing
                """);
        dialog.showAndWait();
    }

    private void showAboutDialog() {
        String version = applicationVersion();
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.initOwner(stage);
        dialog.setTitle("Plaanisepa kohta");
        dialog.setHeaderText("Plaanisepp");
        dialog.setContentText("""
                Ürituse alaplaani ja elektrivajaduse planeerija

                Versioon: %s
                Java: %s
                """.formatted(version, System.getProperty("java.version", "määramata")));
        dialog.showAndWait();
    }

    private void showVersionsDialog() {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.initOwner(stage);
        dialog.setTitle("Plaanisepa versioonid");
        dialog.setHeaderText("Plaanisepp");
        dialog.setContentText("Paigaldatud versioon: " + applicationVersion()
                + "\n\nKontrollin uusimat versiooni…");
        ButtonType openReleasesButton = new ButtonType("Ava GitHub Releases");
        dialog.getButtonTypes().setAll(openReleasesButton, ButtonType.CLOSE);
        checkLatestRelease(dialog);
        dialog.showAndWait().ifPresent(button -> {
            if (button == openReleasesButton) {
                openGitHubReleases();
            }
        });
    }

    private String applicationVersion() {
        String implementationVersion = PlaaniseppApp.class.getPackage().getImplementationVersion();
        if (implementationVersion != null && !implementationVersion.isBlank()) {
            return implementationVersion;
        }
        return System.getProperty("plaanisepp.version", "arendusversioon");
    }

    private void openGitHubReleases() {
        try {
            getHostServices().showDocument(GITHUB_RELEASES_URL);
        } catch (RuntimeException exception) {
            showError("GitHub Releasesi ei saanud avada.", exception.getMessage());
        }
    }

    private void checkForUpdatesOnStartup() {
        Optional<GitHubReleaseService.ReleaseVersion> installedVersion = installedReleaseVersion();
        if (installedVersion.isEmpty()) {
            return;
        }
        gitHubReleaseService.fetchLatestRelease().whenComplete((latestRelease, exception) -> {
            if (exception != null || latestRelease.version().compareTo(installedVersion.get()) <= 0) {
                return;
            }
            Platform.runLater(() -> showUpdateAvailableDialog(latestRelease));
        });
    }

    private void checkLatestRelease(Alert dialog) {
        gitHubReleaseService.fetchLatestRelease().whenComplete((latestRelease, exception) -> Platform.runLater(() -> {
            if (exception != null) {
                dialog.setContentText("Paigaldatud versioon: " + applicationVersion()
                        + "\n\nUusima versiooni kontroll ei õnnestunud."
                        + "\nGitHub Releasesis on saadaval paigalduspaketid ja varasemad versioonid.");
                return;
            }
            Optional<GitHubReleaseService.ReleaseVersion> installedVersion = installedReleaseVersion();
            String updateStatus = installedVersion.isPresent()
                    && latestRelease.version().compareTo(installedVersion.get()) > 0
                    ? "Saadaval on uuem versioon."
                    : "Kasutad uusimat avaldatud versiooni.";
            dialog.setContentText("Paigaldatud versioon: " + applicationVersion()
                    + "\nUusim avaldatud versioon: " + latestRelease.version()
                    + "\n\n" + updateStatus
                    + "\nGitHub Releasesis on saadaval paigalduspaketid ja varasemad versioonid."
                    + "\n\nUuenduse kontrollversioon: 0.4.2.");
        }));
    }

    private Optional<GitHubReleaseService.ReleaseVersion> installedReleaseVersion() {
        try {
            return Optional.of(GitHubReleaseService.ReleaseVersion.parse(applicationVersion()));
        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }

    private void showUpdateAvailableDialog(GitHubReleaseService.LatestRelease latestRelease) {
        Optional<GitHubReleaseService.ReleaseAsset> preferredAsset = latestRelease.preferredAssetFor(
                GitHubReleaseService.Platform.current()
        );
        if (preferredAsset.isEmpty()) {
            showReleasePageOnlyUpdateDialog(latestRelease);
            return;
        }

        GitHubReleaseService.ReleaseAsset asset = preferredAsset.get();
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.initOwner(stage);
        dialog.setTitle("Plaaniseppa saab uuendada");
        dialog.setHeaderText("Uus versioon on saadaval");
        dialog.setContentText("Praegune versioon: " + applicationVersion()
                + "\nUus versioon: " + latestRelease.version()
                + "\n\nSobiv paigalduspakett: " + asset.name()
                + "\nFail laaditakse sinu valitud asukohta ja selle terviklust kontrollitakse.");
        ButtonType downloadButton = new ButtonType("Uuenda");
        ButtonType openReleasesButton = new ButtonType("Ava GitHub Releases");
        dialog.getButtonTypes().setAll(downloadButton, openReleasesButton, ButtonType.CLOSE);
        dialog.showAndWait().ifPresent(button -> {
            if (button == downloadButton) {
                downloadReleaseAsset(asset);
            } else if (button == openReleasesButton) {
                openReleasePage(latestRelease.pageUrl());
            }
        });
    }

    private void downloadReleaseAsset(GitHubReleaseService.ReleaseAsset asset) {
        try {
            Path directory = Files.createTempDirectory("plaanisepp-update-");
            downloadReleaseAsset(asset, directory.resolve(asset.name()));
        } catch (IOException exception) {
            showError("Uuendust ei saanud alustada", exception.getMessage());
        }
    }

    private void showReleasePageOnlyUpdateDialog(GitHubReleaseService.LatestRelease latestRelease) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.initOwner(stage);
        dialog.setTitle("Plaaniseppa saab uuendada");
        dialog.setHeaderText("Uus versioon on saadaval");
        dialog.setContentText("Praegune versioon: " + applicationVersion()
                + "\nUus versioon: " + latestRelease.version()
                + "\n\nSellele operatsioonisüsteemile ei leitud sobivat automaatset paigalduspaketti.");
        ButtonType openReleasesButton = new ButtonType("Ava GitHub Releases");
        dialog.getButtonTypes().setAll(openReleasesButton, ButtonType.CLOSE);
        dialog.showAndWait().ifPresent(button -> {
            if (button == openReleasesButton) {
                openReleasePage(latestRelease.pageUrl());
            }
        });
    }

    private void chooseAndDownloadReleaseAsset(GitHubReleaseService.ReleaseAsset asset) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Salvesta Plaanisepa uuendus");
        chooser.setInitialFileName(asset.name());
        File selectedFile = chooser.showSaveDialog(stage);
        if (selectedFile == null) {
            return;
        }
        Path destination = selectedFile.toPath();
        if (Files.exists(destination) && !confirmReplacingReleaseAsset(destination)) {
            return;
        }

        downloadReleaseAsset(asset, destination);
    }

    private void downloadReleaseAsset(GitHubReleaseService.ReleaseAsset asset, Path destination) {
        Alert progressDialog = new Alert(Alert.AlertType.INFORMATION);
        progressDialog.initOwner(stage);
        progressDialog.setTitle("Plaanisepa uuenduse allalaadimine");
        progressDialog.setHeaderText("Allalaadimine käib");
        progressDialog.setContentText("Laadin faili " + asset.name() + " alla ja kontrollin selle terviklust…");
        progressDialog.getButtonTypes().setAll(ButtonType.CLOSE);
        progressDialog.show();
        releaseAssetDownloadService.downloadAndVerify(asset, destination).whenComplete((file, exception) ->
                Platform.runLater(() -> {
                    progressDialog.close();
                    if (exception != null) {
                        showError("Uuenduse allalaadimine ebaõnnestus", rootMessage(exception));
                        return;
                    }
                    showDownloadedReleaseDialog(file);
                })
        );
    }

    private boolean confirmReplacingReleaseAsset(Path destination) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.initOwner(stage);
        confirmation.setTitle("Asenda olemasolev fail?");
        confirmation.setHeaderText("Fail on juba olemas");
        confirmation.setContentText(destination.getFileName() + " asendatakse pärast kontrollitud allalaadimist.");
        return confirmation.showAndWait().filter(ButtonType.OK::equals).isPresent();
    }

    private void showDownloadedReleaseDialog(Path file) {
        boolean installer = !file.getFileName().toString().toLowerCase().endsWith(".tar.gz");
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.initOwner(stage);
        dialog.setTitle("Uuendus on allalaaditud");
        dialog.setHeaderText("Faili terviklus on kontrollitud");
        dialog.setContentText(file.getFileName() + " on salvestatud asukohta:\n" + file);
        ButtonType openButton = new ButtonType(installer ? "Käivita uuendus" : "Ava kaust");
        dialog.getButtonTypes().setAll(openButton, ButtonType.CLOSE);
        dialog.showAndWait().ifPresent(button -> {
            if (button == openButton) {
                openDownloadedRelease(file, installer);
            }
        });
    }

    private void openDownloadedRelease(Path file, boolean installer) {
        try {
            Path target = installer ? file : file.getParent();
            GitHubReleaseService.Platform platform = GitHubReleaseService.Platform.current();
            if (platform != GitHubReleaseService.Platform.WINDOWS) {
                if (installer) {
                    scheduleLinuxRestartAfterInstall();
                }
            }
            new ProcessBuilder(downloadedReleaseOpenCommand(platform, target, installer)).start();
            // JavaFX GTK cleanup can abort while Discover is opening the RPM.
            // The updater process is already detached, so terminate directly.
            System.exit(0);
        } catch (IOException | UnsupportedOperationException exception) {
            showError("Allalaaditud faili ei saanud avada", exception.getMessage());
        }
    }

    static List<String> downloadedReleaseOpenCommand(
            GitHubReleaseService.Platform platform,
            Path target,
            boolean installer
    ) {
        if (platform == GitHubReleaseService.Platform.WINDOWS) {
            return installer
                    ? List.of(target.toString())
                    : List.of("explorer.exe", target.toString());
        }
        return List.of("xdg-open", target.toString());
    }

    private void scheduleLinuxRestartAfterInstall() {
        Optional<String> executable = ProcessHandle.current().info().command();
        if (executable.isEmpty() || !Files.isExecutable(Path.of(executable.get()))) {
            return;
        }
        String monitorScript = ""
                + "target=\"$1\"; "
                + "before=$(stat -c '%i:%Y:%s' -- \"$target\" 2>/dev/null || true); "
                + "i=0; "
                + "while [ $i -lt 300 ]; do "
                + "sleep 1; "
                + "after=$(stat -c '%i:%Y:%s' -- \"$target\" 2>/dev/null || true); "
                + "if [ -n \"$before\" ] && [ \"$after\" != \"$before\" ]; then "
                + "nohup \"$target\" >/dev/null 2>&1 </dev/null & exit 0; fi; "
                + "i=$((i + 1)); done";
        try {
            new ProcessBuilder("sh", "-c", monitorScript, "plaanisepp-updater", executable.get())
                    .start();
        } catch (IOException ignored) {
            // The installer can still be opened; automatic restart is best effort.
        }
    }

    private String rootMessage(Throwable exception) {
        Throwable cause = exception;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause.getMessage() == null || cause.getMessage().isBlank()
                ? "Tundmatu viga."
                : cause.getMessage();
    }

    private void openReleasePage(java.net.URI releasePageUrl) {
        try {
            getHostServices().showDocument(releasePageUrl.toString());
        } catch (RuntimeException exception) {
            showError("GitHub Releasesi ei saanud avada.", exception.getMessage());
        }
    }

    private ToolBar createToolbar() {
        undoButton = new Button("↶");
        undoButton.setTooltip(new Tooltip("Võta viimane plaanimuudatus tagasi (Ctrl+Z)"));
        undoButton.setOnAction(event -> undoPlanChange());

        redoButton = new Button("↷");
        redoButton.setTooltip(new Tooltip("Tee viimati tagasivõetud plaanimuudatus uuesti (Ctrl+Alt+Z)"));
        redoButton.setOnAction(event -> redoPlanChange());
        updatePlanHistoryButtons();

        placementTypeComboBox = new ComboBox<>();
        placementTypeComboBox.getItems().addAll(PlacementType.values());
        placementTypeComboBox.getSelectionModel().select(PlacementType.TENT);
        placementTypeComboBox.setPrefWidth(120);

        addPlacementButton = new Button("Lisa");
        addPlacementButton.setTooltip(new Tooltip(
                "Vali tüüp ja vajuta kaardile, kuhu objekt lisada (kiirvalikud Ctrl+Shift+1…8)"
        ));
        addPlacementButton.setOnAction(event -> toggleSelectedPlacement());

        zoomSlider = new Slider(25, 800, zoomPercentage * 100);
        zoomSlider.setPrefWidth(140);
        zoomSlider.setBlockIncrement(5);
        zoomSlider.setTooltip(new Tooltip("Kaardi suum 25–800% (100% mahutab kogu aluskaardi vaatesse)"));
        zoomSlider.valueProperty().addListener((observable, oldValue, newValue) ->
                setZoom(newValue.doubleValue() / 100.0)
        );

        zoomPercentButton = new Button(zoomPercentText());
        zoomPercentButton.setTooltip(new Tooltip("Mahuta kogu aluskaart vaatesse (100%)"));
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
        showFenceInventoryLabelsButton = objectTypeToggle(
                "Aedade kogused",
                "Näitab või peidab kaardil aiaridade koguse ja pikkuse"
        );
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

        mapToolStatusLabel = new Label();
        mapToolStatusLabel.setStyle("-fx-text-fill: #374151;");
        updateMapToolStatus();

        saveStatusLabel = new Label("Salvestatud");
        saveStatusLabel.setStyle("-fx-text-fill: #166534; -fx-font-weight: bold;");
        planTitleLabel = new Label();
        planTitleLabel.setStyle("-fx-font-weight: bold;");
        updatePlanTitleLabel();

        return new ToolBar(
                undoButton,
                redoButton,
                new Separator(),
                new Label("Lisa"),
                placementTypeComboBox,
                addPlacementButton,
                new Separator(),
                zoomSlider,
                zoomPercentButton,
                measureButton,
                clearMeasurementsButton,
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

    private void setOrganizerView(boolean enabled) {
        if (organizerView == enabled) {
            return;
        }
        organizerView = enabled;
        preferences.putBoolean(ORGANIZER_VIEW_PREFERENCE, enabled);
        if (enabled) {
            if (pendingPowerSourcePlacement || pendingPowerSourceConsumer != null || addingCablePoint) {
                cancelPlacement();
                pendingPowerSourceConsumer = null;
                finishEditingCableRoute();
            }
            if (selectedObject instanceof PowerSource) {
                selectedObject = null;
            }
        }
        refreshOrganizerViewControls();
        refreshPlacementButtons();
        updateMapToolStatus();
        refreshObjectList();
        refreshDetails();
        refreshSummary();
        redrawMap();
    }

    private void refreshOrganizerViewControls() {
        if (placementTypeComboBox != null) {
            PlacementType selectedType = placementTypeComboBox.getSelectionModel().getSelectedItem();
            List<PlacementType> availableTypes = organizerView
                    ? List.of(
                            PlacementType.TENT,
                            PlacementType.DJ_TRUCK,
                            PlacementType.CUSTOM_OBJECT,
                            PlacementType.TEXT_OBJECT,
                            PlacementType.MARKER_OBJECT,
                            PlacementType.LINE_OBJECT,
                            PlacementType.FENCE_ROW,
                            PlacementType.FENCE_RING,
                            PlacementType.AREA_OBJECT
                    )
                    : List.of(PlacementType.values());
            placementTypeComboBox.getItems().setAll(availableTypes);
            if (selectedType != null && availableTypes.contains(selectedType)) {
                placementTypeComboBox.getSelectionModel().select(selectedType);
            } else {
                placementTypeComboBox.getSelectionModel().select(PlacementType.TENT);
            }
        }
        setSectionVisible(powerSummarySection, !organizerView);
        setSectionVisible(cableLayersPanel, !organizerView);
        setSectionVisible(showPowerSourcesButton, !organizerView);
        if (cablesLayerMenuItem != null) {
            cablesLayerMenuItem.setVisible(!organizerView);
        }
        if (cableLabelsLayerMenuItem != null) {
            cableLabelsLayerMenuItem.setVisible(!organizerView);
        }
        if (powerSourcesLayerMenuItem != null) {
            powerSourcesLayerMenuItem.setVisible(!organizerView);
        }
        if (importTartuCabinetsMenuItem != null) {
            importTartuCabinetsMenuItem.setVisible(!organizerView);
        }
    }

    private void showMapLayoutLockedMessage() {
        showError(
                "Paigutus on lukus",
                "Eemalda menüüs Vaade valik „Paigutus lukus”, et kaardi geomeetriat muuta."
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
        showFenceInventoryLabelsButton.setSelected(plan.showFenceInventoryLabels());
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
        plan.setShowFenceInventoryLabels(showFenceInventoryLabelsButton.isSelected());
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
            if (canStartSelectionBox(event)) {
                startSelectionBox(event);
                event.consume();
            }
        });
        mapPane.setOnMouseDragged(event -> {
            updateMapDragState(event.getSceneX(), event.getSceneY());
            if (selectionBox != null) {
                updateSelectionBox(event);
                event.consume();
            }
        });
        mapPane.setOnMouseReleased(event -> {
            if (selectionBox != null) {
                finishSelectionBox();
                event.consume();
            }
        });
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
            if (pendingFenceRowPlacement && !mapDraggedSincePress) {
                addPendingFencePoint(new Position(event.getX(), event.getY()));
                return;
            }
            if (pendingFenceRingPlacement && !mapDraggedSincePress) {
                placeFenceRing(new Position(event.getX(), event.getY()));
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
            if (event.getButton() == MouseButton.PRIMARY
                    && !event.isControlDown()
                    && !mapDraggedSincePress
                    && (event.getTarget() == mapPane || event.getTarget() == mapImageView)) {
                selectObject(null);
            }
        });
        mapContentPane = new Pane(mapPane);
        updateZoomContentSize();

        mapScrollPane = new ScrollPane(mapContentPane);
        mapScrollPane.setPannable(true);
        mapScrollPane.setFitToWidth(false);
        mapScrollPane.setFitToHeight(false);
        mapScrollPane.setStyle("-fx-background: #eef1ec;");
        mapScrollPane.viewportBoundsProperty().addListener((observable, oldBounds, newBounds) -> {
            if (newBounds.getWidth() > 1 && newBounds.getHeight() > 1) {
                applyZoomScale();
            }
        });
        mapScrollPane.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (isFenceDragTarget(event.getTarget())) {
                mapScrollPane.setPannable(false);
            }
        });
        mapScrollPane.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> mapScrollPane.setPannable(true));
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

        HBox baseMapSwitcher = createBaseMapSwitcher();
        StackPane mapView = new StackPane(mapScrollPane, baseMapSwitcher);
        StackPane.setAlignment(mapScrollPane, Pos.CENTER);
        StackPane.setAlignment(baseMapSwitcher, Pos.TOP_RIGHT);
        StackPane.setMargin(baseMapSwitcher, new Insets(12));

        sidebar = new VBox(10);
        sidebar.setPadding(new Insets(12));
        objectListSection = collapsibleSection(OBJECT_LIST_SECTION, "Objektid", createObjectListPanel(), false);
        TitledPane checklistSection = collapsibleSection(
                CHECKLIST_SECTION, "Checklist", createChecklistPanel(), false
        );
        selectedObjectSection = collapsibleSection(
                SELECTED_OBJECT_SECTION, "Valitud objekt", createDetailPanel(), true
        );
        TitledPane mapLayersSection = collapsibleSection(
                MAP_LAYERS_SECTION, "Kaardi kihid", createMapLayersPanel(), false
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
                    if (!item.isExpandable()) {
                        setText(item.text());
                        setStyle(item.text().contains("ÜLEKOORMUS")
                                ? "-fx-text-fill: #b91c1c; -fx-font-weight: bold;"
                                : "");
                        return;
                    }
                    Label textLabel = new Label(item.text());
                    Button toggleButton = summaryToggleButton(item);
                    HBox row = new HBox(4, toggleButton, textLabel);
                    row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                    row.setPadding(new Insets(0, 0, 0, item.depth() * 16.0));
                    setGraphic(row);
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

                Button toggleButton = summaryToggleButton(item);
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
                summaryList,
                true
        );
        inventoryContent = new VBox(6);
        inventorySection = collapsibleSection(
                INVENTORY_SECTION,
                "Inventar",
                inventoryContent,
                true
        );
        registerSidebarSection(OBJECT_LIST_SECTION, objectListSection);
        registerSidebarSection(CHECKLIST_SECTION, checklistSection);
        registerSidebarSection(SELECTED_OBJECT_SECTION, selectedObjectSection);
        registerSidebarSection(MAP_LAYERS_SECTION, mapLayersSection);
        registerSidebarSection(INVENTORY_SECTION, inventorySection);
        registerSidebarSection(SUMMARY_SECTION, powerSummarySection);
        applySidebarSectionOrder(loadSidebarSectionOrder());
        ScrollPane sidebarScrollPane = new ScrollPane(sidebar);
        sidebarScrollPane.setFitToWidth(true);

        SplitPane splitPane = new SplitPane(mapView, sidebarScrollPane);
        splitPane.setDividerPositions(0.72);
        return splitPane;
    }

    private HBox createBaseMapSwitcher() {
        defaultMapButton = new ToggleButton("Tavakaart");
        orthophotoMapButton = new ToggleButton("Ortofoto");
        defaultMapButton.setFocusTraversable(false);
        orthophotoMapButton.setFocusTraversable(false);
        defaultMapButton.setTooltip(new Tooltip("Laadi vaikimisi Tartu ala tavakaart Maa- ja Ruumiameti teenusest"));
        orthophotoMapButton.setTooltip(new Tooltip("Laadi vaikimisi Tartu ala ortofoto Maa- ja Ruumiameti teenusest"));
        defaultMapButton.setOnAction(event -> switchBaseMap(false));
        orthophotoMapButton.setOnAction(event -> switchBaseMap(true));
        HBox switcher = new HBox(0, defaultMapButton, orthophotoMapButton);
        switcher.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        switcher.setStyle("-fx-background-color: rgba(255,255,255,0.94);"
                + " -fx-background-radius: 5; -fx-border-color: #9ca3af;"
                + " -fx-border-radius: 5; -fx-padding: 2;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 6, 0, 0, 1);");
        refreshBaseMapSwitcher();
        return switcher;
    }

    private void applyDownloadedBaseMap(BaseMapDownload download) {
        PlanSnapshot before = planSnapshotService.create(plan);
        BaseMapBounds previousBounds = plan.downloadedMapBounds();
        double previousPixelsPerMeter = plan.pixelsPerMeter();
        plan.setDownloadedBaseMaps(download);
        if (previousBounds == null) {
            plan.scalePixelGeometry(plan.pixelsPerMeter() / previousPixelsPerMeter);
        } else {
            plan.reprojectPixelGeometry(
                    previousBounds,
                    previousPixelsPerMeter,
                    download.bounds(),
                    plan.pixelsPerMeter()
            );
        }
        if (pixelsPerMeterField != null) {
            pixelsPerMeterField.setText(String.format(Locale.ROOT, "%.4f", plan.pixelsPerMeter())
                    .replaceAll("0+$", "").replaceAll("\\.$", ""));
        }
        refreshBaseMapSwitcher();
        finishAutoAppliedDetailsChange(before, false);
        importTartuPowerCabinets(false);
    }

    private void importTartuPowerCabinets() {
        importTartuPowerCabinets(true);
    }

    private void importTartuPowerCabinets(boolean interactive) {
        if (!plan.hasDownloadedBaseMaps() || plan.downloadedMapBounds() == null) {
            showError(
                    "Püsivoolukilpe ei imporditud",
                    "Impordiks määra esmalt georefereeritud aluskaart Plaani andmete alt."
            );
            return;
        }
        try {
            stage.getScene().setCursor(Cursor.WAIT);
            List<TartuPowerCabinetImportService.Cabinet> cabinets =
                    tartuPowerCabinetImportService.load(plan.downloadedMapBounds());
            Set<String> existingNames = plan.objects().stream()
                    .filter(PowerSource.class::isInstance)
                    .map(object -> object.name().trim().toLowerCase(Locale.ROOT))
                    .collect(java.util.stream.Collectors.toSet());
            List<TartuPowerCabinetImportService.Cabinet> newCabinets = cabinets.stream()
                    .filter(cabinet -> !existingNames.contains(cabinet.name().toLowerCase(Locale.ROOT)))
                    .toList();
            if (newCabinets.isEmpty()) {
                if (interactive) {
                    showInformation("Püsivoolukilpide import", cabinets.isEmpty()
                            ? "Valitud kaardialal ei leitud püsivoolukilpe."
                            : "Kõik valitud kaardiala püsivoolukilbid on juba plaanis.");
                }
                return;
            }
            if (interactive) {
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.initOwner(stage);
                confirmation.setTitle("Impordi Tartu püsivoolukilbid");
                confirmation.setHeaderText("Leiti " + cabinets.size() + " püsivoolukilpi");
                confirmation.setContentText("Plaani lisatakse " + newCabinets.size()
                        + " uut kilpi koos nime ja saadaoleva lisainfoga. Jätkata?");
                if (confirmation.showAndWait().filter(ButtonType.OK::equals).isEmpty()) {
                    return;
                }
            }
            PlanSnapshot before = planSnapshotService.create(plan);
            ee.matteus.plaanisepp.core.map.BaseMapBounds bounds = plan.downloadedMapBounds();
            for (TartuPowerCabinetImportService.Cabinet cabinet : newCabinets) {
                Position position = new Position(
                        (cabinet.easting() - bounds.minX()) * pixelsPerMeter(),
                        (bounds.maxY() - cabinet.northing()) * pixelsPerMeter()
                );
                PowerSource source = new PowerSource(planFactory.newId(), cabinet.name(), position);
                source.setGroupName("Tartu püsivoolukilbid");
                source.setNotes(cabinet.details());
                source.setLocked(true);
                plan.addObject(source);
            }
            finishAutoAppliedDetailsChange(before, true);
            if (interactive) {
                showInformation("Püsivoolukilpide import",
                        "Plaani lisati " + newCabinets.size() + " püsivoolukilpi.");
            }
        } catch (IOException | InterruptedException exception) {
            if (exception instanceof InterruptedException) Thread.currentThread().interrupt();
            showError("Püsivoolukilpe ei imporditud", exception.getMessage());
        } finally {
            if (stage.getScene() != null) stage.getScene().setCursor(Cursor.DEFAULT);
        }
    }

    private OptionalLong tartuCabinetSourceId(PowerSource source) {
        return TartuPowerCabinetImportService.sourceIdFromNotes(source.notes());
    }

    private void showTartuCabinetAttachments(PowerSource source) {
        OptionalLong sourceId = tartuCabinetSourceId(source);
        if (sourceId.isEmpty() || loadingTartuCabinetAttachments) {
            return;
        }
        loadingTartuCabinetAttachments = true;
        if (powerSourceAttachmentsButton != null) {
            powerSourceAttachmentsButton.setDisable(true);
            powerSourceAttachmentsButton.setText("Laen lisafaile…");
        }
        if (stage.getScene() != null) {
            stage.getScene().setCursor(Cursor.WAIT);
        }
        Thread.startVirtualThread(() -> {
            try {
                List<TartuPowerCabinetImportService.Attachment> attachments =
                        tartuPowerCabinetImportService.loadAttachments(sourceId.getAsLong());
                Platform.runLater(() -> {
                    finishLoadingTartuCabinetAttachments();
                    if (attachments.isEmpty()) {
                        showInformation("Tartu GIS-i lisafailid", "Kilbil „" + source.name()
                                + "” ei ole Tartu GIS-is lisafaile.");
                    } else {
                        showTartuCabinetAttachmentDialog(source, attachments);
                    }
                });
            } catch (IOException | InterruptedException exception) {
                if (exception instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
                Platform.runLater(() -> {
                    finishLoadingTartuCabinetAttachments();
                    showError("Tartu GIS-i lisafaile ei saanud laadida", exception.getMessage());
                });
            }
        });
    }

    private void finishLoadingTartuCabinetAttachments() {
        loadingTartuCabinetAttachments = false;
        if (stage.getScene() != null) {
            stage.getScene().setCursor(Cursor.DEFAULT);
        }
        if (powerSourceAttachmentsButton != null) {
            powerSourceAttachmentsButton.setDisable(false);
            powerSourceAttachmentsButton.setText("Vaata lisafaile…");
        }
    }

    private void showTartuCabinetAttachmentDialog(
            PowerSource source,
            List<TartuPowerCabinetImportService.Attachment> attachments
    ) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(stage);
        dialog.setTitle("Tartu GIS-i lisafailid");
        dialog.setHeaderText(source.name());

        ListView<TartuPowerCabinetImportService.Attachment> attachmentList = new ListView<>();
        attachmentList.getItems().setAll(attachments);
        attachmentList.setPrefSize(680, 330);
        attachmentList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(TartuPowerCabinetImportService.Attachment attachment, boolean empty) {
                super.updateItem(attachment, empty);
                setText(empty || attachment == null ? null : attachmentDisplayText(attachment));
            }
        });
        attachmentList.getSelectionModel().selectFirst();

        Button openButton = new Button("Ava valitud fail");
        openButton.disableProperty().bind(attachmentList.getSelectionModel().selectedItemProperty().isNull());
        openButton.setOnAction(event -> openTartuCabinetAttachment(
                attachmentList.getSelectionModel().getSelectedItem()
        ));
        attachmentList.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                openTartuCabinetAttachment(attachmentList.getSelectionModel().getSelectedItem());
            }
        });

        Label explanation = new Label(
                "Fail avatakse otse Tartu linna GIS-ist ja seda ei salvestata plaanifaili."
        );
        explanation.setWrapText(true);
        VBox content = new VBox(10, explanation, attachmentList, openButton);
        content.setPadding(new Insets(4));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().setAll(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void openTartuCabinetAttachment(TartuPowerCabinetImportService.Attachment attachment) {
        if (attachment == null) {
            return;
        }
        try {
            getHostServices().showDocument(attachment.url());
        } catch (RuntimeException exception) {
            showError("Lisafaili ei saanud avada", exception.getMessage());
        }
    }

    private String attachmentDisplayText(TartuPowerCabinetImportService.Attachment attachment) {
        String type = attachment.contentType().isBlank() || attachment.contentType().equalsIgnoreCase("Unknown")
                ? "tundmatu failitüüp"
                : attachment.contentType();
        return "%s · %s · %s".formatted(
                attachment.name(),
                humanReadableFileSize(attachment.sizeBytes()),
                type
        );
    }

    private String humanReadableFileSize(long bytes) {
        if (bytes < 1_024) {
            return bytes + " B";
        }
        double kibibytes = bytes / 1_024.0;
        if (kibibytes < 1_024) {
            return String.format(Locale.ROOT, "%.1f KiB", kibibytes);
        }
        return String.format(Locale.ROOT, "%.1f MiB", kibibytes / 1_024.0);
    }

    private void switchBuiltInMap(String mapPath) {
        if (plan.mapImagePath().equals(mapPath)) {
            refreshBaseMapSwitcher();
            return;
        }
        PlanSnapshot before = planSnapshotService.create(plan);
        plan.setMapImagePath(mapPath);
        refreshBaseMapSwitcher();
        finishAutoAppliedDetailsChange(before, false);
    }

    private void switchBaseMap(boolean orthophoto) {
        if (!plan.hasDownloadedBaseMaps()) {
            if (!installDefaultApiBaseMaps(orthophoto, true)) {
                switchBuiltInMap(orthophoto ? ORTHOPHOTO_MAP_PATH : DEFAULT_MAP_PATH);
            }
            return;
        }
        if (plan.downloadedOrthophotoActive() == orthophoto) {
            refreshBaseMapSwitcher();
            return;
        }
        PlanSnapshot before = planSnapshotService.create(plan);
        plan.setDownloadedBaseMapActive(orthophoto);
        refreshBaseMapSwitcher();
        finishAutoAppliedDetailsChange(before, false);
    }

    private boolean installDefaultApiBaseMaps(boolean orthophoto, boolean showFailure) {
        double widthMetres = DEFAULT_API_MAP_WIDTH / DEFAULT_API_MAP_PIXELS_PER_METRE;
        double heightMetres = DEFAULT_API_MAP_HEIGHT / DEFAULT_API_MAP_PIXELS_PER_METRE;
        BaseMapBounds bounds = new BaseMapBounds(
                DEFAULT_API_MAP_CENTER_X - widthMetres / 2,
                DEFAULT_API_MAP_CENTER_Y - heightMetres / 2,
                DEFAULT_API_MAP_CENTER_X + widthMetres / 2,
                DEFAULT_API_MAP_CENTER_Y + heightMetres / 2
        );
        try {
            if (stage != null && stage.getScene() != null) {
                stage.getScene().setCursor(Cursor.WAIT);
            }
            BaseMapDownload download = new MaaAmetWmsClient().download(
                    bounds,
                    DEFAULT_API_MAP_WIDTH,
                    DEFAULT_API_MAP_HEIGHT,
                    RegularMapStyle.GRAYSCALE
            );
            PlanSnapshot before = planSnapshotService.create(plan);
            double previousPixelsPerMeter = plan.pixelsPerMeter();
            plan.setDownloadedBaseMaps(download);
            plan.scalePixelGeometry(plan.pixelsPerMeter() / previousPixelsPerMeter);
            if (orthophoto) {
                plan.setDownloadedBaseMapActive(true);
            }
            if (pixelsPerMeterField != null) {
                pixelsPerMeterField.setText(formatMeters(plan.pixelsPerMeter()));
            }
            refreshBaseMapSwitcher();
            finishAutoAppliedDetailsChange(before, false);
            importTartuPowerCabinets(false);
            return true;
        } catch (IOException | InterruptedException | RuntimeException exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            if (showFailure) {
                showError(
                        "Vaikekaarti ei saanud alla laadida",
                        "Kasutatakse rakendusega kaasas olevat varukaarti.\n\n" + exception.getMessage()
                );
            }
            return false;
        } finally {
            if (stage != null && stage.getScene() != null) {
                stage.getScene().setCursor(Cursor.DEFAULT);
            }
        }
    }

    private void refreshBaseMapSwitcher() {
        if (defaultMapButton == null || orthophotoMapButton == null || plan == null) {
            return;
        }
        if (plan.hasDownloadedBaseMaps()) {
            defaultMapButton.setSelected(!plan.downloadedOrthophotoActive());
            orthophotoMapButton.setSelected(plan.downloadedOrthophotoActive());
        } else {
            defaultMapButton.setSelected(DEFAULT_MAP_PATH.equals(plan.mapImagePath()));
            orthophotoMapButton.setSelected(ORTHOPHOTO_MAP_PATH.equals(plan.mapImagePath()));
        }
    }

    private Button summaryToggleButton(SummaryListItem item) {
        Button toggleButton = new Button(item.expanded() ? "▾" : "▸");
        toggleButton.setFocusTraversable(false);
        toggleButton.setMinWidth(28);
        toggleButton.setStyle("-fx-background-color: transparent; -fx-padding: 2 5 2 5;");
        toggleButton.setTooltip(new Tooltip(item.expanded() ? "Peida alamread" : "Näita alamridu"));
        toggleButton.setOnAction(event -> toggleSummaryItem(item.hierarchyKey()));
        return toggleButton;
    }

    private Button objectStateIconButton(
            String svgContent,
            boolean active,
            String tooltipText,
            Runnable action
    ) {
        return objectStateIconButton(svgContent, active, tooltipText, action, "#2563eb");
    }

    private Button objectStateIconButton(
            String svgContent,
            boolean active,
            String tooltipText,
            Runnable action,
            String activeColor
    ) {
        SVGPath icon = new SVGPath();
        icon.setContent(svgContent);
        icon.setFill(Color.web(active ? activeColor : "#9ca3af"));
        icon.setScaleX(0.65);
        icon.setScaleY(0.65);

        Button button = new Button();
        button.setGraphic(icon);
        button.setFocusTraversable(false);
        button.setMinSize(27, 27);
        button.setPrefSize(27, 27);
        button.setMaxSize(27, 27);
        button.setStyle("-fx-background-color: transparent; -fx-padding: 2;");
        button.setTooltip(new Tooltip(tooltipText));
        button.setAccessibleText(tooltipText);
        button.setOnAction(event -> action.run());
        return button;
    }

    private boolean canStartSelectionBox(MouseEvent event) {
        return event.getButton() == MouseButton.PRIMARY
                && event.isControlDown()
                && (event.getTarget() == mapPane || event.getTarget() == mapImageView)
                && !isPlacementPending()
                && !addingCablePoint
                && !measuringActive
                && pendingPowerSourceConsumer == null
                && rotatingObjectId == null;
    }

    private void startSelectionBox(MouseEvent event) {
        Point2D point = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
        selectionBoxStart = new Position(point.getX(), point.getY());
        selectionBox = new Rectangle(point.getX(), point.getY(), 0, 0);
        selectionBox.setFill(Color.web("#2563eb", 0.12));
        selectionBox.setStroke(Color.web("#2563eb"));
        selectionBox.setStrokeWidth(1.5 / Math.max(zoomLevel, 0.1));
        selectionBox.getStrokeDashArray().addAll(6.0 / Math.max(zoomLevel, 0.1), 4.0 / Math.max(zoomLevel, 0.1));
        selectionBox.setMouseTransparent(true);
        mapPane.getChildren().add(selectionBox);
        mapScrollPane.setPannable(false);
    }

    private void updateSelectionBox(MouseEvent event) {
        Point2D point = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
        double minX = Math.min(selectionBoxStart.x(), point.getX());
        double minY = Math.min(selectionBoxStart.y(), point.getY());
        selectionBox.setX(minX);
        selectionBox.setY(minY);
        selectionBox.setWidth(Math.abs(point.getX() - selectionBoxStart.x()));
        selectionBox.setHeight(Math.abs(point.getY() - selectionBoxStart.y()));
        selectionBox.toFront();
    }

    private void cancelSelectionBox() {
        if (selectionBox != null) {
            mapPane.getChildren().remove(selectionBox);
        }
        selectionBox = null;
        selectionBoxStart = null;
        mapScrollPane.setPannable(true);
    }

    private void finishSelectionBox() {
        Rectangle completedBox = selectionBox;
        selectionBox = null;
        selectionBoxStart = null;
        mapPane.getChildren().remove(completedBox);
        mapScrollPane.setPannable(true);
        if (completedBox.getWidth() < MAP_CLICK_DRAG_TOLERANCE_PX / Math.max(zoomLevel, 0.1)
                && completedBox.getHeight() < MAP_CLICK_DRAG_TOLERANCE_PX / Math.max(zoomLevel, 0.1)) {
            return;
        }

        List<PlannerObject> intersectingObjects = plan.objects().stream()
                .filter(this::isObjectVisibleOnMap)
                .filter(object -> selectionBoxIntersectsObject(completedBox, object))
                .toList();
        List<MeasurementPathView> intersectingMeasurements = measurementPaths.stream()
                .filter(path -> selectionBoxIntersectsMeasurement(completedBox, path))
                .toList();
        if (intersectingObjects.isEmpty() && intersectingMeasurements.isEmpty()) {
            return;
        }
        if (selectedObject != null && !updatingDetailControls) {
            commitPendingDetailFieldsBeforeSelectionChange();
        }
        intersectingObjects.forEach(object -> selectedObjectIds.addAll(logicalObjectIds(object)));
        intersectingMeasurements.stream()
                .filter(path -> !selectedMeasurementPaths.contains(path))
                .forEach(selectedMeasurementPaths::add);
        if (selectedObject == null) {
            selectedObject = intersectingObjects.isEmpty() ? null : intersectingObjects.getFirst();
        }
        refreshDetails();
        refreshObjectList();
        revealObjectInPowerSummary(selectedObject);
        redrawMap();
    }

    private boolean selectionBoxIntersectsObject(Rectangle box, PlannerObject object) {
        Node node = mapObjectNodes.get(object.id());
        return node != null && box.getBoundsInParent().intersects(node.getBoundsInParent());
    }

    private boolean selectionBoxIntersectsMeasurement(Rectangle box, MeasurementPathView path) {
        return path.nodes().stream()
                .anyMatch(node -> box.getBoundsInParent().intersects(node.getBoundsInParent()));
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
        objectList.setTooltip(new Tooltip(
                "Topeltklõps viib kaardil objektini\n"
                        + "Ctrl+klõps lisab või eemaldab objekti valikust\n"
                        + "Ctrl+Shift+klõps valib nähtavate ridade vahemiku\n"
                        + "Ctrl+klõps grupipäisel valib grupi nähtavad objektid"
        ));
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
                    boolean groupVisible = visibleGroups.contains(entry.groupName());
                    Button visibilityButton = objectStateIconButton(
                            "M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5C21.27 7.61 17 4.5 12 4.5zm0 12.5a5 5 0 1 1 0-10 5 5 0 0 1 0 10zm0-8a3 3 0 1 0 0 6 3 3 0 0 0 0-6z",
                            groupVisible,
                            groupVisible ? "Peida grupp kaardilt" : "Kuva grupp kaardil",
                            () -> setGroupVisible(entry.groupName(), !groupVisible)
                    );
                    boolean groupLocked = allGroupObjectsLocked(entry.groupName());
                    Button lockButton = objectStateIconButton(
                            "M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zm3.1-9H8.9V6c0-1.71 1.39-3.1 3.1-3.1s3.1 1.39 3.1 3.1v2z",
                            groupLocked,
                            groupLocked ? "Eemalda grupi lukustus" : "Lukusta grupp",
                            () -> setGroupLocked(entry.groupName(), !groupLocked)
                    );
                    Label groupLabel = new Label("%s (%d)".formatted(entry.groupName(), entry.objectCount()));
                    groupLabel.setStyle("-fx-font-weight: bold;");
                    HBox groupRow = new HBox(6, toggleButton, visibilityButton, lockButton, groupLabel);
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
                boolean lockedByGroup = plan.isGroupLocked(item.groupName());
                Label detailLabel = new Label(item.detailText()
                        + (lockedByGroup ? " · grupilukk" : ""));
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
                Button visibilityButton = objectStateIconButton(
                        "M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5C21.27 7.61 17 4.5 12 4.5zm0 12.5a5 5 0 1 1 0-10 5 5 0 0 1 0 10zm0-8a3 3 0 1 0 0 6 3 3 0 0 0 0-6z",
                        !item.object().hidden(),
                        item.object().hidden() ? "Kuva objekt kaardil" : "Peida objekt kaardilt",
                        () -> setObjectHidden(item, !item.object().hidden())
                );
                boolean individuallyLocked = item.object().locked();
                String lockTooltip = lockedByGroup
                        ? individuallyLocked
                                ? "Objekt ja grupp on lukus; vajutus eemaldab objekti isikliku luku"
                                : "Lukustatud grupi kaudu; vajutus lisab objektile isikliku luku"
                        : individuallyLocked ? "Eemalda objekti lukustus" : "Lukusta objekt";
                Button lockButton = objectStateIconButton(
                        "M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zm3.1-9H8.9V6c0-1.71 1.39-3.1 3.1-3.1s3.1 1.39 3.1 3.1v2z",
                        individuallyLocked || lockedByGroup,
                        lockTooltip,
                        () -> setObjectLocked(item.object(), !item.object().locked()),
                        lockedByGroup ? individuallyLocked ? "#7c3aed" : "#b45309" : "#2563eb"
                );
                VBox textBox = new VBox(2, nameLabel, detailLabel);
                HBox row = new HBox(6, visibilityButton, lockButton, colorSwatch, textBox);
                row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                row.setPadding(new Insets(0, 0, 0, 34));
                setText(null);
                setGraphic(row);
                String visibilityStyle = item.visible()
                        ? ""
                        : "-fx-text-fill: #6b7280; -fx-font-style: italic;";
                String selectionStyle = PlaaniseppApp.this.isSelected(item.object())
                        ? "-fx-background-color: rgba(37,99,235,0.24);"
                                + "-fx-border-color: transparent transparent transparent #2563eb;"
                                + "-fx-border-width: 0 0 0 3;"
                        : "";
                setStyle(visibilityStyle + selectionStyle);
                setOnContextMenuRequested(event -> {
                    showObjectContextMenu(
                            item.object(), event.getScreenX(), event.getScreenY()
                    );
                    event.consume();
                });
            }
        });
        objectList.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            ObjectListEntry entry = objectListEntryAt(event);
            if (event.getButton() == MouseButton.SECONDARY
                    && entry != null
                    && !entry.isGroup()
                    && isSelected(entry.objectItem().object())
                    && selectedLogicalObjects().size() > 1) {
                preservingSidebarMultiSelection = true;
                Platform.runLater(() -> preservingSidebarMultiSelection = false);
                return;
            }
            if (event.getButton() != MouseButton.PRIMARY || !event.isControlDown()) {
                return;
            }
            if (entry == null) {
                return;
            }
            if (entry.isGroup()) {
                selectObjectGroup(entry.groupName());
            } else if (event.isShiftDown()) {
                selectObjectRange(entry.objectItem().object());
            } else {
                toggleObjectSelection(entry.objectItem().object());
            }
            event.consume();
        });
        objectList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (synchronizingSidebarSelection || preservingSidebarMultiSelection) {
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
            } else if (!event.isControlDown()
                    && event.getClickCount() == 1
                    && (selectedLogicalObjects().size() > 1
                    || selectedObject == null
                    || !selectedObject.id().equals(selectedEntry.objectItem().object().id()))) {
                selectObject(selectedEntry.objectItem().object());
                event.consume();
            } else if (event.getClickCount() == 2) {
                centerMapOnObject(selectedEntry.objectItem().object());
                event.consume();
            }
        });
        revealObjectButton = new Button("Näita kaardil");
        revealObjectButton.setOnAction(event -> revealSelectedObjectOnMap());
        activeSelectionCountLabel = new Label();
        activeSelectionCountLabel.setStyle("-fx-text-fill: #475569; -fx-font-size: 11;");
        updateActiveSelectionCountLabel();
        updateRevealObjectButton();
        refreshObjectList();
        return new VBox(
                8,
                objectSearchField,
                objectList,
                createObjectListResizeHandle(),
                activeSelectionCountLabel,
                revealObjectButton
        );
    }

    private ObjectListEntry objectListEntryAt(MouseEvent event) {
        Node target = event.getPickResult().getIntersectedNode();
        while (target != null && !(target instanceof ListCell<?>)) {
            target = target.getParent();
        }
        if (target instanceof ListCell<?> cell && cell.getItem() instanceof ObjectListEntry entry) {
            return entry;
        }
        return null;
    }

    private void selectObjectRange(PlannerObject target) {
        if (target == null) {
            return;
        }
        List<PlannerObject> visibleObjects = objectList.getItems().stream()
                .filter(entry -> !entry.isGroup())
                .map(entry -> entry.objectItem().object())
                .toList();
        int targetIndex = indexOfVisibleObject(visibleObjects, target);
        int anchorIndex = indexOfVisibleObjectById(visibleObjects, selectionRangeAnchorObjectId);
        if (targetIndex < 0 || anchorIndex < 0) {
            toggleObjectSelection(target);
            return;
        }
        if (selectedObject != null && !updatingDetailControls) {
            commitPendingDetailFieldsBeforeSelectionChange();
        }
        int from = Math.min(anchorIndex, targetIndex);
        int to = Math.max(anchorIndex, targetIndex);
        for (int index = from; index <= to; index++) {
            selectedObjectIds.addAll(logicalObjectIds(visibleObjects.get(index)));
        }
        selectedObject = target;
        selectionRangeAnchorObjectId = target.id();
        refreshDetails();
        refreshObjectList();
        revealObjectInPowerSummary(target);
        redrawMap();
    }

    private void selectObjectGroup(String groupName) {
        String query = objectSearchField == null ? "" : objectSearchField.getText().trim().toLowerCase();
        List<PlannerObject> groupObjects = plan.objects().stream()
                .filter(this::isObjectAvailableInCurrentView)
                .filter(object -> !(object instanceof FenceRow fenceRow)
                        || plan.isFenceNetworkRepresentative(fenceRow))
                .filter(object -> groupNameForFilter(object).equals(groupName))
                .filter(object -> objectListItemMatches(new ObjectListItem(
                        object,
                        objectTypeName(object),
                        groupNameForFilter(object),
                        "",
                        isObjectVisibleOnMap(object)
                ), query))
                .toList();
        if (groupObjects.isEmpty()) {
            return;
        }
        if (selectedObject != null && !updatingDetailControls) {
            commitPendingDetailFieldsBeforeSelectionChange();
        }
        groupObjects.forEach(object -> selectedObjectIds.addAll(logicalObjectIds(object)));
        selectedObject = groupObjects.getLast();
        selectionRangeAnchorObjectId = selectedObject.id();
        refreshDetails();
        refreshObjectList();
        revealObjectInPowerSummary(selectedObject);
        redrawMap();
    }

    private int indexOfVisibleObject(List<PlannerObject> objects, PlannerObject target) {
        for (int index = 0; index < objects.size(); index++) {
            if (logicalObjectIds(objects.get(index)).contains(target.id())) {
                return index;
            }
        }
        return -1;
    }

    private int indexOfVisibleObjectById(List<PlannerObject> objects, String objectId) {
        if (objectId == null) {
            return -1;
        }
        return plan.findObject(objectId)
                .map(anchor -> indexOfVisibleObject(objects, anchor))
                .orElse(-1);
    }

    private VBox createChecklistPanel() {
        checklistList = new ListView<>();
        checklistList.setMinHeight(120);
        checklistList.setPrefHeight(190);
        checklistList.setPlaceholder(new Label("Checklist on tühi"));
        checklistList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(ChecklistItem item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                setGraphic(null);
                setOnContextMenuRequested(null);
                if (empty || item == null) {
                    return;
                }
                CheckBox completedCheckBox = new CheckBox();
                completedCheckBox.setSelected(item.completed());
                completedCheckBox.setTooltip(new Tooltip(item.completed() ? "Märgi tegemata" : "Märgi tehtuks"));
                Label textLabel = new Label(item.text());
                textLabel.setWrapText(true);
                textLabel.setStyle(item.completed()
                        ? "-fx-text-fill: #6b7280; -fx-strikethrough: true;"
                        : "");
                HBox row = new HBox(8, completedCheckBox, textLabel);
                row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                HBox.setHgrow(textLabel, Priority.ALWAYS);
                setGraphic(row);
                completedCheckBox.setOnAction(event -> {
                    item.setCompleted(completedCheckBox.isSelected());
                    refreshChecklist();
                    markDirty();
                });
                setOnContextMenuRequested(event -> {
                    showChecklistItemContextMenu(item, event.getScreenX(), event.getScreenY());
                    event.consume();
                });
            }
        });

        checklistItemField = new TextField();
        checklistItemField.setPromptText("Uus ülesanne");
        checklistItemField.setOnAction(event -> addChecklistItem());
        Button addButton = new Button("Lisa");
        addButton.setOnAction(event -> addChecklistItem());
        HBox addRow = new HBox(8, checklistItemField, addButton);
        HBox.setHgrow(checklistItemField, Priority.ALWAYS);

        checklistSuggestionList = createChecklistSuggestionList();
        TitledPane suggestionsPane = new TitledPane("Soovitused", checklistSuggestionList);
        suggestionsPane.setExpanded(true);
        refreshChecklist();
        return new VBox(8, new Label("Minu ülesanded"), checklistList, addRow, suggestionsPane);
    }

    private ListView<ChecklistSuggestion> createChecklistSuggestionList() {
        ListView<ChecklistSuggestion> list = new ListView<>();
        list.setMinHeight(170);
        list.setPrefHeight(220);
        list.setCellFactory(ignored -> new ListCell<>() {
            @Override
            protected void updateItem(ChecklistSuggestion suggestion, boolean empty) {
                super.updateItem(suggestion, empty);
                setText(null);
                setGraphic(null);
                setOnContextMenuRequested(null);
                if (empty || suggestion == null) {
                    return;
                }
                ChecklistSuggestionStatus status = plan.checklistSuggestionStatus(suggestion.id());
                CheckBox completedCheckBox = new CheckBox();
                completedCheckBox.setSelected(status == ChecklistSuggestionStatus.COMPLETED);
                completedCheckBox.setTooltip(new Tooltip(status == ChecklistSuggestionStatus.COMPLETED
                        ? "Märgi uuesti ootel"
                        : "Märgi tehtuks"));
                Label textLabel = new Label(suggestion.text());
                Label statusLabel = new Label(status == ChecklistSuggestionStatus.IRRELEVANT
                        ? "Ebaoluline"
                        : status == ChecklistSuggestionStatus.COMPLETED ? "Tehtud" : "");
                statusLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11;");
                if (status == ChecklistSuggestionStatus.COMPLETED) {
                    textLabel.setStyle("-fx-text-fill: #6b7280; -fx-strikethrough: true;");
                } else if (status == ChecklistSuggestionStatus.IRRELEVANT) {
                    textLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-style: italic;");
                }
                VBox labels = new VBox(1, textLabel, statusLabel);
                HBox row = new HBox(8, completedCheckBox, labels);
                row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                setGraphic(row);
                completedCheckBox.setOnAction(event -> setChecklistSuggestionStatus(
                        suggestion,
                        status == ChecklistSuggestionStatus.COMPLETED
                                ? ChecklistSuggestionStatus.PENDING
                                : ChecklistSuggestionStatus.COMPLETED
                ));
                setOnContextMenuRequested(event -> {
                    showChecklistSuggestionContextMenu(suggestion, event.getScreenX(), event.getScreenY());
                    event.consume();
                });
            }
        });
        return list;
    }

    private void showChecklistSuggestionContextMenu(
            ChecklistSuggestion suggestion,
            double screenX,
            double screenY
    ) {
        ChecklistSuggestionStatus status = plan.checklistSuggestionStatus(suggestion.id());
        MenuItem completedItem = new MenuItem(status == ChecklistSuggestionStatus.COMPLETED
                ? "Märgi uuesti ootel"
                : "Märgi tehtuks");
        completedItem.setOnAction(event -> setChecklistSuggestionStatus(
                suggestion,
                status == ChecklistSuggestionStatus.COMPLETED
                        ? ChecklistSuggestionStatus.PENDING
                        : ChecklistSuggestionStatus.COMPLETED
        ));
        MenuItem irrelevantItem = new MenuItem(status == ChecklistSuggestionStatus.IRRELEVANT
                ? "Märgi uuesti ootel"
                : "Märgi ebaoluliseks");
        irrelevantItem.setOnAction(event -> setChecklistSuggestionStatus(
                suggestion,
                status == ChecklistSuggestionStatus.IRRELEVANT
                        ? ChecklistSuggestionStatus.PENDING
                        : ChecklistSuggestionStatus.IRRELEVANT
        ));
        showContextMenu(
                new ContextMenu(completedItem, irrelevantItem),
                checklistSuggestionList,
                screenX,
                screenY
        );
    }

    private void setChecklistSuggestionStatus(
            ChecklistSuggestion suggestion,
            ChecklistSuggestionStatus status
    ) {
        plan.setChecklistSuggestionStatus(suggestion.id(), status);
        refreshChecklist();
        markDirty();
    }

    private void addChecklistItem() {
        String text = checklistItemField == null ? "" : checklistItemField.getText().trim();
        if (text.isBlank()) {
            return;
        }
        plan.addChecklistItem(text);
        checklistItemField.clear();
        refreshChecklist();
        checklistList.scrollTo(checklistList.getItems().size() - 1);
        markDirty();
    }

    private void showChecklistItemContextMenu(ChecklistItem item, double screenX, double screenY) {
        MenuItem renameItem = new MenuItem("Nimeta ümber");
        renameItem.setOnAction(event -> renameChecklistItem(item));
        MenuItem moveUpItem = new MenuItem("Liiguta üles");
        moveUpItem.setDisable(plan.checklistItems().indexOf(item) <= 0);
        moveUpItem.setOnAction(event -> moveChecklistItem(item, -1));
        MenuItem moveDownItem = new MenuItem("Liiguta alla");
        int itemIndex = plan.checklistItems().indexOf(item);
        moveDownItem.setDisable(itemIndex < 0 || itemIndex >= plan.checklistItems().size() - 1);
        moveDownItem.setOnAction(event -> moveChecklistItem(item, 1));
        MenuItem deleteItem = new MenuItem("Kustuta");
        deleteItem.setOnAction(event -> {
            if (plan.removeChecklistItem(item.id())) {
                refreshChecklist();
                markDirty();
            }
        });
        showContextMenu(
                new ContextMenu(renameItem, moveUpItem, moveDownItem, new SeparatorMenuItem(), deleteItem),
                checklistList,
                screenX,
                screenY
        );
    }

    private void renameChecklistItem(ChecklistItem item) {
        TextInputDialog dialog = new TextInputDialog(item.text());
        dialog.initOwner(stage);
        dialog.setTitle("Checklist'i kirje");
        dialog.setHeaderText("Nimeta ülesanne ümber");
        dialog.setContentText("Ülesanne");
        String text = dialog.showAndWait().orElse("").trim();
        if (text.isBlank() || text.equals(item.text())) {
            return;
        }
        item.rename(text);
        refreshChecklist();
        markDirty();
    }

    private void moveChecklistItem(ChecklistItem item, int offset) {
        if (plan.moveChecklistItem(item.id(), offset)) {
            refreshChecklist();
            checklistList.getSelectionModel().select(item);
            checklistList.scrollTo(item);
            markDirty();
        }
    }

    private void refreshChecklist() {
        if (checklistList == null || plan == null) {
            return;
        }
        checklistList.getItems().setAll(plan.checklistItems());
        if (checklistSuggestionList != null) {
            checklistSuggestionList.getItems().setAll(CHECKLIST_SUGGESTIONS);
            checklistSuggestionList.refresh();
        }
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
                showLineObjectsButton,
                showFenceInventoryLabelsButton
        );
        cableLayersPanel = new VBox(8, new Label("Kaablid"), cableVisibilityRow, cableTypeRow);
        return new VBox(
                8,
                bulkActionsRow,
                cableLayersPanel,
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
        showFenceInventoryLabelsButton.setSelected(visible);
        updateMapLayerVisibility();
    }

    private void refreshObjectList() {
        if (objectList == null || plan == null) {
            return;
        }
        String query = objectSearchField == null ? "" : objectSearchField.getText().trim().toLowerCase();
        List<ObjectListItem> unfilteredItems = new ArrayList<>();
        for (PlannerObject object : plan.objects()) {
            if (!isObjectAvailableInCurrentView(object)) {
                continue;
            }
            if (object instanceof FenceRow fenceRow && !plan.isFenceNetworkRepresentative(fenceRow)) {
                continue;
            }
            unfilteredItems.add(new ObjectListItem(
                    object,
                    objectTypeName(object),
                    groupNameForFilter(object),
                    object instanceof FenceRow fenceRow
                            ? fenceNetworkMeasurementText(fenceRow)
                            : objectMeasurementText(object),
                    isObjectVisibleOnMap(object)
            ));
        }
        List<ObjectListItem> objectItems = unfilteredItems.stream()
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

    private String fenceNetworkMeasurementText(FenceRow representative) {
        List<FenceRow> rows = plan.fenceNetworkRows(representative.id());
        int fenceCount = rows.stream().mapToInt(FenceRow::segmentCount).sum();
        double totalLength = rows.stream().mapToDouble(FenceRow::totalLengthMeters).sum();
        int gardenStoneCount = fenceStoneNetworkSummary(representative).totalCount();
        return "%d aeda · %.1f m · %d aiakivi · %d osa".formatted(
                fenceCount, totalLength, gardenStoneCount, rows.size()
        );
    }

    private InventorySummaryService.FenceStoneNetwork fenceStoneNetworkSummary(FenceRow representative) {
        return inventorySummaryService.summarize(plan).fenceStoneNetworks().stream()
                .filter(network -> network.representativeId().equals(representative.id()))
                .findFirst()
                .orElseThrow();
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

    private boolean allGroupObjectsLocked(String groupName) {
        return plan.isGroupLocked(groupName);
    }

    private void setGroupLocked(String groupName, boolean locked) {
        plan.setGroupLocked(groupName, locked);
        redrawMap();
        refreshObjectList();
        refreshDetails();
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
        if (object instanceof PowerSource powerSource) {
            return powerSource.colorHex();
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
        if (object instanceof FenceRow fenceRow) {
            return fenceRow.colorHex();
        }
        if (object instanceof LineObject lineObject) {
            return lineObject.colorHex();
        }
        return "#9ca3af";
    }

    private String objectMeasurementText(PlannerObject object) {
        if (object instanceof FenceRow fenceRow) {
            return "%d aeda · pikkus %.1f m".formatted(
                    fenceRow.segmentCount(),
                    fenceRow.totalLengthMeters()
            );
        }
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
        if (selectedObject instanceof FenceRow fenceRow) {
            plan.fenceNetworkRows(fenceRow.id()).forEach(row -> row.setHidden(false));
        } else {
            selectedObject.setHidden(false);
        }
        setObjectTypeVisible(selectedObject, true);
        String groupName = groupNameForFilter(selectedObject);
        visibleGroups.add(groupName);
        plan.setGroupHidden(groupName, false);
        updateMapLayerVisibility();
        refreshGroupFilters();
        refreshObjectList();
        redrawMap();
        refreshInventory();
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
        setZoom(zoomPercentage * factor);
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

        setZoom(zoomPercentage * factor);
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
        this.zoomPercentage = Math.max(0.25, Math.min(8.0, zoomLevel));
        applyZoomScale();
        if (zoomSlider != null && Math.abs(zoomSlider.getValue() - this.zoomPercentage * 100) > 0.001) {
            zoomSlider.setValue(this.zoomPercentage * 100);
        }
        if (zoomPercentButton != null) {
            zoomPercentButton.setText(zoomPercentText());
        }
        if (mapPane != null && plan != null) {
            zoomRedrawDebounce.playFromStart();
        }
    }

    private void applyZoomScale() {
        this.zoomLevel = fitMapZoomLevel() * zoomPercentage;
        if (mapScale != null) {
            mapScale.setX(this.zoomLevel);
            mapScale.setY(this.zoomLevel);
        }
        updateZoomContentSize();
    }

    private double fitMapZoomLevel() {
        if (mapScrollPane == null || mapWidth <= 0 || mapHeight <= 0) {
            return 1.0;
        }
        Bounds viewport = mapScrollPane.getViewportBounds();
        if (viewport.getWidth() <= 1 || viewport.getHeight() <= 1) {
            return 1.0;
        }
        double availableWidth = Math.max(1, viewport.getWidth() - 20);
        double availableHeight = Math.max(1, viewport.getHeight() - 20);
        return Math.min(availableWidth / mapWidth, availableHeight / mapHeight);
    }

    private String zoomPercentText() {
        return Math.round(zoomPercentage * 100) + "%";
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
        selectedObjectOpacitySlider = createOpacitySlider(100);
        configureOpacityPreview(selectedObjectOpacitySlider);
        generalRotationLabel = new Label("Pööre °");
        generalRotationField = new TextField();
        tentWidthField = new TextField();
        tentHeightField = new TextField();
        tentRotationField = new TextField();
        tentColorPicker = new ColorPicker();
        tentOpacitySlider = createOpacitySlider(Tent.DEFAULT_OPACITY * 100.0);
        configureOpacityPreview(tentOpacitySlider);
        powerSourceColorPicker = new ColorPicker();
        powerSourceSizeSlider = createPixelSlider(8, 100, PowerSource.DEFAULT_SIZE_PIXELS);
        customObjectShapeComboBox = new ComboBox<>();
        customObjectShapeComboBox.getItems().addAll(CustomObjectShape.values());
        customObjectShapeComboBox.setConverter(customObjectShapeConverter());
        customObjectShapeComboBox.getSelectionModel().select(CustomObjectShape.SQUARE);
        customObjectColorPicker = new ColorPicker();
        customObjectOpacitySlider = createOpacitySlider(CustomObject.DEFAULT_OPACITY * 100.0);
        configureOpacityPreview(customObjectOpacitySlider);
        textObjectColorPicker = new ColorPicker();
        textObjectTextOpacitySlider = createOpacitySlider(100);
        configureOpacityPreview(textObjectTextOpacitySlider);
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
        cablePieceEditor = new VBox(4);
        cableNotesField = new TextField();
        cableNotesField.setPromptText("Kaabli märkmed");
        cableNotesField.setOnAction(event -> autoApplyCableNotes());
        cableNotesField.focusedProperty().addListener((observable, wasFocused, isFocused) -> {
            if (wasFocused && !isFocused) {
                autoApplyCableNotes();
            }
        });
        cableOpacitySlider = createOpacitySlider(100);
        configureCableOpacityPreview(cableOpacitySlider);
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
        objectInventoryList = new ListView<>();
        objectInventoryList.setPrefHeight(150);
        objectInventoryList.setCellFactory(list -> createObjectInventoryListCell());
        addObjectInventoryButton = new Button("Lisa inventar");
        addObjectInventoryButton.setOnAction(event -> showObjectInventoryDialog(null));
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
        baseForm.addRow(6, new Label("Läbipaistvus"), opacityControl(selectedObjectOpacitySlider));
        baseForm.addRow(7, generalRotationLabel, generalRotationField);

        GridPane customObjectForm = detailGrid();
        customObjectForm.addRow(0, new Label("Kuju"), customObjectShapeComboBox);
        customObjectForm.addRow(1, new Label("Värv"), customObjectColorPicker);
        customObjectForm.addRow(2, customObjectWidthLabel, customObjectWidthField);
        customObjectForm.addRow(3, customObjectHeightLabel, customObjectHeightField);
        customObjectForm.addRow(4, customObjectRotationLabel, customObjectRotationField);
        customObjectForm.addRow(5, new Label("Pindala"), customObjectAreaLabel);
        customObjectForm.addRow(6, new Label("Ümbermõõt"), customObjectPerimeterLabel);
        customObjectPanel = new VBox(8, sectionLabel("Objekt"), customObjectForm);

        GridPane textObjectForm = detailGrid();
        textObjectForm.addRow(0, new Label("Värv"), textObjectColorPicker);
        textObjectForm.addRow(1, new Label("Suurus"), pixelControl(textObjectFontSizeSlider));
        textObjectForm.addRow(2, new Label("Teksti läbipaistvus"), opacityControl(textObjectTextOpacitySlider));
        textObjectSyncNotesCheckBox = new CheckBox("Uuenda objekti nimest ja märkmetest");
        textObjectSyncNotesCheckBox.setOnAction(event -> updateSelectedTextObjectLinkOptions());
        textObjectReferenceLineCheckBox = new CheckBox("Näita viitavat joont");
        textObjectReferenceLineCheckBox.setOnAction(event -> updateSelectedTextObjectLinkOptions());
        textObjectForm.addRow(3, new Label("Seos"), textObjectSyncNotesCheckBox);
        textObjectForm.addRow(4, new Label("Kaardil"), textObjectReferenceLineCheckBox);
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
        areaForm.addRow(1, new Label("Pindala"), areaSizeLabel);
        areaForm.addRow(2, new Label("Ümbermõõt"), areaPerimeterLabel);
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

        fenceSegmentCountField = new TextField();
        fenceSegmentLengthField = new TextField();
        fenceRotationField = new TextField();
        fenceTotalLengthLabel = new Label("-");
        fenceNetworkSummaryLabel = new Label("-");
        fenceNetworkStoneSummaryLabel = new Label("-");
        decreaseFenceNetworkStonesButton = new Button("−");
        decreaseFenceNetworkStonesButton.setOnAction(event -> adjustSelectedFenceNetworkGardenStones(-1));
        increaseFenceNetworkStonesButton = new Button("+");
        increaseFenceNetworkStonesButton.setOnAction(event -> adjustSelectedFenceNetworkGardenStones(1));
        HBox fenceStoneControl = new HBox(
                6, fenceNetworkStoneSummaryLabel,
                decreaseFenceNetworkStonesButton, increaseFenceNetworkStonesButton
        );
        fenceStoneControl.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        showFenceInventoryLabelCheckBox = new CheckBox("Näita kogusesilti");
        showFenceInventoryLabelCheckBox.setOnAction(event -> updateSelectedFenceInventoryLabelVisibility());
        fenceColorPicker = new ColorPicker();
        fenceColorPicker.setOnAction(event -> autoApplySelectedColor());
        fenceWidthSlider = createPixelSlider(1, 50, FenceRow.DEFAULT_WIDTH_PIXELS);
        configureDetailSliderPreview(fenceWidthSlider);
        resetFenceInventoryLabelButton = new Button("Lähtesta kogusesildi asukoht");
        resetFenceInventoryLabelButton.setOnAction(event -> resetFenceInventoryLabelPosition());
        configureTextCommit(fenceSegmentCountField, this::autoApplyFenceRowGeometry);
        configureTextCommit(fenceSegmentLengthField, this::autoApplyFenceRowGeometry);
        configureTextCommit(fenceRotationField, this::autoApplyFenceRowGeometry);
        GridPane fenceRowForm = detailGrid();
        fenceRowForm.addRow(0, new Label("Kogumik"), fenceNetworkSummaryLabel);
        fenceRowForm.addRow(1, new Label("Aiakivid"), fenceStoneControl);
        fenceRowForm.addRow(2, new Label("Kogusesilt"), showFenceInventoryLabelCheckBox);
        fenceRowForm.addRow(3, new Label("Valitud rea aedu"), fenceSegmentCountField);
        fenceRowForm.addRow(4, new Label("Ühe aia pikkus m"), fenceSegmentLengthField);
        fenceRowForm.addRow(5, new Label("Suund °"), fenceRotationField);
        fenceRowForm.addRow(6, new Label("Rea kogupikkus"), fenceTotalLengthLabel);
        fenceRowForm.addRow(7, new Label("Värv"), fenceColorPicker);
        fenceRowForm.addRow(8, new Label("Paksus"), pixelControl(fenceWidthSlider));
        fenceRowForm.addRow(9, new Label("Sildi asukoht"), resetFenceInventoryLabelButton);
        fenceRowPanel = new VBox(8, sectionLabel("Aiarida"), fenceRowForm);

        GridPane tentForm = detailGrid();
        tentForm.addRow(0, new Label("Laius m"), tentWidthField);
        tentForm.addRow(1, new Label("Pikkus m"), tentHeightField);
        tentForm.addRow(2, new Label("Pööre °"), tentRotationField);
        tentForm.addRow(3, new Label("Värv"), tentColorPicker);
        tentPanel = new VBox(8, sectionLabel("Telk"), tentForm);

        GridPane powerSourceForm = detailGrid();
        powerSourceForm.addRow(0, new Label("Värv"), powerSourceColorPicker);
        powerSourceForm.addRow(1, new Label("Suurus"), pixelControl(powerSourceSizeSlider));
        powerSourceAttachmentsButton = new Button("Vaata lisafaile…");
        powerSourceAttachmentsButton.setOnAction(event -> {
            if (selectedObject instanceof PowerSource source) {
                showTartuCabinetAttachments(source);
            }
        });
        powerSourceForm.add(powerSourceAttachmentsButton, 0, 2, 2, 1);
        powerSourcePanel = new VBox(8, sectionLabel("Elektrikilp"), powerSourceForm);

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
        cableDetailsForm.addRow(0, new Label("Kaabli tükid"), cablePieceEditor);
        cableDetailsForm.addRow(1, new Label("Kaabli märkmed"), cableNotesField);
        cableDetailsForm.addRow(2, new Label("Läbipaistvus"), opacityControl(cableOpacitySlider));
        cableDetailsForm.addRow(3, new Label("Kaablisilt"), showSelectedCableLabelCheckBox);
        cableDetailsForm.addRow(4, new Label("Sildi asukoht"), resetCableLabelButton);
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
        objectInventorySection = collapsibleSection(
                OBJECT_INVENTORY_SECTION,
                "Objekti inventar",
                new VBox(8, objectInventoryList, addObjectInventoryButton),
                false
        );
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
                fenceRowPanel,
                tentPanel,
                powerSourcePanel,
                powerConnectionPanel,
                equipmentSection,
                objectInventorySection,
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

    private void configureCableOpacityPreview(Slider slider) {
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (updatingOpacityControls || selectedPowerConnection() == null) {
                return;
            }
            plan.setCableOpacity(selectedPowerConnection().id(), newValue.doubleValue() / 100.0);
            redrawMap();
            markDirty();
        });
    }

    private boolean previewSelectedObjectOpacity(Slider slider, double percentage) {
        double opacity = percentage / 100.0;
        if (slider == selectedObjectOpacitySlider && selectedObject != null) {
            if (selectedObject instanceof FenceRow fenceRow) {
                plan.fenceNetworkRows(fenceRow.id()).forEach(row -> row.setOpacity(opacity));
            } else {
                selectedObject.setOpacity(opacity);
            }
            return true;
        }
        if (slider == textObjectTextOpacitySlider && selectedObject instanceof TextObject textObject) {
            textObject.setTextOpacity(opacity);
            return true;
        }
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

    private void updateSelectedTextObjectLinkOptions() {
        if (updatingDetailControls || !(selectedObject instanceof TextObject textObject)) {
            return;
        }
        textObject.setSyncSourceNotes(textObjectSyncNotesCheckBox.isSelected());
        textObject.setShowReferenceLine(textObjectReferenceLineCheckBox.isSelected());
        if (textObject.syncSourceNotes()) {
            synchronizeLinkedTextObject(textObject);
        }
        notesArea.setDisable(textObject.syncSourceNotes());
        notesArea.setText(textObject.notes());
        redrawMap();
        markDirty();
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
        configureTextCommit(generalRotationField, this::autoApplyGeneralRotation);
        groupField.setOnAction(event -> autoApplySelectedGroup());
        configureTextCommit(tentWidthField, this::autoApplyTentSize);
        configureTextCommit(tentHeightField, this::autoApplyTentSize);
        configureTextCommit(tentRotationField, this::autoApplyTentRotation);
        configureTextCommit(customObjectWidthField, this::autoApplyCustomObjectSize);
        configureTextCommit(customObjectHeightField, this::autoApplyCustomObjectSize);
        configureTextCommit(customObjectRotationField, this::autoApplyCustomObjectRotation);

        tentColorPicker.setOnAction(event -> autoApplySelectedColor());
        powerSourceColorPicker.setOnAction(event -> autoApplySelectedColor());
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
        configureDetailSliderPreview(powerSourceSizeSlider);
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
        if (slider == fenceWidthSlider && selectedObject instanceof FenceRow fenceRow) {
            plan.fenceNetworkRows(fenceRow.id()).forEach(row -> row.setWidthPixels(value));
            return true;
        }
        if (slider == powerSourceSizeSlider && selectedObject instanceof PowerSource powerSource) {
            powerSource.setSizePixels(value);
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
        if (organizerView && isTechnicalPlacementType(selectedType)) {
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
        pendingPlacementShowFenceInventoryLabel = placementDetails.showFenceInventoryLabel();
        pendingFenceSegmentLengthMeters = placementDetails.fenceSegmentLengthMeters();
        pendingFenceRingRadiusMeters = placementDetails.fenceRadiusMeters();
        preferences.putBoolean(PLACEMENT_SHOW_MAP_LABEL_PREFERENCE, placementDetails.showMapLabel());
        if (selectedType == PlacementType.FENCE_ROW || selectedType == PlacementType.FENCE_RING) {
            preferences.putBoolean(
                    PLACEMENT_SHOW_FENCE_INVENTORY_LABEL_PREFERENCE,
                    placementDetails.showFenceInventoryLabel()
            );
            preferences.putDouble(
                    PLACEMENT_FENCE_SEGMENT_LENGTH_PREFERENCE,
                    placementDetails.fenceSegmentLengthMeters()
            );
        }
        pendingFenceRowPlacement = false;
        pendingFenceRingPlacement = false;
        pendingFenceStartJointId = null;
        pendingFenceTemplateRowId = null;

        switch (selectedType) {
            case TENT, DJ_TRUCK -> addTent(selectedType);
            case POWER_SOURCE, DISTRIBUTION_PANEL -> addPowerSource(selectedType);
            case CUSTOM_OBJECT -> addCustomObject();
            case TEXT_OBJECT -> addTextObject();
            case MARKER_OBJECT -> addMarkerObject();
            case LINE_OBJECT -> addLineObject();
            case FENCE_ROW -> addFenceRow();
            case FENCE_RING -> addFenceRing();
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
            if (organizerView && isTechnicalPlacementType(placementType)) {
                continue;
            }
            MenuItem addItem = new MenuItem(placementType.toString());
            addItem.setOnAction(event -> startPlacementAt(placementType, position));
            addMenu.getItems().add(addItem);
        }
        MenuItem pasteItem = new MenuItem("Kleebi");
        pasteItem.setDisable(!hasCopiedObjects() || mapLayoutLocked);
        pasteItem.setOnAction(event -> pasteCopiedObject(position));
        showContextMenu(new ContextMenu(addMenu, pasteItem), mapPane, screenX, screenY);
    }

    private void startPlacementAt(PlacementType placementType, Position position) {
        if (!startPlacement(placementType)) {
            return;
        }
        switch (placementType) {
            case TENT, DJ_TRUCK -> placeTent(position);
            case POWER_SOURCE, DISTRIBUTION_PANEL -> placePowerSource(position);
            case CUSTOM_OBJECT -> placeCustomObject(position);
            case TEXT_OBJECT -> placeTextObject(position);
            case MARKER_OBJECT -> placeMarkerObject(position);
            case LINE_OBJECT, AREA_OBJECT -> addPendingShapePoint(position);
            case FENCE_ROW -> addPendingFencePoint(position);
            case FENCE_RING -> placeFenceRing(position);
        }
    }

    private boolean isTechnicalPlacementType(PlacementType placementType) {
        return placementType == PlacementType.POWER_SOURCE
                || placementType == PlacementType.DISTRIBUTION_PANEL;
    }

    private PlacementDetails askPlacementDetails(PlacementType placementType) {
        return PlacementDetailsDialog.show(
                stage,
                placementType,
                existingGroupNames(),
                preferences.getBoolean(PLACEMENT_SHOW_MAP_LABEL_PREFERENCE, true),
                preferences.getBoolean(PLACEMENT_SHOW_FENCE_INVENTORY_LABEL_PREFERENCE, true),
                preferences.getDouble(
                        PLACEMENT_FENCE_SEGMENT_LENGTH_PREFERENCE,
                        FenceRow.DEFAULT_SEGMENT_LENGTH_METERS
                ),
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

    private void addTent(PlacementType placementType) {
        pendingTentPlacement = !pendingTentPlacement;
        pendingTentPlacementType = pendingTentPlacement ? placementType : null;
        pendingPowerSourcePlacement = false;
        pendingCustomObjectPlacement = false;
        pendingTextObjectPlacement = false;
        pendingMarkerPlacement = false;
        pendingLineObjectPlacement = false;
        pendingFenceRowPlacement = false;
        pendingFenceStartJointId = null;
        pendingFenceTemplateRowId = null;
        pendingAreaObjectPlacement = false;
        pendingPowerSourceConsumer = null;
        refreshPlacementButtons();
        updateMapToolStatus();
    }

    private void placeTent(Position position) {
        PlacementType placementType = pendingTentPlacementType == PlacementType.DJ_TRUCK
                ? PlacementType.DJ_TRUCK
                : PlacementType.TENT;
        Tent tent = new Tent(planFactory.newId(), placementNameOrDefault(placementType), position);
        tent.setGroupName(placementGroupNameOrDefault());
        tent.setShowMapLabel(pendingPlacementShowMapLabelOrDefault());
        tent.setColorHex(placementColorHexOrDefault(placementType));
        tent.setOpacity(pendingTentOpacityOrDefault());
        tent.setSizeMeters(pendingPlacementWidthMetersOrDefault(), pendingPlacementHeightMetersOrDefault());
        if (placementType == PlacementType.DJ_TRUCK) {
            tent.setPreset(TentPreset.DJ_TRUCK);
            tent.addEquipment(new Equipment("DJ Trucki põhitoide", 1000));
        }
        plan.addObject(tent);
        clearPendingPlacementDetails();
        pendingTentPlacement = false;
        pendingTentPlacementType = null;
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
        pendingFenceRowPlacement = false;
        pendingFenceStartJointId = null;
        pendingFenceTemplateRowId = null;
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

    private void addFenceRow() {
        pendingFenceRowPlacement = true;
        pendingTentPlacement = false;
        pendingPowerSourcePlacement = false;
        pendingCustomObjectPlacement = false;
        pendingTextObjectPlacement = false;
        pendingMarkerPlacement = false;
        pendingLineObjectPlacement = false;
        pendingAreaObjectPlacement = false;
        pendingPowerSourceConsumer = null;
        pendingShapePoints.clear();
        pendingFenceStartJointId = null;
        pendingFenceTemplateRowId = null;
        refreshPlacementButtons();
        updateMapToolStatus();
        redrawMap();
    }

    private void addFenceRing() {
        pendingFenceRingPlacement = true;
        pendingTentPlacement = false;
        pendingPowerSourcePlacement = false;
        pendingCustomObjectPlacement = false;
        pendingTextObjectPlacement = false;
        pendingMarkerPlacement = false;
        pendingLineObjectPlacement = false;
        pendingFenceRowPlacement = false;
        pendingAreaObjectPlacement = false;
        pendingPowerSourceConsumer = null;
        pendingShapePoints.clear();
        pendingFenceStartJointId = null;
        pendingFenceTemplateRowId = null;
        refreshPlacementButtons();
        updateMapToolStatus();
        redrawMap();
    }

    private void placeFenceRing(Position center) {
        FenceRingGenerator.Result generated = FenceRingGenerator.generate(
                center,
                pendingFenceRingRadiusMeters,
                pendingFenceSegmentLengthMetersOrDefault(),
                pixelsPerMeter()
        );
        List<FenceRow> rows = new ArrayList<>();
        String firstStartJointId = null;
        String previousEndJointId = null;
        for (int index = 0; index < generated.fenceCount(); index++) {
            Position start = generated.points().get(index);
            Position end = generated.points().get(index + 1);
            FenceRow row = new FenceRow(
                    planFactory.newId(),
                    placementNameOrDefault(PlacementType.FENCE_RING),
                    start
            );
            row.setGroupName(placementGroupNameOrDefault());
            row.setShowMapLabel(pendingPlacementShowMapLabelOrDefault());
            row.setColorHex(placementColorHexOrDefault(PlacementType.FENCE_RING));
            row.setWidthPixels(pendingLineWidthPixelsOrDefault());
            row.setSegmentCount(1);
            row.setSegmentLengthMeters(pendingFenceSegmentLengthMetersOrDefault());
            row.setShowInventoryLabel(pendingPlacementShowFenceInventoryLabelOrDefault());
            row.setRotationDegrees(Math.toDegrees(Math.atan2(end.y() - start.y(), end.x() - start.x())));
            plan.addObject(row);
            if (firstStartJointId == null) {
                firstStartJointId = row.startJointId();
            }
            String startJointId = previousEndJointId == null ? row.startJointId() : previousEndJointId;
            String endJointId = index == generated.fenceCount() - 1
                    ? firstStartJointId
                    : row.endJointId();
            plan.setFenceRowJoints(row, startJointId, endJointId);
            previousEndJointId = endJointId;
            rows.add(row);
        }
        pendingFenceRingPlacement = false;
        clearPendingPlacementDetails();
        refreshPlacementButtons();
        updateMapToolStatus();
        refreshGroupFilters();
        selectObject(rows.getFirst());
        refreshSummary();
        markDirty();
    }

    private void addPendingFencePoint(Position point) {
        if (pendingShapePoints.isEmpty()) {
            Optional<FenceJoint> snappedJoint = nearestFenceJoint(point, null);
            pendingFenceStartJointId = snappedJoint.map(FenceJoint::id).orElse(null);
            pendingShapePoints.add(snappedJoint.map(FenceJoint::position).orElse(point));
            refreshPlacementButtons();
            updateMapToolStatus();
            redrawMap();
            return;
        }
        Position start = pendingShapePoints.getFirst();
        Optional<FenceJoint> snappedEndJoint = nearestFenceJoint(point, pendingFenceStartJointId);
        Position requestedEnd = snappedEndJoint.map(FenceJoint::position).orElse(point);
        double deltaX = requestedEnd.x() - start.x();
        double deltaY = requestedEnd.y() - start.y();
        double distanceMeters = Math.hypot(deltaX, deltaY) / pixelsPerMeter();
        double fenceSegmentLength = Optional.ofNullable(pendingFenceTemplateRowId)
                .flatMap(plan::findObject)
                .filter(FenceRow.class::isInstance)
                .map(FenceRow.class::cast)
                .map(FenceRow::segmentLengthMeters)
                .orElse(pendingFenceSegmentLengthMetersOrDefault());
        int segmentCount = Math.max(
                1,
                (int) Math.round(distanceMeters / fenceSegmentLength)
        );
        FenceRow fenceRow = new FenceRow(
                planFactory.newId(),
                placementNameOrDefault(PlacementType.FENCE_ROW),
                start
        );
        fenceRow.setGroupName(placementGroupNameOrDefault());
        fenceRow.setShowMapLabel(pendingPlacementShowMapLabelOrDefault());
        fenceRow.setColorHex(placementColorHexOrDefault(PlacementType.FENCE_ROW));
        fenceRow.setWidthPixels(pendingLineWidthPixelsOrDefault());
        fenceRow.setSegmentCount(segmentCount);
        fenceRow.setSegmentLengthMeters(fenceSegmentLength);
        fenceRow.setShowInventoryLabel(pendingPlacementShowFenceInventoryLabelOrDefault());
        if (pendingFenceTemplateRowId != null) {
            plan.findObject(pendingFenceTemplateRowId)
                    .filter(FenceRow.class::isInstance)
                    .map(FenceRow.class::cast)
                    .ifPresent(parent -> {
                        fenceRow.setGroupName(parent.groupName());
                        fenceRow.setShowMapLabel(parent.showMapLabel());
                        fenceRow.setColorHex(parent.colorHex());
                        fenceRow.setWidthPixels(parent.widthPixels());
                        fenceRow.setSegmentLengthMeters(parent.segmentLengthMeters());
                        fenceRow.setShowInventoryLabel(parent.showInventoryLabel());
                    });
        }
        fenceRow.setRotationDegrees(Math.toDegrees(Math.atan2(deltaY, deltaX)));
        plan.addObject(fenceRow);
        String startJointId = pendingFenceStartJointId == null
                ? fenceRow.startJointId()
                : pendingFenceStartJointId;
        String endJointId = snappedEndJoint.map(FenceJoint::id).orElse(fenceRow.endJointId());
        plan.setFenceRowJoints(fenceRow, startJointId, endJointId);
        clearPendingPlacementDetails();
        pendingFenceRowPlacement = false;
        pendingFenceStartJointId = null;
        pendingFenceTemplateRowId = null;
        pendingShapePoints.clear();
        refreshPlacementButtons();
        updateMapToolStatus();
        refreshGroupFilters();
        selectObject(fenceRow);
        refreshSummary();
        markDirty();
    }

    private Optional<FenceJoint> nearestFenceJoint(Position point, String excludedJointId) {
        double snapDistancePixels = 12.0;
        return plan.fenceJoints().stream()
                .filter(joint -> excludedJointId == null || !joint.id().equals(excludedJointId))
                .filter(joint -> Math.hypot(
                        joint.position().x() - point.x(),
                        joint.position().y() - point.y()
                ) <= snapDistancePixels)
                .min(java.util.Comparator.comparingDouble(joint -> Math.hypot(
                        joint.position().x() - point.x(),
                        joint.position().y() - point.y()
                )));
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

    private boolean pendingPlacementShowFenceInventoryLabelOrDefault() {
        return pendingPlacementShowFenceInventoryLabel == null
                || pendingPlacementShowFenceInventoryLabel;
    }

    private double pendingFenceSegmentLengthMetersOrDefault() {
        return pendingFenceSegmentLengthMeters == null
                ? FenceRow.DEFAULT_SEGMENT_LENGTH_METERS
                : pendingFenceSegmentLengthMeters;
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
        pendingPlacementShowFenceInventoryLabel = null;
        pendingFenceSegmentLengthMeters = null;
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
                plan.downloadedMapBounds(),
                null
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
            if (settings.selectedBaseMap() != null) {
                applyDownloadedBaseMap(settings.selectedBaseMap());
            } else if (DEFAULT_MAP_PATH.equals(settings.mapImagePath())
                    || ORTHOPHOTO_MAP_PATH.equals(settings.mapImagePath())) {
                installDefaultApiBaseMaps(ORTHOPHOTO_MAP_PATH.equals(settings.mapImagePath()), true);
            }
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
        pendingFenceRowPlacement = false;
        pendingFenceStartJointId = null;
        pendingFenceTemplateRowId = null;
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
        clearMeasurementPreviewReferences();
        measurementNodes.clear();
        measurements.clear();
        measurementPaths.clear();
        selectedMeasurementPaths.clear();
        activeMeasurementPath = null;
        editingMeasurementPath = null;
        visibleGroups.clear();
        knownGroups.clear();
        collapsedObjectGroups.clear();
        collapsedSummaryKeys.clear();
        fenceInventoryExpanded = false;
        gardenStoneInventoryExpanded = false;
        cableInventoryExpanded = false;
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
        refreshChecklist();
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
        updatePlanHistoryButtons();
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
        updatePlanHistoryButtons();
    }

    private void markClean() {
        PlanSnapshot snapshot = planSnapshotService.create(plan);
        savedPlanSnapshot = snapshot;
        planHistory.replaceCurrent(snapshot);
        planDocumentState.markClean();
        updateWindowTitle();
        updatePlanHistoryButtons();
    }

    private void resetPlanHistory() {
        PlanSnapshot snapshot = planSnapshotService.create(plan);
        planHistory.reset(snapshot);
        savedPlanSnapshot = snapshot;
        planDocumentState.markClean();
        updateWindowTitle();
        updatePlanHistoryButtons();
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
        if (rotatingObjectId != null) {
            mapToolStatusLabel.setText("Lohista pööramispunkti või lõpeta Escape'iga");
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
        if (pendingFenceRowPlacement) {
            mapToolStatusLabel.setText(pendingShapePoints.isEmpty()
                    ? "Märgi aiaraja algus"
                    : "Märgi aiaraja suund ja ligikaudne pikkus");
            return;
        }
        if (pendingFenceRingPlacement) {
            mapToolStatusLabel.setText("Märgi aiaringi keskpunkt");
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
            mapToolStatusLabel.setText(editingMeasurementPath != null
                    ? "Lohista mõõdulindi punkte · lõpeta Enteri, topeltklõpsu või Escape'iga"
                    : activeMeasurementPath == null
                    ? "Märgi mõõdulindi esimene punkt · lõpeta Escape'iga"
                    : "Lisa järgmine punkt · lõpeta lint Enteriga");
            return;
        }
        mapToolStatusLabel.setText(organizerView
                ? "Korraldajavaade"
                : "Vali tööriist või objekt");
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
        refreshBaseMapSwitcher();
        synchronizeLinkedTextObjects();
        synchronizeSelectionState();
        plan.synchronizeFenceRows(pixelsPerMeter());
        mapPane.getChildren().clear();
        mapObjectNodes.clear();
        mapObjectVisualNodes.clear();
        powerConnectionVisualNodes.clear();
        powerConnectionVisuals.clear();
        textReferenceVisuals.clear();
        fenceRowVisuals.clear();
        shapeMidpointHandles.clear();
        fenceSelectionHighlights.clear();
        selectedObjectHighlight = null;
        multiSelectionBounds = null;
        fenceInteractionNodes.clear();
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
            } else if (object instanceof FenceRow fenceRow) {
                drawFenceRow(fenceRow);
            } else if (object instanceof LineObject lineObject) {
                drawLineObject(lineObject);
            } else if (object instanceof CustomObject customObject) {
                drawCustomObject(customObject);
            }
        }
        drawSelectedObjectHighlight();
        drawMultiSelectionBounds();
        addMultiObjectRotationHandleIfActive();
        fenceInteractionNodes.forEach(Node::toFront);
        powerConnectionAnchorMarkers.forEach(Node::toFront);
        drawPendingShapePreview();
        mapPane.getChildren().addAll(measurementNodes);
    }

    private void synchronizeSelectionState() {
        if (selectedObject == null) {
            selectedObjectIds.clear();
            selectionRangeAnchorObjectId = null;
            return;
        }
        Set<String> currentObjectIds = plan.objects().stream()
                .map(PlannerObject::id)
                .collect(java.util.stream.Collectors.toSet());
        selectedObjectIds.retainAll(currentObjectIds);
        if (!selectedObjectIds.contains(selectedObject.id())) {
            selectedObjectIds.clear();
            selectedObjectIds.addAll(logicalObjectIds(selectedObject));
        }
        if (selectionRangeAnchorObjectId == null
                || !selectedObjectIds.contains(selectionRangeAnchorObjectId)) {
            selectionRangeAnchorObjectId = selectedObject.id();
        }
    }

    private void drawSelectedObjectHighlight() {
        if (selectedObject == null || rotatingObjectId != null || !isObjectVisibleOnMap(selectedObject)) {
            return;
        }
        Color highlightColor = Color.web("#7c3aed");
        if (selectedObject instanceof Tent tent) {
            Rectangle outline = new Rectangle(
                    tent.position().x(), tent.position().y(),
                    metersToPixels(tent.widthMeters()), metersToPixels(tent.heightMeters())
            );
            outline.setRotate(tent.rotationDegrees());
            addSelectionOutline(outline, highlightColor);
        } else if (selectedObject instanceof CustomObject object) {
            double width = metersToPixels(object.widthMeters());
            double height = metersToPixels(object.heightMeters());
            javafx.scene.shape.Shape outline = object.shape() == CustomObjectShape.CIRCLE
                    ? new Circle(object.position().x(), object.position().y(), width / 2)
                    : new Rectangle(object.position().x() - width / 2, object.position().y() - height / 2, width, height);
            outline.setRotate(object.rotationDegrees());
            addSelectionOutline(outline, highlightColor);
        } else if (selectedObject instanceof AreaObject object) {
            Polygon outline = new Polygon();
            object.points().forEach(point -> outline.getPoints().addAll(point.x(), point.y()));
            selectedObjectHighlight = outline;
            addSelectionOutline(outline, highlightColor);
        } else if (selectedObject instanceof LineObject object) {
            Polyline outline = CablePolylineHelper.create(object.points());
            outline.setStrokeWidth(2);
            selectedObjectHighlight = outline;
            addSelectionOutline(outline, Color.web(object.colorHex()));
        } else if (selectedObject instanceof FenceRow fenceRow) {
            for (FenceRow row : plan.fenceNetworkRows(fenceRow.id())) {
                Position end = row.endPosition(pixelsPerMeter());
                Line outline = new Line(row.position().x(), row.position().y(), end.x(), end.y());
                outline.setStrokeWidth(2);
                fenceSelectionHighlights.put(row.id(), outline);
                addSelectionOutline(outline, Color.web(row.colorHex()));
            }
        } else if (selectedObject instanceof PowerSource source) {
            double radius = adaptiveMapPixels(source.sizePixels()) / 2;
            addSelectionOutline(new Circle(
                    source.position().x(), source.position().y(), radius + screenPixels(4)
            ), highlightColor);
        } else if (selectedObject instanceof MarkerObject marker) {
            double iconSize = 28;
            double scaledSize = iconSize * adaptiveMapPixels(32) / 32;
            double scaledX = marker.position().x() + (iconSize - scaledSize) / 2;
            double scaledY = marker.position().y() + (iconSize - scaledSize) / 2;
            double padding = screenPixels(2);
            addSelectionOutline(new Rectangle(
                    scaledX - padding,
                    scaledY - padding,
                    scaledSize + padding * 2,
                    scaledSize + padding * 2
            ), highlightColor);
        }
    }

    private void drawMultiSelectionBounds() {
        int selectedObjectCount = selectedLogicalObjects().size();
        if (rotatingObjectId != null
                || (selectedMeasurementPaths.isEmpty() && selectedObjectCount < 2)) {
            return;
        }
        List<Bounds> bounds = new ArrayList<>(selectedObjects().stream()
                .map(object -> mapObjectNodes.get(object.id()))
                .filter(java.util.Objects::nonNull)
                .map(Node::getBoundsInParent)
                .toList());
        selectedMeasurementPaths.stream()
                .flatMap(path -> path.nodes().stream())
                .map(Node::getBoundsInParent)
                .forEach(bounds::add);
        if (bounds.isEmpty()) {
            return;
        }
        double minX = bounds.stream().mapToDouble(Bounds::getMinX).min().orElse(0);
        double maxX = bounds.stream().mapToDouble(Bounds::getMaxX).max().orElse(0);
        double minY = bounds.stream().mapToDouble(Bounds::getMinY).min().orElse(0);
        double maxY = bounds.stream().mapToDouble(Bounds::getMaxY).max().orElse(0);
        double scale = Math.max(zoomLevel, 0.1);
        double padding = 8 / scale;
        Rectangle outline = new Rectangle(
                minX - padding,
                minY - padding,
                Math.max(1, maxX - minX + padding * 2),
                Math.max(1, maxY - minY + padding * 2)
        );
        outline.setFill(Color.TRANSPARENT);
        outline.setStroke(Color.web("#2563eb", 0.72));
        outline.setStrokeWidth(1.25 / scale);
        outline.getStrokeDashArray().addAll(6.0 / scale, 5.0 / scale);
        outline.setMouseTransparent(true);
        mapPane.getChildren().add(outline);
        multiSelectionBounds = outline;
    }

    private void addSelectionOutline(javafx.scene.shape.Shape outline, Color color) {
        selectedObjectHighlight = outline;
        outline.setFill(Color.TRANSPARENT);
        outline.setStroke(color);
        outline.setStrokeWidth(screenPixels(2.5));
        outline.setMouseTransparent(true);
        mapPane.getChildren().add(outline);
    }

    private void drawPendingShapePreview() {
        if ((!isShapePlacementPending() && !pendingFenceRowPlacement) || pendingShapePoints.isEmpty()) {
            return;
        }
        Color color = Color.web(placementColorHexOrDefault(
                pendingAreaObjectPlacement
                        ? PlacementType.AREA_OBJECT
                        : pendingFenceRowPlacement ? PlacementType.FENCE_ROW : PlacementType.LINE_OBJECT
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
        applyZoomScale();
        mapPane.getChildren().add(mapImageView);
    }

    private boolean showCables() {
        return !organizerView && (showCablesButton == null || showCablesButton.isSelected());
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
        if (object instanceof FenceRow) {
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
        double strokeWidth = adaptiveMapPixels(CableDisplayHelper.width(cable.connection().connectorType())
                + (selectedCable ? 2.0 : 0.0));

        Polyline line = CablePolylineHelper.create(path);
        line.setStroke(cableColor);
        line.setStrokeWidth(strokeWidth);
        double cableOpacity = plan.cableOpacity(cable.connection().id());
        line.setOpacity(cableOpacity * (selectedCable ? 1.0 : 0.85));
        line.setMouseTransparent(true);
        if (cable.connection().connectorType() == ConnectorType.SCHUKO_230V) {
            line.getStrokeDashArray().addAll(adaptiveMapPixels(8.0), adaptiveMapPixels(6.0));
        }

        Polyline highlightLine = CablePolylineHelper.create(path);
        highlightLine.setStroke(Color.web("#111827"));
        highlightLine.setStrokeWidth(strokeWidth + screenPixels(4.0));
        highlightLine.setOpacity(selectedCable ? 0.28 : 0);
        highlightLine.setMouseTransparent(true);
        if (cable.connection().connectorType() == ConnectorType.SCHUKO_230V) {
            highlightLine.getStrokeDashArray().addAll(adaptiveMapPixels(8.0), adaptiveMapPixels(6.0));
        }

        Polyline hitLine = CablePolylineHelper.create(path);
        hitLine.setStroke(Color.TRANSPARENT);
        hitLine.setStrokeWidth(Math.max(screenPixels(12.0), strokeWidth + screenPixels(8.0)));
        makeCableSelectable(hitLine, cable);

        mapPane.getChildren().addAll(highlightLine, line, hitLine);
        registerPowerConnectionVisual(cable.connection().id(), highlightLine, line, hitLine);
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
                    Double.toString(adaptiveMapPixels(plan.cableLabelFontSize()))
            ));
            distanceLabel.setLayoutX(labelPosition.x());
            distanceLabel.setLayoutY(labelPosition.y());
            makeCableSelectable(distanceLabel, cable);
            makeCableLabelDraggable(distanceLabel, cable);
            mapPane.getChildren().add(distanceLabel);
            registerPowerConnectionVisual(cable.connection().id(), distanceLabel);
        }
        List<Circle> routePointMarkers = new ArrayList<>();
        Circle anchorMarker = null;
        if (selectedCable && !mapLayoutLocked) {
            for (int index = 0; index < cable.connection().routePoints().size(); index++) {
                Position routePoint = cable.connection().routePoints().get(index);
                Circle marker = new Circle(routePoint.x(), routePoint.y(), screenPixels(5));
                marker.setFill(Color.WHITE);
                marker.setStroke(cableColor);
                marker.setStrokeWidth(screenPixels(2));
                marker.setOpacity(cableOpacity);
                Tooltip.install(marker, new Tooltip("Lohista punkti muutmiseks, paremklõps avab valikud"));
                makeCableSelectable(marker, cable.consumer());
                makeCableRoutePointDraggable(marker, cable, index, line, highlightLine, hitLine, distanceLabel);
                mapPane.getChildren().add(marker);
                registerPowerConnectionVisual(cable.connection().id(), marker);
                routePointMarkers.add(marker);
            }
            if (cable.consumer() instanceof PowerConnectable) {
                Position endpoint = path.getLast();
                anchorMarker = new Circle(endpoint.x(), endpoint.y(), screenPixels(7));
                anchorMarker.setFill(Color.web("#fef3c7"));
                anchorMarker.setStroke(Color.web("#111827"));
                anchorMarker.setStrokeWidth(screenPixels(2));
                anchorMarker.setOpacity(cableOpacity);
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
                registerPowerConnectionVisual(cable.connection().id(), anchorMarker);
            }
        }
        powerConnectionVisuals.put(cable.connection().id(), new PowerConnectionVisual(
                cable, line, highlightLine, hitLine, distanceLabel, routePointMarkers, anchorMarker
        ));
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
        MenuItem noteItem = new MenuItem("Muuda märkust");
        noteItem.setOnAction(event -> showCableNoteDialog(
                cable.connection(), cableInventoryHeader(cable.connection())
        ));
        MenuItem piecesItem = new MenuItem("Muuda kaablitükke");
        piecesItem.setOnAction(event -> showCableLengthNotesDialog(
                cable.connection(), cableInventoryHeader(cable.connection())
        ));
        showContextMenu(
                new ContextMenu(noteItem, piecesItem, new SeparatorMenuItem(), routeItem),
                mapPane,
                screenX,
                screenY
        );
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

    private record PowerConnectionVisual(
            PowerCableView cable,
            Polyline line,
            Polyline highlightLine,
            Polyline hitLine,
            Label distanceLabel,
            List<Circle> routePointMarkers,
            Circle anchorMarker
    ) {
    }

    private record TextReferenceVisual(TextObject textObject, PlannerObject source, Line line, Circle anchor) {
    }

    private record RotationDragState(
            String objectId,
            Position center,
            double pointerStartRotationDegrees,
            double objectStartRotationDegrees,
            List<Position> originalPoints,
            Map<String, Position> originalFenceJoints
    ) {
    }

    private Image loadImage(String imagePath) {
        long revision = plan.mapImageRevision();
        String normalizedPath = imagePath == null ? "" : imagePath;
        if (cachedMapImage != null
                && cachedMapPlan == plan
                && cachedMapRevision == revision
                && cachedMapPath.equals(normalizedPath)) {
            return cachedMapImage;
        }
        Image loadedImage = null;
        if (plan.hasPackagedMapImage()) {
            try (InputStream input = new ByteArrayInputStream(plan.packagedMapImage())) {
                loadedImage = new Image(input);
            } catch (RuntimeException | IOException exception) {
                return null;
            }
        } else if (imagePath == null || imagePath.isBlank()) {
            return null;
        } else try {
            if (imagePath.startsWith("classpath:")) {
                String resourcePath = imagePath.substring("classpath:".length());
                try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
                    loadedImage = inputStream == null ? null : new Image(inputStream);
                }
            } else {
                File imageFile = new File(imagePath);
                loadedImage = imageFile.exists() ? new Image(imageFile.toURI().toString()) : null;
            }
        } catch (RuntimeException | IOException exception) {
            return null;
        }
        cachedMapPlan = plan;
        cachedMapRevision = revision;
        cachedMapPath = normalizedPath;
        cachedMapImage = loadedImage;
        return loadedImage;
    }

    private void drawTent(Tent tent) {
        double widthPixels = metersToPixels(tent.widthMeters());
        double heightPixels = metersToPixels(tent.heightMeters());
        Rectangle rectangle = new Rectangle(
                tent.position().x(),
                tent.position().y(),
                widthPixels,
                heightPixels
        );
        rectangle.setArcWidth(4);
        rectangle.setArcHeight(4);
        boolean djTruck = tent.preset() == TentPreset.DJ_TRUCK;
        rectangle.setFill(Color.web(djTruck ? "#1d4ed8" : tent.colorHex()));
        rectangle.setStroke(Color.web(djTruck ? "#dc2626" : "#222222"));
        rectangle.setStrokeWidth(adaptiveMapPixels(isSelected(tent) ? 4 : 1));
        applyLockedStroke(rectangle, tent);
        if (djTruck) {
            Text label = new Text("DJ");
            label.setFill(Color.web("#dc2626"));
            double labelSize = Math.max(
                    adaptiveMapPixels(MIN_DJ_TRUCK_LABEL_SIZE_PIXELS),
                    metersToPixels(DJ_TRUCK_LABEL_HEIGHT_METERS)
            );
            label.setFont(Font.font("System", FontWeight.BOLD, labelSize));
            Bounds labelBounds = label.getLayoutBounds();
            label.setX(tent.position().x() + (widthPixels - labelBounds.getWidth()) / 2 - labelBounds.getMinX());
            label.setY(tent.position().y() + (heightPixels - labelBounds.getHeight()) / 2 - labelBounds.getMinY());
            label.setMouseTransparent(true);
            Group truck = new Group(rectangle, label);
            truck.setRotate(tent.rotationDegrees());
            truck.setOpacity(tent.opacity());
            makeSelectable(truck, tent);
            makeDraggable(truck, tent);
            mapPane.getChildren().add(truck);
        } else {
            rectangle.setRotate(tent.rotationDegrees());
            rectangle.setOpacity(tent.opacity());
            makeSelectable(rectangle, tent);
            makeDraggable(rectangle, tent);
            mapPane.getChildren().add(rectangle);
        }
        addMapLabel(tent, tent.position().x(), tent.position().y() - adaptiveMapPixels(24));
        addRotationHandleIfActive(
                tent,
                new Position(tent.position().x() + widthPixels / 2, tent.position().y() + heightPixels / 2),
                widthPixels,
                heightPixels,
                tent.rotationDegrees()
        );
    }

    private void drawPowerSource(PowerSource source) {
        double radius = adaptiveMapPixels(source.sizePixels()) / 2;
        Circle circle = new Circle(source.position().x(), source.position().y(), radius);
        circle.setFill(Color.web(source.colorHex()));
        circle.setStroke(Color.web("#111827"));
        circle.setStrokeWidth(adaptiveMapPixels(isSelected(source) ? 4 : 1));
        circle.setOpacity(source.opacity());
        applyLockedStroke(circle, source);
        makeSelectable(circle, source);
        makeDraggable(circle, source);

        mapPane.getChildren().add(circle);
        addRotationHandleIfActive(source, circle);
        addMapLabel(source, source.position().x() + radius + adaptiveMapPixels(4), source.position().y() - radius);
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
        shape.setFill(Color.web(object.colorHex()));
        shape.setOpacity(object.opacity());
        shape.setStroke(Color.web("#111827"));
        shape.setStrokeWidth(adaptiveMapPixels(isSelected(object) ? 4 : 1));
        applyLockedStroke(shape, object);
        makeSelectable(shape, object);
        makeDraggable(shape, object);

        mapPane.getChildren().add(shape);
        addMapLabel(object,
                object.position().x() + adaptiveMapPixels(16),
                object.position().y() - adaptiveMapPixels(12));
        addRotationHandleIfActive(
                object,
                object.position(),
                widthPixels,
                heightPixels,
                object.rotationDegrees()
        );
    }

    private void drawAreaObject(AreaObject object) {
        if (object.points().size() < 3) {
            return;
        }
        Polygon polygon = new Polygon();
        for (Position point : object.points()) {
            polygon.getPoints().addAll(point.x(), point.y());
        }
        polygon.setFill(Color.web(object.colorHex()));
        polygon.setOpacity(object.opacity());
        polygon.setStroke(Color.web(object.colorHex()));
        polygon.setStrokeWidth(adaptiveMapPixels(isSelected(object) ? 4 : 1.5));
        applyLockedStroke(polygon, object);
        makeSelectable(polygon, object);
        makeDraggable(polygon, object);

        mapPane.getChildren().add(polygon);
        addRotationHandleIfActive(object, polygon);
        Position labelPosition = averagePosition(object.points());
        addMapLabel(object, labelPosition.x() + adaptiveMapPixels(8), labelPosition.y() + adaptiveMapPixels(8));
        if (isSelected(object) && rotatingObjectId == null && !mapLayoutLocked) {
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
        polyline.setStrokeWidth(adaptiveMapPixels(
                object.widthPixels() + (isSelected(object) ? 2.0 : 0.0)));
        polyline.setOpacity(object.opacity() * (isSelected(object) ? 1.0 : 0.9));
        applyLockedStroke(polyline, object);
        makeSelectable(polyline, object);
        makeDraggable(polyline, object);

        mapPane.getChildren().add(polyline);
        addRotationHandleIfActive(object, polyline);
        Position labelPosition = averagePosition(object.points());
        addMapLabel(object, labelPosition.x() + adaptiveMapPixels(8), labelPosition.y() + adaptiveMapPixels(8));
        if (isSelected(object) && rotatingObjectId == null && !mapLayoutLocked) {
            addLineMidpointHandles(object, polyline);
            addLinePointHandles(object, polyline);
        }
    }

    private void drawFenceRow(FenceRow fenceRow) {
        Position start = fenceRow.position();
        Position end = fenceRow.endPosition(pixelsPerMeter());
        Line fenceLine = new Line(start.x(), start.y(), end.x(), end.y());
        fenceLine.setStroke(Color.web(fenceRow.colorHex()));
        fenceLine.setStrokeWidth(adaptiveMapPixels(
                fenceRow.widthPixels() + (isSelected(fenceRow) ? 2.0 : 0.0)));
        fenceLine.setOpacity(fenceRow.opacity() * (isSelected(fenceRow) ? 1.0 : 0.9));
        applyLockedStroke(fenceLine, fenceRow);
        makeSelectable(fenceLine, fenceRow);
        makeDraggable(fenceLine, fenceRow);
        markFenceDragNode(fenceLine);
        mapPane.getChildren().add(fenceLine);

        double angle = Math.toRadians(fenceRow.rotationDegrees());
        double perpendicularX = -Math.sin(angle) * adaptiveMapPixels(6);
        double perpendicularY = Math.cos(angle) * adaptiveMapPixels(6);
        double segmentLengthPixels = fenceRow.segmentLengthMeters() * pixelsPerMeter();
        List<Line> dividers = new ArrayList<>();
        List<Circle> splitHandles = new ArrayList<>();
        for (int index = 0; index <= fenceRow.segmentCount(); index++) {
            double x = start.x() + Math.cos(angle) * segmentLengthPixels * index;
            double y = start.y() + Math.sin(angle) * segmentLengthPixels * index;
            Line divider = new Line(
                    x - perpendicularX,
                    y - perpendicularY,
                    x + perpendicularX,
                    y + perpendicularY
            );
            divider.setStroke(Color.web(fenceRow.colorHex()));
            divider.setStrokeWidth(adaptiveMapPixels(2));
            divider.setOpacity(fenceRow.opacity() * (isSelected(fenceRow) ? 1.0 : 0.9));
            divider.setMouseTransparent(true);
            mapPane.getChildren().add(divider);
            registerObjectVisual(fenceRow, divider);
            dividers.add(divider);
            if (index > 0 && index < fenceRow.segmentCount() && isSelected(fenceRow) && !mapLayoutLocked) {
                int segmentIndex = index;
                Circle splitHandle = new Circle(x, y, screenPixels(6), Color.TRANSPARENT);
                splitHandle.setStroke(Color.web(fenceRow.colorHex(), 0.65));
                splitHandle.setStrokeWidth(screenPixels(1.5));
                splitHandle.setOpacity(fenceRow.opacity());
                splitHandle.setCursor(Cursor.HAND);
                markFenceDragNode(splitHandle);
                makeDraggable(splitHandle, fenceRow);
                Tooltip.install(splitHandle, new Tooltip("Paremklõpsuga lisa siia ühenduspunkt"));
                splitHandle.setOnContextMenuRequested(event -> {
                    showFenceSplitContextMenu(
                            fenceRow,
                            segmentIndex,
                            splitHandle,
                            event.getScreenX(),
                            event.getScreenY()
                    );
                    event.consume();
                });
                splitHandles.add(splitHandle);
                mapPane.getChildren().add(splitHandle);
                fenceInteractionNodes.add(splitHandle);
                registerObjectVisual(fenceRow, splitHandle);
            }
        }

        Position center = new Position((start.x() + end.x()) / 2, (start.y() + end.y()) / 2);
        if (plan.isFenceNetworkRepresentative(fenceRow)) {
            addMapLabel(fenceRow,
                    center.x() + adaptiveMapPixels(8),
                    center.y() + adaptiveMapPixels(8));
        }
        Label inventoryLabel = null;
        boolean selectedFenceNetwork = selectedObject instanceof FenceRow selectedFence
                && plan.fenceNetworkRows(selectedFence.id()).stream()
                .anyMatch(row -> row.id().equals(fenceRow.id()));
        if (plan.isFenceNetworkRepresentative(fenceRow)
                && (selectedFenceNetwork
                || plan.showFenceInventoryLabels()
                && plan.showFenceNetworkInventoryLabel(fenceRow.id()))) {
            List<FenceRow> networkRows = plan.fenceNetworkRows(fenceRow.id());
            int fenceCount = networkRows.stream().mapToInt(FenceRow::segmentCount).sum();
            double totalLength = networkRows.stream().mapToDouble(FenceRow::totalLengthMeters).sum();
            int gardenStoneCount = fenceStoneNetworkSummary(fenceRow).totalCount();
            inventoryLabel = new Label("%d aeda · %.1f m · %d aiakivi".formatted(
                    fenceCount, totalLength, gardenStoneCount
            ));
            double defaultLabelX = center.x() + adaptiveMapPixels(8);
            double defaultLabelY = center.y() + adaptiveMapPixels(28);
            inventoryLabel.setLayoutX(defaultLabelX + fenceRow.inventoryLabelOffset().x());
            inventoryLabel.setLayoutY(defaultLabelY + fenceRow.inventoryLabelOffset().y());
            double labelFontSize = adaptiveMapPixels(plan.objectLabelFontSize());
            inventoryLabel.setStyle(("-fx-background-color: rgba(255,255,255,0.88);"
                    + " -fx-padding: %spx %spx %spx %spx; -fx-font-size: %spx;").formatted(
                    adaptiveMapPixels(2), adaptiveMapPixels(4),
                    adaptiveMapPixels(2), adaptiveMapPixels(4), labelFontSize
            ));
            makeFenceInventoryLabelDraggable(inventoryLabel, fenceRow, defaultLabelX, defaultLabelY);
            mapPane.getChildren().add(inventoryLabel);
        }
        fenceRowVisuals.put(fenceRow.id(), new FenceRowVisual(
                fenceLine,
                dividers,
                inventoryLabel,
                null,
                null,
                splitHandles
        ));
        if (isSelected(fenceRow) && rotatingObjectId == null && !mapLayoutLocked) {
            addFenceRowEndpointHandles(fenceRow, fenceLine, dividers, inventoryLabel);
            FenceRowVisual visual = fenceRowVisuals.get(fenceRow.id());
            if (visual != null) {
                registerObjectVisual(fenceRow, visual.startHandle());
                registerObjectVisual(fenceRow, visual.endHandle());
            }
        }
        addFenceRotationHandleIfActive(fenceRow);
    }

    private void addFenceRotationHandleIfActive(FenceRow fenceRow) {
        if (!isObjectRotationActive(fenceRow)) {
            return;
        }
        List<Position> jointPositions = rotationFenceJoints(fenceRow).values().stream().toList();
        if (jointPositions.isEmpty()) {
            return;
        }
        double minX = jointPositions.stream().mapToDouble(Position::x).min().orElse(fenceRow.position().x());
        double maxX = jointPositions.stream().mapToDouble(Position::x).max().orElse(fenceRow.position().x());
        double minY = jointPositions.stream().mapToDouble(Position::y).min().orElse(fenceRow.position().y());
        double maxY = jointPositions.stream().mapToDouble(Position::y).max().orElse(fenceRow.position().y());
        addRotationHandleIfActive(
                fenceRow,
                new Position((minX + maxX) / 2, (minY + maxY) / 2),
                Math.max(16, maxX - minX),
                Math.max(16, maxY - minY),
                fenceRow.rotationDegrees()
        );
    }

    private void markFenceDragNode(Node node) {
        node.getProperties().put(FENCE_DRAG_NODE_KEY, Boolean.TRUE);
    }

    private boolean isFenceDragTarget(Object target) {
        Node node = target instanceof Node targetNode ? targetNode : null;
        while (node != null) {
            if (Boolean.TRUE.equals(node.getProperties().get(FENCE_DRAG_NODE_KEY))) {
                return true;
            }
            node = node.getParent();
        }
        return false;
    }

    private void addFenceRowEndpointHandles(
            FenceRow fenceRow,
            Line fenceLine,
            List<Line> dividers,
            Label inventoryLabel
    ) {
        Position start = fenceRow.position();
        Position end = fenceRow.endPosition(pixelsPerMeter());
        Circle startHandle = shapePointHandle(start, Color.web(fenceRow.colorHex()));
        Circle endHandle = shapePointHandle(end, Color.web(fenceRow.colorHex()));
        startHandle.setCursor(Cursor.HAND);
        endHandle.setCursor(Cursor.HAND);
        markFenceDragNode(startHandle);
        markFenceDragNode(endHandle);
        Tooltip.install(startHandle, new Tooltip(fenceEndpointTooltip(fenceRow, true)));
        Tooltip.install(endHandle, new Tooltip(fenceEndpointTooltip(fenceRow, false)));
        makeFenceRowStartDraggable(startHandle, endHandle, fenceRow, fenceLine, dividers, inventoryLabel);
        makeFenceRowEndDraggable(startHandle, endHandle, fenceRow, fenceLine, dividers, inventoryLabel);
        startHandle.setOpacity(fenceRow.opacity() * (plan.fenceJointDegree(fenceRow.startJointId()) > 1 ? 0.55 : 1.0));
        endHandle.setOpacity(fenceRow.opacity() * (plan.fenceJointDegree(fenceRow.endJointId()) > 1 ? 0.55 : 1.0));
        startHandle.setOnContextMenuRequested(event -> {
            showFenceEndpointContextMenu(fenceRow, true, startHandle, event.getScreenX(), event.getScreenY());
            event.consume();
        });
        endHandle.setOnContextMenuRequested(event -> {
            showFenceEndpointContextMenu(fenceRow, false, endHandle, event.getScreenX(), event.getScreenY());
            event.consume();
        });
        fenceRowVisuals.put(fenceRow.id(), new FenceRowVisual(
                fenceLine,
                dividers,
                inventoryLabel,
                startHandle,
                endHandle,
                fenceRowVisuals.get(fenceRow.id()).splitHandles()
        ));
        mapPane.getChildren().addAll(startHandle, endHandle);
        fenceInteractionNodes.add(startHandle);
        fenceInteractionNodes.add(endHandle);
    }

    private String fenceEndpointTooltip(FenceRow fenceRow, boolean startEndpoint) {
        String jointId = startEndpoint ? fenceRow.startJointId() : fenceRow.endJointId();
        if (plan.fenceJointDegree(jointId) > 1) {
            return "Lohista tervet aiarida; Shift+lohistamine muudab jagatud ühenduspunkti";
        }
        return "Lohista tervet aiarida; Shift+lohistamine muudab otspunkti";
    }

    private void showFenceEndpointContextMenu(
            FenceRow fenceRow,
            boolean startEndpoint,
            Node owner,
            double screenX,
            double screenY
    ) {
        MenuItem continueItem = new MenuItem("Jätka aiarida siit");
        continueItem.setDisable(mapLayoutLocked || isObjectEffectivelyLocked(fenceRow));
        continueItem.setOnAction(event -> startConnectedFenceRow(fenceRow, startEndpoint));
        String jointId = startEndpoint ? fenceRow.startJointId() : fenceRow.endJointId();
        MenuItem disconnectItem = new MenuItem("Ühenda see otspunkt lahti");
        disconnectItem.setDisable(
                mapLayoutLocked || isObjectEffectivelyLocked(fenceRow) || plan.fenceJointDegree(jointId) <= 1
        );
        disconnectItem.setOnAction(event -> {
            plan.disconnectFenceEndpoint(fenceRow, startEndpoint);
            refreshEditedShapeObject();
        });
        MenuItem removeJointItem = new MenuItem("Eemalda ühenduspunkt");
        removeJointItem.setDisable(
                mapLayoutLocked || isObjectEffectivelyLocked(fenceRow) || !plan.canRemoveFenceJoint(jointId)
        );
        removeJointItem.setOnAction(event -> {
            FenceRow joinedRow = plan.removeFenceJoint(jointId);
            selectedObject = joinedRow;
            refreshEditedShapeObject();
        });
        showContextMenu(
                new ContextMenu(continueItem, disconnectItem, removeJointItem),
                owner,
                screenX,
                screenY
        );
    }

    private void showFenceSplitContextMenu(
            FenceRow fenceRow,
            int segmentIndex,
            Node owner,
            double screenX,
            double screenY
    ) {
        MenuItem addJointItem = new MenuItem("Lisa ühenduspunkt");
        addJointItem.setDisable(mapLayoutLocked || isObjectEffectivelyLocked(fenceRow));
        addJointItem.setOnAction(event -> {
            plan.splitFenceRow(fenceRow, segmentIndex, planFactory.newId());
            refreshEditedShapeObject();
        });
        showContextMenu(new ContextMenu(addJointItem), owner, screenX, screenY);
    }

    private void startConnectedFenceRow(FenceRow template, boolean startEndpoint) {
        pendingFenceRowPlacement = true;
        pendingFenceTemplateRowId = template.id();
        pendingFenceStartJointId = startEndpoint ? template.startJointId() : template.endJointId();
        pendingShapePoints.clear();
        plan.findFenceJoint(pendingFenceStartJointId)
                .map(FenceJoint::position)
                .ifPresent(pendingShapePoints::add);
        refreshPlacementButtons();
        updateMapToolStatus();
        redrawMap();
    }

    private void makeFenceRowEndDraggable(
            Circle startHandle,
            Circle endHandle,
            FenceRow fenceRow,
            Line fenceLine,
            List<Line> dividers,
            Label inventoryLabel
    ) {
        final boolean[] dragged = {false};
        final boolean[] editingEndpoint = {false};
        final Delta dragStartScene = new Delta();
        final Position[] dragStartPosition = {null};
        endHandle.setOnMousePressed(event -> {
            dragged[0] = false;
            editingEndpoint[0] = event.isShiftDown();
            dragStartScene.x = event.getSceneX();
            dragStartScene.y = event.getSceneY();
            dragStartPosition[0] = plan.findFenceJoint(fenceRow.endJointId())
                    .map(FenceJoint::position)
                    .orElse(fenceRow.endPosition(pixelsPerMeter()));
            selectFenceRowForDrag(fenceRow);
            beginPlanDrag();
            mapScrollPane.setPannable(false);
            event.consume();
        });
        endHandle.setOnMouseDragged(event -> {
            if (measuringActive || addingCablePoint || mapLayoutLocked || isObjectEffectivelyLocked(fenceRow)) {
                event.consume();
                return;
            }
            boolean moved;
            if (editingEndpoint[0]) {
                Position target = new Position(
                        dragStartPosition[0].x() + (event.getSceneX() - dragStartScene.x) / zoomLevel,
                        dragStartPosition[0].y() + (event.getSceneY() - dragStartScene.y) / zoomLevel
                );
                moved = plan.moveFenceEndpoint(fenceRow, false, target);
            } else {
                moved = plan.translateFenceNetwork(
                        fenceRow.id(),
                        (event.getSceneX() - dragStartScene.x) / zoomLevel,
                        (event.getSceneY() - dragStartScene.y) / zoomLevel
                );
                dragStartScene.x = event.getSceneX();
                dragStartScene.y = event.getSceneY();
            }
            if (moved) {
                updateFenceNetworkDragPreview(fenceRow);
                dragged[0] = true;
                recordPlanDragChange();
            }
            event.consume();
        });
        endHandle.setOnMouseReleased(event -> {
            mapScrollPane.setPannable(true);
            if (dragged[0]) {
                redrawMap();
                refreshSummary();
                refreshDetails();
            }
            event.consume();
        });
        endHandle.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && pendingFenceRowPlacement) {
                plan.findFenceJoint(fenceRow.endJointId())
                        .map(FenceJoint::position)
                        .ifPresent(this::addPendingFencePoint);
            }
            event.consume();
        });
    }

    private void makeFenceRowStartDraggable(
            Circle startHandle,
            Circle endHandle,
            FenceRow fenceRow,
            Line fenceLine,
            List<Line> dividers,
            Label inventoryLabel
    ) {
        final boolean[] dragged = {false};
        final boolean[] editingEndpoint = {false};
        final Delta dragStartScene = new Delta();
        final Position[] dragStartPosition = {null};
        startHandle.setOnMousePressed(event -> {
            dragged[0] = false;
            editingEndpoint[0] = event.isShiftDown();
            dragStartScene.x = event.getSceneX();
            dragStartScene.y = event.getSceneY();
            dragStartPosition[0] = plan.findFenceJoint(fenceRow.startJointId())
                    .map(FenceJoint::position)
                    .orElse(fenceRow.position());
            selectFenceRowForDrag(fenceRow);
            beginPlanDrag();
            mapScrollPane.setPannable(false);
            event.consume();
        });
        startHandle.setOnMouseDragged(event -> {
            if (measuringActive || addingCablePoint || mapLayoutLocked || isObjectEffectivelyLocked(fenceRow)) {
                event.consume();
                return;
            }
            boolean moved;
            if (editingEndpoint[0]) {
                Position target = new Position(
                        dragStartPosition[0].x() + (event.getSceneX() - dragStartScene.x) / zoomLevel,
                        dragStartPosition[0].y() + (event.getSceneY() - dragStartScene.y) / zoomLevel
                );
                moved = plan.moveFenceEndpoint(fenceRow, true, target);
            } else {
                moved = plan.translateFenceNetwork(
                        fenceRow.id(),
                        (event.getSceneX() - dragStartScene.x) / zoomLevel,
                        (event.getSceneY() - dragStartScene.y) / zoomLevel
                );
                dragStartScene.x = event.getSceneX();
                dragStartScene.y = event.getSceneY();
            }
            if (moved) {
                updateFenceNetworkDragPreview(fenceRow);
                dragged[0] = true;
                recordPlanDragChange();
            }
            event.consume();
        });
        startHandle.setOnMouseReleased(event -> {
            mapScrollPane.setPannable(true);
            if (dragged[0]) {
                redrawMap();
                refreshSummary();
                refreshDetails();
            }
            event.consume();
        });
        startHandle.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && pendingFenceRowPlacement) {
                plan.findFenceJoint(fenceRow.startJointId())
                        .map(FenceJoint::position)
                        .ifPresent(this::addPendingFencePoint);
            }
            event.consume();
        });
    }

    private void updateFenceNetworkDragPreview(FenceRow selectedFenceRow) {
        fenceRowVisuals.forEach((rowId, visual) -> plan.findObject(rowId)
                .filter(FenceRow.class::isInstance)
                .map(FenceRow.class::cast)
                .ifPresent(row -> updateFenceRowVisual(row, visual)));
        fenceRotationField.setText(formatDegrees(selectedFenceRow.rotationDegrees()));
    }

    private void updateFenceRowVisual(FenceRow fenceRow, FenceRowVisual visual) {
        Position start = fenceRow.position();
        Position end = fenceRow.endPosition(pixelsPerMeter());
        visual.fenceLine().setStartX(start.x());
        visual.fenceLine().setStartY(start.y());
        visual.fenceLine().setEndX(end.x());
        visual.fenceLine().setEndY(end.y());
        Line selectionHighlight = fenceSelectionHighlights.get(fenceRow.id());
        if (selectionHighlight != null) {
            selectionHighlight.setStartX(start.x());
            selectionHighlight.setStartY(start.y());
            selectionHighlight.setEndX(end.x());
            selectionHighlight.setEndY(end.y());
        }
        if (visual.startHandle() != null) {
            visual.startHandle().setCenterX(start.x());
            visual.startHandle().setCenterY(start.y());
        }
        if (visual.endHandle() != null) {
            visual.endHandle().setCenterX(end.x());
            visual.endHandle().setCenterY(end.y());
        }
        double angle = Math.toRadians(fenceRow.rotationDegrees());
        double perpendicularX = -Math.sin(angle) * adaptiveMapPixels(6);
        double perpendicularY = Math.cos(angle) * adaptiveMapPixels(6);
        double segmentLengthPixels = fenceRow.segmentLengthMeters() * pixelsPerMeter();
        for (int index = 0; index < visual.dividers().size(); index++) {
            double x = start.x() + Math.cos(angle) * segmentLengthPixels * index;
            double y = start.y() + Math.sin(angle) * segmentLengthPixels * index;
            Line divider = visual.dividers().get(index);
            divider.setStartX(x - perpendicularX);
            divider.setStartY(y - perpendicularY);
            divider.setEndX(x + perpendicularX);
            divider.setEndY(y + perpendicularY);
            int splitHandleIndex = index - 1;
            if (splitHandleIndex >= 0 && splitHandleIndex < visual.splitHandles().size()) {
                visual.splitHandles().get(splitHandleIndex).setCenterX(x);
                visual.splitHandles().get(splitHandleIndex).setCenterY(y);
            }
        }
        if (visual.inventoryLabel() != null) {
            visual.inventoryLabel().setLayoutX(
                    (start.x() + end.x()) / 2 + adaptiveMapPixels(8) + fenceRow.inventoryLabelOffset().x()
            );
            visual.inventoryLabel().setLayoutY(
                    (start.y() + end.y()) / 2 + adaptiveMapPixels(28) + fenceRow.inventoryLabelOffset().y()
            );
        }
    }

    private void addAreaMidpointHandles(AreaObject object, Polygon polygon) {
        List<Circle> handles = new ArrayList<>();
        for (int index = 0; index < object.points().size(); index++) {
            Position start = object.points().get(index);
            Position end = object.points().get((index + 1) % object.points().size());
            Circle marker = shapeMidpointHandle(midpoint(start, end), Color.web(object.colorHex()));
            Tooltip.install(marker, new Tooltip("Lohista siit uue ala punkti lisamiseks"));
            makeAreaMidpointDraggable(marker, object, index + 1, polygon);
            mapPane.getChildren().add(marker);
            registerObjectVisual(object, marker);
            handles.add(marker);
        }
        shapeMidpointHandles.put(object.id(), handles);
    }

    private void addLineMidpointHandles(LineObject object, Polyline polyline) {
        List<Circle> handles = new ArrayList<>();
        for (int index = 0; index < object.points().size() - 1; index++) {
            Position start = object.points().get(index);
            Position end = object.points().get(index + 1);
            Circle marker = shapeMidpointHandle(midpoint(start, end), Color.web(object.colorHex()));
            Tooltip.install(marker, new Tooltip("Lohista siit uue joone punkti lisamiseks"));
            makeLineMidpointDraggable(marker, object, index + 1, polyline);
            mapPane.getChildren().add(marker);
            registerObjectVisual(object, marker);
            handles.add(marker);
        }
        shapeMidpointHandles.put(object.id(), handles);
    }

    private void addAreaPointHandles(AreaObject object, Polygon polygon) {
        for (int index = 0; index < object.points().size(); index++) {
            Position point = object.points().get(index);
            Circle marker = shapePointHandle(point, Color.web(object.colorHex()));
            Tooltip.install(marker, new Tooltip("Lohista ala punkti muutmiseks"));
            makeAreaPointDraggable(marker, object, index, polygon);
            mapPane.getChildren().add(marker);
            registerObjectVisual(object, marker);
        }
    }

    private void addLinePointHandles(LineObject object, Polyline polyline) {
        for (int index = 0; index < object.points().size(); index++) {
            Position point = object.points().get(index);
            Circle marker = shapePointHandle(point, Color.web(object.colorHex()));
            Tooltip.install(marker, new Tooltip("Lohista joone punkti muutmiseks"));
            makeLinePointDraggable(marker, object, index, polyline);
            mapPane.getChildren().add(marker);
            registerObjectVisual(object, marker);
        }
    }

    private Circle shapePointHandle(Position point, Color color) {
        Circle marker = new Circle(point.x(), point.y(), screenPixels(6));
        marker.setFill(Color.WHITE);
        marker.setStroke(color);
        marker.setStrokeWidth(screenPixels(2.5));
        return marker;
    }

    private Circle shapeMidpointHandle(Position point, Color color) {
        Circle marker = new Circle(point.x(), point.y(), screenPixels(5));
        marker.setFill(Color.web(toHex(color), 0.45));
        marker.setStroke(color);
        marker.setStrokeWidth(screenPixels(1.5));
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
            if (measuringActive || addingCablePoint || mapLayoutLocked || isObjectEffectivelyLocked(object)) {
                event.consume();
                return;
            }
            Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
            Position updatedPoint = new Position(mapPoint.getX(), mapPoint.getY());
            object.setPoints(replacePoint(object.points(), pointIndex, updatedPoint));
            marker.setCenterX(updatedPoint.x());
            marker.setCenterY(updatedPoint.y());
            updatePolygonPoint(polygon, pointIndex, updatedPoint);
            updateShapeEditDecorations(object, true);
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
            if (measuringActive || addingCablePoint || mapLayoutLocked || isObjectEffectivelyLocked(object)) {
                event.consume();
                return;
            }
            inserted[0] = false;
            dragged[0] = false;
            event.consume();
        });
        marker.setOnMouseDragged(event -> {
            if (measuringActive || addingCablePoint || mapLayoutLocked || isObjectEffectivelyLocked(object)) {
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
            updateShapeHighlight(object);
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
            if (measuringActive || addingCablePoint || mapLayoutLocked || isObjectEffectivelyLocked(object)) {
                event.consume();
                return;
            }
            Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
            Position updatedPoint = new Position(mapPoint.getX(), mapPoint.getY());
            object.setPoints(replacePoint(object.points(), pointIndex, updatedPoint));
            marker.setCenterX(updatedPoint.x());
            marker.setCenterY(updatedPoint.y());
            updatePolylinePoint(polyline, pointIndex, updatedPoint);
            updateShapeEditDecorations(object, false);
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
            if (measuringActive || addingCablePoint || mapLayoutLocked || isObjectEffectivelyLocked(object)) {
                event.consume();
                return;
            }
            inserted[0] = false;
            dragged[0] = false;
            event.consume();
        });
        marker.setOnMouseDragged(event -> {
            if (measuringActive || addingCablePoint || mapLayoutLocked || isObjectEffectivelyLocked(object)) {
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
            updateShapeHighlight(object);
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

    private void updateShapeEditDecorations(PlannerObject object, boolean closedShape) {
        updateShapeHighlight(object);
        List<Position> points = object instanceof AreaObject areaObject
                ? areaObject.points()
                : object instanceof LineObject lineObject ? lineObject.points() : List.of();
        List<Circle> handles = shapeMidpointHandles.getOrDefault(object.id(), List.of());
        int segmentCount = closedShape ? points.size() : Math.max(0, points.size() - 1);
        for (int index = 0; index < Math.min(segmentCount, handles.size()); index++) {
            Position start = points.get(index);
            Position end = points.get(closedShape ? (index + 1) % points.size() : index + 1);
            Position center = midpoint(start, end);
            handles.get(index).setCenterX(center.x());
            handles.get(index).setCenterY(center.y());
        }
    }

    private void updateShapeHighlight(PlannerObject object) {
        List<Position> points = object instanceof AreaObject areaObject
                ? areaObject.points()
                : object instanceof LineObject lineObject ? lineObject.points() : List.of();
        if (selectedObjectHighlight instanceof Polygon polygon && object instanceof AreaObject) {
            replaceShapePoints(polygon.getPoints(), points);
        } else if (selectedObjectHighlight instanceof Polyline polyline && object instanceof LineObject) {
            replaceShapePoints(polyline.getPoints(), points);
        }
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
        removePointItem.setDisable(mapLayoutLocked || isObjectEffectivelyLocked(object) || object.points().size() <= 3);

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
        removePointItem.setDisable(mapLayoutLocked || isObjectEffectivelyLocked(object) || object.points().size() <= 2);

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
        double fontSize = adaptiveMapPixels(object.fontSize());
        double boxScale = fontSize / object.fontSize();
        Line referenceLine = null;
        PlannerObject referenceSource = null;
        if (object.showReferenceLine()) {
            referenceSource = plan.findObject(object.sourceObjectId()).orElse(null);
            if (referenceSource != null) {
                Position offset = object.referenceLineSourceOffset();
                referenceLine = new Line(
                        referenceSource.position().x() + offset.x(),
                        referenceSource.position().y() + offset.y(),
                        object.position().x(),
                        object.position().y()
                );
                referenceLine.setStroke(Color.web(object.colorHex(), 0.7));
                referenceLine.setStrokeWidth(screenPixels(1.5));
                referenceLine.getStrokeDashArray().addAll(
                        screenPixels(6.0),
                        screenPixels(4.0)
                );
                referenceLine.setMouseTransparent(true);
                mapPane.getChildren().add(referenceLine);
                textReferenceVisuals.put(object.id(), new TextReferenceVisual(object, referenceSource, referenceLine, null));
            }
        }
        VBox textBox = new VBox(3 * boxScale);
        textBox.setLayoutX(object.position().x());
        textBox.setLayoutY(object.position().y());
        textBox.setMaxWidth(260 * boxScale);
        textBox.setStyle("""
                -fx-background-color: rgba(255,255,255,%s);
                -fx-border-color: %s;
                -fx-border-width: %s;
                -fx-background-radius: 4;
                -fx-border-radius: 4;
                -fx-padding: %spx %spx %spx %spx;
                """.formatted(
                object.opacity(),
                isSelected(object) ? object.colorHex() : cssRgba(object.colorHex(), object.opacity()),
                adaptiveMapPixels(isSelected(object) ? 2 : 1),
                4 * boxScale, 7 * boxScale, 5 * boxScale, 7 * boxScale
        ));

        Label titleLabel = new Label(object.name());
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(246 * boxScale);
        titleLabel.setTextFill(Color.web(object.colorHex()));
        titleLabel.setOpacity(object.textOpacity());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: %spx;"
                .formatted(Double.toString(fontSize)));
        textBox.getChildren().add(titleLabel);

        if (!object.notes().isBlank()) {
            Label contentLabel = new Label(object.notes());
            contentLabel.setWrapText(true);
            contentLabel.setMaxWidth(246 * boxScale);
            contentLabel.setTextFill(Color.web("#111827"));
            contentLabel.setOpacity(object.textOpacity());
            contentLabel.setStyle("-fx-font-size: %spx;"
                    .formatted(Double.toString(fontSize)));
            textBox.getChildren().add(contentLabel);
        }

        makeSelectable(textBox, object);
        makeDraggable(textBox, object);
        mapPane.getChildren().add(textBox);
        if (referenceLine != null && referenceSource != null && isSelected(object)) {
            addTextReferenceLineAnchor(object, referenceSource, referenceLine);
        }
        textBox.applyCss();
        double textBoxWidth = Math.min(260 * boxScale, Math.max(1, textBox.prefWidth(-1)));
        double textBoxHeight = Math.max(1, textBox.prefHeight(textBoxWidth));
        textBox.resize(textBoxWidth, textBoxHeight);
        textBox.setRotate(object.rotationDegrees());
        addRotationHandleIfActive(
                object,
                new Position(
                        object.position().x() + textBoxWidth / 2,
                        object.position().y() + textBoxHeight / 2
                ),
                textBoxWidth,
                textBoxHeight,
                object.rotationDegrees()
        );
    }

    private void addTextReferenceLineAnchor(TextObject textObject, PlannerObject source, Line referenceLine) {
        double scale = Math.max(zoomLevel, 0.1);
        Circle anchor = new Circle(referenceLine.getStartX(), referenceLine.getStartY(), 6 / scale);
        anchor.setFill(Color.WHITE);
        anchor.setStroke(Color.web("#2563eb"));
        anchor.setStrokeWidth(2 / scale);
        anchor.setCursor(Cursor.HAND);
        anchor.setOnMousePressed(event -> {
            mapScrollPane.setPannable(false);
            event.consume();
        });
        anchor.setOnMouseDragged(event -> {
            Point2D point = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
            textObject.setReferenceLineSourceOffset(new Position(
                    point.getX() - source.position().x(),
                    point.getY() - source.position().y()
            ));
            referenceLine.setStartX(point.getX());
            referenceLine.setStartY(point.getY());
            anchor.setCenterX(point.getX());
            anchor.setCenterY(point.getY());
            event.consume();
        });
        anchor.setOnMouseReleased(event -> {
            mapScrollPane.setPannable(true);
            markDirty();
            event.consume();
        });
        mapPane.getChildren().add(anchor);
        textReferenceVisuals.put(textObject.id(), new TextReferenceVisual(textObject, source, referenceLine, anchor));
        anchor.toFront();
    }

    private void synchronizeLinkedTextObjects() {
        for (PlannerObject object : plan.objects()) {
            if (!(object instanceof TextObject textObject) || !textObject.syncSourceNotes()) {
                continue;
            }
            synchronizeLinkedTextObject(textObject);
        }
    }

    private void synchronizeLinkedTextObject(TextObject textObject) {
        if (textObject.sourceType() == TextObjectSourceType.INVENTORY_SUMMARY) {
            synchronizeInventorySummaryTextObject(textObject);
            return;
        }
        plan.findObject(textObject.sourceObjectId())
                .filter(source -> source != textObject)
                .ifPresent(source -> {
                    switch (textObject.sourceType()) {
                        case INVENTORY -> {
                            if (source instanceof InventoryContainer container) {
                                textObject.rename(source.name() + " — inventar");
                                textObject.setNotes(objectInventoryText(container));
                            }
                        }
                        case INVENTORY_SUMMARY -> {
                            // Koondinventari seost käsitletakse enne kaardiobjekti otsimist.
                        }
                        case POWER_OUTLETS -> {
                            if (source instanceof PowerSource powerSource) {
                                textObject.rename(source.name() + " — väljundid");
                                textObject.setNotes(powerOutletsText(powerSource));
                            }
                        }
                        case NOTES -> {
                            textObject.rename(source.name());
                            textObject.setNotes(source.notes());
                        }
                        case NONE -> {
                            // Staatilisel tekstil pole sünkroonitavat allikat.
                        }
                    }
                });
    }

    private void drawMarkerObject(MarkerObject object) {
        Pane markerIcon = MarkerIconFactory.create(object.markerType());
        markerIcon.setLayoutX(object.position().x());
        markerIcon.setLayoutY(object.position().y());
        markerIcon.setOpacity(object.opacity());
        markerIcon.setStyle("-fx-background-color: %s; -fx-background-radius: 6; -fx-border-radius: 6;%s".formatted(
                object.colorHex(),
                isSelected(object) ? " -fx-border-color: #111827; -fx-border-width: 2;" : " -fx-border-color: #111827; -fx-border-width: 1;"
        ));
        makeSelectable(markerIcon, object);
        makeDraggable(markerIcon, object);
        markerIcon.setRotate(object.rotationDegrees());
        double markerScale = adaptiveMapPixels(32) / 32;
        markerIcon.setScaleX(markerScale);
        markerIcon.setScaleY(markerScale);

        mapPane.getChildren().add(markerIcon);
        addRotationHandleIfActive(object, markerIcon);
        addMapLabel(object,
                object.position().x() + adaptiveMapPixels(34),
                object.position().y() + adaptiveMapPixels(4));
    }

    private void addMapLabel(PlannerObject object, double x, double y) {
        if (!isSelected(object) && (!showObjectLabels() || !object.showMapLabel())) {
            return;
        }
        double labelX = object.customMapLabelPosition() ? x + object.mapLabelOffset().x() : x;
        double labelY = object.customMapLabelPosition() ? y + object.mapLabelOffset().y() : y;
        double fontSize = adaptiveMapPixels(plan.objectLabelFontSize());
        double boxScale = fontSize / plan.objectLabelFontSize();
        Label label = new Label(mapLabel(object));
        label.setLayoutX(labelX);
        label.setLayoutY(labelY);
        label.setStyle("""
                -fx-background-color: rgba(255,255,255,0.82);
                -fx-border-color: rgba(17,24,39,0.35);
                -fx-border-width: %spx;
                -fx-background-radius: 3;
                -fx-border-radius: 3;
                -fx-padding: %spx %spx %spx %spx;
                -fx-text-fill: #111827;
                -fx-font-size: %spx;
                """.formatted(
                adaptiveMapPixels(1),
                2 * boxScale, 5 * boxScale, 2 * boxScale, 5 * boxScale,
                Double.toString(fontSize)
        ));
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

    private void makeFenceInventoryLabelDraggable(
            Label label,
            FenceRow fenceRow,
            double defaultX,
            double defaultY
    ) {
        makeSelectable(label, fenceRow);
        markFenceDragNode(label);
        final Delta pressScene = new Delta();
        final Delta startLayout = new Delta();
        label.setCursor(Cursor.HAND);
        label.setOnMousePressed(event -> {
            if (event.getButton() != MouseButton.PRIMARY || mapLayoutLocked) {
                return;
            }
            selectFenceRowForDrag(fenceRow);
            mapScrollPane.setPannable(false);
            beginPlanDrag();
            pressScene.x = event.getSceneX();
            pressScene.y = event.getSceneY();
            startLayout.x = label.getLayoutX();
            startLayout.y = label.getLayoutY();
            event.consume();
        });
        label.setOnMouseDragged(event -> {
            if (mapLayoutLocked) {
                return;
            }
            double labelX = startLayout.x + (event.getSceneX() - pressScene.x) / zoomLevel;
            double labelY = startLayout.y + (event.getSceneY() - pressScene.y) / zoomLevel;
            label.setLayoutX(labelX);
            label.setLayoutY(labelY);
            Position offset = new Position(labelX - defaultX, labelY - defaultY);
            plan.fenceNetworkRows(fenceRow.id()).forEach(row -> row.setInventoryLabelOffset(offset));
            recordPlanDragChange();
            event.consume();
        });
        label.setOnMouseReleased(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                mapScrollPane.setPannable(true);
                refreshDetails();
                event.consume();
            }
        });
    }


    private void makeSelectable(Node node, PlannerObject object) {
        mapObjectNodes.putIfAbsent(object.id(), node);
        registerObjectVisual(object, node);
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
            if (suppressNextObjectClick) {
                suppressNextObjectClick = false;
                event.consume();
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
            if (pendingFenceRowPlacement) {
                Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
                addPendingFencePoint(new Position(mapPoint.getX(), mapPoint.getY()));
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
            if (pendingPowerSourceConsumer != null && object instanceof PowerSource source) {
                connectPowerSourceFromMap(source);
                event.consume();
                return;
            }
            if (event.isControlDown()) {
                toggleObjectSelection(object);
            } else {
                selectObject(object);
            }
            event.consume();
        });
    }

    private void makeDraggable(Node node, PlannerObject object) {
        final Delta dragDelta = new Delta();
        final boolean[] fenceDragged = {false};
        final boolean[] dragArmed = {false};
        final Position[] dragStartObjectPosition = {null};
        node.setOnMousePressed(event -> {
            dragArmed[0] = false;
            if (event.getButton() != MouseButton.PRIMARY || measuringActive || addingCablePoint) {
                return;
            }
            if (event.isControlDown()) {
                event.consume();
                return;
            }
            if (pendingPowerSourceConsumer != null && object instanceof PowerSource source) {
                connectPowerSourceFromMap(source);
                event.consume();
                return;
            }
            boolean draggingMultipleObjects = isSelected(object) && selectedLogicalObjects().size() > 1;
            if (draggingMultipleObjects && selectedObjects().stream().anyMatch(this::isObjectEffectivelyLocked)) {
                event.consume();
                return;
            }
            if (draggingMultipleObjects) {
                multiObjectDragInProgress = true;
                multiObjectDragChanged = false;
            } else if (object instanceof FenceRow) {
                selectFenceRowForDrag(object);
            } else {
                selectObject(object);
            }
            if (mapLayoutLocked) {
                multiObjectDragInProgress = false;
                event.consume();
                return;
            }
            beginPlanDrag();
            dragArmed[0] = true;
            Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
            if (multiObjectDragInProgress || object instanceof FenceRow) {
                fenceDragged[0] = false;
                mapScrollPane.setPannable(false);
                dragDelta.x = event.getSceneX();
                dragDelta.y = event.getSceneY();
                if (multiObjectDragInProgress) {
                    multiObjectDragState = createMultiObjectDragState(mapPoint);
                }
            } else {
                dragStartObjectPosition[0] = object.position();
                dragDelta.x = mapPoint.getX() - object.position().x();
                dragDelta.y = mapPoint.getY() - object.position().y();
            }
            event.consume();
        });
        node.setOnMouseDragged(event -> {
            if (!dragArmed[0]) {
                event.consume();
                return;
            }
            if (measuringActive || addingCablePoint || mapLayoutLocked) {
                return;
            }
            if (isObjectEffectivelyLocked(object)) {
                return;
            }
            if (multiObjectDragInProgress) {
                return;
            } else if (object instanceof FenceRow fenceRow) {
                boolean moved = plan.translateFenceNetwork(
                        fenceRow.id(),
                        (event.getSceneX() - dragDelta.x) / zoomLevel,
                        (event.getSceneY() - dragDelta.y) / zoomLevel
                );
                dragDelta.x = event.getSceneX();
                dragDelta.y = event.getSceneY();
                if (!moved) {
                    event.consume();
                    return;
                }
                fenceDragged[0] = true;
                updateFenceNetworkDragPreview(fenceRow);
            } else {
                Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
                object.moveTo(object.position().moveTo(
                        mapPoint.getX() - dragDelta.x,
                        mapPoint.getY() - dragDelta.y
                ));
                updateObjectDragPreview(
                        object,
                        object.position().x() - dragStartObjectPosition[0].x(),
                        object.position().y() - dragStartObjectPosition[0].y()
                );
                updateDependentDragPreviews();
            }
            recordPlanDragChange();
            event.consume();
        });
        node.setOnMouseReleased(event -> {
            boolean wasArmed = dragArmed[0];
            dragArmed[0] = false;
            if (!wasArmed) {
                return;
            }
            if (multiObjectDragInProgress) {
                return;
            } else if (object instanceof FenceRow) {
                mapScrollPane.setPannable(true);
                if (fenceDragged[0]) {
                    redrawMap();
                    refreshDetails();
                }
                event.consume();
            } else {
                updateObjectDragPreview(object, 0, 0);
                redrawMap();
                refreshDetails();
                refreshSummary();
                event.consume();
            }
        });
    }

    private void finishMultiObjectDrag() {
        multiObjectDragInProgress = false;
        multiObjectDragState = null;
        mapScrollPane.setPannable(true);
        if (!multiObjectDragChanged) {
            return;
        }
        multiObjectDragChanged = false;
        suppressNextObjectClick = true;
        Platform.runLater(() -> suppressNextObjectClick = false);
        redrawMap();
        refreshDetails();
    }

    private MultiObjectDragState createMultiObjectDragState(Point2D pointerStart) {
        Map<String, Position> objectPositions = new HashMap<>();
        Map<String, Position> fenceJointPositions = new HashMap<>();
        for (PlannerObject object : selectedLogicalObjects()) {
            if (object instanceof FenceRow fenceRow) {
                for (FenceRow row : plan.fenceNetworkRows(fenceRow.id())) {
                    plan.findFenceJoint(row.startJointId()).ifPresent(joint ->
                            fenceJointPositions.put(joint.id(), joint.position()));
                    plan.findFenceJoint(row.endJointId()).ifPresent(joint ->
                            fenceJointPositions.put(joint.id(), joint.position()));
                }
            } else {
                objectPositions.put(object.id(), object.position());
            }
        }
        Set<String> movingObjectIds = new HashSet<>(objectPositions.keySet());
        Map<String, List<Position>> cableRoutePositions = plan.powerConnections().stream()
                .filter(connection -> movingObjectIds.contains(connection.sourceId())
                        && movingObjectIds.contains(connection.consumerId()))
                .collect(java.util.stream.Collectors.toMap(
                        PowerConnection::id,
                        connection -> List.copyOf(connection.routePoints())
                ));
        return new MultiObjectDragState(
                new Position(pointerStart.getX(), pointerStart.getY()),
                Map.copyOf(objectPositions),
                Map.copyOf(fenceJointPositions),
                Map.copyOf(cableRoutePositions)
        );
    }

    private void applyMultiObjectDrag(Point2D pointer) {
        if (multiObjectDragState == null) {
            return;
        }
        double deltaX = pointer.getX() - multiObjectDragState.pointerStart().x();
        double deltaY = pointer.getY() - multiObjectDragState.pointerStart().y();
        multiObjectDragState.objectPositions().forEach((objectId, originalPosition) ->
                plan.findObject(objectId).ifPresent(object -> object.moveTo(new Position(
                        originalPosition.x() + deltaX,
                        originalPosition.y() + deltaY
                )))
        );
        multiObjectDragState.fenceJointPositions().forEach((jointId, originalPosition) ->
                plan.findFenceJoint(jointId).ifPresent(joint -> joint.moveTo(new Position(
                        originalPosition.x() + deltaX,
                        originalPosition.y() + deltaY
                )))
        );
        multiObjectDragState.cableRoutePositions().forEach((connectionId, routePoints) ->
                plan.updateCableRoutePointsForConnection(connectionId, routePoints.stream()
                        .map(point -> new Position(point.x() + deltaX, point.y() + deltaY))
                        .toList())
        );
        plan.synchronizeFenceRows(pixelsPerMeter());
        updateDependentDragPreviews();
    }

    private void updateMultiObjectDragPreview(Point2D pointer) {
        if (multiObjectDragState == null) {
            return;
        }
        double deltaX = pointer.getX() - multiObjectDragState.pointerStart().x();
        double deltaY = pointer.getY() - multiObjectDragState.pointerStart().y();
        for (PlannerObject object : selectedObjects()) {
            updateObjectDragPreview(object, deltaX, deltaY);
        }
        if (multiSelectionBounds != null) {
            multiSelectionBounds.setTranslateX(deltaX);
            multiSelectionBounds.setTranslateY(deltaY);
        }
    }

    private void registerPowerConnectionVisual(String connectionId, Node... nodes) {
        List<Node> visuals = powerConnectionVisualNodes.computeIfAbsent(connectionId, ignored -> new ArrayList<>());
        for (Node node : nodes) {
            if (node != null && !visuals.contains(node)) {
                visuals.add(node);
            }
        }
    }

    private void translateNodes(List<Node> nodes, double deltaX, double deltaY) {
        for (Node node : nodes) {
            node.setTranslateX(deltaX);
            node.setTranslateY(deltaY);
        }
    }

    private void updateDependentDragPreviews() {
        powerConnectionVisuals.values().forEach(this::updatePowerConnectionVisual);
        textReferenceVisuals.values().forEach(this::updateTextReferenceVisual);
    }

    private void updatePowerConnectionVisual(PowerConnectionVisual visual) {
        List<Position> path = cablePath(visual.cable());
        replaceShapePoints(visual.line().getPoints(), path);
        replaceShapePoints(visual.highlightLine().getPoints(), path);
        replaceShapePoints(visual.hitLine().getPoints(), path);
        updateCableLabel(visual.distanceLabel(), visual.cable(), visual.cable().connection().routePoints());
        List<Position> routePoints = visual.cable().connection().routePoints();
        for (int index = 0; index < Math.min(routePoints.size(), visual.routePointMarkers().size()); index++) {
            Position point = routePoints.get(index);
            Circle marker = visual.routePointMarkers().get(index);
            marker.setCenterX(point.x());
            marker.setCenterY(point.y());
        }
        if (visual.anchorMarker() != null && !path.isEmpty()) {
            Position endpoint = path.getLast();
            visual.anchorMarker().setCenterX(endpoint.x());
            visual.anchorMarker().setCenterY(endpoint.y());
        }
    }

    private void updateTextReferenceVisual(TextReferenceVisual visual) {
        Position offset = visual.textObject().referenceLineSourceOffset();
        visual.line().setStartX(visual.source().position().x() + offset.x());
        visual.line().setStartY(visual.source().position().y() + offset.y());
        visual.line().setEndX(visual.textObject().position().x());
        visual.line().setEndY(visual.textObject().position().y());
        if (visual.anchor() != null) {
            visual.anchor().setCenterX(visual.line().getStartX());
            visual.anchor().setCenterY(visual.line().getStartY());
        }
    }

    private void registerObjectVisual(PlannerObject object, Node node) {
        if (node == null) {
            return;
        }
        List<Node> visuals = mapObjectVisualNodes.computeIfAbsent(object.id(), ignored -> new ArrayList<>());
        if (!visuals.contains(node)) {
            visuals.add(node);
        }
    }

    private void updateObjectDragPreview(PlannerObject object, double deltaX, double deltaY) {
        for (Node visual : mapObjectVisualNodes.getOrDefault(object.id(), List.of())) {
            visual.setTranslateX(deltaX);
            visual.setTranslateY(deltaY);
        }
        if (object == selectedObject && selectedObjectHighlight != null) {
            selectedObjectHighlight.setTranslateX(deltaX);
            selectedObjectHighlight.setTranslateY(deltaY);
        }
    }

    private void startObjectRotation(PlannerObject object) {
        if (!supportsInteractiveRotation(object)) {
            showError("Objekti ei saa pöörata", "Pööramine on praegu toetatud telkidele ja nelinurksetele objektidele.");
            return;
        }
        boolean multipleObjects = isSelected(object) && selectedLogicalObjects().size() > 1;
        boolean lockedFenceNetwork = object instanceof FenceRow fenceRow
                && plan.fenceNetworkRows(fenceRow.id()).stream().anyMatch(this::isObjectEffectivelyLocked);
        boolean lockedSelection = multipleObjects
                && selectedObjects().stream().anyMatch(this::isObjectEffectivelyLocked);
        if (mapLayoutLocked || isObjectEffectivelyLocked(object) || lockedFenceNetwork || lockedSelection) {
            showMapLayoutLockedMessage();
            return;
        }
        if (!multipleObjects) {
            selectObject(object);
        }
        rotatingMultipleObjects = multipleObjects;
        rotatingObjectId = object.id();
        updateMapToolStatus();
        redrawMap();
    }

    private boolean supportsInteractiveRotation(PlannerObject object) {
        return true;
    }

    private boolean isObjectRotationActive(PlannerObject object) {
        return !rotatingMultipleObjects && object != null && object.id().equals(rotatingObjectId);
    }

    private void addMultiObjectRotationHandleIfActive() {
        if (!rotatingMultipleObjects || rotatingObjectId == null || selectedObjects().isEmpty()) {
            return;
        }
        Position center;
        double handleDistance;
        double handleStartRotationDegrees;
        if (multiObjectRotationState != null) {
            center = multiObjectRotationState.center();
            handleDistance = multiObjectRotationState.handleDistance();
            handleStartRotationDegrees = multiObjectRotationState.handleStartRotationDegrees();
        } else {
            List<Bounds> bounds = selectedObjects().stream()
                    .map(object -> mapObjectNodes.get(object.id()))
                    .filter(java.util.Objects::nonNull)
                    .map(Node::getBoundsInParent)
                    .toList();
            if (bounds.isEmpty()) {
                return;
            }
            double minX = bounds.stream().mapToDouble(Bounds::getMinX).min().orElse(0);
            double maxX = bounds.stream().mapToDouble(Bounds::getMaxX).max().orElse(0);
            double minY = bounds.stream().mapToDouble(Bounds::getMinY).min().orElse(0);
            double maxY = bounds.stream().mapToDouble(Bounds::getMaxY).max().orElse(0);
            center = new Position((minX + maxX) / 2, (minY + maxY) / 2);
            double naturalDistance = Math.max(screenPixels(32),
                    Math.max(maxX - minX, maxY - minY) / 2 + screenPixels(24));
            Position visibleHandle = constrainRotationHandleToViewport(new Position(
                    center.x(), center.y() - naturalDistance
            ));
            handleDistance = Math.max(screenPixels(16), Math.hypot(
                    visibleHandle.x() - center.x(), visibleHandle.y() - center.y()
            ));
            handleStartRotationDegrees = pointerRotationDegrees(
                    new Point2D(visibleHandle.x(), visibleHandle.y()), center
            );
        }

        double radians = Math.toRadians(handleStartRotationDegrees + multiObjectRotationDelta - 90);
        double handleX = center.x() + Math.cos(radians) * handleDistance;
        double handleY = center.y() + Math.sin(radians) * handleDistance;
        Line guide = new Line(center.x(), center.y(), handleX, handleY);
        guide.setStroke(Color.web("#7c3aed"));
        guide.setStrokeWidth(screenPixels(2));
        guide.setMouseTransparent(true);
        Circle handle = new Circle(handleX, handleY, screenPixels(8), Color.WHITE);
        handle.setStroke(Color.web("#7c3aed"));
        handle.setStrokeWidth(screenPixels(3));
        handle.setCursor(Cursor.HAND);
        handle.setOnMousePressed(event -> {
            if (event.getButton() != MouseButton.PRIMARY
                    || mapLayoutLocked
                    || selectedObjects().stream().anyMatch(this::isObjectEffectivelyLocked)) {
                return;
            }
            Point2D pointer = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
            multiObjectRotationState = createMultiObjectRotationState(
                    center,
                    handleDistance,
                    handleStartRotationDegrees,
                    pointerRotationDegrees(pointer, center)
            );
            multiObjectRotationDelta = 0;
            mapScrollPane.setPannable(false);
            beginPlanDrag();
            event.consume();
        });
        mapPane.getChildren().addAll(guide, handle);
    }

    private Position constrainRotationHandleToViewport(Position desiredPosition) {
        Node viewport = mapScrollPane.lookup(".viewport");
        if (viewport == null) {
            return desiredPosition;
        }
        Bounds viewportSceneBounds = viewport.localToScene(viewport.getBoundsInLocal());
        Point2D topLeft = mapPane.sceneToLocal(
                viewportSceneBounds.getMinX(), viewportSceneBounds.getMinY()
        );
        Point2D bottomRight = mapPane.sceneToLocal(
                viewportSceneBounds.getMaxX(), viewportSceneBounds.getMaxY()
        );
        double padding = 14 / Math.max(zoomLevel, 0.1);
        return new Position(
                clamp(desiredPosition.x(), topLeft.getX() + padding, bottomRight.getX() - padding),
                clamp(desiredPosition.y(), topLeft.getY() + padding, bottomRight.getY() - padding)
        );
    }

    private MultiObjectRotationState createMultiObjectRotationState(
            Position center,
            double handleDistance,
            double handleStartRotationDegrees,
            double pointerStartRotationDegrees
    ) {
        Map<String, MultiObjectRotationObjectState> objectStates = new HashMap<>();
        Map<String, Position> fenceJointPositions = new HashMap<>();
        for (PlannerObject object : selectedLogicalObjects()) {
            if (object instanceof FenceRow fenceRow) {
                for (FenceRow row : plan.fenceNetworkRows(fenceRow.id())) {
                    plan.findFenceJoint(row.startJointId()).ifPresent(joint ->
                            fenceJointPositions.put(joint.id(), joint.position()));
                    plan.findFenceJoint(row.endJointId()).ifPresent(joint ->
                            fenceJointPositions.put(joint.id(), joint.position()));
                }
                continue;
            }
            objectStates.put(object.id(), new MultiObjectRotationObjectState(
                    object.position(),
                    CablePathHelper.objectCenter(object, pixelsPerMeter()),
                    object.rotationDegrees(),
                    rotationPoints(object)
            ));
        }
        return new MultiObjectRotationState(
                center,
                handleDistance,
                handleStartRotationDegrees,
                pointerStartRotationDegrees,
                Map.copyOf(objectStates),
                Map.copyOf(fenceJointPositions)
        );
    }

    private void applyMultiObjectRotation(double deltaDegrees) {
        if (multiObjectRotationState == null) {
            return;
        }
        Position center = multiObjectRotationState.center();
        multiObjectRotationState.objectStates().forEach((objectId, state) ->
                plan.findObject(objectId).ifPresent(object -> {
                    if (object instanceof AreaObject areaObject) {
                        areaObject.setPoints(rotatePositions(state.points(), center, deltaDegrees));
                    } else if (object instanceof LineObject lineObject) {
                        lineObject.setPoints(rotatePositions(state.points(), center, deltaDegrees));
                    } else {
                        Position rotatedCenter = rotatePositions(
                                List.of(state.visualCenter()), center, deltaDegrees
                        ).getFirst();
                        object.moveTo(new Position(
                                state.position().x() + rotatedCenter.x() - state.visualCenter().x(),
                                state.position().y() + rotatedCenter.y() - state.visualCenter().y()
                        ));
                    }
                    object.setRotationDegrees(state.rotationDegrees() + deltaDegrees);
                })
        );
        multiObjectRotationState.fenceJointPositions().forEach((jointId, position) ->
                plan.findFenceJoint(jointId).ifPresent(joint ->
                        joint.moveTo(rotatePositions(List.of(position), center, deltaDegrees).getFirst())
                )
        );
        plan.synchronizeFenceRows(pixelsPerMeter());
    }

    private void addRotationHandleIfActive(
            PlannerObject object,
            Position center,
            double widthPixels,
            double heightPixels,
            double rotationDegrees
    ) {
        if (!isObjectRotationActive(object)) {
            return;
        }
        double handleDistance = Math.max(screenPixels(24),
                Math.max(widthPixels, heightPixels) / 2 + screenPixels(24));
        double radians = Math.toRadians(rotationDegrees - 90);
        double handleX = center.x() + Math.cos(radians) * handleDistance;
        double handleY = center.y() + Math.sin(radians) * handleDistance;
        Line guide = new Line(center.x(), center.y(), handleX, handleY);
        guide.setStroke(Color.web("#7c3aed"));
        guide.setStrokeWidth(screenPixels(2));
        guide.setMouseTransparent(true);
        Circle handle = new Circle(handleX, handleY, screenPixels(8), Color.web("#ffffff"));
        handle.setStroke(Color.web("#7c3aed"));
        handle.setStrokeWidth(screenPixels(3));
        handle.setCursor(Cursor.HAND);
        handle.setOnMousePressed(event -> {
            if (event.getButton() != MouseButton.PRIMARY
                    || mapLayoutLocked
                    || isObjectEffectivelyLocked(object)) {
                return;
            }
            mapScrollPane.setPannable(false);
            beginPlanDrag();
            Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
            rotationDragState = new RotationDragState(
                    object.id(),
                    center,
                    pointerRotationDegrees(mapPoint, center),
                    object.rotationDegrees(),
                    rotationPoints(object),
                    rotationFenceJoints(object)
            );
            event.consume();
        });
        handle.setOnMouseDragged(event -> {
            if (mapLayoutLocked || isObjectEffectivelyLocked(object)) {
                return;
            }
            Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
            if (rotationDragState == null || !rotationDragState.objectId().equals(object.id())) {
                return;
            }
            double pointerAngle = pointerRotationDegrees(mapPoint, center);
            double angle = rotationDragState.objectStartRotationDegrees()
                    + normalizeAngleDelta(pointerAngle - rotationDragState.pointerStartRotationDegrees());
            updateInteractiveRotationPreview(object, angle, center, handleDistance, guide, handle);
            recordPlanDragChange();
            event.consume();
        });
        handle.setOnMouseReleased(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                mapScrollPane.setPannable(true);
                finishObjectRotation();
                event.consume();
            }
        });
        mapPane.getChildren().addAll(guide, handle);
    }

    private void addRotationHandleIfActive(PlannerObject object, Node node) {
        if (!isObjectRotationActive(object)) {
            return;
        }
        Bounds bounds = node.getBoundsInParent();
        addRotationHandleIfActive(
                object,
                new Position((bounds.getMinX() + bounds.getMaxX()) / 2, (bounds.getMinY() + bounds.getMaxY()) / 2),
                Math.max(16, bounds.getWidth()),
                Math.max(16, bounds.getHeight()),
                object.rotationDegrees()
        );
    }

    private void setInteractiveRotation(PlannerObject object, double rotationDegrees) {
        object.setRotationDegrees(rotationDegrees);
    }

    private double pointerRotationDegrees(Point2D point, Position center) {
        return Math.toDegrees(Math.atan2(point.getY() - center.y(), point.getX() - center.x())) + 90;
    }

    private double normalizeAngleDelta(double degrees) {
        double normalized = degrees % 360;
        if (normalized > 180) {
            normalized -= 360;
        } else if (normalized < -180) {
            normalized += 360;
        }
        return normalized;
    }

    private List<Position> rotationPoints(PlannerObject object) {
        if (object instanceof AreaObject areaObject) {
            return List.copyOf(areaObject.points());
        }
        if (object instanceof LineObject lineObject) {
            return List.copyOf(lineObject.points());
        }
        return List.of();
    }

    private Map<String, Position> rotationFenceJoints(PlannerObject object) {
        if (!(object instanceof FenceRow fenceRow)) {
            return Map.of();
        }
        Map<String, Position> jointPositions = new HashMap<>();
        for (FenceRow row : plan.fenceNetworkRows(fenceRow.id())) {
            plan.findFenceJoint(row.startJointId()).ifPresent(joint -> jointPositions.put(joint.id(), joint.position()));
            plan.findFenceJoint(row.endJointId()).ifPresent(joint -> jointPositions.put(joint.id(), joint.position()));
        }
        return Map.copyOf(jointPositions);
    }

    private void updateInteractiveRotationPreview(
            PlannerObject object,
            double rotationDegrees,
            Position center,
            double handleDistance,
            Line guide,
            Circle handle
    ) {
        setInteractiveRotation(object, rotationDegrees);
        Node mapObjectNode = mapObjectNodes.get(object.id());
        if (rotationDragState != null && object instanceof FenceRow fenceRow) {
            double delta = rotationDegrees - rotationDragState.objectStartRotationDegrees();
            rotationDragState.originalFenceJoints().forEach((jointId, position) ->
                    plan.findFenceJoint(jointId).ifPresent(joint ->
                            joint.moveTo(rotatePositions(List.of(position), center, delta).getFirst())
                    )
            );
            plan.synchronizeFenceRows(pixelsPerMeter());
            updateFenceNetworkDragPreview(fenceRow);
        } else if (rotationDragState != null && object instanceof AreaObject areaObject) {
            List<Position> points = rotatePositions(
                    rotationDragState.originalPoints(), center,
                    rotationDegrees - rotationDragState.objectStartRotationDegrees()
            );
            areaObject.setPoints(points);
            if (mapObjectNode instanceof Polygon polygon) {
                replaceShapePoints(polygon.getPoints(), points);
            }
        } else if (rotationDragState != null && object instanceof LineObject lineObject) {
            List<Position> points = rotatePositions(
                    rotationDragState.originalPoints(), center,
                    rotationDegrees - rotationDragState.objectStartRotationDegrees()
            );
            lineObject.setPoints(points);
            if (mapObjectNode instanceof Polyline polyline) {
                replaceShapePoints(polyline.getPoints(), points);
            }
        } else if (mapObjectNode != null) {
            mapObjectNode.setRotate(rotationDegrees);
        }

        double radians = Math.toRadians(rotationDegrees - 90);
        double handleX = center.x() + Math.cos(radians) * handleDistance;
        double handleY = center.y() + Math.sin(radians) * handleDistance;
        guide.setEndX(handleX);
        guide.setEndY(handleY);
        handle.setCenterX(handleX);
        handle.setCenterY(handleY);
        syncInteractiveRotationField(object, rotationDegrees);
    }

    private List<Position> rotatePositions(List<Position> points, Position center, double degrees) {
        double radians = Math.toRadians(degrees);
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);
        return points.stream().map(point -> {
            double deltaX = point.x() - center.x();
            double deltaY = point.y() - center.y();
            return new Position(
                    center.x() + deltaX * cos - deltaY * sin,
                    center.y() + deltaX * sin + deltaY * cos
            );
        }).toList();
    }

    private void replaceShapePoints(javafx.collections.ObservableList<Double> coordinates, List<Position> points) {
        coordinates.clear();
        points.forEach(point -> coordinates.addAll(point.x(), point.y()));
    }

    private void syncInteractiveRotationField(PlannerObject object, double rotationDegrees) {
        boolean wasUpdatingDetailControls = updatingDetailControls;
        updatingDetailControls = true;
        try {
            if (object instanceof Tent) {
                tentRotationField.setText(formatDegrees(rotationDegrees));
            } else if (object instanceof CustomObject) {
                customObjectRotationField.setText(formatDegrees(rotationDegrees));
            } else if (object instanceof FenceRow) {
                fenceRotationField.setText(formatDegrees(rotationDegrees));
            } else {
                generalRotationField.setText(formatDegrees(rotationDegrees));
            }
        } finally {
            updatingDetailControls = wasUpdatingDetailControls;
        }
    }

    private void finishObjectRotation() {
        if (rotatingObjectId == null) {
            return;
        }
        rotatingObjectId = null;
        rotationDragState = null;
        rotatingMultipleObjects = false;
        multiObjectRotationState = null;
        multiObjectRotationDelta = 0;
        mapScrollPane.setPannable(true);
        refreshDetails();
        updateMapToolStatus();
        redrawMap();
    }

    private void selectFenceRowForDrag(PlannerObject object) {
        if (selectedObject != null && selectedObject.id().equals(object.id())) {
            return;
        }
        if (selectedObject != null && !updatingDetailControls) {
            commitPendingDetailFieldsBeforeSelectionChange();
        }
        selectedObject = object;
        selectedObjectIds.clear();
        selectedObjectIds.addAll(logicalObjectIds(object));
        clearObjectSearchIfItHides(object);
        refreshDetails();
        revealObjectInObjectList(object);
        revealObjectInPowerSummary(object);
    }

    private void showObjectContextMenu(PlannerObject object, double screenX, double screenY) {
        if (!isSelected(object)) {
            selectObject(object);
        }
        int selectionCount = selectedLogicalObjects().size();
        MenuItem editItem = new MenuItem("Muuda");
        editItem.setDisable(selectionCount > 1);
        editItem.setOnAction(event -> editObject(object));
        MenuItem rotateItem = new MenuItem("Pööra");
        rotateItem.setDisable(mapLayoutLocked
                || selectedObjects().stream().anyMatch(this::isObjectEffectivelyLocked));
        rotateItem.setOnAction(event -> startObjectRotation(object));
        MenuItem copyItem = new MenuItem("Kopeeri");
        copyItem.setOnAction(event -> copySelectedObject());
        MenuItem visibilityItem = new MenuItem(allSelectedObjectsHidden() ? "Kuva valitud" : "Peida valitud");
        visibilityItem.setOnAction(event -> toggleSelectedObjectsHidden());
        MenuItem lockItem = new MenuItem(allSelectedObjectsLocked()
                ? "Eemalda valitud objektide lukustus"
                : "Lukusta valitud objektid");
        lockItem.setOnAction(event -> {
            lockedCheckBox.setSelected(!allSelectedObjectsLocked());
            updateSelectedLock();
        });
        MenuItem deleteItem = new MenuItem(selectionCount > 1
                ? "Kustuta valitud (%d)".formatted(selectionCount)
                : "Kustuta");
        deleteItem.setDisable(mapLayoutLocked
                || selectedObjects().stream().anyMatch(this::isObjectEffectivelyLocked));
        deleteItem.setOnAction(event -> deleteSelectedObject());

        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(editItem);
        if (supportsInteractiveRotation(object)) {
            menuItems.add(rotateItem);
        }
        menuItems.add(copyItem);

        List<MenuItem> objectSpecificItems = objectSpecificContextMenuItems(object, selectionCount);
        if (!objectSpecificItems.isEmpty()) {
            menuItems.add(new SeparatorMenuItem());
            menuItems.addAll(objectSpecificItems);
        }
        menuItems.add(new SeparatorMenuItem());
        menuItems.add(visibilityItem);
        menuItems.add(lockItem);
        menuItems.add(deleteItem);
        showContextMenu(
                new ContextMenu(menuItems.toArray(MenuItem[]::new)),
                mapPane,
                screenX,
                screenY
        );
    }

    private List<MenuItem> objectSpecificContextMenuItems(PlannerObject object, int selectionCount) {
        if (selectionCount > 1) {
            return List.of();
        }
        List<MenuItem> items = new ArrayList<>();
        if (!object.notes().isBlank()) {
            MenuItem notesAsTextItem = new MenuItem("Loo märkmetest tekstiobjekt");
            notesAsTextItem.setOnAction(event -> showNotesTextObjectDialog(object));
            items.add(notesAsTextItem);
        }
        if (object instanceof InventoryContainer container && !container.inventoryItems().isEmpty()) {
            items.add(inventoryAsTextMenuItem(object));
        }
        if (object instanceof PowerSource source && !source.outlets().isEmpty()) {
            items.add(powerOutletsAsTextMenuItem(object));
        }
        if (object instanceof PowerSource source && tartuCabinetSourceId(source).isPresent()) {
            MenuItem attachmentsItem = new MenuItem("Tartu GIS-i lisafailid…");
            attachmentsItem.setOnAction(event -> showTartuCabinetAttachments(source));
            items.add(attachmentsItem);
        }
        if (object instanceof TextObject textObject && !textObject.sourceObjectId().isBlank()) {
            MenuItem detachTextSourceItem = new MenuItem("Muuda staatiliseks tekstiks");
            detachTextSourceItem.setOnAction(event -> detachTextObjectSource(textObject));
            items.add(detachTextSourceItem);
        }
        return items;
    }

    private void detachTextObjectSource(PlannerObject object) {
        if (!(object instanceof TextObject textObject) || textObject.sourceObjectId().isBlank()) {
            return;
        }
        PlanSnapshot before = planSnapshotService.create(plan);
        textObject.setSourceObjectId("");
        finishAutoAppliedDetailsChange(before, false);
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
        if (selectedObjectSection == null || objectEditDialogOpen) {
            return;
        }
        Node detailContent = selectedObjectSection.getContent();
        if (detailContent == null) {
            return;
        }

        boolean sectionExpanded = selectedObjectSection.isExpanded();
        boolean detailContentVisible = detailContent.isVisible();
        boolean detailContentManaged = detailContent.isManaged();
        selectedObjectSection.setExpanded(true);
        selectedObjectSection.setContent(new Label("Objekti andmed on avatud muutmisaknas."));
        detailContent.setVisible(true);
        detailContent.setManaged(true);
        ScrollPane dialogScrollPane = new ScrollPane(detailContent);
        dialogScrollPane.setFitToWidth(true);
        dialogScrollPane.setPrefViewportWidth(620);
        dialogScrollPane.setPrefViewportHeight(680);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(stage);
        dialog.setTitle("Muuda objekti");
        dialog.getDialogPane().setHeaderText(object.name());
        dialog.getDialogPane().setContent(dialogScrollPane);
        dialog.getDialogPane().getButtonTypes().add(new ButtonType(
                "Sulge", ButtonBar.ButtonData.CANCEL_CLOSE
        ));
        objectEditDialogOpen = true;
        try {
            dialog.showAndWait();
        } finally {
            objectEditDialogOpen = false;
            dialogScrollPane.setContent(null);
            detailContent.setVisible(detailContentVisible);
            detailContent.setManaged(detailContentManaged);
            selectedObjectSection.setContent(detailContent);
            selectedObjectSection.setExpanded(sectionExpanded);
            refreshDetails();
        }
    }

    private void deleteObject(PlannerObject object) {
        selectObject(object);
        deleteSelectedObject();
    }

    private void setObjectHidden(PlannerObject object, boolean hidden) {
        if (object instanceof FenceRow fenceRow) {
            plan.fenceNetworkRows(fenceRow.id()).forEach(row -> row.setHidden(hidden));
        } else {
            object.setHidden(hidden);
        }
        redrawMap();
        refreshObjectList();
        refreshInventory();
        updateRevealObjectButton();
        markDirty();
    }

    private void setObjectHidden(ObjectListItem item, boolean hidden) {
        setObjectHidden(item.object(), hidden);
    }

    private void setObjectLocked(PlannerObject object, boolean locked) {
        if (object instanceof FenceRow fenceRow) {
            plan.fenceNetworkRows(fenceRow.id()).forEach(row -> row.setLocked(locked));
        } else {
            object.setLocked(locked);
        }
        redrawMap();
        refreshObjectList();
        refreshDetails();
        markDirty();
    }

    private void selectObject(PlannerObject object) {
        selectedMeasurementPaths.clear();
        if (rotatingObjectId != null && (object == null || !object.id().equals(rotatingObjectId))) {
            if (selectedObject != null) {
                syncInteractiveRotationField(selectedObject, selectedObjectRotationDegrees(selectedObject));
            }
            rotatingObjectId = null;
            mapScrollPane.setPannable(true);
        }
        if (selectedObject != null
                && object != null
                && !selectedObject.id().equals(object.id())
                && !updatingDetailControls) {
            commitPendingDetailFieldsBeforeSelectionChange();
        }
        selectedObject = object;
        selectedObjectIds.clear();
        selectedObjectIds.addAll(logicalObjectIds(object));
        selectionRangeAnchorObjectId = object == null ? null : object.id();
        clearObjectSearchIfItHides(object);
        refreshDetails();
        refreshObjectList();
        revealObjectInPowerSummary(object);
        redrawMap();
    }

    private void toggleObjectSelection(PlannerObject object) {
        if (object == null) {
            return;
        }
        if (selectedObject != null && !updatingDetailControls) {
            commitPendingDetailFieldsBeforeSelectionChange();
        }
        Set<String> logicalIds = logicalObjectIds(object);
        if (logicalIds.stream().allMatch(selectedObjectIds::contains)) {
            selectedObjectIds.removeAll(logicalIds);
            if (selectedObject != null && logicalIds.contains(selectedObject.id())) {
                selectedObject = firstSelectedObject().orElse(null);
            }
            if (logicalIds.contains(selectionRangeAnchorObjectId)) {
                selectionRangeAnchorObjectId = selectedObject == null ? null : selectedObject.id();
            }
        } else {
            selectedObjectIds.addAll(logicalIds);
            selectedObject = object;
            selectionRangeAnchorObjectId = object.id();
        }
        clearObjectSearchIfItHides(selectedObject);
        refreshDetails();
        refreshObjectList();
        revealObjectInPowerSummary(selectedObject);
        redrawMap();
    }

    private Set<String> logicalObjectIds(PlannerObject object) {
        if (object == null) {
            return Set.of();
        }
        if (object instanceof FenceRow fenceRow) {
            Set<String> ids = new LinkedHashSet<>();
            plan.fenceNetworkRows(fenceRow.id()).forEach(row -> ids.add(row.id()));
            return ids;
        }
        return Set.of(object.id());
    }

    private Optional<PlannerObject> firstSelectedObject() {
        return plan.objects().stream().filter(object -> selectedObjectIds.contains(object.id())).findFirst();
    }

    private List<PlannerObject> selectedObjects() {
        return plan.objects().stream().filter(object -> selectedObjectIds.contains(object.id())).toList();
    }

    private List<PlannerObject> selectedLogicalObjects() {
        List<PlannerObject> result = new ArrayList<>();
        Set<String> handledIds = new HashSet<>();
        for (PlannerObject object : selectedObjects()) {
            if (handledIds.contains(object.id())) {
                continue;
            }
            result.add(object);
            handledIds.addAll(logicalObjectIds(object));
        }
        return result;
    }

    private void updateActiveSelectionCountLabel() {
        if (activeSelectionCountLabel == null) {
            return;
        }
        int count = selectedLogicalObjects().size();
        activeSelectionCountLabel.setText(switch (count) {
            case 0 -> "Ühtegi aktiivset objekti pole";
            case 1 -> "1 aktiivne objekt";
            default -> "%d aktiivset objekti".formatted(count);
        });
        activeSelectionCountLabel.setStyle(count > 1
                ? "-fx-text-fill: #1d4ed8; -fx-font-size: 11; -fx-font-weight: bold;"
                : "-fx-text-fill: #475569; -fx-font-size: 11;");
    }

    private boolean allSelectedObjectsHidden() {
        List<PlannerObject> objects = selectedObjects();
        return !objects.isEmpty() && objects.stream().allMatch(PlannerObject::hidden);
    }

    private boolean allSelectedObjectsLocked() {
        List<PlannerObject> objects = selectedObjects();
        return !objects.isEmpty() && objects.stream().allMatch(PlannerObject::locked);
    }

    private void toggleSelectedObjectsHidden() {
        List<PlannerObject> objects = selectedObjects();
        if (objects.isEmpty()) {
            return;
        }
        boolean hidden = !allSelectedObjectsHidden();
        objects.forEach(object -> object.setHidden(hidden));
        redrawMap();
        refreshObjectList();
        refreshInventory();
        updateRevealObjectButton();
        markDirty();
    }

    private double selectedObjectRotationDegrees(PlannerObject object) {
        if (object instanceof Tent tent) {
            return tent.rotationDegrees();
        }
        if (object instanceof CustomObject customObject) {
            return customObject.rotationDegrees();
        }
        return 0;
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
        if (objectSearchField == null || object == null) {
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
                if (!collapsedSummaryKeys.contains(sourceItem.hierarchyKey())) {
                    if (connection.outletId().isBlank()) {
                        target = findSummaryTarget(object.id()).orElse(sourceItem);
                    } else {
                        SummaryListItem outletItem = findPowerSummaryItem(
                                powerOutletSummaryKey(connection.outletId())
                        ).orElse(null);
                        if (outletItem != null) {
                            target = outletItem;
                            if (!collapsedSummaryKeys.contains(outletItem.hierarchyKey())) {
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
                .filter(entry -> objectListEntryRepresents(entry, object))
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

    private boolean objectListEntryRepresents(ObjectListEntry entry, PlannerObject object) {
        PlannerObject listedObject = entry.objectItem().object();
        if (listedObject.id().equals(object.id())) {
            return true;
        }
        if (!(listedObject instanceof FenceRow listedFence) || !(object instanceof FenceRow fenceRow)) {
            return false;
        }
        return plan.fenceNetworkRows(listedFence.id()).stream()
                .anyMatch(candidate -> candidate.id().equals(fenceRow.id()));
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
        return object != null && selectedObjectIds.contains(object.id());
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
        return isObjectAvailableInCurrentView(object)
                && !object.hidden()
                && isGroupVisible(object)
                && isObjectTypeVisible(object);
    }

    private boolean isObjectAvailableInCurrentView(PlannerObject object) {
        if (!organizerView) {
            return true;
        }
        if (object instanceof PowerSource) {
            return false;
        }
        if (object instanceof TextObject textObject) {
            if (textObject.sourceType() == TextObjectSourceType.POWER_OUTLETS) {
                return false;
            }
            return plan.findObject(textObject.sourceObjectId())
                    .map(source -> !(source instanceof PowerSource))
                    .orElse(true);
        }
        return true;
    }

    private String groupNameForFilter(PlannerObject object) {
        return object.groupName().isBlank() ? "Määramata" : object.groupName();
    }

    private boolean isObjectEffectivelyLocked(PlannerObject object) {
        return plan.isObjectLocked(object);
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
        Set<String> lockedGroups = new HashSet<>(plan.lockedGroups());
        lockedGroups.retainAll(currentGroups);
        plan.clearLockedGroups();
        for (String lockedGroup : lockedGroups) {
            plan.setGroupLocked(lockedGroup, true);
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
        if (isObjectEffectivelyLocked(object)) {
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
        updateActiveSelectionCountLabel();
        updatingDetailControls = true;
        try {
            refreshDetailControls();
        } finally {
            updatingDetailControls = false;
        }
    }

    private void refreshDetailControls() {
        boolean hasSelection = selectedObject != null;
        List<PlannerObject> currentSelection = selectedObjects();
        boolean tentSelected = selectedObject instanceof Tent;
        boolean powerSourceSelected = selectedObject instanceof PowerSource;
        boolean tartuCabinetSelected = selectedObject instanceof PowerSource source
                && tartuCabinetSourceId(source).isPresent();
        boolean customObjectSelected = selectedObject instanceof CustomObject;
        boolean textObjectSelected = selectedObject instanceof TextObject;
        boolean markerSelected = selectedObject instanceof MarkerObject;
        boolean areaSelected = selectedObject instanceof AreaObject;
        boolean lineSelected = selectedObject instanceof LineObject;
        boolean fenceRowSelected = selectedObject instanceof FenceRow;
        boolean generalRotationVisible = hasSelection && !tentSelected && !customObjectSelected && !fenceRowSelected;
        boolean fenceGeometryEditable = fenceRowSelected
                && (plan.fenceJointDegree(((FenceRow) selectedObject).startJointId()) == 1
                || plan.fenceJointDegree(((FenceRow) selectedObject).endJointId()) == 1);
        boolean powerConsumerSelected = selectedObject instanceof PowerConsumer;
        boolean equipmentContainerSelected = selectedObject instanceof EquipmentContainer;
        boolean inventoryContainerSelected = selectedObject instanceof InventoryContainer;
        boolean linkedTextObject = selectedObject instanceof TextObject textObject
                && !textObject.sourceObjectId().isBlank();
        boolean textObjectHasMapSource = linkedTextObject
                && ((TextObject) selectedObject).sourceType() != TextObjectSourceType.INVENTORY_SUMMARY;
        nameField.setDisable(!hasSelection);
        groupField.setDisable(!hasSelection);
        notesArea.setDisable(!hasSelection
                || selectedObject instanceof TextObject textObject && textObject.syncSourceNotes());
        lockedCheckBox.setDisable(!hasSelection);
        boolean selectionHasMapLabels = currentSelection.stream()
                .anyMatch(object -> !(object instanceof TextObject));
        showMapLabelCheckBox.setDisable(!selectionHasMapLabels);
        boolean customMapLabelPosition = hasSelection && !textObjectSelected && selectedObject.customMapLabelPosition();
        resetMapLabelButton.setDisable(!customMapLabelPosition || mapLayoutLocked);
        selectedObjectOpacitySlider.setDisable(!hasSelection);
        generalRotationLabel.setVisible(generalRotationVisible);
        generalRotationLabel.setManaged(generalRotationVisible);
        generalRotationField.setVisible(generalRotationVisible);
        generalRotationField.setManaged(generalRotationVisible);
        generalRotationField.setDisable(!generalRotationVisible);
        resetMapLabelButton.setTooltip(new Tooltip(mapLabelResetTooltip(hasSelection, textObjectSelected, customMapLabelPosition)));
        boolean lockedSelection = selectedObjects().stream().anyMatch(this::isObjectEffectivelyLocked);
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
        textObjectTextOpacitySlider.setDisable(!textObjectSelected);
        textObjectSyncNotesCheckBox.setDisable(!linkedTextObject);
        textObjectReferenceLineCheckBox.setDisable(!textObjectHasMapSource);
        markerTypeComboBox.setDisable(!markerSelected);
        markerColorPicker.setDisable(!markerSelected);
        areaColorPicker.setDisable(!areaSelected);
        areaOpacitySlider.setDisable(!areaSelected);
        lineColorPicker.setDisable(!lineSelected);
        fenceColorPicker.setDisable(!fenceRowSelected);
        fenceWidthSlider.setDisable(!fenceRowSelected);
        decreaseFenceNetworkStonesButton.setDisable(!fenceRowSelected);
        increaseFenceNetworkStonesButton.setDisable(!fenceRowSelected);
        showFenceInventoryLabelCheckBox.setDisable(!fenceRowSelected);
        resetFenceInventoryLabelButton.setDisable(
                !fenceRowSelected || !((FenceRow) selectedObject).customInventoryLabelPosition()
        );
        lineWidthSlider.setDisable(!lineSelected);
        fenceSegmentCountField.setDisable(!fenceGeometryEditable);
        fenceSegmentLengthField.setDisable(!fenceGeometryEditable);
        fenceRotationField.setDisable(!fenceGeometryEditable);
        tentWidthField.setDisable(!tentSelected);
        tentHeightField.setDisable(!tentSelected);
        tentRotationField.setDisable(!tentSelected);
        tentColorPicker.setDisable(!tentSelected);
        tentOpacitySlider.setDisable(!tentSelected);
        powerSourceColorPicker.setDisable(!powerSourceSelected);
        powerSourceSizeSlider.setDisable(!powerSourceSelected);
        powerConnectionComboBox.setDisable(!powerConsumerSelected);
        powerSourceComboBox.setDisable(!powerConsumerSelected);
        connectionOutletComboBox.setDisable(!powerConsumerSelected);
        cableLengthNotesField.setDisable(!powerConsumerSelected);
        cableNotesField.setDisable(!powerConsumerSelected);
        PowerConnection editedPowerConnection = powerConsumerSelected ? selectedPowerConnection() : null;
        cablePieceEditor.setDisable(editedPowerConnection == null);
        cableOpacitySlider.setDisable(editedPowerConnection == null);
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
        objectInventoryList.setDisable(!inventoryContainerSelected);
        addObjectInventoryButton.setDisable(!inventoryContainerSelected);
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
        setSectionVisible(fenceRowPanel, fenceRowSelected);
        setSectionVisible(tentPanel, tentSelected);
        setSectionVisible(powerSourcePanel, powerSourceSelected);
        setSectionVisible(powerSourceAttachmentsButton, tartuCabinetSelected && !organizerView);
        setSectionVisible(powerConnectionPanel, powerConsumerSelected && !organizerView);
        setSectionVisible(equipmentSection, equipmentContainerSelected && !organizerView);
        setSectionVisible(objectInventorySection, inventoryContainerSelected);
        setSectionVisible(outletSection, powerSourceSelected && !organizerView);
        setSectionVisible(choosePowerSourceButton, powerConsumerSelected && !organizerView);
        setSectionVisible(deleteObjectButton, hasSelection);

        if (!hasSelection) {
            selectedTypeLabel.setText("Vali kaardilt objekt");
            nameField.clear();
            refreshGroupChoices("");
            notesArea.clear();
            lockedCheckBox.setSelected(false);
            showMapLabelCheckBox.setSelected(false);
            generalRotationField.clear();
            setOpacitySliderValue(selectedObjectOpacitySlider, 100);
            tentWidthField.clear();
            tentHeightField.clear();
            tentRotationField.clear();
            tentColorPicker.setValue(Color.web("#e74c3c"));
            setOpacitySliderValue(tentOpacitySlider, Tent.DEFAULT_OPACITY * 100.0);
            powerSourceColorPicker.setValue(Color.web(PowerSource.DEFAULT_COLOR_HEX));
            powerSourceSizeSlider.setValue(PowerSource.DEFAULT_SIZE_PIXELS);
            customObjectShapeComboBox.getSelectionModel().select(CustomObjectShape.SQUARE);
            customObjectColorPicker.setValue(Color.web("#9ca3af"));
            textObjectColorPicker.setValue(Color.web("#111827"));
            textObjectFontSizeSlider.setValue(TextObject.DEFAULT_FONT_SIZE);
            setOpacitySliderValue(textObjectTextOpacitySlider, 100);
            textObjectSyncNotesCheckBox.setSelected(false);
            textObjectReferenceLineCheckBox.setSelected(false);
            markerTypeComboBox.getSelectionModel().select(MarkerType.WC);
            markerColorPicker.setValue(Color.web(MarkerType.WC.defaultColorHex()));
            areaColorPicker.setValue(Color.web("#f59e0b"));
            setOpacitySliderValue(areaOpacitySlider, AreaObject.DEFAULT_OPACITY * 100.0);
            areaSizeLabel.setText("-");
            areaPerimeterLabel.setText("-");
            lineColorPicker.setValue(Color.web("#0f766e"));
            lineWidthSlider.setValue(LineObject.DEFAULT_WIDTH_PIXELS);
            lineLengthLabel.setText("-");
            fenceSegmentCountField.clear();
            fenceSegmentLengthField.clear();
            fenceRotationField.clear();
            fenceTotalLengthLabel.setText("-");
            fenceNetworkSummaryLabel.setText("-");
            fenceNetworkStoneSummaryLabel.setText("-");
            showFenceInventoryLabelCheckBox.setSelected(true);
            fenceWidthSlider.setValue(FenceRow.DEFAULT_WIDTH_PIXELS);
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

        int logicalSelectionCount = selectedLogicalObjects().size();
        selectedTypeLabel.setText(logicalSelectionCount > 1
                ? "%d objekti valitud · %s".formatted(logicalSelectionCount, objectTypeName(selectedObject))
                : objectTypeName(selectedObject));
        nameField.setText(selectedObject.name());
        refreshGroupChoices(selectedObject.groupName());
        notesArea.setText(selectedObject.notes());
        lockedCheckBox.setSelected(allSelectedObjectsLocked());
        List<PlannerObject> mapLabelObjects = currentSelection.stream()
                .filter(object -> !(object instanceof TextObject))
                .toList();
        showMapLabelCheckBox.setSelected(!mapLabelObjects.isEmpty()
                && mapLabelObjects.stream().allMatch(PlannerObject::showMapLabel));
        setOpacitySliderValue(selectedObjectOpacitySlider, selectedObject.opacity() * 100.0);
        generalRotationField.setText(formatDegrees(selectedObject.rotationDegrees()));
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
        } else if (selectedObject instanceof PowerSource powerSource) {
            tentWidthField.clear();
            tentHeightField.clear();
            tentRotationField.clear();
            powerSourceColorPicker.setValue(Color.web(powerSource.colorHex()));
            powerSourceSizeSlider.setValue(powerSource.sizePixels());
            customObjectWidthField.clear();
            customObjectHeightField.clear();
            customObjectRotationField.clear();
            cableLengthNotesField.clear();
            cableNotesField.clear();
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
            setOpacitySliderValue(textObjectTextOpacitySlider, textObject.textOpacity() * 100.0);
            textObjectSyncNotesCheckBox.setText(switch (textObject.sourceType()) {
                case INVENTORY -> "Uuenda objekti nimest ja inventarist";
                case INVENTORY_SUMMARY -> "Uuenda inventari kokkuvõtet";
                case POWER_OUTLETS -> "Uuenda kilbi nimest ja väljunditest";
                default -> "Uuenda objekti nimest ja märkmetest";
            });
            textObjectSyncNotesCheckBox.setSelected(textObject.syncSourceNotes());
            textObjectReferenceLineCheckBox.setSelected(textObject.showReferenceLine());
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
        } else if (selectedObject instanceof FenceRow fenceRow) {
            List<FenceRow> networkRows = plan.fenceNetworkRows(fenceRow.id());
            int networkFenceCount = networkRows.stream().mapToInt(FenceRow::segmentCount).sum();
            double networkLength = networkRows.stream().mapToDouble(FenceRow::totalLengthMeters).sum();
            InventorySummaryService.FenceStoneNetwork stoneSummary = fenceStoneNetworkSummary(
                    plan.isFenceNetworkRepresentative(fenceRow)
                            ? fenceRow
                            : networkRows.getFirst()
            );
            fenceNetworkSummaryLabel.setText("%d aeda · %s m".formatted(
                    networkFenceCount, formatMeters(networkLength)
            ));
            fenceNetworkStoneSummaryLabel.setText("%d automaatne · %s · %d kokku".formatted(
                    stoneSummary.automaticCount(),
                    signedCount(stoneSummary.adjustment()),
                    stoneSummary.totalCount()
            ));
            decreaseFenceNetworkStonesButton.setDisable(stoneSummary.totalCount() == 0);
            showFenceInventoryLabelCheckBox.setSelected(
                    plan.showFenceNetworkInventoryLabel(fenceRow.id())
            );
            fenceSegmentCountField.setText(Integer.toString(fenceRow.segmentCount()));
            fenceSegmentLengthField.setText(formatMeters(fenceRow.segmentLengthMeters()));
            fenceRotationField.setText(formatDegrees(fenceRow.rotationDegrees()));
            refreshFenceRowLengthLabel(fenceRow);
            fenceColorPicker.setValue(Color.web(fenceRow.colorHex()));
            fenceWidthSlider.setValue(fenceRow.widthPixels());
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
        refreshObjectInventoryList();
        refreshOutletList();
        updateCustomObjectSizeFields();
    }

    private void refreshSelectedPowerConnectionFields() {
        if (!(selectedObject instanceof PowerConsumer)) {
            cableLengthNotesField.clear();
            refreshCablePieceEditor(null);
            cableNotesField.clear();
            showSelectedCableLabelCheckBox.setSelected(true);
            return;
        }
        Optional.ofNullable(selectedPowerConnection()).ifPresentOrElse(connection -> {
            cableLengthNotesField.setText(connection.cableLengthNotes());
            refreshCablePieceEditor(connection);
            cableNotesField.setText(connection.cableNotes());
            showSelectedCableLabelCheckBox.setSelected(plan.showCableLabel(connection.id()));
            setOpacitySliderValue(cableOpacitySlider, plan.cableOpacity(connection.id()) * 100.0);
        }, () -> {
            cableLengthNotesField.clear();
            refreshCablePieceEditor(null);
            cableNotesField.clear();
            showSelectedCableLabelCheckBox.setSelected(true);
            setOpacitySliderValue(cableOpacitySlider, 100);
        });
    }

    private void refreshCablePieceEditor(PowerConnection connection) {
        if (cablePieceEditor == null) {
            return;
        }
        cablePieceEditor.getChildren().clear();
        if (connection == null) {
            cablePieceEditor.getChildren().add(new Label("Vali vooluühendus"));
            return;
        }
        Map<Double, Integer> counts = cablePieceCounts(connection.cableLengthNotes());
        populateCablePieceEditor(cablePieceEditor, counts, () -> {
            PowerConnection current = selectedPowerConnection();
            if (current == null || !current.id().equals(connection.id())) {
                return;
            }
            updateCablePieceCounts(current, counts);
        });
    }

    private void populateCablePieceEditor(
            VBox editor,
            Map<Double, Integer> counts,
            Runnable afterChange
    ) {
        editor.getChildren().clear();
        double totalLength = counts.entrySet().stream()
                .mapToDouble(entry -> entry.getKey() * entry.getValue())
                .sum();
        Label totalLabel = new Label("Kokku: %s m".formatted(formatCablePieceLength(totalLength)));
        totalLabel.setStyle("-fx-font-weight: bold;");
        editor.getChildren().add(totalLabel);
        TreeSet<Double> lengths = new TreeSet<>(DEFAULT_CABLE_PIECE_LENGTHS);
        lengths.addAll(counts.keySet());
        for (double length : lengths) {
            int count = counts.getOrDefault(length, 0);
            Label countLabel = new Label("%d tk".formatted(count));
            countLabel.setMinWidth(34);
            Button decrease = new Button("−");
            decrease.setDisable(count == 0);
            Button increase = new Button("+");
            decrease.setOnAction(event -> {
                adjustCablePieceCount(counts, length, -1);
                afterChange.run();
            });
            increase.setOnAction(event -> {
                adjustCablePieceCount(counts, length, 1);
                afterChange.run();
            });
            HBox row = new HBox(6, new Label(formatCablePieceLength(length) + " m"), countLabel, decrease, increase);
            row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            editor.getChildren().add(row);
        }
        TextField customLength = new TextField();
        customLength.setPromptText("Muu pikkus m");
        customLength.setPrefColumnCount(6);
        Button addCustomLength = new Button("Lisa");
        Runnable addLength = () -> {
            try {
                double length = Double.parseDouble(customLength.getText().trim().replace(',', '.'));
                if (length <= 0 || !Double.isFinite(length)) {
                    throw new NumberFormatException();
                }
                adjustCablePieceCount(counts, length, 1);
                afterChange.run();
            } catch (NumberFormatException exception) {
                showError("Kaablitükki ei lisatud", "Sisesta positiivne pikkus meetrites.");
            }
        };
        addCustomLength.setOnAction(event -> addLength.run());
        customLength.setOnAction(event -> addLength.run());
        HBox addRow = new HBox(6, customLength, addCustomLength);
        addRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        editor.getChildren().add(addRow);
    }

    private void updateCablePieceCounts(PowerConnection connection, Map<Double, Integer> counts) {
        String lengthNotes = formatCablePieceCounts(counts);
        plan.updateCableLengthNotesForConnection(connection.id(), lengthNotes);
        cableLengthNotesField.setText(lengthNotes);
        refreshSelectedPowerConnectionFields();
        redrawMap();
        refreshSummary();
        markDirty();
    }

    private Map<Double, Integer> cablePieceCounts(String lengthNotes) {
        Map<Double, Integer> counts = new TreeMap<>();
        java.util.regex.Pattern piecePattern = java.util.regex.Pattern.compile(
                "(?:(\\d+)\\s*[x×]\\s*)?(\\d+(?:[,.]\\d+)?)\\s*m?"
        );
        for (String part : lengthNotes.split("\\+")) {
            java.util.regex.Matcher matcher = piecePattern.matcher(part.trim());
            if (!matcher.matches()) {
                continue;
            }
            int count = matcher.group(1) == null ? 1 : Integer.parseInt(matcher.group(1));
            double length = Double.parseDouble(matcher.group(2).replace(',', '.'));
            if (length > 0 && Double.isFinite(length) && count > 0) {
                counts.merge(length, count, Integer::sum);
            }
        }
        return counts;
    }

    private void adjustCablePieceCount(Map<Double, Integer> counts, double length, int adjustment) {
        int updated = Math.max(0, counts.getOrDefault(length, 0) + adjustment);
        if (updated == 0) {
            counts.remove(length);
        } else {
            counts.put(length, updated);
        }
    }

    private String formatCablePieceCounts(Map<Double, Integer> counts) {
        return counts.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .map(entry -> entry.getValue() == 1
                        ? formatCablePieceLength(entry.getKey())
                        : "%dx%s".formatted(entry.getValue(), formatCablePieceLength(entry.getKey())))
                .collect(java.util.stream.Collectors.joining(" + "));
    }

    private String formatCablePieceLength(double length) {
        return Math.abs(length - Math.rint(length)) < 0.0001
                ? Integer.toString((int) Math.rint(length))
                : "%.1f".formatted(length);
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
        selectedObjects().forEach(object -> object.setLocked(lockedCheckBox.isSelected()));
        redrawMap();
        refreshObjectList();
        markDirty();
    }

    private void updateSelectedMapLabelVisibility() {
        if (selectedObject == null) {
            return;
        }
        selectedObjects().stream()
                .filter(object -> !(object instanceof TextObject))
                .forEach(object -> object.setShowMapLabel(showMapLabelCheckBox.isSelected()));
        redrawMap();
        refreshDetails();
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

    private void resetFenceInventoryLabelPosition() {
        if (mapLayoutLocked) {
            showMapLayoutLockedMessage();
            return;
        }
        if (!(selectedObject instanceof FenceRow fenceRow)) {
            return;
        }
        plan.fenceNetworkRows(fenceRow.id()).forEach(FenceRow::resetInventoryLabelPosition);
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
        if (selectedObject instanceof FenceRow fenceRow) {
            plan.fenceNetworkRows(fenceRow.id()).forEach(row -> row.setNotes(notes));
        } else {
            selectedObject.setNotes(notes);
        }
        if (selectedObject instanceof TextObject) {
            redrawMap();
        }
        refreshInventory();
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
        if (object instanceof Tent tent && tent.preset() == TentPreset.DJ_TRUCK) {
            return "Red Bull DJ Truck";
        }
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
        if (object instanceof FenceRow) {
            return "Aiarida";
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
        if (selectedObject instanceof FenceRow fenceRow) {
            plan.fenceNetworkRows(fenceRow.id()).forEach(row -> row.rename(nameField.getText()));
        } else {
            selectedObject.rename(nameField.getText());
        }
        finishAutoAppliedDetailsChange(false);
    }

    private void autoApplySelectedGroup() {
        if (updatingDetailControls || selectedObject == null) {
            return;
        }
        String groupName = groupField.getEditor().getText();
        List<PlannerObject> objects = selectedObjects();
        if (objects.stream().allMatch(object -> object.groupName().equals(groupName))) {
            return;
        }
        objects.forEach(object -> object.setGroupName(groupName));
        finishAutoAppliedDetailsChange(true);
    }

    private void autoApplyGeneralRotation() {
        if (updatingDetailControls || selectedObject == null
                || selectedObject instanceof Tent
                || selectedObject instanceof CustomObject
                || selectedObject instanceof FenceRow) {
            return;
        }
        try {
            double targetRotation = Double.parseDouble(generalRotationField.getText().trim().replace(',', '.'));
            if (Double.compare(targetRotation, selectedObject.rotationDegrees()) == 0) {
                return;
            }
            PlanSnapshot before = planSnapshotService.create(plan);
            if (selectedObject instanceof AreaObject areaObject) {
                Position center = averagePosition(areaObject.points());
                areaObject.setPoints(rotatePositions(
                        areaObject.points(), center, targetRotation - areaObject.rotationDegrees()
                ));
            } else if (selectedObject instanceof LineObject lineObject) {
                Position center = averagePosition(lineObject.points());
                lineObject.setPoints(rotatePositions(
                        lineObject.points(), center, targetRotation - lineObject.rotationDegrees()
                ));
            }
            selectedObject.setRotationDegrees(targetRotation);
            finishAutoAppliedDetailsChange(before, false);
        } catch (NumberFormatException exception) {
            showError("Pööret ei muudetud", "Sisesta pööre arvuna kraadides.");
            generalRotationField.setText(formatDegrees(selectedObject.rotationDegrees()));
        }
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

    private void autoApplyFenceRowGeometry() {
        if (updatingDetailControls || !(selectedObject instanceof FenceRow fenceRow)) {
            return;
        }
        if (plan.fenceJointDegree(fenceRow.startJointId()) > 1
                && plan.fenceJointDegree(fenceRow.endJointId()) > 1) {
            return;
        }
        try {
            int segmentCount = Integer.parseInt(fenceSegmentCountField.getText().trim());
            double segmentLength = Double.parseDouble(
                    fenceSegmentLengthField.getText().trim().replace(',', '.')
            );
            double rotation = Double.parseDouble(fenceRotationField.getText().trim().replace(',', '.'));
            if (segmentCount < 1 || segmentLength <= 0) {
                return;
            }
            PlanSnapshot before = planSnapshotService.create(plan);
            fenceRow.setSegmentCount(segmentCount);
            fenceRow.setSegmentLengthMeters(segmentLength);
            fenceRow.setRotationDegrees(rotation);
            plan.applyFenceRowGeometry(fenceRow);
            refreshFenceRowLengthLabel(fenceRow);
            finishAutoAppliedDetailsChange(before, false);
        } catch (NumberFormatException ignored) {
            // Poolikut sisestust ei rakendata enne Enterit või väljalt lahkumist.
        }
    }

    private void refreshFenceRowLengthLabel(FenceRow fenceRow) {
        fenceTotalLengthLabel.setText("%.1f m".formatted(fenceRow.totalLengthMeters()));
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
        } else if (selectedObject instanceof PowerSource powerSource) {
            powerSource.setColorHex(toHex(powerSourceColorPicker.getValue()));
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
        } else if (selectedObject instanceof FenceRow fenceRow) {
            String colorHex = toHex(fenceColorPicker.getValue());
            plan.fenceNetworkRows(fenceRow.id()).forEach(row -> row.setColorHex(colorHex));
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
        List<PlannerObject> logicalSelection = selectedLogicalObjects();
        if (logicalSelection.size() == 1) {
            copyObject(selectedObject);
            return;
        }
        double originX = logicalSelection.stream().mapToDouble(object -> object.position().x()).min().orElse(0);
        double originY = logicalSelection.stream().mapToDouble(object -> object.position().y()).min().orElse(0);
        copiedObjectsOrigin = new Position(originX, originY);
        copiedObjects = logicalSelection.stream()
                .map(object -> new MultiObjectClipboardEntry(
                        copyObjectAt(object, object.position(), object.name()),
                        new Position(object.position().x() - originX, object.position().y() - originY),
                        object instanceof FenceRow fenceRow ? createFenceNetworkClipboard(fenceRow) : null
                ))
                .filter(entry -> entry.template() != null)
                .toList();
        copiedObject = null;
        copiedFenceNetwork = null;
        keyboardPasteCount = 0;
    }

    private void copyObject(PlannerObject object) {
        copiedObject = copyObjectAt(object, object.position(), object.name());
        copiedFenceNetwork = object instanceof FenceRow fenceRow
                ? createFenceNetworkClipboard(fenceRow)
                : null;
        copiedObjects = List.of();
        copiedObjectsOrigin = null;
        keyboardPasteCount = 0;
    }

    private boolean hasCopiedObjects() {
        return copiedObject != null || !copiedObjects.isEmpty();
    }

    private FenceNetworkClipboard createFenceNetworkClipboard(FenceRow selectedRow) {
        Position origin = selectedRow.position();
        List<FenceRow> rows = plan.fenceNetworkRows(selectedRow.id());
        Map<String, Position> relativeJoints = new HashMap<>();
        rows.stream()
                .flatMap(row -> java.util.stream.Stream.of(row.startJointId(), row.endJointId()))
                .distinct()
                .forEach(jointId -> plan.findFenceJoint(jointId).ifPresent(joint ->
                        relativeJoints.put(jointId, new Position(
                                joint.position().x() - origin.x(),
                                joint.position().y() - origin.y()
                        ))));
        List<FenceRowClipboard> copiedRows = rows.stream()
                .map(row -> new FenceRowClipboard(
                        row.name(),
                        row.groupName(),
                        row.notes(),
                        row.showMapLabel(),
                        row.segmentCount(),
                        row.segmentLengthMeters(),
                        row.colorHex(),
                        row.widthPixels(),
                        row.opacity(),
                        row.gardenStoneAdjustment(),
                        row.showInventoryLabel(),
                        row.customInventoryLabelPosition() ? row.inventoryLabelOffset() : null,
                        row.startJointId(),
                        row.endJointId()
                ))
                .toList();
        return new FenceNetworkClipboard(relativeJoints, copiedRows);
    }

    private void pasteCopiedObjectWithOffset() {
        if (!hasCopiedObjects()) {
            return;
        }
        keyboardPasteCount++;
        if (!copiedObjects.isEmpty()) {
            pasteCopiedObjectGroup(new Position(
                    copiedObjectsOrigin.x() + 32.0 * keyboardPasteCount,
                    copiedObjectsOrigin.y() + 32.0 * keyboardPasteCount
            ));
            return;
        }
        pasteCopiedObject(new Position(
                copiedObject.position().x() + 32.0 * keyboardPasteCount,
                copiedObject.position().y() + 32.0 * keyboardPasteCount
        ));
    }

    private void pasteCopiedObject(Position position) {
        if (!hasCopiedObjects()) {
            return;
        }
        if (mapLayoutLocked) {
            showMapLayoutLockedMessage();
            return;
        }
        if (!copiedObjects.isEmpty()) {
            pasteCopiedObjectGroup(position);
            return;
        }
        if (copiedFenceNetwork != null) {
            pasteCopiedFenceNetwork(position);
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

    private void pasteCopiedFenceNetwork(Position position) {
        List<FenceRow> pastedRows = pasteFenceNetworkAt(copiedFenceNetwork, copiedObject.name(), position);
        FenceRow firstRow = pastedRows.getFirst();
        refreshGroupFilters();
        selectObject(firstRow);
        refreshSummary();
        markDirty();
    }

    private List<FenceRow> pasteFenceNetworkAt(
            FenceNetworkClipboard clipboard,
            String sourceName,
            Position position
    ) {
        Map<String, String> pastedJointIds = new HashMap<>();
        clipboard.relativeJoints().forEach((sourceJointId, relativePosition) ->
                pastedJointIds.put(sourceJointId, plan.createFenceJoint(new Position(
                        position.x() + relativePosition.x(),
                        position.y() + relativePosition.y()
                ))));
        List<FenceRow> pastedRows = new ArrayList<>();
        String pastedName = sourceName.isBlank() ? "Koopia" : sourceName + " koopia";
        for (FenceRowClipboard copiedRow : clipboard.rows()) {
            Position startPosition = plan.findFenceJoint(pastedJointIds.get(copiedRow.startJointId()))
                    .orElseThrow()
                    .position();
            FenceRow pastedRow = new FenceRow(planFactory.newId(), pastedName, startPosition);
            pastedRow.setGroupName(copiedRow.groupName());
            pastedRow.setNotes(copiedRow.notes());
            pastedRow.setShowMapLabel(copiedRow.showMapLabel());
            pastedRow.setSegmentCount(copiedRow.segmentCount());
            pastedRow.setSegmentLengthMeters(copiedRow.segmentLengthMeters());
            pastedRow.setColorHex(copiedRow.colorHex());
            pastedRow.setWidthPixels(copiedRow.widthPixels());
            pastedRow.setOpacity(copiedRow.opacity());
            pastedRow.setGardenStoneAdjustment(copiedRow.gardenStoneAdjustment());
            pastedRow.setShowInventoryLabel(copiedRow.showInventoryLabel());
            if (copiedRow.inventoryLabelOffset() != null) {
                pastedRow.setInventoryLabelOffset(copiedRow.inventoryLabelOffset());
            }
            plan.addObject(pastedRow);
            pastedRow.setJointIds(
                    pastedJointIds.get(copiedRow.startJointId()),
                    pastedJointIds.get(copiedRow.endJointId())
            );
            pastedRows.add(pastedRow);
        }
        FenceRow firstRow = pastedRows.getFirst();
        plan.setFenceRowJoints(firstRow, firstRow.startJointId(), firstRow.endJointId());
        return pastedRows;
    }

    private void pasteCopiedObjectGroup(Position origin) {
        if (mapLayoutLocked || copiedObjects.isEmpty()) {
            return;
        }
        List<PlannerObject> pastedObjects = new ArrayList<>();
        for (MultiObjectClipboardEntry entry : copiedObjects) {
            Position target = new Position(
                    origin.x() + entry.relativePosition().x(),
                    origin.y() + entry.relativePosition().y()
            );
            if (entry.fenceNetwork() != null) {
                pastedObjects.addAll(pasteFenceNetworkAt(
                        entry.fenceNetwork(), entry.template().name(), target
                ));
            } else {
                PlannerObject copy = copyObjectAt(
                        entry.template(), target, duplicateName(entry.template())
                );
                if (copy != null) {
                    plan.addObject(copy);
                    pastedObjects.add(copy);
                }
            }
        }
        if (pastedObjects.isEmpty()) {
            return;
        }
        selectedObject = pastedObjects.getFirst();
        selectedObjectIds.clear();
        pastedObjects.forEach(object -> selectedObjectIds.add(object.id()));
        refreshGroupFilters();
        refreshObjectList();
        redrawMap();
        refreshSummary();
        refreshDetails();
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
            tentCopy.setPreset(tent.preset());
            copy = tentCopy;
        } else if (original instanceof PowerSource source) {
            PowerSource sourceCopy = original instanceof DistributionPanel
                    ? new DistributionPanel(planFactory.newId(), copyName, copyPosition)
                    : new PowerSource(planFactory.newId(), copyName, copyPosition);
            sourceCopy.setColorHex(source.colorHex());
            sourceCopy.setSizePixels(source.sizePixels());
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
            textCopy.setTextOpacity(textObject.textOpacity());
            textCopy.setSourceObjectId(textObject.sourceObjectId());
            textCopy.setSyncSourceNotes(textObject.syncSourceNotes());
            textCopy.setShowReferenceLine(textObject.showReferenceLine());
            textCopy.setSourceType(textObject.sourceType());
            textCopy.setReferenceLineSourceOffset(textObject.referenceLineSourceOffset());
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
        } else if (original instanceof FenceRow fenceRow) {
            FenceRow fenceCopy = new FenceRow(planFactory.newId(), copyName, copyPosition);
            fenceCopy.setSegmentCount(fenceRow.segmentCount());
            fenceCopy.setSegmentLengthMeters(fenceRow.segmentLengthMeters());
            fenceCopy.setRotationDegrees(fenceRow.rotationDegrees());
            fenceCopy.setColorHex(fenceRow.colorHex());
            fenceCopy.setWidthPixels(fenceRow.widthPixels());
            fenceCopy.setShowInventoryLabel(fenceRow.showInventoryLabel());
            if (fenceRow.customInventoryLabelPosition()) {
                fenceCopy.setInventoryLabelOffset(fenceRow.inventoryLabelOffset());
            }
            copy = fenceCopy;
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
        copyInventory(original, copy);
        return copy;
    }

    private void copyInventory(PlannerObject original, PlannerObject copy) {
        if (!(original instanceof InventoryContainer originalContainer)
                || !(copy instanceof InventoryContainer copyContainer)) {
            return;
        }
        originalContainer.inventoryItems().forEach(item -> copyContainer.addInventoryItem(
                new InventoryItem(item.name(), item.quantity(), item.notes())
        ));
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
        copy.setOpacity(original.opacity());
        copy.setRotationDegrees(original.rotationDegrees());
        copy.setLocked(false);
        if (original instanceof TextObject originalText && copy instanceof TextObject copiedText) {
            copiedText.setTextOpacity(originalText.textOpacity());
        }
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
        List<PlannerObject> objectsToDelete = selectedObjects();
        boolean lockedSelection = objectsToDelete.stream().anyMatch(this::isObjectEffectivelyLocked);
        if (lockedSelection) {
            showError("Objekti ei kustutatud", "Eemalda enne lukustus ja proovi uuesti.");
            return;
        }
        if (!confirmDeleteSelectedObject()) {
            return;
        }

        deleteSelectedMeasurementPaths();
        objectsToDelete.stream().map(PlannerObject::id).toList().forEach(plan::removeObject);
        selectedObject = null;
        selectedObjectIds.clear();
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
        List<PlannerObject> logicalSelection = selectedLogicalObjects();
        alert.setHeaderText(logicalSelection.size() > 1
                ? "Kas kustutada %d valitud objekti?".formatted(logicalSelection.size())
                : "Kas kustutada \"%s\"?".formatted(selectedObject.name()));
        alert.setContentText(logicalSelection.size() > 1
                ? "Valitud objektid ja nendega seotud andmed kustutatakse. Kustutamise saab tagasi võtta käsuga Ctrl+Z."
                : deleteConfirmationText(selectedObject));
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private String deleteConfirmationText(PlannerObject object) {
        List<String> warnings = new ArrayList<>();
        if (object instanceof FenceRow fenceRow) {
            int partCount = plan.fenceNetworkRows(fenceRow.id()).size();
            warnings.add("Kogu ühendatud aiarada (%d osa) kustutatakse.".formatted(partCount));
        }
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
            return "Kustutamise saab tagasi võtta käsuga Ctrl+Z.";
        }
        return "%s%n%nKustutamise saab tagasi võtta käsuga Ctrl+Z."
                .formatted(String.join(System.lineSeparator(), warnings));
    }

    private void setMeasuringActive(boolean measuringActive) {
        if (measuringActive) {
            cancelMeasurementPreview();
        } else {
            finishActiveMeasurementPath();
            finishEditingMeasurementPath();
        }
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
        updateMapToolStatus();
    }

    private void setAddingCablePoint(boolean addingCablePoint) {
        this.addingCablePoint = addingCablePoint;
        if (!addingCablePoint) {
            editingCableConnectionId = null;
        }
        if (addingCablePoint) {
            finishActiveMeasurementPath();
            finishEditingMeasurementPath();
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
        if (!addingCablePoint) {
            cancelMeasurementPreview();
        }
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
                placementTypeComboBox.getSelectionModel().select(
                        pendingTentPlacementType == null ? PlacementType.TENT : pendingTentPlacementType
                );
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
            } else if (pendingFenceRowPlacement) {
                placementTypeComboBox.getSelectionModel().select(PlacementType.FENCE_ROW);
            } else if (pendingFenceRingPlacement) {
                placementTypeComboBox.getSelectionModel().select(PlacementType.FENCE_RING);
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
                || pendingFenceRowPlacement
                || pendingFenceRingPlacement
                || pendingAreaObjectPlacement;
    }

    private void cancelPlacement() {
        pendingTentPlacement = false;
        pendingTentPlacementType = null;
        pendingPowerSourcePlacement = false;
        pendingCustomObjectPlacement = false;
        pendingTextObjectPlacement = false;
        pendingMarkerPlacement = false;
        pendingLineObjectPlacement = false;
        pendingFenceRowPlacement = false;
        pendingFenceRingPlacement = false;
        pendingFenceStartJointId = null;
        pendingFenceTemplateRowId = null;
        pendingAreaObjectPlacement = false;
        pendingPowerSourceConsumer = null;
        clearPendingPlacementDetails();
        refreshPlacementButtons();
        updateMapToolStatus();
        redrawMap();
    }

    private void addMeasurementPoint(Position point) {
        if (activeMeasurementPath == null) {
            Circle startMarker = createMeasurementMarker(point);
            Label totalLabel = createMeasurementLabel("Kokku 0.00 m");
            positionMeasurementTotalLabel(totalLabel, point);
            activeMeasurementPath = new MeasurementPathView(
                    new ArrayList<>(List.of(point)),
                    new ArrayList<>(),
                    new ArrayList<>(List.of(startMarker, totalLabel)),
                    new ArrayList<>(List.of(startMarker)),
                    totalLabel
            );
            measurementNodes.addAll(List.of(startMarker, totalLabel));
            mapPane.getChildren().addAll(startMarker, totalLabel);
            startMeasurementPreview(point);
            updateMapToolStatus();
            return;
        }

        if (measurementStart == null
                || distancePixels(measurementStart, point) < MAP_CLICK_DRAG_TOLERANCE_PX / mapScale.getX()) {
            return;
        }
        updateMeasurementPreview(point);
        MeasurementView segment = new MeasurementView(
                measurementStart,
                point,
                measurementPreviewLine,
                measurementPreviewLabel
        );
        measurements.add(segment);
        activeMeasurementPath.points().add(point);
        activeMeasurementPath.segments().add(segment);
        activeMeasurementPath.nodes().addAll(List.of(
                measurementPreviewLine,
                measurementPreviewEndMarker,
                measurementPreviewLabel
        ));
        activeMeasurementPath.pointMarkers().add(measurementPreviewEndMarker);
        updateMeasurementTotal(activeMeasurementPath);
        clearMeasurementPreviewReferences();
        startMeasurementPreview(point);
        updateMapToolStatus();
    }

    private void startMeasurementPreview(Position start) {
        measurementStart = start;
        measurementPreviewLine = new Line(start.x(), start.y(), start.x(), start.y());
        measurementPreviewLine.setStroke(Color.web("#111827"));
        measurementPreviewLine.setStrokeWidth(2);
        measurementPreviewEndMarker = createMeasurementMarker(start);
        measurementPreviewLabel = createMeasurementLabel("0.00 m");
        measurementPreviewLabel.setVisible(false);
        measurementNodes.addAll(List.of(
                measurementPreviewLine,
                measurementPreviewEndMarker,
                measurementPreviewLabel
        ));
        mapPane.getChildren().addAll(
                measurementPreviewLine,
                measurementPreviewEndMarker,
                measurementPreviewLabel
        );
    }

    private Label createMeasurementLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-background-color: white; -fx-padding: 2 4 2 4;");
        return label;
    }

    private void updateMeasurementPreview(Position end) {
        if (measurementStart == null || measurementPreviewLine == null) {
            return;
        }
        measurementPreviewLine.setEndX(end.x());
        measurementPreviewLine.setEndY(end.y());
        measurementPreviewEndMarker.setCenterX(end.x());
        measurementPreviewEndMarker.setCenterY(end.y());
        measurementPreviewLabel.setVisible(true);
        measurementPreviewLabel.setText("%.2f m".formatted(distanceMeters(measurementStart, end)));
        measurementPreviewLabel.setLayoutX((measurementStart.x() + end.x()) / 2 + 6);
        measurementPreviewLabel.setLayoutY((measurementStart.y() + end.y()) / 2 + 6);
        if (activeMeasurementPath != null) {
            double totalMeters = activeMeasurementPath.segments().stream()
                    .mapToDouble(segment -> distanceMeters(segment.start(), segment.end()))
                    .sum() + distanceMeters(measurementStart, end);
            activeMeasurementPath.totalLabel().setText("Kokku %.2f m".formatted(totalMeters));
            positionMeasurementTotalLabel(activeMeasurementPath.totalLabel(), end);
            activeMeasurementPath.totalLabel().toFront();
        }
    }

    private void finishActiveMeasurementPath() {
        cancelMeasurementPreview();
        if (activeMeasurementPath == null) {
            return;
        }
        if (activeMeasurementPath.segments().isEmpty()) {
            measurementNodes.removeAll(activeMeasurementPath.nodes());
            mapPane.getChildren().removeAll(activeMeasurementPath.nodes());
            activeMeasurementPath = null;
            updateMapToolStatus();
            return;
        }
        updateMeasurementTotal(activeMeasurementPath);
        MeasurementPathView completedPath = activeMeasurementPath;
        measurementPaths.add(completedPath);
        completedPath.nodes().forEach(node -> {
            node.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    selectMeasurementPath(completedPath, event.isControlDown());
                    event.consume();
                }
            });
            node.setOnContextMenuRequested(event -> {
                ContextMenu menu = new ContextMenu();
                MenuItem editRouteItem = new MenuItem("Muuda trajektoori");
                editRouteItem.setOnAction(actionEvent -> startEditingMeasurementPath(completedPath));
                MenuItem removeItem = new MenuItem("Eemalda mõõdulint");
                removeItem.setOnAction(actionEvent -> removeMeasurementPath(completedPath));
                menu.getItems().addAll(editRouteItem, removeItem);
                menu.show(node, event.getScreenX(), event.getScreenY());
                event.consume();
            });
        });
        configureMeasurementPointDragging(completedPath);
        activeMeasurementPath = null;
        updateMapToolStatus();
    }

    private void finishMeasurementTool() {
        finishActiveMeasurementPath();
        finishEditingMeasurementPath();
        measuringActive = false;
        if (measureButton != null) {
            measureButton.setSelected(false);
        }
        updateMapToolStatus();
    }

    private void finishEditingMeasurementPath() {
        if (editingMeasurementPath == null) {
            return;
        }
        editingMeasurementPath.pointMarkers().forEach(marker -> {
            marker.setRadius(4);
            marker.setFill(Color.web("#111827"));
            marker.setStroke(Color.WHITE);
            marker.setStrokeWidth(1);
        });
        editingMeasurementPath = null;
        mapScrollPane.setPannable(true);
    }

    private void startEditingMeasurementPath(MeasurementPathView path) {
        if (activeMeasurementPath != null) {
            finishActiveMeasurementPath();
        }
        editingMeasurementPath = path;
        measuringActive = true;
        if (measureButton != null) {
            measureButton.setSelected(true);
        }
        path.pointMarkers().forEach(marker -> {
            marker.setRadius(6 / Math.max(zoomLevel, 0.1));
            marker.setFill(Color.web("#ffffff"));
            marker.setStroke(Color.web("#2563eb"));
            marker.toFront();
        });
        updateMapToolStatus();
    }

    private void configureMeasurementPointDragging(MeasurementPathView path) {
        for (int index = 0; index < path.pointMarkers().size(); index++) {
            int pointIndex = index;
            Circle marker = path.pointMarkers().get(index);
            marker.setOnMousePressed(event -> {
                if (editingMeasurementPath == path && event.getButton() == MouseButton.PRIMARY) {
                    mapScrollPane.setPannable(false);
                    event.consume();
                }
            });
            marker.setOnMouseDragged(event -> {
                if (editingMeasurementPath != path) {
                    return;
                }
                Point2D mapPoint = mapPane.sceneToLocal(event.getSceneX(), event.getSceneY());
                path.points().set(pointIndex, new Position(mapPoint.getX(), mapPoint.getY()));
                updateMeasurementPathGeometry(path);
                event.consume();
            });
            marker.setOnMouseReleased(event -> {
                if (editingMeasurementPath == path) {
                    mapScrollPane.setPannable(true);
                    event.consume();
                }
            });
        }
    }

    private void updateMeasurementPathGeometry(MeasurementPathView path) {
        for (int index = 0; index < path.points().size(); index++) {
            Position point = path.points().get(index);
            Circle marker = path.pointMarkers().get(index);
            marker.setCenterX(point.x());
            marker.setCenterY(point.y());
        }
        for (int index = 0; index < path.segments().size(); index++) {
            MeasurementView oldSegment = path.segments().get(index);
            Position start = path.points().get(index);
            Position end = path.points().get(index + 1);
            oldSegment.line().setStartX(start.x());
            oldSegment.line().setStartY(start.y());
            oldSegment.line().setEndX(end.x());
            oldSegment.line().setEndY(end.y());
            oldSegment.distanceLabel().setText("%.2f m".formatted(distanceMeters(start, end)));
            oldSegment.distanceLabel().setLayoutX((start.x() + end.x()) / 2 + 6);
            oldSegment.distanceLabel().setLayoutY((start.y() + end.y()) / 2 + 6);
            path.segments().set(index, new MeasurementView(
                    start,
                    end,
                    oldSegment.line(),
                    oldSegment.distanceLabel()
            ));
        }
        rebuildMeasurementSegments();
        updateMeasurementTotal(path);
    }

    private void rebuildMeasurementSegments() {
        measurements.clear();
        measurementPaths.forEach(path -> measurements.addAll(path.segments()));
        if (activeMeasurementPath != null) {
            measurements.addAll(activeMeasurementPath.segments());
        }
    }

    private void removeMeasurementPath(MeasurementPathView path) {
        if (editingMeasurementPath == path) {
            editingMeasurementPath = null;
        }
        measurementPaths.remove(path);
        selectedMeasurementPaths.remove(path);
        measurements.removeAll(path.segments());
        measurementNodes.removeAll(path.nodes());
        redrawMap();
    }

    private void selectMeasurementPath(MeasurementPathView path, boolean toggle) {
        if (toggle) {
            if (selectedMeasurementPaths.contains(path)) {
                selectedMeasurementPaths.remove(path);
            } else {
                selectedMeasurementPaths.add(path);
            }
        } else {
            selectedObject = null;
            selectedObjectIds.clear();
            selectionRangeAnchorObjectId = null;
            selectedMeasurementPaths.clear();
            selectedMeasurementPaths.add(path);
            refreshDetails();
            refreshObjectList();
        }
        redrawMap();
    }

    private void deleteSelectedMeasurementPaths() {
        List.copyOf(selectedMeasurementPaths).forEach(this::removeMeasurementPath);
        selectedMeasurementPaths.clear();
    }

    private void updateMeasurementTotal(MeasurementPathView path) {
        double totalMeters = path.segments().stream()
                .mapToDouble(segment -> distanceMeters(segment.start(), segment.end()))
                .sum();
        path.totalLabel().setText("Kokku %.2f m".formatted(totalMeters));
        positionMeasurementTotalLabel(path.totalLabel(), path.points().getLast());
        path.totalLabel().toFront();
    }

    private void positionMeasurementTotalLabel(Label label, Position point) {
        label.setLayoutX(point.x() + 10);
        label.setLayoutY(point.y() + 10);
    }

    private void cancelMeasurementPreview() {
        if (measurementPreviewLine != null) {
            List<Node> previewNodes = List.of(
                    measurementPreviewLine,
                    measurementPreviewEndMarker,
                    measurementPreviewLabel
            );
            measurementNodes.removeAll(previewNodes);
            if (mapPane != null) {
                mapPane.getChildren().removeAll(previewNodes);
            }
        }
        clearMeasurementPreviewReferences();
    }

    private void clearMeasurementPreviewReferences() {
        measurementStart = null;
        measurementPreviewLine = null;
        measurementPreviewEndMarker = null;
        measurementPreviewLabel = null;
    }

    private void refreshMeasurementLabels() {
        for (MeasurementView measurement : measurements) {
            measurement.distanceLabel().setText("%.2f m".formatted(distanceMeters(measurement.start(), measurement.end())));
        }
        measurementPaths.forEach(this::updateMeasurementTotal);
        if (activeMeasurementPath != null) {
            updateMeasurementTotal(activeMeasurementPath);
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
        dialog.setHeaderText("Sisesta viimase mõõdulindi lõigu tegelik pikkus meetrites");
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

    private double screenPixels(double pixels) {
        return pixels / Math.max(zoomLevel, 0.1);
    }

    private double adaptiveMapPixels(double basePixels) {
        double densityScale = Math.max(1.0, pixelsPerMeter() / EventPlan.DEFAULT_PIXELS_PER_METER);
        double naturalScreenSize = basePixels * densityScale * Math.max(zoomLevel, 0.1);
        double desiredScreenSize = clamp(naturalScreenSize, basePixels, basePixels * 1.6);
        return screenPixels(desiredScreenSize);
    }

    private void clearMeasurements() {
        cancelMeasurementPreview();
        measurementNodes.clear();
        measurements.clear();
        measurementPaths.clear();
        selectedMeasurementPaths.clear();
        activeMeasurementPath = null;
        editingMeasurementPath = null;
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

    private String cssRgba(String colorHex, double opacity) {
        Color color = Color.web(colorHex);
        return "rgba(%d,%d,%d,%.3f)".formatted(
                Math.round(color.getRed() * 255),
                Math.round(color.getGreen() * 255),
                Math.round(color.getBlue() * 255),
                Math.max(0, Math.min(1, opacity))
        );
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

    private ListCell<InventoryItem> createObjectInventoryListCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(InventoryItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setContextMenu(null);
                    return;
                }
                Label label = new Label("%s: %d tk%s".formatted(
                        item.name(), item.quantity(),
                        item.notes().isBlank() ? "" : " · " + item.notes()
                ));
                label.setWrapText(true);
                Button decreaseButton = new Button("−");
                decreaseButton.setDisable(item.quantity() == 0);
                decreaseButton.setOnAction(event -> adjustSelectedObjectInventoryItem(item, -1));
                Button increaseButton = new Button("+");
                increaseButton.setOnAction(event -> adjustSelectedObjectInventoryItem(item, 1));
                HBox row = new HBox(6, label, decreaseButton, increaseButton);
                row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                HBox.setHgrow(label, Priority.ALWAYS);
                setText(null);
                setGraphic(row);
                MenuItem editItem = new MenuItem("Muuda");
                editItem.setOnAction(event -> showObjectInventoryDialog(item));
                MenuItem removeItem = new MenuItem("Eemalda");
                removeItem.setOnAction(event -> removeObjectInventoryItem(item));
                setContextMenu(new ContextMenu(editItem, removeItem));
                setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                        showObjectInventoryDialog(item);
                    }
                });
            }
        };
    }

    private void showObjectInventoryDialog(InventoryItem existingItem) {
        InventoryContainer container = selectedInventoryContainer();
        if (container == null) {
            return;
        }
        ComboBox<String> nameBox = new ComboBox<>();
        nameBox.setEditable(true);
        nameBox.getItems().addAll("Telgiraskus", "Laud", "Pink");
        nameBox.getEditor().setText(existingItem == null ? "Telgiraskus" : existingItem.name());
        TextField quantityField = new TextField(
                Integer.toString(existingItem == null ? 1 : existingItem.quantity())
        );
        TextField itemNotesField = new TextField(existingItem == null ? "" : existingItem.notes());
        GridPane form = detailGrid();
        form.addRow(0, new Label("Nimetus"), nameBox);
        form.addRow(1, new Label("Kogus"), quantityField);
        form.addRow(2, new Label("Märkus"), itemNotesField);
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.initOwner(stage);
        dialog.setTitle(existingItem == null ? "Lisa inventar" : "Muuda inventari");
        dialog.setHeaderText(selectedObject.name());
        dialog.getDialogPane().setContent(form);
        if (dialog.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }
        String name = nameBox.getEditor().getText().trim();
        try {
            int quantity = Integer.parseInt(quantityField.getText().trim());
            if (name.isBlank() || quantity < 1) {
                throw new IllegalArgumentException("Sisesta nimetus ja vähemalt üks ese.");
            }
            if (existingItem == null) {
                container.addInventoryItem(new InventoryItem(name, quantity, itemNotesField.getText()));
            } else {
                existingItem.rename(name);
                existingItem.setQuantity(quantity);
                existingItem.setNotes(itemNotesField.getText());
            }
            refreshObjectInventoryList();
            refreshInventory();
            markDirty();
        } catch (NumberFormatException exception) {
            showError("Inventari ei muudetud", "Sisesta kogus täisarvuna.");
        } catch (IllegalArgumentException exception) {
            showError("Inventari ei muudetud", exception.getMessage());
        }
    }

    private void removeObjectInventoryItem(InventoryItem item) {
        InventoryContainer container = selectedInventoryContainer();
        if (container == null) {
            return;
        }
        int index = container.inventoryItems().indexOf(item);
        if (index >= 0) {
            container.removeInventoryItem(index);
            refreshObjectInventoryList();
            refreshInventory();
            markDirty();
        }
    }

    private void adjustSelectedObjectInventoryItem(InventoryItem item, int delta) {
        item.setQuantity(Math.max(0, item.quantity() + delta));
        refreshObjectInventoryList();
        refreshInventory();
        markDirty();
    }

    private void refreshObjectInventoryList() {
        objectInventoryList.getItems().clear();
        InventoryContainer container = selectedInventoryContainer();
        if (container != null) {
            objectInventoryList.getItems().addAll(container.inventoryItems());
        }
    }

    private InventoryContainer selectedInventoryContainer() {
        return selectedObject instanceof InventoryContainer container ? container : null;
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
        refreshInventory();
        if (organizerView) {
            return;
        }
        for (PowerHierarchyService.SourceRow source : powerHierarchyService.summarize(plan).sources()) {
            String hierarchyKey = powerSourceSummaryKey(source.id());
            boolean expanded = isSummaryItemExpanded(hierarchyKey);
            summaryList.getItems().add(SummaryListItem.expandableLoad("%s: %d W kasutusel, %s".formatted(
                    source.name(),
                    source.usedWatts(),
                    remainingWattsText(source.remainingWatts())
            ), source.usedWatts(), source.capacityWatts(), source.id(), hierarchyKey, expanded, 0));
            if (expanded) {
                addConnectedConsumers(source);
            }
        }
    }

    private void refreshInventory() {
        if (inventoryContent == null) {
            return;
        }
        inventoryContent.getChildren().clear();
        InventorySummaryService.Summary summary = inventorySummaryService.summarize(plan);
        addFenceInventory(summary);
        addGardenStoneInventory(summary);
        addObjectInventory(summary);
        addStandaloneInventoryButton();
        if (!organizerView) {
            addCableInventory();
        }
        if (inventoryContent.getChildren().isEmpty()) {
            inventoryContent.getChildren().add(new Label("Inventariobjekte pole"));
        }
        if (mapPane != null) {
            redrawMap();
        }
    }

    private void addFenceInventory(InventorySummaryService.Summary inventory) {
        List<InventoryItem> standaloneFences = plan.standaloneInventoryItems().stream()
                .filter(item -> InventoryItemNames.isFence(item.name()))
                .toList();
        if (inventory.fences().totalCount() == 0 && standaloneFences.isEmpty()) {
            return;
        }
        VBox details = new VBox(3);
        for (var network : inventory.fences().byNetwork()) {
            FenceRow row = plan.findObject(network.representativeId()).filter(FenceRow.class::isInstance)
                    .map(FenceRow.class::cast)
                    .orElse(null);
            Label networkLabel = inventoryDetailLabel("%s%s: %d aeda · %s m".formatted(
                    network.name(), row != null && fenceNetworkHidden(row) ? " (peidetud)" : "",
                    network.count(), formatMeters(network.totalLengthMeters())
            ) + inventoryNotesSuffix(row == null ? "" : row.notes()));
            if (row != null) {
                attachInventoryContextMenu(networkLabel, inventoryFenceContextMenu(row));
            }
            details.getChildren().add(networkLabel);
        }
        standaloneFences.forEach(item -> details.getChildren().add(standaloneInventoryRow(item)));
        inventoryContent.getChildren().add(inventoryPane(
                "Aiad: %d tk · %s m kaardil".formatted(
                        inventory.totalFenceCount(),
                        formatMeters(inventory.fences().totalLengthMeters())
                ),
                details,
                fenceInventoryExpanded,
                expanded -> fenceInventoryExpanded = expanded
        ));
    }

    private void addGardenStoneInventory(InventorySummaryService.Summary inventory) {
        List<InventoryItem> standaloneGardenStones = plan.standaloneInventoryItems().stream()
                .filter(item -> InventoryItemNames.isGardenStone(item.name()))
                .toList();
        if (inventory.fenceStoneNetworks().isEmpty() && standaloneGardenStones.isEmpty()) {
            return;
        }
        VBox details = new VBox(8);
        for (InventorySummaryService.FenceStoneNetwork stones : inventory.fenceStoneNetworks()) {
            Label stoneLabel = inventoryDetailLabel("Aiakivid: %d automaatne · %s parandus · %d kokku".formatted(
                    stones.automaticCount(), signedCount(stones.adjustment()), stones.totalCount()
            ));
            Button removeStone = new Button("−");
            removeStone.setDisable(stones.totalCount() == 0);
            removeStone.setTooltip(new Tooltip("Vähenda selle aiakogumiku aiakivide kogust"));
            removeStone.setOnAction(event -> adjustFenceNetworkGardenStones(stones, -1));
            Button addStone = new Button("+");
            addStone.setTooltip(new Tooltip("Suurenda selle aiakogumiku aiakivide kogust"));
            addStone.setOnAction(event -> adjustFenceNetworkGardenStones(stones, 1));
            HBox stoneRow = new HBox(6, stoneLabel, removeStone, addStone);
            stoneRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            HBox.setHgrow(stoneLabel, Priority.ALWAYS);
            FenceRow fenceRow = plan.findObject(stones.representativeId()).filter(FenceRow.class::isInstance)
                    .map(FenceRow.class::cast)
                    .orElse(null);
            Label networkNameLabel = inventoryDetailLabel("%s%s%s".formatted(
                    stones.name(), fenceRow != null && fenceNetworkHidden(fenceRow) ? " (peidetud)" : "",
                    inventoryNotesSuffix(fenceRow == null ? "" : fenceRow.notes())
            ));
            VBox networkDetails = new VBox(
                    2,
                    networkNameLabel,
                    stoneRow
            );
            if (fenceRow != null) {
                attachInventoryContextMenu(networkDetails, inventoryFenceContextMenu(fenceRow));
            }
            details.getChildren().add(networkDetails);
        }
        standaloneGardenStones.forEach(item -> details.getChildren().add(standaloneInventoryRow(item)));
        inventoryContent.getChildren().add(inventoryPane(
                "Aiakivid: %d tk".formatted(inventory.gardenStoneCount()),
                details,
                gardenStoneInventoryExpanded,
                expanded -> gardenStoneInventoryExpanded = expanded
        ));
    }

    private void addObjectInventory(InventorySummaryService.Summary inventory) {
        List<Tent> tents = plan.objects().stream()
                .filter(Tent.class::isInstance)
                .map(Tent.class::cast)
                .toList();
        List<InventoryItem> standaloneTents = plan.standaloneInventoryItems().stream()
                .filter(item -> InventoryItemNames.isTent(item.name()))
                .toList();
        if (!tents.isEmpty() || !standaloneTents.isEmpty()) {
            VBox details = new VBox(3);
            tents.forEach(tent -> {
                Label tentLabel = inventoryDetailLabel("%s%s: %s × %s m%s".formatted(
                        tent.name(), hiddenInventorySuffix(tent),
                        formatMeters(tent.widthMeters()), formatMeters(tent.heightMeters()),
                        tent.notes().isBlank() ? "" : " · " + tent.notes()
                ));
                HBox tentRow = new HBox(6, tentLabel);
                tentRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                HBox.setHgrow(tentLabel, Priority.ALWAYS);
                attachInventoryContextMenu(tentRow, inventoryObjectContextMenu(tent));
                details.getChildren().add(tentRow);
            });
            standaloneTents.forEach(item -> details.getChildren().add(standaloneInventoryRow(item)));
            inventoryContent.getChildren().add(inventoryPane(
                    "Telgid: %d tk".formatted(tents.size() + standaloneTentCount()),
                    details,
                    tentsInventoryExpanded,
                    expanded -> tentsInventoryExpanded = expanded
            ));
        }
        Map<String, List<InventoryItem>> standaloneItemsByName = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (InventoryItem item : plan.standaloneInventoryItems()) {
            if (!item.name().isBlank()) {
                standaloneItemsByName.computeIfAbsent(item.name(), ignored -> new ArrayList<>()).add(item);
            }
        }
        Map<String, InventorySummaryService.ObjectInventoryGroup> groupsByName =
                new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        inventory.objectInventoryGroups().forEach(group -> groupsByName.put(group.name(), group));
        Set<String> itemNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        itemNames.addAll(groupsByName.keySet());
        itemNames.addAll(standaloneItemsByName.keySet());
        itemNames.removeIf(InventoryItemNames::isBuiltInInventory);
        itemNames.forEach(itemName -> {
            InventorySummaryService.ObjectInventoryGroup group = groupsByName.get(itemName);
            List<InventoryItem> standaloneItems = standaloneItemsByName.getOrDefault(itemName, List.of());
            VBox details = new VBox(3);
            if (group != null) {
                group.contributions().forEach(contribution -> {
                PlannerObject contributionObject = plan.findObject(contribution.objectId()).orElse(null);
                Label contributionLabel = inventoryDetailLabel("%s%s (%s): %d tk%s".formatted(
                        contribution.objectName(), hiddenInventorySuffix(contributionObject),
                        contribution.objectType(), contribution.quantity(),
                        contribution.notes().isBlank() ? "" : " · " + contribution.notes()
                ));
                Button decreaseButton = new Button("−");
                decreaseButton.setOnAction(event -> adjustObjectInventoryContribution(contribution, -1));
                Button increaseButton = new Button("+");
                increaseButton.setOnAction(event -> adjustObjectInventoryContribution(contribution, 1));
                HBox contributionRow = new HBox(6, contributionLabel, decreaseButton, increaseButton);
                contributionRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                HBox.setHgrow(contributionLabel, Priority.ALWAYS);
                attachInventoryContextMenu(contributionRow, inventoryContributionContextMenu(contribution));
                details.getChildren().add(contributionRow);
                });
            }
            standaloneItems.forEach(item -> details.getChildren().add(standaloneInventoryRow(item)));
            int objectCount = group == null ? 0 : group.totalCount();
            int standaloneCount = standaloneItems.stream().mapToInt(InventoryItem::quantity).sum();
            String groupKey = itemName.toLowerCase(java.util.Locale.ROOT);
            TitledPane pane = new TitledPane(
                    "%s: %d tk".formatted(itemName, objectCount + standaloneCount),
                    details
            );
            pane.setExpanded(expandedObjectInventoryKeys.contains(groupKey));
            pane.expandedProperty().addListener((observable, oldValue, expanded) -> {
                if (expanded) {
                    expandedObjectInventoryKeys.add(groupKey);
                } else {
                    expandedObjectInventoryKeys.remove(groupKey);
                }
            });
            pane.setAnimated(false);
            pane.getProperties().put(INVENTORY_SUMMARY_KEY_PROPERTY, "item:" + itemName);
            configureInventoryTextAction(pane);
            inventoryContent.getChildren().add(pane);
        });
    }

    private void addStandaloneInventoryButton() {
        Button addItem = new Button("+ Lisa inventar");
        addItem.setOnAction(event -> showStandaloneInventoryDialog(null));
        inventoryContent.getChildren().add(addItem);
    }

    private HBox standaloneInventoryRow(InventoryItem item) {
        Label itemLabel = inventoryDetailLabel("Lisainventar: %d tk%s".formatted(
                item.quantity(), inventoryNotesSuffix(item.notes())
        ));
        Button decreaseButton = new Button("−");
        decreaseButton.setDisable(item.quantity() == 0);
        decreaseButton.setOnAction(event -> adjustStandaloneInventoryItem(item, -1));
        Button increaseButton = new Button("+");
        increaseButton.setOnAction(event -> adjustStandaloneInventoryItem(item, 1));
        MenuItem editItem = new MenuItem("Muuda");
        editItem.setOnAction(event -> showStandaloneInventoryDialog(item));
        MenuItem removeItem = new MenuItem("Eemalda");
        removeItem.setOnAction(event -> removeStandaloneInventoryItem(item));
        HBox itemRow = new HBox(6, itemLabel, decreaseButton, increaseButton);
        itemRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        HBox.setHgrow(itemLabel, Priority.ALWAYS);
        attachInventoryContextMenu(itemRow, new ContextMenu(editItem, removeItem));
        return itemRow;
    }

    private ContextMenu inventoryObjectContextMenu(PlannerObject object) {
        MenuItem noteItem = new MenuItem("Muuda märkust");
        noteItem.setOnAction(event -> showInventoryObjectNoteDialog(object));
        MenuItem notesAsTextItem = inventoryNotesAsTextMenuItem(object, object.name(), object.notes());
        MenuItem inventoryAsTextItem = inventoryAsTextMenuItem(object);
        MenuItem revealItem = new MenuItem("Kuva kaardil");
        revealItem.setOnAction(event -> showInventoryObjectOnMap(object));
        return new ContextMenu(noteItem, notesAsTextItem, inventoryAsTextItem, revealItem);
    }

    private ContextMenu inventoryContributionContextMenu(InventorySummaryService.ObjectInventoryContribution contribution) {
        MenuItem noteItem = new MenuItem("Muuda märkust");
        noteItem.setOnAction(event -> showInventoryContributionNoteDialog(contribution));
        PlannerObject sourceObject = plan.findObject(contribution.objectId()).orElse(null);
        MenuItem notesAsTextItem = inventoryNotesAsTextMenuItem(
                sourceObject, contribution.objectName(), contribution.notes()
        );
        MenuItem inventoryAsTextItem = inventoryAsTextMenuItem(sourceObject);
        MenuItem revealItem = new MenuItem("Kuva kaardil");
        revealItem.setOnAction(event -> plan.findObject(contribution.objectId())
                .ifPresent(this::showInventoryObjectOnMap));
        return new ContextMenu(noteItem, notesAsTextItem, inventoryAsTextItem, revealItem);
    }

    private ContextMenu inventoryFenceContextMenu(FenceRow fenceRow) {
        MenuItem noteItem = new MenuItem("Muuda märkust");
        noteItem.setOnAction(event -> showFenceInventoryNoteDialog(fenceRow));
        MenuItem notesAsTextItem = inventoryNotesAsTextMenuItem(fenceRow, fenceRow.name(), fenceRow.notes());
        MenuItem revealItem = new MenuItem("Kuva kaardil");
        revealItem.setOnAction(event -> showInventoryObjectOnMap(fenceRow));
        return new ContextMenu(noteItem, notesAsTextItem, revealItem);
    }

    private MenuItem inventoryNotesAsTextMenuItem(PlannerObject sourceObject, String title, String notes) {
        MenuItem item = new MenuItem("Loo märkmetest tekstiobjekt");
        item.setDisable(notes == null || notes.isBlank());
        item.setOnAction(event -> {
            if (sourceObject == null) {
                createStaticTextObject(title, notes);
            } else {
                showNotesTextObjectDialog(sourceObject);
            }
        });
        return item;
    }

    private MenuItem inventoryAsTextMenuItem(PlannerObject object) {
        MenuItem item = new MenuItem("Loo inventarist tekstiobjekt");
        if (!(object instanceof InventoryContainer container) || container.inventoryItems().isEmpty()) {
            item.setDisable(true);
            return item;
        }
        item.setOnAction(event -> showInventoryTextObjectDialog(object, container));
        return item;
    }

    private MenuItem powerOutletsAsTextMenuItem(PlannerObject object) {
        MenuItem item = new MenuItem("Loo väljunditest tekstiobjekt");
        if (!(object instanceof PowerSource source) || source.outlets().isEmpty()) {
            item.setDisable(true);
            return item;
        }
        item.setOnAction(event -> showPowerOutletsTextObjectDialog(source));
        return item;
    }

    private String powerOutletsText(PowerSource source) {
        Map<ConnectorType, Integer> typeIndexes = new EnumMap<>(ConnectorType.class);
        List<String> lines = new ArrayList<>();
        for (PowerOutlet outlet : source.outlets()) {
            int index = typeIndexes.merge(outlet.type(), 1, Integer::sum);
            String name = outlet.name().isBlank() ? "" : " — " + outlet.name();
            lines.add("%s %d%s: %d / %d W".formatted(
                    outlet.type().displayName(),
                    index,
                    name,
                    plan.outletDemandWatts(outlet.id()),
                    outlet.capacityWatts()
            ));
        }
        return String.join(System.lineSeparator(), lines);
    }

    private String objectInventoryText(InventoryContainer container) {
        return container.inventoryItems().stream()
                .map(item -> "%s: %d tk%s".formatted(
                        item.name(),
                        item.quantity(),
                        item.notes().isBlank() ? "" : " · " + item.notes()
                ))
                .collect(java.util.stream.Collectors.joining(System.lineSeparator()));
    }

    private ContextMenu cableInventoryContextMenu(CableInventorySummaryService.Row row) {
        MenuItem noteItem = new MenuItem("Muuda märkust");
        noteItem.setOnAction(event -> showCableInventoryNoteDialog(row));
        MenuItem piecesItem = new MenuItem("Muuda kaablitükke");
        piecesItem.setOnAction(event -> showCableInventoryLengthNotesDialog(row));
        MenuItem revealItem = new MenuItem("Kuva kaardil");
        revealItem.setOnAction(event -> showCableInventoryOnMap(row));
        return new ContextMenu(noteItem, piecesItem, revealItem);
    }

    private void attachInventoryContextMenu(Node row, ContextMenu menu) {
        row.setOnContextMenuRequested(event -> {
            showContextMenu(menu, row, event.getScreenX(), event.getScreenY());
            event.consume();
        });
    }

    private void showInventoryObjectNoteDialog(PlannerObject object) {
        TextInputDialog dialog = new TextInputDialog(object.notes());
        dialog.initOwner(stage);
        dialog.setTitle("Objekti märkus");
        dialog.setHeaderText(object.name());
        dialog.setContentText("Märkus");
        dialog.showAndWait().ifPresent(notes -> {
            object.setNotes(notes);
            refreshInventory();
            refreshDetails();
            markDirty();
        });
    }

    private void showInventoryContributionNoteDialog(InventorySummaryService.ObjectInventoryContribution contribution) {
        plan.findObject(contribution.objectId())
                .filter(InventoryContainer.class::isInstance)
                .map(InventoryContainer.class::cast)
                .filter(container -> contribution.itemIndex() >= 0
                        && contribution.itemIndex() < container.inventoryItems().size())
                .ifPresent(container -> {
                    InventoryItem item = container.inventoryItems().get(contribution.itemIndex());
                    TextInputDialog dialog = new TextInputDialog(item.notes());
                    dialog.initOwner(stage);
                    dialog.setTitle("Inventari märkus");
                    dialog.setHeaderText("%s · %s".formatted(contribution.objectName(), item.name()));
                    dialog.setContentText("Märkus");
                    dialog.showAndWait().ifPresent(notes -> {
                        item.setNotes(notes);
                        refreshInventory();
                        refreshObjectInventoryList();
                        markDirty();
                    });
                });
    }

    private void showFenceInventoryNoteDialog(FenceRow fenceRow) {
        TextInputDialog dialog = new TextInputDialog(fenceRow.notes());
        dialog.initOwner(stage);
        dialog.setTitle("Aia märkus");
        dialog.setHeaderText(fenceRow.name());
        dialog.setContentText("Märkus");
        dialog.showAndWait().ifPresent(notes -> {
            plan.fenceNetworkRows(fenceRow.id()).forEach(row -> row.setNotes(notes));
            refreshInventory();
            refreshDetails();
            markDirty();
        });
    }

    private void showCableInventoryNoteDialog(CableInventorySummaryService.Row row) {
        PowerConnection connection = plan.findPowerConnection(row.connectionId()).orElse(null);
        if (connection == null) {
            return;
        }
        showCableNoteDialog(connection, "%s → %s".formatted(row.consumerName(), row.sourceName()));
    }

    private void showCableInventoryLengthNotesDialog(CableInventorySummaryService.Row row) {
        PowerConnection connection = plan.findPowerConnection(row.connectionId()).orElse(null);
        if (connection == null) {
            return;
        }
        showCableLengthNotesDialog(connection, "%s → %s".formatted(row.consumerName(), row.sourceName()));
    }

    private void showCableNoteDialog(PowerConnection connection, String header) {
        TextInputDialog dialog = new TextInputDialog(connection.cableNotes());
        dialog.initOwner(stage);
        dialog.setTitle("Kaabli märkus");
        dialog.setHeaderText(header);
        dialog.setContentText("Märkus");
        dialog.showAndWait().ifPresent(notes -> {
            plan.updateCableNotesForConnection(connection.id(), notes);
            if (connection.id().equals(selectedPowerConnectionId())) {
                cableNotesField.setText(plan.findPowerConnection(connection.id())
                        .map(PowerConnection::cableNotes)
                        .orElse(""));
            }
            refreshSummary();
            redrawMap();
            markDirty();
        });
    }

    private void showCableLengthNotesDialog(PowerConnection connection, String header) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(stage);
        dialog.setTitle("Kaablitükid");
        dialog.setHeaderText(header);
        Map<Double, Integer> counts = cablePieceCounts(connection.cableLengthNotes());
        VBox editor = new VBox(6);
        Runnable[] refreshEditor = new Runnable[1];
        refreshEditor[0] = () -> populateCablePieceEditor(editor, counts, refreshEditor[0]);
        refreshEditor[0].run();
        ScrollPane editorScroll = new ScrollPane(editor);
        editorScroll.setFitToWidth(true);
        editorScroll.setPrefViewportHeight(330);
        editorScroll.setMinViewportHeight(180);
        dialog.getDialogPane().setContent(editorScroll);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        if (dialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            String lengthNotes = formatCablePieceCounts(counts);
            plan.updateCableLengthNotesForConnection(connection.id(), lengthNotes);
            if (connection.id().equals(selectedPowerConnectionId())) {
                cableLengthNotesField.setText(plan.findPowerConnection(connection.id())
                        .map(PowerConnection::cableLengthNotes)
                        .orElse(""));
            }
            refreshSelectedPowerConnectionFields();
            refreshSummary();
            redrawMap();
            markDirty();
        }
    }

    private String cableInventoryHeader(PowerConnection connection) {
        String consumerName = plan.findObject(connection.consumerId())
                .map(PlannerObject::name)
                .orElse("Puuduv tarbija");
        String sourceName = plan.findObject(connection.sourceId())
                .map(PlannerObject::name)
                .orElse("Puuduv vooluallikas");
        return "%s → %s".formatted(consumerName, sourceName);
    }

    private void showCableInventoryOnMap(CableInventorySummaryService.Row row) {
        PlannerObject consumer = plan.findObject(row.consumerId()).orElse(null);
        PowerConnection connection = plan.findPowerConnection(row.connectionId()).orElse(null);
        if (consumer == null || connection == null) {
            return;
        }
        selectObject(consumer);
        if (!isObjectVisibleOnMap(consumer)) {
            revealSelectedObjectOnMap();
        }
        showCablesButton.setSelected(true);
        switch (connection.connectorType()) {
            case SCHUKO_230V -> show230VCablesButton.setSelected(true);
            case INDUSTRIAL_16A -> show16ACablesButton.setSelected(true);
            case INDUSTRIAL_32A -> show32ACablesButton.setSelected(true);
            case INDUSTRIAL_63A -> show63ACablesButton.setSelected(true);
        }
        updateMapLayerVisibility();
        selectPowerConnection(connection.id());
        centerMapOnObject(consumer);
    }

    private int standaloneTentCount() {
        return plan.standaloneInventoryItems().stream()
                .filter(item -> InventoryItemNames.isTent(item.name()))
                .mapToInt(InventoryItem::quantity)
                .sum();
    }

    private String hiddenInventorySuffix(PlannerObject object) {
        return object != null && object.hidden() ? " (peidetud)" : "";
    }

    private String inventoryNotesSuffix(String notes) {
        return notes == null || notes.isBlank() ? "" : " · " + notes;
    }

    private boolean fenceNetworkHidden(FenceRow fenceRow) {
        return plan.fenceNetworkRows(fenceRow.id()).stream().allMatch(PlannerObject::hidden);
    }

    private void showInventoryObjectOnMap(PlannerObject object) {
        selectObject(object);
        if (!isObjectVisibleOnMap(object)) {
            revealSelectedObjectOnMap();
        }
        centerMapOnObject(object);
    }

    private void showStandaloneInventoryDialog(InventoryItem existingItem) {
        ComboBox<String> nameBox = new ComboBox<>();
        nameBox.setEditable(true);
        nameBox.getItems().addAll("Aed", "Aiakivi", "Telk", "Telgiraskus", "Laud", "Pink");
        nameBox.getEditor().setText(existingItem == null ? "Laud" : existingItem.name());
        TextField quantityField = new TextField(
                Integer.toString(existingItem == null ? 1 : existingItem.quantity())
        );
        TextField notesField = new TextField(existingItem == null ? "" : existingItem.notes());
        GridPane form = detailGrid();
        form.addRow(0, new Label("Nimetus"), nameBox);
        form.addRow(1, new Label("Kogus"), quantityField);
        form.addRow(2, new Label("Märkus"), notesField);
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.initOwner(stage);
        dialog.setTitle(existingItem == null ? "Lisa lisainventar" : "Muuda lisainventari");
        dialog.setHeaderText("Inventar, mida ei seota kaardiobjektiga");
        dialog.getDialogPane().setContent(form);
        if (dialog.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }
        String name = nameBox.getEditor().getText().trim();
        try {
            int quantity = Integer.parseInt(quantityField.getText().trim());
            if (name.isBlank() || quantity < 1) {
                throw new IllegalArgumentException("Sisesta nimetus ja vähemalt üks ese.");
            }
            if (existingItem == null) {
                plan.addStandaloneInventoryItem(new InventoryItem(name, quantity, notesField.getText()));
            } else {
                existingItem.rename(name);
                existingItem.setQuantity(quantity);
                existingItem.setNotes(notesField.getText());
            }
            refreshInventory();
            markDirty();
        } catch (NumberFormatException exception) {
            showError("Inventari ei muudetud", "Sisesta kogus täisarvuna.");
        } catch (IllegalArgumentException exception) {
            showError("Inventari ei muudetud", exception.getMessage());
        }
    }

    private void adjustStandaloneInventoryItem(InventoryItem item, int delta) {
        item.setQuantity(Math.max(0, item.quantity() + delta));
        refreshInventory();
        markDirty();
    }

    private void removeStandaloneInventoryItem(InventoryItem item) {
        int index = plan.standaloneInventoryItems().indexOf(item);
        if (index >= 0) {
            plan.removeStandaloneInventoryItem(index);
            refreshInventory();
            markDirty();
        }
    }

    private void adjustObjectInventoryContribution(
            InventorySummaryService.ObjectInventoryContribution contribution,
            int delta
    ) {
        plan.findObject(contribution.objectId())
                .filter(InventoryContainer.class::isInstance)
                .map(InventoryContainer.class::cast)
                .filter(container -> contribution.itemIndex() >= 0
                        && contribution.itemIndex() < container.inventoryItems().size())
                .ifPresent(container -> {
                    InventoryItem item = container.inventoryItems().get(contribution.itemIndex());
                    item.setQuantity(Math.max(0, item.quantity() + delta));
                    refreshInventory();
                    refreshObjectInventoryList();
                    markDirty();
                });
    }

    private String signedCount(int count) {
        return count > 0 ? "+" + count : Integer.toString(count);
    }

    private void adjustFenceNetworkGardenStones(
            InventorySummaryService.FenceStoneNetwork network,
            int delta
    ) {
        int adjustment = network.adjustment() + delta;
        adjustment = Math.max(-network.automaticCount(), adjustment);
        plan.setFenceNetworkGardenStoneAdjustment(network.representativeId(), adjustment);
        refreshInventory();
        refreshObjectList();
        redrawMap();
        markDirty();
    }

    private void adjustSelectedFenceNetworkGardenStones(int delta) {
        if (!(selectedObject instanceof FenceRow fenceRow)) {
            return;
        }
        List<FenceRow> rows = plan.fenceNetworkRows(fenceRow.id());
        FenceRow representative = rows.getFirst();
        adjustFenceNetworkGardenStones(fenceStoneNetworkSummary(representative), delta);
        refreshDetails();
    }

    private void updateSelectedFenceInventoryLabelVisibility() {
        if (updatingDetailControls || !(selectedObject instanceof FenceRow fenceRow)) {
            return;
        }
        plan.setShowFenceNetworkInventoryLabel(
                fenceRow.id(), showFenceInventoryLabelCheckBox.isSelected()
        );
        redrawMap();
        markDirty();
    }

    private void adjustStandaloneGardenStones(int delta) {
        plan.setStandaloneGardenStoneCount(plan.standaloneGardenStoneCount() + delta);
        refreshInventory();
        markDirty();
    }

    private Label inventoryDetailLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        return label;
    }

    private TitledPane inventoryPane(
            String title,
            Node content,
            boolean expanded,
            java.util.function.Consumer<Boolean> expandedState
    ) {
        TitledPane pane = new TitledPane(title, content);
        pane.setExpanded(expanded);
        pane.expandedProperty().addListener((observable, oldValue, newValue) -> expandedState.accept(newValue));
        pane.setAnimated(false);
        configureInventoryTextAction(pane);
        return pane;
    }

    private void configureInventoryTextAction(TitledPane pane) {
        pane.setContextMenu(inventoryTextObjectContextMenu(pane));
    }

    private ContextMenu inventoryTextObjectContextMenu(TitledPane pane) {
        MenuItem createTextItem = new MenuItem("Loo kaardile tekstiobjekt");
        createTextItem.setOnAction(event -> createInventoryTextObject(pane));
        return new ContextMenu(createTextItem);
    }

    private void createInventoryTextObject(TitledPane pane) {
        Object summaryKey = pane.getProperties().get(INVENTORY_SUMMARY_KEY_PROPERTY);
        if (summaryKey instanceof String key) {
            showInventorySummaryTextObjectDialog(key);
            return;
        }
        List<String> detailLines = new ArrayList<>();
        collectInventoryTextLines(pane.getContent(), detailLines);
        createStaticTextObject(pane.getText(), String.join(System.lineSeparator(), detailLines));
    }

    private void showInventorySummaryTextObjectDialog(String key) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(stage);
        dialog.setTitle("Loo inventarist tekstiobjekt");
        dialog.setHeaderText(inventorySummaryTitle(key));
        CheckBox synchronize = new CheckBox("Uuenda teksti inventari muutmisel");
        synchronize.setSelected(true);
        dialog.getDialogPane().setContent(synchronize);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        if (dialog.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }
        createTextObject(
                inventorySummaryTitle(key),
                inventorySummaryText(key),
                "",
                INVENTORY_SUMMARY_SOURCE_PREFIX + key,
                synchronize.isSelected(),
                false,
                TextObjectSourceType.INVENTORY_SUMMARY
        );
    }

    private void synchronizeInventorySummaryTextObject(TextObject textObject) {
        String sourceId = textObject.sourceObjectId();
        if (!sourceId.startsWith(INVENTORY_SUMMARY_SOURCE_PREFIX)) {
            return;
        }
        String key = sourceId.substring(INVENTORY_SUMMARY_SOURCE_PREFIX.length());
        textObject.rename(inventorySummaryTitle(key));
        textObject.setNotes(inventorySummaryText(key));
    }

    private String inventorySummaryTitle(String key) {
        if (!key.startsWith("item:")) {
            return "Inventar";
        }
        String itemName = key.substring("item:".length());
        InventorySummaryService.Summary summary = inventorySummaryService.summarize(plan);
        int objectCount = summary.objectInventoryGroups().stream()
                .filter(group -> group.name().equalsIgnoreCase(itemName))
                .mapToInt(InventorySummaryService.ObjectInventoryGroup::totalCount)
                .sum();
        int standaloneCount = plan.standaloneInventoryItems().stream()
                .filter(item -> item.name().equalsIgnoreCase(itemName))
                .mapToInt(InventoryItem::quantity)
                .sum();
        return "%s: %d tk".formatted(itemName, objectCount + standaloneCount);
    }

    private String inventorySummaryText(String key) {
        if (!key.startsWith("item:")) {
            return "";
        }
        String itemName = key.substring("item:".length());
        InventorySummaryService.Summary summary = inventorySummaryService.summarize(plan);
        List<String> lines = new ArrayList<>();
        summary.objectInventoryGroups().stream()
                .filter(group -> group.name().equalsIgnoreCase(itemName))
                .flatMap(group -> group.contributions().stream())
                .forEach(contribution -> lines.add("%s (%s): %d tk%s".formatted(
                        contribution.objectName(),
                        contribution.objectType(),
                        contribution.quantity(),
                        inventoryNotesSuffix(contribution.notes())
                )));
        plan.standaloneInventoryItems().stream()
                .filter(item -> item.name().equalsIgnoreCase(itemName))
                .forEach(item -> lines.add("Lisainventar: %d tk%s".formatted(
                        item.quantity(), inventoryNotesSuffix(item.notes())
                )));
        return String.join(System.lineSeparator(), lines);
    }

    private void createStaticTextObject(String title, String content) {
        createTextObject(title, content, "", "", false, false, TextObjectSourceType.NONE);
    }

    private void showNotesTextObjectDialog(PlannerObject sourceObject) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(stage);
        dialog.setTitle("Loo märkmetest tekstiobjekt");
        dialog.setHeaderText(sourceObject.name());
        CheckBox syncNotes = new CheckBox("Uuenda teksti objekti nime või märkmete muutmisel");
        syncNotes.setSelected(true);
        CheckBox referenceLine = new CheckBox("Näita kaardil viitavat joont");
        VBox options = new VBox(8, syncNotes, referenceLine);
        dialog.getDialogPane().setContent(options);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        if (dialog.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }
        createTextObject(
                sourceObject.name(),
                sourceObject.notes(),
                sourceObject.groupName(),
                sourceObject.id(),
                syncNotes.isSelected(),
                referenceLine.isSelected(),
                TextObjectSourceType.NOTES
        );
    }

    private void showInventoryTextObjectDialog(PlannerObject sourceObject, InventoryContainer container) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(stage);
        dialog.setTitle("Loo inventarist tekstiobjekt");
        dialog.setHeaderText(sourceObject.name());
        CheckBox synchronize = new CheckBox("Uuenda teksti objekti nime või inventari muutmisel");
        synchronize.setSelected(true);
        CheckBox referenceLine = new CheckBox("Näita kaardil viitavat joont");
        dialog.getDialogPane().setContent(new VBox(8, synchronize, referenceLine));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        if (dialog.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }
        createTextObject(
                sourceObject.name() + " — inventar",
                objectInventoryText(container),
                sourceObject.groupName(),
                sourceObject.id(),
                synchronize.isSelected(),
                referenceLine.isSelected(),
                TextObjectSourceType.INVENTORY
        );
    }

    private void showPowerOutletsTextObjectDialog(PowerSource source) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(stage);
        dialog.setTitle("Loo väljunditest tekstiobjekt");
        dialog.setHeaderText(source.name());
        CheckBox synchronize = new CheckBox("Uuenda teksti kilbi nime, väljundite või koormuse muutmisel");
        synchronize.setSelected(true);
        CheckBox referenceLine = new CheckBox("Näita kaardil viitavat joont");
        dialog.getDialogPane().setContent(new VBox(8, synchronize, referenceLine));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        if (dialog.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }
        createTextObject(
                source.name() + " — väljundid",
                powerOutletsText(source),
                source.groupName(),
                source.id(),
                synchronize.isSelected(),
                referenceLine.isSelected(),
                TextObjectSourceType.POWER_OUTLETS
        );
    }

    private void createTextObject(
            String title,
            String content,
            String groupName,
            String sourceObjectId,
            boolean syncSourceNotes,
            boolean showReferenceLine,
            TextObjectSourceType sourceType
    ) {
        TextObject textObject = new TextObject(
                planFactory.newId(),
                title,
                visibleMapCenter()
        );
        textObject.setNotes(content);
        textObject.setGroupName(groupName);
        textObject.setSourceObjectId(sourceObjectId);
        textObject.setSyncSourceNotes(syncSourceNotes);
        textObject.setShowReferenceLine(showReferenceLine);
        textObject.setSourceType(sourceType);
        plan.addObject(textObject);
        refreshGroupFilters();
        selectObject(textObject);
        refreshSummary();
        markDirty();
    }

    private void collectInventoryTextLines(Node node, List<String> lines) {
        if (node instanceof Label label && !label.getText().isBlank()) {
            lines.add(label.getText());
        }
        if (node instanceof Pane pane) {
            pane.getChildren().forEach(child -> collectInventoryTextLines(child, lines));
        } else if (node instanceof TitledPane titledPane && titledPane.getContent() != null) {
            collectInventoryTextLines(titledPane.getContent(), lines);
        }
    }

    private Position visibleMapCenter() {
        Node viewport = mapScrollPane.lookup(".viewport");
        if (viewport == null) {
            return new Position(mapPane.getWidth() / 2, mapPane.getHeight() / 2);
        }
        Bounds viewportSceneBounds = viewport.localToScene(viewport.getBoundsInLocal());
        Point2D center = mapPane.sceneToLocal(
                viewportSceneBounds.getCenterX(),
                viewportSceneBounds.getCenterY()
        );
        return new Position(center.getX(), center.getY());
    }

    private void addConnectedConsumers(PowerHierarchyService.SourceRow source) {
        for (PowerHierarchyService.OutletRow outlet : source.outlets()) {
            String hierarchyKey = powerOutletSummaryKey(outlet.id());
            boolean expanded = isSummaryItemExpanded(hierarchyKey);
            summaryList.getItems().add(SummaryListItem.expandableLoad("  %s: %d W kasutusel, %s".formatted(
                    outletDisplayName(outlet),
                    outlet.usedWatts(),
                    remainingWattsText(outlet.remainingWatts())
            ), outlet.usedWatts(), outlet.capacityWatts(), source.id(), hierarchyKey, expanded, 1));
            if (expanded) {
                addConnectedConsumers(outlet.consumers(), "    ");
            }
        }
        addConnectedConsumers(source.directConsumers(), "  ");
    }

    private String powerSourceSummaryKey(String sourceId) {
        return "source:" + sourceId;
    }

    private String powerOutletSummaryKey(String outletId) {
        return "outlet:" + outletId;
    }

    private boolean isSummaryItemExpanded(String hierarchyKey) {
        return !collapsedSummaryKeys.contains(hierarchyKey);
    }

    private void toggleSummaryItem(String hierarchyKey) {
        if (!collapsedSummaryKeys.add(hierarchyKey)) {
            collapsedSummaryKeys.remove(hierarchyKey);
        }
        refreshSummary();
    }

    private String outletDisplayName(PowerHierarchyService.OutletRow outlet) {
        if (!outlet.name().isBlank()) {
            return "%s (%s %d)".formatted(outlet.name(), outlet.type().displayName(), outlet.typeIndex());
        }
        return "%s %d".formatted(outlet.type().displayName(), outlet.typeIndex());
    }

    private void addConnectedConsumers(List<PowerHierarchyService.ConsumerRow> consumers, String rowPrefix) {
        for (PowerHierarchyService.ConsumerRow consumer : consumers) {
            summaryList.getItems().add(SummaryListItem.target("%s- %s: %d W (%s)".formatted(
                    rowPrefix,
                    consumer.name(),
                    consumer.usedWatts(),
                    consumer.connectorType().displayName()
            ), consumer.id()));
        }
    }

    private int usedWatts(String outletId) {
        return plan.outletDemandWatts(outletId);
    }

    private void addCableInventory() {
        CableInventorySummaryService.Summary summary = cableInventorySummaryService.summarize(plan);
        if (summary.isEmpty()) {
            return;
        }

        VBox details = new VBox(3);
        String cableTotalText = summary.hasNotedLength()
                ? "Kaablid: %.1f m märgitud · %.1f m kaardil".formatted(
                        summary.totalNotedLengthMeters(), summary.totalMapLengthMeters()
                )
                : "Kaablid: %.1f m".formatted(summary.totalMapLengthMeters());
        for (CableInventorySummaryService.Row row : summary.rows()) {
            Label rowLabel = inventoryDetailLabel(
                    CableInventoryTextFormatter.connectionRow(row).stripLeading()
            );
            attachInventoryContextMenu(rowLabel, cableInventoryContextMenu(row));
            details.getChildren().add(rowLabel);
        }
        for (String row : CableInventoryTextFormatter.typeSummaryRows(summary.byType())) {
            details.getChildren().add(inventoryDetailLabel(row.stripLeading()));
        }
        inventoryContent.getChildren().add(inventoryPane(
                cableTotalText,
                details,
                cableInventoryExpanded,
                expanded -> cableInventoryExpanded = expanded
        ));
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
                    summaryText(options.reportScope()),
                    options.includeObjectLegend() ? objectLegendItems() : List.of()
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
        return reportTextExporter.export(
                plan, reportScope, !organizerView, !organizerView, true, !organizerView
        );
    }

    private List<PdfObjectLegendItem> objectLegendItems() {
        Map<String, List<PlannerObject>> objectsByGroup = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        plan.objects().stream()
                .filter(this::isObjectVisibleOnMap)
                .filter(this::isObjectAvailableInCurrentView)
                .filter(object -> !(object instanceof FenceRow fenceRow)
                        || plan.isFenceNetworkRepresentative(fenceRow))
                .forEach(object -> objectsByGroup
                        .computeIfAbsent(groupNameForFilter(object), ignored -> new ArrayList<>())
                        .add(object));

        List<PdfObjectLegendItem> items = new ArrayList<>();
        for (Map.Entry<String, List<PlannerObject>> groupEntry : objectsByGroup.entrySet()) {
            groupEntry.getValue().stream()
                    .sorted(Comparator.comparing(this::objectTypeName, String.CASE_INSENSITIVE_ORDER)
                            .thenComparing(PlannerObject::name, String.CASE_INSENSITIVE_ORDER))
                    .forEach(object -> items.add(new PdfObjectLegendItem(
                            groupEntry.getKey(),
                            objectTypeName(object),
                            object.name(),
                            objectListColorHex(object),
                            object instanceof FenceRow fenceRow
                                    ? fenceNetworkMeasurementText(fenceRow)
                                    : objectMeasurementText(object)
                    )));
        }
        return items;
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

    private record MeasurementView(Position start, Position end, Line line, Label distanceLabel) {
    }

    private record MeasurementPathView(
            List<Position> points,
            List<MeasurementView> segments,
            List<Node> nodes,
            List<Circle> pointMarkers,
            Label totalLabel
    ) {
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

    private void showInformation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(stage);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private record FenceNetworkClipboard(
            Map<String, Position> relativeJoints,
            List<FenceRowClipboard> rows
    ) {
    }

    private record MultiObjectClipboardEntry(
            PlannerObject template,
            Position relativePosition,
            FenceNetworkClipboard fenceNetwork
    ) {
    }

    private record MultiObjectDragState(
            Position pointerStart,
            Map<String, Position> objectPositions,
            Map<String, Position> fenceJointPositions,
            Map<String, List<Position>> cableRoutePositions
    ) {
    }

    private record MultiObjectRotationState(
            Position center,
            double handleDistance,
            double handleStartRotationDegrees,
            double pointerStartRotationDegrees,
            Map<String, MultiObjectRotationObjectState> objectStates,
            Map<String, Position> fenceJointPositions
    ) {
    }

    private record MultiObjectRotationObjectState(
            Position position,
            Position visualCenter,
            double rotationDegrees,
            List<Position> points
    ) {
    }

    private record FenceRowClipboard(
            String name,
            String groupName,
            String notes,
            boolean showMapLabel,
            int segmentCount,
            double segmentLengthMeters,
            String colorHex,
            double widthPixels,
            double opacity,
            int gardenStoneAdjustment,
            boolean showInventoryLabel,
            Position inventoryLabelOffset,
            String startJointId,
            String endJointId
    ) {
    }

    private record FenceRowVisual(
            Line fenceLine,
            List<Line> dividers,
            Label inventoryLabel,
            Circle startHandle,
            Circle endHandle,
            List<Circle> splitHandles
    ) {
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

    private record ChecklistSuggestion(String id, String text) {
    }

    private static class Delta {
        private double x;
        private double y;
    }
}
