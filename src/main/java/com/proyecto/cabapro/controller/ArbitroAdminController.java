package com.proyecto.cabapro.controller;

import com.proyecto.cabapro.controller.forms.ArbitroForm;
import com.proyecto.cabapro.enums.Escalafon;
import com.proyecto.cabapro.enums.Especialidad;
import com.proyecto.cabapro.model.Arbitro;
import com.proyecto.cabapro.model.Asignacion;
import com.proyecto.cabapro.service.ArbitroService;
import com.proyecto.cabapro.service.AsignacionService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/arbitros")
public class ArbitroAdminController {

    private final ArbitroService service;
    private final AsignacionService asignacionService;

    public ArbitroAdminController(ArbitroService service, AsignacionService asignacionService) {
        this.service = service;
        this.asignacionService = asignacionService;
    }

    // ================= LISTADO (panel central) =================
    @GetMapping
    public String list(@RequestParam(value = "arbitroId", required = false) Integer arbitroId,
                       Model model) {
        model.addAttribute("arbitros", service.listar());

        if (arbitroId != null) {
            Arbitro sel = service.buscar(arbitroId);
            if (sel != null) {
                List<Asignacion> asignaciones = asignacionService.listarPorArbitroId(arbitroId);
                model.addAttribute("arbitroSel", sel);
                model.addAttribute("asignacionesSel", asignaciones);
            } else {
                model.addAttribute("err", "Árbitro no encontrado (id=" + arbitroId + ")");
            }
        }
        return "admin/arbitros/list";
    }

    // ================= CREAR =================
    @GetMapping("/nuevo")
    public String newForm(Model model) {
        model.addAttribute("form", new ArbitroForm());
        commonSelects(model);
        model.addAttribute("modo", "crear");
        model.addAttribute("actionUrl", "/admin/arbitros");
        return "admin/arbitros/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") ArbitroForm form,
                         BindingResult br,
                         Model model) {

        if (br.hasErrors()) {
            commonSelects(model);
            model.addAttribute("modo", "crear");
            model.addAttribute("actionUrl", "/admin/arbitros");
            return "admin/arbitros/form";
        }

        try {
            Arbitro entidad = toEntity(form);
            service.crear(entidad);
            return "redirect:/admin/arbitros";
        } catch (ArbitroService.PasswordRequiredOnCreateException e) {
            br.rejectValue("contrasena", "contrasena.requerida", e.getMessage());
        } catch (ArbitroService.DuplicateEmailException e) {
            br.rejectValue("correo", "correo.duplicado", e.getMessage());
        } catch (IllegalArgumentException e) {
            br.reject("crear.error", e.getMessage());
        }

        commonSelects(model);
        model.addAttribute("modo", "crear");
        model.addAttribute("actionUrl", "/admin/arbitros");
        return "admin/arbitros/form";
    }

    // ================= EDITAR =================
    @GetMapping("/{id}/editar")
    public String editForm(@PathVariable Integer id, Model model) {
        Arbitro a = service.buscar(id);
        if (a == null) return "redirect:/admin/arbitros";

        ArbitroForm form = fromEntity(a);
        form.setContrasena(""); // no exponer

        model.addAttribute("form", form);
        commonSelects(model);
        model.addAttribute("modo", "editar");
        model.addAttribute("actionUrl", "/admin/arbitros/" + id);
        return "admin/arbitros/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                         @Valid @ModelAttribute("form") ArbitroForm form,
                         BindingResult br,
                         Model model) {

        if (br.hasErrors()) {
            commonSelects(model);
            model.addAttribute("modo", "editar");
            model.addAttribute("actionUrl", "/admin/arbitros/" + id);
            return "admin/arbitros/form";
        }

        try {
            Arbitro a = toEntity(form);
            service.actualizar(id, a);
            return "redirect:/admin/arbitros";
        } catch (ArbitroService.DuplicateEmailException e) {
            br.rejectValue("correo", "correo.duplicado", e.getMessage());
        } catch (IllegalArgumentException e) {
            br.reject("editar.error", e.getMessage());
        }

        commonSelects(model);
        model.addAttribute("modo", "editar");
        model.addAttribute("actionUrl", "/admin/arbitros/" + id);
        return "admin/arbitros/form";
    }

    // ================= ELIMINAR =================
    @PostMapping("/{id}/eliminar")
    public String delete(@PathVariable Integer id) {
        service.eliminar(id);
        return "redirect:/admin/arbitros";
    }

    // ================= Helpers =================
    private void commonSelects(Model model) {
        model.addAttribute("especialidades", Especialidad.values());
        model.addAttribute("escalafones", Escalafon.values());
    }

    private Arbitro toEntity(ArbitroForm f) {
        Arbitro a = new Arbitro();
        a.setNombre(f.getNombre());
        a.setApellido(f.getApellido());
        a.setCorreo(f.getCorreo());
        a.setContrasena(f.getContrasena()); 
        a.setUrlFoto(f.getUrlFoto());
        a.setEspecialidad(f.getEspecialidad());
        a.setEscalafon(f.getEscalafon());
        return a;
    }

    private ArbitroForm fromEntity(Arbitro a) {
        ArbitroForm f = new ArbitroForm();
        f.setId(a.getId());
        f.setNombre(a.getNombre());
        f.setApellido(a.getApellido());
        f.setCorreo(a.getCorreo());
        f.setUrlFoto(a.getUrlFoto());
        f.setEspecialidad(a.getEspecialidad());
        f.setEscalafon(a.getEscalafon());
        return f;
    }
}
