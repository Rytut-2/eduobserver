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

public class ObservacionController {

    private final ObservadorDAO observacionDAO;

    public ObservacionController(ObservadorDAO dao) {
        this.observacionDAO = dao;
    }

    // ==================== Registro de observaciones ====================

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

    // ==================== Consulta de historial y alertas ====================

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

    // ==================== Estadísticas para tarjetas (Coordinador) ====================

    public int getTotalObservaciones() throws DataAccessException {
        List<Estudiante> estudiantes = observacionDAO.listarEstudiantes();
        int total = 0;
        for (Estudiante e : estudiantes) {
            total += observacionDAO.cargarHistorialEstudiante(e.getId()).size();
        }
        return total;
    }

    public int getTotalObservacionesAcademicas() throws DataAccessException {
        List<Estudiante> estudiantes = observacionDAO.listarEstudiantes();
        int total = 0;
        for (Estudiante e : estudiantes) {
            total += (int) observacionDAO.cargarHistorialEstudiante(e.getId()).stream()
                    .filter(o -> o instanceof ObservacionAcademica).count();
        }
        return total;
    }

    public int getTotalObservacionesDisciplinarias() throws DataAccessException {
        List<Estudiante> estudiantes = observacionDAO.listarEstudiantes();
        int total = 0;
        for (Estudiante e : estudiantes) {
            total += (int) observacionDAO.cargarHistorialEstudiante(e.getId()).stream()
                    .filter(o -> o instanceof ObservacionDisciplinaria).count();
        }
        return total;
    }

    // ==================== Estadísticas para estudiante ====================

    public int getTotalObservacionesEstudiante(String estudianteId, boolean soloActivas) throws DataAccessException {
        return getHistorialEstudiante(estudianteId, soloActivas).size();
    }

    public int getTotalAcademicasEstudiante(String estudianteId, boolean soloActivas) throws DataAccessException {
        return (int) getHistorialEstudiante(estudianteId, soloActivas).stream()
                .filter(o -> o instanceof ObservacionAcademica).count();
    }

    public int getTotalDisciplinariasEstudiante(String estudianteId, boolean soloActivas) throws DataAccessException {
        return (int) getHistorialEstudiante(estudianteId, soloActivas).stream()
                .filter(o -> o instanceof ObservacionDisciplinaria).count();
    }

    // ==================== Peticiones de revisión ====================

    public void crearPeticionRevision(String observacionId, String motivo, Usuario solicitante)
            throws DataAccessException {
        if (!(solicitante instanceof Estudiante) || !((Estudiante) solicitante).isEsRepresentante()) {
            throw new SecurityException("Solo los representantes pueden solicitar revisiones");
        }
        Observacion obs = observacionDAO.buscarObservacionPorId(observacionId);
        if (obs == null) throw new IllegalArgumentException("Observación no encontrada");
        PeticionRevision peticion = new PeticionRevision(null, obs, motivo);
        observacionDAO.guardarPeticion(peticion);
    }

    public List<PeticionRevision> listarPeticionesPendientes() throws DataAccessException {
        return observacionDAO.listarPeticionesPorEstado(EstadoPeticion.PENDIENTE);
    }

    public void aprobarPeticion(String peticionId, Usuario coordinador) throws DataAccessException {
        if (!(coordinador instanceof Coordinador)) {
            throw new SecurityException("Solo el coordinador puede aprobar peticiones");
        }
        observacionDAO.actualizarEstadoPeticion(peticionId, EstadoPeticion.APROBADA);
        // Opcional: anular la observación automáticamente
    }

    public void rechazarPeticion(String peticionId, Usuario coordinador) throws DataAccessException {
        if (!(coordinador instanceof Coordinador)) {
            throw new SecurityException("Solo el coordinador puede rechazar peticiones");
        }
        observacionDAO.actualizarEstadoPeticion(peticionId, EstadoPeticion.RECHAZADA);
    }

    public int getPeticionesRevisadas() throws DataAccessException {
        return observacionDAO.listarPeticionesPorEstado(EstadoPeticion.APROBADA).size() +
                observacionDAO.listarPeticionesPorEstado(EstadoPeticion.RECHAZADA).size();
    }

    public int getPeticionesPendientes() throws DataAccessException {
        return observacionDAO.listarPeticionesPorEstado(EstadoPeticion.PENDIENTE).size();
    }
}