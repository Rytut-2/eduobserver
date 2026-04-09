package view;

import model.*;
import java.util.List;


public class TeacherView {

    public int showMenu() {
        ConsoleHelper.printHeader("TEACHER MENU");
        ConsoleHelper.printLine("1. Create Observation");
        ConsoleHelper.printLine("2. View My Observations");
        ConsoleHelper.printLine("3. View All Students");
        ConsoleHelper.printLine("0. Logout");
        ConsoleHelper.printSeparator();
        ConsoleHelper.print("Select an option: ");
        return ConsoleHelper.readInt();
    }

    public ObservationData getObservationData(List<Student> students) {
        ConsoleHelper.printHeader("CREATE OBSERVATION");

        ConsoleHelper.printLine("Available Students:");
        ConsoleHelper.printSeparator();
        for (Student s : students) {
            ConsoleHelper.printLine(s.toString());
        }
        ConsoleHelper.printSeparator();

        ConsoleHelper.print("Enter Student ID: ");
        String studentId = ConsoleHelper.readLine();

        ConsoleHelper.printLine("\nObservation Types:");
        ConsoleHelper.printLine("1. Academic");
        ConsoleHelper.printLine("2. Disciplinary");
        ConsoleHelper.print("Select type: ");
        int typeChoice = ConsoleHelper.readInt();
        ObservationType type = (typeChoice == 1) ? ObservationType.ACADEMIC : ObservationType.DISCIPLINARY;

        ConsoleHelper.printLine("\nSeverity Levels:");
        ConsoleHelper.printLine("1. Low");
        ConsoleHelper.printLine("2. Medium");
        ConsoleHelper.printLine("3. High");
        ConsoleHelper.print("Select severity: ");
        int severityChoice = ConsoleHelper.readInt();
        Severity severity;
        switch (severityChoice) {
            case 1: severity = Severity.LOW; break;
            case 3: severity = Severity.HIGH; break;
            default: severity = Severity.MEDIUM; break;
        }

        ConsoleHelper.print("\nEnter description: ");
        String description = ConsoleHelper.readLine();

        return new ObservationData(studentId, type, severity, description);
    }

    public void showObservations(List<Observation> observations) {
        ConsoleHelper.printHeader("MY OBSERVATIONS");
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

    public void showStudents(List<Student> students) {
        ConsoleHelper.printHeader("ALL STUDENTS");
        if (students.isEmpty()) {
            ConsoleHelper.printLine("No students found.");
        } else {
            for (Student s : students) {
                ConsoleHelper.printLine(s.toString());
            }
        }
        ConsoleHelper.pause();
    }

    public void showObservationCreated() {
        ConsoleHelper.printSuccess("Observation created successfully!");
        ConsoleHelper.pause();
    }

    public void showError(String message) {
        ConsoleHelper.printError(message);
        ConsoleHelper.pause();
    }

    public static class ObservationData {
        public String studentId;
        public ObservationType type;
        public Severity severity;
        public String description;

        public ObservationData(String studentId, ObservationType type, Severity severity, String description) {
            this.studentId = studentId;
            this.type = type;
            this.severity = severity;
            this.description = description;
        }
    }
}
