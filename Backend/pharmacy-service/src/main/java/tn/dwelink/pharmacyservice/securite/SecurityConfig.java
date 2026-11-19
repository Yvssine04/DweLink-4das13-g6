package tn.dwelink.pharmacyservice.securite;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
public class SecurityConfig {

    /**
     * Les erreurs 401/403 levées dans les filtres de sécurité sont confiées au mécanisme MVC,
     * pour être rendues par GestionErreurs au même format JSON {code, message, date}.
     */
    private final HandlerExceptionResolver resolveurErreurs;

    public SecurityConfig(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolveurErreurs) {
        this.resolveurErreurs = resolveurErreurs;
    }

    @Bean
    public SecurityFilterChain filtreSecurite(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET,
                                "/api/pharmacies", "/api/pharmacies/proches", "/api/pharmacies/{id}").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/pharmacies/{id}/statut").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(convertisseurJwt()))
                        .authenticationEntryPoint(pointEntree())
                        .accessDeniedHandler(gestionnaireAccesRefuse()))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(pointEntree())
                        .accessDeniedHandler(gestionnaireAccesRefuse()));
        return http.build();
    }

    /** Rôles Keycloak (realm_access.roles : ADMIN, PHARMACIEN, PATIENT) → ROLE_ADMIN, ROLE_PHARMACIEN, ROLE_PATIENT. */
    private JwtAuthenticationConverter convertisseurJwt() {
        JwtAuthenticationConverter convertisseur = new JwtAuthenticationConverter();
        convertisseur.setJwtGrantedAuthoritiesConverter(SecurityConfig::rolesDuRealm);
        return convertisseur;
    }

    private static Collection<GrantedAuthority> rolesDuRealm(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null || !(realmAccess.get("roles") instanceof Collection<?> roles)) {
            return List.of();
        }
        return roles.stream()
                .map(String::valueOf)
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
    }

    private AuthenticationEntryPoint pointEntree() {
        return (requete, reponse, exception) -> resolveurErreurs.resolveException(requete, reponse, null, exception);
    }

    private AccessDeniedHandler gestionnaireAccesRefuse() {
        return (requete, reponse, exception) -> resolveurErreurs.resolveException(requete, reponse, null, exception);
    }
}
