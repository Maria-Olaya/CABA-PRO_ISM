package com.proyecto.cabapro.enums;
import io.swagger.v3.oas.annotations.media.Schema;
@Schema(
    description = "Especialidad técnica del árbitro según el tipo de función que desempeña en un partido."
)
public enum Especialidad {
    @Schema(description = "Árbitro principal del partido, encargado de las decisiones finales.")
    PRINCIPAL,

    @Schema(description = "Asistente o juez de línea que apoya al árbitro principal.")
    AUXILIAR,

    @Schema(description = "Encargado de llevar el registro de anotaciones y estadísticas del partido.")
    APUNTADOR,

    @Schema(description = "Responsable del control del tiempo y cronómetro del partido.")
    CRONOMETRISTA;


    public String getMessageKey() {
        return "especialidad." + this.name().toLowerCase();
    }
}
