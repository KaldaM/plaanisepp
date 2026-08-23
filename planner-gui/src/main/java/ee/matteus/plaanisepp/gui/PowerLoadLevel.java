package ee.matteus.plaanisepp.gui;

enum PowerLoadLevel {
    NORMAL("#16a34a"),
    WARNING("#ca8a04"),
    HIGH("#ea580c"),
    OVERLOADED("#dc2626");

    private final String colorHex;

    PowerLoadLevel(String colorHex) {
        this.colorHex = colorHex;
    }

    static PowerLoadLevel from(int usedWatts, int capacityWatts) {
        if (capacityWatts <= 0) {
            return usedWatts > 0 ? OVERLOADED : NORMAL;
        }
        double utilization = (double) usedWatts / capacityWatts;
        if (utilization > 1.0) {
            return OVERLOADED;
        }
        if (utilization >= 0.9) {
            return HIGH;
        }
        if (utilization >= 0.7) {
            return WARNING;
        }
        return NORMAL;
    }

    String colorHex() {
        return colorHex;
    }
}
