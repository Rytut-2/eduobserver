package edu.observador.view;

import edu.observador.MainApp;
import edu.observador.controller.ObservacionController;
import edu.observador.model.Observacion;
import edu.observador.view.controllers.Sesion;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SolicitarRevisionView extends VBox {
    private ComboBox<Observacion> cmbObservacion;
    private TextArea txtMotivo;
    private Button btnEnviar;
    private ObservacionController obsController;

    public SolicitarRevisionView() {
        obsController = new ObservacionController(MainApp.getDAO());
        inicializar();
    }

    private void inicializar() {
        setSpacing(10);
        setPadding(new Insets(20));
        cmbObservacion = new ComboBox<>();
        cargarObservaciones();
        cmbObservacion.setPromptText("Seleccione observación a impugnar");
        txtMotivo = new TextArea();
        txtMotivo.setPromptText("Motivo de la solicitud de revisión");
        txtMotivo.setPrefRowCount(3);
        btnEnviar = new Button("Enviar Solicitud");
        btnEnviar.setOnAction(e -> enviar());

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
        // Cargar observaciones del estudiante que sean activas (no anuladas)
        // Aquí necesitaríamos un método en ObservacionController para obtener observaciones del estudiante actual (representante)
        // Por simplicidad, se puede obtener todas las observaciones del estudiante (de su grado)
        // Implementación pendiente.
    }

    private void enviar() {
        // Validar y llamar a obsController.crearPeticionRevision
    }
}