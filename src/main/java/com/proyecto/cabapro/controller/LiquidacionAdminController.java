package com.proyecto.cabapro.controller;

import com.proyecto.cabapro.model.Arbitro;
import com.proyecto.cabapro.service.ArbitroService;
import com.proyecto.cabapro.service.LiquidacionService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder; 
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/admin/liquidaciones")
public class LiquidacionAdminController {

    private final ArbitroService arbitroService;
    private final LiquidacionService liquidacionService;
    private final MessageSource messageSource; 

    public LiquidacionAdminController(ArbitroService arbitroService,
                                      LiquidacionService liquidacionService,
                                      MessageSource messageSource) { 
        this.arbitroService = arbitroService;
        this.liquidacionService = liquidacionService;
        this.messageSource = messageSource; 
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
                Locale locale = LocaleContextHolder.getLocale();
                String errMsg = messageSource.getMessage("error.arbitroNoEncontradoId", new Object[]{arbitroId}, locale); // 🔄 CAMBIO
                model.addAttribute("err", errMsg);
            }
        }
        return "admin/liquidaciones/list";
    }

    @PostMapping("/{arbitroId}/generar")
    public String generar(@PathVariable Integer arbitroId, RedirectAttributes ra) {
        Locale locale = LocaleContextHolder.getLocale(); 
        try {
            var liq = liquidacionService.generarParaArbitro(arbitroId);
            String msg = messageSource.getMessage("msg.liquidacionGenerada",
                    new Object[]{liq.getId(), liq.getTotal()}, locale); 
            ra.addFlashAttribute("msg", msg);
        } catch (LiquidacionService.DuplicateLiquidacionException d) {
            ra.addFlashAttribute("err", d.getMessage());
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("err", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); 
            String msg = messageSource.getMessage("error.generico", new Object[]{e.getMessage()}, locale); // 🔄 CAMBIO
            ra.addFlashAttribute("err", msg);
        }
        return "redirect:/admin/liquidaciones?arbitroId=" + arbitroId;
    }

    @PostMapping("/{liqId}/pagar")
    public String pagar(@PathVariable Long liqId, @RequestParam Integer arbitroId, RedirectAttributes ra) {
        Locale locale = LocaleContextHolder.getLocale();
        try {
            liquidacionService.pagar(liqId);
            String msg = messageSource.getMessage("msg.liquidacionPagada", new Object[]{liqId}, locale); 
            ra.addFlashAttribute("msg", msg);
        } catch (Exception e) {
            String msg = messageSource.getMessage("error.alPagar", new Object[]{e.getMessage()}, locale); 
            ra.addFlashAttribute("err", msg);
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

