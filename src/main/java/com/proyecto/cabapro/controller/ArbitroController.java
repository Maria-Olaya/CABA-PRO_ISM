package com.proyecto.cabapro.controller;

import com.proyecto.cabapro.model.Arbitro;
import com.proyecto.cabapro.service.ArbitroService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Set;

@Controller
@RequestMapping("/arbitro/edit")
public class ArbitroController {

    private final ArbitroService arbitroService;

    public ArbitroController(ArbitroService arbitroService) {
        this.arbitroService = arbitroService;
    }

    
    @GetMapping
    public String verPerfil(
            @AuthenticationPrincipal(expression = "username") String correo,
            Model model
    ) {
        Arbitro arbitro = arbitroService.getActual(correo);

        
        Set<LocalDate> bloqueadas = arbitroService.fechasBloqueadas(arbitro);

        model.addAttribute("arbitro", arbitro);
        model.addAttribute("bloqueadas", bloqueadas);

        return "arbitro/perfil"; 
    }

    
    @PostMapping
    public String actualizarPerfil(
            @AuthenticationPrincipal(expression = "username") String correo,
            @ModelAttribute("arbitro") Arbitro form,
            BindingResult binding,
            Model model,
            RedirectAttributes ra
    ) {
        if (binding.hasErrors()) {
            Arbitro actual = arbitroService.getActual(correo);
            Set<LocalDate> bloqueadas = arbitroService.fechasBloqueadas(actual);
            model.addAttribute("bloqueadas", bloqueadas);
            return "arbitro/perfil";
        }

        try {
            arbitroService.actualizarPerfil(
                    correo,
                    form.getUrlFoto(),
                    form.getFechasDisponibles()
            );
            ra.addFlashAttribute("msgCode", "perfil.actualizado");
            return "redirect:/arbitro/dashboard";

        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("errCode", "perfil.error");
            ra.addFlashAttribute("errArg0", ex.getMessage());
            return "redirect:/arbitro/perfil";
        }
    }
}
