package com.banco.users.application.usecase;

import com.banco.users.domain.model.Cliente;

import java.util.UUID;

public interface ActualizarParcialClienteUseCase {

    Cliente execute(UUID clienteId, PatchClienteCommand command);
}
