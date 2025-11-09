package com.proyecto.cabapro.rest;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.cabapro.controller.forms.TorneoForm;
import com.proyecto.cabapro.model.Torneo;
import com.proyecto.cabapro.service.TorneoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/torneos")
@CrossOrigin(origins = "*")
@Tag(
    name = "Gestión de Torneos",
    description = "Endpoints para crear, listar, buscar, actualizar y eliminar torneos del sistema de arbitraje."
)
public class TorneoRestController {

    private final TorneoService torneoService;

    @Autowired
    public TorneoRestController(TorneoService torneoService) {
        this.torneoService = torneoService;
    }

   
    // ================= LISTAR TORNEOS =================
    @Operation(
        summary = "Listar todos los torneos",
        description = "Devuelve una lista con todos los torneos registrados en el sistema.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Lista de torneos obtenida exitosamente",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "[{ \"id\": 1, \"nombre\": \"Copa Primavera\", \"tipoTorneo\": \"LIGA\", \"categoria\": \"UNIVERSITARIO\", \"fechaInicio\": \"2025-11-01\", \"fechaFin\": \"2025-11-30\" }]"
                    )
                )
            )
        }
    )
    @GetMapping
    public List<Torneo> listarTorneos() {
        return torneoService.listarTorneos();
    }

  
    // ================= OBTENER TORNEO =================
    @Operation(
        summary = "Obtener un torneo por ID",
        description = "Devuelve la información completa de un torneo dado su ID.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Torneo encontrado",
                content = @Content(
                    schema = @Schema(
                        example = "{ \"id\": 1, \"nombre\": \"Copa Primavera\", \"tipoTorneo\": \"LIGA\", \"categoria\": \"UNIVERSITARIO\", \"fechaInicio\": \"2025-11-01\", \"fechaFin\": \"2025-11-30\" }"
                    )
                )
            ),
            @ApiResponse(responseCode = "404", description = "Torneo no encontrado")
        }
    )
    @GetMapping("/{id}")
    public Torneo obtenerTorneo(
            @Parameter(description = "ID del torneo que se desea consultar.", example = "1")
            @PathVariable int id) {

        Torneo torneo = torneoService.obtenerPorId(id);
        if (torneo == null) {
            throw new RuntimeException("Torneo no encontrado con id " + id);
        }
        return torneo;
    }

    @Operation(
        summary = "Buscar un torneo por nombre",
        description = "Permite buscar un torneo existente utilizando su nombre exacto.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Torneo encontrado",
                content = @Content(
                    schema = @Schema(
                        example = "{ \"id\": 1, \"nombre\": \"Copa Primavera\", \"tipoTorneo\": \"LIGA\", \"categoria\": \"UNIVERSITARIO\", \"fechaInicio\": \"2025-11-01\", \"fechaFin\": \"2025-11-30\" }"
                    )
                )
            ),
            @ApiResponse(responseCode = "404", description = "No se encontró un torneo con ese nombre")
        }
    )
    @GetMapping("/buscar")
    public Optional<Torneo> buscarPorNombre(
            @Parameter(description = "Nombre exacto del torneo a buscar.", example = "Copa Primavera")
            @RequestParam String nombre) {
        return torneoService.obtenerPorNombre(nombre);
    }

    @Operation(
        summary = "Crear un nuevo torneo",
        description = "Registra un nuevo torneo en el sistema con los datos proporcionados.",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Torneo creado exitosamente",
                content = @Content(
                    schema = @Schema(
                        example = "{ \"id\": 2, \"nombre\": \"Copa Otoño\", \"tipoTorneo\": \"LIGA\", \"categoria\": \"UNIVERSITARIO\", \"fechaInicio\": \"2025-12-01\", \"fechaFin\": \"2025-12-31\" }"
                    )
                )
            )
        }
    )
    @PostMapping
    public Torneo crearTorneo(@Valid @RequestBody TorneoForm form) {
  
    Torneo torneo = new Torneo();
    torneo.setNombre(form.getNombre());
    torneo.setTipoTorneo(form.getTipoTorneo());
    torneo.setCategoria(form.getCategoria());
    torneo.setFechaInicio(form.getFechaInicio());
    torneo.setFechaFin(form.getFechaFin());

    return torneoService.guardarTorneo(torneo);
}



    @Operation(
        summary = "Actualizar un torneo existente",
        description = "Modifica los datos de un torneo existente usando su ID.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Torneo actualizado correctamente",
                content = @Content(
                    schema = @Schema(
                        example = "{ \"id\": 1, \"nombre\": \"Copa Primavera Actualizada\", \"tipoTorneo\": \"LIGA\", \"categoria\": \"UNIVERSITARIO\", \"fechaInicio\": \"2025-11-01\", \"fechaFin\": \"2025-11-30\" }"
                    )
                )
            ),
            @ApiResponse(responseCode = "404", description = "No existe torneo con ese ID")
        }
    )
    @PutMapping("/{id}")
    public Torneo actualizarTorneo(@PathVariable int id, @Valid @RequestBody TorneoForm form) {
        Torneo torneo = torneoService.obtenerPorId(id);
        if (torneo == null) {
            throw new RuntimeException("No existe torneo con id " + id);
        }

        torneo.setNombre(form.getNombre());
        torneo.setTipoTorneo(form.getTipoTorneo());
        torneo.setCategoria(form.getCategoria());
        torneo.setFechaInicio(form.getFechaInicio());
        torneo.setFechaFin(form.getFechaFin());

        return torneoService.guardarTorneo(torneo);
    }


    @Operation(
        summary = "Eliminar un torneo",
        description = "Elimina un torneo existente del sistema usando su ID.",
        responses = {
            @ApiResponse(responseCode = "204", description = "Torneo eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el torneo especificado")
        }
    )
    @DeleteMapping("/{id}")
    public void eliminarTorneo(
            @Parameter(description = "ID del torneo a eliminar.", example = "1")
            @PathVariable int id) {
        torneoService.eliminarTorneo(id);
    }
}
