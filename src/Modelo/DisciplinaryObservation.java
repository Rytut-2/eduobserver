package Modelo;



public class DisciplinaryObservation extends Observation{
    
    // ============================ ATRIBUTOS ============================
    
    private Severity severity;           // Severity level of the infraction
    private String followUpAction;       // Follow-up action taken (optional)
    private boolean parentNotified;      // Whether parents were notified
    
    // ============================ CONSTRUCTORES ============================
    
    /**
     * Constructor for disciplinary observation
     * @param createdByTeacher Teacher who creates the observation
     * @param student Student associated with the observation
     * @param description Description of the infraction
     * @param severity Severity level (LEVE, GRAVE, GRAVISIMA)
     * @throws IllegalArgumentException if severity is null
     */
    public DisciplinaryObservation(Teacher createdByTeacher, Student student,
                                   String description, Severity severity) {
        super(createdByTeacher, student, description);
        
        // RF-4: Severity is required
        if (severity == null) {
            throw new IllegalArgumentException("Severity level is required for disciplinary observation (RF-4)");
        }
        
        this.severity = severity;
        this.followUpAction = "";
        this.parentNotified = false;
    }
    
    /**
     * Constructor for disciplinary observation with follow-up action
     * @param createdByTeacher Teacher who creates the observation
     * @param student Student associated with the observation
     * @param description Description of the infraction
     * @param severity Severity level
     * @param followUpAction Follow-up action taken
     */
    public DisciplinaryObservation(Teacher createdByTeacher, Student student,
                                   String description, Severity severity, String followUpAction) {
        this(createdByTeacher, student, description, severity);
        this.followUpAction = followUpAction;
    }
    
    // ============================ GETTERS Y SETTERS ============================
    
    public Severity getSeverity() {
        return severity;
    }
    
    public void setSeverity(Severity severity) {
        this.severity = severity;
    }
    
    public String getFollowUpAction() {
        return followUpAction;
    }
    
    public void setFollowUpAction(String followUpAction) {
        this.followUpAction = followUpAction;
    }
    
    public boolean isParentNotified() {
        return parentNotified;
    }
    
    public void setParentNotified(boolean parentNotified) {
        this.parentNotified = parentNotified;
    }
    
    // ============================ MÉTODOS ABSTRACTOS IMPLEMENTADOS ============================
    
    @Override
    public String getObservationType() {
        return "DISCIPLINARY";
    }
    
    @Override
    public String getDetails() {
        return String.format("Severity: %s | Follow-up: %s | Parents notified: %s",
                severity.getDisplayName(),
                followUpAction.isEmpty() ? "None" : followUpAction,
                parentNotified ? "Yes" : "No");
    }
    
    // ============================ MÉTODOS DE UTILIDAD ============================
    
    /**
     * Gets the severity level as a numeric value for sorting
     * @return 1=LEVE, 2=GRAVE, 3=GRAVISIMA
     */
    public int getSeverityLevel() {
        switch (severity) {
            case LEVE: return 1;
            case GRAVE: return 2;
            case GRAVISIMA: return 3;
            default: return 0;
        }
    }
    
    /**
     * Gets the recommended action based on severity
     * @return Recommended action string
     */
    public String getRecommendedAction() {
        switch (severity) {
            case LEVE:
                return "Verbal warning and classroom reflection";
            case GRAVE:
                return "Written notification to parents and behavior contract";
            case GRAVISIMA:
                return "Coordinator meeting, parent conference, and possible suspension";
            default:
                return "No action defined";
        }
    }
    
    /**
     * Gets the color code for the severity (for UI display)
     * @return ANSI color code or emoji
     */
    public String getSeverityColor() {
        switch (severity) {
            case LEVE: return "🟡";      // Yellow
            case GRAVE: return "🟠";     // Orange
            case GRAVISIMA: return "🔴"; // Red
            default: return "⚪";
        }
    }
    
    /**
     * Marks parents as notified and records the follow-up action
     * @param notificationMethod Method of notification (email, call, meeting)
     */
    public void notifyParents(String notificationMethod) {
        this.parentNotified = true;
        this.followUpAction = "Parents notified via " + notificationMethod;
        System.out.println("📧 Parents notified for student: " + student.getName());
        System.out.println("   Method: " + notificationMethod);
    }
    
    /**
     * Adds a follow-up action to the observation
     * @param action Description of the follow-up action
     */
    public void addFollowUpAction(String action) {
        if (this.followUpAction.isEmpty()) {
            this.followUpAction = action;
        } else {
            this.followUpAction = this.followUpAction + " | " + action;
        }
        System.out.println("📝 Follow-up action added: " + action);
    }
    
    /**
     * Checks if this is a grave or very serious infraction
     * @return true if severity is GRAVE or GRAVISIMA
     */
    public boolean isSerious() {
        return severity == Severity.GRAVE || severity == Severity.GRAVISIMA;
    }
    
    /**
     * Gets a formatted string for reports
     * @return Formatted report string
     */
    public String toReportString() {
        StringBuilder sb = new StringBuilder();
        sb.append("⚠️ DISCIPLINARY OBSERVATION\n");
        sb.append("   ID: ").append(id).append("\n");
        sb.append("   Date: ").append(getFormattedDate()).append("\n");
        sb.append("   Student: ").append(student.getName()).append("\n");
        sb.append("   Teacher: ").append(createdByTeacher.getName()).append("\n");
        sb.append("   Severity: ").append(severity.getDisplayName()).append("\n");
        sb.append("   Description: ").append(description).append("\n");
        if (!followUpAction.isEmpty()) {
            sb.append("   Follow-up: ").append(followUpAction).append("\n");
        }
        if (parentNotified) {
            sb.append("   Parents notified: Yes\n");
        }
        if (!isActive()) {
            sb.append("   Status: CANCELLED\n");
            sb.append("   Cancellation justification: ").append(cancellationJustification).append("\n");
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getSeverityColor()).append(" [").append(getObservationType()).append("] ");
        sb.append(id).append(" - ");
        sb.append(getFormattedDate()).append("\n");
        sb.append("   Student: ").append(student.getName()).append(" (ID: ").append(student.getId()).append(")\n");
        sb.append("   Teacher: ").append(createdByTeacher.getName()).append("\n");
        sb.append("   Severity: ").append(severity).append("\n");
        sb.append("   Description: ").append(description).append("\n");
        
        if (!followUpAction.isEmpty()) {
            sb.append("   Follow-up: ").append(followUpAction).append("\n");
        }
        
        if (parentNotified) {
            sb.append("   📧 Parents notified\n");
        }
        
        if (!isActive()) {
            sb.append("   ⚠️ CANCELLED\n");
            sb.append("   Cancellation Date: ").append(getFormattedCancellationDate()).append("\n");
            sb.append("   Justification: ").append(cancellationJustification).append("\n");
        }
        
        sb.append("   Recommended Action: ").append(getRecommendedAction()).append("\n");
        
        return sb.toString();
    }
    
}
