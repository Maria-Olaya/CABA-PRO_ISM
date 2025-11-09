package com.proyecto.cabapro.model;

import com.proyecto.cabapro.enums.Escalafon;
import com.proyecto.cabapro.enums.Especialidad;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
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

    // ---- Disponibilidad por FECHAS ----
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "disponibilidades_fecha",
        joinColumns = @JoinColumn(
            name = "arbitro_id",
            foreignKey = @ForeignKey(name = "FK_disponibilidades_fecha_arbitro")
        )
    )
    @Column(name = "fecha")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Set<LocalDate> fechasDisponibles = new HashSet<>();

    @OneToMany(mappedBy = "arbitro", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Asignacion> asignaciones = new ArrayList<>();

    @OneToMany(mappedBy = "arbitro", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Tarifa> tarifas = new ArrayList<>();

    @OneToMany(mappedBy = "arbitro", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Liquidacion> liquidaciones = new ArrayList<>();

    @ManyToMany(mappedBy = "arbitros", fetch = FetchType.LAZY)
    private List<Partido> partidos = new ArrayList<>();


    public Arbitro() {
    }

    public Arbitro(
        String nombre,
        String apellido,
        String correo,
        String contrasena,
        String rol,
        String urlFoto,
        Especialidad especialidad,
        Escalafon escalafon
    ) {
        super(nombre, apellido, correo, contrasena, rol);
        this.urlFoto = urlFoto;
        this.especialidad = especialidad;
        this.escalafon = escalafon;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
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

    public Set<LocalDate> getFechasDisponibles() {
        return fechasDisponibles;
    }

    public void setFechasDisponibles(Set<LocalDate> fechasDisponibles) {
        this.fechasDisponibles = fechasDisponibles;
    }

    public List<Asignacion> getAsignaciones() {
        return asignaciones;
    }

    public void setAsignaciones(List<Asignacion> asignaciones) {
        this.asignaciones = asignaciones;
    }

    public List<Tarifa> getTarifas() {
        return tarifas;
    }

    public void setTarifas(List<Tarifa> tarifas) {
        this.tarifas = tarifas;
    }

    public List<Liquidacion> getLiquidaciones() {
        return liquidaciones;
    }

    public void setLiquidaciones(List<Liquidacion> liquidaciones) {
        this.liquidaciones = liquidaciones;
    }

    public List<Partido> getPartidos() {
        return partidos;
    }

    public void setPartidos(List<Partido> partidos) {
        this.partidos = partidos;
    }
}
