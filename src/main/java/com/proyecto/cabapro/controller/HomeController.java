package com.proyecto.cabapro.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.proyecto.cabapro.model.Arbitro;
import com.proyecto.cabapro.model.Usuario;
import com.proyecto.cabapro.repository.UsuarioRepository;

@Controller
public class HomeController {

    private final UsuarioRepository usuarioRepository;

    public HomeController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "redirect:/admin/arbitros"; 
    }

    @GetMapping("/arbitro/dashboard")
    public String arbitroDashboard(Principal principal, Model model) {
        if (principal != null) {
            String correo = principal.getName();
            Optional<Usuario> opt = usuarioRepository.findByCorreo(correo);

            if (opt.isPresent() && opt.get() instanceof Arbitro) {
                model.addAttribute("arbitro", (Arbitro) opt.get());
            }
        }
        return "arbitro/home";
    }
}
