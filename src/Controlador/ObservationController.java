package Controlador;

import Modelo.*;
import java.util.List;

/**
 * Controller for observation management (RF-4, RF-5, RF-7, RF-8, RF-10)
 * Handles creating, cancelling, and viewing observations
 */
public class ObservationController {
    private AuthController authController;
    private UserController userController;
    
    public ObservationController(AuthController authController, UserController userController) {
        this.authController = authController;
        this.userController = userController;
    }
    
    /**
     * Creates a disciplinary observation (RF-4 - Teacher, GroupTeacher, or Coordinator only)
     * @param studentId Student ID
     * @param description Observation description
     * @param severity Severity level
     * @return true if observation created successfully
     */
    public boolean createDisciplinaryObservation(String studentId, String description, Severity severity) {
        // Check permission (RF-2: Only Teachers and Coordinator can register observations)
        if (!authController.hasRole("TEACHER") && !authController.hasRole("GROUP_TEACHER") && !authController.hasRole("COORDINATOR")) {
            System.out.println("❌ Permission denied: Only Teachers and Coordinator can create observations");
            return false;
        }
        
        User currentUser = authController.getCurrentUser();
        if (!(currentUser instanceof Teacher)) {
            System.out.println("❌ Only teachers can create disciplinary observations");
            return false;
        }
        
        Student student = (Student) userController.findUserById(studentId);
        if (student == null) {
            System.out.println("❌ Student not found");
            return false;
        }
        
        Teacher teacher = (Teacher) currentUser;
        
        // Validation
        if (description == null || description.trim().isEmpty()) {
            System.out.println("❌ Description is required");
            return false;
        }
        
        if (severity == null) {
            System.out.println("❌ Severity is required");
            return false;
        }
        
        DisciplinaryObservation observation = new DisciplinaryObservation(teacher, student, description, severity);
        student.addObservation(observation);
        
        System.out.println("✅ Disciplinary observation created for student: " + student.getName());
        System.out.println("   Severity: " + severity.getDisplayName());
        return true;
    }
    
    /**
     * Creates an academic observation (RF-5 - Teacher, GroupTeacher, or Coordinator only)
     * @param studentId Student ID
     * @param description Observation description
     * @param academicType Type of academic observation
     * @param detail Specific detail
     * @return true if observation created successfully
     */
    public boolean createAcademicObservation(String studentId, String description, AcademicType academicType, String detail) {
        // Check permission (RF-2: Only Teachers and Coordinator can register observations)
        if (!authController.hasRole("TEACHER") && !authController.hasRole("GROUP_TEACHER") && !authController.hasRole("COORDINATOR")) {
            System.out.println("❌ Permission denied: Only Teachers and Coordinator can create observations");
            return false;
        }
        
        User currentUser = authController.getCurrentUser();
        if (!(currentUser instanceof Teacher)) {
            System.out.println("❌ Only teachers can create academic observations");
            return false;
        }
        
        Student student = (Student) userController.findUserById(studentId);
        if (student == null) {
            System.out.println("❌ Student not found");
            return false;
        }
        
        Teacher teacher = (Teacher) currentUser;
        
        // Validation
        if (description == null || description.trim().isEmpty()) {
            System.out.println("❌ Description is required");
            return false;
        }
        
        if (academicType == null) {
            System.out.println("❌ Academic type is required");
            return false;
        }
        
        if (detail == null || detail.trim().isEmpty()) {
            System.out.println("❌ Detail is required");
            return false;
        }
        
        AcademicObservation observation = new AcademicObservation(teacher, student, description, academicType, detail);
        student.addObservation(observation);
        
        System.out.println("✅ Academic observation created for student: " + student.getName());
        System.out.println("   Type: " + academicType.getDisplayName());
        return true;
    }
    
    /**
     * Cancels an observation (RF-10 - Coordinator only)
     * @param studentId Student ID
     * @param observationIndex Index of observation in student's list
     * @param justification Justification for cancellation
     * @return true if observation cancelled successfully
     */
    public boolean cancelObservation(String studentId, int observationIndex, String justification) {
        if (!authController.hasRole("COORDINATOR")) {
            System.out.println("❌ Permission denied: Only Coordinator can cancel observations");
            return false;
        }
        
        Student student = (Student) userController.findUserById(studentId);
        if (student == null) {
            System.out.println("❌ Student not found");
            return false;
        }
        
        List<Observation> observations = student.getObservations();
        if (observationIndex < 0 || observationIndex >= observations.size()) {
            System.out.println("❌ Observation not found");
            return false;
        }
        
        if (justification == null || justification.trim().isEmpty()) {
            System.out.println("❌ Justification is required to cancel an observation");
            return false;
        }
        
        Coordinator coordinator = (Coordinator) authController.getCurrentUser();
        Observation observation = observations.get(observationIndex);
        
        try {
            observation.cancel(coordinator, justification);
            System.out.println("✅ Observation cancelled successfully");
            return true;
        } catch (IllegalStateException e) {
            System.out.println("❌ " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets student's record (RF-8)
     * @param studentId Student ID
     * @return List of observations or null if not authorized
     */
    public List<Observation> getStudentRecord(String studentId) {
        User currentUser = authController.getCurrentUser();
        Student student = (Student) userController.findUserById(studentId);
        
        if (student == null) {
            System.out.println("❌ Student not found");
            return null;
        }
        
        // RF-3: Students can only access their own data
        if (currentUser instanceof Student && !currentUser.getId().equals(studentId)) {
            System.out.println("❌ Permission denied: Students can only view their own records");
            return null;
        }
        
        // Teachers, Coordinators, and GroupTeachers can view any student's record
        List<Observation> observations = student.viewRecord();
        
        if (observations.isEmpty()) {
            System.out.println("📋 No observations found for student: " + student.getName());
        } else {
            System.out.println("📋 Showing record for student: " + student.getName());
            System.out.println("   Total observations: " + observations.size());
        }
        
        return observations;
    }
    
    /**
     * Prints student record to console
     * @param studentId Student ID
     */
    public void printStudentRecord(String studentId) {
        Student student = (Student) userController.findUserById(studentId);
        if (student == null) {
            System.out.println("❌ Student not found");
            return;
        }
        
        student.printRecord();
    }
    
    /**
     * Gets all observations for a student (without printing)
     * @param studentId Student ID
     * @return List of observations
     */
    public List<Observation> getObservationsByStudent(String studentId) {
        Student student = (Student) userController.findUserById(studentId);
        if (student == null) {
            return null;
        }
        return student.getObservations();
    }
}