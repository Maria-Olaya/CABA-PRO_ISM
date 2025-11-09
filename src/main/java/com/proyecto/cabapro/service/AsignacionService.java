package com.proyecto.cabapro.service;

import com.proyecto.cabapro.enums.Especialidad;
import com.proyecto.cabapro.enums.EstadoAsignacion;
import com.proyecto.cabapro.model.Arbitro;
import com.proyecto.cabapro.model.Asignacion;
import com.proyecto.cabapro.model.Partido;
import com.proyecto.cabapro.repository.AsignacionRepository;
import com.proyecto.cabapro.repository.PartidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AsignacionService {

    private final AsignacionRepository asignacionRepo;
    private final PartidoRepository partidoRepo;
    private final ArbitroService arbitroService;
    private final MailService mailService;
    private final TarifaService tarifaService;
    private final MessageSource messageSource;

    public AsignacionService(
            AsignacionRepository asignacionRepo,
            PartidoRepository partidoRepo,
            ArbitroService arbitroService,
            MailService mailService,
            TarifaService tarifaService,
            MessageSource messageSource
    ) {
        this.asignacionRepo = asignacionRepo;
        this.partidoRepo = partidoRepo;
        this.arbitroService = arbitroService;
        this.tarifaService = tarifaService;
        this.messageSource = messageSource;
        this.mailService = mailService;
    }

    // ============== CREATE / UPDATE (dominio) ==============

  
    public Asignacion crearParaArbitroYPartido(Integer arbitroId, int partidoId) {
        Arbitro a = buscarArbitroOrThrow(arbitroId);
        Partido p = buscarPartido(partidoId);
        LocalDate f = fechaPartido(p);

        validarPrecondicionesDeAsignacion(a, p, f);

        Asignacion asg = asignacionRepo.save(construirPendiente(a, p));

      
        traducirEstado(asg);

        mailService.notificarNuevaAsignacion(asg);  
        return asg;
    }

    public void aceptar(String correo, Long asignacionId) {
        Arbitro a = getArbitroActual(correo);
        Asignacion asig = asignacionRepo.findByIdAndArbitro(asignacionId, a)
                .orElseThrow(() -> new IllegalArgumentException("Asignación no encontrada."));
        if (asig.getEstado() != EstadoAsignacion.PENDIENTE) {
            throw new IllegalStateException("La asignación ya fue " + asig.getEstado());
        }

        asig.setEstado(EstadoAsignacion.ACEPTADA);
        asignacionRepo.save(asig);

        traducirEstado(asig);

        Partido p = asig.getPartido();
        if (p.getArbitros().stream().noneMatch(x -> Objects.equals(x.getId(), a.getId()))) {
            p.getArbitros().add(a);
            partidoRepo.save(p);
        }
    }

    public void rechazar(String correo, Long asignacionId) {
        Arbitro a = getArbitroActual(correo);
        Asignacion asig = asignacionRepo.findByIdAndArbitro(asignacionId, a)
                .orElseThrow(() -> new IllegalArgumentException("Asignación no encontrada."));
        if (asig.getEstado() != EstadoAsignacion.PENDIENTE) {
            throw new IllegalStateException("La asignación ya fue " + asig.getEstado());
        }

        asig.setEstado(EstadoAsignacion.RECHAZADA);
        asignacionRepo.save(asig);

        
        traducirEstado(asig);

        Partido p = asig.getPartido();
        p.getArbitros().removeIf(x -> Objects.equals(x.getId(), a.getId()));
        partidoRepo.save(p);
    }

    // ======================= READ ========================
    @Transactional(readOnly = true)
    public List<Asignacion> listarDelActual(String correo) {
        Arbitro a = getArbitroActual(correo);
        List<Asignacion> lista = asignacionRepo.findByArbitroOrderByFechaAsignacionDesc(a);
        traducirEstados(lista);
        return lista;
    }

    @Transactional(readOnly = true)
    public List<Asignacion> listarAceptadasPorPartido(int partidoId) {
        Partido p = buscarPartido(partidoId);
        List<Asignacion> lista = asignacionRepo.findByPartidoAndEstado(p, EstadoAsignacion.ACEPTADA);
        traducirEstados(lista);
        return lista;
    }

    @Transactional(readOnly = true)
    public List<Asignacion> listarPorArbitroId(Integer arbitroId) {
        Arbitro a = arbitroService.buscar(arbitroId);
        if (a == null) throw new IllegalArgumentException("Árbitro no encontrado.");
        List<Asignacion> lista = asignacionRepo.findByArbitroOrderByFechaAsignacionDesc(a);
        traducirEstados(lista);
        return lista;
    }

    // ======= Soporte a vistas / consultas de dominio =======
    @Transactional(readOnly = true)
    public List<Arbitro> listarArbitros() {
        return arbitroService.listar();
    }

    @Transactional(readOnly = true)
    public Partido buscarPartido(int id) {
        return partidoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Partido no encontrado."));
    }

    @Transactional(readOnly = true)
    public Arbitro getArbitroActual(String correo) {
        return arbitroService.getActual(correo);
    }

    @Transactional(readOnly = true)
    public boolean yaTieneAsignacion(Arbitro a, Partido p) {
        return asignacionRepo.existsByArbitroAndPartido(a, p);
    }

    @Transactional(readOnly = true)
    public Set<Especialidad> especialidadesOcupadas(int partidoId) {
        Partido p = buscarPartido(partidoId);
        EnumSet<Especialidad> out = EnumSet.noneOf(Especialidad.class);
        for (Asignacion asg : asignacionRepo.findByPartido(p)) {
            if (asg.getEstado() != EstadoAsignacion.RECHAZADA
                    && asg.getArbitro() != null
                    && asg.getArbitro().getEspecialidad() != null) {
                out.add(asg.getArbitro().getEspecialidad());
            }
        }
        return out;
    }

    @Transactional(readOnly = true)
    public boolean especialidadOcupada(int partidoId, Especialidad esp) {
        if (esp == null) return false;
        return especialidadesOcupadas(partidoId).contains(esp);
    }

    @Transactional(readOnly = true)
    public List<Especialidad> especialidadesFaltantes(int partidoId) {
        EnumSet<Especialidad> requeridas = EnumSet.of(
                Especialidad.AUXILIAR,
                Especialidad.PRINCIPAL,
                Especialidad.CRONOMETRISTA,
                Especialidad.APUNTADOR
        );
        for (Asignacion asg : listarAceptadasPorPartido(partidoId)) {
            if (asg.getArbitro() != null && asg.getArbitro().getEspecialidad() != null) {
                requeridas.remove(asg.getArbitro().getEspecialidad());
            }
        }
        return new ArrayList<>(requeridas);
    }

    @Transactional(readOnly = true)
    public Set<Integer> arbitrosNoDisponiblesIds(int partidoId) {
        Partido p = buscarPartido(partidoId);
        LocalDate f = fechaPartido(p);
        return listarArbitros().stream()
                .filter(a -> !disponible(a, f))
                .map(Arbitro::getId)
                .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public Set<Integer> arbitrosYaAsignadosIds(int partidoId) {
        Partido p = buscarPartido(partidoId);
        return listarArbitros().stream()
                .filter(a -> yaTieneAsignacion(a, p))
                .map(Arbitro::getId)
                .collect(Collectors.toSet());
    }

    // =================== Helpers privados ===================
    private Arbitro buscarArbitroOrThrow(Integer id) {
        Arbitro a = arbitroService.buscar(id);
        if (a == null) throw new IllegalArgumentException("Árbitro no encontrado.");
        return a;
    }

    private void validarPrecondicionesDeAsignacion(Arbitro a, Partido p, LocalDate f) {
        if (!disponible(a, f)) {
            throw new IllegalArgumentException("El árbitro NO está disponible en la fecha del partido.");
        }
        if (yaTieneAsignacion(a, p)) {
            throw new IllegalArgumentException("Este árbitro ya tiene asignación para ese partido.");
        }
        if (a.getEspecialidad() == null) {
            throw new IllegalArgumentException("El árbitro no tiene especialidad definida.");
        }
        if (especialidadOcupada(p.getIdPartido(), a.getEspecialidad())) {
            throw new IllegalArgumentException("Ya hay un " + a.getEspecialidad() + " para este partido.");
        }
    }

    private LocalDate fechaPartido(Partido p) {
        return p.getFecha().toLocalDate();
    }

    private boolean disponible(Arbitro a, LocalDate f) {
        return a.getFechasDisponibles() != null && a.getFechasDisponibles().contains(f);
    }

    private Asignacion construirPendiente(Arbitro a, Partido p) {
        Asignacion asg = new Asignacion();
        asg.setArbitro(a);
        asg.setPartido(p);
        asg.setTorneo(p.getTorneo());
        asg.setFechaAsignacion(fechaPartido(p));
        asg.setEstado(EstadoAsignacion.PENDIENTE);

        BigDecimal monto = BigDecimal.ZERO;
        if (p.getTorneo() != null
                && p.getTorneo().getCategoria() != null
                && a != null
                && a.getEscalafon() != null) {
            monto = tarifaService.totalPor(p.getTorneo().getCategoria(), a.getEscalafon());
        }
        asg.setMonto(monto); 
      
        traducirEstado(asg);
        return asg;
    }


    private void traducirEstado(Asignacion a) {
        if (a != null && a.getEstado() != null) {
            String txt = messageSource.getMessage(
                    a.getEstado().getMensajeKey(),
                    null,
                    LocaleContextHolder.getLocale()
            );
            a.setEstadoTraducido(txt);
        }
    }


    private void traducirEstados(List<Asignacion> lista) {
        if (lista != null) {
            lista.forEach(this::traducirEstado);
        }
    }
}
