package com.backend_aimarket.demo.config;

import com.backend_aimarket.demo.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public SecurityConfig(@org.springframework.context.annotation.Lazy AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
                    corsConfiguration.setAllowedOrigins(java.util.List.of("*"));
                    corsConfiguration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfiguration.setAllowedHeaders(java.util.List.of("*"));
                    return corsConfiguration;
                }))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // Endpoints públicos
                        .requestMatchers("/api/categorias/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/productos/**").permitAll()
                        .requestMatchers("/api/canastas/test").permitAll()
                        .requestMatchers("/api/canastas/salud-ia").permitAll()
                        .requestMatchers("/api/categorias/**").permitAll()

                        // Endpoints de administración (requieren rol ADMIN)
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Endpoints de usuario (requieren autenticación)
                        .requestMatchers("/api/usuarios/**").authenticated()
                        .requestMatchers("/api/canastas/generar").authenticated()

                        .anyRequest().authenticated())
                .addFilterBefore(new JwtAuthenticationFilter(authService, jwtUtil),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}