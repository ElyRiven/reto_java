package com.banco.users.application.usecase;

import com.banco.users.domain.model.Persona;

public interface ConsultarPersonaUseCase {

    Persona execute(String identificacion);
}
