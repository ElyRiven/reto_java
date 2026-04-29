package com.banco.bank.domain.port.out;

import com.banco.bank.domain.model.Cliente;

import java.util.Optional;
import java.util.UUID;

public interface ClienteRepositoryPort {

    Optional<Cliente> findByClienteId(UUID clienteId);

    Cliente save(Cliente cliente);

    boolean existsByEventId(String eventId);

    void markEventProcessed(String eventId);
}
