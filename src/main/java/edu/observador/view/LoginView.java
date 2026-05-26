// Archivo: src/edu/observador/view/LoginView.java
package edu.observador.view;

import edu.observador.MainApp;
import edu.observador.controller.AutenticacionController;
import edu.observador.data.DataAccessException;
import edu.observador.model.Usuario;
import edu.observador.view.controllers.Sesion;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginView extends VBox {

    private TextField txtId;
    private PasswordField txtContrasenia;
    private Button btnIngresar;
    private AutenticacionController authController;

    public LoginView() {
        authController = new AutenticacionController(MainApp.getDAO());
        inicializar();
    }

    private void inicializar() {
        setAlignment(Pos.CENTER);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #f4f7fc;");

        Label titulo = new Label("EduObservador");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 28));
        titulo.setStyle("-fx-text-fill: #2c3e50;");

        Label subtitulo = new Label("Inicio de Sesión");
        subtitulo.setFont(Font.font("System", 16));
        subtitulo.setStyle("-fx-text-fill: #7f8c8d;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        Label lblId = new Label("ID:");
        txtId = new TextField();
        txtId.setPromptText("Documento o código");

        Label lblPass = new Label("Contraseña:");
        txtContrasenia = new PasswordField();
        txtContrasenia.setPromptText("••••••••");

        btnIngresar = new Button("Ingresar");
        btnIngresar.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5;");
        btnIngresar.setOnAction(e -> manejarIngreso());

        grid.add(lblId, 0, 0);
        grid.add(txtId, 1, 0);
        grid.add(lblPass, 0, 1);
        grid.add(txtContrasenia, 1, 1);
        grid.add(btnIngresar, 1, 2);

        getChildren().addAll(titulo, subtitulo, grid);
        setSpacing(15);
    }

    private void manejarIngreso() {
        String id = txtId.getText().trim();
        String pass = txtContrasenia.getText();

        if (id.isEmpty() || pass.isEmpty()) {
            mostrarAlerta("Error", "Debe ingresar ID y contraseña", Alert.AlertType.ERROR);
            return;
        }

        try {
            boolean exito = authController.iniciarSesion(id, pass);
            if (!exito) {
                mostrarAlerta("Credenciales inválidas", "ID o contraseña incorrectos", Alert.AlertType.ERROR);
                return;
            }

            Usuario usuario = authController.obtenerUsuarioLogueado();
            Sesion.setUsuarioActual(usuario);

            if (usuario.esPrimerIngreso()) {
                mostrarDialogoCambioContrasenia(usuario);
            } else {
                MainApp.cargarDashboard();
            }
        } catch (DataAccessException e) {
            mostrarAlerta("Error de base de datos", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void mostrarDialogoCambioContrasenia(Usuario usuario) {
        // Ventana informativa primero
        Alert alertaInfo = new Alert(Alert.AlertType.INFORMATION);
        alertaInfo.setTitle("Cambio obligatorio de contraseña");
        alertaInfo.setHeaderText("Bienvenido, " + usuario.getNombre());
        alertaInfo.setContentText("Es tu primer ingreso. Debes cambiar tu contraseña antes de continuar.\n\n"
                + "La nueva contraseña debe tener al menos 4 caracteres.");
        alertaInfo.initOwner(getScene().getWindow());

        alertaInfo.showAndWait().ifPresent(response -> {
            boolean cambioExitoso = false;
            while (!cambioExitoso) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Nueva contraseña");
                dialog.setHeaderText("Cambio de contraseña obligatorio");
                dialog.setContentText("Ingrese su nueva contraseña:");
                dialog.initOwner(getScene().getWindow());

                String nuevaPass = dialog.showAndWait().orElse(null);
                if (nuevaPass == null) {
                    mostrarAlerta("Cancelado", "Debe cambiar la contraseña para acceder al sistema.", Alert.AlertType.WARNING);
                    return; // Regresa al login
                }
                if (nuevaPass.length() < 4) {
                    mostrarAlerta("Contraseña muy corta", "La contraseña debe tener al menos 4 caracteres.", Alert.AlertType.ERROR);
                    continue; // Repite el diálogo
                }
                try {
                    authController.procesarCambioContraseniaObligatorio(nuevaPass);
                    mostrarAlerta("Éxito", "Contraseña cambiada correctamente. Serás redirigido al dashboard.", Alert.AlertType.INFORMATION);
                    cambioExitoso = true;
                    MainApp.cargarDashboard();
                } catch (Exception e) {
                    mostrarAlerta("Error", "No se pudo cambiar la contraseña: " + e.getMessage(), Alert.AlertType.ERROR);
                    // Se puede reintentar
                }
            }
        });
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}