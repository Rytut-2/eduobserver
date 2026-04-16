package Modelo;

import java.util.Objects;

/**
* Teacher class (RF-2, RF-4, RF-5)
* Teachers can create disciplinary and academic observations
*/

public class Teacher extends User{

    // ============================ CONSTRUCTOR ============================
    
    /**
     * Constructor for a teacher
     * @param id Unique identifier (e.g., "DOC-001")
     * @param name Full name of the teacher
     * @param password Initial password
     */
    public Teacher(String id, String name, String password) {
        super(id, name, password);
    }
    
    // ============================ MÉTODOS PRINCIPALES ============================
    
    /**
     * Gets the role of the user
     * @return "TEACHER"
     */
    @Override
    public String getRole() {
        return "TEACHER";
    }
    
    /**
     * RF-4: Creates a disciplinary observation
     * @param student Student associated with the observation
     * @param description Description of the infraction
     * @param severity Severity level (LEVE, GRAVE, GRAVISIMA)
     * @return The created DisciplinaryObservation object
     * @throws IllegalArgumentException if student is null
     */
    public DisciplinaryObservation createDisciplinaryObservation(Student student, 
                                                                  String description, 
                                                                  Severity severity) {
        // RF-7 / RnF-1: Observation must be associated with a student
        if (student == null) {
            throw new IllegalArgumentException("Observation must be associated with a student (RnF-1)");
        }
        
        // RnF-2: Description is required
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description is required for observation (RnF-2)");
        }
        
        // RF-4: Severity is required
        if (severity == null) {
            throw new IllegalArgumentException("Severity level is required for disciplinary observation (RF-4)");
        }
        
        DisciplinaryObservation observation = new DisciplinaryObservation(this, student, description, severity);
        student.addObservation(observation);
        
        System.out.println("✅ Disciplinary observation created by teacher: " + this.getName());
        System.out.println("   Student: " + student.getName());
        System.out.println("   Severity: " + severity);
        
        return observation;
    }
    
    /**
     * RF-5: Creates an academic observation
     * @param student Student associated with the observation
     * @param description General description of the observation
     * @param academicType Type (TALENT, DIFFICULTY, ACHIEVEMENT)
     * @param detail Specific detail about the talent/difficulty/achievement
     * @return The created AcademicObservation object
     * @throws IllegalArgumentException if student is null
     */
    public AcademicObservation createAcademicObservation(Student student, 
                                                          String description, 
                                                          AcademicType academicType, 
                                                          String detail) {
        // RF-7 / RnF-1: Observation must be associated with a student
        if (student == null) {
            throw new IllegalArgumentException("Observation must be associated with a student (RnF-1)");
        }
        
        // RnF-2: Description is required
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description is required for observation (RnF-2)");
        }
        
        // RF-5: Academic type is required
        if (academicType == null) {
            throw new IllegalArgumentException("Academic type is required for academic observation (RF-5)");
        }
        
        // RF-5: Detail is required
        if (detail == null || detail.trim().isEmpty()) {
            throw new IllegalArgumentException("Detail is required for academic observation (RF-5)");
        }
        
        AcademicObservation observation = new AcademicObservation(this, student, description, academicType, detail);
        student.addObservation(observation);
        
        System.out.println("✅ Academic observation created by teacher: " + this.getName());
        System.out.println("   Student: " + student.getName());
        System.out.println("   Type: " + academicType.getDisplayName());
        System.out.println("   Detail: " + detail);
        
        return observation;
    }
    
    // ============================ MÉTODOS UTILITARIOS ============================
    
    /**
     * Checks if this teacher is a group teacher
     * @return true if this teacher is an instance of GroupTeacher
     */
    public boolean isGroupTeacher() {
        return this instanceof GroupTeacher;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        Teacher teacher = (Teacher) obj;
        return Objects.equals(id, teacher.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
    
    @Override
    public String toString() {
        return String.format("Teacher{id='%s', name='%s', active=%s}",
                id, name, active);
    }
    
}
