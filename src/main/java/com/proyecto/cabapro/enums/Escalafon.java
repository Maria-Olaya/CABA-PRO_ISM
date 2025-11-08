// MODIFICADO

package com.proyecto.cabapro.enums;

public enum Escalafon {
    INTERNACIONAL_FIBA, PROFESIONAL_NACIONAL, SEMIPROFESIONAL, REGIONAL, EN_FORMACION;

    public String getMessageKey() {
        return "escalafon." + this.name().toLowerCase();
    }
}
