package tn.dwelink.pharmacyservice.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.amqp.autoconfigure.RabbitTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_EVENEMENTS = "dwelink.events";

    @Bean
    public TopicExchange exchangeEvenements() {
        return new TopicExchange(EXCHANGE_EVENEMENTS, true, false);
    }

    /** Convertisseur JSON basé sur Jackson 3 (le JsonMapper configuré par Spring Boot). */
    @Bean
    public MessageConverter convertisseurJson(JsonMapper jsonMapper) {
        return new JacksonJsonMessageConverter(jsonMapper);
    }

    @Bean
    public RabbitTemplateCustomizer brancherConvertisseurJson(MessageConverter convertisseurJson) {
        return rabbitTemplate -> rabbitTemplate.setMessageConverter(convertisseurJson);
    }
}
