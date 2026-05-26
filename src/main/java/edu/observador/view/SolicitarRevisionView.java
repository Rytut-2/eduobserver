// Archivo: src/edu/observador/view/SolicitarRevisionView.java
package edu.observador.view;

import edu.observador.MainApp;
import edu.observador.controller.ObservacionController;
import edu.observador.controller.UsuarioController;
import edu.observador.data.DataAccessException;
import edu.observador.model.Estudiante;
import edu.observador.model.Observacion;
import edu.observador.view.controllers.Sesion;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class SolicitarRevisionView extends VBox {

    private ComboBox<ObservacionItem> cmbObservacion;
    private TextArea txtMotivo;
    private Button btnEnviar;
    private ObservacionController obsController;
    private UsuarioController userController;
    private Estudiante representante;

    // Clase interna para mostrar observación con información legible
    private static class ObservacionItem {
        private final Observacion observacion;
        private final String displayText;

        public ObservacionItem(Observacion observacion, String estudianteNombre) {
            this.observacion = observacion;
            this.displayText = estudianteNombre + " - " + observacion.getDescripcion().substring(0, Math.min(observacion.getDescripcion().length(), 50));
        }

        public Observacion getObservacion() { return observacion; }

        @Override
        public String toString() { return displayText; }
    }

    public SolicitarRevisionView() {
        this.obsController = new ObservacionController(MainApp.getDAO());
        this.userController = new UsuarioController(MainApp.getDAO());
        this.representante = (Estudiante) Sesion.getUsuarioActual();
        inicializar();
        cargarObservaciones();
    }

    private void inicializar() {
        setSpacing(10);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: white;");

        cmbObservacion = new ComboBox<>();
        cmbObservacion.setPromptText("Seleccione la observación a impugnar");
        cmbObservacion.setPrefWidth(400);

        txtMotivo = new TextArea();
        txtMotivo.setPromptText("Explique detalladamente el motivo de la solicitud de revisión");
        txtMotivo.setPrefRowCount(4);
        txtMotivo.setWrapText(true);

        btnEnviar = new Button("Enviar Solicitud");
        btnEnviar.setOnAction(e -> enviarSolicitud());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Observación:"), 0, 0);
        grid.add(cmbObservacion, 1, 0);
        grid.add(new Label("Motivo:"), 0, 1);
        grid.add(txtMotivo, 1, 1);
        grid.add(btnEnviar, 1, 2);

        getChildren().add(grid);
    }

    private void cargarObservaciones() {
        try {
            String grado = representante.getGrado();
            if (grado == null || grado.isEmpty()) {
                mostrarAlerta("Error", "El representante no tiene un grado asignado.", Alert.AlertType.ERROR);
                return;
            }

            List<Estudiante> estudiantes = userController.listarEstudiantesPorGrado(grado);
            List<ObservacionItem> items = new ArrayList<>();

            for (Estudiante e : estudiantes) {
                // Obtener observaciones activas (no anuladas) del estudiante
                List<Observacion> observaciones = obsController.getHistorialEstudiante(e.getId(), true);
                for (Observacion obs : observaciones) {
                    items.add(new ObservacionItem(obs, e.getNombreCompleto()));
                }
            }

            if (items.isEmpty()) {
                cmbObservacion.setDisable(true);
                btnEnviar.setDisable(true);
                mostrarAlerta("Sin observaciones", "No hay observaciones activas en su grado para solicitar revisión.", Alert.AlertType.WARNING);
            } else {
                cmbObservacion.setItems(FXCollections.observableArrayList(items));
            }
        } catch (DataAccessException e) {
            mostrarAlerta("Error", "No se pudieron cargar las observaciones: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void enviarSolicitud() {
        ObservacionItem selected = cmbObservacion.getValue();
        if (selected == null) {
            mostrarAlerta("Selección requerida", "Debe seleccionar una observación.", Alert.AlertType.WARNING);
            return;
        }
        String motivo = txtMotivo.getText();
        if (motivo == null || motivo.trim().isEmpty()) {
            mostrarAlerta("Motivo requerido", "Debe ingresar un motivo para la revisión.", Alert.AlertType.WARNING);
            return;
        }

        try {
            obsController.crearPeticionRevision(selected.getObservacion().getId(), motivo, representante);
            mostrarAlerta("Solicitud enviada", "Su solicitud de revisión ha sido registrada. El coordinador la evaluará.", Alert.AlertType.INFORMATION);
            ((Stage) getScene().getWindow()).close();
        } catch (Exception ex) {
            mostrarAlerta("Error", "No se pudo enviar la solicitud: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}