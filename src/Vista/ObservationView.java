package Vista;

import Controlador.ObservationController;
import Controlador.UserController;
import Controlador.AuthController;
import Modelo.*;
import java.util.List;
import java.util.Scanner;

public class ObservationView {
    private Scanner scanner;
    private ObservationController observationController;
    private UserController userController;
    private AuthController authController;
    
    public ObservationView(Scanner scanner, ObservationController observationController,
                           UserController userController, AuthController authController) {
        this.scanner = scanner;
        this.observationController = observationController;
        this.userController = userController;
        this.authController = authController;
    }
    
    public void showCreateObservationMenu() {
        System.out.println("\n" + line(40));
        System.out.println("📝 CREATE OBSERVATION");
        System.out.println(line(40));
        System.out.println("1. ⚠️ Disciplinary Observation");
        System.out.println("2. 📚 Academic Observation");
        System.out.println("0. Back");
        System.out.print("\nOption: ");
        
        int option = readInt();
        
        switch (option) {
            case 1:
                showCreateDisciplinaryObservation();
                break;
            case 2:
                showCreateAcademicObservation();
                break;
        }
    }
    
    public void showCreateDisciplinaryObservation() {
        System.out.println("\n" + line(40));
        System.out.println("⚠️ DISCIPLINARY OBSERVATION");
        System.out.println(line(40));
        
        showStudentList();
        
        System.out.print("\n📝 Student ID: ");
        String studentId = scanner.nextLine();
        
        System.out.print("📝 Description: ");
        String description = scanner.nextLine();
        
        System.out.println("\nSeverity levels:");
        System.out.println("1. LEVE (Minor)");
        System.out.println("2. GRAVE (Serious)");
        System.out.println("3. GRAVISIMA (Very Serious)");
        System.out.print("Select severity: ");
        
        int severityOption = readInt();
        Severity severity;
        switch (severityOption) {
            case 1: severity = Severity.LEVE; break;
            case 2: severity = Severity.GRAVE; break;
            case 3: severity = Severity.GRAVISIMA; break;
            default:
                System.out.println("❌ Invalid severity");
                return;
        }
        
        if (observationController.createDisciplinaryObservation(studentId, description, severity)) {
            System.out.println("\n✅ Disciplinary observation created successfully!");
        }
    }
    
    public void showCreateAcademicObservation() {
        System.out.println("\n" + line(40));
        System.out.println("📚 ACADEMIC OBSERVATION");
        System.out.println(line(40));
        
        showStudentList();
        
        System.out.print("\n📝 Student ID: ");
        String studentId = scanner.nextLine();
        
        System.out.print("📝 Description: ");
        String description = scanner.nextLine();
        
        System.out.println("\nAcademic types:");
        System.out.println("1. TALENT (Special ability)");
        System.out.println("2. DIFFICULTY (Learning challenge)");
        System.out.println("3. ACHIEVEMENT (Academic success)");
        System.out.print("Select type: ");
        
        int typeOption = readInt();
        AcademicType academicType;
        switch (typeOption) {
            case 1: academicType = AcademicType.TALENT; break;
            case 2: academicType = AcademicType.DIFFICULTY; break;
            case 3: academicType = AcademicType.ACHIEVEMENT; break;
            default:
                System.out.println("❌ Invalid type");
                return;
        }
        
        System.out.print("📝 Detail/Specific information: ");
        String detail = scanner.nextLine();
        
        if (observationController.createAcademicObservation(studentId, description, academicType, detail)) {
            System.out.println("\n✅ Academic observation created successfully!");
        }
    }
    
    public void showCancelObservationMenu() {
        if (!authController.hasRole("COORDINATOR")) {
            System.out.println("❌ Permission denied. Only Coordinator can cancel observations.");
            return;
        }
        
        System.out.println("\n" + line(40));
        System.out.println("❌ CANCEL OBSERVATION");
        System.out.println(line(40));
        
        showStudentList();
        
        System.out.print("\n📝 Student ID: ");
        String studentId = scanner.nextLine();
        
        List<Observation> observations = observationController.getStudentRecord(studentId);
        if (observations == null || observations.isEmpty()) {
            System.out.println("❌ No observations found for this student");
            return;
        }
        
        System.out.println("\n📋 Observations for this student:");
        for (int i = 0; i < observations.size(); i++) {
            Observation obs = observations.get(i);
            System.out.println((i + 1) + ". " + obs.getSummary());
            System.out.println("   Status: " + obs.getStatus());
        }
        
        System.out.print("\n📝 Select observation number to cancel: ");
        int obsIndex = readInt() - 1;
        
        System.out.print("📝 Justification for cancellation: ");
        String justification = scanner.nextLine();
        
        if (observationController.cancelObservation(studentId, obsIndex, justification)) {
            System.out.println("\n✅ Observation cancelled successfully!");
        }
    }
    
    public void showViewStudentRecord() {
        System.out.println("\n" + line(40));
        System.out.println("👨‍🎓 VIEW STUDENT RECORD");
        System.out.println(line(40));
        
        showStudentList();
        
        System.out.print("\n📝 Student ID: ");
        String studentId = scanner.nextLine();
        
        observationController.printStudentRecord(studentId);
    }
    
    public void showViewOwnRecord() {
        String currentUserId = authController.getCurrentUser().getId();
        System.out.println("\n" + line(40));
        System.out.println("📖 MY ACADEMIC RECORD");
        System.out.println(line(40));
        
        observationController.printStudentRecord(currentUserId);
    }
    
    private void showStudentList() {
        List<Student> students = userController.getAllStudents();
        
        System.out.println("\n📋 Student List:");
        System.out.println(line(50));
        System.out.printf("%-12s %-25s\n", "ID", "Name");
        System.out.println(line(50));
        
        for (Student student : students) {
            if (student.isActive()) {
                System.out.printf("%-12s %-25s\n",
                    student.getId(),
                    truncate(student.getName(), 25));
            }
        }
        System.out.println(line(50));
    }
    
    private int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private String truncate(String str, int length) {
        if (str == null) return "";
        if (str.length() <= length) return str;
        return str.substring(0, length - 3) + "...";
    }
    
    private String line(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append("─");
        }
        return sb.toString();
    }
}