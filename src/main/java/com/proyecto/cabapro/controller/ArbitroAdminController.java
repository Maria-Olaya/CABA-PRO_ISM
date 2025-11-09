package com.proyecto.cabapro.controller;

import com.proyecto.cabapro.controller.forms.ArbitroForm;
import com.proyecto.cabapro.enums.Escalafon;
import com.proyecto.cabapro.enums.Especialidad;
import com.proyecto.cabapro.model.Arbitro;
import com.proyecto.cabapro.model.Asignacion;
import com.proyecto.cabapro.service.ArbitroService;
import com.proyecto.cabapro.service.AsignacionService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/admin/arbitros")
public class ArbitroAdminController {

    private final ArbitroService service;
    private final AsignacionService asignacionService;
    private final MessageSource messageSource;

    public ArbitroAdminController(ArbitroService service,
                                  AsignacionService asignacionService,
                                  MessageSource messageSource) {
        this.service = service;
        this.asignacionService = asignacionService;
        this.messageSource = messageSource;
    }

    // ================= LISTADO (panel central) =================
    @GetMapping
    public String list(@RequestParam(value = "arbitroId", required = false) Integer arbitroId,
                       Model model, Locale locale) {
        model.addAttribute("arbitros", service.listar());

        if (arbitroId != null) {
            Arbitro sel = service.buscar(arbitroId);
            if (sel != null) {
                List<Asignacion> asignaciones = asignacionService.listarPorArbitroId(arbitroId);
                model.addAttribute("arbitroSel", sel);
                model.addAttribute("asignacionesSel", asignaciones);
            } else {
                String msg = messageSource.getMessage(
                        "admin.arbitros.noEncontrado",
                        new Object[]{arbitroId}, locale);
                model.addAttribute("err", msg);
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
                         Model model,
                         Locale locale) {

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
            br.rejectValue("contrasena", "admin.arbitros.error.contrasenaRequerida",
                    messageSource.getMessage("admin.arbitros.error.contrasenaRequerida", null, locale));
        } catch (ArbitroService.DuplicateEmailException e) {
            br.rejectValue("correo", "admin.arbitros.error.correoDuplicado",
                    messageSource.getMessage("admin.arbitros.error.correoDuplicado", null, locale));
        } catch (IllegalArgumentException e) {
            br.reject("admin.arbitros.error.crear",
                    messageSource.getMessage("admin.arbitros.error.crear", null, locale));
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
        form.setContrasena(""); 

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
                         Model model,
                         Locale locale) {

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
            br.rejectValue("correo", "admin.arbitros.error.correoDuplicado",
                    messageSource.getMessage("admin.arbitros.error.correoDuplicado", null, locale));
        } catch (IllegalArgumentException e) {
            br.reject("admin.arbitros.error.editar",
                    messageSource.getMessage("admin.arbitros.error.editar", null, locale));
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
        f.setEspecialidad(a.getEspecialidad());
        f.setEscalafon(a.getEscalafon());
        return f;
    }
}
