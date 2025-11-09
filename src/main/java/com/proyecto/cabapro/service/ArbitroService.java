package com.proyecto.cabapro.service;

import com.proyecto.cabapro.enums.EstadoAsignacion;
import com.proyecto.cabapro.model.Arbitro;
import com.proyecto.cabapro.model.Asignacion;
import com.proyecto.cabapro.repository.ArbitroRepository;
import com.proyecto.cabapro.repository.AsignacionRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ArbitroService {

    // ===== Excepciones =====
    public static class DuplicateEmailException extends RuntimeException {
        public DuplicateEmailException(String message) {
            super(message);
        }
    }

    public static class PasswordRequiredOnCreateException extends RuntimeException {
        public PasswordRequiredOnCreateException(String message) {
            super(message);
        }
    }

    private final ArbitroRepository arbitroRepo;
    private final AsignacionRepository asignacionRepo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public ArbitroService(ArbitroRepository arbitroRepo, AsignacionRepository asignacionRepo) {
        this.arbitroRepo = arbitroRepo;
        this.asignacionRepo = asignacionRepo;
    }

    // =============== (DESDE ADMIN) ===============

    public List<Arbitro> listar() {
        return arbitroRepo.findAll();
    }

    public Arbitro buscar(Integer id) {
        return arbitroRepo.findById(id).orElse(null);
    }

    public Arbitro crear(Arbitro a) {
      
        if (a.getCorreo() == null || a.getCorreo().isBlank()) {
            throw new IllegalArgumentException("El correo es obligatorio");
        }
        if (arbitroRepo.existsByCorreoIgnoreCase(a.getCorreo())) {
            throw new DuplicateEmailException("El correo ya está en uso por otro usuario");
        }
        if (a.getContrasena() == null || a.getContrasena().isBlank()) {
            throw new PasswordRequiredOnCreateException("La contraseña es obligatoria al crear el árbitro");
        }

        a.setContrasena(encoder.encode(a.getContrasena()));
        if (a.getRol() == null || a.getRol().isBlank()) {
            a.setRol("ROLE_ARBITRO");
        }
        return arbitroRepo.save(a);
    }

    public Arbitro actualizar(Integer id, Arbitro datos) {
        Arbitro actual = buscar(id);
        if (actual == null) return null;

        if (datos.getCorreo() == null || datos.getCorreo().isBlank()) {
            throw new IllegalArgumentException("El correo es obligatorio");
        }
        if (!datos.getCorreo().equalsIgnoreCase(actual.getCorreo())) {
            if (arbitroRepo.existsByCorreoIgnoreCaseAndIdNot(datos.getCorreo(), id)) {
                throw new DuplicateEmailException("El correo ya está en uso por otro usuario");
            }
        }

        actual.setNombre(datos.getNombre());
        actual.setApellido(datos.getApellido());
        actual.setCorreo(datos.getCorreo());

        
        if (datos.getRol() != null && !datos.getRol().isBlank()) {
            actual.setRol(datos.getRol());
        }
        if (actual.getRol() == null || actual.getRol().isBlank()) {
            actual.setRol("ROLE_ARBITRO");
        }

        
        if (datos.getContrasena() != null && !datos.getContrasena().isBlank()) {
            actual.setContrasena(encoder.encode(datos.getContrasena()));
        }

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

        Set<LocalDate> resultado = new HashSet<>();
        if (nuevasFechas != null) resultado.addAll(nuevasFechas);
        resultado.addAll(fechasBloqueadas(a));

        a.getFechasDisponibles().clear();
        a.getFechasDisponibles().addAll(resultado);

        arbitroRepo.save(a);
    }

    // =============== FECHAS BLOQUEADAS ===============

    @Transactional(readOnly = true)
    public Set<LocalDate> fechasBloqueadas(Arbitro a) {
        return asignacionRepo.findByArbitroAndEstado(a, EstadoAsignacion.ACEPTADA)
                .stream()
                .map(Asignacion::getFechaAsignacion)
                .collect(Collectors.toSet());
    }
}
