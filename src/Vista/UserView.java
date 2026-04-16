package Vista;

import Controlador.UserController;
import Controlador.AuthController;
import Modelo.*;
import java.util.List;
import java.util.Scanner;

public class UserView {
    private Scanner scanner;
    private UserController userController;
    private AuthController authController;
    
    public UserView(Scanner scanner, UserController userController, AuthController authController) {
        this.scanner = scanner;
        this.userController = userController;
        this.authController = authController;
    }
    
    public void showUserManagementMenu() {
        if (!authController.hasRole("COORDINATOR")) {
            System.out.println("❌ Permission denied. Only Coordinator can manage users.");
            return;
        }
        
        boolean back = false;
        while (!back) {
            System.out.println("\n" + line(40));
            System.out.println("👥 USER MANAGEMENT");
            System.out.println(line(40));
            System.out.println("1. List all users");
            System.out.println("2. Create new user");
            System.out.println("3. Edit user");
            System.out.println("4. Disable user");
            System.out.println("5. Enable user");
            System.out.println("0. Back to main menu");
            System.out.print("\nOption: ");
            
            int option = readInt();
            
            switch (option) {
                case 1:
                    showAllUsers();
                    break;
                case 2:
                    showCreateUser();
                    break;
                case 3:
                    showEditUser();
                    break;
                case 4:
                    showDisableUser();
                    break;
                case 5:
                    showEnableUser();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("❌ Invalid option");
            }
        }
    }
    
    private void showAllUsers() {
        List<User> users = userController.getAllUsers();
        
        System.out.println("\n" + line(70));
        System.out.println("📋 ALL USERS");
        System.out.println(line(70));
        System.out.printf("%-12s %-25s %-15s %-10s\n", "ID", "Name", "Role", "Active");
        System.out.println(line(70));
        
        for (User user : users) {
            System.out.printf("%-12s %-25s %-15s %-10s\n",
                user.getId(),
                truncate(user.getName(), 25),
                user.getRole(),
                user.isActive() ? "✓ Yes" : "✗ No");
        }
        System.out.println(line(70));
        System.out.println("Total users: " + users.size());
    }
    
    private void showCreateUser() {
        System.out.println("\n" + line(40));
        System.out.println("➕ CREATE NEW USER");
        System.out.println(line(40));
        
        System.out.print("ID: ");
        String id = scanner.nextLine();
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        System.out.println("\nUser types:");
        System.out.println("1. Student");
        System.out.println("2. Teacher");
        System.out.println("3. Coordinator");
        System.out.println("4. Representative");
        System.out.println("5. Group Teacher");
        System.out.print("Select type: ");
        
        int type = readInt();
        User newUser = null;
        
        switch (type) {
            case 1:
                newUser = new Student(id, name, password);
                break;
            case 2:
                newUser = new Teacher(id, name, password);
                break;
            case 3:
                newUser = new Coordinator(id, name, password);
                break;
            case 4:
                newUser = new Representative(id, name, password);
                break;
            case 5:
                newUser = new GroupTeacher(id, name, password);
                break;
            default:
                System.out.println("❌ Invalid type");
                return;
        }
        
        if (userController.createUser(newUser)) {
            System.out.println("\n✅ User created successfully!");
        }
    }
    
    private void showEditUser() {
        System.out.print("\n📝 Enter user ID to edit: ");
        String userId = scanner.nextLine();
        
        User user = userController.findUserById(userId);
        if (user == null) {
            System.out.println("❌ User not found");
            return;
        }
        
        System.out.println("\nCurrent information:");
        System.out.println("  ID: " + user.getId());
        System.out.println("  Name: " + user.getName());
        System.out.println("  Role: " + user.getRole());
        
        System.out.print("\nNew name (press Enter to keep): ");
        String newName = scanner.nextLine();
        if (newName.isEmpty()) newName = user.getName();
        
        System.out.print("New password (press Enter to keep): ");
        String newPassword = scanner.nextLine();
        if (newPassword.isEmpty()) newPassword = null;
        
        if (userController.editUser(userId, newName, newPassword)) {
            System.out.println("\n✅ User updated successfully!");
        }
    }
    
    private void showDisableUser() {
        System.out.print("\n🔒 Enter user ID to disable: ");
        String userId = scanner.nextLine();
        
        User user = userController.findUserById(userId);
        if (user == null) {
            System.out.println("❌ User not found");
            return;
        }
        
        System.out.println("\n⚠️ You are about to disable user:");
        System.out.println("   ID: " + user.getId());
        System.out.println("   Name: " + user.getName());
        System.out.println("   Role: " + user.getRole());
        System.out.print("\nConfirm disable? (y/n): ");
        
        String confirm = scanner.nextLine();
        if (confirm.equalsIgnoreCase("y")) {
            if (userController.disableUser(userId)) {
                System.out.println("\n✅ User disabled successfully!");
            }
        } else {
            System.out.println("❌ Operation cancelled");
        }
    }
    
    private void showEnableUser() {
        System.out.print("\n🔓 Enter user ID to enable: ");
        String userId = scanner.nextLine();
        
        User user = userController.findUserById(userId);
        if (user == null) {
            System.out.println("❌ User not found");
            return;
        }
        
        if (!user.isActive()) {
            user.setActive(true);
            System.out.println("\n✅ User enabled successfully!");
        } else {
            System.out.println("\n⚠️ User is already active");
        }
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