package ee.matteus.plaanisepp.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PowerSource extends PlannerObject {
    public static final String DEFAULT_COLOR_HEX = "#2563eb";
    public static final double DEFAULT_SIZE_PIXELS = 24.0;

    private final List<PowerOutlet> outlets = new ArrayList<>();
    private String colorHex = DEFAULT_COLOR_HEX;
    private double sizePixels = DEFAULT_SIZE_PIXELS;

    public PowerSource(String id, String name, Position position) {
        super(id, name, position);
    }

    public void addOutlet(PowerOutlet outlet) {
        outlets.add(outlet);
    }

    public void removeOutlet(int index) {
        outlets.remove(index);
    }

    public List<PowerOutlet> outlets() {
        return Collections.unmodifiableList(outlets);
    }

    public int totalCapacityWatts() {
        return outlets.stream().mapToInt(PowerOutlet::capacityWatts).sum();
    }

    public String colorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex == null || colorHex.isBlank() ? DEFAULT_COLOR_HEX : colorHex;
    }

    public double sizePixels() {
        return sizePixels;
    }

    public void setSizePixels(double sizePixels) {
        if (sizePixels <= 0) {
            throw new IllegalArgumentException("Elektrikilbi suurus peab olema positiivne.");
        }
        this.sizePixels = sizePixels;
    }
}
