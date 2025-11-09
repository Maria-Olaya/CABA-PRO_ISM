package com.proyecto.cabapro.controller.forms;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.proyecto.cabapro.enums.EstadoPartido;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public class PartidoForm {

    private Integer idPartido;

    @NotNull(message = "{partido.fecha.obligatoria}")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fecha;

    @NotBlank(message = "{partido.lugar.obligatorio}")
    @Size(min = 3, max = 100, message = "{partido.lugar.tamano}")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9\\s]+$", message = "{partido.lugar.pattern}")
    private String lugar;

    private EstadoPartido estadoPartido;

    @NotBlank(message = "{partido.equipoLocal.obligatorio}")
    @Size(min = 3, max = 50, message = "{partido.equipoLocal.tamano}")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9\\s]+$", message = "{partido.equipoLocal.pattern}")
    private String equipoLocal;

    @NotBlank(message = "{partido.equipoVisitante.obligatorio}")
    @Size(min = 3, max = 50, message = "{partido.equipoVisitante.tamano}")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9\\s]+$", message = "{partido.equipoVisitante.pattern}")
    private String equipoVisitante;

    @NotNull(message = "{partido.torneo.obligatorio}")
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
