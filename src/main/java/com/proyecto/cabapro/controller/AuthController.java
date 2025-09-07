package com.proyecto.cabapro.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.proyecto.cabapro.controller.forms.RegisterForm;
import com.proyecto.cabapro.model.Administrador;
import com.proyecto.cabapro.repository.UsuarioRepository;
import com.proyecto.cabapro.service.CustomUserDetailsService;

import jakarta.validation.Valid;

@Controller
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;

    public AuthController(UsuarioRepository usuarioRepository , CustomUserDetailsService customUserDetailsService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.customUserDetailsService = customUserDetailsService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String mostrarFormulario(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@Valid @ModelAttribute("registerForm") RegisterForm form,
                                BindingResult result) {
        if (result.hasErrors()) {
            return "registro";
        }

        if(customUserDetailsService.correoExiste(form.getCorreo())) {
            result.rejectValue("correo", "error.registerForm", "El correo ya está en uso");
            return "registro";
        }



        Administrador admin = new Administrador(
                form.getNombre(),
                form.getApellido(),
                form.getCorreo(),
                passwordEncoder.encode(form.getContrasena()),
                "ROLE_ADMIN"
        );

        usuarioRepository.save(admin);

        return "redirect:/login";
    }

}
