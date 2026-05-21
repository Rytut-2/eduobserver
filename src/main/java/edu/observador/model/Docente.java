// Archivo: src/edu/observador/model/Docente.java
package edu.observador.model;

import edu.observador.model.enums.RolUsuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Clase que representa a un docente en el sistema.
 * Puede tener cursos asignados, ser docente de grupo (con capacidad de ver
 * historial completo de sus estudiantes) y crear observaciones.
 */
public class Docente extends Usuario {

    private List<String> cursosAsignados;
    private String cursoDireccionGrupo;
    private boolean esDocenteDeGrupo;
    private List<Observacion> observacionesCreadas;

    /**
     * Constructor para crear un docente.
     *
     * @param id          Identificador único (ej. DOC-001)
     * @param nombre      Nombre del docente
     * @param apellido    Apellido del docente
     * @param contrasenia Contraseña de acceso
     */
    public Docente(String id, String nombre, String apellido, String contrasenia) {
        super(id, nombre, apellido, contrasenia, RolUsuario.DOCENTE);
        this.cursosAsignados = new ArrayList<>();
        this.cursoDireccionGrupo = null;
        this.esDocenteDeGrupo = false;
        this.observacionesCreadas = new ArrayList<>();
    }

    /**
     * Verifica si el docente puede ver el historial completo de los estudiantes
     * a su cargo. Solo los docentes de grupo tienen este permiso (RF-13).
     *
     * @return true si es docente de grupo, false en caso contrario
     */
    public boolean puedeVerHistorialCompleto() {
        return esDocenteDeGrupo;
    }

    /**
     * Agrega un curso a la lista de cursos asignados al docente.
     *
     * @param curso Identificador del curso (ej. "10A", "11B")
     * @throws IllegalArgumentException si el curso es nulo o vacío
     */
    public void agregarCursoAsignado(String curso) {
        if (curso == null || curso.trim().isEmpty()) {
            throw new IllegalArgumentException("El curso no puede ser nulo o vacío");
        }
        if (!cursosAsignados.contains(curso)) {
            cursosAsignados.add(curso);
        }
    }

    /**
     * Elimina un curso de la lista de cursos asignados.
     *
     * @param curso Identificador del curso
     * @return true si se eliminó, false si no existía
     */
    public boolean eliminarCursoAsignado(String curso) {
        return cursosAsignados.remove(curso);
    }

    /**
     * Registra una observación creada por el docente.
     *
     * @param observacion Observación creada
     * @throws IllegalArgumentException si la observación es nula
     */
    public void agregarObservacionCreada(Observacion observacion) {
        if (observacion == null) {
            throw new IllegalArgumentException("La observación no puede ser nula");
        }
        if (!observacion.getCreador().equals(this)) {
            throw new IllegalArgumentException("El creador de la observación no coincide con este docente");
        }
        observacionesCreadas.add(observacion);
    }

    @Override
    public String obtenerInfoEspecifica() {
        return String.format("Docente - Docente de grupo: %s, Cursos asignados: %s, Curso que dirige: %s",
                esDocenteDeGrupo ? "Sí" : "No",
                cursosAsignados,
                cursoDireccionGrupo != null ? cursoDireccionGrupo : "Ninguno");
    }

    // Getters y Setters

    public List<String> getCursosAsignados() {
        return new ArrayList<>(cursosAsignados);
    }

    public void setCursosAsignados(List<String> cursosAsignados) {
        this.cursosAsignados = cursosAsignados != null ? new ArrayList<>(cursosAsignados) : new ArrayList<>();
    }

    public String getCursoDireccionGrupo() {
        return cursoDireccionGrupo;
    }

    public void setCursoDireccionGrupo(String cursoDireccionGrupo) {
        this.cursoDireccionGrupo = cursoDireccionGrupo;
        // Si se asigna un curso de dirección, automáticamente se convierte en docente de grupo
        if (cursoDireccionGrupo != null && !cursoDireccionGrupo.trim().isEmpty()) {
            this.esDocenteDeGrupo = true;
        }
    }

    public boolean isEsDocenteDeGrupo() {
        return esDocenteDeGrupo;
    }

    public void setEsDocenteDeGrupo(boolean esDocenteDeGrupo) {
        this.esDocenteDeGrupo = esDocenteDeGrupo;
    }

    public List<Observacion> getObservacionesCreadas() {
        return new ArrayList<>(observacionesCreadas);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Docente docente = (Docente) o;
        return Objects.equals(cursosAsignados, docente.cursosAsignados);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), cursosAsignados);
    }

    @Override
    public String toString() {
        return String.format("Docente{id='%s', nombre='%s %s', docenteDeGrupo=%s, cursoDir='%s'}",
                getId(), getNombre(), getApellido(), esDocenteDeGrupo, cursoDireccionGrupo);
    }
}