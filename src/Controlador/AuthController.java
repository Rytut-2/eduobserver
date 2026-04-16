package Controlador;

import Modelo.User;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for authentication and session management
 * Handles login, logout, password changes, and role verification
 */
public class AuthController {
    private User currentUser;
    private List<User> users;
    
    public AuthController(List<User> users) {
        this.users = users != null ? users : new ArrayList<>();
        this.currentUser = null;
    }
    
    /**
     * Authenticates a user with ID and password
     * @param id User's ID
     * @param password User's password
     * @return true if authentication successful
     */
    public boolean login(String id, String password) {
        if (users == null) return false;
        
        for (User user : users) {
            if (user.login(id, password)) {
                currentUser = user;
                System.out.println("✅ Login successful! Welcome " + user.getName());
                
                if (user.isFirstTime()) {
                    System.out.println("⚠️ You must change your password on first login");
                }
                return true;
            }
        }
        System.out.println("❌ Login failed: Invalid credentials or inactive account");
        return false;
    }
    
    /**
     * Logs out the current user
     */
    public void logout() {
        if (currentUser != null) {
            System.out.println("👋 Goodbye " + currentUser.getName());
            currentUser = null;
        }
    }
    
    /**
     * Changes password for current user (RF-15)
     * @param newPassword New password
     * @return true if password changed successfully
     */
    public boolean changePassword(String newPassword) {
        if (currentUser != null) {
            try {
                currentUser.changePassword(newPassword);
                System.out.println("✅ Password changed successfully!");
                return true;
            } catch (IllegalArgumentException e) {
                System.out.println("❌ " + e.getMessage());
                return false;
            }
        }
        System.out.println("❌ No user logged in");
        return false;
    }
    
    /**
     * Gets the currently logged in user
     * @return Current user or null
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Gets the role of the current user
     * @return Role as string or null
     */
    public String getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }
    
    /**
     * Checks if current user has a specific role
     * @param role Role to check
     * @return true if current user has the role
     */
    public boolean hasRole(String role) {
        return currentUser != null && currentUser.getRole().equals(role);
    }
    
    /**
     * Checks if a user is logged in
     * @return true if user is logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Updates the user list (useful after creating new users)
     * @param users New user list
     */
    public void updateUsers(List<User> users) {
        this.users = users;
    }
}