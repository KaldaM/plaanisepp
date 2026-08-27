package ee.matteus.plaanisepp.core.model;

public class FenceRow extends PlannerObject {
    public static final double DEFAULT_SEGMENT_LENGTH_METERS = 3.5;
    public static final String DEFAULT_COLOR_HEX = "#64748b";
    public static final double DEFAULT_WIDTH_PIXELS = 5.0;

    private int segmentCount;
    private double segmentLengthMeters;
    private double rotationDegrees;
    private String colorHex;
    private double widthPixels;
    private String connectedToFenceRowId;
    private String startJointId;
    private String endJointId;
    private boolean customInventoryLabelPosition;
    private boolean showInventoryLabel = true;
    private Position inventoryLabelOffset;
    private int gardenStoneAdjustment;

    public FenceRow(String id, String name, Position position) {
        super(id, name, position);
        segmentCount = 1;
        segmentLengthMeters = DEFAULT_SEGMENT_LENGTH_METERS;
        colorHex = DEFAULT_COLOR_HEX;
        widthPixels = DEFAULT_WIDTH_PIXELS;
        connectedToFenceRowId = "";
        startJointId = "";
        endJointId = "";
        inventoryLabelOffset = new Position(0, 0);
    }

    public int segmentCount() {
        return segmentCount;
    }

    public void setSegmentCount(int segmentCount) {
        if (segmentCount < 1) {
            throw new IllegalArgumentException("Aiarida peab sisaldama vähemalt ühte aeda.");
        }
        this.segmentCount = segmentCount;
    }

    public double segmentLengthMeters() {
        return segmentLengthMeters;
    }

    public void setSegmentLengthMeters(double segmentLengthMeters) {
        if (segmentLengthMeters <= 0) {
            throw new IllegalArgumentException("Aialõigu pikkus peab olema positiivne.");
        }
        this.segmentLengthMeters = segmentLengthMeters;
    }

    public double totalLengthMeters() {
        return segmentCount * segmentLengthMeters;
    }

    public double rotationDegrees() {
        return rotationDegrees;
    }

    public void setRotationDegrees(double rotationDegrees) {
        this.rotationDegrees = rotationDegrees;
    }

    public String colorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex == null || colorHex.isBlank() ? DEFAULT_COLOR_HEX : colorHex;
    }

    public double widthPixels() {
        return widthPixels;
    }

    public void setWidthPixels(double widthPixels) {
        if (widthPixels <= 0) {
            throw new IllegalArgumentException("Aia joone laius peab olema positiivne.");
        }
        this.widthPixels = widthPixels;
    }

    public Position endPosition(double pixelsPerMeter) {
        double angleRadians = Math.toRadians(rotationDegrees);
        double lengthPixels = totalLengthMeters() * pixelsPerMeter;
        return new Position(
                position().x() + Math.cos(angleRadians) * lengthPixels,
                position().y() + Math.sin(angleRadians) * lengthPixels
        );
    }

    public String connectedToFenceRowId() {
        return connectedToFenceRowId;
    }

    public boolean connectedAtStart() {
        return !connectedToFenceRowId.isBlank();
    }

    public void connectStartTo(String fenceRowId) {
        if (fenceRowId == null || fenceRowId.isBlank() || id().equals(fenceRowId)) {
            throw new IllegalArgumentException("Aiarida peab ühenduma teise olemasoleva aiarajaga.");
        }
        connectedToFenceRowId = fenceRowId;
    }

    public void disconnectStart() {
        connectedToFenceRowId = "";
    }

    public String startJointId() {
        return startJointId;
    }

    public String endJointId() {
        return endJointId;
    }

    public void setJointIds(String startJointId, String endJointId) {
        if (startJointId == null || startJointId.isBlank()
                || endJointId == null || endJointId.isBlank()
                || startJointId.equals(endJointId)) {
            throw new IllegalArgumentException("Aiarida peab ühendama kahte erinevat ühenduspunkti.");
        }
        this.startJointId = startJointId;
        this.endJointId = endJointId;
    }

    public boolean customInventoryLabelPosition() {
        return customInventoryLabelPosition;
    }

    public boolean showInventoryLabel() {
        return showInventoryLabel;
    }

    public void setShowInventoryLabel(boolean showInventoryLabel) {
        this.showInventoryLabel = showInventoryLabel;
    }

    public Position inventoryLabelOffset() {
        return inventoryLabelOffset;
    }

    public void setInventoryLabelOffset(Position inventoryLabelOffset) {
        this.inventoryLabelOffset = inventoryLabelOffset == null ? new Position(0, 0) : inventoryLabelOffset;
        customInventoryLabelPosition = true;
    }

    public void resetInventoryLabelPosition() {
        inventoryLabelOffset = new Position(0, 0);
        customInventoryLabelPosition = false;
    }

    public int gardenStoneAdjustment() {
        return gardenStoneAdjustment;
    }

    public void setGardenStoneAdjustment(int gardenStoneAdjustment) {
        this.gardenStoneAdjustment = gardenStoneAdjustment;
    }

    void alignToEndpoints(Position start, Position end, double pixelsPerMeter) {
        moveToIgnoringLock(start);
        setRotationDegrees(Math.toDegrees(Math.atan2(end.y() - start.y(), end.x() - start.x())));
        double lengthMeters = Math.hypot(end.x() - start.x(), end.y() - start.y()) / pixelsPerMeter;
        setSegmentLengthMeters(lengthMeters / segmentCount);
    }

    void alignDirectionToEndpoints(Position start, Position end) {
        moveToIgnoringLock(start);
        setRotationDegrees(Math.toDegrees(Math.atan2(end.y() - start.y(), end.x() - start.x())));
    }

    public void rotateEndToward(Position target) {
        if (locked()) {
            return;
        }
        double deltaX = target.x() - position().x();
        double deltaY = target.y() - position().y();
        if (deltaX == 0 && deltaY == 0) {
            return;
        }
        setRotationDegrees(Math.toDegrees(Math.atan2(deltaY, deltaX)));
    }

    public void moveStartTowardKeepingEnd(Position target, double pixelsPerMeter) {
        if (locked() || pixelsPerMeter <= 0) {
            return;
        }
        Position fixedEnd = endPosition(pixelsPerMeter);
        double deltaX = target.x() - fixedEnd.x();
        double deltaY = target.y() - fixedEnd.y();
        double distance = Math.hypot(deltaX, deltaY);
        if (distance == 0) {
            return;
        }
        double lengthPixels = totalLengthMeters() * pixelsPerMeter;
        Position newStart = new Position(
                fixedEnd.x() + deltaX / distance * lengthPixels,
                fixedEnd.y() + deltaY / distance * lengthPixels
        );
        moveTo(newStart);
        setRotationDegrees(Math.toDegrees(Math.atan2(
                fixedEnd.y() - newStart.y(),
                fixedEnd.x() - newStart.x()
        )));
    }
}
