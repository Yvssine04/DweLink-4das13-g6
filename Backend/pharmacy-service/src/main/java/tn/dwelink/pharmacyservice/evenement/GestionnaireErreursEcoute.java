package tn.dwelink.pharmacyservice.evenement;

import com.rabbitmq.client.Channel;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.listener.ListenerExecutionFailedException;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.stereotype.Component;

/**
 * Un message illisible (JSON cassé) ou invalide est rejeté sans être remis dans la queue,
 * pour ne pas tourner en boucle. Les autres erreurs (base indisponible…) sont relancées telles quelles.
 */
@Slf4j
@Component("gestionnaireErreursEcoute")
public class GestionnaireErreursEcoute implements RabbitListenerErrorHandler {

    @Override
    public Object handleError(Message messageAmqp, Channel canal,
                              org.springframework.messaging.Message<?> message,
                              ListenerExecutionFailedException exception) throws Exception {
        if (exception.getCause() instanceof MessageConversionException cause) {
            log.error("Message rejeté sur {} (illisible ou invalide) : {} | contenu : {}",
                    messageAmqp.getMessageProperties().getConsumerQueue(), cause.getMessage(), contenu(messageAmqp));
            throw new AmqpRejectAndDontRequeueException("Message rejeté", exception);
        }
        throw exception;
    }

    private static String contenu(Message message) {
        return new String(message.getBody(), StandardCharsets.UTF_8);
    }
}
