package eduobserver;

import controller.Controller;
import model.*;

/**
 * Main entry point for the EduObserver application.
 * Initializes the system with sample data and starts the controller.
 *
 * SOLID Principles Applied:
 * - Single Responsibility: Entry point and initialization only
 */
public class EduObserver {
    public static void main(String[] args) {
        initializeSampleData();

        Controller controller = new Controller();
        controller.start();
    }

    private static void initializeSampleData() {
        DataRepository repository = DataRepository.getInstance();

        // Create sample teachers
        Teacher t1 = new Teacher("T001", "Dr. Maria Garcia", "mgarcia@school.edu", "Science");
        Teacher t2 = new Teacher("T002", "Prof. Carlos Lopez", "clopez@school.edu", "Mathematics");
        repository.addUser(t1);
        repository.addUser(t2);

        // Create sample students
        Student s1 = new Student("S001", "Ana Martinez", "amartinez@student.edu", "10th", "A");
        Student s2 = new Student("S002", "Pedro Sanchez", "psanchez@student.edu", "10th", "B");
        Student s3 = new Student("S003", "Laura Rodriguez", "lrodriguez@student.edu", "11th", "A");
        repository.addUser(s1);
        repository.addUser(s2);
        repository.3
    addUser(s3);

        // Create sample coordinator
        Coordinator c1 = new Coordinator("C001", "Dr. Juan Hernandez", "jhernandez@school.edu", "Academic Affairs");
        repository.addUser(c1);

        // Create sample observations
        Observation obs1 = new Observation("OBS001", "T001", "S001",
            ObservationType.ACADEMIC, Severity.MEDIUM,
            "Excellent participation in science project.");
        repository.addObservation(obs1);

        Observation obs2 = new Observation("OBS002", "T001", "S002",
            ObservationType.DISCIPLINARY, Severity.HIGH,
            "Disruptive behavior during laboratory session.");
        repository.addObservation(obs2);

        Observation obs3 = new Observation("OBS003", "T002", "S001",
            ObservationType.ACADEMIC, Severity.LOW,
            "Needs improvement in algebra homework.");
        repository.addObservation(obs3);
    }
}
