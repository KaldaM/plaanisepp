package ee.matteus.plaanisepp.core.model;

/** Visual and setup preset for a regular tent object. */
public enum TentPreset {
    STANDARD,
    DJ_TRUCK;

    public static TentPreset fromStorageValue(String value) {
        try {
            return value == null || value.isBlank() ? STANDARD : TentPreset.valueOf(value);
        } catch (IllegalArgumentException exception) {
            return STANDARD;
        }
    }
}
