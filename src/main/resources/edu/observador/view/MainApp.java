// Archivo: src/edu/observador/view/MainApp.java
package edu.observador.view;

import edu.observador.controller.AutenticacionController;
import edu.observador.controller.ObservacionController;
import edu.observador.controller.UsuarioController;
import edu.observador.data.ObservadorDAO;
import edu.observador.data.ObservadorDAOSQLite;
import edu.observador.data.DataAccessException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase principal de la aplicación JavaFX.
 * Inicializa la base de datos, controladores y muestra la ventana de login.
 *
 * @author TuNombre
 * @version 1.0
 */
public class MainApp extends Application {

    private static MainApp instance;
    private Stage primaryStage;
    private ObservadorDAO dao;
    private AutenticacionController authController;
    private UsuarioController usuarioController;
    private ObservacionController observacionController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        instance = this;
        this.primaryStage = primaryStage;
        primaryStage.setTitle("EduObservador 2.0");

        // Inicializar persistencia
        dao = new ObservadorDAOSQLite();
        try {
            dao.inicializarBaseDatos();
            // Opcional: cargar datos de prueba si está vacía
        } catch (DataAccessException e) {
            e.printStackTrace();
            mostrarError("Error al inicializar la base de datos: " + e.getMessage());
        }

        // Inicializar controladores
        authController = new AutenticacionController(dao);
        usuarioController = new UsuarioController(dao);
        observacionController = new ObservacionController(dao);

        // Cargar escena de login
        cargarEscena("/edu/observador/view/login.fxml");
        primaryStage.show();
    }

    /**
     * Cambia la escena actual por un nuevo FXML.
     *
     * @param fxmlPath Ruta al archivo FXML (en resources)
     */
    public void cargarEscena(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("No se pudo cargar la vista: " + fxmlPath);
        }
    }

    private void mostrarError(String mensaje) {
        // Implementar alerta de error
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public static MainApp getInstance() {
        return instance;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public AutenticacionController getAuthController() {
        return authController;
    }

    public UsuarioController getUsuarioController() {
        return usuarioController;
    }

    public ObservacionController getObservacionController() {
        return observacionController;
    }

    public static void main(String[] args) {
        launch(args);
    }
}