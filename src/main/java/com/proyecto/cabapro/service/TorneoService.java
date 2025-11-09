package com.proyecto.cabapro.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
    private final MessageSource messageSource;


    // Constructor con inyección de dependencias
   @Autowired
    public TorneoService(TorneoRepository torneoRepository, MessageSource messageSource) {
        this.torneoRepository = torneoRepository;
        this.messageSource = messageSource;
    }

    // Guarda o actualiza un torneo en la base de datos
    public Torneo guardarTorneo(Torneo torneo) {
        return torneoRepository.save(torneo);
    }

    // Devuelve la lista completa de torneos
    public List<Torneo> listarTorneos() {
        List<Torneo> torneos = torneoRepository.findAll();
        torneos.forEach(t -> {
            traducirCategoria(t);
            traducirTipo(t);
        });
        return torneos;
    }

    // Obtiene un torneo por su ID, devuelve null si no existe
    public Torneo obtenerPorId(int id) {
        return torneoRepository.findById(id)
            .map(t -> {
                traducirCategoria(t);
                traducirTipo(t);
                return t;
            })
            .orElse(null);
    }


    // Obtiene un torneo por su nombre, devuelve Optional para manejar el caso "no encontrado"
    public Optional<Torneo> obtenerPorNombre(String nombre) {
        return torneoRepository.findByNombre(nombre);
    }

    // Elimina un torneo por su ID
    public void eliminarTorneo(int id) {
        torneoRepository.deleteById(id);
    }



    public void traducirCategoria(Torneo torneo) {
        if (torneo.getCategoria() != null) {
            String mensaje = messageSource.getMessage(
                torneo.getCategoria().getMensajeKey(),
                null,
                LocaleContextHolder.getLocale()
            );
            torneo.setCategoriaTraducida(mensaje);
        }
    }

    public void traducirTipo(Torneo torneo) {
        if (torneo.getTipoTorneo() != null) {
            String mensaje = messageSource.getMessage(
                torneo.getTipoTorneo().getMensajeKey(),
                null,
                LocaleContextHolder.getLocale()
            );
            torneo.setTipoTraducido(mensaje); // necesitas agregar tipoTraducido en el modelo
        }
    }

}
