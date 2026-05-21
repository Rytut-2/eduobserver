// Archivo: src/edu/observador/model/ObservacionDisciplinaria.java
package edu.observador.model;

import edu.observador.model.enums.NivelSeveridad;
import java.time.LocalDate;

/**
 * Representa una observación de tipo disciplinario, con un nivel de severidad
 * (LEVE, MODERADA, GRAVE).
 */
public class ObservacionDisciplinaria extends Observacion {

    private NivelSeveridad severidad;

    /**
     * Constructor para observación disciplinaria.
     *
     * @param id          Identificador único (puede ser null)
     * @param descripcion Descripción de la falta o comportamiento
     * @param fecha       Fecha de la observación
     * @param estudiante  Estudiante asociado
     * @param creador     Usuario creador
     * @param severidad   Nivel de severidad (LEVE, MODERADA, GRAVE)
     * @throws IllegalArgumentException si severidad es nulo
     */
    public ObservacionDisciplinaria(String id, String descripcion, LocalDate fecha,
                                    Estudiante estudiante, Usuario creador,
                                    NivelSeveridad severidad) {
        super(id, descripcion, fecha, estudiante, creador);
        if (severidad == null) {
            throw new IllegalArgumentException("La severidad no puede ser nula");
        }
        this.severidad = severidad;
    }

    @Override
    public String getTipoObservacion() {
        return "Disciplinaria";
    }

    public NivelSeveridad getSeveridad() {
        return severidad;
    }

    public void setSeveridad(NivelSeveridad severidad) {
        if (severidad == null) {
            throw new IllegalArgumentException("La severidad no puede ser nula");
        }
        this.severidad = severidad;
    }

    @Override
    public String toString() {
        return String.format("ObservacionDisciplinaria{id='%s', severidad=%s, estudiante=%s}",
                id, severidad, estudiante.getNombreCompleto());
    }
}