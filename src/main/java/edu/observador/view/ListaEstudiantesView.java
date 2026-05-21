// Archivo: src/edu/observador/view/ListaEstudiantesView.java
package edu.observador.view;

import edu.observador.MainApp;
import edu.observador.controller.UsuarioController;
import edu.observador.data.DataAccessException;
import edu.observador.model.Docente;
import edu.observador.model.Estudiante;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Vista que muestra la lista de estudiantes asignados a los cursos del docente.
 * Permite ver el historial de observaciones de cada estudiante.
 */
public class ListaEstudiantesView extends BorderPane {

    private final Docente docente;
    private final UsuarioController userController;
    private TableView<Estudiante> tablaEstudiantes;
    private Label lblTitulo;

    public ListaEstudiantesView(Docente docente) {
        this.docente = docente;
        this.userController = new UsuarioController(MainApp.getDAO());
        inicializarUI();
        cargarEstudiantes();
    }

    private void inicializarUI() {
        setPadding(new Insets(10));
        setStyle("-fx-background-color: #f4f7fc;");

        lblTitulo = new Label("Estudiantes a cargo de " + docente.getNombreCompleto());
        lblTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Tabla
        tablaEstudiantes = new TableView<>();
        tablaEstudiantes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Estudiante, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Estudiante, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombreCompleto()));

        TableColumn<Estudiante, String> colGrado = new TableColumn<>("Grado");
        colGrado.setCellValueFactory(new PropertyValueFactory<>("grado"));

        TableColumn<Estudiante, String> colAlerta = new TableColumn<>("Nivel Alerta");
        colAlerta.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().calcularNivelAlerta()));

        TableColumn<Estudiante, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnHistorial = new Button("Ver Historial");

            {
                btnHistorial.setOnAction(e -> {
                    Estudiante est = getTableView().getItems().get(getIndex());
                    verHistorial(est);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(btnHistorial);
            }
        });

        tablaEstudiantes.getColumns().addAll(colId, colNombre, colGrado, colAlerta, colAcciones);

        VBox center = new VBox(10, lblTitulo, tablaEstudiantes);
        setCenter(center);
    }

    private void cargarEstudiantes() {
        try {
            List<String> cursosDocente = docente.getCursosAsignados();
            if (cursosDocente == null || cursosDocente.isEmpty()) {
                mostrarAlerta("Información", "No tiene cursos asignados.", Alert.AlertType.INFORMATION);
                tablaEstudiantes.setItems(FXCollections.observableArrayList());
                return;
            }

            List<Estudiante> todosEstudiantes = new ArrayList<>();
            for (String curso : cursosDocente) {
                List<Estudiante> estudiantesCurso = userController.listarEstudiantesPorGrado(curso);
                todosEstudiantes.addAll(estudiantesCurso);
            }
            // Eliminar duplicados (mismo estudiante puede estar en varios cursos del docente? No debería, pero por si acaso)
            List<Estudiante> unicos = todosEstudiantes.stream().distinct().toList();
            tablaEstudiantes.setItems(FXCollections.observableArrayList(unicos));
        } catch (DataAccessException e) {
            mostrarAlerta("Error", "No se pudieron cargar los estudiantes: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void verHistorial(Estudiante estudiante) {
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Historial de " + estudiante.getNombreCompleto());
        HistorialView historial = new HistorialView(estudiante);
        stage.setScene(new Scene(historial, 900, 600));
        stage.initOwner(getScene().getWindow());
        stage.show();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}