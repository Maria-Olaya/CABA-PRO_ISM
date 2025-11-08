
// MODIFICADO

package com.proyecto.cabapro.enums;

public enum Especialidad {
    PRINCIPAL, AUXILIAR, APUNTADOR, CRONOMETRISTA;

    public String getMessageKey() {
        return "especialidad." + this.name().toLowerCase();
    }
}
