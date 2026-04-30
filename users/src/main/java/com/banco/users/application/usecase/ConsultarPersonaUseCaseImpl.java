package com.banco.users.application.usecase;

import com.banco.users.domain.exception.PersonaNotFoundException;
import com.banco.users.domain.model.Persona;
import com.banco.users.domain.port.out.PersonaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsultarPersonaUseCaseImpl implements ConsultarPersonaUseCase {

    private final PersonaRepositoryPort personaRepositoryPort;

    @Override
    public Persona execute(UUID id) {
        return personaRepositoryPort.findById(id)
                .orElseThrow(() -> new PersonaNotFoundException("No existe una persona con el id indicado"));
    }
}
