package ee.matteus.plaanisepp.core.model;

import ee.matteus.plaanisepp.core.map.BaseMapBounds;
import ee.matteus.plaanisepp.core.map.BaseMapDownload;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EventPlan {
    public static final double DEFAULT_PIXELS_PER_METER = 6.45;
    public static final double DEFAULT_OBJECT_LABEL_FONT_SIZE = 12.0;
    public static final double DEFAULT_CABLE_LABEL_FONT_SIZE = 12.0;

    private String name;
    private String festivalName = "";
    private String mapImagePath;
    private String packagedMapImageEntry = "";
    private byte[] packagedMapImage = new byte[0];
    private byte[] downloadedRegularMap = new byte[0];
    private byte[] downloadedOrthophoto = new byte[0];
    private BaseMapBounds downloadedMapBounds;
    private boolean downloadedOrthophotoActive;
    private long mapImageRevision;
    private double pixelsPerMeter = DEFAULT_PIXELS_PER_METER;
    private double objectLabelFontSize = DEFAULT_OBJECT_LABEL_FONT_SIZE;
    private double cableLabelFontSize = DEFAULT_CABLE_LABEL_FONT_SIZE;
    private final List<PlannerObject> objects = new ArrayList<>();
    private final List<FenceJoint> fenceJoints = new ArrayList<>();
    private final List<PowerConnection> powerConnections = new ArrayList<>();
    private final List<PlanLayerEntry> layerOrder = new ArrayList<>();
    private final List<InventoryItem> standaloneInventoryItems = new ArrayList<>();
    private final List<ChecklistItem> checklistItems = new ArrayList<>();
    private final Map<String, ChecklistSuggestionStatus> checklistSuggestionStatuses = new TreeMap<>();
    private final Set<String> hiddenGroups = new HashSet<>();
    private final Set<String> lockedGroups = new HashSet<>();
    private final Set<String> hiddenCableLabelConnectionIds = new HashSet<>();
    private final Map<String, Double> cableOpacities = new TreeMap<>();
    private boolean showCables = true;
    private boolean showCableLabels = true;
    private boolean show230VCables = true;
    private boolean show16ACables = true;
    private boolean show32ACables = true;
    private boolean show63ACables = true;
    private boolean showObjectLabels = true;
    private boolean showTents = true;
    private boolean showPowerSources = true;
    private boolean showCustomObjects = true;
    private boolean showTextObjects = true;
    private boolean showMarkerObjects = true;
    private boolean showAreaObjects = true;
    private boolean showLineObjects = true;
    private boolean showFenceInventoryLabels = true;
    private int standaloneGardenStoneCount;

    public EventPlan(String name) {
        this.name = name;
        this.mapImagePath = "";
    }

    public String name() {
        return name;
    }

    public void rename(String name) {
        this.name = name;
    }

    public String festivalName() {
        return festivalName;
    }

    public void setFestivalName(String festivalName) {
        this.festivalName = festivalName == null ? "" : festivalName.trim();
    }

    public String mapImagePath() {
        return mapImagePath;
    }

    public void setMapImagePath(String mapImagePath) {
        this.mapImagePath = mapImagePath == null ? "" : mapImagePath;
        clearPackagedMapImage();
        downloadedRegularMap = new byte[0];
        downloadedOrthophoto = new byte[0];
        downloadedMapBounds = null;
        downloadedOrthophotoActive = false;
        mapImageRevision++;
    }

    public boolean hasPackagedMapImage() {
        return packagedMapImage.length > 0;
    }

    public String packagedMapImageEntry() {
        return packagedMapImageEntry;
    }

    public byte[] packagedMapImage() {
        return packagedMapImage.clone();
    }

    public void setPackagedMapImage(String entryName, byte[] imageData) {
        if (entryName == null || entryName.isBlank()) {
            throw new IllegalArgumentException("Pakitud kaardipildi kirje nimi ei tohi olla tühi.");
        }
        if (imageData == null || imageData.length == 0) {
            throw new IllegalArgumentException("Pakitud kaardipilt ei tohi olla tühi.");
        }
        packagedMapImageEntry = entryName;
        packagedMapImage = imageData.clone();
        mapImagePath = "package:/" + entryName;
        mapImageRevision++;
    }

    public void clearPackagedMapImage() {
        packagedMapImageEntry = "";
        packagedMapImage = new byte[0];
    }

    public boolean hasDownloadedBaseMaps() {
        return downloadedRegularMap.length > 0 && downloadedOrthophoto.length > 0;
    }

    public byte[] downloadedRegularMap() {
        return downloadedRegularMap.clone();
    }

    public byte[] downloadedOrthophoto() {
        return downloadedOrthophoto.clone();
    }

    public BaseMapBounds downloadedMapBounds() {
        return downloadedMapBounds;
    }

    public boolean downloadedOrthophotoActive() {
        return downloadedOrthophotoActive;
    }

    public void setDownloadedBaseMaps(BaseMapDownload download) {
        downloadedRegularMap = download.regularMap();
        downloadedOrthophoto = download.orthophoto();
        downloadedMapBounds = download.bounds();
        downloadedOrthophotoActive = false;
        pixelsPerMeter = download.pixelsPerMetre();
        setDownloadedBaseMapActive(false);
    }

    public void scalePixelGeometry(double factor) {
        if (!Double.isFinite(factor) || factor <= 0) {
            throw new IllegalArgumentException("Geomeetria mõõtkava peab olema positiivne.");
        }
        transformPixelGeometry(factor, 0, 0);
    }

    public void reprojectPixelGeometry(
            BaseMapBounds previousBounds,
            double previousPixelsPerMetre,
            BaseMapBounds newBounds,
            double newPixelsPerMetre
    ) {
        if (previousBounds == null || newBounds == null
                || !Double.isFinite(previousPixelsPerMetre) || previousPixelsPerMetre <= 0
                || !Double.isFinite(newPixelsPerMetre) || newPixelsPerMetre <= 0) {
            throw new IllegalArgumentException("Kaardigeomeetria teisendamiseks on vaja korrektseid kaardiandmeid.");
        }
        double factor = newPixelsPerMetre / previousPixelsPerMetre;
        double offsetX = (previousBounds.minX() - newBounds.minX()) * newPixelsPerMetre;
        double offsetY = (newBounds.maxY() - previousBounds.maxY()) * newPixelsPerMetre;
        transformPixelGeometry(factor, offsetX, offsetY);
    }

    private void transformPixelGeometry(double factor, double offsetX, double offsetY) {
        if (Math.abs(factor - 1.0) < 0.000001
                && Math.abs(offsetX) < 0.000001
                && Math.abs(offsetY) < 0.000001) {
            return;
        }
        for (PlannerObject object : objects) {
            List<Position> originalPoints = object instanceof AreaObject areaObject
                    ? List.copyOf(areaObject.points())
                    : object instanceof LineObject lineObject ? List.copyOf(lineObject.points()) : List.of();
            boolean locked = object.locked();
            object.setLocked(false);
            object.moveTo(transformPosition(object.position(), factor, offsetX, offsetY));
            object.setLocked(locked);
            if (object instanceof AreaObject areaObject) {
                areaObject.setPoints(transformPositions(originalPoints, factor, offsetX, offsetY));
            } else if (object instanceof LineObject lineObject) {
                lineObject.setPoints(transformPositions(originalPoints, factor, offsetX, offsetY));
            }
            if (object.customMapLabelPosition()) {
                object.setMapLabelOffset(scalePosition(object.mapLabelOffset(), factor));
            }
            if (object instanceof TextObject textObject) {
                textObject.setReferenceLineSourceOffset(
                        scalePosition(textObject.referenceLineSourceOffset(), factor));
            }
            if (object instanceof EquipmentContainer container) {
                container.setPowerConnectionOffset(scalePosition(container.powerConnectionOffset(), factor));
            }
        }
        for (FenceJoint joint : fenceJoints) {
            joint.moveTo(transformPosition(joint.position(), factor, offsetX, offsetY));
        }
        for (int index = 0; index < powerConnections.size(); index++) {
            PowerConnection connection = powerConnections.get(index);
            powerConnections.set(index, new PowerConnection(
                    connection.id(), connection.sourceId(), connection.consumerId(), connection.connectorType(),
                    connection.outletId(), connection.cableNotes(), connection.cableLengthNotes(),
                    transformPositions(connection.routePoints(), factor, offsetX, offsetY), connection.customCableLabelPosition(),
                    scalePosition(connection.cableLabelOffset(), factor), connection.defaultForConsumer()
            ));
        }
    }

    private static List<Position> scalePositions(List<Position> positions, double factor) {
        return positions.stream().map(position -> scalePosition(position, factor)).toList();
    }

    private static List<Position> transformPositions(
            List<Position> positions,
            double factor,
            double offsetX,
            double offsetY
    ) {
        return positions.stream()
                .map(position -> transformPosition(position, factor, offsetX, offsetY))
                .toList();
    }

    private static Position scalePosition(Position position, double factor) {
        return new Position(position.x() * factor, position.y() * factor);
    }

    private static Position transformPosition(Position position, double factor, double offsetX, double offsetY) {
        return new Position(position.x() * factor + offsetX, position.y() * factor + offsetY);
    }

    public void restoreDownloadedBaseMaps(
            byte[] regularMap,
            byte[] orthophoto,
            BaseMapBounds bounds,
            boolean orthophotoActive
    ) {
        if (regularMap == null || regularMap.length == 0 || orthophoto == null || orthophoto.length == 0) {
            throw new IllegalArgumentException("Allalaaditud aluskaardid ei tohi olla tühjad.");
        }
        downloadedRegularMap = regularMap.clone();
        downloadedOrthophoto = orthophoto.clone();
        downloadedMapBounds = bounds;
        setDownloadedBaseMapActive(orthophotoActive);
    }

    public void setDownloadedBaseMapActive(boolean orthophoto) {
        if (!hasDownloadedBaseMaps()) {
            throw new IllegalStateException("Plaanil ei ole allalaaditud aluskaarte.");
        }
        downloadedOrthophotoActive = orthophoto;
        packagedMapImageEntry = orthophoto ? "assets/orthophoto.png" : "assets/base-map.png";
        packagedMapImage = (orthophoto ? downloadedOrthophoto : downloadedRegularMap).clone();
        mapImagePath = "package:/" + packagedMapImageEntry;
        mapImageRevision++;
    }

    public long mapImageRevision() {
        return mapImageRevision;
    }

    public double pixelsPerMeter() {
        return pixelsPerMeter;
    }

    public void setPixelsPerMeter(double pixelsPerMeter) {
        if (pixelsPerMeter <= 0) {
            throw new IllegalArgumentException("Pikslit meetri kohta peab olema positiivne.");
        }
        this.pixelsPerMeter = pixelsPerMeter;
    }

    public double objectLabelFontSize() {
        return objectLabelFontSize;
    }

    public void setObjectLabelFontSize(double objectLabelFontSize) {
        if (objectLabelFontSize <= 0) {
            throw new IllegalArgumentException("Objektisildi teksti suurus peab olema positiivne.");
        }
        this.objectLabelFontSize = objectLabelFontSize;
    }

    public double cableLabelFontSize() {
        return cableLabelFontSize;
    }

    public void setCableLabelFontSize(double cableLabelFontSize) {
        if (cableLabelFontSize <= 0) {
            throw new IllegalArgumentException("Kaablisildi teksti suurus peab olema positiivne.");
        }
        this.cableLabelFontSize = cableLabelFontSize;
    }

    public void addObject(PlannerObject object) {
        objects.add(object);
        layerOrder.add(PlanLayerEntry.object(object.id()));
        if (object instanceof FenceRow fenceRow) {
            ensureFenceJoints(fenceRow);
        }
    }

    public void removeObject(String objectId) {
        findObject(objectId)
                .filter(FenceRow.class::isInstance)
                .map(FenceRow.class::cast)
                .filter(row -> row.gardenStoneAdjustment() != 0)
                .ifPresent(row -> fenceNetworkRows(row.id()).stream()
                        .filter(candidate -> !candidate.id().equals(row.id()))
                        .findFirst()
                        .ifPresent(candidate -> candidate.setGardenStoneAdjustment(
                                candidate.gardenStoneAdjustment() + row.gardenStoneAdjustment()
                        )));
        objects.removeIf(object -> object.id().equals(objectId));
        layerOrder.removeIf(entry -> entry.equals(PlanLayerEntry.object(objectId)));
        removeUnusedFenceJoints();
        removePowerConnections(connection ->
                connection.sourceId().equals(objectId) || connection.consumerId().equals(objectId));
    }

    public List<PlannerObject> objects() {
        return Collections.unmodifiableList(objects);
    }

    public List<PlanLayerEntry> layerOrder() {
        synchronizeLayerOrder();
        return Collections.unmodifiableList(layerOrder);
    }

    public void setLayerOrder(List<PlanLayerEntry> entries) {
        layerOrder.clear();
        if (entries != null) {
            entries.stream().filter(entry -> entry != null).forEach(layerOrder::add);
        }
        synchronizeLayerOrder();
    }

    public boolean moveLayerEntries(Collection<PlanLayerEntry> entries, int direction) {
        synchronizeLayerOrder();
        return moveEntriesByLayer(layerOrder, entries, direction);
    }

    public boolean moveLayerEntriesToBoundary(Collection<PlanLayerEntry> entries, boolean front) {
        synchronizeLayerOrder();
        if (entries == null || entries.isEmpty()) return false;
        Set<PlanLayerEntry> selectedIds = Set.copyOf(entries);
        List<PlanLayerEntry> selected = layerOrder.stream().filter(selectedIds::contains).toList();
        if (selected.isEmpty()) return false;
        List<PlanLayerEntry> reordered = new ArrayList<>(layerOrder.size());
        if (!front) reordered.addAll(selected);
        layerOrder.stream().filter(entry -> !selectedIds.contains(entry)).forEach(reordered::add);
        if (front) reordered.addAll(selected);
        if (reordered.equals(layerOrder)) return false;
        layerOrder.clear();
        layerOrder.addAll(reordered);
        return true;
    }

    public boolean moveLayerEntryToIndex(PlanLayerEntry entry, int targetIndex) {
        synchronizeLayerOrder();
        int currentIndex = layerOrder.indexOf(entry);
        if (currentIndex < 0) return false;
        int boundedTarget = Math.max(0, Math.min(targetIndex, layerOrder.size() - 1));
        if (currentIndex == boundedTarget) return false;
        layerOrder.remove(currentIndex);
        layerOrder.add(boundedTarget, entry);
        return true;
    }

    private void synchronizeLayerOrder() {
        Set<PlanLayerEntry> valid = new HashSet<>();
        objects.forEach(object -> valid.add(PlanLayerEntry.object(object.id())));
        powerConnections.forEach(connection -> valid.add(PlanLayerEntry.cable(connection.id())));
        layerOrder.removeIf(entry -> !valid.contains(entry));
        for (PlanLayerEntry entry : valid) {
            if (!layerOrder.contains(entry)) layerOrder.add(entry);
        }
    }

    private static <T> boolean moveEntriesByLayer(List<T> order, Collection<T> entries, int direction) {
        if (entries == null || entries.isEmpty() || direction == 0) return false;
        Set<T> ids = Set.copyOf(entries);
        boolean moved = false;
        if (direction > 0) {
            for (int index = order.size() - 2; index >= 0; index--) {
                if (ids.contains(order.get(index)) && !ids.contains(order.get(index + 1))) {
                    Collections.swap(order, index, index + 1);
                    moved = true;
                }
            }
        } else {
            for (int index = 1; index < order.size(); index++) {
                if (ids.contains(order.get(index)) && !ids.contains(order.get(index - 1))) {
                    Collections.swap(order, index, index - 1);
                    moved = true;
                }
            }
        }
        return moved;
    }

    public boolean moveObjectsByLayer(Collection<String> objectIds, int direction) {
        if (objectIds == null || objectIds.isEmpty() || direction == 0) {
            return false;
        }
        Set<String> ids = Set.copyOf(objectIds);
        boolean moved = false;
        if (direction > 0) {
            for (int index = objects.size() - 2; index >= 0; index--) {
                if (ids.contains(objects.get(index).id()) && !ids.contains(objects.get(index + 1).id())) {
                    Collections.swap(objects, index, index + 1);
                    moved = true;
                }
            }
        } else {
            for (int index = 1; index < objects.size(); index++) {
                if (ids.contains(objects.get(index).id()) && !ids.contains(objects.get(index - 1).id())) {
                    Collections.swap(objects, index, index - 1);
                    moved = true;
                }
            }
        }
        return moved;
    }

    public boolean moveObjectsToLayerBoundary(Collection<String> objectIds, boolean front) {
        if (objectIds == null || objectIds.isEmpty()) {
            return false;
        }
        Set<String> ids = Set.copyOf(objectIds);
        List<PlannerObject> selected = objects.stream()
                .filter(object -> ids.contains(object.id()))
                .toList();
        if (selected.isEmpty()) {
            return false;
        }
        List<PlannerObject> reordered = new ArrayList<>(objects.size());
        if (!front) {
            reordered.addAll(selected);
        }
        objects.stream()
                .filter(object -> !ids.contains(object.id()))
                .forEach(reordered::add);
        if (front) {
            reordered.addAll(selected);
        }
        if (reordered.equals(objects)) {
            return false;
        }
        objects.clear();
        objects.addAll(reordered);
        return true;
    }

    public List<ChecklistItem> checklistItems() {
        return Collections.unmodifiableList(checklistItems);
    }

    /**
     * Inventar, mida ei seota ühegi kaardil oleva objektiga, näiteks eraldi lauad või raskused.
     */
    public List<InventoryItem> standaloneInventoryItems() {
        return Collections.unmodifiableList(standaloneInventoryItems);
    }

    public void addStandaloneInventoryItem(InventoryItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Inventarikirje puudub.");
        }
        standaloneInventoryItems.add(item);
    }

    public void removeStandaloneInventoryItem(int index) {
        standaloneInventoryItems.remove(index);
    }

    public ChecklistItem addChecklistItem(String text) {
        ChecklistItem item = new ChecklistItem(text);
        checklistItems.add(item);
        return item;
    }

    public void addChecklistItem(ChecklistItem item) {
        checklistItems.add(item);
    }

    public boolean removeChecklistItem(String itemId) {
        return checklistItems.removeIf(item -> item.id().equals(itemId));
    }

    public boolean moveChecklistItem(String itemId, int offset) {
        int currentIndex = -1;
        for (int index = 0; index < checklistItems.size(); index++) {
            if (checklistItems.get(index).id().equals(itemId)) {
                currentIndex = index;
                break;
            }
        }
        int targetIndex = currentIndex + offset;
        if (currentIndex < 0 || targetIndex < 0 || targetIndex >= checklistItems.size()) {
            return false;
        }
        ChecklistItem item = checklistItems.remove(currentIndex);
        checklistItems.add(targetIndex, item);
        return true;
    }

    public ChecklistSuggestionStatus checklistSuggestionStatus(String suggestionId) {
        return checklistSuggestionStatuses.getOrDefault(suggestionId, ChecklistSuggestionStatus.PENDING);
    }

    public void setChecklistSuggestionStatus(String suggestionId, ChecklistSuggestionStatus status) {
        if (suggestionId == null || suggestionId.isBlank()) {
            throw new IllegalArgumentException("Checklist'i soovituse tunnus ei tohi olla tühi.");
        }
        ChecklistSuggestionStatus selectedStatus = status == null
                ? ChecklistSuggestionStatus.PENDING
                : status;
        if (selectedStatus == ChecklistSuggestionStatus.PENDING) {
            checklistSuggestionStatuses.remove(suggestionId);
        } else {
            checklistSuggestionStatuses.put(suggestionId, selectedStatus);
        }
    }

    public Map<String, ChecklistSuggestionStatus> checklistSuggestionStatuses() {
        return Collections.unmodifiableMap(checklistSuggestionStatuses);
    }

    public List<PowerSource> powerSources() {
        return objects.stream()
                .filter(PowerSource.class::isInstance)
                .map(PowerSource.class::cast)
                .toList();
    }

    public List<Tent> tents() {
        return objects.stream()
                .filter(Tent.class::isInstance)
                .map(Tent.class::cast)
                .toList();
    }

    public List<PowerConsumer> powerConsumers() {
        return objects.stream()
                .filter(PowerConsumer.class::isInstance)
                .map(PowerConsumer.class::cast)
                .toList();
    }

    public List<AreaObject> areaObjects() {
        return objects.stream()
                .filter(AreaObject.class::isInstance)
                .map(AreaObject.class::cast)
                .toList();
    }

    public List<LineObject> lineObjects() {
        return objects.stream()
                .filter(LineObject.class::isInstance)
                .map(LineObject.class::cast)
                .toList();
    }

    public Optional<PlannerObject> findObject(String id) {
        return objects.stream().filter(object -> object.id().equals(id)).findFirst();
    }

    public List<FenceJoint> fenceJoints() {
        return Collections.unmodifiableList(fenceJoints);
    }

    public void addFenceJoint(FenceJoint joint) {
        if (findFenceJoint(joint.id()).isEmpty()) {
            fenceJoints.add(joint);
        }
    }

    public Optional<FenceJoint> findFenceJoint(String id) {
        return fenceJoints.stream().filter(joint -> joint.id().equals(id)).findFirst();
    }

    public int fenceJointDegree(String jointId) {
        return (int) objects.stream()
                .filter(FenceRow.class::isInstance)
                .map(FenceRow.class::cast)
                .filter(row -> row.startJointId().equals(jointId) || row.endJointId().equals(jointId))
                .count();
    }

    public String createFenceJoint(Position position) {
        String id = UUID.randomUUID().toString();
        addFenceJoint(new FenceJoint(id, position));
        return id;
    }

    public void setFenceRowJoints(FenceRow row, String startJointId, String endJointId) {
        if (findFenceJoint(startJointId).isEmpty() || findFenceJoint(endJointId).isEmpty()) {
            throw new IllegalArgumentException("Aiaraja ühenduspunkti ei leitud.");
        }
        row.setJointIds(startJointId, endJointId);
        removeUnusedFenceJoints();
        synchronizeFenceRows(pixelsPerMeter);
    }

    public void disconnectFenceEndpoint(FenceRow row, boolean startEndpoint) {
        String currentJointId = startEndpoint ? row.startJointId() : row.endJointId();
        FenceJoint current = findFenceJoint(currentJointId).orElseThrow();
        String separateJointId = createFenceJoint(current.position());
        row.setJointIds(
                startEndpoint ? separateJointId : row.startJointId(),
                startEndpoint ? row.endJointId() : separateJointId
        );
        removeUnusedFenceJoints();
    }

    public boolean moveFenceEndpoint(FenceRow row, boolean startEndpoint, Position target) {
        String movingJointId = startEndpoint ? row.startJointId() : row.endJointId();
        if (fenceJointDegree(movingJointId) > 1 || fenceNetworkRows(row.id()).size() > 1) {
            return reshapeFenceNetwork(row, movingJointId, target);
        }
        FenceJoint fixed = findFenceJoint(startEndpoint ? row.endJointId() : row.startJointId()).orElseThrow();
        double deltaX = target.x() - fixed.position().x();
        double deltaY = target.y() - fixed.position().y();
        double distance = Math.hypot(deltaX, deltaY);
        if (distance == 0) {
            return false;
        }
        double lengthPixels = row.totalLengthMeters() * pixelsPerMeter;
        Position constrained = new Position(
                fixed.position().x() + deltaX / distance * lengthPixels,
                fixed.position().y() + deltaY / distance * lengthPixels
        );
        findFenceJoint(movingJointId).orElseThrow().moveTo(constrained);
        synchronizeFenceRows(pixelsPerMeter);
        return true;
    }

    private boolean reshapeFenceNetwork(FenceRow startRow, String pinnedJointId, Position target) {
        Set<String> rowIds = new HashSet<>();
        Set<String> jointIds = new HashSet<>();
        collectFenceNetwork(startRow, rowIds, jointIds);
        boolean lockedNetwork = objects.stream()
                .filter(FenceRow.class::isInstance)
                .filter(object -> rowIds.contains(object.id()))
                .anyMatch(this::isObjectLocked);
        if (lockedNetwork) {
            return false;
        }

        List<FenceRow> networkRows = objects.stream()
                .filter(FenceRow.class::isInstance)
                .map(FenceRow.class::cast)
                .filter(candidate -> rowIds.contains(candidate.id()))
                .toList();
        Map<String, Position> originalPositions = fenceJoints.stream()
                .filter(joint -> jointIds.contains(joint.id()))
                .collect(Collectors.toMap(FenceJoint::id, FenceJoint::position));
        findFenceJoint(pinnedJointId).orElseThrow().moveTo(target);
        for (int iteration = 0; iteration < 240; iteration++) {
            double largestError = 0;
            for (FenceRow networkRow : networkRows) {
                FenceJoint start = findFenceJoint(networkRow.startJointId()).orElseThrow();
                FenceJoint end = findFenceJoint(networkRow.endJointId()).orElseThrow();
                double requiredLength = networkRow.totalLengthMeters() * pixelsPerMeter;
                double deltaX = end.position().x() - start.position().x();
                double deltaY = end.position().y() - start.position().y();
                double actualLength = Math.hypot(deltaX, deltaY);
                largestError = Math.max(largestError, Math.abs(actualLength - requiredLength));
                if (actualLength < 0.000001) {
                    double angle = Math.toRadians(networkRow.rotationDegrees());
                    deltaX = Math.cos(angle);
                    deltaY = Math.sin(angle);
                    actualLength = 1;
                }
                double correctionX = deltaX / actualLength * (requiredLength - actualLength);
                double correctionY = deltaY / actualLength * (requiredLength - actualLength);
                boolean startPinned = start.id().equals(pinnedJointId);
                boolean endPinned = end.id().equals(pinnedJointId);
                if (startPinned && !endPinned) {
                    end.moveTo(new Position(end.position().x() + correctionX, end.position().y() + correctionY));
                } else if (endPinned && !startPinned) {
                    start.moveTo(new Position(start.position().x() - correctionX, start.position().y() - correctionY));
                } else if (!startPinned && !endPinned) {
                    start.moveTo(new Position(
                            start.position().x() - correctionX / 2,
                            start.position().y() - correctionY / 2
                    ));
                    end.moveTo(new Position(
                            end.position().x() + correctionX / 2,
                            end.position().y() + correctionY / 2
                    ));
                }
            }
            findFenceJoint(pinnedJointId).orElseThrow().moveTo(target);
            if (largestError < 0.001) {
                break;
            }
        }
        double remainingError = networkRows.stream()
                .mapToDouble(networkRow -> {
                    Position start = findFenceJoint(networkRow.startJointId()).orElseThrow().position();
                    Position end = findFenceJoint(networkRow.endJointId()).orElseThrow().position();
                    double actualLength = Math.hypot(end.x() - start.x(), end.y() - start.y());
                    return Math.abs(actualLength - networkRow.totalLengthMeters() * pixelsPerMeter);
                })
                .max()
                .orElse(0);
        if (!Double.isFinite(remainingError) || remainingError > 0.05) {
            originalPositions.forEach((jointId, position) ->
                    findFenceJoint(jointId).orElseThrow().moveTo(position));
            alignFenceRowsKeepingLengths(networkRows);
            return false;
        }
        alignFenceRowsKeepingLengths(networkRows);
        return true;
    }

    private void alignFenceRowsKeepingLengths(List<FenceRow> rows) {
        for (FenceRow row : rows) {
            FenceJoint start = findFenceJoint(row.startJointId()).orElseThrow();
            FenceJoint end = findFenceJoint(row.endJointId()).orElseThrow();
            row.alignDirectionToEndpoints(start.position(), end.position());
        }
    }

    public boolean applyFenceRowGeometry(FenceRow row) {
        if (fenceJointDegree(row.endJointId()) == 1) {
            findFenceJoint(row.endJointId()).orElseThrow().moveTo(row.endPosition(pixelsPerMeter));
        } else if (fenceJointDegree(row.startJointId()) == 1) {
            Position fixedEnd = findFenceJoint(row.endJointId()).orElseThrow().position();
            double angle = Math.toRadians(row.rotationDegrees());
            double lengthPixels = row.totalLengthMeters() * pixelsPerMeter;
            findFenceJoint(row.startJointId()).orElseThrow().moveTo(new Position(
                    fixedEnd.x() - Math.cos(angle) * lengthPixels,
                    fixedEnd.y() - Math.sin(angle) * lengthPixels
            ));
        } else {
            return false;
        }
        synchronizeFenceRows(pixelsPerMeter);
        return true;
    }

    public boolean translateFenceNetwork(String fenceRowId, double deltaX, double deltaY) {
        FenceRow startRow = findObject(fenceRowId)
                .filter(FenceRow.class::isInstance)
                .map(FenceRow.class::cast)
                .orElseThrow();
        Set<String> rowIds = new HashSet<>();
        Set<String> jointIds = new HashSet<>();
        collectFenceNetwork(startRow, rowIds, jointIds);
        boolean lockedNetwork = objects.stream()
                .filter(FenceRow.class::isInstance)
                .filter(object -> rowIds.contains(object.id()))
                .anyMatch(this::isObjectLocked);
        if (lockedNetwork) {
            return false;
        }
        fenceJoints.stream()
                .filter(joint -> jointIds.contains(joint.id()))
                .forEach(joint -> joint.moveTo(new Position(
                        joint.position().x() + deltaX,
                        joint.position().y() + deltaY
                )));
        synchronizeFenceRows(pixelsPerMeter);
        return true;
    }

    private void collectFenceNetwork(FenceRow row, Set<String> rowIds, Set<String> jointIds) {
        if (!rowIds.add(row.id())) {
            return;
        }
        jointIds.add(row.startJointId());
        jointIds.add(row.endJointId());
        objects.stream()
                .filter(FenceRow.class::isInstance)
                .map(FenceRow.class::cast)
                .filter(candidate -> !rowIds.contains(candidate.id()))
                .filter(candidate -> jointIds.contains(candidate.startJointId())
                        || jointIds.contains(candidate.endJointId()))
                .forEach(candidate -> collectFenceNetwork(candidate, rowIds, jointIds));
    }

    public List<FenceRow> fenceNetworkRows(String fenceRowId) {
        FenceRow startRow = findObject(fenceRowId)
                .filter(FenceRow.class::isInstance)
                .map(FenceRow.class::cast)
                .orElseThrow();
        Set<String> rowIds = new HashSet<>();
        Set<String> jointIds = new HashSet<>();
        collectFenceNetwork(startRow, rowIds, jointIds);
        return objects.stream()
                .filter(FenceRow.class::isInstance)
                .map(FenceRow.class::cast)
                .filter(row -> rowIds.contains(row.id()))
                .toList();
    }

    public int fenceNetworkGardenStoneAdjustment(String fenceRowId) {
        return fenceNetworkRows(fenceRowId).stream()
                .mapToInt(FenceRow::gardenStoneAdjustment)
                .sum();
    }

    public void setFenceNetworkGardenStoneAdjustment(String fenceRowId, int adjustment) {
        List<FenceRow> rows = fenceNetworkRows(fenceRowId);
        rows.forEach(row -> row.setGardenStoneAdjustment(0));
        rows.getFirst().setGardenStoneAdjustment(adjustment);
    }

    public boolean showFenceNetworkInventoryLabel(String fenceRowId) {
        return fenceNetworkRows(fenceRowId).stream().allMatch(FenceRow::showInventoryLabel);
    }

    public void setShowFenceNetworkInventoryLabel(String fenceRowId, boolean visible) {
        fenceNetworkRows(fenceRowId).forEach(row -> row.setShowInventoryLabel(visible));
    }

    public int standaloneGardenStoneCount() {
        return standaloneGardenStoneCount;
    }

    public void setStandaloneGardenStoneCount(int count) {
        standaloneGardenStoneCount = Math.max(0, count);
    }

    public boolean isFenceNetworkRepresentative(FenceRow row) {
        return fenceNetworkRows(row.id()).getFirst().id().equals(row.id());
    }

    public FenceRow splitFenceRow(FenceRow row, int segmentIndex, String newRowId) {
        if (isObjectLocked(row) || segmentIndex < 1 || segmentIndex >= row.segmentCount()) {
            throw new IllegalArgumentException("Ühenduspunkti saab lisada ainult aiaraja sisemisele piirile.");
        }
        int originalSegmentCount = row.segmentCount();
        String originalEndJointId = row.endJointId();
        double angle = Math.toRadians(row.rotationDegrees());
        double splitDistancePixels = segmentIndex * row.segmentLengthMeters() * pixelsPerMeter;
        Position splitPosition = new Position(
                row.position().x() + Math.cos(angle) * splitDistancePixels,
                row.position().y() + Math.sin(angle) * splitDistancePixels
        );
        String splitJointId = createFenceJoint(splitPosition);
        FenceRow continuation = new FenceRow(newRowId, row.name(), splitPosition);
        continuation.setSegmentCount(originalSegmentCount - segmentIndex);
        continuation.setSegmentLengthMeters(row.segmentLengthMeters());
        continuation.setRotationDegrees(row.rotationDegrees());
        continuation.setColorHex(row.colorHex());
        continuation.setWidthPixels(row.widthPixels());
        continuation.setGroupName(row.groupName());
        continuation.setNotes(row.notes());
        continuation.setHidden(row.hidden());
        continuation.setShowMapLabel(row.showMapLabel());
        continuation.setShowInventoryLabel(row.showInventoryLabel());
        continuation.setHighFence(row.highFence());
        if (row.customInventoryLabelPosition()) {
            continuation.setInventoryLabelOffset(row.inventoryLabelOffset());
        }
        addObject(continuation);
        row.setSegmentCount(segmentIndex);
        row.setJointIds(row.startJointId(), splitJointId);
        continuation.setJointIds(splitJointId, originalEndJointId);
        removeUnusedFenceJoints();
        synchronizeFenceRows(pixelsPerMeter);
        return continuation;
    }

    public boolean canRemoveFenceJoint(String jointId) {
        List<FenceRow> incidentRows = fenceRowsAtJoint(jointId);
        if (incidentRows.size() != 2 || incidentRows.stream().anyMatch(this::isObjectLocked)) {
            return false;
        }
        FenceRow first = incidentRows.get(0);
        FenceRow second = incidentRows.get(1);
        Position firstOuter = outerFenceJointPosition(first, jointId);
        Position secondOuter = outerFenceJointPosition(second, jointId);
        return Math.hypot(
                secondOuter.x() - firstOuter.x(),
                secondOuter.y() - firstOuter.y()
        ) > 0.0001;
    }

    public FenceRow removeFenceJoint(String jointId) {
        if (!canRemoveFenceJoint(jointId)) {
            throw new IllegalArgumentException("Seda ühenduspunkti ei saa naaberpunkte ühendades eemaldada.");
        }
        List<FenceRow> incidentRows = fenceRowsAtJoint(jointId);
        FenceRow survivor = incidentRows.get(0);
        FenceRow removed = incidentRows.get(1);
        survivor.setGardenStoneAdjustment(
                survivor.gardenStoneAdjustment() + removed.gardenStoneAdjustment()
        );
        String survivorOuterJointId = outerFenceJointId(survivor, jointId);
        String removedOuterJointId = outerFenceJointId(removed, jointId);
        Position survivorOuter = findFenceJoint(survivorOuterJointId).orElseThrow().position();
        Position removedOuter = findFenceJoint(removedOuterJointId).orElseThrow().position();
        double directLengthMeters = Math.hypot(
                removedOuter.x() - survivorOuter.x(),
                removedOuter.y() - survivorOuter.y()
        ) / pixelsPerMeter;
        survivor.setSegmentCount(Math.max(
                1,
                (int) Math.round(directLengthMeters / survivor.segmentLengthMeters())
        ));
        survivor.setJointIds(survivorOuterJointId, removedOuterJointId);
        objects.remove(removed);
        removeUnusedFenceJoints();
        synchronizeFenceRows(pixelsPerMeter);
        return survivor;
    }

    private List<FenceRow> fenceRowsAtJoint(String jointId) {
        return objects.stream()
                .filter(FenceRow.class::isInstance)
                .map(FenceRow.class::cast)
                .filter(row -> row.startJointId().equals(jointId) || row.endJointId().equals(jointId))
                .toList();
    }

    private String outerFenceJointId(FenceRow row, String sharedJointId) {
        return row.startJointId().equals(sharedJointId) ? row.endJointId() : row.startJointId();
    }

    private Position outerFenceJointPosition(FenceRow row, String sharedJointId) {
        return findFenceJoint(outerFenceJointId(row, sharedJointId)).orElseThrow().position();
    }

    public void migrateLegacyFenceConnections() {
        List<FenceRow> rows = objects.stream()
                .filter(FenceRow.class::isInstance)
                .map(FenceRow.class::cast)
                .toList();
        for (FenceRow row : rows) {
            if (!row.connectedAtStart()) {
                continue;
            }
            findObject(row.connectedToFenceRowId())
                    .filter(FenceRow.class::isInstance)
                    .map(FenceRow.class::cast)
                    .ifPresentOrElse(parent -> row.setJointIds(parent.endJointId(), row.endJointId()), () -> {
                    });
            row.disconnectStart();
        }
        removeUnusedFenceJoints();
        synchronizeFenceRows(pixelsPerMeter);
    }

    private void ensureFenceJoints(FenceRow row) {
        if (!row.startJointId().isBlank() && !row.endJointId().isBlank()) {
            return;
        }
        row.setJointIds(createFenceJoint(row.position()), createFenceJoint(row.endPosition(pixelsPerMeter)));
    }

    private void removeUnusedFenceJoints() {
        Set<String> usedJointIds = objects.stream()
                .filter(FenceRow.class::isInstance)
                .map(FenceRow.class::cast)
                .flatMap(row -> java.util.stream.Stream.of(row.startJointId(), row.endJointId()))
                .collect(Collectors.toSet());
        fenceJoints.removeIf(joint -> !usedJointIds.contains(joint.id()));
    }

    public void synchronizeFenceRows(double pixelsPerMeter) {
        for (PlannerObject object : objects) {
            if (object instanceof FenceRow fenceRow) {
                ensureFenceJoints(fenceRow);
                FenceJoint start = findFenceJoint(fenceRow.startJointId()).orElse(null);
                FenceJoint end = findFenceJoint(fenceRow.endJointId()).orElse(null);
                if (start != null && end != null) {
                    fenceRow.alignToEndpoints(start.position(), end.position(), pixelsPerMeter);
                }
            }
        }
    }

    public boolean showCables() {
        return showCables;
    }

    public void setShowCables(boolean showCables) {
        this.showCables = showCables;
    }

    public boolean showCableLabels() {
        return showCableLabels;
    }

    public void setShowCableLabels(boolean showCableLabels) {
        this.showCableLabels = showCableLabels;
    }

    public boolean showCableType(ConnectorType connectorType) {
        return switch (connectorType) {
            case SCHUKO_230V -> show230VCables;
            case INDUSTRIAL_16A -> show16ACables;
            case INDUSTRIAL_32A -> show32ACables;
            case INDUSTRIAL_63A -> show63ACables;
        };
    }

    public void setShowCableType(ConnectorType connectorType, boolean visible) {
        switch (connectorType) {
            case SCHUKO_230V -> show230VCables = visible;
            case INDUSTRIAL_16A -> show16ACables = visible;
            case INDUSTRIAL_32A -> show32ACables = visible;
            case INDUSTRIAL_63A -> show63ACables = visible;
        }
    }

    public boolean showObjectLabels() {
        return showObjectLabels;
    }

    public void setShowObjectLabels(boolean showObjectLabels) {
        this.showObjectLabels = showObjectLabels;
    }

    public boolean showTents() {
        return showTents;
    }

    public void setShowTents(boolean showTents) {
        this.showTents = showTents;
    }

    public boolean showPowerSources() {
        return showPowerSources;
    }

    public void setShowPowerSources(boolean showPowerSources) {
        this.showPowerSources = showPowerSources;
    }

    public boolean showCustomObjects() {
        return showCustomObjects;
    }

    public void setShowCustomObjects(boolean showCustomObjects) {
        this.showCustomObjects = showCustomObjects;
    }

    public boolean showTextObjects() {
        return showTextObjects;
    }

    public void setShowTextObjects(boolean showTextObjects) {
        this.showTextObjects = showTextObjects;
    }

    public boolean showMarkerObjects() {
        return showMarkerObjects;
    }

    public void setShowMarkerObjects(boolean showMarkerObjects) {
        this.showMarkerObjects = showMarkerObjects;
    }

    public boolean showAreaObjects() {
        return showAreaObjects;
    }

    public void setShowAreaObjects(boolean showAreaObjects) {
        this.showAreaObjects = showAreaObjects;
    }

    public boolean showLineObjects() {
        return showLineObjects;
    }

    public void setShowLineObjects(boolean showLineObjects) {
        this.showLineObjects = showLineObjects;
    }

    public boolean showFenceInventoryLabels() {
        return showFenceInventoryLabels;
    }

    public void setShowFenceInventoryLabels(boolean showFenceInventoryLabels) {
        this.showFenceInventoryLabels = showFenceInventoryLabels;
    }

    public Optional<PowerConnection> connectToPower(String sourceId, String consumerId, ConnectorType connectorType) {
        return connectToPower(sourceId, consumerId, connectorType, "");
    }

    public PowerConnectionValidationResult validatePowerConnection(
            String sourceId,
            String consumerId,
            ConnectorType connectorType,
            String outletId
    ) {
        PowerSource source = findObject(sourceId)
                .filter(PowerSource.class::isInstance)
                .map(PowerSource.class::cast)
                .orElse(null);
        if (source == null) {
            return PowerConnectionValidationResult.SOURCE_NOT_FOUND;
        }
        if (findObject(consumerId).filter(PowerConsumer.class::isInstance).isEmpty()) {
            return PowerConnectionValidationResult.CONSUMER_NOT_FOUND;
        }
        if (sourceId.equals(consumerId)) {
            return PowerConnectionValidationResult.SELF_CONNECTION;
        }
        if (wouldCreatePowerCycle(sourceId, consumerId)) {
            return PowerConnectionValidationResult.CYCLE_DETECTED;
        }

        ConnectorType selectedType = connectorType == null ? ConnectorType.SCHUKO_230V : connectorType;
        if (selectOutlet(source, consumerId, selectedType, outletId).isEmpty()) {
            return PowerConnectionValidationResult.NO_COMPATIBLE_OUTLET;
        }
        return PowerConnectionValidationResult.VALID;
    }

    public Optional<PowerConnection> connectToPower(String sourceId, String consumerId, ConnectorType connectorType, String outletId) {
        return connectToPower(sourceId, consumerId, connectorType, outletId, existingCableNotes(consumerId));
    }

    public Optional<PowerConnection> connectToPower(String sourceId, String consumerId, ConnectorType connectorType, String outletId, String cableNotes) {
        return connectToPower(sourceId, consumerId, connectorType, outletId, cableNotes, existingCableLengthNotes(consumerId));
    }

    public Optional<PowerConnection> connectToPower(
            String sourceId,
            String consumerId,
            ConnectorType connectorType,
            String outletId,
            String cableNotes,
            String cableLengthNotes
    ) {
        return connectToPower(sourceId, consumerId, connectorType, outletId, cableNotes, cableLengthNotes, "");
    }

    public Optional<PowerConnection> connectToPower(
            String sourceId,
            String consumerId,
            ConnectorType connectorType,
            String outletId,
            String cableNotes,
            String cableLengthNotes,
            String connectionId
    ) {
        return connectToPower(
                sourceId, consumerId, connectorType, outletId, cableNotes, cableLengthNotes, connectionId, true
        );
    }

    public Optional<PowerConnection> addAlternativePowerConnection(
            String sourceId,
            String consumerId,
            ConnectorType connectorType,
            String outletId
    ) {
        return addAlternativePowerConnection(sourceId, consumerId, connectorType, outletId, "", "", "");
    }

    public Optional<PowerConnection> addAlternativePowerConnection(
            String sourceId,
            String consumerId,
            ConnectorType connectorType,
            String outletId,
            String cableNotes,
            String cableLengthNotes,
            String connectionId
    ) {
        if (findPowerConnectionForConsumer(consumerId).isEmpty()) {
            return Optional.empty();
        }
        return connectToPower(
                sourceId, consumerId, connectorType, outletId, cableNotes, cableLengthNotes, connectionId, false
        );
    }

    private Optional<PowerConnection> connectToPower(
            String sourceId,
            String consumerId,
            ConnectorType connectorType,
            String outletId,
            String cableNotes,
            String cableLengthNotes,
            String connectionId,
            boolean defaultForConsumer
    ) {
        if (validatePowerConnection(sourceId, consumerId, connectorType, outletId)
                != PowerConnectionValidationResult.VALID) {
            return Optional.empty();
        }

        PowerSource source = findObject(sourceId)
                .filter(PowerSource.class::isInstance)
                .map(PowerSource.class::cast)
                .orElseThrow();

        ConnectorType selectedType = connectorType == null ? ConnectorType.SCHUKO_230V : connectorType;
        PowerOutlet selectedOutlet = selectOutlet(source, consumerId, selectedType, outletId).orElseThrow();

        List<Position> existingRoutePoints = defaultForConsumer ? existingCableRoutePoints(consumerId) : List.of();
        PowerConnection existingConnection = defaultForConsumer
                ? findPowerConnectionForConsumer(consumerId).orElse(null)
                : null;
        String selectedConnectionId = connectionId == null || connectionId.isBlank()
                ? existingConnection == null ? "" : existingConnection.id()
                : connectionId;
        if (defaultForConsumer) {
            powerConnections.removeIf(connection ->
                    connection.consumerId().equals(consumerId) && connection.defaultForConsumer());
        }
        PowerConnection connection = new PowerConnection(
                selectedConnectionId,
                sourceId,
                consumerId,
                selectedType,
                selectedOutlet.id(),
                cableNotes,
                cableLengthNotes,
                existingRoutePoints,
                existingConnection != null && existingConnection.customCableLabelPosition(),
                existingConnection == null ? new Position(0, 0) : existingConnection.cableLabelOffset(),
                defaultForConsumer
        );
        powerConnections.add(connection);
        PlanLayerEntry cableLayer = PlanLayerEntry.cable(connection.id());
        if (!layerOrder.contains(cableLayer)) {
            int consumerLayerIndex = layerOrder.indexOf(PlanLayerEntry.object(consumerId));
            layerOrder.add(consumerLayerIndex < 0 ? layerOrder.size() : consumerLayerIndex + 1, cableLayer);
        }
        return Optional.of(connection);
    }

    public void updateCableNotes(String consumerId, String cableNotes) {
        for (int index = 0; index < powerConnections.size(); index++) {
            PowerConnection connection = powerConnections.get(index);
            if (connection.consumerId().equals(consumerId) && connection.defaultForConsumer()) {
                powerConnections.set(index, new PowerConnection(
                        connection.id(),
                        connection.sourceId(),
                        connection.consumerId(),
                        connection.connectorType(),
                        connection.outletId(),
                        cableNotes,
                        connection.cableLengthNotes(),
                        connection.routePoints(),
                        connection.customCableLabelPosition(),
                        connection.cableLabelOffset(),
                        connection.defaultForConsumer()
                ));
                return;
            }
        }
    }

    public boolean updateCableNotesForConnection(String connectionId, String cableNotes) {
        return replacePowerConnection(connectionId, connection -> new PowerConnection(
                connection.id(), connection.sourceId(), connection.consumerId(), connection.connectorType(),
                connection.outletId(), cableNotes, connection.cableLengthNotes(), connection.routePoints(),
                connection.customCableLabelPosition(), connection.cableLabelOffset(), connection.defaultForConsumer()
        ));
    }

    public void updateCableLengthNotes(String consumerId, String cableLengthNotes) {
        for (int index = 0; index < powerConnections.size(); index++) {
            PowerConnection connection = powerConnections.get(index);
            if (connection.consumerId().equals(consumerId) && connection.defaultForConsumer()) {
                powerConnections.set(index, new PowerConnection(
                        connection.id(),
                        connection.sourceId(),
                        connection.consumerId(),
                        connection.connectorType(),
                        connection.outletId(),
                        connection.cableNotes(),
                        cableLengthNotes,
                        connection.routePoints(),
                        connection.customCableLabelPosition(),
                        connection.cableLabelOffset(),
                        connection.defaultForConsumer()
                ));
                return;
            }
        }
    }

    public boolean updateCableLengthNotesForConnection(String connectionId, String cableLengthNotes) {
        return replacePowerConnection(connectionId, connection -> new PowerConnection(
                connection.id(), connection.sourceId(), connection.consumerId(), connection.connectorType(),
                connection.outletId(), connection.cableNotes(), cableLengthNotes, connection.routePoints(),
                connection.customCableLabelPosition(), connection.cableLabelOffset(), connection.defaultForConsumer()
        ));
    }

    public boolean updatePowerConnection(
            String connectionId,
            String sourceId,
            ConnectorType connectorType,
            String outletId,
            String cableNotes,
            String cableLengthNotes
    ) {
        PowerConnection existing = findPowerConnection(connectionId).orElse(null);
        if (existing == null || validatePowerConnection(
                sourceId, existing.consumerId(), connectorType, outletId
        ) != PowerConnectionValidationResult.VALID) {
            return false;
        }
        PowerSource source = findObject(sourceId)
                .filter(PowerSource.class::isInstance)
                .map(PowerSource.class::cast)
                .orElseThrow();
        PowerOutlet outlet = selectOutlet(source, existing.consumerId(), connectorType, outletId).orElseThrow();
        return replacePowerConnection(connectionId, connection -> new PowerConnection(
                connection.id(), sourceId, connection.consumerId(), outlet.type(), outlet.id(),
                cableNotes, cableLengthNotes, connection.routePoints(), connection.customCableLabelPosition(),
                connection.cableLabelOffset(), connection.defaultForConsumer()
        ));
    }

    public boolean makeDefaultPowerConnection(String connectionId) {
        PowerConnection selected = findPowerConnection(connectionId).orElse(null);
        if (selected == null) {
            return false;
        }
        for (int index = 0; index < powerConnections.size(); index++) {
            PowerConnection connection = powerConnections.get(index);
            if (!connection.consumerId().equals(selected.consumerId())) {
                continue;
            }
            boolean makeDefault = connection.id().equals(connectionId);
            if (connection.defaultForConsumer() != makeDefault) {
                powerConnections.set(index, new PowerConnection(
                        connection.id(), connection.sourceId(), connection.consumerId(), connection.connectorType(),
                        connection.outletId(), connection.cableNotes(), connection.cableLengthNotes(),
                        connection.routePoints(), connection.customCableLabelPosition(), connection.cableLabelOffset(),
                        makeDefault
                ));
            }
        }
        findEquipmentContainer(selected.consumerId()).ifPresent(container -> container.equipment().stream()
                .filter(equipment -> equipment.powerConnectionId().equals(connectionId))
                .forEach(Equipment::useDefaultPower));
        return true;
    }

    private boolean replacePowerConnection(
            String connectionId,
            java.util.function.Function<PowerConnection, PowerConnection> replacement
    ) {
        for (int index = 0; index < powerConnections.size(); index++) {
            PowerConnection connection = powerConnections.get(index);
            if (connection.id().equals(connectionId)) {
                powerConnections.set(index, replacement.apply(connection));
                return true;
            }
        }
        return false;
    }

    private String existingCableNotes(String consumerId) {
        return findPowerConnectionForConsumer(consumerId)
                .map(PowerConnection::cableNotes)
                .orElse("");
    }

    private String existingCableLengthNotes(String consumerId) {
        return findPowerConnectionForConsumer(consumerId)
                .map(PowerConnection::cableLengthNotes)
                .orElse("");
    }

    private List<Position> existingCableRoutePoints(String consumerId) {
        return findPowerConnectionForConsumer(consumerId)
                .map(PowerConnection::routePoints)
                .orElse(List.of());
    }

    private Optional<PowerOutlet> selectOutlet(PowerSource source, String consumerId, ConnectorType connectorType, String outletId) {
        if (outletId != null && !outletId.isBlank()) {
            Optional<PowerOutlet> requestedOutlet = source.outlets().stream()
                    .filter(outlet -> outlet.id().equals(outletId))
                    .filter(outlet -> outlet.type() == connectorType)
                    .findFirst();
            if (requestedOutlet.isPresent()) {
                return requestedOutlet;
            }
        }

        List<PowerOutlet> matchingOutlets = source.outlets().stream()
                .filter(outlet -> outlet.type() == connectorType)
                .toList();
        if (matchingOutlets.isEmpty()) {
            return Optional.empty();
        }

        int requiredWatts = findObject(consumerId)
                .filter(PowerConsumer.class::isInstance)
                .map(PowerConsumer.class::cast)
                .map(PowerConsumer::requiredWatts)
                .orElse(0);
        return matchingOutlets.stream()
                .filter(outlet -> outlet.capacityWatts() - usedWatts(source.id(), outlet.id(), consumerId) >= requiredWatts)
                .findFirst()
                .or(() -> matchingOutlets.stream().findFirst());
    }

    private int usedWatts(String sourceId, String outletId, String ignoredConsumerId) {
        return powerConnections.stream()
                .filter(connection -> connection.sourceId().equals(sourceId))
                .filter(connection -> connection.outletId().equals(outletId))
                .filter(connection -> !connection.consumerId().equals(ignoredConsumerId))
                .mapToInt(this::powerDemandWatts)
                .sum();
    }

    public void disconnectPower(String consumerId) {
        removePowerConnections(connection -> connection.consumerId().equals(consumerId));
    }

    public void disconnectPowerFromOutlet(String outletId) {
        removePowerConnections(connection -> connection.outletId().equals(outletId));
    }

    public boolean disconnectPowerConnection(String connectionId) {
        boolean connectionExists = powerConnections.stream()
                .anyMatch(connection -> connection.id().equals(connectionId));
        if (connectionExists) {
            removePowerConnections(connection -> connection.id().equals(connectionId));
        }
        return connectionExists;
    }

    public boolean showCableLabel(String connectionId) {
        return !hiddenCableLabelConnectionIds.contains(connectionId);
    }

    public void setShowCableLabel(String connectionId, boolean visible) {
        if (visible) {
            hiddenCableLabelConnectionIds.remove(connectionId);
        } else if (findPowerConnection(connectionId).isPresent()) {
            hiddenCableLabelConnectionIds.add(connectionId);
        }
    }

    public double cableOpacity(String connectionId) {
        return cableOpacities.getOrDefault(connectionId, 1.0);
    }

    public void setCableOpacity(String connectionId, double opacity) {
        if (findPowerConnection(connectionId).isPresent()) {
            cableOpacities.put(connectionId, Math.max(0.0, Math.min(1.0, opacity)));
        }
    }

    public EquipmentPowerAssignmentResult assignEquipmentToPowerConnection(
            String containerId,
            String equipmentId,
            String connectionId
    ) {
        EquipmentContainer container = findEquipmentContainer(containerId).orElse(null);
        if (container == null) {
            return EquipmentPowerAssignmentResult.CONTAINER_NOT_FOUND;
        }

        Equipment equipment = container.equipment().stream()
                .filter(item -> item.id().equals(equipmentId))
                .findFirst()
                .orElse(null);
        if (equipment == null) {
            return EquipmentPowerAssignmentResult.EQUIPMENT_NOT_FOUND;
        }

        PowerConnection connection = powerConnections.stream()
                .filter(item -> item.id().equals(connectionId))
                .findFirst()
                .orElse(null);
        if (connection == null) {
            return EquipmentPowerAssignmentResult.CONNECTION_NOT_FOUND;
        }
        if (!connection.consumerId().equals(containerId)) {
            return EquipmentPowerAssignmentResult.CONNECTION_BELONGS_TO_ANOTHER_CONSUMER;
        }

        equipment.assignPowerConnection(connection.id());
        return EquipmentPowerAssignmentResult.SUCCESS;
    }

    public EquipmentPowerAssignmentResult useDefaultPowerForEquipment(String containerId, String equipmentId) {
        EquipmentContainer container = findEquipmentContainer(containerId).orElse(null);
        if (container == null) {
            return EquipmentPowerAssignmentResult.CONTAINER_NOT_FOUND;
        }

        Equipment equipment = container.equipment().stream()
                .filter(item -> item.id().equals(equipmentId))
                .findFirst()
                .orElse(null);
        if (equipment == null) {
            return EquipmentPowerAssignmentResult.EQUIPMENT_NOT_FOUND;
        }

        equipment.useDefaultPower();
        return EquipmentPowerAssignmentResult.SUCCESS;
    }

    private Optional<EquipmentContainer> findEquipmentContainer(String containerId) {
        return findObject(containerId)
                .filter(EquipmentContainer.class::isInstance)
                .map(EquipmentContainer.class::cast);
    }

    private void removePowerConnections(Predicate<PowerConnection> predicate) {
        Set<String> removedConnectionIds = powerConnections.stream()
                .filter(predicate)
                .map(PowerConnection::id)
                .collect(Collectors.toSet());
        powerConnections.removeIf(predicate);
        layerOrder.removeIf(entry -> entry.type() == PlanLayerEntry.Type.CABLE
                && removedConnectionIds.contains(entry.id()));
        hiddenCableLabelConnectionIds.removeAll(removedConnectionIds);
        if (removedConnectionIds.isEmpty()) {
            return;
        }
        objects.stream()
                .filter(EquipmentContainer.class::isInstance)
                .map(EquipmentContainer.class::cast)
                .flatMap(container -> container.equipment().stream())
                .filter(equipment -> removedConnectionIds.contains(equipment.powerConnectionId()))
                .forEach(Equipment::useDefaultPower);
    }

    public void updateConnectorTypeForOutlet(String outletId, ConnectorType connectorType) {
        ConnectorType selectedType = connectorType == null ? ConnectorType.SCHUKO_230V : connectorType;
        for (int index = 0; index < powerConnections.size(); index++) {
            PowerConnection connection = powerConnections.get(index);
            if (connection.outletId().equals(outletId)) {
                powerConnections.set(index, new PowerConnection(
                        connection.id(),
                        connection.sourceId(),
                        connection.consumerId(),
                        selectedType,
                        connection.outletId(),
                        connection.cableNotes(),
                        connection.cableLengthNotes(),
                        connection.routePoints(),
                        connection.customCableLabelPosition(),
                        connection.cableLabelOffset(),
                        connection.defaultForConsumer()
                ));
            }
        }
    }

    public void addCableRoutePoint(String consumerId, Position point) {
        insertCableRoutePoint(consumerId, -1, point);
    }

    public void insertCableRoutePoint(String consumerId, int routePointIndex, Position point) {
        if (point == null) {
            return;
        }
        for (int index = 0; index < powerConnections.size(); index++) {
            PowerConnection connection = powerConnections.get(index);
            if (connection.consumerId().equals(consumerId) && connection.defaultForConsumer()) {
                List<Position> routePoints = new ArrayList<>(connection.routePoints());
                if (routePointIndex < 0 || routePointIndex > routePoints.size()) {
                    routePoints.add(point);
                } else {
                    routePoints.add(routePointIndex, point);
                }
                powerConnections.set(index, new PowerConnection(
                        connection.id(),
                        connection.sourceId(),
                        connection.consumerId(),
                        connection.connectorType(),
                        connection.outletId(),
                        connection.cableNotes(),
                        connection.cableLengthNotes(),
                        routePoints,
                        connection.customCableLabelPosition(),
                        connection.cableLabelOffset(),
                        connection.defaultForConsumer()
                ));
                return;
            }
        }
    }

    public void updateCableRoutePoints(String consumerId, List<Position> routePoints) {
        for (int index = 0; index < powerConnections.size(); index++) {
            PowerConnection connection = powerConnections.get(index);
            if (connection.consumerId().equals(consumerId) && connection.defaultForConsumer()) {
                powerConnections.set(index, new PowerConnection(
                        connection.id(),
                        connection.sourceId(),
                        connection.consumerId(),
                        connection.connectorType(),
                        connection.outletId(),
                        connection.cableNotes(),
                        connection.cableLengthNotes(),
                        routePoints,
                        connection.customCableLabelPosition(),
                        connection.cableLabelOffset(),
                        connection.defaultForConsumer()
                ));
                return;
            }
        }
    }

    public void updateCableRoutePointsForConnection(String connectionId, List<Position> routePoints) {
        for (int index = 0; index < powerConnections.size(); index++) {
            PowerConnection connection = powerConnections.get(index);
            if (connection.id().equals(connectionId)) {
                powerConnections.set(index, new PowerConnection(
                        connection.id(),
                        connection.sourceId(),
                        connection.consumerId(),
                        connection.connectorType(),
                        connection.outletId(),
                        connection.cableNotes(),
                        connection.cableLengthNotes(),
                        routePoints,
                        connection.customCableLabelPosition(),
                        connection.cableLabelOffset(),
                        connection.defaultForConsumer()
                ));
                return;
            }
        }
    }

    public void updateCableLabelOffset(String consumerId, Position offset) {
        for (int index = 0; index < powerConnections.size(); index++) {
            PowerConnection connection = powerConnections.get(index);
            if (connection.consumerId().equals(consumerId) && connection.defaultForConsumer()) {
                powerConnections.set(index, new PowerConnection(
                        connection.id(),
                        connection.sourceId(),
                        connection.consumerId(),
                        connection.connectorType(),
                        connection.outletId(),
                        connection.cableNotes(),
                        connection.cableLengthNotes(),
                        connection.routePoints(),
                        true,
                        offset,
                        connection.defaultForConsumer()
                ));
                return;
            }
        }
    }

    public void updateCableLabelOffsetForConnection(String connectionId, Position offset) {
        for (int index = 0; index < powerConnections.size(); index++) {
            PowerConnection connection = powerConnections.get(index);
            if (connection.id().equals(connectionId)) {
                powerConnections.set(index, new PowerConnection(
                        connection.id(),
                        connection.sourceId(),
                        connection.consumerId(),
                        connection.connectorType(),
                        connection.outletId(),
                        connection.cableNotes(),
                        connection.cableLengthNotes(),
                        connection.routePoints(),
                        true,
                        offset,
                        connection.defaultForConsumer()
                ));
                return;
            }
        }
    }

    public void resetCableLabelOffset(String consumerId) {
        for (int index = 0; index < powerConnections.size(); index++) {
            PowerConnection connection = powerConnections.get(index);
            if (connection.consumerId().equals(consumerId) && connection.defaultForConsumer()) {
                powerConnections.set(index, new PowerConnection(
                        connection.id(),
                        connection.sourceId(),
                        connection.consumerId(),
                        connection.connectorType(),
                        connection.outletId(),
                        connection.cableNotes(),
                        connection.cableLengthNotes(),
                        connection.routePoints(),
                        false,
                        new Position(0, 0),
                        connection.defaultForConsumer()
                ));
                return;
            }
        }
    }

    public boolean resetCableLabelOffsetForConnection(String connectionId) {
        return replacePowerConnection(connectionId, connection -> new PowerConnection(
                connection.id(), connection.sourceId(), connection.consumerId(), connection.connectorType(),
                connection.outletId(), connection.cableNotes(), connection.cableLengthNotes(),
                connection.routePoints(), false, new Position(0, 0), connection.defaultForConsumer()
        ));
    }

    public void clearCableRoutePoints(String consumerId) {
        updateCableRoutePoints(consumerId, List.of());
    }

    public Optional<PowerConnection> findPowerConnectionForConsumer(String consumerId) {
        return powerConnections.stream()
                .filter(connection -> connection.consumerId().equals(consumerId))
                .filter(PowerConnection::defaultForConsumer)
                .findFirst();
    }

    public Optional<PowerConnection> findPowerConnection(String connectionId) {
        return powerConnections.stream()
                .filter(connection -> connection.id().equals(connectionId))
                .findFirst();
    }

    public List<PowerConnection> findPowerConnectionsForConsumer(String consumerId) {
        return powerConnections.stream()
                .filter(connection -> connection.consumerId().equals(consumerId))
                .toList();
    }

    public int powerDemandWatts(PowerConnection connection) {
        return powerDemandWatts(connection, new HashSet<>());
    }

    public int outletDemandWatts(String outletId) {
        return powerConnections.stream()
                .filter(connection -> connection.outletId().equals(outletId))
                .mapToInt(this::powerDemandWatts)
                .sum();
    }

    private int powerDemandWatts(PowerConnection connection, Set<String> visitedSourceIds) {
        return findObject(connection.consumerId())
                .map(object -> {
                    if (object instanceof DistributionPanel panel) {
                        if (!connection.defaultForConsumer() || !visitedSourceIds.add(panel.id())) {
                            return 0;
                        }
                        int downstreamWatts = powerConnections.stream()
                                .filter(candidate -> candidate.sourceId().equals(panel.id()))
                                .mapToInt(candidate -> powerDemandWatts(candidate, visitedSourceIds))
                                .sum();
                        visitedSourceIds.remove(panel.id());
                        return downstreamWatts;
                    }
                    if (object instanceof EquipmentContainer container) {
                        return container.equipment().stream()
                                .filter(equipment -> equipment.usesDefaultPower()
                                        ? connection.defaultForConsumer()
                                        : connection.id().equals(equipment.powerConnectionId()))
                                .mapToInt(Equipment::requiredWatts)
                                .sum();
                    }
                    return connection.defaultForConsumer() && object instanceof PowerConsumer consumer
                            ? consumer.requiredWatts()
                            : 0;
                })
                .orElse(0);
    }

    private boolean wouldCreatePowerCycle(String sourceId, String consumerId) {
        if (sourceId.equals(consumerId)) {
            return true;
        }
        Set<String> visited = new HashSet<>();
        List<String> pending = new ArrayList<>();
        pending.add(consumerId);
        while (!pending.isEmpty()) {
            String currentSourceId = pending.removeLast();
            if (!visited.add(currentSourceId)) {
                continue;
            }
            for (PowerConnection connection : powerConnections) {
                if (!connection.sourceId().equals(currentSourceId)) {
                    continue;
                }
                if (connection.consumerId().equals(sourceId)) {
                    return true;
                }
                pending.add(connection.consumerId());
            }
        }
        return false;
    }

    public int clearInvalidEquipmentPowerAssignments() {
        int clearedAssignments = 0;
        for (PlannerObject object : objects) {
            if (!(object instanceof EquipmentContainer container)) {
                continue;
            }
            for (Equipment equipment : container.equipment()) {
                if (equipment.usesDefaultPower()) {
                    continue;
                }
                boolean validAssignment = powerConnections.stream()
                        .anyMatch(connection -> connection.id().equals(equipment.powerConnectionId())
                                && connection.consumerId().equals(object.id()));
                if (!validAssignment) {
                    equipment.useDefaultPower();
                    clearedAssignments++;
                }
            }
        }
        return clearedAssignments;
    }

    public List<PowerConnection> powerConnections() {
        return Collections.unmodifiableList(powerConnections);
    }

    public Set<String> hiddenGroups() {
        return Collections.unmodifiableSet(hiddenGroups);
    }

    public void setGroupHidden(String groupName, boolean hidden) {
        if (groupName == null || groupName.isBlank()) {
            return;
        }
        if (hidden) {
            hiddenGroups.add(groupName);
        } else {
            hiddenGroups.remove(groupName);
        }
    }

    public void clearHiddenGroups() {
        hiddenGroups.clear();
    }

    public Set<String> lockedGroups() {
        return Collections.unmodifiableSet(lockedGroups);
    }

    public boolean isGroupLocked(String groupName) {
        return groupName != null && !groupName.isBlank() && lockedGroups.contains(groupName);
    }

    public boolean isObjectLocked(PlannerObject object) {
        if (object == null) {
            return false;
        }
        String groupName = object.groupName().isBlank() ? "Määramata" : object.groupName();
        return object.locked() || lockedGroups.contains(groupName);
    }

    public void setGroupLocked(String groupName, boolean locked) {
        if (groupName == null || groupName.isBlank()) {
            return;
        }
        if (locked) {
            lockedGroups.add(groupName);
        } else {
            lockedGroups.remove(groupName);
        }
    }

    public void clearLockedGroups() {
        lockedGroups.clear();
    }
}
