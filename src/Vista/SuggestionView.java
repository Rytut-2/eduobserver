package Vista;

import Controlador.SuggestionController;
import Controlador.AuthController;
import java.util.Scanner;

public class SuggestionView {
    private Scanner scanner;
    private SuggestionController suggestionController;
    private AuthController authController;
    
    public SuggestionView(Scanner scanner, SuggestionController suggestionController, AuthController authController) {
        this.scanner = scanner;
        this.suggestionController = suggestionController;
        this.authController = authController;
    }
    
    public void showSuggestionBoxMenu() {
        if (!authController.hasRole("REPRESENTATIVE") && !authController.hasRole("COORDINATOR")) {
            System.out.println("❌ Permission denied. Only Representatives and Coordinator can access suggestions.");
            return;
        }
        
        boolean back = false;
        while (!back) {
            System.out.println("\n" + line(40));
            System.out.println("💬 SUGGESTION BOX");
            System.out.println(line(40));
            System.out.println("1. View all suggestions");
            System.out.println("2. Send new suggestion (Representative only)");
            System.out.println("0. Back to main menu");
            System.out.print("\nOption: ");
            
            int option = readInt();
            
            switch (option) {
                case 1:
                    suggestionController.listSuggestions();
                    break;
                case 2:
                    if (authController.hasRole("REPRESENTATIVE")) {
                        showSendSuggestion();
                    } else {
                        System.out.println("❌ Only Representatives can send suggestions");
                    }
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("❌ Invalid option");
            }
        }
    }
    
    public void showSendSuggestion() {
        if (!authController.hasRole("REPRESENTATIVE")) {
            System.out.println("❌ Permission denied. Only Representatives can send suggestions.");
            return;
        }
        
        System.out.println("\n" + line(40));
        System.out.println("✉️ SEND NEW SUGGESTION");
        System.out.println(line(40));
        
        System.out.println("You can suggest improvements, report issues, or propose activities.");
        System.out.print("\n📝 Your suggestion: ");
        String text = scanner.nextLine();
        
        if (suggestionController.sendSuggestion(text)) {
            System.out.println("\n✅ Suggestion sent successfully!");
            System.out.println("   Thank you for your contribution!");
        }
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