// Archivo: src/edu/observador/data/ConexionDB.java
package edu.observador.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase singleton para manejar la conexión a la base de datos SQLite.
 * Proporciona métodos para obtener conexiones y activar claves foráneas.
 *
 * @author TuNombre
 * @version 1.0
 */
public class ConexionDB {

    private static final String URL = "jdbc:sqlite:edu_observador.db";
    private static ConexionDB instancia;
    private Connection conexion;

    private ConexionDB() {
        // Constructor privado para singleton
    }

    /**
     * Obtiene la instancia única de la clase.
     *
     * @return Instancia de ConexionDB
     */
    public static ConexionDB getInstancia() {
        if (instancia == null) {
            instancia = new ConexionDB();
        }
        return instancia;
    }

    /**
     * Obtiene una conexión a la base de datos.
     * Si no existe, la crea y activa las claves foráneas.
     *
     * @return Conexión activa
     * @throws SQLException si no se puede conectar
     */
    public Connection obtenerConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(URL);
            // Activar soporte de claves foráneas en SQLite
            try (Statement stmt = conexion.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }
        }
        return conexion;
    }

    /**
     * Cierra la conexión si está abierta.
     */
    public void cerrar() {
        if (conexion != null) {
            try {
                if (!conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
                System.err.println("Error cerrando conexión: " + e.getMessage());
            }
        }
    }
}