package model;

/**
 * Enum representing the status of an observation.
 * Observations can be ACTIVE or VOIDED.
 *
 * SOLID Principle Applied:
 * - Open/Closed Principle: New statuses can be added by extending this enum
 *   without modifying existing code.
 */
public enum ObservationStatus {
    ACTIVE("Active"),
    VOIDED("Voided");

    private final String displayName;

    ObservationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
