package controller;

import model.*;
import view.*;
import java.util.List;
import java.util.UUID;

/**
 * TeacherController class that handles teacher-specific operations.
 *
 * SOLID Principles Applied:
 * - Single Responsibility: Handles teacher operations only
 * - Dependency Inversion: Depends on view and repository abstractions
 */
public class TeacherController {
    private TeacherView teacherView;
    private DataRepository repository;
    private Teacher currentTeacher;

    public TeacherController(Teacher teacher) {
        this.teacherView = new TeacherView();
        this.repository = DataRepository.getInstance();
        this.currentTeacher = teacher;
    }

    public void handleTeacherSession() {
        boolean sessionActive = true;

        while (sessionActive) {
            int choice = teacherView.showMenu();

            switch (choice) {
                case 1:
                    createObservation();
                    break;
                case 2:
                    viewMyObservations();
                    break;
                case 3:
                    viewAllStudents();
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

    private void createObservation() {
        List<Student> students = repository.getAllStudents();
        if (students.isEmpty()) {
            teacherView.showError("No students available in the system.");
            return;
        }

        TeacherView.ObservationData data = teacherView.getObservationData(students);

        Student selectedStudent = (Student) repository.getUser(data.studentId);
        if (selectedStudent == null) {
            teacherView.showError("Student not found with ID: " + data.studentId);
            return;
        }

        String observationId = UUID.randomUUID().toString().substring(0, 8);
        Observation observation = new Observation(
            observationId,
            currentTeacher.getId(),
            data.studentId,
            data.type,
            data.severity,
            data.description
        );

        repository.addObservation(observation);
        teacherView.showObservationCreated();
    }

    private void viewMyObservations() {
        List<Observation> observations = repository.getObservationsByTeacher(currentTeacher.getId());
        teacherView.showObservations(observations);
    }

    private void viewAllStudents() {
        List<Student> students = repository.getAllStudents();
        teacherView.showStudents(students);
    }
}
