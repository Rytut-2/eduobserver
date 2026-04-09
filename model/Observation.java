package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Observation {
    private String id;
    private String teacherId;
    private String studentId;
    private ObservationType type;
    private Severity severity;
    private String description;
    private ObservationStatus status;
    private String justification;
    private LocalDateTime createdAt;
    private LocalDateTime voidedAt;
    private String voidedBy;

    public Observation(String id, String teacherId, String studentId,
            ObservationType type, Severity severity, String description) {
        this.id = id;
        this.teacherId = teacherId;
        this.studentId = studentId;
        this.type = type;
        this.severity = severity;
        this.description = description;
        this.status = ObservationStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    public void voidObservation(String justification, String voidedBy) {
        this.status = ObservationStatus.VOIDED;
        this.justification = justification;
        this.voidedBy = voidedBy;
        this.voidedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getTeacherId() { return teacherId; }
    public String getStudentId() { return studentId; }
    public ObservationType getType() { return type; }
    public Severity getSeverity() { return severity; }
    public String getDescription() { return description; }
    public ObservationStatus getStatus() { return status; }
    public String getJustification() { return justification; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getVoidedAt() { return voidedAt; }
    public String getVoidedBy() { return voidedBy; }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(id).append("\n");
        sb.append("Type: ").append(type.getDisplayName()).append("\n");
        sb.append("Severity: ").append(severity.getDisplayName()).append("\n");
        sb.append("Description: ").append(description).append("\n");
        sb.append("Status: ").append(status.getDisplayName()).append("\n");
        sb.append("Created: ").append(createdAt.format(formatter)).append("\n");
        if (status == ObservationStatus.VOIDED) {
            sb.append("Voided by: ").append(voidedBy).append("\n");
            sb.append("Justification: ").append(justification).append("\n");
        }
        return sb.toString();
    }
}
