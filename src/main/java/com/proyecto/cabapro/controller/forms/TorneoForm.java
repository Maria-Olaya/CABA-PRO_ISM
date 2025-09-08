package com.proyecto.cabapro.controller.forms;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.proyecto.cabapro.enums.CategoriaTorneo;
import com.proyecto.cabapro.enums.TipoTorneo;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;


public class TorneoForm {

    private Integer idTorneo; // Usado para edición

    @NotEmpty(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "El tipo de torneo es obligatorio")
    private TipoTorneo tipoTorneo;   

    @NotNull(message = "La categoría es obligatoria")
    private CategoriaTorneo categoria;  
  
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime fechaInicio;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @NotNull(message = "La fecha de fin es obligatoria")
    @Future(message = "La fecha de fin debe estar en el futuro")
    private LocalDateTime fechaFin;



    

    // Validación personalizada
    @AssertTrue(message = "La fecha de fin debe ser posterior al día de inicio (no se permiten eventos el mismo día)")
    public boolean isFechasValidas() {
        if (fechaInicio == null || fechaFin == null) return true; // @NotNull lo maneja aparte
        return fechaFin.toLocalDate().isAfter(fechaInicio.toLocalDate());
    }

    // Getters y setters
    public Integer getIdTorneo() { return idTorneo; }
    public void setIdTorneo(Integer idTorneo) { this.idTorneo = idTorneo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public TipoTorneo getTipoTorneo() { return tipoTorneo; }
    public void setTipoTorneo(TipoTorneo tipoTorneo) { this.tipoTorneo = tipoTorneo; }

    public CategoriaTorneo getCategoria() { return categoria; }
    public void setCategoria(CategoriaTorneo categoria) { this.categoria = categoria; }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }
}
