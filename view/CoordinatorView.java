package view;

import model.*;
import java.util.List;

public class CoordinatorView {

    public int showMenu() {
        ConsoleHelper.printHeader("COORDINATOR MENU");
        ConsoleHelper.printLine("1. View All Users");
        ConsoleHelper.printLine("2. View All Observations");
        ConsoleHelper.printLine("3. View Active Observations");
        ConsoleHelper.printLine("4. Void an Observation");
        ConsoleHelper.printLine("5. Generate Report");
        ConsoleHelper.printLine("0. Logout");
        ConsoleHelper.printSeparator();
        ConsoleHelper.print("Select an option: ");
        return ConsoleHelper.readInt();
    }

    public void showAllUsers(List<User> users) {
        ConsoleHelper.printHeader("ALL USERS");
        if (users.isEmpty()) {
            ConsoleHelper.printLine("No users found.");
        } else {
            ConsoleHelper.printLine(String.format("%-10s %-20s %-30s %-12s", "ID", "Name", "Email", "Role"));
            ConsoleHelper.printSeparator();
            for (User u : users) {
                ConsoleHelper.printLine(String.format("%-10s %-20s %-30s %-12s",
                    u.getId(), u.getName(), u.getEmail(), u.getRole()));
            }
        }
        ConsoleHelper.pause();
    }

    public void showAllObservations(List<Observation> observations) {
        ConsoleHelper.printHeader("ALL OBSERVATIONS");
        if (observations.isEmpty()) {
            ConsoleHelper.printLine("No observations found.");
        } else {
            for (Observation obs : observations) {
                ConsoleHelper.printLine(obs.toString());
                ConsoleHelper.printSeparator();
            }
        }
        ConsoleHelper.pause();
    }

    public void showActiveObservations(List<Observation> observations) {
        ConsoleHelper.printHeader("ACTIVE OBSERVATIONS");
        if (observations.isEmpty()) {
            ConsoleHelper.printLine("No active observations found.");
        } else {
            for (Observation obs : observations) {
                if (obs.getStatus() == ObservationStatus.ACTIVE) {
                    ConsoleHelper.printLine(obs.toString());
                    ConsoleHelper.printSeparator();
                }
            }
        }
        ConsoleHelper.pause();
    }

    public String[] getVoidObservationData(List<Observation> observations) {
        ConsoleHelper.printHeader("VOID OBSERVATION");
        ConsoleHelper.printLine("Active Observations:");
        ConsoleHelper.printSeparator();

        boolean hasActive = false;
        for (Observation obs : observations) {
            if (obs.getStatus() == ObservationStatus.ACTIVE) {
                ConsoleHelper.printLine("ID: " + obs.getId() + " | Type: " + obs.getType().getDisplayName() +
                    " | Severity: " + obs.getSeverity().getDisplayName());
                hasActive = true;
            }
        }

        if (!hasActive) {
            ConsoleHelper.printLine("No active observations to void.");
            ConsoleHelper.pause();
            return null;
        }

        ConsoleHelper.printSeparator();
        ConsoleHelper.print("\nEnter Observation ID to void: ");
        String obsId = ConsoleHelper.readLine();
        ConsoleHelper.print("Enter justification: ");
        String justification = ConsoleHelper.readLine();

        return new String[]{obsId, justification};
    }

    public void showObservationVoided(boolean success) {
        if (success) {
            ConsoleHelper.printSuccess("Observation voided successfully!");
        } else {
            ConsoleHelper.printError("Failed to void observation. ID not found or already voided.");
        }
        ConsoleHelper.pause();
    }

    public void showReport(String report) {
        ConsoleHelper.printHeader("SYSTEM REPORT");
        ConsoleHelper.printLine(report);
        ConsoleHelper.pause();
    }
}
