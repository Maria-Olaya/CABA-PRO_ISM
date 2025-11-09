
package com.proyecto.cabapro.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.proyecto.cabapro.security.ApiKeyAuthFilter;
import com.proyecto.cabapro.security.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ApiKeyAuthFilter apiKeyAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, ApiKeyAuthFilter apiKeyAuthFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.apiKeyAuthFilter = apiKeyAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(request -> {
                var c = new org.springframework.web.cors.CorsConfiguration();
                c.setAllowedOriginPatterns(List.of("*"));
                c.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
                c.setAllowedHeaders(List.of("*"));
                c.setAllowCredentials(true);
                c.setMaxAge(3600L);
                return c;
            }))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Swagger público
                .requestMatchers(
                    "/swagger-ui/**","/swagger-ui.html",
                    "/api-docs/**","/api-docs/swagger-config",
                    "/swagger-resources/**","/webjars/**",
                    "/","/error","/favicon.ico"
                ).permitAll()

                // Auth público
                .requestMatchers("/api/auth/**").permitAll()

                // ✅ News requieren rol API (que otorga el ApiKeyAuthFilter)
                .requestMatchers("/api/news/**").hasRole("API")

                // Resto con JWT
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/arbitro/**").hasRole("ARBITRO")
                .requestMatchers("/api/**").authenticated()

                .anyRequest().denyAll()
            )

            // Orden: primero API KEY, luego JWT
            .addFilterBefore(apiKeyAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

            .exceptionHandling(ex -> ex.authenticationEntryPoint((req, res, e) -> {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.setContentType("application/json");
                res.getWriter().write("{\"error\":\"Unauthorized or invalid credentials\"}");
            }));

        return http.build();
    }
}
