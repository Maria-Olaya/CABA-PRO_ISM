package com.proyecto.cabapro.controller;

import com.proyecto.cabapro.service.AsignacionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/arbitro/asignaciones")
public class AsignacionController {

    private final AsignacionService asignacionService;

    public AsignacionController(AsignacionService asignacionService) {
        this.asignacionService = asignacionService;
    }

    @GetMapping
    public String vista(@AuthenticationPrincipal User principal,
                        @RequestParam(value = "msg", required = false) String msg,
                        @RequestParam(value = "err", required = false) String err,
                        Model model) {

        String correo = principal.getUsername();

        model.addAttribute("arbitro", asignacionService.getArbitroActual(correo));
        model.addAttribute("asignaciones", asignacionService.listarDelActual(correo));
        model.addAttribute("msg", msg);
        model.addAttribute("err", err);
        return "arbitro/asignacion"; // templates/arbitro/asignacion.html
    }

    @PostMapping("/{id}/aceptar")
    public String aceptar(@AuthenticationPrincipal User principal, @PathVariable Long id) {
        try {
            asignacionService.aceptar(principal.getUsername(), id);
            return "redirect:/arbitro/asignaciones?msg=Asignacion aceptada";
        } catch (IllegalArgumentException ex) {
            return "redirect:/arbitro/asignaciones?err=" + ex.getMessage();
        }
    }

    @PostMapping("/{id}/rechazar")
    public String rechazar(@AuthenticationPrincipal User principal, @PathVariable Long id) {
        try {
            asignacionService.rechazar(principal.getUsername(), id);
            return "redirect:/arbitro/asignaciones?msg=Asignacion rechazada";
        } catch (IllegalArgumentException ex) {
            return "redirect:/arbitro/asignaciones?err=" + ex.getMessage();
        }
    }
}
