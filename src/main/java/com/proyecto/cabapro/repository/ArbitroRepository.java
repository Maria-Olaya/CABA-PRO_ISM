package com.proyecto.cabapro.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.proyecto.cabapro.model.Arbitro;

public interface ArbitroRepository extends JpaRepository<Arbitro, Integer> {

    Optional<Arbitro> findByCorreo(String correo);

    boolean existsByCorreoIgnoreCase(String correo);

    boolean existsByCorreoIgnoreCaseAndIdNot(String correo, Integer id);

}
