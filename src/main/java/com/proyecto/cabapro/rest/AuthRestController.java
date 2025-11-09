package com.proyecto.cabapro.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.cabapro.controller.forms.RegisterForm;
import com.proyecto.cabapro.model.Usuario;
import com.proyecto.cabapro.repository.UsuarioRepository;
import com.proyecto.cabapro.security.JwtTokenProvider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // --- LOGIN ---
    // ================= LOGIN =================
    // ================= LOGIN =================
    @Operation(
        summary = "Login de usuario",
        description = "Permite a un usuario autenticarse y obtener un token JWT.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Credenciales de login",
            required = true,
            content = @Content(
                schema = @Schema(
                    example = "{ \"correo\": \"admin@liga.com\", \"contrasena\": \"123456\" }"
                )
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Login exitoso",
                content = @Content(
                    schema = @Schema(
                        example = "{ \"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\", "
                                + "\"correo\": \"admin@liga.com\", "
                                + "\"rol\": \"ADMIN\" }"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Error de credenciales",
                content = @Content(
                    schema = @Schema(
                        example = "{ \"error\": \"Usuario no encontrado\" }"
                    )
                )
            )
        }
    )
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> request) {
        String correo = request.get("correo");
        String contrasena = request.get("contrasena");

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(contrasena, usuario.getContrasena())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String token = jwtTokenProvider.createToken(usuario.getCorreo(), usuario.getRol());

        return Map.of(
                "token", token,
                "correo", usuario.getCorreo(),
                "rol", usuario.getRol()
        );
    }

    // --- REGISTRO ---
    // ================= REGISTRO =================
    @Operation(
        summary = "Registro de usuario",
        description = "Permite registrar un nuevo usuario en el sistema."
                    + "Campos requeridos según RegisterForm:\n"
                    + "- nombre: mínimo 2, máximo 50 caracteres, solo letras y espacios.\n"
                    + "- apellido: mínimo 2, máximo 50 caracteres, solo letras y espacios.\n"
                    + "- correo: formato de correo válido.\n"
                    + "- contrasena: mínimo 6 caracteres.\n"
                    + "- confirmContrasena: debe coincidir con contrasena.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos de registro",
            required = true,
            content = @Content(
                schema = @Schema(
                    example = "{ \"nombre\": \"Juan\", "
                            + "\"apellido\": \"Perez\", "
                            + "\"correo\": \"juan.perez@liga.com\", "
                            + "\"contrasena\": \"123456\", "
                            + "\"confirmContrasena\": \"123456\" }"
                )
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Usuario registrado correctamente",
                content = @Content(
                    schema = @Schema(
                        example = "{ \"mensaje\": \"Usuario registrado correctamente\", "
                                + "\"correo\": \"juan.perez@liga.com\", "
                                + "\"rol\": \"ADMIN\", "
                                + "\"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\" }"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Error de validación",
                content = @Content(
                    schema = @Schema(
                        example = "{ \"error\": \"Las contraseñas no coinciden\" }"
                    )
                )
            )
        }
    )
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody @Valid RegisterForm form) {

        // Validación de contraseñas
        if (!form.getContrasena().equals(form.getConfirmContrasena())) {
            throw new RuntimeException("Las contraseñas no coinciden");
        }

        // Validación de correo duplicado
        if (usuarioRepository.findByCorreo(form.getCorreo()).isPresent()) {
            throw new RuntimeException("Ya existe un usuario con ese correo");
        }

        // Creación del usuario
        Usuario nuevo = new Usuario();
        nuevo.setNombre(form.getNombre());
        nuevo.setApellido(form.getApellido());
        nuevo.setCorreo(form.getCorreo());
        nuevo.setContrasena(passwordEncoder.encode(form.getContrasena()));
        nuevo.setRol("ADMIN"); // Rol por defecto

        usuarioRepository.save(nuevo);

        // Generar token JWT al registrar
        String token = jwtTokenProvider.createToken(nuevo.getCorreo(), nuevo.getRol());

        return Map.of(
                "mensaje", "Usuario registrado correctamente",
                "correo", nuevo.getCorreo(),
                "rol", nuevo.getRol(),
                "token", token
        );
    }
}
