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
     * Constructor para crear una nueva petición de revisión (nuevo registro).
     * Genera un ID automático si es nulo, y establece fecha actual y estado PENDIENTE.
     *
     * @param id                   Identificador único (puede ser null)
     * @param observacionImplicada Observación que se desea revisar
     * @param motivoAplicacion     Razón por la que se solicita la revisión
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
     * Constructor completo (para carga desde base de datos).
     * Permite establecer todos los campos, incluyendo fecha y estado.
     *
     * @param id                   Identificador único
     * @param observacionImplicada Observación implicada
     * @param motivoAplicacion     Motivo de la petición
     * @param fechaPeticion        Fecha de la petición
     * @param estado               Estado de la petición
     */
    public PeticionRevision(String id, Observacion observacionImplicada, String motivoAplicacion,
                            LocalDate fechaPeticion, EstadoPeticion estado) {
        if (observacionImplicada == null) {
            throw new IllegalArgumentException("La observación implicada no puede ser nula");
        }
        if (motivoAplicacion == null || motivoAplicacion.trim().isEmpty()) {
            throw new IllegalArgumentException("El motivo de aplicación no puede ser nulo o vacío");
        }
        if (fechaPeticion == null) {
            throw new IllegalArgumentException("La fecha de petición no puede ser nula");
        }
        if (estado == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo");
        }
        this.id = (id == null || id.trim().isEmpty()) ? UUID.randomUUID().toString() : id;
        this.observacionImplicada = observacionImplicada;
        this.motivoAplicacion = motivoAplicacion;
        this.fechaPeticion = fechaPeticion;
        this.estado = estado;
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

    // Getters y Setters (incluyendo setters para fecha y estado, usados por el DAO)

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Observacion getObservacionImplicada() {
        return observacionImplicada;
    }

    public void setObservacionImplicada(Observacion observacionImplicada) {
        this.observacionImplicada = observacionImplicada;
    }

    public String getMotivoAplicacion() {
        return motivoAplicacion;
    }

    public void setMotivoAplicacion(String motivoAplicacion) {
        this.motivoAplicacion = motivoAplicacion;
    }

    public LocalDate getFechaPeticion() {
        return fechaPeticion;
    }

    public void setFechaPeticion(LocalDate fechaPeticion) {
        this.fechaPeticion = fechaPeticion;
    }

    public EstadoPeticion getEstado() {
        return estado;
    }

    public void setEstado(EstadoPeticion estado) {
        this.estado = estado;
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