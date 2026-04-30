package com.banco.users.application.usecase;

import com.banco.users.domain.model.Persona;

public interface RegistrarPersonaUseCase {

    Persona execute(Persona persona);
}
