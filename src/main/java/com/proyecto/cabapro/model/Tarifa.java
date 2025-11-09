package com.proyecto.cabapro.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.proyecto.cabapro.enums.Escalafon;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "tarifas",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_partido_arbitro",
        columnNames = {"partido_id", "arbitro_id"}
    )
)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Tarifa {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "partido_id", nullable = false)
    @JsonIgnoreProperties({"arbitros", "torneo"}) 
    private Partido partido;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "torneo_id", nullable = false)
    @JsonIgnoreProperties("partidos") 
    private Torneo torneo;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "arbitro_id", nullable = false)
    @JsonBackReference 
    private Arbitro arbitro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 60)
    private Escalafon escalafon;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal monto;

    @Column(name = "generado_en", nullable = false)
    private LocalDateTime generadoEn = LocalDateTime.now();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "liquidacion_id")
    @JsonIgnoreProperties("tarifas")
    private Liquidacion liquidacion;

    // getters/setters...
    public Long getId() { return id; }
    public Partido getPartido() { return partido; }
    public void setPartido(Partido partido) { this.partido = partido; }
    public Torneo getTorneo() { return torneo; }
    public void setTorneo(Torneo torneo) { this.torneo = torneo; }
    public Arbitro getArbitro() { return arbitro; }
    public void setArbitro(Arbitro arbitro) { this.arbitro = arbitro; }
    public Escalafon getEscalafon() { return escalafon; }
    public void setEscalafon(Escalafon escalafon) { this.escalafon = escalafon; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public LocalDateTime getGeneradoEn() { return generadoEn; }
    public void setGeneradoEn(LocalDateTime generadoEn) { this.generadoEn = generadoEn; }
    public Liquidacion getLiquidacion() { return liquidacion; }
    public void setLiquidacion(Liquidacion liquidacion) { this.liquidacion = liquidacion; }
}
