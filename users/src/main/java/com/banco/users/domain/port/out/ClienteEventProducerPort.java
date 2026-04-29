package com.banco.users.domain.port.out;

import com.banco.users.domain.model.events.ClienteEvent;

public interface ClienteEventProducerPort {
    void publish(ClienteEvent event);
}
