package Modelo;

public class AcademicObservation extends Observation{
    
    private AcademicType academicType;
    private String detail;
    private String subject;
    private double grade;
    private String recommendation;
    
    // Constructor principal
    public AcademicObservation(Teacher createdByTeacher, Student student,
                               String description, AcademicType academicType, String detail) {
        super(createdByTeacher, student, description);
        
        if (academicType == null) {
            throw new IllegalArgumentException("Academic type is required (RF-5)");
        }
        if (detail == null || detail.trim().isEmpty()) {
            throw new IllegalArgumentException("Detail is required (RF-5)");
        }
        
        this.academicType = academicType;
        this.detail = detail;
        this.subject = "";
        this.grade = -1;
        this.recommendation = "";
    }
    
    // Constructor con materia
    public AcademicObservation(Teacher createdByTeacher, Student student,
                               String description, AcademicType academicType, 
                               String detail, String subject) {
        this(createdByTeacher, student, description, academicType, detail);
        this.subject = subject;
    }
    
    // Constructor con materia y nota
    public AcademicObservation(Teacher createdByTeacher, Student student,
                               String description, AcademicType academicType,
                               String detail, String subject, double grade) {
        this(createdByTeacher, student, description, academicType, detail, subject);
        this.grade = grade;
    }
    
    // Getters
    public AcademicType getAcademicType() { return academicType; }
    public String getDetail() { return detail; }
    public String getSubject() { return subject; }
    public double getGrade() { return grade; }
    public String getRecommendation() { 
        return recommendation.isEmpty() ? getDefaultRecommendation() : recommendation;
    }
    
    // Setters
    public void setAcademicType(AcademicType academicType) { this.academicType = academicType; }
    public void setDetail(String detail) { this.detail = detail; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setGrade(double grade) { this.grade = grade; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    
    @Override
    public String getObservationType() {
        return "ACADEMIC";
    }
    
    @Override
    public String getDetails() {
        return detail;  // Solo el detalle, no el tipo
    }
    
    public String getAcademicTypeDescription() {
        return academicType.getDisplayName();
    }
    
    public String getIcon() {
        return academicType.getIcon();
    }
    
    public String getDefaultRecommendation() {
        switch (academicType) {
            case TALENT:
                return "Provide enrichment activities and advanced challenges";
            case DIFFICULTY:
                return "Schedule additional support sessions and monitor progress";
            case ACHIEVEMENT:
                return "Recognize achievement and encourage continued excellence";
            default:
                return "Monitor progress regularly";
        }
    }
    
    public String getTypeIndicator() {
        switch (academicType) {
            case TALENT: return "🎯";
            case DIFFICULTY: return "📝";
            case ACHIEVEMENT: return "🏆";
            default: return "📚";
        }
    }
    
    public boolean isPositive() {
        return academicType == AcademicType.TALENT || academicType == AcademicType.ACHIEVEMENT;
    }
    
    public boolean needsIntervention() {
        return academicType == AcademicType.DIFFICULTY;
    }
    
    public String getFormattedGrade() {
        if (grade >= 0 && grade <= 10) {
            return String.format("%.1f", grade);
        }
        return "N/A";
    }
    
    public void addFollowUpAction(String action) {
        if (recommendation.isEmpty()) {
            recommendation = action;
        } else {
            recommendation = recommendation + " | " + action;
        }
    }
    
    public String toReportString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getTypeIndicator()).append(" ACADEMIC OBSERVATION\n");
        sb.append("   ID: ").append(id).append("\n");
        sb.append("   Date: ").append(getFormattedDate()).append("\n");
        sb.append("   Student: ").append(student.getName()).append("\n");
        sb.append("   Teacher: ").append(createdByTeacher.getName()).append("\n");
        sb.append("   Type: ").append(academicType.getDisplayName()).append("\n");
        sb.append("   Description: ").append(description).append("\n");
        sb.append("   Detail: ").append(detail).append("\n");
        if (subject != null && !subject.isEmpty()) {
            sb.append("   Subject: ").append(subject).append("\n");
        }
        if (grade >= 0 && grade <= 10) {
            sb.append("   Grade: ").append(String.format("%.1f", grade)).append("\n");
        }
        sb.append("   Recommendation: ").append(getRecommendation()).append("\n");
        if (!isActive()) {
            sb.append("   Status: CANCELLED\n");
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getIcon()).append(" [ACADEMIC] ");
        sb.append(id).append(" - ");
        sb.append(getFormattedDate()).append("\n");
        sb.append("   Student: ").append(student.getName()).append("\n");
        sb.append("   Teacher: ").append(createdByTeacher.getName()).append("\n");
        sb.append("   Type: ").append(academicType.getDisplayName()).append("\n");
        sb.append("   Description: ").append(description).append("\n");
        sb.append("   Detail: ").append(detail).append("\n");
        if (subject != null && !subject.isEmpty()) {
            sb.append("   Subject: ").append(subject).append("\n");
        }
        if (grade >= 0 && grade <= 10) {
            sb.append("   Grade: ").append(String.format("%.1f", grade)).append("/10\n");
        }
        if (!isActive()) {
            sb.append("   ⚠️ CANCELLED\n");
        }
        sb.append("   Recommendation: ").append(getRecommendation()).append("\n");
        return sb.toString();
    }
}
