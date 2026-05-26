// Archivo: src/edu/observador/view/RegistroObservacionView.java
package edu.observador.view;

import edu.observador.MainApp;
import edu.observador.controller.ObservacionController;
import edu.observador.controller.UsuarioController;
import edu.observador.model.Estudiante;
import edu.observador.model.enums.NivelSeveridad;
import edu.observador.model.enums.TipoAcademia;
import edu.observador.view.controllers.Sesion;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegistroObservacionView extends VBox {

    private ComboBox<Estudiante> cmbEstudiante;
    private TextArea txtDescripcion;
    private RadioButton rbAcademica;
    private RadioButton rbDisciplinaria;
    private ComboBox<TipoAcademia> cmbTipo;
    private TextField txtDetalle;
    private ComboBox<NivelSeveridad> cmbSeveridad;
    private Button btnGuardar;

    private ObservacionController obsController;
    private UsuarioController userController;

    public RegistroObservacionView() {
        obsController = new ObservacionController(MainApp.getDAO());
        userController = new UsuarioController(MainApp.getDAO());
        inicializar();
    }

    private void inicializar() {
        setSpacing(10);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: white;");

        // Estudiante con visualización personalizada
        cmbEstudiante = new ComboBox<>();
        cargarEstudiantes();
        cmbEstudiante.setPromptText("Seleccione estudiante");
        cmbEstudiante.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Estudiante est, boolean empty) {
                super.updateItem(est, empty);
                if (empty || est == null) setText(null);
                else setText(est.getNombreCompleto() + " - " + est.getGrado());
            }
        });
        cmbEstudiante.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Estudiante est, boolean empty) {
                super.updateItem(est, empty);
                if (empty || est == null) setText(null);
                else setText(est.getNombreCompleto() + " - " + est.getGrado());
            }
        });

        // Descripción
        txtDescripcion = new TextArea();
        txtDescripcion.setPromptText("Descripción de la observación");
        txtDescripcion.setPrefRowCount(4);
        txtDescripcion.setWrapText(true);

        // Tipo de observación
        rbAcademica = new RadioButton("Académica");
        rbDisciplinaria = new RadioButton("Disciplinaria");
        ToggleGroup grupo = new ToggleGroup();
        rbAcademica.setToggleGroup(grupo);
        rbDisciplinaria.setToggleGroup(grupo);
        rbAcademica.setSelected(true);

        // Campos académicos
        cmbTipo = new ComboBox<>();
        cmbTipo.getItems().addAll(TipoAcademia.values());
        cmbTipo.setPromptText("Tipo de logro");
        txtDetalle = new TextField();
        txtDetalle.setPromptText("Detalle académico");

        // Campo disciplinario
        cmbSeveridad = new ComboBox<>();
        cmbSeveridad.getItems().addAll(NivelSeveridad.values());
        cmbSeveridad.setPromptText("Severidad");

        btnGuardar = new Button("Guardar Observación");
        btnGuardar.setOnAction(e -> guardar());

        // Lógica de habilitación de campos según tipo
        rbAcademica.selectedProperty().addListener((obs, old, val) -> {
            cmbTipo.setDisable(!val);
            txtDetalle.setDisable(!val);
            cmbSeveridad.setDisable(val);
        });
        rbDisciplinaria.selectedProperty().addListener((obs, old, val) -> {
            cmbSeveridad.setDisable(!val);
            cmbTipo.setDisable(val);
            txtDetalle.setDisable(val);
        });
        cmbSeveridad.setDisable(true); // inicial

        // Organizar en GridPane con columnas mejor distribuidas
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        // Fila 0: Estudiante
        grid.add(new Label("Estudiante:"), 0, 0);
        grid.add(cmbEstudiante, 1, 0);
        GridPane.setColumnSpan(cmbEstudiante, 2);

        // Fila 1: Descripción
        grid.add(new Label("Descripción:"), 0, 1);
        grid.add(txtDescripcion, 1, 1);
        GridPane.setColumnSpan(txtDescripcion, 2);

        // Fila 2: Tipo
        grid.add(new Label("Tipo:"), 0, 2);
        grid.add(rbAcademica, 1, 2);
        grid.add(rbDisciplinaria, 2, 2);

        // Fila 3: Detalle académico (label)
        grid.add(new Label("Detalle académico:"), 0, 3);
        grid.add(txtDetalle, 1, 3);
        GridPane.setColumnSpan(txtDetalle, 2);

        // Fila 4: Tipo academia
        grid.add(new Label("Tipo academia:"), 0, 4);
        grid.add(cmbTipo, 1, 4);
        GridPane.setColumnSpan(cmbTipo, 2);

        // Fila 5: Severidad
        grid.add(new Label("Severidad:"), 0, 5);
        grid.add(cmbSeveridad, 1, 5);
        GridPane.setColumnSpan(cmbSeveridad, 2);

        // Fila 6: Botón
        grid.add(btnGuardar, 1, 6);

        // Ancho de columnas
        grid.getColumnConstraints().add(new javafx.scene.layout.ColumnConstraints(100)); // etiquetas
        grid.getColumnConstraints().add(new javafx.scene.layout.ColumnConstraints(200)); // campo
        grid.getColumnConstraints().add(new javafx.scene.layout.ColumnConstraints(100)); // opcional

        getChildren().add(grid);
    }

    private void cargarEstudiantes() {
        try {
            cmbEstudiante.getItems().setAll(userController.listarEstudiantes());
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron cargar los estudiantes: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void guardar() {
        Estudiante est = cmbEstudiante.getValue();
        if (est == null) {
            mostrarAlerta("Error", "Debe seleccionar un estudiante.", Alert.AlertType.ERROR);
            return;
        }
        String desc = txtDescripcion.getText();
        if (desc == null || desc.trim().isEmpty()) {
            mostrarAlerta("Error", "La descripción no puede estar vacía.", Alert.AlertType.ERROR);
            return;
        }

        try {
            if (rbAcademica.isSelected()) {
                TipoAcademia tipo = cmbTipo.getValue();
                String detalle = txtDetalle.getText();
                if (tipo == null || detalle == null || detalle.trim().isEmpty()) {
                    mostrarAlerta("Error", "Debe completar el tipo de academia y el detalle académico.", Alert.AlertType.ERROR);
                    return;
                }
                obsController.registrarAcademica(est.getId(), Sesion.getUsuarioActual().getId(), desc, tipo, detalle);
                mostrarAlerta("Éxito", "Observación académica registrada correctamente.", Alert.AlertType.INFORMATION);
            } else {
                NivelSeveridad sev = cmbSeveridad.getValue();
                if (sev == null) {
                    mostrarAlerta("Error", "Debe seleccionar la severidad.", Alert.AlertType.ERROR);
                    return;
                }
                obsController.registrarDisciplinaria(est.getId(), Sesion.getUsuarioActual().getId(), desc, sev);
                mostrarAlerta("Éxito", "Observación disciplinaria registrada correctamente.", Alert.AlertType.INFORMATION);
            }
            ((Stage) getScene().getWindow()).close();
        } catch (Exception ex) {
            mostrarAlerta("Error", "No se pudo registrar la observación: " + ex.getMessage(), Alert.AlertType.ERROR);
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