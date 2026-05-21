// Archivo: src/edu/observador/data/DataAccessException.java
package edu.observador.data;

/**
 * Excepción personalizada para errores de acceso a datos.
 * Encapsula excepciones de bajo nivel (SQLException, IOException).
 *
 * @author TuNombre
 * @version 1.0
 */
public class DataAccessException extends Exception {

    /**
     * Constructor con mensaje de error.
     *
     * @param message Mensaje descriptivo del error
     */
    public DataAccessException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa original.
     *
     * @param message Mensaje descriptivo del error
     * @param cause   Causa original (excepción lanzada por la capa inferior)
     */
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}