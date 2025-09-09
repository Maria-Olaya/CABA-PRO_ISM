package com.proyecto.cabapro.service;

import com.proyecto.cabapro.model.Arbitro;
import com.proyecto.cabapro.repository.ArbitroRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ArbitroService {

    // ===== Excepciones de negocio locales al Service =====
    public static class DuplicateEmailException extends RuntimeException {
        public DuplicateEmailException(String message) { super(message); }
    }
    public static class PasswordRequiredOnCreateException extends RuntimeException {
        public PasswordRequiredOnCreateException(String message) { super(message); }
    }

    private final ArbitroRepository arbitroRepo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public ArbitroService(ArbitroRepository arbitroRepo) {
        this.arbitroRepo = arbitroRepo;
    }

    // =============== CRUD (ADMIN) ===============

    public List<Arbitro> listar() {
        return arbitroRepo.findAll();
    }

    public Arbitro buscar(Integer id) {
        return arbitroRepo.findById(id).orElse(null);
    }

    public Arbitro crear(Arbitro a) {
        // Reglas de negocio
        if (a.getCorreo() == null || a.getCorreo().isBlank()) {
            throw new IllegalArgumentException("El correo es obligatorio");
        }
        if (arbitroRepo.existsByCorreoIgnoreCase(a.getCorreo())) {
            throw new DuplicateEmailException("El correo ya está en uso por otro usuario");
        }
        if (a.getContrasena() == null || a.getContrasena().isBlank()) {
            throw new PasswordRequiredOnCreateException("La contraseña es obligatoria al crear el árbitro");
        }

        // Transformaciones
        a.setContrasena(encoder.encode(a.getContrasena()));
        if (a.getRol() == null || a.getRol().isBlank()) {
            a.setRol("ROLE_ARBITRO");
        }
        return arbitroRepo.save(a);
    }

    public Arbitro actualizar(Integer id, Arbitro datos) {
        Arbitro actual = buscar(id);
        if (actual == null) return null;

        // Reglas: correo único (si cambió)
        if (datos.getCorreo() == null || datos.getCorreo().isBlank()) {
            throw new IllegalArgumentException("El correo es obligatorio");
        }
        if (!datos.getCorreo().equalsIgnoreCase(actual.getCorreo())) {
            if (arbitroRepo.existsByCorreoIgnoreCaseAndIdNot(datos.getCorreo(), id)) {
                throw new DuplicateEmailException("El correo ya está en uso por otro usuario");
            }
        }

        // Usuario
        actual.setNombre(datos.getNombre());
        actual.setApellido(datos.getApellido());
        actual.setCorreo(datos.getCorreo());

        // Rol: mantener si no envían, y nunca dejar vacío
        if (datos.getRol() != null && !datos.getRol().isBlank()) {
            actual.setRol(datos.getRol());
        }
        if (actual.getRol() == null || actual.getRol().isBlank()) {
            actual.setRol("ROLE_ARBITRO");
        }

        // Contraseña solo si llega no vacía
        if (datos.getContrasena() != null && !datos.getContrasena().isBlank()) {
            actual.setContrasena(encoder.encode(datos.getContrasena()));
        }

        // Árbitro
        actual.setUrlFoto(datos.getUrlFoto());
        actual.setEspecialidad(datos.getEspecialidad());
        actual.setEscalafon(datos.getEscalafon());

        if (datos.getFechasDisponibles() != null) {
            actual.getFechasDisponibles().clear();
            actual.getFechasDisponibles().addAll(datos.getFechasDisponibles());
        }

        return arbitroRepo.save(actual);
    }

    public void eliminar(Integer id) {
        Arbitro a = buscar(id);
        if (a != null) {
            arbitroRepo.delete(a);
        }
    }

    // =============== PERFIL (ÁRBITRO) ===============
    public Arbitro getActual(String correo) {
        return arbitroRepo.findByCorreo(correo)
                .orElseThrow(() -> new IllegalArgumentException("Árbitro no encontrado para correo: " + correo));
    }

    public void actualizarPerfil(String correo, String urlFoto, Set<LocalDate> nuevasFechas) {
        Arbitro a = getActual(correo);
        a.setUrlFoto(urlFoto);

        a.getFechasDisponibles().clear();
        if (nuevasFechas != null) {
            a.getFechasDisponibles().addAll(nuevasFechas);
        }

        arbitroRepo.save(a);
    }
}
