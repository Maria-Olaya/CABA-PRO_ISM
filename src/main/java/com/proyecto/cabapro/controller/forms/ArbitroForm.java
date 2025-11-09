package com.proyecto.cabapro.controller.forms;
import com.proyecto.cabapro.enums.Escalafon;
import com.proyecto.cabapro.enums.Especialidad;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ArbitroForm {

    private Integer id;

    @NotBlank(message = "{admin.arbitros.error.nombreRequerido}")
    private String nombre;

    @NotBlank(message = "{admin.arbitros.error.apellidoRequerido}")
    private String apellido;

    @NotBlank(message = "{admin.arbitros.error.correoRequerido}")
    @Email(message = "{admin.arbitros.error.correoInvalido}")
    private String correo;

    private String contrasena;

    @NotNull(message = "{admin.arbitros.error.especialidadRequerida}")
    private Especialidad especialidad;

    @NotNull(message = "{admin.arbitros.error.escalafonRequerido}")
    private Escalafon escalafon;

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    public Escalafon getEscalafon() {
        return escalafon;
    }

    public void setEscalafon(Escalafon escalafon) {
        this.escalafon = escalafon;
    }
}
