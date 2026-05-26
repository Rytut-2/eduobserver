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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class ListaEstudiantesView extends BorderPane {

    private final Docente docente;
    private final UsuarioController userController;
    private TableView<Estudiante> tablaEstudiantes;
    private ComboBox<String> cmbFiltroCurso;
    private Label lblTitulo;
    private List<Estudiante> todosEstudiantes;
    private Button btnCerrar;

    public ListaEstudiantesView(Docente docente) {
        this.docente = docente;
        this.userController = new UsuarioController(MainApp.getDAO());
        inicializarUI();
        cargarDatos();
    }

    private void inicializarUI() {
        setPadding(new Insets(10));
        setStyle("-fx-background-color: #f4f7fc;");

        lblTitulo = new Label("Estudiantes a cargo de " + docente.getNombreCompleto());
        lblTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Filtro por curso
        cmbFiltroCurso = new ComboBox<>();
        cmbFiltroCurso.setPromptText("Filtrar por salón");
        cmbFiltroCurso.setOnAction(e -> filtrarEstudiantes());

        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(0, 0, 10, 0));
        topBar.getChildren().addAll(cmbFiltroCurso);

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

        // Botón cerrar
        btnCerrar = new Button("Cerrar");
        btnCerrar.setOnAction(e -> ((Stage) getScene().getWindow()).close());
        HBox bottomBar = new HBox(10);
        bottomBar.setPadding(new Insets(10, 0, 0, 0));
        bottomBar.getChildren().add(btnCerrar);

        VBox center = new VBox(10, lblTitulo, topBar, tablaEstudiantes, bottomBar);
        setCenter(center);
    }

    private void cargarDatos() {
        try {
            List<String> cursosDocente = docente.getCursosAsignados();
            if (cursosDocente == null || cursosDocente.isEmpty()) {
                mostrarAlerta("Información", "No tiene cursos asignados.", Alert.AlertType.INFORMATION);
                tablaEstudiantes.setItems(FXCollections.observableArrayList());
                cmbFiltroCurso.setDisable(true);
                return;
            }

            // Cargar estudiantes de todos los cursos del docente
            todosEstudiantes = new ArrayList<>();
            for (String curso : cursosDocente) {
                List<Estudiante> estudiantesCurso = userController.listarEstudiantesPorGrado(curso);
                todosEstudiantes.addAll(estudiantesCurso);
            }
            // Eliminar duplicados (por si un estudiante aparece en varios cursos del mismo docente)
            todosEstudiantes = todosEstudiantes.stream().distinct().toList();

            // Configurar combo de filtro con los cursos únicos
            cmbFiltroCurso.getItems().clear();
            cmbFiltroCurso.getItems().add("Todos");
            cmbFiltroCurso.getItems().addAll(cursosDocente);
            cmbFiltroCurso.setValue("Todos");

            filtrarEstudiantes();
        } catch (DataAccessException e) {
            mostrarAlerta("Error", "No se pudieron cargar los estudiantes: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void filtrarEstudiantes() {
        String filtro = cmbFiltroCurso.getValue();
        if (filtro == null || filtro.equals("Todos")) {
            tablaEstudiantes.setItems(FXCollections.observableArrayList(todosEstudiantes));
        } else {
            List<Estudiante> filtrados = todosEstudiantes.stream()
                    .filter(e -> e.getGrado().equals(filtro))
                    .toList();
            tablaEstudiantes.setItems(FXCollections.observableArrayList(filtrados));
        }
        if (tablaEstudiantes.getItems().isEmpty()) {
            tablaEstudiantes.setPlaceholder(new Label("No hay estudiantes en este curso."));
        }
    }

    private void verHistorial(Estudiante estudiante) {
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Historial de " + estudiante.getNombreCompleto());
        HistorialView historial = new HistorialView(estudiante);
        stage.setScene(new Scene(historial, 1000, 700));
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