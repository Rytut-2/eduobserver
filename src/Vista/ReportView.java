package Vista;

import Controlador.ReportController;
import Controlador.AuthController;
import java.util.Scanner;

public class ReportView {
    private Scanner scanner;
    private ReportController reportController;
    private AuthController authController;
    
    public ReportView(Scanner scanner, ReportController reportController, AuthController authController) {
        this.scanner = scanner;
        this.reportController = reportController;
        this.authController = authController;
    }
    
    public void showReportMenu() {
        if (!authController.hasRole("COORDINATOR")) {
            System.out.println("❌ Permission denied. Only Coordinator can generate reports.");
            return;
        }
        
        boolean back = false;
        while (!back) {
            System.out.println("\n" + line(40));
            System.out.println("📊 REPORT GENERATOR");
            System.out.println(line(40));
            System.out.println("1. Generate General Coexistence Report (RF-11)");
            System.out.println("2. Generate Student Report");
            System.out.println("3. Generate Grade Report");
            System.out.println("0. Back to main menu");
            System.out.print("\nOption: ");
            
            int option = readInt();
            
            switch (option) {
                case 1:
                    reportController.generateGeneralReport();
                    break;
                case 2:
                    generateStudentReport();
                    break;
                case 3:
                    generateGradeReport();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("❌ Invalid option");
            }
        }
    }
    
    private void generateStudentReport() {
        System.out.print("\n📝 Enter Student ID: ");
        String studentId = scanner.nextLine();
        reportController.generateStudentReport(studentId);
    }
    
    private void generateGradeReport() {
        System.out.print("\n📝 Enter Grade ID: ");
        int gradeId = readInt();
        reportController.generateGradeReport(gradeId);
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