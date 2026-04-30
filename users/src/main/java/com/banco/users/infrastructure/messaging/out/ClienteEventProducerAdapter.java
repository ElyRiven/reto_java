package com.banco.users.infrastructure.messaging.out;

import com.banco.users.domain.model.events.ClienteEvent;
import com.banco.users.domain.port.out.ClienteEventProducerPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClienteEventProducerAdapter implements ClienteEventProducerPort {

    private final RabbitTemplate rabbitTemplate;

    @Value("${messaging.exchange.name}")
    private String exchangeName;

    @Value("${messaging.routing-key.cliente}")
    private String routingKey;

    @Override
    public void publish(ClienteEvent event) {
        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, event);
            log.info("Evento publicado: eventId={}, action={}",
                    event.getMetadata().getEventId(),
                    event.getPayload().getAction());
        } catch (Exception e) {
            log.error("Error publicando evento clienteId={}: {}",
                    event.getPayload().getClienteId(), e.getMessage(), e);
        }
    }
}
