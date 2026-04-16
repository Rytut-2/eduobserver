package Modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Observation {
    
    // ============================ ATRIBUTOS ============================
    
    protected String id;                    // Observation ID (e.g., "OBS-001")
    protected LocalDateTime date;           // Date and time of observation
    protected String description;           // Description of the observation (RnF-2)
    protected String status;                // "active" or "cancelled"
    protected LocalDateTime cancellationDate;    // When it was cancelled (if applicable)
    protected String cancellationJustification;  // Why it was cancelled (RF-10)
    protected Student student;              // Associated student (cannot be null - RnF-1)
    protected Teacher createdByTeacher;     // Teacher who created it (cannot be null - RnF-1)
    
    protected static int nextId = 1;        // Counter for generating IDs
    
    // ============================ CONSTRUCTOR ============================
    
    /**
     * Constructor for a new observation
     * @param createdByTeacher Teacher who creates the observation (RF-7)
     * @param student Student associated with the observation (RF-7)
     * @param description Description of the observation (RnF-2)
     * @throws IllegalArgumentException if any required field is null or empty
     */
    public Observation(Teacher createdByTeacher, Student student, String description) {
        // RnF-1: Observation must have a teacher
        if (createdByTeacher == null) {
            throw new IllegalArgumentException("Observation must have a teacher creator (RnF-1)");
        }
        
        // RnF-1: Observation must be associated with a student
        if (student == null) {
            throw new IllegalArgumentException("Observation must be associated with a student (RnF-1)");
        }
        
        // RnF-2: Description is required
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description is required for observation (RnF-2)");
        }
        
        this.id = generateId();
        this.createdByTeacher = createdByTeacher;
        this.student = student;
        this.description = description;
        this.date = LocalDateTime.now();
        this.status = "active";
        this.cancellationDate = null;
        this.cancellationJustification = null;
    }
    
    // ============================ MÉTODOS PRINCIPALES ============================
    
    /**
     * Generates a unique ID for the observation
     * @return String ID like "OBS-001"
     */
    private static String generateId() {
        String idString = String.format("%03d", nextId++);
        return "OBS-" + idString;
    }
    
    /**
     * Resets the ID counter (useful for testing)
     */
    protected static void resetIdCounter() {
        nextId = 1;
    }
    
    /**
     * RF-10, RnF-5: Cancels the observation
     * Observations cannot be deleted, only cancelled with justification
     * @param coordinator Coordinator who cancels the observation
     * @param justification Reason for cancellation (required)
     * @throws IllegalStateException if observation is already cancelled
     * @throws IllegalArgumentException if justification is null or empty
     */
    public void cancel(Coordinator coordinator, String justification) {
        if (!"active".equals(this.status)) {
            throw new IllegalStateException("Observation is already cancelled or not active");
        }
        
        if (coordinator == null) {
            throw new IllegalArgumentException("Only a Coordinator can cancel observations");
        }
        
        if (justification == null || justification.trim().isEmpty()) {
            throw new IllegalArgumentException("Justification is required to cancel an observation");
        }
        
        this.status = "cancelled";
        this.cancellationDate = LocalDateTime.now();
        this.cancellationJustification = justification;
        
        System.out.println("❌ Observation " + id + " cancelled by Coordinator: " + coordinator.getName());
        System.out.println("   Justification: " + justification);
    }
    
    /**
     * Checks if the observation is active
     * @return true if active, false if cancelled
     */
    public boolean isActive() {
        return "active".equals(this.status);
    }
    
    /**
     * Checks if the observation is cancelled
     * @return true if cancelled, false if active
     */
    public boolean isCancelled() {
        return "cancelled".equals(this.status);
    }
    
    /**
     * Gets formatted date string
     * @return Date formatted as "dd/MM/yyyy HH:mm:ss"
     */
    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return date.format(formatter);
    }
    
    /**
     * Gets formatted cancellation date string (if cancelled)
     * @return Cancellation date or "N/A" if not cancelled
     */
    public String getFormattedCancellationDate() {
        if (cancellationDate == null) {
            return "N/A";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return cancellationDate.format(formatter);
    }
    
    // ============================ MÉTODOS ABSTRACTOS ============================
    
    /**
     * Gets the type of observation
     * @return Type as string (DISCIPLINARY or ACADEMIC)
     */
    public abstract String getObservationType();
    
    /**
     * Gets additional details specific to the observation type
     * @return Details as string
     */
    public abstract String getDetails();
    
    // ============================ GETTERS ============================
    
    public String getId() {
        return id;
    }
    
    public LocalDateTime getDate() {
        return date;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getStatus() {
        return status;
    }
    
    public LocalDateTime getCancellationDate() {
        return cancellationDate;
    }
    
    public String getCancellationJustification() {
        return cancellationJustification;
    }
    
    public Student getStudent() {
        return student;
    }
    
    public Teacher getCreatedByTeacher() {
        return createdByTeacher;
    }
    
    // ============================ MÉTODOS UTILITARIOS ============================
    
    /**
     * Gets a short summary of the observation
     * @return Short summary string
     */
    public String getSummary() {
        return String.format("[%s] %s - %s", getObservationType(), id, 
            description.length() > 50 ? description.substring(0, 47) + "..." : description);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(getObservationType()).append("] ");
        sb.append(id).append(" - ");
        sb.append(getFormattedDate()).append("\n");
        sb.append("   Student: ").append(student.getName()).append(" (ID: ").append(student.getId()).append(")\n");
        sb.append("   Teacher: ").append(createdByTeacher.getName()).append("\n");
        sb.append("   Description: ").append(description).append("\n");
        
        if (!isActive()) {
            sb.append("   ⚠️ CANCELLED\n");
            sb.append("   Cancellation Date: ").append(getFormattedCancellationDate()).append("\n");
            sb.append("   Justification: ").append(cancellationJustification).append("\n");
        }
        
        return sb.toString();
    }
    
}
