// Archivo: src/edu/observador/data/ObservadorDAOSQLite.java
package edu.observador.data;

import edu.observador.model.*;
import edu.observador.model.enums.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de ObservadorDAO usando SQLite como motor de persistencia.
 * Maneja las operaciones CRUD para usuarios y observaciones.
 */
public class ObservadorDAOSQLite implements ObservadorDAO {

    private final ConexionDB conexionDB;

    public ObservadorDAOSQLite() {
        this.conexionDB = ConexionDB.getInstancia();
    }

    @Override
    public void inicializarBaseDatos() throws DataAccessException {
        String sqlUsuario = """
            CREATE TABLE IF NOT EXISTS usuarios (
                id TEXT PRIMARY KEY,
                nombre TEXT NOT NULL,
                apellido TEXT NOT NULL,
                contrasenia TEXT NOT NULL,
                rol TEXT NOT NULL,
                activo INTEGER NOT NULL DEFAULT 1,
                primer_ingreso INTEGER NOT NULL DEFAULT 1,
                grado TEXT,
                es_representante INTEGER DEFAULT 0,
                cursos_asignados TEXT,
                curso_direccion_grupo TEXT,
                es_docente_grupo INTEGER DEFAULT 0
            );
            """;

        String sqlObservacion = """
            CREATE TABLE IF NOT EXISTS observaciones (
                id TEXT PRIMARY KEY,
                descripcion TEXT NOT NULL,
                fecha TEXT NOT NULL,
                estudiante_id TEXT NOT NULL,
                creador_id TEXT NOT NULL,
                anulada INTEGER NOT NULL DEFAULT 0,
                justificacion_anulacion TEXT,
                tipo TEXT NOT NULL,
                detalle_academico TEXT,
                tipo_academia TEXT,
                severidad TEXT,
                FOREIGN KEY (estudiante_id) REFERENCES usuarios(id) ON DELETE CASCADE,
                FOREIGN KEY (creador_id) REFERENCES usuarios(id) ON DELETE CASCADE
            );
            """;

        String sqlPeticion = """
            CREATE TABLE IF NOT EXISTS peticiones_revision (
                id TEXT PRIMARY KEY,
                observacion_id TEXT NOT NULL,
                motivo TEXT NOT NULL,
                fecha_peticion TEXT NOT NULL,
                estado TEXT NOT NULL,
                FOREIGN KEY (observacion_id) REFERENCES observaciones(id) ON DELETE CASCADE
            );
            """;

        try (Statement stmt = conexionDB.obtenerConexion().createStatement()) {
            stmt.execute(sqlUsuario);
            stmt.execute(sqlObservacion);
            stmt.execute(sqlPeticion);
        } catch (SQLException e) {
            throw new DataAccessException("Error inicializando base de datos", e);
        }
    }

    // ==================== Implementación métodos Usuario ====================

    @Override
    public void guardarUsuario(Usuario usuario) throws DataAccessException {
        String sql = """
            INSERT OR REPLACE INTO usuarios 
            (id, nombre, apellido, contrasenia, rol, activo, primer_ingreso, 
             grado, es_representante, cursos_asignados, curso_direccion_grupo, es_docente_grupo)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement pstmt = conexionDB.obtenerConexion().prepareStatement(sql)) {
            pstmt.setString(1, usuario.getId());
            pstmt.setString(2, usuario.getNombre());
            pstmt.setString(3, usuario.getApellido());
            pstmt.setString(4, usuario.getContrasenia());
            pstmt.setString(5, usuario.getRol().name());
            pstmt.setInt(6, usuario.isActivo() ? 1 : 0);
            pstmt.setInt(7, usuario.esPrimerIngreso() ? 1 : 0);

            if (usuario instanceof Estudiante) {
                Estudiante est = (Estudiante) usuario;
                pstmt.setString(8, est.getGrado());
                pstmt.setInt(9, est.isEsRepresentante() ? 1 : 0);
                pstmt.setString(10, null);
                pstmt.setString(11, null);
                pstmt.setInt(12, 0);
            } else if (usuario instanceof Docente) {
                Docente doc = (Docente) usuario;
                pstmt.setString(8, null);
                pstmt.setInt(9, 0);
                String cursos = String.join(",", doc.getCursosAsignados());
                pstmt.setString(10, cursos.isEmpty() ? null : cursos);
                pstmt.setString(11, doc.getCursoDireccionGrupo());
                pstmt.setInt(12, doc.isEsDocenteDeGrupo() ? 1 : 0);
            } else {
                // Coordinador
                pstmt.setString(8, null);
                pstmt.setInt(9, 0);
                pstmt.setString(10, null);
                pstmt.setString(11, null);
                pstmt.setInt(12, 0);
            }

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error guardando usuario: " + usuario.getId(), e);
        }
    }

    @Override
    public Usuario buscarUsuarioPorId(String id) throws DataAccessException {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (PreparedStatement pstmt = conexionDB.obtenerConexion().prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapearUsuario(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Error buscando usuario: " + id, e);
        }
    }

    @Override
    public List<Usuario> listarUsuariosPorRol(RolUsuario rol) throws DataAccessException {
        String sql = "SELECT * FROM usuarios WHERE rol = ?";
        List<Usuario> usuarios = new ArrayList<>();
        try (PreparedStatement pstmt = conexionDB.obtenerConexion().prepareStatement(sql)) {
            pstmt.setString(1, rol.name());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error listando usuarios por rol: " + rol, e);
        }
        return usuarios;
    }

    @Override
    public List<Estudiante> listarEstudiantes() throws DataAccessException {
        String sql = "SELECT * FROM usuarios WHERE rol = 'ESTUDIANTE'";
        List<Estudiante> estudiantes = new ArrayList<>();
        try (Statement stmt = conexionDB.obtenerConexion().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Usuario u = mapearUsuario(rs);
                if (u instanceof Estudiante) {
                    estudiantes.add((Estudiante) u);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error listando estudiantes", e);
        }
        return estudiantes;
    }

    @Override
    public List<Estudiante> listarEstudiantesPorGrado(String grado) throws DataAccessException {
        String sql = "SELECT * FROM usuarios WHERE rol = 'ESTUDIANTE' AND grado = ?";
        List<Estudiante> estudiantes = new ArrayList<>();
        try (PreparedStatement pstmt = conexionDB.obtenerConexion().prepareStatement(sql)) {
            pstmt.setString(1, grado);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Usuario u = mapearUsuario(rs);
                if (u instanceof Estudiante) {
                    estudiantes.add((Estudiante) u);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error listando estudiantes por grado: " + grado, e);
        }
        return estudiantes;
    }

    // ==================== Implementación métodos Observación ====================

    @Override
    public void guardarObservacion(Observacion observacion) throws DataAccessException {
        String sql = """
            INSERT OR REPLACE INTO observaciones 
            (id, descripcion, fecha, estudiante_id, creador_id, anulada, 
             justificacion_anulacion, tipo, detalle_academico, tipo_academia, severidad)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement pstmt = conexionDB.obtenerConexion().prepareStatement(sql)) {
            pstmt.setString(1, observacion.getId());
            pstmt.setString(2, observacion.getDescripcion());
            pstmt.setString(3, observacion.getFecha().toString());
            pstmt.setString(4, observacion.getEstudiante().getId());
            pstmt.setString(5, observacion.getCreador().getId());
            pstmt.setInt(6, observacion.isAnulada() ? 1 : 0);
            pstmt.setString(7, observacion.getJustificacionAnulacion());

            if (observacion instanceof ObservacionAcademica) {
                ObservacionAcademica acad = (ObservacionAcademica) observacion;
                pstmt.setString(8, "ACADEMICA");
                pstmt.setString(9, acad.getDetalleAcademico());
                pstmt.setString(10, acad.getTipo().name());
                pstmt.setString(11, null);
            } else if (observacion instanceof ObservacionDisciplinaria) {
                ObservacionDisciplinaria disc = (ObservacionDisciplinaria) observacion;
                pstmt.setString(8, "DISCIPLINARIA");
                pstmt.setString(9, null);
                pstmt.setString(10, null);
                pstmt.setString(11, disc.getSeveridad().name());
            } else {
                throw new DataAccessException("Tipo de observación no soportado");
            }

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error guardando observación: " + observacion.getId(), e);
        }
    }

    @Override
    public List<Observacion> cargarHistorialEstudiante(String estudianteId) throws DataAccessException {
        String sql = "SELECT * FROM observaciones WHERE estudiante_id = ? ORDER BY fecha DESC";
        List<Observacion> observaciones = new ArrayList<>();
        try (PreparedStatement pstmt = conexionDB.obtenerConexion().prepareStatement(sql)) {
            pstmt.setString(1, estudianteId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                observaciones.add(mapearObservacion(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error cargando historial del estudiante: " + estudianteId, e);
        }
        return observaciones;
    }

    @Override
    public Observacion buscarObservacionPorId(String id) throws DataAccessException {
        String sql = "SELECT * FROM observaciones WHERE id = ?";
        try (PreparedStatement pstmt = conexionDB.obtenerConexion().prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapearObservacion(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Error buscando observación: " + id, e);
        }
    }

    @Override
    public void actualizarObservacion(Observacion observacion) throws DataAccessException {
        String sql = """
            UPDATE observaciones 
            SET descripcion = ?, anulada = ?, justificacion_anulacion = ?
            WHERE id = ?
            """;
        try (PreparedStatement pstmt = conexionDB.obtenerConexion().prepareStatement(sql)) {
            pstmt.setString(1, observacion.getDescripcion());
            pstmt.setInt(2, observacion.isAnulada() ? 1 : 0);
            pstmt.setString(3, observacion.getJustificacionAnulacion());
            pstmt.setString(4, observacion.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error actualizando observación: " + observacion.getId(), e);
        }
    }

    @Override
    public void cerrar() {
        conexionDB.cerrar();
    }

    // ==================== Métodos auxiliares de mapeo ====================

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String nombre = rs.getString("nombre");
        String apellido = rs.getString("apellido");
        String contrasenia = rs.getString("contrasenia");
        RolUsuario rol = RolUsuario.valueOf(rs.getString("rol"));
        boolean activo = rs.getInt("activo") == 1;
        boolean primerIngreso = rs.getInt("primer_ingreso") == 1;

        Usuario usuario;
        if (rol == RolUsuario.ESTUDIANTE) {
            String grado = rs.getString("grado");
            boolean esRepresentante = rs.getInt("es_representante") == 1;
            Estudiante est = new Estudiante(id, nombre, apellido, contrasenia, grado);
            est.setEsRepresentante(esRepresentante);
            usuario = est;
        } else if (rol == RolUsuario.DOCENTE) {
            String cursosStr = rs.getString("cursos_asignados");
            List<String> cursos = new ArrayList<>();
            if (cursosStr != null && !cursosStr.isEmpty())
                cursos = List.of(cursosStr.split(","));
            String cursoDireccion = rs.getString("curso_direccion_grupo");
            boolean esDocenteGrupo = rs.getInt("es_docente_grupo") == 1;
            Docente doc = new Docente(id, nombre, apellido, contrasenia);
            doc.setCursosAsignados(cursos);
            doc.setCursoDireccionGrupo(cursoDireccion);
            doc.setEsDocenteDeGrupo(esDocenteGrupo);
            usuario = doc;
        } else {
            usuario = new Coordinador(id, nombre, apellido, contrasenia);
        }

        usuario.setActivo(activo);
        usuario.setPrimerIngreso(primerIngreso);
        return usuario;
    }

    private Observacion mapearObservacion(ResultSet rs) throws SQLException, DataAccessException {
        String id = rs.getString("id");
        String descripcion = rs.getString("descripcion");
        LocalDate fecha = LocalDate.parse(rs.getString("fecha"));
        String estudianteId = rs.getString("estudiante_id");
        String creadorId = rs.getString("creador_id");
        boolean anulada = rs.getInt("anulada") == 1;
        String justificacion = rs.getString("justificacion_anulacion");
        String tipo = rs.getString("tipo");

        Estudiante estudiante = (Estudiante) buscarUsuarioPorId(estudianteId);
        Usuario creador = buscarUsuarioPorId(creadorId);

        if (estudiante == null || creador == null) {
            throw new DataAccessException("Estudiante o creador no encontrado para observación " + id);
        }

        Observacion observacion;
        if ("ACADEMICA".equals(tipo)) {
            TipoAcademia tipoAcad = TipoAcademia.valueOf(rs.getString("tipo_academia"));
            String detalle = rs.getString("detalle_academico");
            observacion = new ObservacionAcademica(id, descripcion, fecha, estudiante, creador, tipoAcad, detalle);
        } else {
            NivelSeveridad severidad = NivelSeveridad.valueOf(rs.getString("severidad"));
            observacion = new ObservacionDisciplinaria(id, descripcion, fecha, estudiante, creador, severidad);
        }

        if (anulada) {
            observacion.anular(justificacion);
        }
        return observacion;
    }
}