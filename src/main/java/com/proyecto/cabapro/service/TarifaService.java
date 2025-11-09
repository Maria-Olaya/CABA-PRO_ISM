package com.proyecto.cabapro.service;

import com.proyecto.cabapro.enums.CategoriaTorneo;
import com.proyecto.cabapro.enums.Escalafon;
import com.proyecto.cabapro.enums.EstadoPartido;
import com.proyecto.cabapro.model.Arbitro;
import com.proyecto.cabapro.model.Partido;
import com.proyecto.cabapro.model.Tarifa;
import com.proyecto.cabapro.model.Torneo;
import com.proyecto.cabapro.repository.TarifaRepository;
import org.springframework.context.MessageSource; 
import org.springframework.context.i18n.LocaleContextHolder; 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class TarifaService {

    private final TarifaRepository tarifaRepo;
    private final TorneoService torneoService;
    private final PartidoService partidoService;
    private final MessageSource messageSource; 

    public TarifaService(TarifaRepository tarifaRepo,
                         TorneoService torneoService,
                         PartidoService partidoService,
                         MessageSource messageSource) { 
        this.tarifaRepo = tarifaRepo;
        this.torneoService = torneoService;
        this.partidoService = partidoService;
        this.messageSource = messageSource; 
    }

    // ===== BASE por categoría (VALOR DEL PARTIDO) =====
    public BigDecimal baseCategoria(CategoriaTorneo cat) {
        if (cat == null) return BigDecimal.ZERO;
        return switch (cat) {
            case UNIVERSITARIO -> new BigDecimal("4500000.00");
            case AMATEUR       -> new BigDecimal("3000000.00");
            case PROFESIONAL   -> new BigDecimal("6000000.00");
        };
    }

    // ===== FACTOR por escalafón  =====
    public BigDecimal factorEscalafon(Escalafon esc) {
        if (esc == null) return new BigDecimal("1.10"); 
        return switch (esc) {
            case INTERNACIONAL_FIBA   -> new BigDecimal("1.30");
            case PROFESIONAL_NACIONAL -> new BigDecimal("1.25");
            case SEMIPROFESIONAL      -> new BigDecimal("1.20");
            case REGIONAL             -> new BigDecimal("1.15");
            case EN_FORMACION         -> new BigDecimal("1.10");
        };
    }

    public BigDecimal totalPor(CategoriaTorneo categoria, Escalafon escalafon) {
        return baseCategoria(categoria)
                .multiply(factorEscalafon(escalafon))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal adicionalEscalafon(CategoriaTorneo cat, Escalafon esc) {
        BigDecimal base = baseCategoria(cat);
        BigDecimal factor = factorEscalafon(esc);
        return base.multiply(factor.subtract(BigDecimal.ONE))
                   .setScale(2, RoundingMode.HALF_UP);
    }

    private boolean debeGenerarseTarifa(Partido p, Arbitro a) {
        if (p == null || a == null) return false;
        if (p.getTorneo() == null) return false;
        if (a.getEscalafon() == null) return false;
        return p.getEstadoPartido() == EstadoPartido.FINALIZADO;
    }

    public boolean esElegibleParaTarifa(Partido p, Arbitro a) {
        return debeGenerarseTarifa(p, a);
    }

    public void ensureTarifaIfEligible(Partido partido, Arbitro arbitro) {
        if (!debeGenerarseTarifa(partido, arbitro)) return;
        Torneo torneo = partido.getTorneo();
        BigDecimal monto = totalPor(torneo.getCategoria(), arbitro.getEscalafon());
        upsert(partido, arbitro, torneo, arbitro.getEscalafon(), monto);
    }

    public Tarifa upsert(Partido partido, Arbitro arbitro, Torneo torneo, Escalafon escalafon, BigDecimal monto) {
        return tarifaRepo.findByPartidoAndArbitro(partido, arbitro)
                .map(t -> {
                    t.setTorneo(torneo);
                    t.setEscalafon(escalafon);
                    t.setMonto(monto);
                    return tarifaRepo.save(t);
                })
                .orElseGet(() -> {
                    Tarifa t = new Tarifa();
                    t.setTorneo(torneo);
                    t.setPartido(partido);
                    t.setArbitro(arbitro);
                    t.setEscalafon(escalafon);
                    t.setMonto(monto);
                    return tarifaRepo.save(t);
                });
    }

    public void generarAutomaticoParaTorneo(int torneoId) {
        Torneo torneo = torneoService.obtenerPorId(torneoId);
        if (torneo == null) {
          
            Locale locale = LocaleContextHolder.getLocale();
            String msg = messageSource.getMessage("error.torneoNoEncontrado", null, locale);
            throw new IllegalArgumentException(msg);
        }

        List<Partido> partidos = partidoService.getPartidosByTorneo(torneoId);
        for (Partido p : partidos) {
            for (Arbitro a : p.getArbitros()) {
                ensureTarifaIfEligible(p, a);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<Tarifa> listarPorTorneo(int torneoId) {
        return tarifaRepo.findByTorneo_IdTorneo(torneoId);
    }
}
