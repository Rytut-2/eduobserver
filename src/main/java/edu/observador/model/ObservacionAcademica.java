// Archivo: src/edu/observador/model/ObservacionAcademica.java
package edu.observador.model;

import edu.observador.model.enums.TipoAcademia;
import java.time.LocalDate;

/**
 * Representa una observación de tipo académico, destacando logros o talentos
 * del estudiante según el tipo de academia definido.
 */
public class ObservacionAcademica extends Observacion {

    private TipoAcademia tipo;
    private String detalleAcademico;

    /**
     * Constructor para observación académica.
     *
     * @param id               Identificador único (puede ser null)
     * @param descripcion      Descripción general
     * @param fecha            Fecha de la observación
     * @param estudiante       Estudiante asociado
     * @param creador          Usuario creador
     * @param tipo             Tipo de logro académico (LOGRO_DESTACADO, TALENTO_EXCEPCIONAL, etc.)
     * @param detalleAcademico Detalle específico del logro o talento
     * @throws IllegalArgumentException si tipo o detalle son nulos/vacíos
     */
    public ObservacionAcademica(String id, String descripcion, LocalDate fecha,
                                Estudiante estudiante, Usuario creador,
                                TipoAcademia tipo, String detalleAcademico) {
        super(id, descripcion, fecha, estudiante, creador);
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo académico no puede ser nulo");
        }
        if (detalleAcademico == null || detalleAcademico.trim().isEmpty()) {
            throw new IllegalArgumentException("El detalle académico no puede ser nulo o vacío");
        }
        this.tipo = tipo;
        this.detalleAcademico = detalleAcademico;
    }

    @Override
    public String getTipoObservacion() {
        return "Académica";
    }

    public TipoAcademia getTipo() {
        return tipo;
    }

    public void setTipo(TipoAcademia tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo académico no puede ser nulo");
        }
        this.tipo = tipo;
    }

    public String getDetalleAcademico() {
        return detalleAcademico;
    }

    public void setDetalleAcademico(String detalleAcademico) {
        if (detalleAcademico == null || detalleAcademico.trim().isEmpty()) {
            throw new IllegalArgumentException("El detalle académico no puede ser nulo o vacío");
        }
        this.detalleAcademico = detalleAcademico;
    }

    @Override
    public String toString() {
        return String.format("ObservacionAcademica{id='%s', tipo=%s, detalle='%s', estudiante=%s}",
                id, tipo, detalleAcademico, estudiante.getNombreCompleto());
    }
}