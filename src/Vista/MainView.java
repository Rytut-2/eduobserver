package Vista;

import Controlador.*;
import Modelo.User;
import java.util.Scanner;

public class MainView {
    private AuthController authController;
    private UserController userController;
    private ObservationController observationController;
    private GradeController gradeController;
    private ReportController reportController;
    private SuggestionController suggestionController;
    
    private AuthView authView;
    private UserView userView;
    private ObservationView observationView;
    private GradeView gradeView;
    private ReportView reportView;
    private SuggestionView suggestionView;
    
    private Scanner scanner;
    private boolean running;
    
    public MainView() {
        this.scanner = new Scanner(System.in);
        this.running = true;
        
        // Inicializar controladores
        this.authController = new AuthController(null);
        this.userController = new UserController(authController);
        this.authController = new AuthController(userController.getAllUsers());
        this.observationController = new ObservationController(authController, userController);
        this.gradeController = new GradeController(authController, userController);
        this.reportController = new ReportController(authController, userController, gradeController);
        this.suggestionController = new SuggestionController(authController, gradeController);
        
        // Inicializar vistas
        this.authView = new AuthView(scanner, authController);
        this.userView = new UserView(scanner, userController, authController);
        this.observationView = new ObservationView(scanner, observationController, userController, authController);
        this.gradeView = new GradeView(scanner, gradeController, authController);
        this.reportView = new ReportView(scanner, reportController, authController);
        this.suggestionView = new SuggestionView(scanner, suggestionController, authController);
    }
    
    public void start() {
        printBanner();
        
        // Proceso de login
        if (!authView.showLogin()) {
            System.out.println("\n❌ Failed to login. Exiting system...");
            return;
        }
        
        // RF-15: Verificar si necesita cambiar contraseña
        if (authController.getCurrentUser().isFirstTime()) {
            authView.showForcePasswordChange();
        }
        
        // Mostrar menú según el rol
        showRoleBasedMenu();
    }
    
    private void printBanner() {
        System.out.println("\n" + line(60));
        System.out.println("   🎓 EDU-OBSERVADOR - Student Observation System");
        System.out.println("   Programación Orientada a Objetos");
        System.out.println(line(60));
        System.out.println("   Integrantes:");
        System.out.println("   • Jesus Beltran");
        System.out.println("   • Daniel Castillo");
        System.out.println("   • Serguio Martinez");
        System.out.println(line(60));
    }
    
    private void showRoleBasedMenu() {
        String role = authController.getCurrentUserRole();
        User currentUser = authController.getCurrentUser();
        
        System.out.println("\n✅ Welcome " + currentUser.getName() + "!");
        System.out.println("📋 Role: " + role);
        
        switch (role) {
            case "COORDINATOR":
                showCoordinatorMenu();
                break;
            case "TEACHER":
                showTeacherMenu();
                break;
            case "STUDENT":
                showStudentMenu();
                break;
            case "REPRESENTATIVE":
                showRepresentativeMenu();
                break;
            case "GROUP_TEACHER":
                showGroupTeacherMenu();
                break;
            default:
                System.out.println("❌ Unknown role: " + role);
        }
    }
    
    private void showCoordinatorMenu() {
        while (running && authController.isLoggedIn()) {
            printMenuHeader("COORDINATOR CONTROL PANEL");
            System.out.println("┌────────────────────────────────────────┐");
            System.out.println("│ 1. 📋 Manage Users                     │");
            System.out.println("│ 2. 📝 Create Observation               │");
            System.out.println("│ 3. ❌ Cancel Observation               │");
            System.out.println("│ 4. 📊 Generate Reports                 │");
            System.out.println("│ 5. 🏫 Manage Grades                    │");
            System.out.println("│ 6. 💬 View Suggestions                 │");
            System.out.println("│ 7. 🔑 Change Password                  │");
            System.out.println("│ 8. 🚪 Logout                           │");
            System.out.println("│ 0. 🛑 Exit System                      │");
            System.out.println("└────────────────────────────────────────┘");
            System.out.print("\n👉 Select an option: ");
            
            int option = readInt();
            
            switch (option) {
                case 1:
                    userView.showUserManagementMenu();
                    break;
                case 2:
                    observationView.showCreateObservationMenu();
                    break;
                case 3:
                    observationView.showCancelObservationMenu();
                    break;
                case 4:
                    reportView.showReportMenu();
                    break;
                case 5:
                    gradeView.showGradeManagementMenu();
                    break;
                case 6:
                    suggestionView.showSuggestionBoxMenu();
                    break;
                case 7:
                    authView.showChangePassword();
                    break;
                case 8:
                    logout();
                    return;
                case 0:
                    exitSystem();
                    return;
                default:
                    System.out.println("❌ Invalid option");
            }
        }
    }
    
    private void showTeacherMenu() {
        while (running && authController.isLoggedIn()) {
            printMenuHeader("TEACHER CONTROL PANEL");
            System.out.println("┌────────────────────────────────────────┐");
            System.out.println("│ 1. ⚠️ Create Disciplinary Observation  │");
            System.out.println("│ 2. 📚 Create Academic Observation      │");
            System.out.println("│ 3. 👨‍🎓 View Student Record              │");
            System.out.println("│ 4. 🔑 Change Password                  │");
            System.out.println("│ 5. 🚪 Logout                           │");
            System.out.println("│ 0. 🛑 Exit System                      │");
            System.out.println("└────────────────────────────────────────┘");
            System.out.print("\n👉 Select an option: ");
            
            int option = readInt();
            
            switch (option) {
                case 1:
                    observationView.showCreateDisciplinaryObservation();
                    break;
                case 2:
                    observationView.showCreateAcademicObservation();
                    break;
                case 3:
                    observationView.showViewStudentRecord();
                    break;
                case 4:
                    authView.showChangePassword();
                    break;
                case 5:
                    logout();
                    return;
                case 0:
                    exitSystem();
                    return;
                default:
                    System.out.println("❌ Invalid option");
            }
        }
    }
    
    private void showStudentMenu() {
        while (running && authController.isLoggedIn()) {
            printMenuHeader("STUDENT PORTAL");
            System.out.println("┌────────────────────────────────────────┐");
            System.out.println("│ 1. 📖 View My Academic Record         │");
            System.out.println("│ 2. 🔑 Change Password                  │");
            System.out.println("│ 3. 🚪 Logout                           │");
            System.out.println("│ 0. 🛑 Exit System                      │");
            System.out.println("└────────────────────────────────────────┘");
            System.out.print("\n👉 Select an option: ");
            
            int option = readInt();
            
            switch (option) {
                case 1:
                    observationView.showViewOwnRecord();
                    break;
                case 2:
                    authView.showChangePassword();
                    break;
                case 3:
                    logout();
                    return;
                case 0:
                    exitSystem();
                    return;
                default:
                    System.out.println("❌ Invalid option");
            }
        }
    }
    
    private void showRepresentativeMenu() {
        while (running && authController.isLoggedIn()) {
            printMenuHeader("REPRESENTATIVE PORTAL");
            System.out.println("┌────────────────────────────────────────┐");
            System.out.println("│ 1. 📖 View My Academic Record         │");
            System.out.println("│ 2. 💬 Access Suggestion Box           │");
            System.out.println("│ 3. ✉️ Send New Suggestion              │");
            System.out.println("│ 4. 🔑 Change Password                  │");
            System.out.println("│ 5. 🚪 Logout                           │");
            System.out.println("│ 0. 🛑 Exit System                      │");
            System.out.println("└────────────────────────────────────────┘");
            System.out.print("\n👉 Select an option: ");
            
            int option = readInt();
            
            switch (option) {
                case 1:
                    observationView.showViewOwnRecord();
                    break;
                case 2:
                    suggestionView.showSuggestionBoxMenu();
                    break;
                case 3:
                    suggestionView.showSendSuggestion();
                    break;
                case 4:
                    authView.showChangePassword();
                    break;
                case 5:
                    logout();
                    return;
                case 0:
                    exitSystem();
                    return;
                default:
                    System.out.println("❌ Invalid option");
            }
        }
    }
    
    private void showGroupTeacherMenu() {
        while (running && authController.isLoggedIn()) {
            printMenuHeader("GROUP TEACHER PORTAL");
            System.out.println("┌────────────────────────────────────────┐");
            System.out.println("│ 1. 📝 Create Observation               │");
            System.out.println("│ 2. 📖 View Full Grade History (RF-13)  │");
            System.out.println("│ 3. 👨‍🎓 View Student Record              │");
            System.out.println("│ 4. 📊 View Grade Statistics            │");
            System.out.println("│ 5. 🔑 Change Password                  │");
            System.out.println("│ 6. 🚪 Logout                           │");
            System.out.println("│ 0. 🛑 Exit System                      │");
            System.out.println("└────────────────────────────────────────┘");
            System.out.print("\n👉 Select an option: ");
            
            int option = readInt();
            
            switch (option) {
                case 1:
                    observationView.showCreateObservationMenu();
                    break;
                case 2:
                    gradeView.showGradeFullHistory();
                    break;
                case 3:
                    observationView.showViewStudentRecord();
                    break;
                case 4:
                    gradeView.showGradeStatistics();
                    break;
                case 5:
                    authView.showChangePassword();
                    break;
                case 6:
                    logout();
                    return;
                case 0:
                    exitSystem();
                    return;
                default:
                    System.out.println("❌ Invalid option");
            }
        }
    }
    
    private void printMenuHeader(String title) {
        System.out.println("\n" + line(50));
        System.out.println("   " + title);
        System.out.println(line(50));
    }
    
    private void logout() {
        authController.logout();
        System.out.println("\n👋 You have been logged out.\n");
        start();
    }
    
    private void exitSystem() {
        System.out.println("\n🛑 Thank you for using Edu-Observador!");
        System.out.println("   Goodbye! 👋\n");
        running = false;
        scanner.close();
        System.exit(0);
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
            sb.append("═");
        }
        return sb.toString();
    }
    
    public static void main(String[] args) {
        MainView mainView = new MainView();
        mainView.start();
    }
}