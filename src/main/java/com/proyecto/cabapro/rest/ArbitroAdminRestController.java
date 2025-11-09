package com.proyecto.cabapro.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.cabapro.controller.forms.ArbitroForm;
import com.proyecto.cabapro.model.Arbitro;
import com.proyecto.cabapro.model.Asignacion;
import com.proyecto.cabapro.service.ArbitroService;
import com.proyecto.cabapro.service.AsignacionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/arbitros")
@Tag(name = "Gestión de Árbitros", description = "Endpoints administrativos para la gestión completa de árbitros y sus asignaciones.")
public class ArbitroAdminRestController {

    private final ArbitroService service;
    private final AsignacionService asignacionService;

    @Autowired
    public ArbitroAdminRestController(ArbitroService service, AsignacionService asignacionService) {
        this.service = service;
        this.asignacionService = asignacionService;
    }


    @Operation(
        summary = "Listar todos los árbitros registrados",
        description = "Devuelve una lista completa con los árbitros del sistema, incluyendo su nombre, correo, especialidad y escalafón.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Lista obtenida correctamente",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Arbitro.class),
                    examples = @ExampleObject(
                        name = "Ejemplo de lista de árbitros",
                        value = """
                        [
                          {
                            "id": 1,
                            "nombre": "Carlos",
                            "apellido": "Gómez",
                            "correo": "carlos.gomez@liga.com",
                            "especialidad": "Fútbol Sala",
                            "escalafon": "A"
                          },
                          {
                            "id": 2,
                            "nombre": "Laura",
                            "apellido": "Pérez",
                            "correo": "laura.perez@liga.com",
                            "especialidad": "Baloncesto",
                            "escalafon": "B"
                          }
                        ]
                        """
                    )
                )
            )
        }
    )
    @GetMapping
    public List<Arbitro> listarArbitros() {
        return service.listar();
    }

 
    @Operation(
        summary = "Obtener un árbitro por su ID",
        description = "Retorna los datos completos de un árbitro específico.",
        parameters = @Parameter(name = "id", description = "Identificador único del árbitro.", example = "1"),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Árbitro encontrado",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Arbitro.class),
                    examples = @ExampleObject(
                        value = """
                        {
                          "id": 1,
                          "nombre": "Carlos",
                          "apellido": "Gómez",
                          "correo": "carlos.gomez@liga.com",
                          "especialidad": "Fútbol Sala",
                          "escalafon": "A"
                        }
                        """
                    )
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Árbitro no encontrado",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"error\": \"No existe un árbitro con el ID especificado.\"}")
                )
            )
        }
    )
    @GetMapping("/{id}")
    public Arbitro obtenerArbitro(@PathVariable Integer id) {
        return service.buscar(id);
    }


    @Operation(
        summary = "Registrar un nuevo árbitro",
        description = "Permite crear un nuevo árbitro a partir de los datos enviados en formato JSON.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ArbitroForm.class),
                examples = @ExampleObject(
                    name = "Ejemplo de creación de árbitro",
                    value = """
                    {
                      "nombre": "Juan",
                      "apellido": "Restrepo",
                      "correo": "juan.restrepo@liga.com",
                      "contrasena": "1234",
                      "especialidad": "AUXILIAR",
                      "escalafon": "REGIONAL"
                    }
                    """
                )
            )
        ),
        responses = {
            @ApiResponse(responseCode = "201", description = "Árbitro creado correctamente"),
            @ApiResponse(
                responseCode = "400",
                description = "Datos inválidos o faltantes",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"error\": \"El correo ya está registrado.\"}")
                )
            )
        }
    )
    @PostMapping(consumes = "application/json", produces = "application/json")
    public Arbitro crearArbitro(@Valid @RequestBody ArbitroForm form) {
        Arbitro arbitro = new Arbitro();
        arbitro.setNombre(form.getNombre());
        arbitro.setApellido(form.getApellido());
        arbitro.setCorreo(form.getCorreo());
        arbitro.setContrasena(form.getContrasena());
        arbitro.setEspecialidad(form.getEspecialidad());
        arbitro.setEscalafon(form.getEscalafon());
        return service.crear(arbitro);
    }

    @Operation(
        summary = "Actualizar los datos de un árbitro",
        description = "Permite modificar los datos personales o técnicos de un árbitro existente.",
        parameters = @Parameter(name = "id", description = "Identificador del árbitro a actualizar.", example = "1"),
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ArbitroForm.class),
                examples = @ExampleObject(
                    name = "Ejemplo de actualización de árbitro",
                    value = """
                    {
                      "nombre": "Carlos",
                      "apellido": "Gómez",
                      "correo": "carlos.gomez@liga.com",
                      "contrasena": "1234",
                      "especialidad": "AUXILIAR",
                      "escalafon": "REGIONAL"
                    }
                    """
                )
            )
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Árbitro actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Árbitro no encontrado")
        }
    )
    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public Arbitro actualizarArbitro(@PathVariable Integer id, 
                                    @Valid @RequestBody ArbitroForm form) {
        Arbitro arbitro = new Arbitro();
        arbitro.setNombre(form.getNombre());
        arbitro.setApellido(form.getApellido());
        arbitro.setCorreo(form.getCorreo());
        arbitro.setContrasena(form.getContrasena());
        arbitro.setEspecialidad(form.getEspecialidad());
        arbitro.setEscalafon(form.getEscalafon());
        return service.actualizar(id, arbitro);
    }


    @Operation(
        summary = "Eliminar un árbitro del sistema",
        description = "Borra permanentemente el registro del árbitro identificado por su ID.",
        parameters = @Parameter(name = "id", description = "Identificador del árbitro a eliminar.", example = "3"),
        responses = {
            @ApiResponse(responseCode = "204", description = "Árbitro eliminado exitosamente"),
            @ApiResponse(
                responseCode = "404",
                description = "Árbitro no encontrado",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"error\": \"No se encontró el árbitro con el ID especificado.\"}")
                )
            )
        }
    )
    @DeleteMapping("/{id}")
    public void eliminarArbitro(@PathVariable Integer id) {
        service.eliminar(id);
    }


    @Operation(
        summary = "Listar las asignaciones de un árbitro",
        description = "Obtiene todos los partidos o torneos asignados a un árbitro específico.",
        parameters = @Parameter(name = "id", description = "Identificador del árbitro.", example = "1"),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Lista de asignaciones encontrada",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Asignacion.class),
                    examples = @ExampleObject(
                        name = "Ejemplo de asignaciones",
                        value = """
                        [
                          {
                            "id": 101,
                            "partido": "Torneo Universitario 2025 - Fecha 3",
                            "fecha": "2025-09-12T14:00:00",
                            "estado": "Pendiente"
                          }
                        ]
                        """
                    )
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Árbitro no encontrado o sin asignaciones registradas"
            )
        }
    )
    @GetMapping("/{id}/asignaciones")
    public List<Asignacion> obtenerAsignacionesPorArbitro(@PathVariable Integer id) {
        return asignacionService.listarPorArbitroId(id);
    }
}
