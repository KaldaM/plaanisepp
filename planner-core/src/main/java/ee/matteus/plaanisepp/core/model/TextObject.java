package ee.matteus.plaanisepp.core.model;

public class TextObject extends PlannerObject {
    public static final double DEFAULT_FONT_SIZE = 14.0;

    private String colorHex;
    private double fontSize;
    private double textOpacity;

    public TextObject(String id, String name, Position position) {
        super(id, name, position);
        this.colorHex = "#111827";
        this.fontSize = DEFAULT_FONT_SIZE;
        this.textOpacity = 1.0;
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
}
