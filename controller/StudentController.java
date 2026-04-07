package controller;

import model.*;
import view.*;
import java.util.List;

/**
 * StudentController class that handles student-specific operations.
 * Enforces privacy by only showing the student their own observations.
 *
 * SOLID Principles Applied:
 * - Single Responsibility: Handles student operations only
 * - Dependency Inversion: Depends on view and repository abstractions
 */
public class StudentController {
    private StudentView studentView;
    private DataRepository repository;
    private Student currentStudent;

    public StudentController(Student student) {
        this.studentView = new StudentView();
        this.repository = DataRepository.getInstance();
        this.currentStudent = student;
    }

    public void handleStudentSession() {
        boolean sessionActive = true;

        while (sessionActive) {
            int choice = studentView.showMenu();

            switch (choice) {
                case 1:
                    viewObservationHistory();
                    break;
                case 2:
                    viewActiveObservations();
                    break;
                case 0:
                    sessionActive = false;
                    ConsoleHelper.printLine("Logging out...");
                    break;
                default:
                    ConsoleHelper.printError("Invalid option. Please try again.");
                    ConsoleHelper.pause();
            }
        }
    }

    private void viewObservationHistory() {
        List<Observation> observations = repository.getObservationsByStudent(currentStudent.getId());
        studentView.showObservationHistory(observations, currentStudent);
    }

    private void viewActiveObservations() {
        List<Observation> observations = repository.getObservationsByStudent(currentStudent.getId());
        studentView.showActiveObservations(observations, currentStudent);
    }
}
