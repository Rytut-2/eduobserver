package Modelo;

public enum AcademicType {
    
    TALENT("Talento Específico", "🎯", "El estudiante demuestra una habilidad o talento especial en un área académica"),
    DIFFICULTY("Dificultad de Aprendizaje", "📝", "El estudiante presenta dificultades en su proceso de aprendizaje"),
    ACHIEVEMENT("Logro Académico", "🏆", "El estudiante ha alcanzado un logro académico importante");
    
    // ============================ ATRIBUTOS ============================
    
    private final String displayName;   // Nombre para mostrar en español
    private final String icon;          // Emoji o icono visual
    private final String description;   // Descripción detallada del tipo
    
    // ============================ CONSTRUCTOR ============================
    
    /**
     * Constructor for AcademicType enum
     * @param displayName Human-readable name in Spanish
     * @param icon Visual emoji representation
     * @param description Detailed description of the type
     */
    AcademicType(String displayName, String icon, String description) {
        this.displayName = displayName;
        this.icon = icon;
        this.description = description;
    }
    
    // ============================ GETTERS ============================
    
    /**
     * Gets the human-readable name in Spanish
     * @return Display name (e.g., "Talento Específico")
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Gets the visual emoji icon
     * @return Icon emoji (e.g., "🎯", "📝", "🏆")
     */
    public String getIcon() {
        return icon;
    }
    
    /**
     * Gets the detailed description of the academic type
     * @return Description string
     */
    public String getDescription() {
        return description;
    }
    
    // ============================ MÉTODOS DE UTILIDAD ============================
    
    /**
     * Gets the AcademicType from a string value (case-insensitive)
     * @param value String value (TALENT, DIFFICULTY, ACHIEVEMENT)
     * @return AcademicType enum or null if not found
     */
    public static AcademicType fromString(String value) {
        if (value == null) return null;
        
        for (AcademicType type : AcademicType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
    
    /**
     * Gets the AcademicType from a display name (case-insensitive)
     * @param displayName Display name (e.g., "Talento Específico")
     * @return AcademicType enum or null if not found
     */
    public static AcademicType fromDisplayName(String displayName) {
        if (displayName == null) return null;
        
        for (AcademicType type : AcademicType.values()) {
            if (type.getDisplayName().equalsIgnoreCase(displayName)) {
                return type;
            }
        }
        return null;
    }
    
    /**
     * Checks if this type represents a positive observation (TALENT or ACHIEVEMENT)
     * @return true if positive, false if difficulty
     */
    public boolean isPositive() {
        return this == TALENT || this == ACHIEVEMENT;
    }
    
    /**
     * Checks if this type represents a difficulty (needs intervention)
     * @return true if difficulty
     */
    public boolean needsIntervention() {
        return this == DIFFICULTY;
    }
    
    /**
     * Gets the default recommendation for this academic type
     * @return Recommendation string
     */
    public String getDefaultRecommendation() {
        switch (this) {
            case TALENT:
                return "Proporcionar actividades de enriquecimiento y desafíos avanzados para desarrollar este talento";
            case DIFFICULTY:
                return "Programar sesiones de apoyo adicional, crear plan de aprendizaje personalizado y monitorear progreso";
            case ACHIEVEMENT:
                return "Reconocer el logro públicamente, fomentar la excelencia continua y considerar para premios académicos";
            default:
                return "Monitorear el progreso regularmente";
        }
    }
    
    /**
     * Gets the English name of the type
     * @return English name (TALENT, DIFFICULTY, ACHIEVEMENT)
     */
    public String getEnglishName() {
        return this.name();
    }
    
    // ============================ MÉTODOS SOBRESCRITOS ============================
    
    @Override
    public String toString() {
        return icon + " " + displayName + " (" + this.name() + ")";
    }
}
