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
            menu.getChildren().addAll(
                    crearBotonMenu("Inicio", "🏠"),
                    crearBotonMenu("Mi Historial", "📋")
            );
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

        VBox calendario = crearCalendario(LocalDate.now().getYear(), LocalDate.now().getMonthValue());

        if (usuarioActual instanceof Coordinador) {
            try {
                int totalUsuarios = userController.getTotalUsuarios();
                int totalObservaciones = obsController.getTotalObservaciones();
                int peticionesRevisadas = obsController.getPeticionesRevisadas();
                int peticionesPendientes = obsController.getPeticionesPendientes();

                HBox tarjetas = new HBox(20);
                tarjetas.setAlignment(Pos.CENTER);
                tarjetas.getChildren().addAll(
                        crearTarjetaEstadistica("👥 Total Usuarios", String.valueOf(totalUsuarios), "#3498db"),
                        crearTarjetaEstadistica("📝 Total Observaciones", String.valueOf(totalObservaciones), "#2ecc71"),
                        crearTarjetaEstadistica("✅ Revisadas", String.valueOf(peticionesRevisadas), "#f39c12"),
                        crearTarjetaEstadistica("⏳ Pendientes", String.valueOf(peticionesPendientes), "#e74c3c")
                );
                panel.getChildren().addAll(bienvenida, tarjetas, calendario);
            } catch (DataAccessException e) {
                panel.getChildren().add(new Label("Error cargando estadísticas: " + e.getMessage()));
            }
        } else if (usuarioActual instanceof Docente) {
            // Por ahora solo bienvenida y calendario (puede ampliarse)
            panel.getChildren().addAll(bienvenida, calendario);
        } else if (usuarioActual instanceof Estudiante) {
            Estudiante est = (Estudiante) usuarioActual;
            try {
                int total = obsController.getTotalObservacionesEstudiante(est.getId(), true);
                int academicas = obsController.getTotalAcademicasEstudiante(est.getId(), true);
                int disciplinarias = obsController.getTotalDisciplinariasEstudiante(est.getId(), true);

                HBox tarjetas = new HBox(20);
                tarjetas.setAlignment(Pos.CENTER);
                tarjetas.getChildren().addAll(
                        crearTarjetaEstudiante("📋 Total Observaciones", String.valueOf(total), "#3498db"),
                        crearTarjetaEstudiante("📖 Académicas", String.valueOf(academicas), "#2ecc71"),
                        crearTarjetaEstudiante("⚠️ Disciplinarias", String.valueOf(disciplinarias), "#e74c3c")
                );
                panel.getChildren().addAll(bienvenida, tarjetas, calendario);
            } catch (DataAccessException e) {
                panel.getChildren().add(new Label("Error cargando estadísticas: " + e.getMessage()));
            }
        } else {
            panel.getChildren().addAll(bienvenida, calendario);
        }
        contenidoCentral.getChildren().setAll(panel);
    }

    private VBox crearTarjetaEstadistica(String titulo, String valor, String color) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        card.setPrefWidth(180);
        card.setMinWidth(150);

        Label lblTitulo = new Label(titulo);
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblTitulo.setStyle("-fx-text-fill: #7f8c8d;");

        Label lblValor = new Label(valor);
        lblValor.setFont(Font.font("System", FontWeight.BOLD, 32));
        lblValor.setStyle("-fx-text-fill: " + color + ";");

        card.getChildren().addAll(lblTitulo, lblValor);
        return card;
    }

    private VBox crearTarjetaEstudiante(String titulo, String valor, String color) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        card.setPrefWidth(160);
        card.setMinWidth(140);

        Label lblTitulo = new Label(titulo);
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblTitulo.setStyle("-fx-text-fill: #7f8c8d;");

        Label lblValor = new Label(valor);
        lblValor.setFont(Font.font("System", FontWeight.BOLD, 32));
        lblValor.setStyle("-fx-text-fill: " + color + ";");

        card.getChildren().addAll(lblTitulo, lblValor);
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
            int totalEstudiantes = userController.getTotalEstudiantes();
            int totalDocentes = userController.getTotalDocentes();
            int totalAcademicas = obsController.getTotalObservacionesAcademicas();
            int totalDisciplinarias = obsController.getTotalObservacionesDisciplinarias();

            HBox tarjetas = new HBox(20);
            tarjetas.setAlignment(Pos.CENTER);
            tarjetas.getChildren().addAll(
                    crearTarjetaAnalitica("Total Estudiantes", String.valueOf(totalEstudiantes), "#3498db"),
                    crearTarjetaAnalitica("Total Docentes", String.valueOf(totalDocentes), "#27ae60"),
                    crearTarjetaAnalitica("Observaciones Académicas", String.valueOf(totalAcademicas), "#2ecc71"),
                    crearTarjetaAnalitica("Observaciones Disciplinarias", String.valueOf(totalDisciplinarias), "#e74c3c")
            );
            panel.getChildren().addAll(titulo, tarjetas);
        } catch (DataAccessException e) {
            panel.getChildren().add(new Label("Error cargando analíticas: " + e.getMessage()));
        }
        contenidoCentral.getChildren().setAll(panel);
    }

    private VBox crearTarjetaAnalitica(String titulo, String valor, String color) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        card.setPrefWidth(200);
        card.setMinWidth(160);

        Label lblTitulo = new Label(titulo);
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblTitulo.setStyle("-fx-text-fill: #7f8c8d;");

        Label lblValor = new Label(valor);
        lblValor.setFont(Font.font("System", FontWeight.BOLD, 32));
        lblValor.setStyle("-fx-text-fill: " + color + ";");

        card.getChildren().addAll(lblTitulo, lblValor);
        return card;
    }

    private void cargarGestionUsuarios() {
        if (!(usuarioActual instanceof Coordinador)) return;
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Gestión de Usuarios");
        GestionUsuariosView gestion = new GestionUsuariosView();
        stage.setScene(new Scene(gestion, 1100, 700));
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.initOwner(getScene().getWindow());
        stage.show();
    }

    private void cargarRegistroObservacion() {
        RegistroObservacionView registro = new RegistroObservacionView();
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Registrar Observación");
        stage.setScene(new Scene(registro, 650, 550));
        stage.setMinWidth(600);
        stage.setMinHeight(500);
        stage.initOwner(getScene().getWindow());
        stage.show();
    }

    private void cargarGestionPeticiones() {
        if (!(usuarioActual instanceof Coordinador)) return;
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Gestionar Peticiones de Revisión");
        GestionPeticionesView view = new GestionPeticionesView();
        stage.setScene(new Scene(view, 950, 650));
        stage.setMinWidth(800);
        stage.setMinHeight(550);
        stage.initOwner(getScene().getWindow());
        stage.show();
    }

    private void cargarReportes() {
        if (!(usuarioActual instanceof Coordinador)) return;
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Reportes de Convivencia");
        ReportesView reportes = new ReportesView();
        stage.setScene(new Scene(reportes, 950, 700));
        stage.setMinWidth(800);
        stage.setMinHeight(600);
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
            stage.setScene(new Scene(historial, 1000, 700));
            stage.setMinWidth(800);
            stage.setMinHeight(600);
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
        stage.setScene(new Scene(view, 600, 500));
        stage.setMinWidth(500);
        stage.setMinHeight(450);
        stage.initOwner(getScene().getWindow());
        stage.show();
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