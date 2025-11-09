package com.proyecto.cabapro.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.cabapro.service.AsignacionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/arbitro/asignaciones")//bien
public class AsignacionArbitroRestController {

    private final AsignacionService asignacionService;

    public AsignacionArbitroRestController(AsignacionService asignacionService) {
        this.asignacionService = asignacionService;
    }

    @Operation(
        summary = "Listar asignaciones del árbitro autenticado",
        description = "Devuelve el árbitro actual y sus asignaciones con el estado.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Asignaciones obtenidas correctamente",
                content = @Content(
                    schema = @Schema(
                        example = "{ " +
                                  "\"arbitro\": { \"id\": 10, \"nombre\": \"Carlos\", \"especialidad\": \"PRINCIPAL\" }, " +
                                  "\"asignaciones\": [ " +
                                  "{ \"id\": 5, \"partidoId\": 1, \"estado\": \"PENDIENTE\" }, " +
                                  "{ \"id\": 6, \"partidoId\": 2, \"estado\": \"ACEPTADA\" } " +
                                  "]" +
                                  "}"
                    )
                )
            )
        }
    )
    @GetMapping
    public Map<String, Object> listarAsignaciones(@AuthenticationPrincipal User principal) {
        String correo = principal.getUsername();
        Map<String, Object> response = new HashMap<>();
        response.put("arbitro", asignacionService.getArbitroActual(correo));
        response.put("asignaciones", asignacionService.listarDelActual(correo));
        return response;
    }

    @Operation(
        summary = "Aceptar asignación",
        description = "El árbitro acepta una asignación específica.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Asignación aceptada correctamente",
                content = @Content(
                    schema = @Schema(
                        example = "{ \"status\": \"success\", \"message\": \"Asignación aceptada correctamente.\" }"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Error al aceptar la asignación",
                content = @Content(
                    schema = @Schema(
                        example = "{ \"status\": \"error\", \"message\": \"Asignación ya fue aceptada previamente\" }"
                    )
                )
            )
        }
    )
    @PostMapping("/{id}/aceptar")
    public Map<String, String> aceptarAsignacion(@AuthenticationPrincipal User principal,
                                                 @PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            asignacionService.aceptar(principal.getUsername(), id);
            response.put("status", "success");
            response.put("message", "Asignación aceptada correctamente.");
        } catch (IllegalArgumentException ex) {
            response.put("status", "error");
            response.put("message", ex.getMessage());
        }
        return response;
    }

    @Operation(
        summary = "Rechazar asignación",
        description = "El árbitro rechaza una asignación específica.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Asignación rechazada correctamente",
                content = @Content(
                    schema = @Schema(
                        example = "{ \"status\": \"success\", \"message\": \"Asignación rechazada correctamente.\" }"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Error al rechazar la asignación",
                content = @Content(
                    schema = @Schema(
                        example = "{ \"status\": \"error\", \"message\": \"Asignación ya fue rechazada previamente\" }"
                    )
                )
            )
        }
    )
    @PostMapping("/{id}/rechazar")
    public Map<String, String> rechazarAsignacion(@AuthenticationPrincipal User principal,
                                                  @PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            asignacionService.rechazar(principal.getUsername(), id);
            response.put("status", "success");
            response.put("message", "Asignación rechazada correctamente.");
        } catch (IllegalArgumentException ex) {
            response.put("status", "error");
            response.put("message", ex.getMessage());
        }
        return response;
    }
}
