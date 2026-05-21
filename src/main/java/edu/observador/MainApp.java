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

/**
 * Clase principal de la aplicación EduObservador.
 * Inicializa la base de datos y maneja las transiciones entre Login y Dashboard.
 */
public class MainApp extends Application {

    private static Stage primaryStage;
    private static ObservadorDAO dao;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("EduObservador");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);

        // Inicializar DAO y base de datos
        dao = new ObservadorDAOSQLite();
        try {
            dao.inicializarBaseDatos();
            System.out.println("Base de datos inicializada correctamente.");
        } catch (DataAccessException e) {
            System.err.println("Error inicializando BD: " + e.getMessage());
            e.printStackTrace();
        }

        // Mostrar pantalla de login
        mostrarLogin();
        primaryStage.show();
    }

    /**
     * Muestra la pantalla de inicio de sesión.
     */
    public static void mostrarLogin() {
        LoginView loginView = new LoginView();
        Scene scene = new Scene(loginView, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("EduObservador - Inicio de Sesión");
        primaryStage.centerOnScreen();
    }

    /**
     * Carga el dashboard principal después del login exitoso.
     */
    public static void cargarDashboard() {
        DashboardView dashboard = new DashboardView();
        Scene scene = new Scene(dashboard, 1100, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("EduObservador - Dashboard");
        primaryStage.centerOnScreen();
    }

    /**
     * Vuelve a la pantalla de login (cierra sesión).
     */
    public static void volverALogin() {
        mostrarLogin();
    }

    /**
     * Obtiene el DAO global para ser usado por los controladores.
     *
     * @return ObservadorDAO
     */
    public static ObservadorDAO getDAO() {
        return dao;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}