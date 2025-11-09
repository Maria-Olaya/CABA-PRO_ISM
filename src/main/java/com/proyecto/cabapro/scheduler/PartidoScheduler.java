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

    @Transactional
    @Scheduled(cron = "0 * * * * *") 
    public void actualizarEstadosPartidos() {
        List<Partido> partidos = partidoService.getAllPartidos();
        for (Partido partido : partidos) {
            partidoService.actualizarEstado(partido); 
            partidoService.savePartido(partido); 
        }
        System.out.println("Scheduler ejecutado: estados de partidos actualizados.");
    }
}
