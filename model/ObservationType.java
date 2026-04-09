package model;


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
