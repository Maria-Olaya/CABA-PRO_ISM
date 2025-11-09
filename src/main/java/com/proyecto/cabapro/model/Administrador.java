package com.proyecto.cabapro.model;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

/**
 * La PK del hijo es la misma del padre.
 * @PrimaryKeyJoinColumn indica que la PK de "administradores" se llama "id"
 * y además es FK hacia "usuarios(id)".
 */
@Entity
@Table(name = "administrador")
@PrimaryKeyJoinColumn(name = "id") 
public class Administrador extends Usuario {
  
    public Administrador() {
       
    }

    public Administrador(String nombre, String apellido, String correo, String contrasena, String rol) {
        super(nombre, apellido, correo, contrasena, rol);
    }
}
