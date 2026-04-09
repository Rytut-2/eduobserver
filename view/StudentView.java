package view;

import model.*;
import java.util.List;


public class StudentView {

    public int showMenu() {
        ConsoleHelper.printHeader("STUDENT MENU");
        ConsoleHelper.printLine("1. View My Observation History");
        ConsoleHelper.printLine("2. View Active Observations Only");
        ConsoleHelper.printLine("0. Logout");
        ConsoleHelper.printSeparator();
        ConsoleHelper.print("Select an option: ");
        return ConsoleHelper.readInt();
    }

    public void showObservationHistory(List<Observation> observations, Student student) {
        ConsoleHelper.printHeader("OBSERVATION HISTORY - " + student.getName());
        ConsoleHelper.printLine("Grade: " + student.getGrade() + " | Group: " + student.getGroup());
        ConsoleHelper.printSeparator();

        if (observations.isEmpty()) {
            ConsoleHelper.printLine("No observations found.");
        } else {
            int count = 1;
            for (Observation obs : observations) {
                ConsoleHelper.printLine("Observation #" + count);
                ConsoleHelper.printLine(obs.toString());
                ConsoleHelper.printSeparator();
                count++;
            }
        }
        ConsoleHelper.pause();
    }

    public void showActiveObservations(List<Observation> observations, Student student) {
        ConsoleHelper.printHeader("ACTIVE OBSERVATIONS - " + student.getName());
        ConsoleHelper.printLine("Grade: " + student.getGrade() + " | Group: " + student.getGroup());
        ConsoleHelper.printSeparator();

        if (observations.isEmpty()) {
            ConsoleHelper.printLine("No active observations found.");
        } else {
            int count = 1;
            for (Observation obs : observations) {
                if (obs.getStatus() == ObservationStatus.ACTIVE) {
                    ConsoleHelper.printLine("Observation #" + count);
                    ConsoleHelper.printLine(obs.toString());
                    ConsoleHelper.printSeparator();
                    count++;
                }
            }
        }
        ConsoleHelper.pause();
    }
}
