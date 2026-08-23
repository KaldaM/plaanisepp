package ee.matteus.plaanisepp.core.model;

public enum PowerConnectionValidationResult {
    VALID,
    SOURCE_NOT_FOUND,
    CONSUMER_NOT_FOUND,
    SELF_CONNECTION,
    CYCLE_DETECTED,
    NO_COMPATIBLE_OUTLET
}
