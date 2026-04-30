package com.banco.users.application.usecase;

import com.banco.users.domain.model.Cliente;

import java.util.UUID;

public interface ActualizarClienteUseCase {

    Cliente execute(UUID clienteId, Cliente cliente);
}
