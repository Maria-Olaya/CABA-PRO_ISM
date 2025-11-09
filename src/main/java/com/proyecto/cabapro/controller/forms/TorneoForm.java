package com.proyecto.cabapro.controller.forms;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.proyecto.cabapro.enums.CategoriaTorneo;
import com.proyecto.cabapro.enums.TipoTorneo;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public class TorneoForm {

    private Integer idTorneo; 

    // --- NOMBRE ---
    @NotBlank(message = "{torneo.nombre.obligatorio}")
    @Size(min = 3, max = 100, message = "{torneo.nombre.tamano}")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9\\s]+$", message = "{torneo.nombre.pattern}")
    private String nombre;

    
    @NotNull(message = "{torneo.tipo.obligatorio}")
    private TipoTorneo tipoTorneo; 

   
    @NotNull(message = "{torneo.categoria.obligatoria}")
    private CategoriaTorneo categoria;    

   
    @NotNull(message = "{torneo.fechaInicio.obligatoria}")
    @FutureOrPresent(message = "{torneo.fechaInicio.futuro}")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fechaInicio;

   
    @NotNull(message = "{torneo.fechaFin.obligatoria}")
    @Future(message = "{torneo.fechaFin.futuro}")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fechaFin;



    

  
    @AssertTrue(message = "La fecha de fin debe ser posterior al día de inicio (no se permiten eventos el mismo día)")
    public boolean isFechasValidas() {
        if (fechaInicio == null || fechaFin == null) return true; 
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
