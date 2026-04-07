package controller;

import model.*;
import view.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CoordinatorController class that handles coordinator-specific operations.
 * Coordinators can manage users, generate reports, and void observations.
 *
 * SOLID Principles Applied:
 * - Single Responsibility: Handles coordinator operations only
 * - Dependency Inversion: Depends on view and repository abstractions
 */
public class CoordinatorController {
    private CoordinatorView coordinatorView;
    private DataRepository repository;
    private Coordinator currentCoordinator;

    public CoordinatorController(Coordinator coordinator) {
        this.coordinatorView = new CoordinatorView();
        this.repository = DataRepository.getInstance();
        this.currentCoordinator = coordinator;
    }

    public void handleCoordinatorSession() {
        boolean sessionActive = true;

        while (sessionActive) {
            int choice = coordinatorView.showMenu();

            switch (choice) {
                case 1:
                    viewAllUsers();
                    break;
                case 2:
                    viewAllObservations();
                    break;
                case 3:
                    viewActiveObservations();
                    break;
                case 4:
                    voidObservation();
                    break;
                case 5:
                    generateReport();
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

    private void viewAllUsers() {
        List<User> users = repository.getAllUsers();
        coordinatorView.showAllUsers(users);
    }

    private void viewAllObservations() {
        List<Observation> observations = repository.getAllObservations();
        coordinatorView.showAllObservations(observations);
    }

    private void viewActiveObservations() {
        List<Observation> observations = repository.getActiveObservations();
        coordinatorView.showActiveObservations(observations);
    }

    private void voidObservation() {
        List<Observation> activeObservations = repository.getActiveObservations();
        String[] data = coordinatorView.getVoidObservationData(activeObservations);

        if (data == null) {
            return;
        }

        String obsId = data[0];
        String justification = data[1];

        Observation observation = repository.getObservation(obsId);
        if (observation != null && observation.getStatus() == ObservationStatus.ACTIVE) {
            observation.voidObservation(justification, currentCoordinator.getName());
            coordinatorView.showObservationVoided(true);
        } else {
            coordinatorView.showObservationVoided(false);
        }
    }

    private void generateReport() {
        StringBuilder report = new StringBuilder();

        report.append("=== EDUOBSERVER SYSTEM REPORT ===\n\n");

        report.append("USER STATISTICS\n");
        report.append("----------------\n");
        int teachers = repository.getAllTeachers().size();
        int students = repository.getAllStudents().size();
        int totalUsers = repository.getAllUsers().size();
        report.append("Total Users: ").append(totalUsers).append("\n");
        report.append("Teachers: ").append(teachers).append("\n");
        report.append("Students: ").append(students).append("\n\n");

        report.append("OBSERVATION STATISTICS\n");
        report.append("----------------------\n");
        List<Observation> allObs = repository.getAllObservations();
        int totalObs = allObs.size();
        int activeObs = repository.getActiveObservations().size();
        int voidedObs = totalObs - activeObs;

        report.append("Total Observations: ").append(totalObs).append("\n");
        report.append("Active: ").append(activeObs).append("\n");
        report.append("Voided: ").append(voidedObs).append("\n\n");

        report.append("BY TYPE\n");
        long academic = allObs.stream().filter(o -> o.getType() == ObservationType.ACADEMIC).count();
        long disciplinary = allObs.stream().filter(o -> o.getType() == ObservationType.DISCIPLINARY).count();
        report.append("Academic: ").append(academic).append("\n");
        report.append("Disciplinary: ").append(disciplinary).append("\n\n");

        report.append("BY SEVERITY\n");
        long low = allObs.stream().filter(o -> o.getSeverity() == Severity.LOW).count();
        long medium = allObs.stream().filter(o -> o.getSeverity() == Severity.MEDIUM).count();
        long high = allObs.stream().filter(o -> o.getSeverity() == Severity.HIGH).count();
        report.append("Low: ").append(low).append("\n");
        report.append("Medium: ").append(medium).append("\n");
        report.append("High: ").append(high).append("\n");

        coordinatorView.showReport(report.toString());
    }
}
