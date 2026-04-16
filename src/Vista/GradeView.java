package Vista;

import Controlador.GradeController;
import Controlador.AuthController;
import Modelo.*;
import java.util.List;
import java.util.Scanner;

public class GradeView {
    private Scanner scanner;
    private GradeController gradeController;
    private AuthController authController;
    
    public GradeView(Scanner scanner, GradeController gradeController, AuthController authController) {
        this.scanner = scanner;
        this.gradeController = gradeController;
        this.authController = authController;
    }
    
    public void showGradeManagementMenu() {
        if (!authController.hasRole("COORDINATOR")) {
            System.out.println("❌ Permission denied. Only Coordinator can manage grades.");
            return;
        }
        
        boolean back = false;
        while (!back) {
            System.out.println("\n" + line(40));
            System.out.println("🏫 GRADE MANAGEMENT");
            System.out.println(line(40));
            System.out.println("1. List all grades");
            System.out.println("2. Add new grade");
            System.out.println("3. Assign student to grade");
            System.out.println("4. Assign group teacher to grade");
            System.out.println("5. Assign representative to grade");
            System.out.println("0. Back to main menu");
            System.out.print("\nOption: ");
            
            int option = readInt();
            
            switch (option) {
                case 1:
                    showAllGrades();
                    break;
                case 2:
                    showAddGrade();
                    break;
                case 3:
                    showAssignStudentToGrade();
                    break;
                case 4:
                    showAssignGroupTeacher();
                    break;
                case 5:
                    showAssignRepresentative();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("❌ Invalid option");
            }
        }
    }
    
    public void showGradeFullHistory() {
        if (!authController.hasRole("GROUP_TEACHER")) {
            System.out.println("❌ Permission denied. Only Group Teacher can view grade history.");
            return;
        }
        
        System.out.println("\n" + line(40));
        System.out.println("📖 GRADE FULL HISTORY (RF-13)");
        System.out.println(line(40));
        
        showGradesList();
        System.out.print("\n📝 Grade ID: ");
        int gradeId = readInt();
        
        gradeController.printFullGradeHistory(gradeId);
    }
    
    public void showGradeStatistics() {
        if (!authController.hasRole("GROUP_TEACHER")) {
            System.out.println("❌ Permission denied. Only Group Teacher can view grade statistics.");
            return;
        }
        
        System.out.println("\n" + line(40));
        System.out.println("📊 GRADE STATISTICS");
        System.out.println(line(40));
        
        showGradesList();
        System.out.print("\n📝 Grade ID: ");
        int gradeId = readInt();
        
        gradeController.printGradeStatistics(gradeId);
    }
    
    private void showAllGrades() {
        List<Grade> grades = gradeController.getAllGrades();
        
        System.out.println("\n" + line(70));
        System.out.println("📋 ALL GRADES");
        System.out.println(line(70));
        
        for (Grade grade : grades) {
            System.out.println("\n🏫 " + grade.getName() + " (ID: " + grade.getId() + ")");
            System.out.println("   Teacher: " + (grade.getDocenteGrupo() != null ?
                grade.getDocenteGrupo().getName() : "Not assigned"));
            System.out.println("   Representative: " + (grade.getRepresentante() != null ?
                grade.getRepresentante().getName() : "Not assigned"));
            System.out.println("   Students: " + grade.getStudents().size());
        }
        System.out.println("\n" + line(70));
    }
    
    private void showAddGrade() {
        System.out.println("\n" + line(40));
        System.out.println("➕ ADD NEW GRADE");
        System.out.println(line(40));
        
        System.out.print("Grade name (e.g., 'Grade 10A'): ");
        String name = scanner.nextLine();
        
        if (gradeController.addGrade(name)) {
            System.out.println("\n✅ Grade created successfully!");
        }
    }
    
    private void showAssignStudentToGrade() {
        System.out.println("\n" + line(40));
        System.out.println("👨‍🎓 ASSIGN STUDENT TO GRADE");
        System.out.println(line(40));
        
        showGradesList();
        System.out.print("\n📝 Grade ID: ");
        int gradeId = readInt();
        
        showAvailableStudents();
        System.out.print("\n📝 Student ID: ");
        String studentId = scanner.nextLine();
        
        if (gradeController.assignStudentToGrade(studentId, gradeId)) {
            System.out.println("\n✅ Student assigned to grade successfully!");
        }
    }
    
    private void showAssignGroupTeacher() {
        System.out.println("\n" + line(40));
        System.out.println("👨‍🏫 ASSIGN GROUP TEACHER TO GRADE");
        System.out.println(line(40));
        
        showGradesList();
        System.out.print("\n📝 Grade ID: ");
        int gradeId = readInt();
        
        showTeachersList();
        System.out.print("\n📝 Teacher ID: ");
        String teacherId = scanner.nextLine();
        
        if (gradeController.assignGroupTeacherToGrade(teacherId, gradeId)) {
            System.out.println("\n✅ Group teacher assigned successfully!");
        }
    }
    
    private void showAssignRepresentative() {
        System.out.println("\n" + line(40));
        System.out.println("👥 ASSIGN REPRESENTATIVE TO GRADE");
        System.out.println(line(40));
        
        showGradesList();
        System.out.print("\n📝 Grade ID: ");
        int gradeId = readInt();
        
        showStudentsList();
        System.out.print("\n📝 Student ID (will become Representative): ");
        String studentId = scanner.nextLine();
        
        if (gradeController.assignRepresentativeToGrade(studentId, gradeId)) {
            System.out.println("\n✅ Representative assigned successfully!");
        }
    }
    
    private void showGradesList() {
        List<Grade> grades = gradeController.getAllGrades();
        
        System.out.println("\n📋 Available Grades:");
        System.out.println(line(40));
        for (Grade grade : grades) {
            System.out.println("  ID: " + grade.getId() + " - " + grade.getName());
        }
        System.out.println(line(40));
    }
    
    private void showAvailableStudents() {
        List<Student> students = gradeController.getAvailableStudents();
        
        System.out.println("\n📋 Available Students:");
        System.out.println(line(40));
        for (Student student : students) {
            System.out.println("  ID: " + student.getId() + " - " + student.getName());
        }
        System.out.println(line(40));
    }
    
    private void showTeachersList() {
        List<Teacher> teachers = gradeController.getAllTeachers();
        
        System.out.println("\n📋 Teachers List:");
        System.out.println(line(40));
        for (Teacher teacher : teachers) {
            System.out.println("  ID: " + teacher.getId() + " - " + teacher.getName() + " (" + teacher.getRole() + ")");
        }
        System.out.println(line(40));
    }
    
    private void showStudentsList() {
        List<Student> students = gradeController.getAllStudents();
        
        System.out.println("\n📋 Students List:");
        System.out.println(line(40));
        for (Student student : students) {
            System.out.println("  ID: " + student.getId() + " - " + student.getName());
        }
        System.out.println(line(40));
    }
    
    private int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private String line(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append("─");
        }
        return sb.toString();
    }
}