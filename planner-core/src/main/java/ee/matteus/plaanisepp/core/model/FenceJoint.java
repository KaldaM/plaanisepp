package ee.matteus.plaanisepp.core.model;

public final class FenceJoint {
    private final String id;
    private Position position;

    public FenceJoint(String id, Position position) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Aia ühenduspunkti tunnus ei tohi olla tühi.");
        }
        this.id = id;
        this.position = position;
    }

    public String id() {
        return id;
    }

    public Position position() {
        return position;
    }

    public void moveTo(Position position) {
        this.position = position;
    }
}
