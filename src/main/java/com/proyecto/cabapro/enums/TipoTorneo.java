package com.proyecto.cabapro.enums;

public enum TipoTorneo {
    LIGA,
    ELIMINACION_DIRECTA,
    TORNEO_CORTO;

    public String getMensajeKey() {
        return "torneo.tipo." + this.name().toLowerCase();
    }
}
