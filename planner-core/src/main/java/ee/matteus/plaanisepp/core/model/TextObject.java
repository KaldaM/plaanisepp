package ee.matteus.plaanisepp.core.model;

public class TextObject extends PlannerObject {
    public static final double DEFAULT_FONT_SIZE = 14.0;

    private String colorHex;
    private double fontSize;
    private double textOpacity;
    private String sourceObjectId;
    private boolean syncSourceNotes;
    private boolean showReferenceLine;
    private boolean inventorySource;

    public TextObject(String id, String name, Position position) {
        super(id, name, position);
        this.colorHex = "#111827";
        this.fontSize = DEFAULT_FONT_SIZE;
        this.textOpacity = 1.0;
        this.sourceObjectId = "";
        this.syncSourceNotes = false;
        this.showReferenceLine = false;
        this.inventorySource = false;
    }

    public String colorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex == null || colorHex.isBlank() ? "#111827" : colorHex;
    }

    public double fontSize() {
        return fontSize;
    }

    public void setFontSize(double fontSize) {
        if (fontSize <= 0) {
            throw new IllegalArgumentException("Teksti suurus peab olema positiivne.");
        }
        this.fontSize = fontSize;
    }

    public double textOpacity() {
        return textOpacity;
    }

    public void setTextOpacity(double textOpacity) {
        this.textOpacity = Math.max(0.0, Math.min(1.0, textOpacity));
    }

    public String sourceObjectId() {
        return sourceObjectId;
    }

    public void setSourceObjectId(String sourceObjectId) {
        this.sourceObjectId = sourceObjectId == null ? "" : sourceObjectId;
        if (this.sourceObjectId.isBlank()) {
            syncSourceNotes = false;
            showReferenceLine = false;
        }
    }

    public boolean syncSourceNotes() {
        return syncSourceNotes;
    }

    public void setSyncSourceNotes(boolean syncSourceNotes) {
        this.syncSourceNotes = syncSourceNotes && !sourceObjectId.isBlank();
    }

    public boolean showReferenceLine() {
        return showReferenceLine;
    }

    public void setShowReferenceLine(boolean showReferenceLine) {
        this.showReferenceLine = showReferenceLine && !sourceObjectId.isBlank();
    }

    public boolean inventorySource() {
        return inventorySource;
    }

    public void setInventorySource(boolean inventorySource) {
        this.inventorySource = inventorySource && !sourceObjectId.isBlank();
    }
}
