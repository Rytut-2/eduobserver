// Archivo: src/edu/observador/model/Estudiante.java
package edu.observador.model;

import edu.observador.model.enums.NivelSeveridad;
import edu.observador.model.enums.RolUsuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Clase que representa a un estudiante del sistema.
 * Hereda de Usuario y puede tener observaciones académicas y disciplinarias.
 * Puede ser marcado como representante de clase.
 */
public class Estudiante extends Usuario {

    private String grado;
    private List<Observacion> historial;
    private boolean esRepresentante;

    /**
     * Constructor para crear un estudiante.
     *
     * @param id          Identificador único (ej. EST-001)
     * @param nombre      Nombre del estudiante
     * @param apellido    Apellido del estudiante
     * @param contrasenia Contraseña de acceso
     * @param grado       Grado o curso al que pertenece (ej. "10A")
     * @throws IllegalArgumentException si grado es nulo o vacío
     */
    public Estudiante(String id, String nombre, String apellido, String contrasenia, String grado) {
        super(id, nombre, apellido, contrasenia, RolUsuario.ESTUDIANTE);
        if (grado == null || grado.trim().isEmpty()) {
            throw new IllegalArgumentException("El grado no puede ser nulo o vacío");
        }
        this.grado = grado;
        this.historial = new ArrayList<>();
        this.esRepresentante = false;
    }

    /**
     * Agrega una observación al historial del estudiante.
     *
     * @param observacion La observación a agregar
     * @throws IllegalArgumentException si la observación es nula
     */
    public void agregarObservacion(Observacion observacion) {
        if (observacion == null) {
            throw new IllegalArgumentException("La observación no puede ser nula");
        }
        // Verificar que la observación esté asociada a este estudiante
        if (!observacion.getEstudiante().equals(this)) {
            throw new IllegalArgumentException("La observación no corresponde a este estudiante");
        }
        historial.add(observacion);
    }

    /**
     * Obtiene el historial de observaciones activas (no anuladas).
     *
     * @return Lista de observaciones activas
     */
    public List<Observacion> obtenerHistorialActivo() {
        return historial.stream()
                .filter(Observacion::esValida)
                .collect(Collectors.toList());
    }

    /**
     * Calcula el nivel de alerta del estudiante basado en sus observaciones disciplinarias activas.
     * - Nivel "Rojo": al menos una observación de severidad GRAVE.
     * - Nivel "Amarillo": al menos una observación de severidad MODERADA (sin GRAVE).
     * - Nivel "Verde": ninguna observación grave o moderada (solo leves o sin observaciones).
     *
     * @return "Rojo", "Amarillo" o "Verde"
     */
    public String calcularNivelAlerta() {
        boolean tieneGrave = false;
        boolean tieneModerada = false;

        for (Observacion obs : obtenerHistorialActivo()) {
            if (obs instanceof ObservacionDisciplinaria) {
                ObservacionDisciplinaria disc = (ObservacionDisciplinaria) obs;
                if (disc.getSeveridad() == NivelSeveridad.GRAVE) {
                    tieneGrave = true;
                    break; // Grave es máximo, salimos
                } else if (disc.getSeveridad() == NivelSeveridad.MODERADA) {
                    tieneModerada = true;
                }
            }
        }

        if (tieneGrave) {
            return "Rojo";
        } else if (tieneModerada) {
            return "Amarillo";
        } else {
            return "Verde";
        }
    }

    /**
     * Obtiene el historial completo (incluyendo anuladas).
     *
     * @return Lista de todas las observaciones
     */
    public List<Observacion> getHistorialCompleto() {
        return new ArrayList<>(historial);
    }

    /**
     * Verifica si el estudiante tiene observaciones activas.
     *
     * @return true si tiene al menos una observación activa
     */
    public boolean tieneObservacionesActivas() {
        return obtenerHistorialActivo().size() > 0;
    }

    /**
     * Elimina una observación del historial (solo para pruebas o limpieza).
     *
     * @param observacion Observación a eliminar
     * @return true si fue eliminada
     */
    public boolean eliminarObservacion(Observacion observacion) {
        return historial.remove(observacion);
    }

    @Override
    public String obtenerInfoEspecifica() {
        return String.format("Estudiante - Grado: %s, Representante: %s, Nivel Alerta: %s",
                grado, esRepresentante ? "Sí" : "No", calcularNivelAlerta());
    }

    // Getters y Setters

    public String getGrado() {
        return grado;
    }

    public void setGrado(String grado) {
        if (grado == null || grado.trim().isEmpty()) {
            throw new IllegalArgumentException("El grado no puede ser nulo o vacío");
        }
        this.grado = grado;
    }

    public boolean isEsRepresentante() {
        return esRepresentante;
    }

    public void setEsRepresentante(boolean esRepresentante) {
        this.esRepresentante = esRepresentante;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Estudiante that = (Estudiante) o;
        return Objects.equals(grado, that.grado);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), grado);
    }

    @Override
    public String toString() {
        return String.format("Estudiante{id='%s', nombre='%s %s', grado='%s', representante=%s}",
                getId(), getNombre(), getApellido(), grado, esRepresentante);
    }
}