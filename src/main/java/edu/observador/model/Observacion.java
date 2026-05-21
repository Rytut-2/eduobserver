// Archivo: src/edu/observador/model/Observacion.java
package edu.observador.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Clase abstracta que representa una observación realizada a un estudiante.
 * Puede ser de tipo académica o disciplinaria.
 * Una observación está asociada a un estudiante y a un usuario creador (docente o coordinador).
 */
public abstract class Observacion {

    protected String id;
    protected String descripcion;
    protected LocalDate fecha;
    protected Estudiante estudiante;
    protected Usuario creador;
    protected boolean anulada;
    protected String justificacionAnulacion;

    /**
     * Constructor para crear una nueva observación.
     *
     * @param id          Identificador único de la observación (si es null se genera automático)
     * @param descripcion Descripción detallada de la observación
     * @param fecha       Fecha en que se realiza la observación
     * @param estudiante  Estudiante al que se le asigna la observación
     * @param creador     Usuario que crea la observación (docente o coordinador)
     * @throws IllegalArgumentException si descripción, fecha, estudiante o creador son nulos
     */
    public Observacion(String id, String descripcion, LocalDate fecha,
                       Estudiante estudiante, Usuario creador) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción no puede ser nula o vacía");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula");
        }
        if (estudiante == null) {
            throw new IllegalArgumentException("El estudiante no puede ser nulo");
        }
        if (creador == null) {
            throw new IllegalArgumentException("El creador no puede ser nulo");
        }

        this.id = (id == null || id.trim().isEmpty()) ? UUID.randomUUID().toString() : id;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.estudiante = estudiante;
        this.creador = creador;
        this.anulada = false;
        this.justificacionAnulacion = null;
    }

    /**
     * Verifica si la observación es válida (no anulada).
     *
     * @return true si no está anulada, false en caso contrario
     */
    public boolean esValida() {
        return !anulada;
    }

    /**
     * Anula la observación. Solo puede ser llamado por un coordinador.
     *
     * @param justificacion Motivo de la anulación
     * @throws IllegalArgumentException si la justificación es nula o vacía
     * @throws IllegalStateException    si la observación ya estaba anulada
     */
    public void anular(String justificacion) {
        if (justificacion == null || justificacion.trim().isEmpty()) {
            throw new IllegalArgumentException("La justificación de anulación es obligatoria");
        }
        if (anulada) {
            throw new IllegalStateException("La observación ya estaba anulada");
        }
        this.anulada = true;
        this.justificacionAnulacion = justificacion;
    }

    /**
     * Obtiene el tipo específico de observación (para uso en UI).
     *
     * @return "Académica" o "Disciplinaria"
     */
    public abstract String getTipoObservacion();

    // Getters y Setters

    public String getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción no puede ser nula o vacía");
        }
        this.descripcion = descripcion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula");
        }
        this.fecha = fecha;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public Usuario getCreador() {
        return creador;
    }

    public boolean isAnulada() {
        return anulada;
    }

    public String getJustificacionAnulacion() {
        return justificacionAnulacion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Observacion that = (Observacion) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Observacion{id='%s', fecha=%s, estudiante=%s, anulada=%s}",
                id, fecha, estudiante.getNombreCompleto(), anulada);
    }
}