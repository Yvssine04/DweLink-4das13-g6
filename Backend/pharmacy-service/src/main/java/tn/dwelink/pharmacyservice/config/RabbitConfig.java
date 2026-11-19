package tn.dwelink.pharmacyservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJavaTypeMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.amqp.autoconfigure.RabbitTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_EVENEMENTS = "dwelink.events";

    public static final String QUEUE_COMPTE = "pharmacie.compte.q";
    public static final String CLE_STATUT_COMPTE_MODIFIE = "utilisateur.compte.statut.modifie";

    @Bean
    public TopicExchange exchangeEvenements() {
        return new TopicExchange(EXCHANGE_EVENEMENTS, true, false);
    }

    @Bean
    public Queue queueCompte() {
        return QueueBuilder.durable(QUEUE_COMPTE).build();
    }

    @Bean
    public Binding liaisonQueueCompte(Queue queueCompte, TopicExchange exchangeEvenements) {
        return BindingBuilder.bind(queueCompte).to(exchangeEvenements).with(CLE_STATUT_COMPTE_MODIFIE);
    }

    /**
     * Convertisseur JSON basé sur Jackson 3 (le JsonMapper configuré par Spring Boot).
     * En réception, le type cible est déduit du paramètre de la méthode d'écoute (INFERRED)
     * et non du header __TypeId__, qui désigne une classe des autres services.
     */
    @Bean
    public MessageConverter convertisseurJson(JsonMapper jsonMapper) {
        JacksonJsonMessageConverter convertisseur = new JacksonJsonMessageConverter(jsonMapper);
        convertisseur.setTypePrecedence(JacksonJavaTypeMapper.TypePrecedence.INFERRED);
        return convertisseur;
    }

    @Bean
    public RabbitTemplateCustomizer brancherConvertisseurJson(MessageConverter convertisseurJson) {
        return rabbitTemplate -> rabbitTemplate.setMessageConverter(convertisseurJson);
    }
}
