package com.proyecto.cabapro.enums;

public enum EstadoPartido {
    PROGRAMADO,
    EN_CURSO,
    FINALIZADO,
    CANCELADO;

    public String getMensajeKey() {
        return "partido.estado." + this.name().toLowerCase();
    }
}
