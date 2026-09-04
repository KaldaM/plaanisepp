package ee.matteus.plaanisepp.core.model;

public record PlanLayerEntry(Type type, String id) {
    public PlanLayerEntry {
        if (type == null || id == null || id.isBlank()) {
            throw new IllegalArgumentException("Kihikirje tüüp ja ID peavad olema määratud.");
        }
    }

    public static PlanLayerEntry object(String id) {
        return new PlanLayerEntry(Type.OBJECT, id);
    }

    public static PlanLayerEntry cable(String id) {
        return new PlanLayerEntry(Type.CABLE, id);
    }

    public enum Type { OBJECT, CABLE }
}
