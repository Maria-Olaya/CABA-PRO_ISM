// MODIFCADO 

package com.proyecto.cabapro.controller.forms;
import com.proyecto.cabapro.enums.Escalafon;
import com.proyecto.cabapro.enums.Especialidad;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ArbitroForm {

    private Integer id;

    @NotBlank(message = "{admin.arbitros.error.nombreRequerido}")
    @Schema(example = "Carlos", description = "Nombre del árbitro")
    private String nombre;

    @NotBlank(message = "{admin.arbitros.error.apellidoRequerido}")
    @Schema(example = "Ramírez", description = "Apellido del árbitro")
    private String apellido;

    @NotBlank(message = "{admin.arbitros.error.correoRequerido}")
    @Email(message = "{admin.arbitros.error.correoInvalido}")
    @Schema(example = "carlos.ramirez@liga.com", description = "Correo institucional del árbitro")
    private String correo;
    
    @Schema(example = "12345", description = "Contraseña temporal del árbitro (puede actualizarse luego)")
    private String contrasena;

    @NotNull(message = "{admin.arbitros.error.especialidadRequerida}")
    @Schema(
        description = "Especialidad del árbitro según su rol técnico.",
        example = "PRINCIPAL",
        allowableValues = {"PRINCIPAL", "AUXILIAR", "APUNTADOR", "CRONOMETRISTA"}
    )
    private Especialidad especialidad;

    @NotNull(message = "{admin.arbitros.error.escalafonRequerido}")
    @Schema(
        description = "Nivel de certificación o rango del árbitro.",
        example = "REGIONAL",
        allowableValues = {"INTERNACIONAL_FIBA", "PROFESIONAL_NACIONAL", "SEMIPROFESIONAL", "REGIONAL", "EN_FORMACION"}
    )
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
