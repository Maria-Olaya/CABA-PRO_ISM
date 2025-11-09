// NUEVO - si - si MODFICADO
package com.proyecto.cabapro.rest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.cabapro.model.Arbitro;
import com.proyecto.cabapro.service.ArbitroService;
import com.proyecto.cabapro.service.LiquidacionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/arbitro/liquidaciones")
public class LiquidacionArbitroRestController {

    private final ArbitroService arbitroService;
    private final LiquidacionService liquidacionService;

    public LiquidacionArbitroRestController(ArbitroService arbitroService,
                                            LiquidacionService liquidacionService) {
        this.arbitroService = arbitroService;
        this.liquidacionService = liquidacionService;
    }

    /**
     * 🔹 Obtiene las liquidaciones del árbitro autenticado
     */
    // ================= LISTAR =================
    @Operation(
        summary = "Listar mis liquidaciones",
        description = "Devuelve las liquidaciones correspondientes al árbitro actualmente autenticado.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Liquidaciones obtenidas correctamente",
                content = @Content(
                    schema = @Schema(
                        example = "{"
                                + "\"arbitroId\": 1, "
                                + "\"cantidad\": 3, "
                                + "\"liquidaciones\": ["
                                + "{ \"id\": 10, \"total\": 150000, \"estado\": \"PENDIENTE\" },"
                                + "{ \"id\": 11, \"total\": 200000, \"estado\": \"PAGADA\" },"
                                + "{ \"id\": 12, \"total\": 175000, \"estado\": \"PENDIENTE\" }"
                                + "]"
                                + "}"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Error al obtener las liquidaciones",
                content = @Content(
                    schema = @Schema(
                        example = "{ \"error\": \"No se pudieron obtener las liquidaciones\", "
                                + "\"detalle\": \"<mensaje de excepción>\" }"
                    )
                )
            )
        }
    )
    
    @GetMapping
    public ResponseEntity<?> listarMisLiquidaciones(@AuthenticationPrincipal User principal) {
        try {
            Arbitro arbitro = arbitroService.getActual(principal.getUsername());
            List<?> liquidaciones = liquidacionService.listarPorArbitro(arbitro.getId());
            return ResponseEntity.ok(Map.of(
                    "arbitroId", arbitro.getId(),
                    "cantidad", liquidaciones.size(),
                    "liquidaciones", liquidaciones
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "No se pudieron obtener las liquidaciones",
                            "detalle", e.getMessage()
                    ));
        }
    }

    /**
     * 🔹 Devuelve el PDF de una liquidación específica del árbitro autenticado
     */
    // ================= DESCARGAR PDF =================
    @Operation(
        summary = "Obtener PDF de una liquidación",
        description = "Genera y devuelve el PDF de una liquidación específica del árbitro autenticado.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "PDF generado correctamente",
                content = @Content(
                    mediaType = "application/pdf"
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Liquidación no encontrada",
                content = @Content(
                    schema = @Schema(
                        example = "{}"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Error al generar el PDF",
                content = @Content(
                    schema = @Schema(
                        example = "{}"
                    )
                )
            )
        }
    )
@GetMapping("/{liqId}/pdf")
public ResponseEntity<Resource> obtenerPdf(@PathVariable Long liqId, @AuthenticationPrincipal User principal) {
    try {
        // 1️⃣ Obtener el árbitro autenticado
        Arbitro arbitro = arbitroService.getActual(principal.getUsername());

        // 2️⃣ Verificar que la liquidación pertenezca a ese árbitro
        var liquidacion = liquidacionService.buscarPorId(liqId);
        if (liquidacion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (liquidacion.getArbitro() == null || liquidacion.getArbitro().getId() != arbitro.getId()) {
              return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new ByteArrayResource("No tienes permiso para ver esta liquidación.".getBytes()));
       }

        // 3️⃣ Generar el PDF si la liquidación sí pertenece al árbitro
        byte[] pdf = liquidacionService.obtenerPdf(liqId);

        if (pdf == null || pdf.length == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        String filename = URLEncoder.encode("mi-liquidacion-" + liqId + ".pdf", StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(pdf);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename)
                .contentLength(pdf.length)
                .body(resource);

    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ByteArrayResource(("Error: " + e.getMessage()).getBytes()));
    }
}

}
