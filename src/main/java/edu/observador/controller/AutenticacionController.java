// Archivo: src/edu/observador/controller/AutenticacionController.java
package edu.observador.controller;

import edu.observador.data.DataAccessException;
import edu.observador.data.ObservadorDAO;
import edu.observador.model.Usuario;

/**
 * Controlador encargado de la autenticación y gestión de sesión de usuarios.
 * Maneja el inicio de sesión, cambio obligatorio de contraseña y cierre de sesión.
 */
public class AutenticacionController {

    private final ObservadorDAO usuarioDAO;
    private Usuario usuarioLogueado;

    /**
     * Constructor que recibe el DAO para acceder a la persistencia.
     *
     * @param dao Objeto DAO para operaciones con usuarios
     */
    public AutenticacionController(ObservadorDAO dao) {
        this.usuarioDAO = dao;
        this.usuarioLogueado = null;
    }

    /**
     * Intenta iniciar sesión con las credenciales proporcionadas.
     *
     * @param id          Identificador del usuario
     * @param contrasenia Contraseña del usuario
     * @return true si las credenciales son válidas y el usuario está activo, false en otro caso
     * @throws DataAccessException si hay error de acceso a datos
     */
    public boolean iniciarSesion(String id, String contrasenia) throws DataAccessException {
        Usuario usuario = usuarioDAO.buscarUsuarioPorId(id);
        if (usuario == null) {
            return false;
        }
        if (!usuario.isActivo()) {
            return false;
        }
        if (!usuario.getContrasenia().equals(contrasenia)) {
            return false;
        }
        if (!usuario.esValida()) {
            return false;
        }
        this.usuarioLogueado = usuario;
        return true;
    }

    /**
     * Cambia la contraseña del usuario logueado cuando es obligatorio (primer ingreso)
     * o por solicitud voluntaria.
     *
     * @param nuevaContrasenia Nueva contraseña (debe cumplir requisitos mínimos)
     * @return true si se cambió exitosamente
     * @throws DataAccessException        si hay error al guardar
     * @throws IllegalStateException      si no hay usuario logueado
     * @throws IllegalArgumentException   si la contraseña es inválida
     */
    public boolean procesarCambioContraseniaObligatorio(String nuevaContrasenia)
            throws DataAccessException {
        if (usuarioLogueado == null) {
            throw new IllegalStateException("No hay un usuario logueado");
        }
        if (nuevaContrasenia == null || nuevaContrasenia.length() < 4) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 4 caracteres");
        }
        usuarioLogueado.setContrasenia(nuevaContrasenia);
        usuarioLogueado.setPrimerIngreso(false);
        usuarioDAO.guardarUsuario(usuarioLogueado);
        return true;
    }

    /**
     * Obtiene el usuario actualmente logueado.
     *
     * @return Usuario logueado o null si no hay sesión activa
     */
    public Usuario obtenerUsuarioLogueado() {
        return usuarioLogueado;
    }

    /**
     * Cierra la sesión actual.
     */
    public void cerrarSesion() {
        this.usuarioLogueado = null;
    }
}