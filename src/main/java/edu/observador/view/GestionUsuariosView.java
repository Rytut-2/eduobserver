// Archivo: src/edu/observador/view/GestionUsuariosView.java
package edu.observador.view;

import edu.observador.MainApp;
import edu.observador.controller.UsuarioController;
import edu.observador.data.DataAccessException;
import edu.observador.model.*;
import edu.observador.model.enums.RolUsuario;
import edu.observador.view.controllers.Sesion;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Vista para gestionar usuarios (solo coordinador).
 * Permite listar, crear, editar, deshabilitar y asignar roles especiales.
 */
public class GestionUsuariosView extends BorderPane {

    private final UsuarioController userController;
    private TableView<Usuario> tablaUsuarios;
    private ComboBox<String> cmbRolFiltro;

    public GestionUsuariosView() {
        this.userController = new UsuarioController(MainApp.getDAO());
        inicializarUI();
        cargarUsuarios();
    }

    private void inicializarUI() {
        setPadding(new Insets(10));
        setStyle("-fx-background-color: #f4f7fc;");

        // Barra superior
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(0, 0, 10, 0));
        Button btnCrear = new Button("+ Nuevo Usuario");
        btnCrear.setOnAction(e -> mostrarFormularioUsuario(null));
        cmbRolFiltro = new ComboBox<>();
        cmbRolFiltro.getItems().addAll("Todos", "ESTUDIANTE", "DOCENTE", "COORDINADOR");
        cmbRolFiltro.setValue("Todos");
        cmbRolFiltro.setOnAction(e -> cargarUsuarios());
        topBar.getChildren().addAll(btnCrear, cmbRolFiltro);

        // Tabla
        tablaUsuarios = new TableView<>();
        tablaUsuarios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Usuario, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Usuario, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombreCompleto()));

        TableColumn<Usuario, RolUsuario> colRol = new TableColumn<>("Rol");
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));

        TableColumn<Usuario, Boolean> colActivo = new TableColumn<>("Activo");
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        TableColumn<Usuario, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnDeshabilitar = new Button("Deshabilitar");
            private final HBox pane = new HBox(5, btnEditar, btnDeshabilitar);

            {
                btnEditar.setOnAction(e -> {
                    Usuario u = getTableView().getItems().get(getIndex());
                    mostrarFormularioUsuario(u);
                });
                btnDeshabilitar.setOnAction(e -> {
                    Usuario u = getTableView().getItems().get(getIndex());
                    alternarEstadoActivo(u);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(pane);
            }
        });

        tablaUsuarios.getColumns().addAll(colId, colNombre, colRol, colActivo, colAcciones);

        VBox center = new VBox(10, topBar, tablaUsuarios);
        setCenter(center);
    }

    private void cargarUsuarios() {
        try {
            String filtro = cmbRolFiltro.getValue();
            if ("Todos".equals(filtro)) {
                List<Usuario> todos = new ArrayList<>();
                todos.addAll(userController.listarUsuariosPorRol(RolUsuario.ESTUDIANTE));
                todos.addAll(userController.listarUsuariosPorRol(RolUsuario.DOCENTE));
                todos.addAll(userController.listarUsuariosPorRol(RolUsuario.COORDINADOR));
                tablaUsuarios.setItems(FXCollections.observableArrayList(todos));
            } else {
                RolUsuario rol = RolUsuario.valueOf(filtro);
                tablaUsuarios.setItems(FXCollections.observableArrayList(userController.listarUsuariosPorRol(rol)));
            }
        } catch (DataAccessException e) {
            mostrarAlerta("Error", "No se pudo cargar usuarios: " + e.getMessage());
        }
    }

    private void mostrarFormularioUsuario(Usuario usuario) {
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle(usuario == null ? "Nuevo Usuario" : "Editar Usuario");

        FormularioUsuarioView formulario = new FormularioUsuarioView(usuario, () -> {
            cargarUsuarios();
            stage.close();
        });
        stage.setScene(new Scene(formulario, 500, 500));
        stage.initOwner(getScene().getWindow());
        stage.show();
    }

    private void alternarEstadoActivo(Usuario usuario) {
        boolean nuevoEstado = !usuario.isActivo();
        try {
            userController.modificarEstadoActivo(usuario.getId(), nuevoEstado);
            cargarUsuarios();
            String estado = nuevoEstado ? "activada" : "deshabilitada";
            mostrarAlerta("Cuenta " + estado, "La cuenta de " + usuario.getNombreCompleto() + " ha sido " + estado + ".", Alert.AlertType.INFORMATION);
        } catch (DataAccessException e) {
            mostrarAlerta("Error", "No se pudo cambiar el estado: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        mostrarAlerta(titulo, mensaje, Alert.AlertType.ERROR);
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo, mensaje);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}