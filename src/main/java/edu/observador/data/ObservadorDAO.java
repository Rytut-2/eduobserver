// Archivo: src/edu/observador/data/ObservadorDAO.java
package edu.observador.data;

import edu.observador.model.Estudiante;
import edu.observador.model.Observacion;
import edu.observador.model.PeticionRevision;
import edu.observador.model.enums.RolUsuario;
import edu.observador.model.Usuario;
import edu.observador.model.enums.EstadoPeticion;

import java.util.List;

/**
 * Interfaz DAO (Data Access Object) para el sistema EduObservador.
 * Define las operaciones básicas de persistencia para usuarios,
 * observaciones y peticiones de revisión.
 */
public interface ObservadorDAO {

    // ==================== Operaciones Usuario ====================

    /**
     * Guarda un usuario en la base de datos (insert o update).
     * @param usuario Usuario a guardar
     * @throws DataAccessException si ocurre un error de acceso a datos
     */
    void guardarUsuario(Usuario usuario) throws DataAccessException;

    /**
     * Busca un usuario por su ID.
     * @param id Identificador del usuario
     * @return Usuario encontrado, o null si no existe
     * @throws DataAccessException si ocurre un error de acceso a datos
     */
    Usuario buscarUsuarioPorId(String id) throws DataAccessException;

    /**
     * Lista todos los usuarios de un rol específico.
     * @param rol Rol a filtrar (COORDINADOR, DOCENTE, ESTUDIANTE)
     * @return Lista de usuarios con ese rol
     * @throws DataAccessException si ocurre un error de acceso a datos
     */
    List<Usuario> listarUsuariosPorRol(RolUsuario rol) throws DataAccessException;

    /**
     * Lista todos los estudiantes (método de conveniencia).
     * @return Lista de todos los estudiantes
     * @throws DataAccessException si ocurre un error de acceso a datos
     */
    List<Estudiante> listarEstudiantes() throws DataAccessException;

    /**
     * Lista estudiantes por grado.
     * @param grado Grado a filtrar
     * @return Lista de estudiantes de ese grado
     * @throws DataAccessException si ocurre un error de acceso a datos
     */
    List<Estudiante> listarEstudiantesPorGrado(String grado) throws DataAccessException;

    // ==================== Operaciones Observación ====================

    /**
     * Guarda una observación en la base de datos.
     * @param observacion Observación a guardar
     * @throws DataAccessException si ocurre un error de acceso a datos
     */
    void guardarObservacion(Observacion observacion) throws DataAccessException;

    /**
     * Carga el historial completo de observaciones de un estudiante.
     * @param estudianteId ID del estudiante
     * @return Lista de observaciones del estudiante
     * @throws DataAccessException si ocurre un error de acceso a datos
     */
    List<Observacion> cargarHistorialEstudiante(String estudianteId) throws DataAccessException;

    /**
     * Busca una observación por su ID.
     * @param id ID de la observación
     * @return Observación encontrada, o null
     * @throws DataAccessException si ocurre un error de acceso a datos
     */
    Observacion buscarObservacionPorId(String id) throws DataAccessException;

    /**
     * Actualiza una observación (ej. anulación).
     * @param observacion Observación con datos actualizados
     * @throws DataAccessException si ocurre un error de acceso a datos
     */
    void actualizarObservacion(Observacion observacion) throws DataAccessException;

    // ==================== Operaciones Petición de Revisión ====================

    /**
     * Guarda una petición de revisión en la base de datos.
     * @param peticion Petición a guardar
     * @throws DataAccessException si ocurre un error de acceso a datos
     */
    void guardarPeticion(PeticionRevision peticion) throws DataAccessException;

    /**
     * Lista las peticiones de revisión según su estado.
     * @param estado Estado de la petición (PENDIENTE, APROBADA, RECHAZADA)
     * @return Lista de peticiones con ese estado
     * @throws DataAccessException si ocurre un error de acceso a datos
     */
    List<PeticionRevision> listarPeticionesPorEstado(EstadoPeticion estado) throws DataAccessException;

    /**
     * Lista las peticiones de revisión realizadas sobre observaciones de un estudiante específico.
     * @param estudianteId ID del estudiante cuyas observaciones fueron impugnadas
     * @return Lista de peticiones asociadas a las observaciones de ese estudiante
     * @throws DataAccessException si ocurre un error de acceso a datos
     */
    List<PeticionRevision> listarPeticionesPorEstudiante(String estudianteId) throws DataAccessException;

    /**
     * Actualiza el estado de una petición de revisión.
     * @param peticionId ID de la petición
     * @param nuevoEstado Nuevo estado (APROBADA, RECHAZADA)
     * @throws DataAccessException si ocurre un error de acceso a datos
     */
    void actualizarEstadoPeticion(String peticionId, EstadoPeticion nuevoEstado) throws DataAccessException;

    // ==================== Operaciones de inicialización ====================

    /**
     * Inicializa la base de datos creando las tablas si no existen.
     * @throws DataAccessException si ocurre un error de acceso a datos
     */
    void inicializarBaseDatos() throws DataAccessException;

    /**
     * Cierra los recursos de la conexión si es necesario.
     */
    void cerrar();
}