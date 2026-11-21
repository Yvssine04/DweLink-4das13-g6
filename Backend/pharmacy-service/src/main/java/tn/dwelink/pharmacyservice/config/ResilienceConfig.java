package tn.dwelink.pharmacyservice.config;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.openfeign.CircuitBreakerNameResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tn.dwelink.pharmacyservice.client.UtilisateurClient;

@Slf4j
@Configuration
public class ResilienceConfig {

    /**
     * Par défaut, OpenFeign nomme le circuit breaker d'après la méthode ("UtilisateurClient#trouver(Long)").
     * On utilise plutôt le contextId du client : "utilisateurService".
     */
    @Bean
    public CircuitBreakerNameResolver nommageCircuitBreaker() {
        return (nomClientFeign, cible, methode) -> nomClientFeign;
    }

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> circuitBreakerUtilisateurService() {
        CircuitBreakerConfig circuitBreaker = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(10)
                .minimumNumberOfCalls(10)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                // Un 404 est une réponse normale (utilisateur inexistant), pas une panne de user-service.
                .ignoreExceptions(FeignException.NotFound.class)
                .build();

        // Le TimeLimiter (1 s par défaut) couperait l'appel avant le timeout de lecture Feign (3 s).
        TimeLimiterConfig limiteDeTemps = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(4))
                .build();

        return factory -> {
            factory.configure(builder -> builder
                    .circuitBreakerConfig(circuitBreaker)
                    .timeLimiterConfig(limiteDeTemps), UtilisateurClient.NOM);
            factory.addCircuitBreakerCustomizer(cb -> cb.getEventPublisher().onStateTransition(e ->
                    log.warn("Circuit breaker {} : {}", e.getCircuitBreakerName(), e.getStateTransition())),
                    UtilisateurClient.NOM);
        };
    }
}
