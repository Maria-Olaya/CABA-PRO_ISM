package com.proyecto.cabapro.repository;

import com.proyecto.cabapro.enums.EstadoAsignacion;
import com.proyecto.cabapro.model.Arbitro;
import com.proyecto.cabapro.model.Asignacion;
import com.proyecto.cabapro.model.Partido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {

    // Para obtener una asignación por su ID y árbitro
    // Verifica que la asignación pertenezca al árbitro antes de aceptarla o rechazarla
    Optional<Asignacion> findByIdAndArbitro(Long id, Arbitro arbitro);

    // Para listar las asignaciones de un árbitro
    List<Asignacion> findByArbitroOrderByFechaAsignacionDesc(Arbitro arbitro);


    // Para listar las aceptadas de un partido en (listarAceptadasPorPartido)
    List<Asignacion> findByPartidoAndEstado(Partido partido, EstadoAsignacion estado);


    // Para saber si un árbitro tiene una asignación para un partido
    boolean existsByArbitroAndPartido(Arbitro arbitro, Partido partido);

    // Para listar las asignaciones de un partido usado en (especialidas ocupadas)
    List<Asignacion> findByPartido(Partido partido);

}