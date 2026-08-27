package ee.matteus.plaanisepp.core.model;

public class DistributionPanel extends PowerSource implements PowerConnectable {
    public static final String DEFAULT_COLOR_HEX = "#f59e0b";

    private Position powerConnectionOffset = new Position(0, 0);

    public DistributionPanel(String id, String name, Position position) {
        super(id, name, position);
        setColorHex(DEFAULT_COLOR_HEX);
    }

    @Override
    public void setColorHex(String colorHex) {
        super.setColorHex(colorHex == null || colorHex.isBlank() ? DEFAULT_COLOR_HEX : colorHex);
    }

    @Override
    public int requiredWatts() {
        return 0;
    }

    @Override
    public Position powerConnectionOffset() {
        return powerConnectionOffset;
    }

    @Override
    public void setPowerConnectionOffset(Position offset) {
        powerConnectionOffset = offset == null ? new Position(0, 0) : offset;
    }
}
