package com.proyecto.cabapro.model;

import com.proyecto.cabapro.enums.Escalafon;
import com.proyecto.cabapro.enums.Especialidad;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;


@Entity
@Table(name = "arbitros")
public class Arbitro extends Usuario {

    private String urlFoto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "La especialidad es obligatoria")
    private Especialidad especialidad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "El escalafón es obligatorio")
    private Escalafon escalafon;

    public Arbitro() {}

    public Arbitro(String nombre, String apellido, String correo, String contrasena, String rol,
                   String urlFoto, Especialidad especialidad, Escalafon escalafon) {
        super(nombre, apellido, correo, contrasena, rol);
        this.urlFoto = urlFoto;
        this.especialidad = especialidad;
        this.escalafon = escalafon;
    }

    // Getters / Setters
    public String getUrlFoto() { return urlFoto; }
    public void setUrlFoto(String urlFoto) { this.urlFoto = urlFoto; }

    public Especialidad getEspecialidad() { return especialidad; }
    public void setEspecialidad(Especialidad especialidad) { this.especialidad = especialidad; }

    public Escalafon getEscalafon() { return escalafon; }
    public void setEscalafon(Escalafon escalafon) { this.escalafon = escalafon; }

}
