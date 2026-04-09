package view;

import model.*;


public class MainView {

    public void showWelcome() {
        ConsoleHelper.printHeader("EduObserver System");
        ConsoleHelper.printLine("Academic Observation ");
        ConsoleHelper.printLine();
    }

    public int showMainMenu() {
        ConsoleHelper.printSeparator();
        ConsoleHelper.printLine("MAIN MENU");
        ConsoleHelper.printSeparator();
        ConsoleHelper.printLine("1. Login as Teacher");
        ConsoleHelper.printLine("2. Login as Student");
        ConsoleHelper.printLine("3. Login as Coordinator");
        ConsoleHelper.printLine("0. Exit");
        ConsoleHelper.printSeparator();
        ConsoleHelper.print("Select an option: ");
        return ConsoleHelper.readInt();
    }

    public String[] getLoginCredentials() {
        ConsoleHelper.printHeader("Login");
        ConsoleHelper.print("Enter User ID: ");
        String id = ConsoleHelper.readLine();
        return new String[]{id};
    }

    public void showLoginSuccess(User user) {
        ConsoleHelper.printSuccess("Login successful!");
        ConsoleHelper.printLine("Welcome, " + user.getName() + " (" + user.getRole() + ")");
        ConsoleHelper.pause();
    }

    public void showLoginError() {
        ConsoleHelper.printError("Invalid user ID or user not found.");
        ConsoleHelper.pause();
    }

    public void showGoodbye() {
        ConsoleHelper.printLine("\nThank you for using EduObserver System. Goodbye!");
    }

    public void showInvalidOption() {
        ConsoleHelper.printError("Invalid option. Please try again.");
        ConsoleHelper.pause();
    }
}
