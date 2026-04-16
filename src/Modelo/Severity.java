package Modelo;

public enum Severity {

    LEVE("Leve", "🟡", "Falta leve - Amonestación verbal en clase"),
    GRAVE("Grave", "🟠", "Falta grave - Notificación escrita a padres de familia"),
    GRAVISIMA("Gravísima", "🔴", "Falta gravísima - Intervención del coordinador requerida");
    
    // ============================ ATRIBUTOS ============================
    
    private final String displayName;   // Nombre para mostrar en español
    private final String icon;          // Emoji o icono visual (color)
    private final String description;   // Descripción detallada de la severidad
    
    // ============================ CONSTRUCTOR ============================
    
    /**
     * Constructor for Severity enum
     * @param displayName Human-readable name in Spanish
     * @param icon Visual emoji representation (color indicator)
     * @param description Detailed description of the severity level
     */
    Severity(String displayName, String icon, String description) {
        this.displayName = displayName;
        this.icon = icon;
        this.description = description;
    }
    
    // ============================ GETTERS ============================
    
    /**
     * Gets the human-readable name in Spanish
     * @return Display name (e.g., "Leve", "Grave", "Gravísima")
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Gets the visual emoji icon (color indicator)
     * @return Icon emoji (🟡 for LEVE, 🟠 for GRAVE, 🔴 for GRAVISIMA)
     */
    public String getIcon() {
        return icon;
    }
    
    /**
     * Gets the detailed description of the severity level
     * @return Description string
     */
    public String getDescription() {
        return description;
    }
    
    // ============================ MÉTODOS DE UTILIDAD ============================
    
    /**
     * Gets the severity level as a numeric value for sorting
     * @return 1=LEVE, 2=GRAVE, 3=GRAVISIMA
     */
    public int getLevel() {
        switch (this) {
            case LEVE: return 1;
            case GRAVE: return 2;
            case GRAVISIMA: return 3;
            default: return 0;
        }
    }
    
    /**
     * Gets the Severity from a string value (case-insensitive)
     * @param value String value (LEVE, GRAVE, GRAVISIMA)
     * @return Severity enum or null if not found
     */
    public static Severity fromString(String value) {
        if (value == null) return null;
        
        for (Severity severity : Severity.values()) {
            if (severity.name().equalsIgnoreCase(value)) {
                return severity;
            }
        }
        return null;
    }
    
    /**
     * Gets the Severity from a display name (case-insensitive)
     * @param displayName Display name (e.g., "Leve", "Grave", "Gravísima")
     * @return Severity enum or null if not found
     */
    public static Severity fromDisplayName(String displayName) {
        if (displayName == null) return null;
        
        for (Severity severity : Severity.values()) {
            if (severity.getDisplayName().equalsIgnoreCase(displayName)) {
                return severity;
            }
        }
        return null;
    }
    
    /**
     * Checks if this severity is serious (GRAVE or GRAVISIMA)
     * @return true if GRAVE or GRAVISIMA, false if LEVE
     */
    public boolean isSerious() {
        return this == GRAVE || this == GRAVISIMA;
    }
    
    /**
     * Checks if this severity requires parent notification
     * @return true if GRAVE or GRAVISIMA
     */
    public boolean requiresParentNotification() {
        return this == GRAVE || this == GRAVISIMA;
    }
    
    /**
     * Checks if this severity requires coordinator intervention
     * @return true only for GRAVISIMA
     */
    public boolean requiresCoordinatorIntervention() {
        return this == GRAVISIMA;
    }
    
    /**
     * Gets the recommended action for this severity level
     * @return Recommended action string
     */
    public String getRecommendedAction() {
        switch (this) {
            case LEVE:
                return "Amonestación verbal y reflexión en clase";
            case GRAVE:
                return "Notificación escrita a padres y compromiso de comportamiento";
            case GRAVISIMA:
                return "Reunión con coordinador, conferencia con padres y posible suspensión";
            default:
                return "Sin acción definida";
        }
    }
    
    /**
     * Gets the English name of the severity
     * @return English name (LEVE, GRAVE, GRAVISIMA)
     */
    public String getEnglishName() {
        return this.name();
    }
    
    /**
     * Gets the color code for terminal output (ANSI)
     * @return ANSI color code or empty string
     */
    public String getAnsiColor() {
        switch (this) {
            case LEVE: return "\u001B[33m";      // Yellow
            case GRAVE: return "\u001B[38;5;208m"; // Orange
            case GRAVISIMA: return "\u001B[31m";   // Red
            default: return "";
        }
    }
    
    /**
     * Resets ANSI color
     * @return ANSI reset code
     */
    public static String getAnsiReset() {
        return "\u001B[0m";
    }
    
    // ============================ MÉTODOS SOBRESCRITOS ============================
    
    @Override
    public String toString() {
        return icon + " " + displayName + " (" + this.name() + ")";
    }
    
}
