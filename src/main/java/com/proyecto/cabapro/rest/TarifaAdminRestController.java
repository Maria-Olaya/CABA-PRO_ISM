package com.proyecto.cabapro.rest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.cabapro.dto.TarifaCalculoRow;
import com.proyecto.cabapro.enums.Escalafon;
import com.proyecto.cabapro.model.Arbitro;
import com.proyecto.cabapro.model.Partido;
import com.proyecto.cabapro.model.Torneo;
import com.proyecto.cabapro.service.PartidoService;
import com.proyecto.cabapro.service.TarifaService;
import com.proyecto.cabapro.service.TorneoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/admin/tarifas")
public class TarifaAdminRestController {

    private final TorneoService torneoService;
    private final PartidoService partidoService;
    private final TarifaService tarifaService;
    private final MessageSource messageSource;

    @Autowired
    public TarifaAdminRestController(
            TorneoService torneoService,
            PartidoService partidoService,
            TarifaService tarifaService,
            MessageSource messageSource) {
        this.torneoService = torneoService;
        this.partidoService = partidoService;
        this.tarifaService = tarifaService;
        this.messageSource = messageSource;
    }


     // ================= ASIGNAR TARIFAS =================
    @Operation(
        summary = "Generar y listar tarifas de árbitros",
        description = "Devuelve las tarifas calculadas para los árbitros de un torneo específico.\n"
                    + "Si no se pasa torneoId, devuelve solo los torneos disponibles.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Tarifas generadas correctamente",
                content = @Content(
                    schema = @Schema(
                        example = "{"
                                + "\"torneos\": [{ \"idTorneo\": 1, \"nombre\": \"Torneo A\" }], "
                                + "\"torneo\": { \"idTorneo\": 1, \"nombre\": \"Torneo A\", \"categoria\": \"UNIVERSITARIO\" }, "
                                + "\"filas\": ["
                                + "{ \"partido\": { \"id\": 101 }, \"arbitro\": { \"id\": 10, \"nombre\": \"Juan\" }, \"base\": 100000, \"adicional\": 20000, \"total\": 120000 }"
                                + "], "
                                + "\"resumen\": ["
                                + "{ \"arbitro\": { \"id\": 10, \"nombre\": \"Juan\" }, \"total\": 120000 }"
                                + "]"
                                + "}"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Error por torneoId inexistente o no especificado",
                content = @Content(
                    schema = @Schema(
                        example = "{ \"message\": \"Debe especificar un torneoId para generar tarifas.\" }"
                    )
                )
            )
        }
    )
    @GetMapping("/asignar")
    public Object asignar(@RequestParam(required = false) Integer torneoId, Locale locale) {

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("torneos", torneoService.listarTorneos());

        if (torneoId == null) {
            response.put("message", "Debe especificar un torneoId para generar tarifas.");
            return response;
        }

        Torneo torneo = torneoService.obtenerPorId(torneoId);
        if (torneo == null) {
            String errorMsg = messageSource.getMessage("error.torneoNoEncontrado", null, locale);
            response.put("error", errorMsg);
            return response;
        }

        tarifaService.generarAutomaticoParaTorneo(torneoId);

        List<Partido> partidos = partidoService.getPartidosByTorneo(torneo.getIdTorneo());

        List<TarifaCalculoRow> filas = new ArrayList<>();
        Map<Integer, BigDecimal> totalesPorArbitro = new LinkedHashMap<>();
        Map<Integer, Arbitro> arbitroById = new HashMap<>();

        for (Partido p : partidos) {
            BigDecimal base = tarifaService.baseCategoria(torneo.getCategoria());
            for (Arbitro a : p.getArbitros()) {
                Escalafon esc = a.getEscalafon();
                BigDecimal adicional = tarifaService.adicionalEscalafon(torneo.getCategoria(), esc);
                BigDecimal total = tarifaService.totalPor(torneo.getCategoria(), esc);

                filas.add(new TarifaCalculoRow(p, a, base, adicional, total));

                arbitroById.putIfAbsent(a.getId(), a);
                totalesPorArbitro.merge(a.getId(), total, BigDecimal::add);
            }
        }

        List<Map<String, Object>> resumen = new ArrayList<>();
        totalesPorArbitro.forEach((id, total) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("arbitro", arbitroById.get(id));
            item.put("total", total);
            resumen.add(item);
        });

        response.put("torneo", torneo);
        response.put("filas", filas);
        response.put("resumen", resumen);

        return response;
    }
}
