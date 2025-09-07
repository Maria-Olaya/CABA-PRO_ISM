package com.proyecto.cabapro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.cabapro.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    // Para login / seguridad
    Optional<Usuario> findByCorreo(String correo);

    // Buscar por nombre exacto
    List<Usuario> findByNombre(String nombre);

    // Buscar por apellido exacto
    List<Usuario> findByApellido(String apellido);

   
}
