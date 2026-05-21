// Archivo: src/edu/observador/view/FormularioUsuarioView.java
package edu.observador.view;

import edu.observador.MainApp;
import edu.observador.controller.UsuarioController;
import edu.observador.model.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Formulario para crear o editar un usuario.
 * Es utilizado por GestionUsuariosView en un diálogo modal.
 */
public class FormularioUsuarioView extends VBox {

    private final Usuario usuarioEditar;
    private final Runnable onSuccess;
    private final UsuarioController userController;

    private ComboBox<String> cmbTipo;
    private TextField txtNombre, txtApellido, txtGrado, txtCursos, txtCursoDir;
    private PasswordField txtContrasenia;
    private CheckBox chkRepresentante, chkDocenteGrupo;
    private Button btnGuardar;
    private Label lblIdGenerado;

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

        txtGrado = new TextField();
        txtGrado.setPromptText("Grado (ej. 10A)");
        grid.add(new Label("Grado:"), 0, row);
        grid.add(txtGrado, 1, row++);

        chkRepresentante = new CheckBox("Representante de grupo");
        grid.add(chkRepresentante, 1, row++);

        txtCursos = new TextField();
        txtCursos.setPromptText("Cursos asignados, separados por coma");
        grid.add(new Label("Cursos:"), 0, row);
        grid.add(txtCursos, 1, row++);

        txtCursoDir = new TextField();
        txtCursoDir.setPromptText("Curso que dirige (si es docente de grupo)");
        grid.add(new Label("Curso a cargo:"), 0, row);
        grid.add(txtCursoDir, 1, row++);

        chkDocenteGrupo = new CheckBox("Docente de grupo");
        grid.add(chkDocenteGrupo, 1, row++);

        btnGuardar = new Button("Guardar");
        btnGuardar.setOnAction(e -> guardar());
        grid.add(btnGuardar, 1, row);

        getChildren().add(grid);
        ajustarCamposPorTipo();
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
        txtCursos.setVisible(esDocente);
        txtCursoDir.setVisible(esDocente);
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
            txtCursos.setVisible(false);
            txtCursoDir.setVisible(false);
            chkDocenteGrupo.setVisible(false);
        } else if (usuarioEditar instanceof Docente) {
            Docente d = (Docente) usuarioEditar;
            txtCursos.setText(String.join(",", d.getCursosAsignados()));
            txtCursoDir.setText(d.getCursoDireccionGrupo());
            chkDocenteGrupo.setSelected(d.isEsDocenteDeGrupo());
            txtGrado.setVisible(false);
            chkRepresentante.setVisible(false);
        } else {
            txtGrado.setVisible(false);
            chkRepresentante.setVisible(false);
            txtCursos.setVisible(false);
            txtCursoDir.setVisible(false);
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
                // Asignar banderas después de creado
                if (nuevo instanceof Estudiante && chkRepresentante.isSelected()) {
                    userController.asignarRepresentante(nuevo.getId(), true);
                } else if (nuevo instanceof Docente) {
                    if (chkDocenteGrupo.isSelected()) {
                        userController.asignarDocenteDeGrupo(nuevo.getId(), true, txtCursoDir.getText().trim());
                    }
                    // Asignar cursos (pendiente implementar, si se requiere)
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
                    userController.asignarDocenteDeGrupo(usuarioEditar.getId(), chkDocenteGrupo.isSelected(), txtCursoDir.getText().trim());
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