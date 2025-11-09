package com.proyecto.cabapro.enums;
import io.swagger.v3.oas.annotations.media.Schema;
@Schema(
    description = "Escalafón o nivel de certificación del árbitro dentro del sistema de arbitraje nacional."
)
public enum Escalafon {
    @Schema(description = "Nivel máximo, reconocido por FIBA a nivel internacional.")
    INTERNACIONAL_FIBA,

    @Schema(description = "Árbitro profesional habilitado para competencias nacionales de alto nivel.")
    PROFESIONAL_NACIONAL,

    @Schema(description = "Árbitro semiprofesional que participa en torneos regionales y locales.")
    SEMIPROFESIONAL,

    @Schema(description = "Árbitro en formación o certificado en ligas regionales.")
    REGIONAL,

    @Schema(description = "Nivel inicial para árbitros en proceso de aprendizaje o certificación.")
    EN_FORMACION;

    public String getMessageKey() {
        return "escalafon." + this.name().toLowerCase();
    }
}
