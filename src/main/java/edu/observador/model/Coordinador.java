// Archivo: src/edu/observador/model/Coordinador.java
package edu.observador.model;

import edu.observador.model.enums.NivelSeveridad;
import edu.observador.model.enums.RolUsuario;
import edu.observador.model.enums.TipoAcademia;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Clase que representa al coordinador académico (unifica también las funciones de director).
 * El coordinador tiene privilegios especiales: gestión de usuarios, anulación de observaciones,
 * generación de reportes generales y supervisión del sistema.
 */
public class Coordinador extends Usuario {

    private List<Observacion> observacionesCreadas;

    /**
     * Constructor para crear un coordinador.
     *
     * @param id          Identificador único (ej. COOD-001)
     * @param nombre      Nombre del coordinador
     * @param apellido    Apellido del coordinador
     * @param contrasenia Contraseña de acceso
     */
    public Coordinador(String id, String nombre, String apellido, String contrasenia) {
        super(id, nombre, apellido, contrasenia, RolUsuario.COORDINADOR);
        this.observacionesCreadas = new ArrayList<>();
    }

    /**
     * Crea un nuevo usuario (estudiante, docente o coordinador).
     * Este método crea la instancia pero no la persiste.
     * La persistencia debe ser manejada por el DAO correspondiente.
     *
     * @param tipoUsuario El tipo de usuario a crear ("estudiante", "docente", "coordinador")
     * @param id          Identificador único (puede ser null para generación automática)
     * @param nombre      Nombre
     * @param apellido    Apellido
     * @param contrasenia Contraseña
     * @param adicional   Parámetro adicional según el tipo (para estudiante es el grado,
     *                    para docente puede ser null)
     * @return El usuario creado
     * @throws IllegalArgumentException si el tipo no es soportado o faltan parámetros
     */
    public Usuario crearUsuario(String tipoUsuario, String id, String nombre,
                                String apellido, String contrasenia, String adicional) {
        Usuario nuevoUsuario;

        switch (tipoUsuario.toLowerCase()) {
            case "estudiante":
                if (adicional == null || adicional.trim().isEmpty()) {
                    throw new IllegalArgumentException("El grado del estudiante es obligatorio");
                }
                nuevoUsuario = new Estudiante(id, nombre, apellido, contrasenia, adicional);
                break;
            case "docente":
                nuevoUsuario = new Docente(id, nombre, apellido, contrasenia);
                break;
            case "coordinador":
                nuevoUsuario = new Coordinador(id, nombre, apellido, contrasenia);
                break;
            default:
                throw new IllegalArgumentException("Tipo de usuario no soportado: " + tipoUsuario);
        }
        return nuevoUsuario;
    }

    /**
     * Crea una nueva observación (académica o disciplinaria) y la registra en el estudiante.
     *
     * @param estudiante      Estudiante al que se le asigna la observación
     * @param descripcion     Descripción de la observación
     * @param tipoObservacion "academica" o "disciplinaria"
     * @param adicional       Parámetro adicional: para académica es el TipoAcademia,
     *                        para disciplinaria es el NivelSeveridad
     * @param detalle         Detalle específico (para académica es el detalle académico,
     *                        para disciplinaria puede ser null)
     * @return La observación creada
     * @throws IllegalArgumentException si los parámetros son inválidos
     */
    public Observacion crearObservacion(Estudiante estudiante, String descripcion,
                                        String tipoObservacion, Object adicional, String detalle) {
        if (estudiante == null) {
            throw new IllegalArgumentException("El estudiante no puede ser nulo");
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción no puede ser nula o vacía");
        }

        Observacion observacion;
        String id = null; // Se generará automáticamente en el constructor

        if ("academica".equalsIgnoreCase(tipoObservacion)) {
            if (!(adicional instanceof TipoAcademia)) {
                throw new IllegalArgumentException("Para observación académica el adicional debe ser TipoAcademia");
            }
            if (detalle == null || detalle.trim().isEmpty()) {
                throw new IllegalArgumentException("El detalle académico es obligatorio");
            }
            observacion = new ObservacionAcademica(id, descripcion, LocalDate.now(),
                    estudiante, this, (TipoAcademia) adicional, detalle);
        } else if ("disciplinaria".equalsIgnoreCase(tipoObservacion)) {
            if (!(adicional instanceof NivelSeveridad)) {
                throw new IllegalArgumentException("Para observación disciplinaria el adicional debe ser NivelSeveridad");
            }
            observacion = new ObservacionDisciplinaria(id, descripcion, LocalDate.now(),
                    estudiante, this, (NivelSeveridad) adicional);
        } else {
            throw new IllegalArgumentException("Tipo de observación no soportado: " + tipoObservacion);
        }

        // Registrar la observación en el estudiante
        estudiante.agregarObservacion(observacion);
        // Registrar en la lista del coordinador
        observacionesCreadas.add(observacion);

        return observacion;
    }

    /**
     * Anula una observación existente. Solo el coordinador tiene este privilegio (RF-10).
     *
     * @param observacion   La observación a anular
     * @param justificacion Motivo de la anulación
     * @throws IllegalArgumentException si la observación o la justificación son nulas/vacías
     * @throws IllegalStateException    si la observación ya estaba anulada
     */
    public void anularObservacion(Observacion observacion, String justificacion) {
        if (observacion == null) {
            throw new IllegalArgumentException("La observación no puede ser nula");
        }
        if (justificacion == null || justificacion.trim().isEmpty()) {
            throw new IllegalArgumentException("La justificación de anulación es obligatoria");
        }
        observacion.anular(justificacion);
    }

    /**
     * Genera un reporte general del estado de convivencia de la institución.
     * Incluye estadísticas de observaciones por tipo, nivel de severidad,
     * estudiantes con mayor número de observaciones, etc.
     *
     * @param estudiantes Lista de todos los estudiantes del sistema
     * @return Reporte en formato String
     */
    public String generarReporteGeneral(List<Estudiante> estudiantes) {
        if (estudiantes == null || estudiantes.isEmpty()) {
            return "No hay estudiantes registrados en el sistema.";
        }

        StringBuilder reporte = new StringBuilder();
        reporte.append("========== REPORTE GENERAL DE CONVIVENCIA ==========\n\n");

        int totalObservaciones = 0;
        int totalAcademicas = 0;
        int totalDisciplinarias = 0;
        int totalAnuladas = 0;
        int estudiantesConAlertasRojas = 0;
        int estudiantesConAlertasAmarillas = 0;

        Estudiante estudianteMasObservaciones = null;
        int maxObservaciones = 0;

        for (Estudiante est : estudiantes) {
            List<Observacion> historialCompleto = est.getHistorialCompleto();
            int cantidadObs = historialCompleto.size();
            totalObservaciones += cantidadObs;

            if (cantidadObs > maxObservaciones) {
                maxObservaciones = cantidadObs;
                estudianteMasObservaciones = est;
            }

            for (Observacion obs : historialCompleto) {
                if (obs instanceof ObservacionAcademica) {
                    totalAcademicas++;
                } else if (obs instanceof ObservacionDisciplinaria) {
                    totalDisciplinarias++;
                }
                if (obs.isAnulada()) {
                    totalAnuladas++;
                }
            }

            String alerta = est.calcularNivelAlerta();
            if ("Rojo".equals(alerta)) {
                estudiantesConAlertasRojas++;
            } else if ("Amarillo".equals(alerta)) {
                estudiantesConAlertasAmarillas++;
            }
        }

        reporte.append("Estadísticas generales:\n");
        reporte.append("- Total de estudiantes: ").append(estudiantes.size()).append("\n");
        reporte.append("- Total de observaciones: ").append(totalObservaciones).append("\n");
        reporte.append("  * Académicas: ").append(totalAcademicas).append("\n");
        reporte.append("  * Disciplinarias: ").append(totalDisciplinarias).append("\n");
        reporte.append("  * Anuladas: ").append(totalAnuladas).append("\n\n");

        reporte.append("Niveles de alerta:\n");
        reporte.append("- Estudiantes en nivel ROJO: ").append(estudiantesConAlertasRojas).append("\n");
        reporte.append("- Estudiantes en nivel AMARILLO: ").append(estudiantesConAlertasAmarillas).append("\n");
        reporte.append("- Estudiantes en nivel VERDE: ").append(estudiantes.size() - estudiantesConAlertasRojas - estudiantesConAlertasAmarillas).append("\n\n");

        if (estudianteMasObservaciones != null) {
            reporte.append("Estudiante con más observaciones: ")
                    .append(estudianteMasObservaciones.getNombreCompleto())
                    .append(" (").append(maxObservaciones).append(" observaciones)\n");
        }

        reporte.append("\n========== FIN DEL REPORTE ==========");
        return reporte.toString();
    }

    /**
     * Registra una observación creada por el coordinador.
     *
     * @param observacion Observación creada
     */
    public void agregarObservacionCreada(Observacion observacion) {
        if (observacion == null) {
            throw new IllegalArgumentException("La observación no puede ser nula");
        }
        if (!observacion.getCreador().equals(this)) {
            throw new IllegalArgumentException("El creador de la observación no coincide con este coordinador");
        }
        observacionesCreadas.add(observacion);
    }

    @Override
    public String obtenerInfoEspecifica() {
        return String.format("Coordinador - Observaciones creadas: %d", observacionesCreadas.size());
    }

    // Getters

    public List<Observacion> getObservacionesCreadas() {
        return new ArrayList<>(observacionesCreadas);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Coordinador that = (Coordinador) o;
        return Objects.equals(observacionesCreadas, that.observacionesCreadas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), observacionesCreadas);
    }

    @Override
    public String toString() {
        return String.format("Coordinador{id='%s', nombre='%s %s', obsCreadas=%d}",
                getId(), getNombre(), getApellido(), observacionesCreadas.size());
    }
}