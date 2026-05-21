// Archivo: src/edu/observador/controller/UsuarioController.java
package edu.observador.controller;

import edu.observador.data.DataAccessException;
import edu.observador.data.ObservadorDAO;
import edu.observador.model.*;
import edu.observador.model.enums.RolUsuario;

import java.util.List;

/**
 * Controlador para la gestión de usuarios: creación, modificación de estado,
 * listado por rol, y asignación de banderas (representante, docente de grupo).
 */
public class UsuarioController {

    private final ObservadorDAO usuarioDAO;

    public UsuarioController(ObservadorDAO dao) {
        this.usuarioDAO = dao;
    }

    /**
     * Genera un ID automático según el rol y la secuencia actual.
     *
     * @param tipoRol "estudiante", "docente" o "coordinador"
     * @return ID con formato EST-xxx, DOC-xxx o COOD-xxx
     * @throws DataAccessException si falla la consulta a la BD
     */
    public String generarIdPorRol(String tipoRol) throws DataAccessException {
        String prefijo;
        switch (tipoRol.toLowerCase()) {
            case "estudiante":
                prefijo = "EST-";
                break;
            case "docente":
                prefijo = "DOC-";
                break;
            case "coordinador":
                prefijo = "COOD-";
                break;
            default:
                throw new IllegalArgumentException("Rol inválido: " + tipoRol);
        }
        RolUsuario rol = RolUsuario.valueOf(tipoRol.toUpperCase());
        List<Usuario> usuarios = listarUsuariosPorRol(rol);
        int max = 0;
        for (Usuario u : usuarios) {
            String id = u.getId();
            if (id.startsWith(prefijo)) {
                String numStr = id.substring(prefijo.length());
                try {
                    int num = Integer.parseInt(numStr);
                    if (num > max) max = num;
                } catch (NumberFormatException ignored) {
                }
            }
        }
        int nuevo = max + 1;
        return prefijo + String.format("%03d", nuevo);
    }

    /**
     * Registra un nuevo usuario. Si el id es null o vacío, lo genera automáticamente.
     *
     * @param tipo        "estudiante", "docente" o "coordinador"
     * @param id          Identificador (puede ser null para generación automática)
     * @param nombre      Nombre
     * @param apellido    Apellido
     * @param contrasenia Contraseña
     * @param adicional   Para estudiante: grado; para otros: null
     * @return El usuario creado
     * @throws DataAccessException si falla la persistencia
     */
    public Usuario registrarNuevoUsuario(String tipo, String id, String nombre,
                                         String apellido, String contrasenia, String adicional)
            throws DataAccessException {
        if (id == null || id.trim().isEmpty()) {
            id = generarIdPorRol(tipo);
        }
        Usuario nuevo;
        switch (tipo.toLowerCase()) {
            case "estudiante":
                if (adicional == null || adicional.trim().isEmpty())
                    throw new IllegalArgumentException("Grado requerido para estudiante");
                nuevo = new Estudiante(id, nombre, apellido, contrasenia, adicional);
                break;
            case "docente":
                nuevo = new Docente(id, nombre, apellido, contrasenia);
                break;
            case "coordinador":
                nuevo = new Coordinador(id, nombre, apellido, contrasenia);
                break;
            default:
                throw new IllegalArgumentException("Tipo de usuario inválido: " + tipo);
        }
        usuarioDAO.guardarUsuario(nuevo);
        return nuevo;
    }

    /**
     * Cambia el estado activo/inactivo de un usuario.
     *
     * @param idUsuario Identificador del usuario
     * @param activo    Nuevo estado (true = activo, false = inactivo)
     * @return true si se modificó correctamente
     * @throws DataAccessException si falla la búsqueda o actualización
     */
    public boolean modificarEstadoActivo(String idUsuario, boolean activo) throws DataAccessException {
        Usuario u = usuarioDAO.buscarUsuarioPorId(idUsuario);
        if (u == null) return false;
        u.setActivo(activo);
        usuarioDAO.guardarUsuario(u);
        return true;
    }

    /**
     * Lista todos los usuarios de un rol específico.
     *
     * @param rol Rol a filtrar
     * @return Lista de usuarios
     * @throws DataAccessException si falla la consulta
     */
    public List<Usuario> listarUsuariosPorRol(RolUsuario rol) throws DataAccessException {
        return usuarioDAO.listarUsuariosPorRol(rol);
    }

    /**
     * Obtiene todos los estudiantes (método conveniente para vistas).
     *
     * @return Lista de estudiantes
     * @throws DataAccessException si falla la consulta
     */
    public List<Estudiante> listarEstudiantes() throws DataAccessException {
        return usuarioDAO.listarEstudiantes();
    }

    /**
     * Busca estudiantes por grado.
     *
     * @param grado Grado a filtrar
     * @return Lista de estudiantes de ese grado
     * @throws DataAccessException si falla la consulta
     */
    public List<Estudiante> listarEstudiantesPorGrado(String grado) throws DataAccessException {
        return usuarioDAO.listarEstudiantesPorGrado(grado);
    }

    /**
     * Asigna la bandera de representante a un estudiante.
     *
     * @param estudianteId   ID del estudiante
     * @param esRepresentante true para marcar como representante
     * @throws DataAccessException si falla la actualización
     */
    public void asignarRepresentante(String estudianteId, boolean esRepresentante) throws DataAccessException {
        Usuario u = usuarioDAO.buscarUsuarioPorId(estudianteId);
        if (u instanceof Estudiante) {
            ((Estudiante) u).setEsRepresentante(esRepresentante);
            usuarioDAO.guardarUsuario(u);
        } else {
            throw new IllegalArgumentException("El ID no corresponde a un estudiante");
        }
    }

    /**
     * Asigna la bandera de docente de grupo a un docente.
     *
     * @param docenteId      ID del docente
     * @param esDocenteGrupo true para marcar como docente de grupo
     * @param cursoDir       Curso que dirige (opcional, puede ser null)
     * @throws DataAccessException si falla la actualización
     */
    public void asignarDocenteDeGrupo(String docenteId, boolean esDocenteGrupo, String cursoDir) throws DataAccessException {
        Usuario u = usuarioDAO.buscarUsuarioPorId(docenteId);
        if (u instanceof Docente) {
            Docente d = (Docente) u;
            d.setEsDocenteDeGrupo(esDocenteGrupo);
            if (cursoDir != null && !cursoDir.isEmpty()) {
                d.setCursoDireccionGrupo(cursoDir);
            }
            usuarioDAO.guardarUsuario(d);
        } else {
            throw new IllegalArgumentException("El ID no corresponde a un docente");
        }
    }
}