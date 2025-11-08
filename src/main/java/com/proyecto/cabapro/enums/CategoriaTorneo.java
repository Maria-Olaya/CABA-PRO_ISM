package com.proyecto.cabapro.enums;


public enum CategoriaTorneo {
    UNIVERSITARIO,
    AMATEUR,
    PROFESIONAL;

    public String getMensajeKey() {
        return "torneo.categoria." + this.name().toLowerCase();
    }
}


