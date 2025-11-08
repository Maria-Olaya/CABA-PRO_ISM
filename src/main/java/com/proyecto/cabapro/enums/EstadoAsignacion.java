package com.proyecto.cabapro.enums;

public enum EstadoAsignacion {
    PENDIENTE,
    ACEPTADA,
    RECHAZADA;

    /** Clave para messages*.properties */
    public String getMensajeKey() {
        return "asignacion.estado." + this.name().toLowerCase();
    }
}
