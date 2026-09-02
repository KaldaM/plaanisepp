package ee.matteus.plaanisepp.core.map;

public enum RegularMapStyle {
    GRAYSCALE("Halltoonides kaart"),
    TOPOGRAPHIC("Värviline põhikaart");

    private final String displayName;

    RegularMapStyle(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
