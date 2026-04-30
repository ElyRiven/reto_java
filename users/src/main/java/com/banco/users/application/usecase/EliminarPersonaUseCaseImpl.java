package com.banco.users.application.usecase;

import com.banco.users.domain.exception.PersonaNotFoundException;
import com.banco.users.domain.port.out.PersonaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EliminarPersonaUseCaseImpl implements EliminarPersonaUseCase {

    private final PersonaRepositoryPort personaRepositoryPort;

    @Override
    public void execute(UUID id) {
        personaRepositoryPort.findById(id)
                .orElseThrow(() -> new PersonaNotFoundException(
                        "No existe una persona con el id indicado"));
        personaRepositoryPort.softDelete(id);
    }
}
