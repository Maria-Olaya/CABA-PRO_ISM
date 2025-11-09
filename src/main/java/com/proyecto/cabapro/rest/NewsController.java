package com.proyecto.cabapro.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.cabapro.model.NewsArticle;
import com.proyecto.cabapro.service.NoticiasProvider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NoticiasProvider noticiasApi;
    private final NoticiasProvider noticiasMock;

    public NewsController(
            @Qualifier("noticiasApiProvider") NoticiasProvider noticiasApi,
            @Qualifier("noticiasMockProvider") NoticiasProvider noticiasMock) {
        this.noticiasApi = noticiasApi;
        this.noticiasMock = noticiasMock;
    }

    @Operation(
        summary = "Obtiene noticias de NBA desde la API real",
        description = "Consulta la API pública de NewsData.io para obtener las últimas noticias de la NBA en inglés.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Lista de noticias obtenidas desde la API externa",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = NewsArticle.class)))
            )
        }
    )
    @GetMapping("/nba-api")
    public List<NewsArticle> obtenerNoticiasDesdeApi() {
        return noticiasApi.obtenerNoticias();
    }

    @Operation(
        summary = "Obtiene noticias de NBA desde datos simulados (mock)",
        description = "Devuelve una lista de noticias de prueba sin conexión externa, útil para testing o entornos locales.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Lista de noticias simuladas",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = NewsArticle.class)))
            )
        }
    )
    @GetMapping("/nba-mock")
    public List<NewsArticle> obtenerNoticiasDesdeMock() {
        return noticiasMock.obtenerNoticias();
    }
}
