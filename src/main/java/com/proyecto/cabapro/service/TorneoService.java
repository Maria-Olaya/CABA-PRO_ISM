package com.proyecto.cabapro.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.proyecto.cabapro.model.Torneo;
import com.proyecto.cabapro.repository.TorneoRepository;

// TorneoService.java
// Clase de servicio que contiene la lógica de negocio relacionada con los torneos.
// Actúa como intermediario entre los controladores y el repositorio.
@Service
public class TorneoService {

    // Repositorio para acceder a los datos de Torneo en la base de datos
    private final TorneoRepository torneoRepository;

    // Constructor con inyección de dependencias
    public TorneoService(TorneoRepository torneoRepository) {
        this.torneoRepository = torneoRepository;
    }

    // Guarda o actualiza un torneo en la base de datos
    public Torneo guardarTorneo(Torneo torneo) {
        return torneoRepository.save(torneo);
    }

    // Devuelve la lista completa de torneos
    public List<Torneo> listarTorneos() {
        return torneoRepository.findAll();
    }

    // Obtiene un torneo por su ID, devuelve null si no existe
    public Torneo obtenerPorId(int id) {
        return torneoRepository.findById(id).orElse(null);
    }

    // Obtiene un torneo por su nombre, devuelve Optional para manejar el caso "no encontrado"
    public Optional<Torneo> obtenerPorNombre(String nombre) {
        return torneoRepository.findByNombre(nombre);
    }

    // Elimina un torneo por su ID
    public void eliminarTorneo(int id) {
        torneoRepository.deleteById(id);
    }
}
