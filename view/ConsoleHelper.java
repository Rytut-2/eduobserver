package view;

import java.util.Scanner;

/**
 * ConsoleHelper utility class for handling console I/O operations.
 * Centralizes all input/output operations to follow Single Responsibility Principle.
 *
 * SOLID Principles Applied:
 * - Single Responsibility: Handles all console I/O operations
 */
public class ConsoleHelper {
    private static Scanner scanner = new Scanner(System.in);

    public static void printLine(String message) {
        System.out.println(message);
    }

    public static void printLine() {
        System.out.println();
    }

    public static void print(String message) {
        System.out.print(message);
    }

    public static String readLine() {
        return scanner.nextLine().trim();
    }

    public static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                printLine("Invalid input. Please enter a number.");
            }
        }
    }

    public static void printSeparator() {
        printLine("========================================");
    }

    public static void printHeader(String title) {
        printSeparator();
        printLine("        " + title);
        printSeparator();
    }

    public static void printError(String message) {
        System.out.println("ERROR: " + message);
    }

    public static void printSuccess(String message) {
        System.out.println("SUCCESS: " + message);
    }

    public static void pause() {
        printLine("\nPress Enter to continue...");
        readLine();
    }
}
