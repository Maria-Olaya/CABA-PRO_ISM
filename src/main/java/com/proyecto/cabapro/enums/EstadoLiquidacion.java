package com.proyecto.cabapro.enums;

public enum EstadoLiquidacion {
    PENDIENTE, PAGADA;


    public String getMessageKey() {
        return "liquidacion.estado." + this.name().toLowerCase();
    }
}
