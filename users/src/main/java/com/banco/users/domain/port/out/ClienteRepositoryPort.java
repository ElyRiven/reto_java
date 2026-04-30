package com.banco.users.domain.port.out;

import com.banco.users.domain.model.Cliente;

import java.util.Optional;
import java.util.UUID;

public interface ClienteRepositoryPort {

    Cliente save(Cliente cliente);

    Optional<Cliente> findByClienteId(UUID clienteId);

    boolean existsByPersonaId(UUID personaId);

    void softDelete(UUID clienteId);
}
