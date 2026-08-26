package ee.matteus.plaanisepp.core.model;

public abstract class PlannerObject {
    private final String id;
    private String name;
    private Position position;
    private boolean locked;
    private String groupName;
    private String notes;
    private boolean hidden;
    private boolean showMapLabel;
    private boolean customMapLabelPosition;
    private Position mapLabelOffset;
    private double opacity;

    protected PlannerObject(String id, String name, Position position) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.groupName = "";
        this.notes = "";
        this.hidden = false;
        this.showMapLabel = true;
        this.customMapLabelPosition = false;
        this.mapLabelOffset = new Position(0, 0);
        this.opacity = 1.0;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public void rename(String name) {
        this.name = name;
    }

    public Position position() {
        return position;
    }

    public void moveTo(Position position) {
        if (!locked) {
            this.position = position;
        }
    }

    protected void moveToIgnoringLock(Position position) {
        this.position = position;
    }

    public boolean locked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public String groupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName == null ? "" : groupName;
    }

    public String notes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes == null ? "" : notes;
    }

    public boolean hidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean showMapLabel() {
        return showMapLabel;
    }

    public void setShowMapLabel(boolean showMapLabel) {
        this.showMapLabel = showMapLabel;
    }

    public boolean customMapLabelPosition() {
        return customMapLabelPosition;
    }

    public Position mapLabelOffset() {
        return mapLabelOffset;
    }

    public void setMapLabelOffset(Position mapLabelOffset) {
        this.mapLabelOffset = mapLabelOffset == null ? new Position(0, 0) : mapLabelOffset;
        this.customMapLabelPosition = true;
    }

    public void resetMapLabelPosition() {
        this.mapLabelOffset = new Position(0, 0);
        this.customMapLabelPosition = false;
    }

    public double opacity() {
        return opacity;
    }

    public void setOpacity(double opacity) {
        this.opacity = Math.max(0.0, Math.min(1.0, opacity));
    }
}
