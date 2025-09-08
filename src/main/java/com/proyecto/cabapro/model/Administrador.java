package com.proyecto.cabapro.model;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

/**
 * Administrador hereda todos los campos de Usuario:
 * id (PK autoincrement), nombre, apellido, correo, contrasena, rol.
 *
 * En estrategia JOINED, la PK del hijo es la misma del padre.
 * @PrimaryKeyJoinColumn indica que la PK de "administradores" se llama "id"
 * y además es FK hacia "usuarios(id)".
 */
@Entity
@Table(name = "administrador")
@PrimaryKeyJoinColumn(name = "id") // usa la misma PK del padre
public class Administrador extends Usuario {
    // Por ahora NO agregamos campos propios.
    // Si luego necesitas algo propio (p. ej. "oficina", "extensionTelefono"),
    // los agregas aquí como propiedades normales.
    
    public Administrador() {
        // Requerido por JPA
    }

    // Puedes agregar constructores de conveniencia si deseas.
    public Administrador(String nombre, String apellido, String correo, String contrasena, String rol) {
        super(nombre, apellido, correo, contrasena, rol);
    }
}
