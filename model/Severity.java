package model;

/**
 * Enum representing the severity level of an observation.
 *
 * SOLID Principle Applied:
 * - Open/Closed Principle: New severity levels can be added by extending this enum
 *   without modifying existing code.
 */
public enum Severity {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    private final String displayName;

    Severity(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
