// Archivo: src/edu/observador/view/FormularioUsuarioView.java
package edu.observador.view;

import edu.observador.MainApp;
import edu.observador.controller.UsuarioController;
import edu.observador.model.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FormularioUsuarioView extends VBox {

    private final Usuario usuarioEditar;
    private final Runnable onSuccess;
    private final UsuarioController userController;

    private ComboBox<String> cmbTipo;
    private TextField txtNombre, txtApellido, txtGrado, txtMateria;
    private PasswordField txtContrasenia;
    private CheckBox chkRepresentante, chkDocenteGrupo;
    private Button btnGuardar;
    private Label lblIdGenerado;

    // Nuevos controles para docente
    private ListView<String> listCursosAsignados;
    private ComboBox<String> cmbCursoDir;

    // Lista estática de todos los cursos disponibles (1A a 11D)
    private static final List<String> TODOS_LOS_CURSOS = generarCursos();

    private static List<String> generarCursos() {
        List<String> cursos = new ArrayList<>();
        String[] letras = {"A", "B", "C", "D"};
        for (int grado = 1; grado <= 11; grado++) {
            for (String letra : letras) {
                cursos.add(grado + letra);
            }
        }
        return cursos;
    }

    public FormularioUsuarioView(Usuario usuario, Runnable onSuccess) {
        this.usuarioEditar = usuario;
        this.onSuccess = onSuccess;
        this.userController = new UsuarioController(MainApp.getDAO());
        inicializar();
        cargarDatos();
    }

    private void inicializar() {
        setPadding(new Insets(20));
        setSpacing(10);
        setStyle("-fx-background-color: white;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        int row = 0;

        if (usuarioEditar == null) {
            cmbTipo = new ComboBox<>();
            cmbTipo.getItems().addAll("estudiante", "docente", "coordinador");
            cmbTipo.setValue("estudiante");
            cmbTipo.setOnAction(e -> {
                ajustarCamposPorTipo();
                actualizarIdGenerado();
            });
            grid.add(new Label("Tipo:"), 0, row);
            grid.add(cmbTipo, 1, row++);

            lblIdGenerado = new Label();
            lblIdGenerado.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            grid.add(new Label("ID asignado:"), 0, row);
            grid.add(lblIdGenerado, 1, row++);
            actualizarIdGenerado();
        }

        txtNombre = new TextField();
        grid.add(new Label("Nombre:"), 0, row);
        grid.add(txtNombre, 1, row++);

        txtApellido = new TextField();
        grid.add(new Label("Apellido:"), 0, row);
        grid.add(txtApellido, 1, row++);

        txtContrasenia = new PasswordField();
        grid.add(new Label("Contraseña:"), 0, row);
        grid.add(txtContrasenia, 1, row++);

        // Campos específicos (comunes)
        txtGrado = new TextField();
        txtGrado.setPromptText("Grado (ej. 10A)");
        grid.add(new Label("Grado:"), 0, row);
        grid.add(txtGrado, 1, row++);

        chkRepresentante = new CheckBox("Representante de grupo");
        grid.add(chkRepresentante, 1, row++);

        txtMateria = new TextField();
        txtMateria.setPromptText("Materia que enseña (ej. Matemáticas)");
        grid.add(new Label("Materia:"), 0, row);
        grid.add(txtMateria, 1, row++);

        // Cursos asignados (ListView múltiple)
        listCursosAsignados = new ListView<>();
        listCursosAsignados.getItems().addAll(TODOS_LOS_CURSOS);
        listCursosAsignados.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listCursosAsignados.setPrefHeight(120);
        grid.add(new Label("Cursos asignados:"), 0, row);
        grid.add(listCursosAsignados, 1, row++);

        // Curso a cargo (ComboBox)
        cmbCursoDir = new ComboBox<>();
        cmbCursoDir.setDisable(true);
        grid.add(new Label("Curso a cargo:"), 0, row);
        grid.add(cmbCursoDir, 1, row++);

        chkDocenteGrupo = new CheckBox("Docente de grupo");
        chkDocenteGrupo.selectedProperty().addListener((obs, old, val) -> {
            cmbCursoDir.setDisable(!val);
            if (val) actualizarComboCursoDir();
        });
        grid.add(chkDocenteGrupo, 1, row++);

        // Cuando se cambia la selección de cursos, actualizar el combo de curso a cargo
        listCursosAsignados.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            if (chkDocenteGrupo.isSelected()) actualizarComboCursoDir();
        });

        btnGuardar = new Button("Guardar");
        btnGuardar.setOnAction(e -> guardar());
        grid.add(btnGuardar, 1, row);

        getChildren().add(grid);
        ajustarCamposPorTipo();
    }

    private void actualizarComboCursoDir() {
        List<String> seleccionados = listCursosAsignados.getSelectionModel().getSelectedItems();
        cmbCursoDir.getItems().setAll(seleccionados);
        if (!seleccionados.contains(cmbCursoDir.getValue())) {
            cmbCursoDir.setValue(null);
        }
    }

    private void actualizarIdGenerado() {
        if (usuarioEditar == null && cmbTipo != null) {
            try {
                String id = userController.generarIdPorRol(cmbTipo.getValue());
                lblIdGenerado.setText(id);
            } catch (Exception e) {
                lblIdGenerado.setText("Error generando ID");
            }
        }
    }

    private void ajustarCamposPorTipo() {
        if (usuarioEditar != null) return;
        String tipo = cmbTipo.getValue();
        boolean esEstudiante = "estudiante".equals(tipo);
        boolean esDocente = "docente".equals(tipo);

        txtGrado.setVisible(esEstudiante);
        chkRepresentante.setVisible(esEstudiante);
        txtMateria.setVisible(esDocente);
        listCursosAsignados.setVisible(esDocente);
        cmbCursoDir.setVisible(esDocente);
        chkDocenteGrupo.setVisible(esDocente);
    }

    private void cargarDatos() {
        if (usuarioEditar == null) return;
        txtNombre.setText(usuarioEditar.getNombre());
        txtApellido.setText(usuarioEditar.getApellido());
        txtContrasenia.setText(usuarioEditar.getContrasenia());

        if (usuarioEditar instanceof Estudiante) {
            Estudiante e = (Estudiante) usuarioEditar;
            txtGrado.setText(e.getGrado());
            chkRepresentante.setSelected(e.isEsRepresentante());
            // Ocultar controles de docente
            txtMateria.setVisible(false);
            listCursosAsignados.setVisible(false);
            cmbCursoDir.setVisible(false);
            chkDocenteGrupo.setVisible(false);
        } else if (usuarioEditar instanceof Docente) {
            Docente d = (Docente) usuarioEditar;
            txtMateria.setText(d.getMateria());
            // Seleccionar cursos asignados
            List<String> cursosAsig = d.getCursosAsignados();
            listCursosAsignados.getSelectionModel().clearSelection();
            for (String curso : cursosAsig) {
                if (TODOS_LOS_CURSOS.contains(curso)) {
                    listCursosAsignados.getSelectionModel().select(curso);
                }
            }
            if (d.isEsDocenteDeGrupo()) {
                chkDocenteGrupo.setSelected(true);
                cmbCursoDir.setValue(d.getCursoDireccionGrupo());
                cmbCursoDir.setDisable(false);
                actualizarComboCursoDir();
            } else {
                chkDocenteGrupo.setSelected(false);
                cmbCursoDir.setValue(null);
            }
            // Ocultar controles de estudiante
            txtGrado.setVisible(false);
            chkRepresentante.setVisible(false);
        } else {
            // Coordinador
            txtGrado.setVisible(false);
            chkRepresentante.setVisible(false);
            txtMateria.setVisible(false);
            listCursosAsignados.setVisible(false);
            cmbCursoDir.setVisible(false);
            chkDocenteGrupo.setVisible(false);
        }
    }

    private void guardar() {
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String pass = txtContrasenia.getText();
        if (nombre.isEmpty() || apellido.isEmpty() || pass.isEmpty()) {
            mostrarAlerta("Todos los campos obligatorios deben estar llenos.");
            return;
        }

        try {
            if (usuarioEditar == null) {
                String tipo = cmbTipo.getValue();
                String adicional = null;
                if ("estudiante".equals(tipo)) {
                    adicional = txtGrado.getText().trim();
                    if (adicional.isEmpty()) throw new IllegalArgumentException("Grado requerido");
                }
                Usuario nuevo = userController.registrarNuevoUsuario(tipo, null, nombre, apellido, pass, adicional);

                if (nuevo instanceof Estudiante && chkRepresentante.isSelected()) {
                    userController.asignarRepresentante(nuevo.getId(), true);
                } else if (nuevo instanceof Docente) {
                    Docente doc = (Docente) nuevo;
                    doc.setMateria(txtMateria.getText().trim());
                    List<String> cursosSel = listCursosAsignados.getSelectionModel().getSelectedItems();
                    doc.setCursosAsignados(new ArrayList<>(cursosSel));
                    if (chkDocenteGrupo.isSelected()) {
                        String cursoDir = cmbCursoDir.getValue();
                        if (cursoDir == null) {
                            mostrarAlerta("Debe seleccionar un curso a cargo.");
                            return;
                        }
                        doc.setCursoDireccionGrupo(cursoDir);
                        doc.setEsDocenteDeGrupo(true);
                    } else {
                        doc.setCursoDireccionGrupo(null);
                        doc.setEsDocenteDeGrupo(false);
                    }
                    MainApp.getDAO().guardarUsuario(doc);
                }
            } else {
                // Edición
                usuarioEditar.setNombre(nombre);
                usuarioEditar.setApellido(apellido);
                if (!pass.equals(usuarioEditar.getContrasenia())) {
                    usuarioEditar.setContrasenia(pass);
                }
                MainApp.getDAO().guardarUsuario(usuarioEditar);

                if (usuarioEditar instanceof Estudiante) {
                    userController.asignarRepresentante(usuarioEditar.getId(), chkRepresentante.isSelected());
                } else if (usuarioEditar instanceof Docente) {
                    Docente doc = (Docente) usuarioEditar;
                    doc.setMateria(txtMateria.getText().trim());
                    List<String> cursosSel = listCursosAsignados.getSelectionModel().getSelectedItems();
                    doc.setCursosAsignados(new ArrayList<>(cursosSel));
                    if (chkDocenteGrupo.isSelected()) {
                        String cursoDir = cmbCursoDir.getValue();
                        if (cursoDir == null) {
                            mostrarAlerta("Debe seleccionar un curso a cargo.");
                            return;
                        }
                        doc.setCursoDireccionGrupo(cursoDir);
                        doc.setEsDocenteDeGrupo(true);
                    } else {
                        doc.setCursoDireccionGrupo(null);
                        doc.setEsDocenteDeGrupo(false);
                    }
                    MainApp.getDAO().guardarUsuario(doc);
                }
            }
            mostrarAlerta("Usuario guardado correctamente", Alert.AlertType.INFORMATION);
            onSuccess.run();
        } catch (Exception e) {
            mostrarAlerta("Error: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String msg) {
        mostrarAlerta(msg, Alert.AlertType.ERROR);
    }

    private void mostrarAlerta(String msg, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo, msg);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}