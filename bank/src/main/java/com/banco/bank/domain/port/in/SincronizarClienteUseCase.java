package com.banco.bank.domain.port.in;

import com.banco.bank.domain.model.events.ClienteEvent;

public interface SincronizarClienteUseCase {
    void execute(ClienteEvent event);
}
