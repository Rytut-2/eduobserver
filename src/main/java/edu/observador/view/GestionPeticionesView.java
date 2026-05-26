// Archivo: src/edu/observador/view/GestionPeticionesView.java
package edu.observador.view;

import edu.observador.MainApp;
import edu.observador.controller.ObservacionController;
import edu.observador.data.DataAccessException;
import edu.observador.model.PeticionRevision;
import edu.observador.model.enums.EstadoPeticion;
import edu.observador.view.controllers.Sesion;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class GestionPeticionesView extends BorderPane {

    private final ObservacionController obsController;
    private TableView<PeticionRevision> tabla;
    private Button btnRefrescar;

    public GestionPeticionesView() {
        this.obsController = new ObservacionController(MainApp.getDAO());
        inicializarUI();
        cargarPeticiones();
    }

    private void inicializarUI() {
        setPadding(new Insets(10));
        setStyle("-fx-background-color: #f4f7fc;");

        // Barra superior con botón refrescar
        btnRefrescar = new Button("Refrescar");
        btnRefrescar.setOnAction(e -> cargarPeticiones());
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(0, 0, 10, 0));
        topBar.getChildren().add(btnRefrescar);

        // Tabla
        tabla = new TableView<>();
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<PeticionRevision, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<PeticionRevision, String> colObservacion = new TableColumn<>("Observación ID");
        colObservacion.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getObservacionImplicada().getId()));

        TableColumn<PeticionRevision, String> colMotivo = new TableColumn<>("Motivo");
        colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivoAplicacion"));

        TableColumn<PeticionRevision, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaPeticion"));

        TableColumn<PeticionRevision, EstadoPeticion> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        TableColumn<PeticionRevision, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnAprobar = new Button("Aprobar");
            private final Button btnRechazar = new Button("Rechazar");
            private final HBox pane = new HBox(5, btnAprobar, btnRechazar);

            {
                btnAprobar.setOnAction(e -> {
                    PeticionRevision p = getTableView().getItems().get(getIndex());
                    if (p.getEstado() != EstadoPeticion.PENDIENTE) {
                        mostrarAlerta("Error", "Solo se pueden gestionar peticiones pendientes.", Alert.AlertType.ERROR);
                        return;
                    }
                    try {
                        obsController.aprobarPeticion(p.getId(), Sesion.getUsuarioActual());
                        cargarPeticiones();
                        mostrarAlerta("Éxito", "Petición aprobada.", Alert.AlertType.INFORMATION);
                    } catch (Exception ex) {
                        mostrarAlerta("Error", ex.getMessage(), Alert.AlertType.ERROR);
                    }
                });
                btnRechazar.setOnAction(e -> {
                    PeticionRevision p = getTableView().getItems().get(getIndex());
                    if (p.getEstado() != EstadoPeticion.PENDIENTE) {
                        mostrarAlerta("Error", "Solo se pueden gestionar peticiones pendientes.", Alert.AlertType.ERROR);
                        return;
                    }
                    try {
                        obsController.rechazarPeticion(p.getId(), Sesion.getUsuarioActual());
                        cargarPeticiones();
                        mostrarAlerta("Éxito", "Petición rechazada.", Alert.AlertType.INFORMATION);
                    } catch (Exception ex) {
                        mostrarAlerta("Error", ex.getMessage(), Alert.AlertType.ERROR);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(pane);
            }
        });

        tabla.getColumns().addAll(colId, colObservacion, colMotivo, colFecha, colEstado, colAcciones);

        VBox center = new VBox(10, topBar, tabla);
        setCenter(center);
    }

    private void cargarPeticiones() {
        try {
            List<PeticionRevision> peticiones = obsController.listarPeticionesPendientes();
            tabla.setItems(FXCollections.observableArrayList(peticiones));
            if (peticiones.isEmpty()) {
                // Opcional: mostrar mensaje en la tabla vacía
                tabla.setPlaceholder(new Label("No hay peticiones pendientes."));
            }
        } catch (DataAccessException e) {
            mostrarAlerta("Error", "No se pudieron cargar las peticiones: " + e.getMessage(), Alert.AlertType.ERROR);
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