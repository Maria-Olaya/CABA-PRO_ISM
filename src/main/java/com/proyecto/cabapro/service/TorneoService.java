package com.proyecto.cabapro.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.proyecto.cabapro.model.Torneo;
import com.proyecto.cabapro.repository.TorneoRepository;




@Service
public class TorneoService {

    private final TorneoRepository torneoRepository;
    private final MessageSource messageSource;


   @Autowired
    public TorneoService(TorneoRepository torneoRepository, MessageSource messageSource) {
        this.torneoRepository = torneoRepository;
        this.messageSource = messageSource;
    }

    public Torneo guardarTorneo(Torneo torneo) {
        return torneoRepository.save(torneo);
    }

   
    public List<Torneo> listarTorneos() {
        List<Torneo> torneos = torneoRepository.findAll();
        torneos.forEach(t -> {
            traducirCategoria(t);
            traducirTipo(t);
        });
        return torneos;
    }

   
    public Torneo obtenerPorId(int id) {
        return torneoRepository.findById(id)
            .map(t -> {
                traducirCategoria(t);
                traducirTipo(t);
                return t;
            })
            .orElse(null);
    }


    public Optional<Torneo> obtenerPorNombre(String nombre) {
        return torneoRepository.findByNombre(nombre);
    }

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
            torneo.setTipoTraducido(mensaje);
        }
    }

}
