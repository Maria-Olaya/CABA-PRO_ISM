package com.proyecto.cabapro.model;

import java.time.LocalDateTime;
import java.util.List;

import com.proyecto.cabapro.enums.CategoriaTorneo;
import com.proyecto.cabapro.enums.TipoTorneo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "torneos")
public class Torneo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idTorneo;

    private String nombre;      
    
    @Enumerated(EnumType.STRING)
    private TipoTorneo tipoTorneo;
    
    @Enumerated(EnumType.STRING) // <-- guarda el nombre del enum (no el número)
    private CategoriaTorneo categoria;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    @OneToMany(mappedBy = "torneo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)  // cargar partidos solo si los pides
    private List<Partido> partidos;

    // Getters y Setters
    public int getIdTorneo() {
        return idTorneo;
    }

    public void setIdTorneo(int idTorneo) {
        this.idTorneo = idTorneo;
    }

    public String getNombre() {   
        return nombre;
    }

    public void setNombre(String nombre) {  
        this.nombre = nombre;
    }

    public TipoTorneo getTipoTorneo() {
    return tipoTorneo;
}

    public void setTipoTorneo(TipoTorneo tipoTorneo) {
        this.tipoTorneo = tipoTorneo;
    }


    

    public CategoriaTorneo getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaTorneo categoria) {
        this.categoria = categoria;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public List<Partido> getPartidos() {
        return partidos;
    }

    public void setPartidos(List<Partido> partidos) {
        this.partidos = partidos;
    }
}
