package com.banco.users.application.usecase;

import com.banco.users.domain.model.Persona;

import java.util.UUID;

public interface ActualizarParcialPersonaUseCase {

    Persona execute(UUID id, PatchPersonaCommand command);
}
