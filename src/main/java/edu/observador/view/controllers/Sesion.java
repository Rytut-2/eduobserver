// Archivo: src/edu/observador/view/controllers/Sesion.java
package edu.observador.view.controllers;

import edu.observador.model.Usuario;

/**
 * Clase para mantener el usuario actualmente logueado en la sesión de la vista.
 * Permite acceder al usuario desde cualquier controlador o vista.
 */
public class Sesion {
    private static Usuario usuarioActual;

    /**
     * Obtiene el usuario actualmente logueado.
     *
     * @return Usuario logueado o null si no hay sesión activa
     */
    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Establece el usuario actual (después de un login exitoso).
     *
     * @param usuario Usuario logueado
     */
    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
    }

    /**
     * Cierra la sesión, eliminando el usuario actual.
     * Nota: No maneja navegación, solo limpia el estado.
     */
    public static void cerrarSesion() {
        usuarioActual = null;
    }
}