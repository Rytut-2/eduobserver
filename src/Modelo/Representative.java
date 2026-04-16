package Modelo;

import java.util.Objects;

public class Representative extends Student{
    
    // ============================ ATRIBUTOS ============================
    
    private SuggestionBox suggestionBox;   // Suggestion box for group reports
    
    // ============================ CONSTRUCTOR ============================
    
    /**
     * Constructor for a representative
     * @param id Unique identifier (e.g., "REP-001")
     * @param name Full name of the representative
     * @param password Initial password
     */
    public Representative(String id, String name, String password) {
        super(id, name, password);
        this.suggestionBox = null;
    }
    
    // ============================ MÉTODOS PRINCIPALES ============================
    
    /**
     * Gets the role of the user
     * @return "REPRESENTATIVE"
     */
    @Override
    public String getRole() {
        return "REPRESENTATIVE";
    }
    
    /**
     * RF-14: Accesses the group suggestion box
     * @return The suggestion box associated with this representative
     */
    public SuggestionBox accessSuggestionBox() {
        if (suggestionBox == null) {
            System.out.println("⚠️ No suggestion box assigned to this representative");
            return null;
        }
        
        System.out.println("📬 Representative " + this.getName() + " is accessing the suggestion box");
        return suggestionBox;
    }
    
    /**
     * RF-14: Sends a suggestion to the group suggestion box
     * @param text The suggestion text
     * @return true if suggestion was sent successfully
     */
    public boolean sendSuggestion(String text) {
        if (suggestionBox == null) {
            System.out.println("❌ No suggestion box available. Please contact the coordinator.");
            return false;
        }
        
        if (text == null || text.trim().isEmpty()) {
            System.out.println("❌ Suggestion text cannot be empty");
            return false;
        }
        
        boolean result = suggestionBox.sendSuggestion(text, this);
        if (result) {
            System.out.println("✅ Suggestion sent successfully by representative: " + this.getName());
        }
        return result;
    }
    
    /**
     * RF-14: Sends a suggestion with a specific title/category
     * @param title Title or category of the suggestion
     * @param text The suggestion text
     * @return true if suggestion was sent successfully
     */
    public boolean sendSuggestion(String title, String text) {
        String fullText = "[" + title + "] " + text;
        return sendSuggestion(fullText);
    }
    
    // ============================ GETTERS Y SETTERS ============================
    
    public SuggestionBox getSuggestionBox() {
        return suggestionBox;
    }
    
    public void setSuggestionBox(SuggestionBox suggestionBox) {
        this.suggestionBox = suggestionBox;
        System.out.println("📬 Suggestion box assigned to representative: " + this.getName());
    }
    
    // ============================ MÉTODOS UTILITARIOS ============================
    
    /**
     * Checks if the representative has a suggestion box assigned
     * @return true if suggestion box exists
     */
    public boolean hasSuggestionBox() {
        return suggestionBox != null;
    }
    
    /**
     * Views all suggestions in the box (if accessible)
     */
    public void viewAllSuggestions() {
        if (suggestionBox == null) {
            System.out.println("❌ No suggestion box available");
            return;
        }
        
        suggestionBox.printAllSuggestions();
    }
    
    /**
     * Gets suggestions sent by this representative
     * @return List of suggestions from this representative
     */
    public java.util.List<Suggestion> getMySuggestions() {
        if (suggestionBox == null) {
            System.out.println("❌ No suggestion box available");
            return java.util.Collections.emptyList();
        }
        
        return suggestionBox.getSuggestionsByRepresentative(this);
    }
    
    /**
     * Prints the representative's complete information
     */
    public void printInfo() {
        System.out.println("\n" + "═".repeat(50));
        System.out.println("👥 REPRESENTATIVE INFORMATION");
        System.out.println("═".repeat(50));
        System.out.println("ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Grade: " + (getGrade() != null ? getGrade().getName() : "Not assigned"));
        System.out.println("Active: " + (active ? "Yes" : "No"));
        System.out.println("Suggestion Box: " + (suggestionBox != null ? "Assigned" : "Not assigned"));
        System.out.println("Total Suggestions Sent: " + getMySuggestions().size());
        System.out.println("═".repeat(50));
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        Representative that = (Representative) obj;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
    
    @Override
    public String toString() {
        return String.format("Representative{id='%s', name='%s', grade='%s', active=%s, hasSuggestionBox=%s}",
                id, name, getGrade() != null ? getGrade().getName() : "None", active, suggestionBox != null);
    }
    
}
