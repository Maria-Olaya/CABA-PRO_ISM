package com.proyecto.cabapro.controller;

import com.proyecto.cabapro.model.Arbitro;
import com.proyecto.cabapro.service.ArbitroService;
import com.proyecto.cabapro.service.LiquidacionService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/admin/liquidaciones")
public class LiquidacionAdminController {

    private final ArbitroService arbitroService;
    private final LiquidacionService liquidacionService;

    public LiquidacionAdminController(ArbitroService arbitroService,
                                      LiquidacionService liquidacionService) {
        this.arbitroService = arbitroService;
        this.liquidacionService = liquidacionService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) Integer arbitroId, Model model) {
        List<Arbitro> arbitros = arbitroService.listar();
        model.addAttribute("arbitros", arbitros);

        if (arbitroId != null) {
            Arbitro a = arbitroService.buscar(arbitroId);
            if (a != null) {
                model.addAttribute("arbitro", a);
                model.addAttribute("liquidaciones", liquidacionService.listarPorArbitro(arbitroId));
            } else {
                model.addAttribute("err", "Árbitro no encontrado (id=" + arbitroId + ")");
            }
        }
        return "admin/liquidaciones/list";
    }

    @PostMapping("/{arbitroId}/generar")
    public String generar(@PathVariable Integer arbitroId, RedirectAttributes ra) {
        try {
            var liq = liquidacionService.generarParaArbitro(arbitroId);
            ra.addFlashAttribute("msg", "Liquidación #" + liq.getId() + " generada. Total: " + liq.getTotal());
        } catch (LiquidacionService.DuplicateLiquidacionException d) {
            ra.addFlashAttribute("err", d.getMessage());
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("err", e.getMessage()); // "no hay pendientes"
        } catch (Exception e) {
            ra.addFlashAttribute("err", "Error: " + e.getMessage());
        }
        return "redirect:/admin/liquidaciones?arbitroId=" + arbitroId;
    }

    @PostMapping("/{liqId}/pagar")
    public String pagar(@PathVariable Long liqId, @RequestParam Integer arbitroId, RedirectAttributes ra) {
        try {
            liquidacionService.pagar(liqId);
            ra.addFlashAttribute("msg", "Liquidación #" + liqId + " marcada como PAGADA.");
        } catch (Exception e) {
            ra.addFlashAttribute("err", "Error al pagar: " + e.getMessage());
        }
        return "redirect:/admin/liquidaciones?arbitroId=" + arbitroId;
    }

    @GetMapping("/{liqId}/pdf")
    public void pdf(@PathVariable Long liqId, HttpServletResponse resp) throws Exception {
        byte[] pdf = liquidacionService.obtenerPdf(liqId);
        resp.setContentType("application/pdf");
        String fn = URLEncoder.encode("liquidacion-" + liqId + ".pdf", StandardCharsets.UTF_8);
        resp.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + fn);
        resp.getOutputStream().write(pdf);
        resp.flushBuffer();
    }
}
