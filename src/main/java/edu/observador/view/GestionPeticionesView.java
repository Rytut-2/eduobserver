package edu.observador.view;

import edu.observador.MainApp;
import edu.observador.controller.ObservacionController;
import edu.observador.model.PeticionRevision;
import edu.observador.model.enums.EstadoPeticion;
import edu.observador.view.controllers.Sesion;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GestionPeticionesView extends BorderPane {
    private TableView<PeticionRevision> tabla;
    private ObservacionController obsController;

    public GestionPeticionesView() {
        obsController = new ObservacionController(MainApp.getDAO());
        inicializarUI();
        cargarPeticiones();
    }

    private void inicializarUI() {
        tabla = new TableView<>();
        TableColumn<PeticionRevision, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<PeticionRevision, String> colObservacion = new TableColumn<>("Observación ID");
        colObservacion.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getObservacionImplicada().getId()));
        TableColumn<PeticionRevision, String> colMotivo = new TableColumn<>("Motivo");
        colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivoAplicacion"));
        TableColumn<PeticionRevision, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        TableColumn<PeticionRevision, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnAprobar = new Button("Aprobar");
            private final Button btnRechazar = new Button("Rechazar");
            private final HBox pane = new HBox(5, btnAprobar, btnRechazar);
            {
                btnAprobar.setOnAction(e -> {
                    PeticionRevision p = getTableView().getItems().get(getIndex());
                    try {
                        obsController.aprobarPeticion(p.getId(), Sesion.getUsuarioActual());
                        cargarPeticiones();
                    } catch (Exception ex) { mostrarAlerta("Error", ex.getMessage()); }
                });
                btnRechazar.setOnAction(e -> {
                    PeticionRevision p = getTableView().getItems().get(getIndex());
                    try {
                        obsController.rechazarPeticion(p.getId(), Sesion.getUsuarioActual());
                        cargarPeticiones();
                    } catch (Exception ex) { mostrarAlerta("Error", ex.getMessage()); }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(pane);
            }
        });
        tabla.getColumns().addAll(colId, colObservacion, colMotivo, colEstado, colAcciones);
        setCenter(tabla);
    }

    private void cargarPeticiones() {
        try {
            tabla.setItems(FXCollections.observableArrayList(obsController.listarPeticionesPendientes()));
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR, mensaje);
        alert.setTitle(titulo);
        alert.showAndWait();
    }
}