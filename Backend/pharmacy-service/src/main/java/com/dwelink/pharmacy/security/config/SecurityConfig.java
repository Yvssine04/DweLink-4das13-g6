package com.dwelink.pharmacy.security.config;

import com.dwelink.pharmacy.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                // Public pharmacy lookups
                .requestMatchers(HttpMethod.GET, "/api/pharmacies", "/api/pharmacies/*", "/api/pharmacies/proches").permitAll()
                // Internal endpoints for inter-service communication
                .requestMatchers(HttpMethod.GET, "/api/pharmacies/*/membres/verifier").permitAll()
                // Admin endpoints
                .requestMatchers(HttpMethod.PATCH, "/api/pharmacies/*/statut").hasRole("ADMINISTRATEUR")
                // Pharmacist endpoints
                .requestMatchers(HttpMethod.POST, "/api/pharmacies").hasAnyRole("PHARMACIEN", "ADMINISTRATEUR")
                .requestMatchers(HttpMethod.POST, "/api/pharmacies/*/membres").hasAnyRole("PHARMACIEN", "ADMINISTRATEUR")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
