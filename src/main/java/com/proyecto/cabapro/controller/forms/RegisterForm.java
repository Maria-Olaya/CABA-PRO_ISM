package com.proyecto.cabapro.controller.forms;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public class RegisterForm {

    @NotBlank(message = "{registro.nombre.obligatorio}")
    @Size(min = 2, max = 50, message = "{registro.nombre.tamano}")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "{registro.nombre.pattern}")
    private String nombre;

    @NotBlank(message = "{registro.apellido.obligatorio}")
    @Size(min = 2, max = 50, message = "{registro.apellido.tamano}")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "{registro.apellido.pattern}")
    private String apellido;

    @NotBlank(message = "{registro.correo.obligatorio}")
    @Email(message = "{registro.correo.invalido}")
    private String correo;

    @NotBlank(message = "{registro.contrasena.obligatoria}")
    @Size(min = 6, max = 100, message = "{registro.contrasena.tamano}")
    private String contrasena;

    @NotBlank(message = "{registro.confirmContrasena.obligatoria}")
    private String confirmContrasena;


    // Getters y setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    
    public String getConfirmContrasena() { return confirmContrasena; }
    public void setConfirmContrasena(String confirmContrasena) { this.confirmContrasena = confirmContrasena; }


}
