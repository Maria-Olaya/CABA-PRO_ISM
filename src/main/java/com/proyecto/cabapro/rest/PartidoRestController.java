package com.proyecto.cabapro.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.cabapro.controller.forms.PartidoForm;
import com.proyecto.cabapro.model.Partido;
import com.proyecto.cabapro.model.Torneo;
import com.proyecto.cabapro.service.PartidoService;
import com.proyecto.cabapro.service.TorneoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/admin/torneos/{torneoId}/partidos")
@Tag(name = "Partidos", description = "Operaciones sobre partidos de un torneo")
public class PartidoRestController {

    private final PartidoService partidoService;
    private final TorneoService torneoService;

    public PartidoRestController(PartidoService partidoService, TorneoService torneoService) {
        this.partidoService = partidoService;
        this.torneoService = torneoService;
    }

   
    @Operation(
        summary = "Lista todos los partidos de un torneo",
        description = "Devuelve la lista completa de partidos que pertenecen al torneo especificado por el path variable `torneoId`.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Lista de partidos",
                content = @io.swagger.v3.oas.annotations.media.Content(
                    schema = @io.swagger.v3.oas.annotations.media.Schema(
                        example = "[{ \"id\": 1, \"fecha\": \"2025-11-01T15:00:00\", \"cancha\": \"A\", \"arbitros\": [] }]"
                    )
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Torneo no encontrado"
            )
        }
    )
    @GetMapping
    public ResponseEntity<List<Partido>> listarPartidos(@PathVariable int torneoId) {
        List<Partido> partidos = partidoService.getPartidosByTorneo(torneoId);
        return ResponseEntity.ok(partidos);
    }

    @Operation(
        summary = "Obtiene un partido por ID dentro de un torneo",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Partido encontrado",
                content = @io.swagger.v3.oas.annotations.media.Content(
                    schema = @io.swagger.v3.oas.annotations.media.Schema(
                        example = "{ \"id\": 1, \"fecha\": \"2025-11-01T15:00:00\", \"cancha\": \"A\", \"arbitros\": [] }"
                    )
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Partido o torneo no encontrado"
            )
        }
    )
    @GetMapping("/{partidoId}")
    public ResponseEntity<Partido> obtenerPartido(@PathVariable int torneoId, @PathVariable int partidoId) {
        return partidoService.getPartidoById(partidoId)
                .filter(p -> p.getTorneo().getIdTorneo() == torneoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @Operation(
        summary = "Crea un nuevo partido en un torneo",
        description = "Se debe enviar un JSON con los datos del partido. El `torneoId` se toma del path variable.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "Partido creado correctamente",
                content = @io.swagger.v3.oas.annotations.media.Content(
                    schema = @io.swagger.v3.oas.annotations.media.Schema(
                        example = "{ \"id\": 5, \"fecha\": \"2025-11-10T18:00:00\", \"cancha\": \"B\", \"arbitros\": [] }"
                    )
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Torneo no encontrado"
            )
        }
    )
    @PostMapping
    public ResponseEntity<Partido> crearPartido(@PathVariable int torneoId,
                                                @Valid @RequestBody PartidoForm form) {
      
        Torneo torneo = torneoService.obtenerPorId(torneoId);
        if (torneo == null) {
            return ResponseEntity.notFound().build();
        }

       
        Partido partido = partidoService.crearPartido(form, torneo);
        return ResponseEntity.status(HttpStatus.CREATED).body(partido);
    }


    
    @Operation(
        summary = "Actualiza un partido existente de un torneo",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Partido actualizado correctamente",
                content = @io.swagger.v3.oas.annotations.media.Content(
                    schema = @io.swagger.v3.oas.annotations.media.Schema(
                        example = "{ \"id\": 1, \"fecha\": \"2025-11-01T17:00:00\", \"cancha\": \"A\", \"arbitros\": [] }"
                    )
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Partido o torneo no encontrado"
            )
        }
    )
    @PutMapping("/{partidoId}")
    public ResponseEntity<Partido> actualizarPartido(@PathVariable int torneoId,
                                                     @PathVariable int partidoId,
                                                     @Valid @RequestBody PartidoForm form) {
        return partidoService.getPartidoById(partidoId)
                .filter(p -> p.getTorneo().getIdTorneo() == torneoId)
                .map(p -> {
                    Partido actualizado = partidoService.actualizarPartido(p, form);
                    return ResponseEntity.ok(actualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }
   
    @Operation(
        summary = "Elimina un partido de un torneo",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "Partido eliminado correctamente"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Partido o torneo no encontrado"
            )
        }
    )
    @DeleteMapping("/{partidoId}")
    public ResponseEntity<Void> eliminarPartido(@PathVariable int torneoId,
                                                @PathVariable int partidoId) {
        return partidoService.getPartidoById(partidoId)
                .filter(p -> p.getTorneo().getIdTorneo() == torneoId)
                .map(p -> {
                    partidoService.deletePartido(partidoId);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
