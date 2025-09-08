package com.proyecto.cabapro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.proyecto.cabapro.controller.forms.TorneoForm;
import com.proyecto.cabapro.model.Torneo;
import com.proyecto.cabapro.service.TorneoService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/torneos")
public class TorneoController {

    private final TorneoService torneoService;

    public TorneoController(TorneoService torneoService) {
        this.torneoService = torneoService;
    }

    // Mostrar lista de torneos
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("torneos", torneoService.listarTorneos());
        return "torneos/lista";
    }

    // Formulario de creación
    @GetMapping("/nuevo")
    public String mostrarFormNuevo(Model model) {
        model.addAttribute("torneoForm", new TorneoForm());
        return "torneos/form";
    }

    // Guardar torneo nuevo
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("torneoForm") TorneoForm torneoForm,
                          BindingResult result) {
        if (result.hasErrors()) {
            return "torneos/form";
        }

        Torneo torneo = new Torneo();
        torneo.setNombre(torneoForm.getNombre());
        torneo.setTipoTorneo(torneoForm.getTipoTorneo());
        torneo.setCategoria(torneoForm.getCategoria());
        torneo.setFechaInicio(torneoForm.getFechaInicio());
        torneo.setFechaFin(torneoForm.getFechaFin());

        torneoService.guardarTorneo(torneo);
        return "redirect:/torneos";
    }

    // Mostrar formulario de edición
    @GetMapping("/editar/{id}")
    public String mostrarFormEditar(@PathVariable("id") int id, Model model) {
        Torneo torneo = torneoService.obtenerPorId(id);
        if (torneo == null) {
            return "redirect:/torneos";
        }

        TorneoForm torneoForm = new TorneoForm();
        torneoForm.setIdTorneo(torneo.getIdTorneo());
        torneoForm.setNombre(torneo.getNombre());
        torneoForm.setTipoTorneo(torneo.getTipoTorneo());
        torneoForm.setCategoria(torneo.getCategoria());
        torneoForm.setFechaInicio(torneo.getFechaInicio());
        torneoForm.setFechaFin(torneo.getFechaFin());

        model.addAttribute("torneoForm", torneoForm);
        return "torneos/form";
    }

    // Actualizar torneo existente
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable("id") int id,
                             @Valid @ModelAttribute("torneoForm") TorneoForm torneoForm,
                             BindingResult result) {
        if (result.hasErrors()) {
            return "torneos/form";
        }

        Torneo torneo = torneoService.obtenerPorId(id);
        if (torneo == null) {
            // Si el torneo no existe, redirigir a la lista de torneos
            
            return "redirect:/torneos";
        }

        torneo.setNombre(torneoForm.getNombre());
        torneo.setTipoTorneo(torneoForm.getTipoTorneo());
        torneo.setCategoria(torneoForm.getCategoria());
        torneo.setFechaInicio(torneoForm.getFechaInicio());
        torneo.setFechaFin(torneoForm.getFechaFin());

        torneoService.guardarTorneo(torneo);
        return "redirect:/torneos";
    }

    // Eliminar torneo
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") int id) {
        torneoService.eliminarTorneo(id);
        return "redirect:/torneos";
    }

  
    

}
