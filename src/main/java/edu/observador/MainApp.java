// Archivo: src/edu/observador/MainApp.java
package edu.observador;

import edu.observador.data.DataAccessException;
import edu.observador.data.ObservadorDAO;
import edu.observador.data.ObservadorDAOSQLite;
import edu.observador.view.DashboardView;
import edu.observador.view.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static Stage primaryStage;
    private static ObservadorDAO dao;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("EduObservador");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);

        dao = new ObservadorDAOSQLite();
        try {
            dao.inicializarBaseDatos();
            System.out.println("Base de datos inicializada correctamente.");
        } catch (DataAccessException e) {
            System.err.println("Error inicializando BD: " + e.getMessage());
            e.printStackTrace();
        }

        mostrarLogin();
        primaryStage.show();
    }

    public static void mostrarLogin() {
        LoginView loginView = new LoginView();
        Scene scene = new Scene(loginView, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("EduObservador - Inicio de Sesión");
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
    }

    public static void cargarDashboard() {
        DashboardView dashboard = new DashboardView();
        Scene scene = new Scene(dashboard, 1100, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("EduObservador - Dashboard");
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
    }

    public static void volverALogin() {
        mostrarLogin();
    }

    public static ObservadorDAO getDAO() { return dao; }
    public static Stage getPrimaryStage() { return primaryStage; }

    public static void main(String[] args) { launch(args); }
}