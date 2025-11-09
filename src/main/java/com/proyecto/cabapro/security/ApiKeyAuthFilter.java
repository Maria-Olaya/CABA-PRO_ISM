package com.proyecto.cabapro.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.proyecto.cabapro.service.ApiKeyService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final ApiKeyService apiKeyService;

    public ApiKeyAuthFilter(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws ServletException, IOException {

        String path = request.getRequestURI();

        // Ignorar Swagger (sin cambios)
        if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") ||
            path.startsWith("/api-docs") || path.startsWith("/swagger-resources") ||
            path.startsWith("/webjars")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Permitir rutas públicas (sin cambios)
        if (path.startsWith("/api/public")) {
            filterChain.doFilter(request, response);
            return;
        }

        // >>>>>> NUEVO: exigir X-API-KEY SOLO en /api/news/** <<<<<<
        if (path.equals("/api/news") || path.startsWith("/api/news/")) {
            String apiKey = request.getHeader("X-API-KEY");

            if (apiKey != null && apiKeyService.isValid(apiKey)) {
                // Autenticar con ROLE_API (útil si en SecurityConfig usas hasRole("API"))
                var auth = new UsernamePasswordAuthenticationToken(
                        "api-key-client",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_API"))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);

                filterChain.doFilter(request, response);
                return;
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Invalid or missing API key\"}");
                return;
            }
        }

        // Para el resto de rutas, NO cambiamos tu flujo: sigue con el siguiente filtro (JWT, etc.)
        filterChain.doFilter(request, response);
    }
}

