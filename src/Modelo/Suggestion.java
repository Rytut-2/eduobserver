package Modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Suggestion {
    
    // ============================ ATRIBUTOS ============================
    
    private int id;
    private String text;
    private LocalDateTime date;
    private Representative representative;
    
    // ============================ CONSTRUCTOR ============================
    
    /**
     * Constructor for a new suggestion
     * @param id Unique identifier
     * @param text Suggestion text content
     * @param representative Representative who sent the suggestion
     */
    public Suggestion(int id, String text, Representative representative) {
        this.id = id;
        this.text = text;
        this.representative = representative;
        this.date = LocalDateTime.now();
    }
    
    // ============================ GETTERS Y SETTERS ============================
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public LocalDateTime getDate() {
        return date;
    }
    
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    
    public Representative getRepresentative() {
        return representative;
    }
    
    public void setRepresentative(Representative representative) {
        this.representative = representative;
    }
    
    // ============================ MÉTODOS UTILITARIOS ============================
    
    /**
     * Gets formatted date string
     * @return Date formatted as "dd/MM/yyyy HH:mm:ss"
     */
    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return date.format(formatter);
    }
    
    /**
     * Gets a shortened version of the text (for preview)
     * @param maxLength Maximum length of the preview
     * @return Shortened text
     */
    public String getShortText(int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Suggestion that = (Suggestion) obj;
        return id == that.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("📝 Suggestion #").append(id).append("\n");
        sb.append("   From: ").append(representative.getName())
          .append(" (ID: ").append(representative.getId()).append(")\n");
        sb.append("   Date: ").append(getFormattedDate()).append("\n");
        sb.append("   Text: ").append(text);
        return sb.toString();
    }
    
}
