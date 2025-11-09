package com.proyecto.cabapro.controller;

import com.proyecto.cabapro.service.AsignacionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/arbitro/asignaciones")
public class AsignacionController {

    private final AsignacionService asignacionService;

    public AsignacionController(AsignacionService asignacionService) {
        this.asignacionService = asignacionService;
    }

    @GetMapping
    public String vista(@AuthenticationPrincipal User principal, Model model) {
        String correo = principal.getUsername();
        model.addAttribute("arbitro", asignacionService.getArbitroActual(correo));
        model.addAttribute("asignaciones", asignacionService.listarDelActual(correo));
        return "arbitro/asignacion"; 
    }

    @PostMapping("/{id}/aceptar")
    public String aceptar(@AuthenticationPrincipal User principal,
                          @PathVariable Long id,
                          RedirectAttributes ra) {
        try {
            asignacionService.aceptar(principal.getUsername(), id);
            ra.addFlashAttribute("msgCode", "flash.asignacion.aceptada");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("errCode", "flash.asignacion.error");
            ra.addFlashAttribute("errArg0", ex.getMessage());
        }
        return "redirect:/arbitro/asignaciones";
    }

    @PostMapping("/{id}/rechazar")
    public String rechazar(@AuthenticationPrincipal User principal,
                           @PathVariable Long id,
                           RedirectAttributes ra) {
        try {
            asignacionService.rechazar(principal.getUsername(), id);
            ra.addFlashAttribute("msgCode", "flash.asignacion.rechazada");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("errCode", "flash.asignacion.error");
            ra.addFlashAttribute("errArg0", ex.getMessage());
        }
        return "redirect:/arbitro/asignaciones";
    }
}
