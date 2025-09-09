package com.proyecto.cabapro.dto;

import com.proyecto.cabapro.model.Arbitro;
import com.proyecto.cabapro.model.Partido;

import java.math.BigDecimal;

public class TarifaCalculoRow {
    private final Partido partido;
    private final Arbitro arbitro;
    private final BigDecimal base;     
    private final BigDecimal adicional; 
    private final BigDecimal total;      

    public TarifaCalculoRow(Partido partido, Arbitro arbitro,
                            BigDecimal base, BigDecimal adicional, BigDecimal total) {
        this.partido = partido;
        this.arbitro = arbitro;
        this.base = base;
        this.adicional = adicional;
        this.total = total;
    }

    public Partido getPartido() { return partido; }
    public Arbitro getArbitro() { return arbitro; }
    public BigDecimal getBase() { return base; }
    public BigDecimal getAdicional() { return adicional; }
    public BigDecimal getTotal() { return total; }
}

