module edu.observador {
    // 1. DEPENDENCIAS DEL SISTEMA Y LIBRERÍAS EXTERNAS
    requires javafx.controls;
    requires javafx.fxml;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires java.sql; // Obligatorio para la conexión JDBC con SQLite

    // 2. APERTURA DE PAQUETES (Permite a JavaFX usar Reflexión para leerlos)
    opens edu.observador to javafx.fxml;
    opens edu.observador.controller to javafx.fxml; // Enlaza tus FXML con los controladores
    opens edu.observador.model to javafx.base;      // Permite que las tablas (TableView) muestren datos

    // 3. EXPORTACIÓN DE PAQUETES (Hace públicos tus paquetes para el entorno de ejecución)
    exports edu.observador;
    exports edu.observador.model;
    exports edu.observador.model.enums;
    exports edu.observador.data;
    exports edu.observador.controller;
}