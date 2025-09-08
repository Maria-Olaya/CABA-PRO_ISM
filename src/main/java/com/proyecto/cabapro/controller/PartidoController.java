package com.proyecto.cabapro.controller;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.proyecto.cabapro.controller.forms.PartidoForm;
import com.proyecto.cabapro.model.Partido;
import com.proyecto.cabapro.model.Torneo;
import com.proyecto.cabapro.service.PartidoService;
import com.proyecto.cabapro.service.TorneoService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/partidos")
public class PartidoController {

    private final PartidoService partidoService;
    private final TorneoService torneoService;

    public PartidoController(PartidoService partidoService,
                             TorneoService torneoService)
                              {
        this.partidoService = partidoService;
        this.torneoService = torneoService;
        
    }


    // Form editar
    @GetMapping("/editar/{id}")
    public String mostrarFormEditar(@PathVariable("id") int id, Model model) {
        Partido partido = partidoService.getPartidoById(id).orElse(null);
        if (partido == null) {
            return "redirect:/partidos";
        }

        PartidoForm partidoForm = new PartidoForm();
        partidoForm.setIdPartido(partido.getIdPartido());
        partidoForm.setFecha(partido.getFecha());
        partidoForm.setLugar(partido.getLugar());
        //cambiar por partidoForm.setEstadoPartido(partido.getEstadoPartido());
        // Recalcular estado automáticamente
        partido.setEstadoPartido(partidoService.calcularEstado(partido));

        partidoForm.setEquipoLocal(partido.getEquipoLocal());
        partidoForm.setEquipoVisitante(partido.getEquipoVisitante());
        partidoForm.setTorneoId(partido.getTorneo() != null ? partido.getTorneo().getIdTorneo() : null);
      

        cargarListas(model);
        model.addAttribute("partidoForm", partidoForm);
        return "partidos/form";
    }

    // Actualizar
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable("id") int id,
                             @Valid @ModelAttribute("partidoForm") PartidoForm partidoForm,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            cargarListas(model);
            return "partidos/form";
        }

        Partido partido = partidoService.getPartidoById(id).orElse(null);
        if (partido == null) {
            return "redirect:/partidos";
        }

        Torneo torneo = torneoService.obtenerPorId(partidoForm.getTorneoId());
        if (torneo == null) {
            return "redirect:/partidos";
        }

        partido.setFecha(partidoForm.getFecha());
        partido.setLugar(partidoForm.getLugar());
        
        partido.setEquipoLocal(partidoForm.getEquipoLocal());
        partido.setEquipoVisitante(partidoForm.getEquipoVisitante());
        partido.setTorneo(torneo);
        partido.setEstadoPartido(partidoForm.getEstadoPartido());


        try {
            partidoService.savePartido(partido);
        } catch (IllegalArgumentException e) {
            result.rejectValue("fecha", "error.partidoForm", e.getMessage());
            cargarListas(model);
            return "partidos/form";
        }
            return "redirect:/partidos";
        }



    // Helpers
    private void cargarListas(Model model) {
        // Torneos activos = fechaFin > ahora
        List<Torneo> torneosActivos = torneoService.listarTorneos()
                .stream()
                .filter(t -> t.getFechaFin() != null && t.getFechaFin().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
        model.addAttribute("torneos", torneosActivos);

    }
}
