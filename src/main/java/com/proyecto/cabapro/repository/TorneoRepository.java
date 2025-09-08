package com.proyecto.cabapro.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.cabapro.enums.CategoriaTorneo;
import com.proyecto.cabapro.enums.TipoTorneo;
import com.proyecto.cabapro.model.Torneo;

public interface TorneoRepository extends JpaRepository<Torneo, Integer> {

    
    List<Torneo> findByTipoTorneo(TipoTorneo tipoTorneo);
    List<Torneo> findByCategoria(CategoriaTorneo categoria);

    List<Torneo> findByFechaFinAfter(LocalDateTime fechaActual);
    List<Torneo> findByFechaInicioBetween(LocalDateTime inicio, LocalDateTime fin);

    Optional<Torneo> findByNombre(String nombre);
}
