package com.proyecto.cabapro.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.proyecto.cabapro.enums.Escalafon;
import com.proyecto.cabapro.enums.Especialidad;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.*;

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


    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "disponibilidades_fecha",
        joinColumns = @JoinColumn(name = "arbitro_id", foreignKey = @ForeignKey(name = "FK_disponibilidades_fecha_arbitro"))
    )
    @Column(name = "fecha")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Set<LocalDate> fechasDisponibles = new HashSet<>();

    
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

    public Set<LocalDate> getFechasDisponibles() { return fechasDisponibles; }
    public void setFechasDisponibles(Set<LocalDate> fechasDisponibles) { this.fechasDisponibles = fechasDisponibles; }

}
