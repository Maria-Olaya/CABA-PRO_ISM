package com.proyecto.cabapro.controller;

import com.proyecto.cabapro.model.Asignacion;
import com.proyecto.cabapro.model.Partido;
import com.proyecto.cabapro.service.AsignacionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/asignacion/crear")
public class AsignacionAdminController {

    private final AsignacionService asignacionService;

    public AsignacionAdminController(AsignacionService asignacionService) {
        this.asignacionService = asignacionService;
    }

    /**
     * Formulario de creación (requiere ?partidoId=...):
     * - Lista árbitros
     * - Carga partido
     * - Pasa: no disponibles, ya asignados, aceptadas, faltantes, especialidades ocupadas
     */
    @GetMapping
    public String form(@RequestParam("partidoId") int partidoId, Model model) {
        model.addAttribute("arbitros", asignacionService.listarArbitros());

        Partido p = asignacionService.buscarPartido(partidoId); // lanza IllegalArgumentException si no existe
        model.addAttribute("partido", p);

        model.addAttribute("noDispIds", asignacionService.arbitrosNoDisponiblesIds(partidoId));
        model.addAttribute("yaAsigIds", asignacionService.arbitrosYaAsignadosIds(partidoId));

        model.addAttribute("aceptadas", asignacionService.listarAceptadasPorPartido(partidoId));
        model.addAttribute("faltanEsp", asignacionService.especialidadesFaltantes(partidoId));
        model.addAttribute("espOcupadas", asignacionService.especialidadesOcupadas(partidoId));

        return "admin/asignacion/crearlo"; //  templates/admin/asignacion/crearlo.html
    }

    // Crear UNA asignación para el partido indicado. 
    @PostMapping("/uno")
    public String crearUna(@RequestParam("partidoId") int partidoId,
                           @RequestParam("arbitroId") Integer arbitroId,
                           RedirectAttributes ra) {
        try {
            Asignacion a = asignacionService.crearParaArbitroYPartido(arbitroId, partidoId);
            ra.addFlashAttribute("msg", "Asignación creada (id=" + a.getId() + ")");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("err", ex.getMessage());
        }
        return "redirect:/asignacion/crear?partidoId=" + partidoId;
    }
}
