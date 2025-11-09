package com.proyecto.cabapro.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.proyecto.cabapro.enums.EstadoLiquidacion;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "liquidaciones",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_liq_arbitro_firma",
        columnNames = {"arbitro_id","firma"}
    )
)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Liquidacion {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "arbitro_id", nullable = false)
    @JsonBackReference 
    private Arbitro arbitro;

    @Column(name = "fecha_generada", nullable = false)
    private LocalDateTime fechaGenerada;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoLiquidacion estado = EstadoLiquidacion.PENDIENTE;

    @Column(name = "pagado_en")
    private LocalDateTime pagadoEn;

    @Column(name = "total", nullable = false, precision = 14, scale = 2)
    private BigDecimal total;

    
    @Column(name = "firma", nullable = false, length = 64)
    private String firma;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "pdf", columnDefinition = "LONGBLOB")
    private byte[] pdf;

    // getters/setters
    public Long getId() { return id; }

    public Arbitro getArbitro() { return arbitro; }
    public void setArbitro(Arbitro arbitro) { this.arbitro = arbitro; }

    public LocalDateTime getFechaGenerada() { return fechaGenerada; }
    public void setFechaGenerada(LocalDateTime fechaGenerada) { this.fechaGenerada = fechaGenerada; }

    public EstadoLiquidacion getEstado() { return estado; }
    public void setEstado(EstadoLiquidacion estado) { this.estado = estado; }

    public LocalDateTime getPagadoEn() { return pagadoEn; }
    public void setPagadoEn(LocalDateTime pagadoEn) { this.pagadoEn = pagadoEn; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getFirma() { return firma; }
    public void setFirma(String firma) { this.firma = firma; }

    public byte[] getPdf() { return pdf; }
    public void setPdf(byte[] pdf) { this.pdf = pdf; }
}
