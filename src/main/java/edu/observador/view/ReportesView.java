// Archivo: src/edu/observador/view/ReportesView.java
package edu.observador.view;

import edu.observador.MainApp;
import edu.observador.controller.ObservacionController;
import edu.observador.controller.UsuarioController;
import edu.observador.data.DataAccessException;
import edu.observador.model.Estudiante;
import edu.observador.view.controllers.Sesion;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class ReportesView extends BorderPane {

    private final ObservacionController obsController;
    private final UsuarioController userController;
    private TextArea txtReporte;
    private Button btnRefrescar, btnExportar, btnCerrar;

    public ReportesView() {
        this.obsController = new ObservacionController(MainApp.getDAO());
        this.userController = new UsuarioController(MainApp.getDAO());
        inicializarUI();
        cargarReporte();
    }

    private void inicializarUI() {
        setPadding(new Insets(10));
        setStyle("-fx-background-color: #f4f7fc;");

        // Barra superior con botones
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(0, 0, 10, 0));
        btnRefrescar = new Button("Refrescar");
        btnRefrescar.setOnAction(e -> cargarReporte());
        btnExportar = new Button("Exportar (simulado)");
        btnExportar.setOnAction(e -> exportarReporte());
        topBar.getChildren().addAll(btnRefrescar, btnExportar);

        // Área de texto para el reporte
        txtReporte = new TextArea();
        txtReporte.setEditable(false);
        txtReporte.setWrapText(true);
        txtReporte.setStyle("-fx-font-family: monospace; -fx-font-size: 12px;");

        // Botón cerrar en la parte inferior
        btnCerrar = new Button("Cerrar");
        btnCerrar.setOnAction(e -> ((Stage) getScene().getWindow()).close());
        HBox bottomBar = new HBox(10);
        bottomBar.setAlignment(Pos.CENTER_RIGHT);
        bottomBar.setPadding(new Insets(10, 0, 0, 0));
        bottomBar.getChildren().add(btnCerrar);

        VBox center = new VBox(10, topBar, txtReporte, bottomBar);
        setCenter(center);
    }

    private void cargarReporte() {
        try {
            List<Estudiante> estudiantes = userController.listarEstudiantes();
            // Cargar historial de cada estudiante para el reporte
            for (Estudiante e : estudiantes) {
                var historial = obsController.getHistorialEstudiante(e.getId(), false);
                for (var o : historial) {
                    e.agregarObservacion(o); // poblar historial en memoria
                }
            }
            String reporte = obsController.generarReporteGeneral(Sesion.getUsuarioActual());
            txtReporte.setText(reporte);
        } catch (DataAccessException e) {
            txtReporte.setText("Error al cargar el reporte: " + e.getMessage());
        } catch (SecurityException e) {
            txtReporte.setText("Acceso denegado: " + e.getMessage());
        }
    }

    private void exportarReporte() {
        // Simulación de exportación (podría guardar en archivo)
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Exportar Reporte");
        alert.setHeaderText(null);
        alert.setContentText("Función de exportación no implementada aún.\nEl texto del reporte puede copiarse manualmente.");
        alert.showAndWait();
    }
}