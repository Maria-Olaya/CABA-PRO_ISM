package com.proyecto.cabapro.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.cabapro.model.Partido;
import com.proyecto.cabapro.service.PartidoService;

@Component
public class PartidoScheduler {

    private final PartidoService partidoService;

    public PartidoScheduler(PartidoService partidoService) {
        this.partidoService = partidoService;
    }

    /**
     * Corre cada minuto y actualiza el estado de todos los partidos.
     * Cron: sec min hora día mes díaSemana
     */
    @Transactional
    @Scheduled(cron = "0 * * * * *") // cada minuto
    public void actualizarEstadosPartidos() {
        List<Partido> partidos = partidoService.getAllPartidos();
        for (Partido partido : partidos) {
            partidoService.actualizarEstado(partido); // actualizar estado
            partidoService.savePartido(partido); // persistir cambio
        }
        System.out.println("Scheduler ejecutado: estados de partidos actualizados.");
    }
}
