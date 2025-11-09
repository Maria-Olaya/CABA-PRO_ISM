package com.proyecto.cabapro.repository;

import com.proyecto.cabapro.model.Arbitro;
import com.proyecto.cabapro.model.Liquidacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LiquidacionRepository extends JpaRepository<Liquidacion, Long> {
    
    List<Liquidacion> findByArbitroOrderByFechaGeneradaDesc(Arbitro arbitro);

    boolean existsByArbitroAndFirma(Arbitro arbitro, String firma);
}



