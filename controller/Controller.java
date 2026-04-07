package controller;

import model.*;
import view.*;

/**
 * Main Controller class that coordinates the application flow.
 * Acts as the entry point for the MVC architecture.
 *
 * SOLID Principles Applied:
 * - Single Responsibility: Coordinates navigation between different user roles
 * - Dependency Inversion: Depends on abstractions (views and repository)
 */
public class Controller {
    private MainView mainView;
    private DataRepository repository;
    private User currentUser;

    public Controller() {
        this.mainView = new MainView();
        this.repository = DataRepository.getInstance();
    }

    public void start() {
        mainView.showWelcome();

        boolean running = true;
        while (running) {
            int choice = mainView.showMainMenu();

            switch (choice) {
                case 1:
                    handleLogin("Teacher");
                    break;
                case 2:
                    handleLogin("Student");
                    break;
                case 3:
                    handleLogin("Coordinator");
                    break;
                case 0:
                    mainView.showGoodbye();
                    running = false;
                    break;
                default:
                    mainView.showInvalidOption();
            }
        }
    }

    private void handleLogin(String expectedRole) {
        String[] credentials = mainView.getLoginCredentials();
        String userId = credentials[0];

        User user = repository.getUser(userId);

        if (user != null && user.getRole().equals(expectedRole)) {
            currentUser = user;
            mainView.showLoginSuccess(user);

            switch (expectedRole) {
                case "Teacher":
                    new TeacherController((Teacher) user).handleTeacherSession();
                    break;
                case "Student":
                    new StudentController((Student) user).handleStudentSession();
                    break;
                case "Coordinator":
                    new CoordinatorController((Coordinator) user).handleCoordinatorSession();
                    break;
            }
        } else {
            mainView.showLoginError();
        }
    }
}
