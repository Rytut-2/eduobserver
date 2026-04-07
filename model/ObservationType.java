package model;

/**
 * Enum representing the type of observation.
 * Observations can be either Academic or Disciplinary.
 *
 * SOLID Principle Applied:
 * - Open/Closed Principle: New observation types can be added by extending this enum
 *   without modifying existing code.
 */
public enum ObservationType {
    ACADEMIC("Academic"),
    DISCIPLINARY("Disciplinary");

    private final String displayName;

    ObservationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
