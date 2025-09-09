package com.proyecto.cabapro.controller;

import com.proyecto.cabapro.model.Arbitro;
import com.proyecto.cabapro.service.ArbitroService;
import com.proyecto.cabapro.service.LiquidacionService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/arbitro/liquidaciones")
public class LiquidacionArbitroController {

    private final ArbitroService arbitroService;
    private final LiquidacionService liquidacionService;

    public LiquidacionArbitroController(ArbitroService arbitroService,
                                        LiquidacionService liquidacionService) {
        this.arbitroService = arbitroService;
        this.liquidacionService = liquidacionService;
    }

    @GetMapping
    public String mis(@AuthenticationPrincipal User principal, Model model) {
        Arbitro a = arbitroService.getActual(principal.getUsername());
        model.addAttribute("liquidaciones", liquidacionService.listarPorArbitro(a.getId()));
        return "arbitro/liquidaciones/mis";
    }

    @GetMapping("/{liqId}/pdf")
    public void pdf(@PathVariable Long liqId, HttpServletResponse resp) throws Exception {
        byte[] pdf = liquidacionService.obtenerPdf(liqId);
        resp.setContentType("application/pdf");

        String fn = URLEncoder.encode("mi-liquidacion-" + liqId + ".pdf", StandardCharsets.UTF_8);
        resp.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + fn);

        if (pdf != null) {
            resp.getOutputStream().write(pdf);
        }

        resp.flushBuffer();
    }
}
