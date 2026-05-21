// Archivo: src/edu/observador/view/HistorialView.java
package edu.observador.view;

import edu.observador.MainApp;
import edu.observador.controller.ObservacionController;
import edu.observador.data.DataAccessException;
import edu.observador.model.*;
import edu.observador.view.controllers.Sesion;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Vista para mostrar el historial de observaciones de un estudiante.
 * Permite filtrar por tipo (académicas/disciplinarias/todas) y,
 * si el usuario logueado es coordinador, anular observaciones.
 */
public class HistorialView extends BorderPane {

    private final Estudiante estudiante;
    private final Usuario usuarioActual;
    private final ObservacionController obsController;

    private TableView<Observacion> tabla;
    private ComboBox<String> cmbFiltro;
    private Label lblTitulo;
    private Button btnAnular;

    public HistorialView(Estudiante estudiante) {
        this.estudiante = estudiante;
        this.usuarioActual = Sesion.getUsuarioActual();
        this.obsController = new ObservacionController(MainApp.getDAO());

        inicializarUI();
        cargarHistorial();
    }

    private void inicializarUI() {
        setPadding(new Insets(10));
        setStyle("-fx-background-color: #f4f7fc;");

        lblTitulo = new Label("Historial de " + estudiante.getNombreCompleto());
        lblTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Filtro por tipo
        cmbFiltro = new ComboBox<>();
        cmbFiltro.getItems().addAll("Todas", "Académicas", "Disciplinarias");
        cmbFiltro.setValue("Todas");
        cmbFiltro.setOnAction(e -> cargarHistorial());

        // Botón anular (solo coordinador)
        btnAnular = new Button("Anular Observación");
        btnAnular.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        btnAnular.setVisible(usuarioActual instanceof Coordinador);
        btnAnular.setOnAction(e -> anularObservacion());

        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(0, 0, 10, 0));
        topBar.getChildren().addAll(cmbFiltro, btnAnular);

        // Tabla
        tabla = new TableView<>();
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Observacion, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));

        TableColumn<Observacion, String> colDescripcion = new TableColumn<>("Descripción");
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        TableColumn<Observacion, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTipoObservacion()));

        TableColumn<Observacion, String> colCreador = new TableColumn<>("Creado por");
        colCreador.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCreador().getNombreCompleto()));

        TableColumn<Observacion, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().isAnulada() ? "Anulada" : "Activa"));

        TableColumn<Observacion, String> colJustificacion = new TableColumn<>("Justificación Anulación");
        colJustificacion.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getJustificacionAnulacion() != null ?
                                cellData.getValue().getJustificacionAnulacion() : "-"
                ));

        tabla.getColumns().addAll(colFecha, colDescripcion, colTipo, colCreador, colEstado, colJustificacion);

        VBox center = new VBox(10, lblTitulo, topBar, tabla);
        setCenter(center);
    }

    private void cargarHistorial() {
        try {
            List<Observacion> todas = obsController.getHistorialEstudiante(estudiante.getId(), false);
            String filtro = cmbFiltro.getValue();
            List<Observacion> filtradas;
            if ("Académicas".equals(filtro)) {
                filtradas = todas.stream().filter(o -> o instanceof ObservacionAcademica).toList();
            } else if ("Disciplinarias".equals(filtro)) {
                filtradas = todas.stream().filter(o -> o instanceof ObservacionDisciplinaria).toList();
            } else {
                filtradas = todas;
            }
            tabla.setItems(FXCollections.observableArrayList(filtradas));
        } catch (DataAccessException e) {
            mostrarAlerta("Error", "No se pudo cargar el historial: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void anularObservacion() {
        Observacion obs = tabla.getSelectionModel().getSelectedItem();
        if (obs == null) {
            mostrarAlerta("Selección requerida", "Seleccione una observación para anular.", Alert.AlertType.WARNING);
            return;
        }
        if (obs.isAnulada()) {
            mostrarAlerta("Ya anulada", "Esta observación ya fue anulada.", Alert.AlertType.WARNING);
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Anular Observación");
        dialog.setHeaderText("Motivo de anulación");
        dialog.setContentText("Justificación:");
        dialog.showAndWait().ifPresent(justificacion -> {
            if (justificacion == null || justificacion.trim().isEmpty()) {
                mostrarAlerta("Justificación requerida", "Debe ingresar una justificación.", Alert.AlertType.ERROR);
                return;
            }
            try {
                obsController.anularObservacion(obs.getId(), justificacion, usuarioActual);
                mostrarAlerta("Anulación exitosa", "La observación ha sido anulada.", Alert.AlertType.INFORMATION);
                cargarHistorial(); // refrescar
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo anular: " + e.getMessage(), Alert.AlertType.ERROR);
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