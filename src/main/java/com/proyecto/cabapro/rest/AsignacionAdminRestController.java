package com.proyecto.cabapro.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.cabapro.model.Asignacion;
import com.proyecto.cabapro.model.Partido;
import com.proyecto.cabapro.service.AsignacionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/admin/asignaciones")
public class AsignacionAdminRestController {

    private final AsignacionService asignacionService;

    public AsignacionAdminRestController(AsignacionService asignacionService) {
        this.asignacionService = asignacionService;
    }

    @Operation(
        summary = "Obtener datos para crear una asignación",
        description = "Devuelve información del partido, árbitros disponibles, ya asignados y especialidades faltantes/ocupadas.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Datos obtenidos correctamente",
                content = @Content(
                    schema = @Schema(
                        example = "{ " +
                                  "\"partido\": { \"id\": 1, \"fecha\": \"2025-11-15\", \"torneoId\": 2 }, " +
                                  "\"arbitros\": [{ \"id\": 10, \"nombre\": \"Carlos\", \"especialidad\": \"PRINCIPAL\" }], " +
                                  "\"noDisponibles\": [11,12], " +
                                  "\"yaAsignados\": [10], " +
                                  "\"aceptadas\": [ { \"id\": 5, \"arbitroId\": 10, \"estado\": \"ACEPTADA\" } ], " +
                                  "\"faltanEspecialidades\": [\"CRONOMETRISTA\"], " +
                                  "\"especialidadesOcupadas\": [\"PRINCIPAL\"] " +
                                  "}"
                    )
                )
            )
        }
    )
    @GetMapping("/crear")
    public Map<String, Object> obtenerDatosCreacion(@RequestParam("partidoId") int partidoId) {
        Map<String, Object> datos = new HashMap<>();

        Partido partido = asignacionService.buscarPartido(partidoId);
        datos.put("partido", partido);
        datos.put("arbitros", asignacionService.listarArbitros());
        datos.put("noDisponibles", asignacionService.arbitrosNoDisponiblesIds(partidoId));
        datos.put("yaAsignados", asignacionService.arbitrosYaAsignadosIds(partidoId));
        datos.put("aceptadas", asignacionService.listarAceptadasPorPartido(partidoId));
        datos.put("faltanEspecialidades", asignacionService.especialidadesFaltantes(partidoId));
        datos.put("especialidadesOcupadas", asignacionService.especialidadesOcupadas(partidoId));

        return datos;
    }


    @Operation(
        summary = "Crear asignación para un árbitro y partido",
        description = "Crea una nueva asignación y devuelve el ID de la asignación junto con el estado.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Asignación creada correctamente",
                content = @Content(
                    schema = @Schema(
                        example = "{ \"mensaje\": \"Asignación creada correctamente\", \"idAsignacion\": 123, \"status\": \"success\" }"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Error al crear la asignación",
                content = @Content(
                    schema = @Schema(
                        example = "{ \"mensaje\": \"Error al crear la asignación: Árbitro ya asignado\", \"status\": \"error\" }"
                    )
                )
            )
        }
    )
    @PostMapping("/uno")
    public Map<String, Object> crearAsignacion(
            @RequestParam("partidoId") int partidoId,
            @RequestParam("arbitroId") Integer arbitroId
    ) {
        Map<String, Object> respuesta = new HashMap<>();
        try {
            Asignacion asignacion = asignacionService.crearParaArbitroYPartido(arbitroId, partidoId);
            respuesta.put("mensaje", "Asignación creada correctamente");
            respuesta.put("idAsignacion", asignacion.getId());
            respuesta.put("status", "success");
        } catch (IllegalArgumentException e) {
            respuesta.put("mensaje", "Error al crear la asignación: " + e.getMessage());
            respuesta.put("status", "error");
        }
        return respuesta;
    }
}
