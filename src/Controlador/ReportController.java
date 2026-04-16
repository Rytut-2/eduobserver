package Controlador;

import Modelo.*;
import java.util.List;

/**
 * Controller for report generation (RF-11)
 * Handles general reports, student reports, and grade reports
 */
public class ReportController {
    private AuthController authController;
    private UserController userController;
    private GradeController gradeController;
    
    public ReportController(AuthController authController, UserController userController, GradeController gradeController) {
        this.authController = authController;
        this.userController = userController;
        this.gradeController = gradeController;
    }
    
    /**
     * Generates a general coexistence report (RF-11 - Coordinator only)
     */
    public void generateGeneralReport() {
        if (!authController.hasRole("COORDINATOR")) {
            System.out.println("❌ Permission denied: Only Coordinator can generate general reports");
            return;
        }
        
        User currentUser = authController.getCurrentUser();
        if (!(currentUser instanceof Coordinator)) {
            System.out.println("❌ Invalid user type");
            return;
        }
        
        Coordinator coordinator = (Coordinator) currentUser;
        List<Student> allStudents = userController.getAllStudents();
        
        Report report = coordinator.generateGeneralReport(allStudents);
        report.print();
    }
    
    /**
     * Generates a report for a specific student
     * @param studentId Student ID
     */
    public void generateStudentReport(String studentId) {
        User currentUser = authController.getCurrentUser();
        
        // Check permissions
        if (!authController.hasRole("COORDINATOR") && !authController.hasRole("TEACHER") && !authController.hasRole("GROUP_TEACHER")) {
            if (currentUser instanceof Student && !currentUser.getId().equals(studentId)) {
                System.out.println("❌ Permission denied: Students can only view their own reports");
                return;
            }
        }
        
        Student student = (Student) userController.findUserById(studentId);
        if (student == null) {
            System.out.println("❌ Student not found");
            return;
        }
        
        Report report = Report.createStudentReport(student, currentUser.getName());
        report.print();
    }
    
    /**
     * Generates a report for a specific grade
     * @param gradeId Grade ID
     */
    public void generateGradeReport(int gradeId) {
        if (!authController.hasRole("COORDINATOR") && !authController.hasRole("GROUP_TEACHER")) {
            System.out.println("❌ Permission denied: Only Coordinator and Group Teacher can generate grade reports");
            return;
        }
        
        // If Group Teacher, verify they are assigned to this grade
        if (authController.hasRole("GROUP_TEACHER")) {
            User currentUser = authController.getCurrentUser();
            if (currentUser instanceof GroupTeacher) {
                GroupTeacher groupTeacher = (GroupTeacher) currentUser;
                if (groupTeacher.getAssignedGrade() == null || groupTeacher.getAssignedGrade().getId() != gradeId) {
                    System.out.println("❌ You are not the group teacher for this grade");
                    return;
                }
            }
        }
        
        Grade grade = gradeController.findGradeById(gradeId);
        if (grade == null) {
            System.out.println("❌ Grade not found");
            return;
        }
        
        Report report = Report.createGradeReport(grade, authController.getCurrentUser().getName());
        report.print();
    }
}