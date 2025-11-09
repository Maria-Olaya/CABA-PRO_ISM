package com.proyecto.cabapro.repository;

import com.proyecto.cabapro.enums.EstadoAsignacion;
import com.proyecto.cabapro.model.Arbitro;
import com.proyecto.cabapro.model.Asignacion;
import com.proyecto.cabapro.model.Partido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {


    Optional<Asignacion> findByIdAndArbitro(Long id, Arbitro arbitro);


    List<Asignacion> findByArbitroOrderByFechaAsignacionDesc(Arbitro arbitro);

    List<Asignacion> findByPartidoAndEstado(Partido partido, EstadoAsignacion estado);

    boolean existsByArbitroAndPartido(Arbitro arbitro, Partido partido);


    List<Asignacion> findByPartido(Partido partido);

    List<Asignacion> findByArbitroAndEstado(Arbitro arbitro, EstadoAsignacion estado);

}