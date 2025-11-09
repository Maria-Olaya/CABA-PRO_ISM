package com.proyecto.cabapro.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.cabapro.model.ApiKey;
import com.proyecto.cabapro.service.ApiKeyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/admin/keys")
@Tag(name = "Gestión de API Keys", description = "Endpoints para la administración de llaves de acceso (API Keys) de aplicaciones cliente.")
public class ApiKeyAdminRestController {

    private final ApiKeyService service;

    public ApiKeyAdminRestController(ApiKeyService service) {
        this.service = service;
    }


    @Operation(
        summary = "Genera una nueva API Key para una aplicación cliente",
        description = "Este endpoint permite crear una nueva API Key asociada al nombre de una aplicación cliente. "
                    + "Solo puede ser utilizado por administradores autenticados. "
                    + "Cada API Key es única e incluye un identificador, la aplicación asociada y la fecha de creación.",
        parameters = {
            @Parameter(
                name = "appName",
                description = "Nombre único de la aplicación cliente que solicita la API Key.",
                required = true,
                example = "AppDeTorneos2025"
            )
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "API Key generada exitosamente",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiKey.class),
                    examples = {
                        @ExampleObject(
                            name = "Ejemplo de respuesta exitosa",
                            value = """
                            {
                              "id": 12,
                              "appName": "AppDeTorneos2025",
                              "keyValue": "f9bcd8a4-93e4-46a1-982a-9df7a99b3412",
                              "creationDate": "2025-11-01T14:25:43.123Z"
                            }
                            """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "El nombre de la aplicación ya tiene una API Key asociada o el parámetro es inválido.",
                content = @Content(
                    mediaType = "application/json",
                    examples = {
                        @ExampleObject(
                            name = "Error por nombre duplicado",
                            value = """
                            {
                              "error": "API Key ya existente para la aplicación especificada."
                            }
                            """
                        ),
                        @ExampleObject(
                            name = "Error por parámetro vacío",
                            value = """
                            {
                              "error": "El parámetro 'appName' no puede estar vacío."
                            }
                            """
                        )
                    }
                )
            )
        }
    )

    @PostMapping
    public ApiKey generateKey(
        @RequestParam 
        @Parameter(
            description = "Nombre único de la aplicación cliente.",
            example = "AppDeTorneos2025"
        )
        String appName
    ) {

        return service.generateForApp(appName);
    }
}
