package model;

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
