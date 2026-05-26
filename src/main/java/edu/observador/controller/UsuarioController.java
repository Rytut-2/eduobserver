// Archivo: src/edu/observador/controller/UsuarioController.java
package edu.observador.controller;

import edu.observador.data.DataAccessException;
import edu.observador.data.ObservadorDAO;
import edu.observador.model.*;
import edu.observador.model.enums.RolUsuario;

import java.util.ArrayList;
import java.util.List;

public class UsuarioController {

    private final ObservadorDAO usuarioDAO;

    public UsuarioController(ObservadorDAO dao) {
        this.usuarioDAO = dao;
    }

    // ==================== Generación de ID ====================

    public String generarIdPorRol(String tipoRol) throws DataAccessException {
        String prefijo;
        switch (tipoRol.toLowerCase()) {
            case "estudiante": prefijo = "EST-"; break;
            case "docente": prefijo = "DOC-"; break;
            case "coordinador": prefijo = "COOD-"; break;
            default: throw new IllegalArgumentException("Rol inválido: " + tipoRol);
        }
        RolUsuario rol = RolUsuario.valueOf(tipoRol.toUpperCase());
        List<Usuario> usuarios = listarUsuariosPorRol(rol);
        int max = 0;
        for (Usuario u : usuarios) {
            String id = u.getId();
            if (id.startsWith(prefijo)) {
                String numStr = id.substring(prefijo.length());
                try {
                    int num = Integer.parseInt(numStr);
                    if (num > max) max = num;
                } catch (NumberFormatException ignored) {}
            }
        }
        int nuevo = max + 1;
        return prefijo + String.format("%03d", nuevo);
    }

    // ==================== CRUD Usuarios ====================

    public Usuario registrarNuevoUsuario(String tipo, String id, String nombre,
                                         String apellido, String contrasenia, String adicional)
            throws DataAccessException {
        if (id == null || id.trim().isEmpty()) {
            id = generarIdPorRol(tipo);
        }
        Usuario nuevo;
        switch (tipo.toLowerCase()) {
            case "estudiante":
                if (adicional == null || adicional.trim().isEmpty())
                    throw new IllegalArgumentException("Grado requerido para estudiante");
                nuevo = new Estudiante(id, nombre, apellido, contrasenia, adicional);
                break;
            case "docente":
                nuevo = new Docente(id, nombre, apellido, contrasenia);
                break;
            case "coordinador":
                nuevo = new Coordinador(id, nombre, apellido, contrasenia);
                break;
            default:
                throw new IllegalArgumentException("Tipo de usuario inválido: " + tipo);
        }
        usuarioDAO.guardarUsuario(nuevo);
        return nuevo;
    }

    public boolean modificarEstadoActivo(String idUsuario, boolean activo) throws DataAccessException {
        Usuario u = usuarioDAO.buscarUsuarioPorId(idUsuario);
        if (u == null) return false;
        u.setActivo(activo);
        usuarioDAO.guardarUsuario(u);
        return true;
    }

    // ==================== Listados ====================

    public List<Usuario> listarUsuariosPorRol(RolUsuario rol) throws DataAccessException {
        return usuarioDAO.listarUsuariosPorRol(rol);
    }

    public List<Estudiante> listarEstudiantes() throws DataAccessException {
        return usuarioDAO.listarEstudiantes();
    }

    public List<Estudiante> listarEstudiantesPorGrado(String grado) throws DataAccessException {
        return usuarioDAO.listarEstudiantesPorGrado(grado);
    }

    // ==================== Asignación de banderas ====================

    public void asignarRepresentante(String estudianteId, boolean esRepresentante) throws DataAccessException {
        Usuario u = usuarioDAO.buscarUsuarioPorId(estudianteId);
        if (u instanceof Estudiante) {
            ((Estudiante) u).setEsRepresentante(esRepresentante);
            usuarioDAO.guardarUsuario(u);
        } else {
            throw new IllegalArgumentException("El ID no corresponde a un estudiante");
        }
    }

    public void asignarDocenteDeGrupo(String docenteId, boolean esDocenteGrupo, String cursoDir) throws DataAccessException {
        Usuario u = usuarioDAO.buscarUsuarioPorId(docenteId);
        if (u instanceof Docente) {
            Docente d = (Docente) u;
            d.setEsDocenteDeGrupo(esDocenteGrupo);
            if (cursoDir != null && !cursoDir.isEmpty()) {
                d.setCursoDireccionGrupo(cursoDir);
            }
            usuarioDAO.guardarUsuario(d);
        } else {
            throw new IllegalArgumentException("El ID no corresponde a un docente");
        }
    }

    // ==================== Métodos de conteo para estadísticas ====================

    public int getTotalUsuarios() throws DataAccessException {
        List<Usuario> todos = new ArrayList<>();
        todos.addAll(listarUsuariosPorRol(RolUsuario.ESTUDIANTE));
        todos.addAll(listarUsuariosPorRol(RolUsuario.DOCENTE));
        todos.addAll(listarUsuariosPorRol(RolUsuario.COORDINADOR));
        return todos.size();
    }

    public int getTotalEstudiantes() throws DataAccessException {
        return listarUsuariosPorRol(RolUsuario.ESTUDIANTE).size();
    }

    public int getTotalDocentes() throws DataAccessException {
        return listarUsuariosPorRol(RolUsuario.DOCENTE).size();
    }
}