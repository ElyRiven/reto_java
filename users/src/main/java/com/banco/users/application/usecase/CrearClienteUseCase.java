package com.banco.users.application.usecase;

import com.banco.users.domain.model.Cliente;

import java.util.UUID;

public interface CrearClienteUseCase {

    Cliente execute(UUID personaId, String contrasena, Boolean estado);
}
