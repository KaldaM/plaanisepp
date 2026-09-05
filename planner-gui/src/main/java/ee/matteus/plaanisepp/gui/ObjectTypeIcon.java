package ee.matteus.plaanisepp.gui;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Polyline;
import javafx.scene.control.Tooltip;

/** Shared monochrome strokes for the desktop and PDF legends, in a 16 × 16 box. */
enum ObjectTypeIcon {
    FENCE("Aed / aiavõrk", new double[]{2,2,2,14}, new double[]{8,2,8,14}, new double[]{14,2,14,14}, new double[]{2,5,14,5}, new double[]{2,11,14,11}),
    LINE("Joon", new double[]{2,13,7,4,14,9}),
    AREA("Ala", new double[]{2,4,10,2,14,9,9,14,2,11,2,4}),
    TENT("Telk", new double[]{1,13,8,2,15,13,1,13}, new double[]{5,13,8,7,11,13}),
    POWER("Elektrikilp", new double[]{9,1,3,9,8,9,6,15,13,6,8,6,9,1}),
    DISTRIBUTION("Alajaotuskilp", new double[]{9,1,3,9,8,9,6,15,13,6,8,6,9,1}, new double[]{1,1,1,15}, new double[]{15,1,15,15}),
    TEXT("Tekst", new double[]{2,3,14,3}, new double[]{8,3,8,14}, new double[]{5,14,11,14}),
    CIRCLE("Ring", new double[]{8,2,12,3,14,8,12,13,8,14,4,13,2,8,4,3,8,2}),
    RECTANGLE("Ristkülik", new double[]{2,3,14,3,14,13,2,13,2,3}),
    MUSIC("Red Bull DJ Truck", new double[]{5,12,5,3,13,1,13,10}, new double[]{5,12,3,11,1,13,3,14,5,12}, new double[]{13,10,11,9,9,11,11,12,13,10}),
    MARKER("Marker", new double[]{8,15,2,7,3,3,8,1,13,3,14,7,8,15});

    final String title;
    final double[][] strokes;
    ObjectTypeIcon(String title, double[]... strokes) { this.title = title; this.strokes = strokes; }

    static ObjectTypeIcon forType(String type) {
        return switch (type) {
            case "Aiarida", "Aiavõrk", "Aed", "Aed / aiavõrk" -> FENCE;
            case "Joon" -> LINE;
            case "Ala" -> AREA;
            case "Telk" -> TENT;
            case "Elektrikapp", "Elektrikilp" -> POWER;
            case "Alajaotuskilp" -> DISTRIBUTION;
            case "Tekst" -> TEXT;
            case "Ring" -> CIRCLE;
            case "Red Bull DJ Truck" -> MUSIC;
            case "Marker" -> MARKER;
            default -> RECTANGLE;
        };
    }

    Pane graphic() {
        Pane box = new Pane();
        box.setMinSize(16, 16); box.setPrefSize(16, 16); box.setMaxSize(16, 16);
        box.setAccessibleText(title);
        Tooltip.install(box, new Tooltip(title));
        for (double[] points : strokes) {
            Polyline line = new Polyline(points);
            line.getStyleClass().add("object-type-icon");
            box.getChildren().add(line);
        }
        return box;
    }
}
