// Archivo: src/edu/observador/model/PeticionRevision.java
package edu.observador.model;

import edu.observador.model.enums.EstadoPeticion;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa una petición de revisión de una observación, realizada por un estudiante
 * o representante. El estado puede ser PENDIENTE, APROBADA o RECHAZADA.
 */
public class PeticionRevision {

    private String id;
    private Observacion observacionImplicada;
    private String motivoAplicacion;
    private LocalDate fechaPeticion;
    private EstadoPeticion estado;

    /**
     * Constructor para crear una nueva petición de revisión.
     *
     * @param id                   Identificador único (si es null se genera automático)
     * @param observacionImplicada Observación que se desea revisar
     * @param motivoAplicacion     Razón por la que se solicita la revisión
     * @throws IllegalArgumentException si observación o motivo son nulos/vacíos
     */
    public PeticionRevision(String id, Observacion observacionImplicada, String motivoAplicacion) {
        if (observacionImplicada == null) {
            throw new IllegalArgumentException("La observación implicada no puede ser nula");
        }
        if (motivoAplicacion == null || motivoAplicacion.trim().isEmpty()) {
            throw new IllegalArgumentException("El motivo de aplicación no puede ser nulo o vacío");
        }
        this.id = (id == null || id.trim().isEmpty()) ? UUID.randomUUID().toString() : id;
        this.observacionImplicada = observacionImplicada;
        this.motivoAplicacion = motivoAplicacion;
        this.fechaPeticion = LocalDate.now();
        this.estado = EstadoPeticion.PENDIENTE;
    }

    /**
     * Aprueba la petición. Solo puede ser llamado por un coordinador.
     *
     * @throws IllegalStateException si la petición ya no está pendiente
     */
    public void aprobar() {
        if (estado != EstadoPeticion.PENDIENTE) {
            throw new IllegalStateException("Solo se puede aprobar una petición pendiente");
        }
        this.estado = EstadoPeticion.APROBADA;
    }

    /**
     * Rechaza la petición. Solo puede ser llamado por un coordinador.
     *
     * @throws IllegalStateException si la petición ya no está pendiente
     */
    public void rechazar() {
        if (estado != EstadoPeticion.PENDIENTE) {
            throw new IllegalStateException("Solo se puede rechazar una petición pendiente");
        }
        this.estado = EstadoPeticion.RECHAZADA;
    }

    // Getters y Setters

    public String getId() {
        return id;
    }

    public Observacion getObservacionImplicada() {
        return observacionImplicada;
    }

    public String getMotivoAplicacion() {
        return motivoAplicacion;
    }

    public LocalDate getFechaPeticion() {
        return fechaPeticion;
    }

    public EstadoPeticion getEstado() {
        return estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PeticionRevision that = (PeticionRevision) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("PeticionRevision{id='%s', observacion=%s, estado=%s, fecha=%s}",
                id, observacionImplicada.getId(), estado, fechaPeticion);
    }
}