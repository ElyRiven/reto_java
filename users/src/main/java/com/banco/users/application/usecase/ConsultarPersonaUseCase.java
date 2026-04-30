package com.banco.users.application.usecase;

import com.banco.users.domain.model.Persona;

import java.util.UUID;

public interface ConsultarPersonaUseCase {

    Persona execute(UUID id);
}
