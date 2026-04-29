package com.banco.bank.infrastructure.messaging.in;

import com.banco.bank.domain.model.events.ClienteEvent;
import com.banco.bank.domain.port.in.SincronizarClienteUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClienteEventConsumerAdapter {

    private final SincronizarClienteUseCase sincronizarClienteUseCase;

    @RabbitListener(queues = "${messaging.queue.name}")
    public void consume(ClienteEvent event) {
        log.info("Evento recibido: eventId={}", event.getMetadata().getEventId());
        sincronizarClienteUseCase.execute(event);
    }
}
