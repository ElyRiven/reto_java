package com.banco.users.application.usecase;

import com.banco.users.domain.exception.PersonaYaExisteException;
import com.banco.users.domain.model.Persona;
import com.banco.users.domain.port.out.PersonaRepositoryPort;
import com.fasterxml.uuid.Generators;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RegistrarPersonaUseCaseImpl implements RegistrarPersonaUseCase {

    private final PersonaRepositoryPort personaRepositoryPort;

    @Override
    public Persona execute(Persona persona) {
        if (personaRepositoryPort.findByIdentification(persona.getIdentificacion()).isPresent()) {
            throw new PersonaYaExisteException(
                    "El número de identificación ya existe: " + persona.getIdentificacion());
        }
        persona.setId(Generators.timeBasedEpochGenerator().generate());
        persona.setCreatedAt(Instant.now());
        return personaRepositoryPort.save(persona);
    }
}
