package com.proyecto.cabapro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.proyecto.cabapro.enums.EstadoPartido;
import com.proyecto.cabapro.model.Partido;

@Repository
public interface PartidoRepository extends JpaRepository<Partido, Integer> {

    List<Partido> findByTorneo_IdTorneo(int torneoId);

    List<Partido> findByArbitros_Id(int idArbitro);

   
    List<Partido> findByEstadoPartido(EstadoPartido estado);
    List<Partido> findByTorneo_IdTorneoAndEstadoPartido(int torneoId, EstadoPartido estado);

 

    List<Partido> findByEquipoLocal(String equipoLocal);

    List<Partido> findByEquipoVisitante(String equipoVisitante);
     
   
    @Query("SELECT DISTINCT p FROM Partido p " +
           "JOIN FETCH p.torneo t " +
           "LEFT JOIN FETCH p.arbitros a " +
           "WHERE t.idTorneo = :torneoId")
    List<Partido> findByTorneoWithArbitros(@Param("torneoId") int torneoId);

}

