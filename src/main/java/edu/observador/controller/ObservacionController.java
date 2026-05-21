// Archivo: src/edu/observador/controller/ObservacionController.java
package edu.observador.controller;

import edu.observador.data.DataAccessException;
import edu.observador.data.ObservadorDAO;
import edu.observador.model.*;
import edu.observador.model.enums.EstadoPeticion;
import edu.observador.model.enums.NivelSeveridad;
import edu.observador.model.enums.TipoAcademia;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador para la gestión de observaciones y peticiones de revisión.
 */
public class ObservacionController {

    private final ObservadorDAO observacionDAO;

    public ObservacionController(ObservadorDAO dao) {
        this.observacionDAO = dao;
    }

    // ==================== Observaciones ====================

    public Observacion registrarDisciplinaria(String estudianteId, String creadorId,
                                              String descripcion, NivelSeveridad severidad)
            throws DataAccessException {
        Estudiante estudiante = (Estudiante) observacionDAO.buscarUsuarioPorId(estudianteId);
        Usuario creador = observacionDAO.buscarUsuarioPorId(creadorId);
        if (estudiante == null || creador == null) {
            throw new IllegalArgumentException("Estudiante o creador no encontrado");
        }
        if (!(creador instanceof Docente || creador instanceof Coordinador)) {
            throw new IllegalArgumentException("Solo docentes o coordinadores pueden crear observaciones");
        }
        ObservacionDisciplinaria obs = new ObservacionDisciplinaria(
                null, descripcion, LocalDate.now(), estudiante, creador, severidad);
        estudiante.agregarObservacion(obs);
        if (creador instanceof Docente) {
            ((Docente) creador).agregarObservacionCreada(obs);
        } else if (creador instanceof Coordinador) {
            ((Coordinador) creador).agregarObservacionCreada(obs);
        }
        observacionDAO.guardarObservacion(obs);
        return obs;
    }

    public Observacion registrarAcademica(String estudianteId, String creadorId,
                                          String descripcion, TipoAcademia tipo, String detalleAcademico)
            throws DataAccessException {
        Estudiante estudiante = (Estudiante) observacionDAO.buscarUsuarioPorId(estudianteId);
        Usuario creador = observacionDAO.buscarUsuarioPorId(creadorId);
        if (estudiante == null || creador == null) {
            throw new IllegalArgumentException("Estudiante o creador no encontrado");
        }
        ObservacionAcademica obs = new ObservacionAcademica(
                null, descripcion, LocalDate.now(), estudiante, creador, tipo, detalleAcademico);
        estudiante.agregarObservacion(obs);
        if (creador instanceof Docente) {
            ((Docente) creador).agregarObservacionCreada(obs);
        } else if (creador instanceof Coordinador) {
            ((Coordinador) creador).agregarObservacionCreada(obs);
        }
        observacionDAO.guardarObservacion(obs);
        return obs;
    }

    public void anularObservacion(String observacionId, String justificacion, Usuario solicitante)
            throws DataAccessException {
        if (!(solicitante instanceof Coordinador)) {
            throw new SecurityException("Solo el coordinador puede anular observaciones");
        }
        Observacion obs = observacionDAO.buscarObservacionPorId(observacionId);
        if (obs == null) {
            throw new IllegalArgumentException("Observación no encontrada");
        }
        obs.anular(justificacion);
        observacionDAO.actualizarObservacion(obs);
    }

    public List<Observacion> getHistorialEstudiante(String estudianteId, boolean soloActivas)
            throws DataAccessException {
        List<Observacion> todas = observacionDAO.cargarHistorialEstudiante(estudianteId);
        if (soloActivas) {
            return todas.stream().filter(Observacion::esValida).toList();
        }
        return todas;
    }

    public String calcularAlertaEstudiante(String estudianteId) throws DataAccessException {
        Estudiante estudiante = (Estudiante) observacionDAO.buscarUsuarioPorId(estudianteId);
        if (estudiante == null) throw new IllegalArgumentException("Estudiante no encontrado");
        return estudiante.calcularNivelAlerta();
    }

    public List<Estudiante> obtenerEstudiantesMasObservaciones(int limite) throws DataAccessException {
        List<Estudiante> estudiantes = observacionDAO.listarEstudiantes();
        estudiantes.sort((e1, e2) -> {
            try {
                int s1 = observacionDAO.cargarHistorialEstudiante(e1.getId()).size();
                int s2 = observacionDAO.cargarHistorialEstudiante(e2.getId()).size();
                return Integer.compare(s2, s1);
            } catch (DataAccessException e) {
                return 0;
            }
        });
        return estudiantes.stream().limit(limite).toList();
    }

    public String generarReporteGeneral(Usuario solicitante) throws DataAccessException {
        if (!(solicitante instanceof Coordinador)) {
            throw new SecurityException("Solo el coordinador puede generar reportes generales");
        }
        List<Estudiante> estudiantes = observacionDAO.listarEstudiantes();
        for (Estudiante e : estudiantes) {
            List<Observacion> obs = observacionDAO.cargarHistorialEstudiante(e.getId());
            for (Observacion o : obs) {
                e.agregarObservacion(o);
            }
        }
        Coordinador coord = (Coordinador) solicitante;
        return coord.generarReporteGeneral(estudiantes);
    }

    // ==================== Peticiones de Revisión ====================

    /**
     * Crea una petición de revisión sobre una observación.
     * Solo puede ser usado por un estudiante representante.
     */
    public void crearPeticionRevision(String observacionId, String motivo, Usuario solicitante)
            throws DataAccessException {
        if (!(solicitante instanceof Estudiante) || !((Estudiante) solicitante).isEsRepresentante()) {
            throw new SecurityException("Solo los representantes pueden solicitar revisiones");
        }
        Observacion obs = observacionDAO.buscarObservacionPorId(observacionId);
        if (obs == null) {
            throw new IllegalArgumentException("Observación no encontrada");
        }
        // Opcional: verificar que la observación pertenezca a un estudiante del mismo grado que el representante
        PeticionRevision peticion = new PeticionRevision(null, obs, motivo);
        observacionDAO.guardarPeticion(peticion);
    }

    /**
     * Lista todas las peticiones pendientes (para coordinador).
     */
    public List<PeticionRevision> listarPeticionesPendientes() throws DataAccessException {
        return observacionDAO.listarPeticionesPorEstado(EstadoPeticion.PENDIENTE);
    }

    /**
     * Aprueba una petición de revisión. Solo coordinador.
     * Opcionalmente, se puede anular la observación asociada.
     */
    public void aprobarPeticion(String peticionId, Usuario coordinador) throws DataAccessException {
        if (!(coordinador instanceof Coordinador)) {
            throw new SecurityException("Solo el coordinador puede aprobar peticiones");
        }
        // Obtener la petición (necesitaríamos un método buscarPeticionPorId, aquí simplificamos)
        // Por ahora, solo actualizamos estado. Para anular la observación, se requiere cargar la petición.
        observacionDAO.actualizarEstadoPeticion(peticionId, EstadoPeticion.APROBADA);
        // Opcional: buscar la petición y anular la observación
        // PeticionRevision p = observacionDAO.buscarPeticionPorId(peticionId);
        // if (p != null) anularObservacion(p.getObservacionImplicada().getId(), "Aprobada por revisión", coordinador);
    }

    /**
     * Rechaza una petición de revisión. Solo coordinador.
     */
    public void rechazarPeticion(String peticionId, Usuario coordinador) throws DataAccessException {
        if (!(coordinador instanceof Coordinador)) {
            throw new SecurityException("Solo el coordinador puede rechazar peticiones");
        }
        observacionDAO.actualizarEstadoPeticion(peticionId, EstadoPeticion.RECHAZADA);
    }
}