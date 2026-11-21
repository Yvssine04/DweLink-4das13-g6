package tn.dwelink.pharmacyservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Client REST vers user-service, protégé par le circuit breaker "utilisateurService" (voir ResilienceConfig).
 * <p>
 * Pas d'URL fixe : Feign résout le nom "user-service" via le registre Eureka (avec répartition de charge).
 */
@FeignClient(name = "user-service", contextId = UtilisateurClient.NOM)
public interface UtilisateurClient {

    /** Nom du client Feign, utilisé aussi pour le circuit breaker et les timeouts. */
    String NOM = "utilisateurService";

    @GetMapping("/api/utilisateurs/{id}")
    UtilisateurResponse trouver(@PathVariable("id") Long id);
}
