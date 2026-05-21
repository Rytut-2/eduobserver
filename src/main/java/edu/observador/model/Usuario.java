package edu.observador.model;

import edu.observador.model.enums.RolUsuario;
import java.util.Objects;

public abstract class Usuario {
    private String id;
    private String nombre;
    private String apellido;
    private String contrasenia;
    private RolUsuario rol;
    private boolean activo;
    private boolean primerIngreso;

    public Usuario(String id, String nombre, String apellido, String contrasenia, RolUsuario rol) {
        if (id == null || id.trim().isEmpty())
            throw new IllegalArgumentException("El ID no puede ser nulo o vacío");
        if (nombre == null || nombre.trim().isEmpty())
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacío");
        if (apellido == null || apellido.trim().isEmpty())
            throw new IllegalArgumentException("El apellido no puede ser nulo o vacío");
        if (contrasenia == null || contrasenia.trim().isEmpty())
            throw new IllegalArgumentException("La contraseña no puede ser nula o vacía");
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.contrasenia = contrasenia;
        this.rol = rol;
        this.activo = true;
        this.primerIngreso = true;
    }

    public boolean esValida() {
        return activo;
    }

    public boolean esPrimerIngreso() {
        return primerIngreso;
    }

    public abstract String obtenerInfoEspecifica();

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getContrasenia() { return contrasenia; }
    public void setContrasenia(String contrasenia) { this.contrasenia = contrasenia; }
    public RolUsuario getRol() { return rol; }
    public void setRol(RolUsuario rol) { this.rol = rol; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public boolean isPrimerIngreso() { return primerIngreso; }
    public void setPrimerIngreso(boolean primerIngreso) { this.primerIngreso = primerIngreso; }

    public String getNombreCompleto() { return nombre + " " + apellido; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return String.format("Usuario{id='%s', nombre='%s %s', rol=%s, activo=%s}",
                id, nombre, apellido, rol, activo);
    }
}