package com.proyecto.cabapro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frame -> frame.disable())) // 👈 permite iframes (requerido por H2)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/registro", "/css/**", "/js/**","/images/**","/uploads/**","/noticias/api","/noticias/mock","/h2-console/**").permitAll()
                .requestMatchers("/admin/**","/torneos/**","/partidos/**").hasRole("ADMIN")
                .requestMatchers("/arbitro/**").hasRole("ARBITRO")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler((request, response, authentication) -> {
                    // Tomamos el rol del usuario autenticado
                    String role = authentication.getAuthorities().iterator().next().getAuthority();
                    if (role.equals("ROLE_ADMIN")) {
                        response.sendRedirect("/admin/dashboard");
                    } else if (role.equals("ROLE_ARBITRO")) {
                        response.sendRedirect("/arbitro/dashboard");
                    } else {
                        response.sendRedirect("/"); // fallback
                    }
                })
                .permitAll()
            )

            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            );

        return http.build();
    }
}


/*[Petición HTTP]
       |
       v
[SecurityFilterChain]
       |
       +--> CSRF? disabled -> sigue
       |
       +--> URL autorizada? 
       |      |-- /, /login, /registro, /css/** -> pasa
       |      |-- /admin/** -> check ROLE_ADMIN
       |      |-- /arbitro/** -> check ROLE_ARBITRO
       |      |-- cualquier otra -> check autenticado
       |
       +--> Login Form?
       |      |-- credenciales correctas -> successHandler -> redirige según rol
       |
       +--> Logout?
              |-- invalida sesión -> redirige a /
*/