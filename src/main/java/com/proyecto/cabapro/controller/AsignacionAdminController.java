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


    @GetMapping
    public String form(@RequestParam("partidoId") int partidoId, Model model) {
        model.addAttribute("arbitros", asignacionService.listarArbitros());

        Partido p = asignacionService.buscarPartido(partidoId); 
        model.addAttribute("partido", p);

        model.addAttribute("noDispIds", asignacionService.arbitrosNoDisponiblesIds(partidoId));
        model.addAttribute("yaAsigIds", asignacionService.arbitrosYaAsignadosIds(partidoId));
        model.addAttribute("aceptadas", asignacionService.listarAceptadasPorPartido(partidoId));
        model.addAttribute("faltanEsp", asignacionService.especialidadesFaltantes(partidoId));
        model.addAttribute("espOcupadas", asignacionService.especialidadesOcupadas(partidoId));

        return "admin/asignacion/crearlo"; 
    }

    
    @PostMapping("/uno")
    public String crearUna(@RequestParam("partidoId") int partidoId,
                           @RequestParam("arbitroId") Integer arbitroId,
                           RedirectAttributes ra) {
        try {
            Asignacion a = asignacionService.crearParaArbitroYPartido(arbitroId, partidoId);
         
            ra.addFlashAttribute("msgCode", "flash.asignacion.creada");
            ra.addFlashAttribute("msgArg0", a.getId());
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("errCode", "flash.asignacion.error");
            ra.addFlashAttribute("errArg0", ex.getMessage());

        }
        return "redirect:/asignacion/crear?partidoId=" + partidoId;
    }
}
