package tn.dwelink.pharmacyservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Client REST vers user-service, protégé par le circuit breaker "utilisateurService" (voir ResilienceConfig).
 * <p>
 * Pour l'instant l'URL est fixe (propriété services.utilisateur.url). Plus tard, avec Eureka,
 * on supprimera l'attribut url : Feign résoudra alors le nom "user-service" via le registre.
 */
@FeignClient(name = "user-service", contextId = UtilisateurClient.NOM, url = "${services.utilisateur.url}")
public interface UtilisateurClient {

    /** Nom du client Feign, utilisé aussi pour le circuit breaker et les timeouts. */
    String NOM = "utilisateurService";

    @GetMapping("/api/utilisateurs/{id}")
    UtilisateurResponse trouver(@PathVariable("id") Long id);
}
