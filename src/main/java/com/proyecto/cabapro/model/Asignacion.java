package com.proyecto.cabapro.model;

import com.proyecto.cabapro.enums.EstadoAsignacion;
import jakarta.persistence.*;
import java.math.BigDecimal;            
import java.time.LocalDate;

@Entity
@Table(name = "asignaciones")
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Árbitro dueño
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "arbitro_id", nullable = false)
    private Arbitro arbitro;

    // Partido asignado (OBLIGATORIO)
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "partido_id", nullable = false)
    private Partido partido;

    // Torneo (se toma del partido)
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "torneo_id", nullable = false)
    private Torneo torneo;

    // Fecha (copia de la fecha del partido)
    private LocalDate fechaAsignacion;

    @Enumerated(EnumType.STRING)
    private EstadoAsignacion estado = EstadoAsignacion.PENDIENTE;

    @Column(name = "monto", precision = 14, scale = 2, nullable = false)
    private BigDecimal monto = BigDecimal.ZERO;      

    public Asignacion() {}

    public Asignacion(Arbitro arbitro, Partido partido) {
        this.arbitro = arbitro;
        this.partido = partido;
        this.torneo  = partido.getTorneo();
        this.fechaAsignacion = partido.getFecha().toLocalDate();
        this.estado = EstadoAsignacion.PENDIENTE;
        this.monto = BigDecimal.ZERO;                
    }

    public Long getId() { return id; }
    public Arbitro getArbitro() { return arbitro; }
    public void setArbitro(Arbitro arbitro) { this.arbitro = arbitro; }
    public Partido getPartido() { return partido; }
    public void setPartido(Partido partido) { this.partido = partido; }
    public Torneo getTorneo() { return torneo; }
    public void setTorneo(Torneo torneo) { this.torneo = torneo; }
    public LocalDate getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(LocalDate fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }
    public EstadoAsignacion getEstado() { return estado; }
    public void setEstado(EstadoAsignacion estado) { this.estado = estado; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = (monto != null ? monto : BigDecimal.ZERO); }
}
