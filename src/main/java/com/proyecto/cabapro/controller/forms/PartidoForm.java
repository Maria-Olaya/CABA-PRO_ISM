package com.proyecto.cabapro.controller.forms;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.proyecto.cabapro.enums.EstadoPartido;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;


public class PartidoForm {

    private Integer idPartido;

    @NotNull(message = "La fecha es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fecha;

    @NotEmpty(message = "El lugar es obligatorio")
    private String lugar;

   
    private EstadoPartido estadoPartido;

    @NotEmpty(message = "El equipo local es obligatorio")
    private String equipoLocal;

    @NotEmpty(message = "El equipo visitante es obligatorio")
    private String equipoVisitante;

    @NotNull(message = "Debe seleccionar un torneo")
    private Integer torneoId;


    // Getters y setters
    public Integer getIdPartido() { return idPartido; }
    public void setIdPartido(Integer idPartido) { this.idPartido = idPartido; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getLugar() { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }

    public EstadoPartido getEstadoPartido() { return estadoPartido; }
    public void setEstadoPartido(EstadoPartido estadoPartido) { this.estadoPartido = estadoPartido; }

    public String getEquipoLocal() { return equipoLocal; }
    public void setEquipoLocal(String equipoLocal) { this.equipoLocal = equipoLocal; }

    public String getEquipoVisitante() { return equipoVisitante; }
    public void setEquipoVisitante(String equipoVisitante) { this.equipoVisitante = equipoVisitante; }

    public Integer getTorneoId() { return torneoId; }
    public void setTorneoId(Integer torneoId) { this.torneoId = torneoId; }

   
}
