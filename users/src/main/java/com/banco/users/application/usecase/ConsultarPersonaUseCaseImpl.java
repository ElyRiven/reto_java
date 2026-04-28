package com.banco.users.application.usecase;

import com.banco.users.domain.exception.PersonaNotFoundException;
import com.banco.users.domain.model.Persona;
import com.banco.users.domain.port.out.PersonaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConsultarPersonaUseCaseImpl implements ConsultarPersonaUseCase {

    private final PersonaRepositoryPort personaRepositoryPort;

    @Override
    public Persona execute(String identificacion) {
        return personaRepositoryPort.findByIdentification(identificacion)
                .orElseThrow(() -> new PersonaNotFoundException("No existe una persona con la identificación indicada"));
    }
}
