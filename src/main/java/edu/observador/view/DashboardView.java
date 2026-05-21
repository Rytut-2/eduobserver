// Archivo: src/edu/observador/view/DashboardView.java
package edu.observador.view;

import edu.observador.MainApp;
import edu.observador.controller.ObservacionController;
import edu.observador.controller.UsuarioController;
import edu.observador.data.DataAccessException;
import edu.observador.model.*;
import edu.observador.view.controllers.Sesion;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardView extends BorderPane {

    private final Usuario usuarioActual;
    private final ObservacionController obsController;
    private final UsuarioController userController;

    private VBox menuLateral;
    private StackPane contenidoCentral;

    public DashboardView() {
        this.usuarioActual = Sesion.getUsuarioActual();
        this.obsController = new ObservacionController(MainApp.getDAO());
        this.userController = new UsuarioController(MainApp.getDAO());

        inicializarUI();
        cargarPanelPorDefecto();
    }

    private void inicializarUI() {
        HBox topBar = crearTopBar();
        setTop(topBar);

        menuLateral = crearMenuLateral();
        setLeft(menuLateral);

        contenidoCentral = new StackPane();
        contenidoCentral.setPadding(new Insets(20));
        setCenter(contenidoCentral);

        setStyle("-fx-background-color: #f4f7fc;");
    }

    private HBox crearTopBar() {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(15, 20, 15, 20));
        topBar.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2);");

        VBox perfil = new VBox(5);
        perfil.setAlignment(Pos.CENTER_RIGHT);
        Label lblUsuario = new Label(usuarioActual.getNombreCompleto());
        lblUsuario.setFont(Font.font("System", FontWeight.BOLD, 14));
        Label lblEmail = new Label(usuarioActual.getId() + "@educo.edu");
        lblEmail.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");
        perfil.getChildren().addAll(lblUsuario, lblEmail);
        topBar.getChildren().add(perfil);
        return topBar;
    }

    private VBox crearMenuLateral() {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(20, 10, 20, 10));
        menu.setPrefWidth(220);
        menu.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 1 0 0;");

        Label titulo = new Label("EduObservador");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 18));
        titulo.setStyle("-fx-text-fill: #2c3e50;");
        titulo.setPadding(new Insets(0, 0, 20, 10));
        menu.getChildren().add(titulo);

        if (usuarioActual instanceof Coordinador) {
            menu.getChildren().addAll(
                    crearBotonMenu("Inicio", "🏠"),
                    crearBotonMenu("Analíticas", "📊"),
                    crearBotonMenu("Usuarios", "👥"),
                    crearBotonMenu("Observaciones", "📝"),
                    crearBotonMenu("Gestionar Peticiones", "📋"),
                    crearBotonMenu("Reportes", "📄")
            );
        } else if (usuarioActual instanceof Docente) {
            menu.getChildren().addAll(
                    crearBotonMenu("Inicio", "🏠"),
                    crearBotonMenu("Mis Estudiantes", "👨‍🎓"),
                    crearBotonMenu("Nueva Observación", "✏️"),
                    crearBotonMenu("Historial", "📜")
            );
        } else if (usuarioActual instanceof Estudiante) {
            Estudiante est = (Estudiante) usuarioActual;
            // Todos los estudiantes ven Inicio y Mi Historial
            menu.getChildren().addAll(
                    crearBotonMenu("Inicio", "🏠"),
                    crearBotonMenu("Mi Historial", "📋")
            );
            // Solo representantes ven opción adicional de solicitar revisión
            if (est.isEsRepresentante()) {
                menu.getChildren().add(crearBotonMenu("Solicitar Revisión", "⚖️"));
            }
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        menu.getChildren().add(spacer);

        Button btnCerrar = new Button("Cerrar Sesión");
        btnCerrar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5;");
        btnCerrar.setMaxWidth(Double.MAX_VALUE);
        btnCerrar.setOnAction(e -> cerrarSesion());
        menu.getChildren().add(btnCerrar);

        return menu;
    }

    private Button crearBotonMenu(String texto, String icono) {
        Button btn = new Button(icono + "  " + texto);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle("-fx-background-color: transparent; -fx-padding: 10 5 10 15; -fx-text-fill: #34495e;");
        btn.setOnAction(e -> manejarOpcionMenu(texto));
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 10 5 10 15;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-padding: 10 5 10 15;"));
        return btn;
    }

    private void manejarOpcionMenu(String opcion) {
        switch (opcion) {
            case "Inicio": cargarPanelInicio(); break;
            case "Analíticas": cargarAnaliticas(); break;
            case "Usuarios": cargarGestionUsuarios(); break;
            case "Observaciones": cargarRegistroObservacion(); break;
            case "Gestionar Peticiones": cargarGestionPeticiones(); break;
            case "Reportes": cargarReportes(); break;
            case "Mis Estudiantes": cargarListaEstudiantes(); break;
            case "Nueva Observación": cargarRegistroObservacion(); break;
            case "Historial": cargarHistorial(); break;
            case "Mi Historial": cargarMiHistorial(); break;
            case "Solicitar Revisión": cargarSolicitarRevision(); break;
            case "Buzón de Sugerencias": cargarBuzon(); break;
            case "Reportes de Grupo": cargarReporteGrupo(); break;
            default: cargarPanelInicio();
        }
    }

    private void cargarPanelPorDefecto() {
        cargarPanelInicio();
    }

    private void cargarPanelInicio() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(10));

        Label bienvenida = new Label("Bienvenido de vuelta, " + usuarioActual.getNombre());
        bienvenida.setFont(Font.font("System", FontWeight.BOLD, 24));

        VBox resumen = crearResumenEstudiantesMasObservaciones();
        VBox calendario = crearCalendario(LocalDate.now().getYear(), LocalDate.now().getMonthValue());

        HBox row = new HBox(20);
        row.getChildren().addAll(resumen, calendario);
        panel.getChildren().addAll(bienvenida, row);
        contenidoCentral.getChildren().setAll(panel);
    }

    private VBox crearResumenEstudiantesMasObservaciones() {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        card.setPrefWidth(300);

        Label titulo = new Label("📌 Estudiantes con más observaciones");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 16));

        ListView<String> lista = new ListView<>();
        try {
            List<Estudiante> tops = obsController.obtenerEstudiantesMasObservaciones(5);
            for (Estudiante e : tops) {
                int cantidad = obsController.getHistorialEstudiante(e.getId(), true).size();
                lista.getItems().add(e.getNombreCompleto() + " - " + cantidad + " obs.");
            }
        } catch (DataAccessException e) {
            lista.getItems().add("No se pudieron cargar datos.");
        }
        lista.setPrefHeight(200);
        card.getChildren().addAll(titulo, lista);
        return card;
    }

    private VBox crearCalendario(int año, int mes) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        card.setPrefWidth(350);

        YearMonth yearMonth = YearMonth.of(año, mes);
        LocalDate primerDia = yearMonth.atDay(1);
        int diasEnMes = yearMonth.lengthOfMonth();
        int diaSemanaInicio = primerDia.getDayOfWeek().getValue();

        Label titulo = new Label(yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        titulo.setFont(Font.font("System", FontWeight.BOLD, 16));

        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setAlignment(Pos.CENTER);

        String[] diasSemana = {"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};
        for (int i = 0; i < diasSemana.length; i++) {
            Label diaLabel = new Label(diasSemana[i]);
            diaLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
            diaLabel.setStyle("-fx-text-fill: #7f8c8d;");
            grid.add(diaLabel, i, 0);
        }

        int fila = 1;
        int col = diaSemanaInicio - 1;
        for (int dia = 1; dia <= diasEnMes; dia++) {
            Label numero = new Label(String.valueOf(dia));
            numero.setAlignment(Pos.CENTER);
            numero.setPrefSize(40, 40);
            numero.setStyle("-fx-background-color: #f9f9f9; -fx-border-radius: 20; -fx-background-radius: 20;");
            if (dia == LocalDate.now().getDayOfMonth() && año == LocalDate.now().getYear() && mes == LocalDate.now().getMonthValue()) {
                numero.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-border-radius: 20; -fx-background-radius: 20;");
            }
            grid.add(numero, col, fila);
            col++;
            if (col % 7 == 0) {
                col = 0;
                fila++;
            }
        }

        card.getChildren().addAll(titulo, grid);
        return card;
    }

    private void cargarAnaliticas() {
        if (!(usuarioActual instanceof Coordinador)) return;
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(10));
        Label titulo = new Label("Analíticas de Convivencia");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 20));

        try {
            List<Estudiante> estudiantes = userController.listarEstudiantes();
            int total = estudiantes.size();
            int rojos = 0, amarillos = 0, verdes = 0;
            for (Estudiante e : estudiantes) {
                String nivel = e.calcularNivelAlerta();
                switch (nivel) {
                    case "Rojo": rojos++; break;
                    case "Amarillo": amarillos++; break;
                    default: verdes++;
                }
            }
            GridPane stats = new GridPane();
            stats.setHgap(20);
            stats.setVgap(10);
            stats.add(new Label("Total estudiantes:"), 0, 0);
            stats.add(new Label(String.valueOf(total)), 1, 0);
            stats.add(new Label("Alerta Roja:"), 0, 1);
            stats.add(new Label(String.valueOf(rojos)), 1, 1);
            stats.add(new Label("Alerta Amarilla:"), 0, 2);
            stats.add(new Label(String.valueOf(amarillos)), 1, 2);
            stats.add(new Label("Alerta Verde:"), 0, 3);
            stats.add(new Label(String.valueOf(verdes)), 1, 3);
            panel.getChildren().addAll(titulo, stats);
        } catch (DataAccessException e) {
            panel.getChildren().add(new Label("Error cargando datos"));
        }
        contenidoCentral.getChildren().setAll(panel);
    }

    private void cargarGestionUsuarios() {
        if (!(usuarioActual instanceof Coordinador)) return;
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Gestión de Usuarios");
        GestionUsuariosView gestion = new GestionUsuariosView();
        stage.setScene(new Scene(gestion, 1000, 600));
        stage.initOwner(getScene().getWindow());
        stage.show();
    }

    private void cargarRegistroObservacion() {
        RegistroObservacionView registro = new RegistroObservacionView();
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Registrar Observación");
        stage.setScene(new Scene(registro, 600, 500));
        stage.initOwner(getScene().getWindow());
        stage.show();
    }

    private void cargarGestionPeticiones() {
        if (!(usuarioActual instanceof Coordinador)) return;
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Gestionar Peticiones de Revisión");
        GestionPeticionesView view = new GestionPeticionesView();
        stage.setScene(new Scene(view, 800, 500));
        stage.initOwner(getScene().getWindow());
        stage.show();
    }

    private void cargarReportes() {
        if (!(usuarioActual instanceof Coordinador)) return;
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Reportes de Convivencia");
        ReportesView reportes = new ReportesView();
        stage.setScene(new Scene(reportes, 800, 600));
        stage.initOwner(getScene().getWindow());
        stage.show();
    }

    private void cargarListaEstudiantes() {
        if (!(usuarioActual instanceof Docente)) return;
        Docente docente = (Docente) usuarioActual;
        ListaEstudiantesView listaView = new ListaEstudiantesView(docente);
        contenidoCentral.getChildren().setAll(listaView);
    }

    private void cargarHistorial() {
        if (usuarioActual instanceof Docente) {
            cargarListaEstudiantes();
        } else {
            Label label = new Label("Historial - Pendiente implementar");
            contenidoCentral.getChildren().setAll(label);
        }
    }

    private void cargarMiHistorial() {
        if (usuarioActual instanceof Estudiante) {
            Estudiante est = (Estudiante) usuarioActual;
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setTitle("Mi Historial");
            HistorialView historial = new HistorialView(est);
            stage.setScene(new Scene(historial, 900, 600));
            stage.initOwner(getScene().getWindow());
            stage.show();
        }
    }

    private void cargarSolicitarRevision() {
        if (!(usuarioActual instanceof Estudiante) || !((Estudiante) usuarioActual).isEsRepresentante()) {
            mostrarAlerta("Acceso denegado", "Solo los representantes pueden solicitar revisiones.", Alert.AlertType.WARNING);
            return;
        }
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Solicitar Revisión de Observación");
        SolicitarRevisionView view = new SolicitarRevisionView();
        stage.setScene(new Scene(view, 500, 400));
        stage.initOwner(getScene().getWindow());
        stage.show();
    }

    private void cargarBuzon() {
        if (!(usuarioActual instanceof Estudiante) || !((Estudiante) usuarioActual).isEsRepresentante()) {
            mostrarAlerta("Acceso denegado", "Solo los representantes pueden acceder al buzón.", Alert.AlertType.WARNING);
            return;
        }
        Label label = new Label("Buzón de sugerencias - Pendiente implementar");
        contenidoCentral.getChildren().setAll(label);
    }

    private void cargarReporteGrupo() {
        if (!(usuarioActual instanceof Estudiante) || !((Estudiante) usuarioActual).isEsRepresentante()) {
            mostrarAlerta("Acceso denegado", "Solo los representantes pueden ver reportes de grupo.", Alert.AlertType.WARNING);
            return;
        }
        Label label = new Label("Reportes de grupo - Pendiente implementar");
        contenidoCentral.getChildren().setAll(label);
    }

    private void cerrarSesion() {
        Sesion.cerrarSesion();
        MainApp.volverALogin();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}