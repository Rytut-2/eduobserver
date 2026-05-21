// Archivo: src/edu/observador/controller/ObservacionController.java
package edu.observador.controller;

import edu.observador.data.DataAccessException;
import edu.observador.data.ObservadorDAO;
import edu.observador.model.*;
import edu.observador.model.enums.NivelSeveridad;
import edu.observador.model.enums.TipoAcademia;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador para la gestión de observaciones (académicas y disciplinarias):
 * creación, anulación, consulta de historial, alertas y reportes.
 */
public class ObservacionController {

    private final ObservadorDAO observacionDAO;

    public ObservacionController(ObservadorDAO dao) {
        this.observacionDAO = dao;
    }

    /**
     * Registra una observación disciplinaria para un estudiante.
     *
     * @param estudianteId ID del estudiante
     * @param creadorId    ID del usuario creador (docente o coordinador)
     * @param descripcion  Texto de la observación
     * @param severidad    Nivel de severidad
     * @return La observación creada
     * @throws DataAccessException si falla la persistencia
     */
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
        // Nota: El estudiante ya tiene la observación en memoria; la relación se persiste en la tabla observaciones.
        return obs;
    }

    /**
     * Registra una observación académica para un estudiante.
     *
     * @param estudianteId   ID del estudiante
     * @param creadorId      ID del creador
     * @param descripcion    Descripción general
     * @param tipo           Tipo de logro académico
     * @param detalleAcademico Detalle específico
     * @return Observación académica creada
     * @throws DataAccessException si falla persistencia
     */
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

    /**
     * Anula una observación. Solo el coordinador puede hacerlo.
     *
     * @param observacionId ID de la observación
     * @param justificacion Motivo de la anulación
     * @param solicitante   Usuario que intenta anular (debe ser Coordinador)
     * @throws DataAccessException si falla la actualización
     * @throws SecurityException    si el solicitante no es coordinador
     */
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

    /**
     * Obtiene el historial de observaciones de un estudiante.
     *
     * @param estudianteId ID del estudiante
     * @param soloActivas  true para filtrar solo no anuladas
     * @return Lista de observaciones
     * @throws DataAccessException si falla la consulta
     */
    public List<Observacion> getHistorialEstudiante(String estudianteId, boolean soloActivas)
            throws DataAccessException {
        List<Observacion> todas = observacionDAO.cargarHistorialEstudiante(estudianteId);
        if (soloActivas) {
            return todas.stream().filter(Observacion::esValida).toList();
        }
        return todas;
    }

    /**
     * Calcula el nivel de alerta de un estudiante (Rojo, Amarillo, Verde).
     *
     * @param estudianteId ID del estudiante
     * @return String con el nivel
     * @throws DataAccessException si falla la consulta
     */
    public String calcularAlertaEstudiante(String estudianteId) throws DataAccessException {
        Estudiante estudiante = (Estudiante) observacionDAO.buscarUsuarioPorId(estudianteId);
        if (estudiante == null) throw new IllegalArgumentException("Estudiante no encontrado");
        return estudiante.calcularNivelAlerta();
    }

    /**
     * Obtiene la lista de estudiantes con mayor número de observaciones (para dashboard).
     *
     * @param limite Cantidad máxima de estudiantes a retornar
     * @return Lista de estudiantes ordenados desc por cantidad de observaciones
     * @throws DataAccessException si falla la consulta
     */
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

    /**
     * Genera un reporte general de convivencia. Solo el coordinador puede hacerlo.
     *
     * @param solicitante Usuario que solicita el reporte (debe ser coordinador)
     * @return Reporte en formato String
     * @throws DataAccessException si falla la consulta
     * @throws SecurityException    si el solicitante no es coordinador
     */
    public String generarReporteGeneral(Usuario solicitante) throws DataAccessException {
        if (!(solicitante instanceof Coordinador)) {
            throw new SecurityException("Solo el coordinador puede generar reportes generales");
        }
        List<Estudiante> estudiantes = observacionDAO.listarEstudiantes();
        // Cargar historial de cada estudiante para que el método generarReporteGeneral los use
        for (Estudiante e : estudiantes) {
            List<Observacion> obs = observacionDAO.cargarHistorialEstudiante(e.getId());
            for (Observacion o : obs) {
                e.agregarObservacion(o); // poblamos el historial en memoria
            }
        }
        Coordinador coord = (Coordinador) solicitante;
        return coord.generarReporteGeneral(estudiantes);
    }
}