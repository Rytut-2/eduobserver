package Controlador;

import Modelo.*;
import java.util.List;

/**
 * Controller for suggestion box management (RF-14)
 * Handles sending and viewing suggestions
 */
public class SuggestionController {
    private AuthController authController;
    private GradeController gradeController;
    private SuggestionBox suggestionBox;
    
    public SuggestionController(AuthController authController, GradeController gradeController) {
        this.authController = authController;
        this.gradeController = gradeController;
        this.suggestionBox = new SuggestionBox();
    }
    
    /**
     * Accesses the suggestion box (RF-14 - Representative only)
     * @return SuggestionBox or null if not authorized
     */
    public SuggestionBox accessSuggestionBox() {
        if (!authController.hasRole("REPRESENTATIVE") && !authController.hasRole("COORDINATOR")) {
            System.out.println("❌ Permission denied: Only Representatives and Coordinator can access the suggestion box");
            return null;
        }
        
        return suggestionBox;
    }
    
    /**
     * Sends a suggestion (RF-14 - Representative only)
     * @param text Suggestion text
     * @return true if suggestion sent successfully
     */
    public boolean sendSuggestion(String text) {
        if (!authController.hasRole("REPRESENTATIVE")) {
            System.out.println("❌ Permission denied: Only Representatives can send suggestions");
            return false;
        }
        
        User currentUser = authController.getCurrentUser();
        if (!(currentUser instanceof Representative)) {
            System.out.println("❌ Invalid user type");
            return false;
        }
        
        Representative representative = (Representative) currentUser;
        
        // Assign suggestion box if not already assigned
        if (representative.getSuggestionBox() == null) {
            representative.setSuggestionBox(suggestionBox);
        }
        
        return representative.sendSuggestion(text);
    }
    
    /**
     * Lists all suggestions
     */
    public void listSuggestions() {
        if (!authController.hasRole("REPRESENTATIVE") && !authController.hasRole("COORDINATOR")) {
            System.out.println("❌ Permission denied: Only Representatives and Coordinator can view suggestions");
            return;
        }
        
        List<Suggestion> suggestions = suggestionBox.listSuggestions();
        
        if (suggestions.isEmpty()) {
            System.out.println("\n📭 No suggestions found");
            return;
        }
        
        System.out.println("\n" + line(50));
        System.out.println("💬 SUGGESTIONS BOX");
        System.out.println(line(50));
        System.out.println("Total suggestions: " + suggestions.size());
        System.out.println(line(50));
        
        for (Suggestion suggestion : suggestions) {
            System.out.println(suggestion);
            System.out.println(line(50));
        }
    }
    
    private String line(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append("═");
        }
        return sb.toString();
    }
}