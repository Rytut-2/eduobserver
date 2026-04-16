package Vista;

import Controlador.AuthController;
import Modelo.User;
import java.util.Scanner;

public class AuthView {
    private Scanner scanner;
    private AuthController authController;
    
    public AuthView(Scanner scanner, AuthController authController) {
        this.scanner = scanner;
        this.authController = authController;
    }
    
    public boolean showLogin() {
        System.out.println("\n" + line(40));
        System.out.println("🔐 USER LOGIN");
        System.out.println(line(40));
        
        int attempts = 0;
        int maxAttempts = 3;
        
        while (attempts < maxAttempts) {
            System.out.print("\n📧 ID: ");
            String id = scanner.nextLine();
            System.out.print("🔒 Password: ");
            String password = scanner.nextLine();
            
            if (authController.login(id, password)) {
                System.out.println("\n✅ Login successful!");
                return true;
            }
            
            attempts++;
            System.out.println("❌ Invalid credentials! Attempts left: " + (maxAttempts - attempts));
        }
        
        System.out.println("\n🔒 Too many failed attempts. System locked.");
        return false;
    }
    
    public void showForcePasswordChange() {
        System.out.println("\n" + "⚠️".repeat(40));
        System.out.println("   FIRST LOGIN - PASSWORD CHANGE REQUIRED");
        System.out.println("⚠️".repeat(40));
        System.out.println("For security reasons, you must change your password.");
        
        boolean changed = false;
        while (!changed) {
            System.out.print("\n📝 Enter new password (min 4 chars): ");
            String newPassword = scanner.nextLine();
            System.out.print("📝 Confirm new password: ");
            String confirmPassword = scanner.nextLine();
            
            if (newPassword.equals(confirmPassword) && newPassword.length() >= 4) {
                changed = authController.changePassword(newPassword);
                if (changed) {
                    System.out.println("\n✅ Password changed successfully!");
                }
            } else {
                System.out.println("❌ Passwords do not match or are too short (min 4 characters)");
            }
        }
    }
    
    public void showChangePassword() {
        System.out.println("\n" + line(40));
        System.out.println("🔑 CHANGE PASSWORD");
        System.out.println(line(40));
        
        System.out.print("Current password: ");
        String currentPassword = scanner.nextLine();
        
        User currentUser = authController.getCurrentUser();
        if (currentUser == null || !currentUser.getPassword().equals(currentPassword)) {
            System.out.println("❌ Current password is incorrect!");
            return;
        }
        
        System.out.print("New password (min 4 chars): ");
        String newPassword = scanner.nextLine();
        System.out.print("Confirm new password: ");
        String confirmPassword = scanner.nextLine();
        
        if (newPassword.equals(confirmPassword) && newPassword.length() >= 4) {
            if (authController.changePassword(newPassword)) {
                System.out.println("\n✅ Password changed successfully!");
            } else {
                System.out.println("❌ Failed to change password!");
            }
        } else {
            System.out.println("❌ Passwords do not match or are too short (min 4 characters)");
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