package com.proyecto.cabapro.repository;

import com.proyecto.cabapro.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TarifaRepository extends JpaRepository<Tarifa, Long> {

    // clave para upsert automático
    Optional<Tarifa> findByPartidoAndArbitro(Partido partido, Arbitro arbitro);

    // Para listar por torneo
    List<Tarifa> findByTorneo_IdTorneo(int torneoId);

    // Todas las tarifas de un árbitro, ordenadas por fecha de partido
    List<Tarifa> findByArbitroOrderByPartido_FechaAsc(Arbitro arbitro);
    
    boolean existsByPartidoAndArbitro(Partido partido, Arbitro arbitro);

    List<Tarifa> findByArbitroAndLiquidacionIsNullOrderByGeneradoEnAsc(Arbitro arbitro);

}


