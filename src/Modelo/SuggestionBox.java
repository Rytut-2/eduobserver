package Modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SuggestionBox {
    
    // ============================ ATRIBUTOS ============================
    
    private List<Suggestion> suggestions;
    private static int nextSuggestionId = 1;
    
    // ============================ CONSTRUCTOR ============================
    
    /**
     * Constructor for a new suggestion box
     */
    public SuggestionBox() {
        this.suggestions = new ArrayList<>();
    }
    
    // ============================ MÉTODOS PRINCIPALES ============================
    
    /**
     * Sends a new suggestion to the box (RF-14)
     * @param text The suggestion text
     * @param representative The representative sending the suggestion
     * @return true if suggestion was sent successfully
     */
    public boolean sendSuggestion(String text, Representative representative) {
        // Validations
        if (text == null || text.trim().isEmpty()) {
            System.out.println("❌ Suggestion text cannot be empty");
            return false;
        }
        
        if (representative == null) {
            System.out.println("❌ Representative cannot be null");
            return false;
        }
        
        // Create and add the suggestion
        Suggestion suggestion = new Suggestion(generateId(), text, representative);
        suggestions.add(suggestion);
        
        System.out.println("📬 Suggestion sent by: " + representative.getName());
        System.out.println("   Text: " + (text.length() > 50 ? text.substring(0, 47) + "..." : text));
        
        return true;
    }
    
    /**
     * Lists all suggestions in the box
     * @return Unmodifiable list of all suggestions
     */
    public List<Suggestion> listSuggestions() {
        return Collections.unmodifiableList(suggestions);
    }
    
    /**
     * Gets the total number of suggestions
     * @return Number of suggestions
     */
    public int getTotalSuggestions() {
        return suggestions.size();
    }
    
    /**
     * Clears all suggestions from the box (Coordinator only)
     * @param coordinator The coordinator requesting the clear operation
     * @return true if suggestions were cleared
     */
    public boolean clearSuggestions(Coordinator coordinator) {
        if (coordinator == null) {
            System.out.println("❌ Only Coordinator can clear suggestions");
            return false;
        }
        
        int count = suggestions.size();
        suggestions.clear();
        System.out.println("✅ Suggestion box cleared by Coordinator: " + coordinator.getName());
        System.out.println("   Removed " + count + " suggestions");
        return true;
    }
    
    /**
     * Gets a suggestion by its ID
     * @param id Suggestion ID
     * @return Suggestion object or null if not found
     */
    public Suggestion getSuggestionById(int id) {
        for (Suggestion suggestion : suggestions) {
            if (suggestion.getId() == id) {
                return suggestion;
            }
        }
        return null;
    }
    
    /**
     * Gets all suggestions from a specific representative
     * @param representative The representative
     * @return List of suggestions from that representative
     */
    public List<Suggestion> getSuggestionsByRepresentative(Representative representative) {
        List<Suggestion> result = new ArrayList<>();
        for (Suggestion suggestion : suggestions) {
            if (suggestion.getRepresentative().equals(representative)) {
                result.add(suggestion);
            }
        }
        return Collections.unmodifiableList(result);
    }
    
    // ============================ MÉTODOS PRIVADOS ============================
    
    /**
     * Generates a unique ID for a suggestion
     * @return Generated ID
     */
    private static int generateId() {
        return nextSuggestionId++;
    }
    
    /**
     * Resets the ID counter (useful for testing)
     */
    protected static void resetIdCounter() {
        nextSuggestionId = 1;
    }
    
    // ============================ MÉTODOS UTILITARIOS ============================
    
    /**
     * Checks if the suggestion box is empty
     * @return true if no suggestions
     */
    public boolean isEmpty() {
        return suggestions.isEmpty();
    }
    
    /**
     * Prints all suggestions to console (formatted)
     */
    public void printAllSuggestions() {
        if (suggestions.isEmpty()) {
            System.out.println("\n📭 Suggestion box is empty");
            return;
        }
        
        System.out.println("\n" + "═".repeat(60));
        System.out.println("📬 SUGGESTION BOX");
        System.out.println("═".repeat(60));
        System.out.printf("Total suggestions: %d\n", suggestions.size());
        System.out.println("─".repeat(60));
        
        for (Suggestion suggestion : suggestions) {
            System.out.println(suggestion);
            System.out.println("─".repeat(60));
        }
    }
    
    @Override
    public String toString() {
        return String.format("SuggestionBox{totalSuggestions=%d}", suggestions.size());
    }
    
}
