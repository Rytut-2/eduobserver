// Archivo: src/edu/observador/view/HistorialView.java
package edu.observador.view;

import edu.observador.MainApp;
import edu.observador.controller.ObservacionController;
import edu.observador.data.DataAccessException;
import edu.observador.model.*;
import edu.observador.view.controllers.Sesion;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class HistorialView extends BorderPane {

    private final Estudiante estudiante;
    private final Usuario usuarioActual;
    private final ObservacionController obsController;

    private TableView<Observacion> tabla;
    private ComboBox<String> cmbFiltro;
    private Label lblTitulo;
    private Button btnAnular;
    private Button btnCerrar;

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

        // Botón cerrar
        btnCerrar = new Button("Cerrar");
        btnCerrar.setOnAction(e -> ((Stage) getScene().getWindow()).close());
        HBox bottomBar = new HBox(10);
        bottomBar.setAlignment(Pos.CENTER_RIGHT);
        bottomBar.setPadding(new Insets(10, 0, 0, 0));
        bottomBar.getChildren().add(btnCerrar);

        VBox center = new VBox(10, lblTitulo, topBar, tabla, bottomBar);
        setCenter(center);
    }

    private void cargarHistorial() {
        try {
            // Obtener todas las observaciones del estudiante (incluye anuladas)
            List<Observacion> todas = obsController.getHistorialEstudiante(estudiante.getId(), false);

            // Filtrar según permisos del usuario logueado
            List<Observacion> filtradasPorPermiso = aplicarFiltroPermisos(todas);

            // Aplicar filtro adicional por tipo (académicas/disciplinarias)
            String filtroTipo = cmbFiltro.getValue();
            if ("Académicas".equals(filtroTipo)) {
                filtradasPorPermiso = filtradasPorPermiso.stream()
                        .filter(o -> o instanceof ObservacionAcademica)
                        .collect(Collectors.toList());
            } else if ("Disciplinarias".equals(filtroTipo)) {
                filtradasPorPermiso = filtradasPorPermiso.stream()
                        .filter(o -> o instanceof ObservacionDisciplinaria)
                        .collect(Collectors.toList());
            }

            tabla.setItems(FXCollections.observableArrayList(filtradasPorPermiso));
            if (filtradasPorPermiso.isEmpty()) {
                tabla.setPlaceholder(new Label("No hay observaciones para mostrar con los filtros actuales."));
            }
        } catch (DataAccessException e) {
            mostrarAlerta("Error", "No se pudo cargar el historial: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Aplica las reglas de visibilidad según el rol del usuario logueado.
     * - Coordinador: ve todas las observaciones.
     * - Estudiante: ve todas sus observaciones (ya filtradas por estudiante).
     * - Docente regular: solo observaciones que él mismo creó.
     * - Docente de grupo: ve todas las observaciones de los estudiantes de su curso a cargo.
     */
    private List<Observacion> aplicarFiltroPermisos(List<Observacion> todas) {
        if (usuarioActual instanceof Coordinador) {
            return todas;
        }
        if (usuarioActual instanceof Estudiante) {
            // El estudiante ya solo ve sus propias observaciones porque cargamos por estudianteId
            return todas;
        }
        if (usuarioActual instanceof Docente) {
            Docente docente = (Docente) usuarioActual;
            if (docente.isEsDocenteDeGrupo()) {
                // Docente de grupo: verifica si el estudiante pertenece a su curso a cargo
                String cursoDir = docente.getCursoDireccionGrupo();
                if (cursoDir != null && estudiante.getGrado().equals(cursoDir)) {
                    return todas; // Ve todo el historial del estudiante
                } else {
                    // El estudiante no es de su grupo: no ve nada (o solo sus propias observaciones? por seguridad, ninguna)
                    return List.of();
                }
            } else {
                // Docente regular: solo observaciones creadas por él
                return todas.stream()
                        .filter(obs -> obs.getCreador().getId().equals(docente.getId()))
                        .collect(Collectors.toList());
            }
        }
        return List.of();
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