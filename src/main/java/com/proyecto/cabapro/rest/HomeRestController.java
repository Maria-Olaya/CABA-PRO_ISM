// NUEVO - si
package com.proyecto.cabapro.rest;

import java.security.Principal;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.cabapro.model.Arbitro;
import com.proyecto.cabapro.model.Usuario;
import com.proyecto.cabapro.repository.UsuarioRepository;

@RestController
@RequestMapping("/api")
public class HomeRestController {

    private final UsuarioRepository usuarioRepository;

    public HomeRestController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Ruta principal
    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Bienvenido a la API de CabaPro");
    }

    // Dashboard de admin
    @GetMapping("/admin/dashboard")//bien
    public ResponseEntity<String> adminDashboard() {
        // En una API REST no se redirige, solo se devuelve un mensaje o datos
        return ResponseEntity.ok("Dashboard del administrador - usa /api/admin/arbitros para gestionar árbitros");
    }

    // Dashboard de árbitro
    @GetMapping("/arbitro/dashboard")//bien
    public ResponseEntity<?> arbitroDashboard(Principal principal) {
        if (principal != null) {
            String correo = principal.getName();
            Optional<Usuario> opt = usuarioRepository.findByCorreo(correo);

            if (opt.isPresent() && opt.get() instanceof Arbitro) {
                Arbitro arbitro = (Arbitro) opt.get();
                return ResponseEntity.ok(arbitro);
            } else {
                return ResponseEntity.status(403).body("Usuario no autorizado o no es árbitro");
            }
        }
        return ResponseEntity.status(401).body("No se encontró usuario autenticado");
    }
}
