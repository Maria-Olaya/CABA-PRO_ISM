package com.proyecto.cabapro.controller;

import com.proyecto.cabapro.dto.TarifaCalculoRow;
import com.proyecto.cabapro.enums.Escalafon;
import com.proyecto.cabapro.model.Arbitro;
import com.proyecto.cabapro.model.Partido;
import com.proyecto.cabapro.model.Torneo;
import com.proyecto.cabapro.service.PartidoService;
import com.proyecto.cabapro.service.TarifaService;
import com.proyecto.cabapro.service.TorneoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/admin/tarifas")
public class TarifaAdminController {

    private final TorneoService torneoService;
    private final PartidoService partidoService;
    private final TarifaService tarifaService;

    public TarifaAdminController(TorneoService torneoService,
                                 PartidoService partidoService,
                                 TarifaService tarifaService) {
        this.torneoService = torneoService;
        this.partidoService = partidoService;
        this.tarifaService = tarifaService;
    }

    @GetMapping("/asignar")
    public String asignar(@RequestParam(required = false) Integer torneoId, Model model) {
        model.addAttribute("torneos", torneoService.listarTorneos());

        if (torneoId == null) {
            return "admin/tarifas/asignar";
        }

        Torneo torneo = torneoService.obtenerPorId(torneoId);
        if (torneo == null) {
            model.addAttribute("err", "Torneo no encontrado.");
            return "admin/tarifas/asignar";
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

        model.addAttribute("torneo", torneo);
        model.addAttribute("filas", filas);

        List<Map.Entry<Arbitro, BigDecimal>> resumen = new ArrayList<>();
        totalesPorArbitro.forEach((id, total) -> resumen.add(Map.entry(arbitroById.get(id), total)));
        model.addAttribute("resumen", resumen);

        return "admin/tarifas/asignar";
    }
}
