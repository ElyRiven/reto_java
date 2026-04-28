package com.banco.users.infrastructure.persistence;

import com.banco.users.domain.model.Persona;
import com.banco.users.domain.port.out.PersonaRepositoryPort;
import com.banco.users.infrastructure.mapper.PersonaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PersonaRepositoryAdapter implements PersonaRepositoryPort {

    private final PersonaJpaRepository personaJpaRepository;
    private final PersonaMapper personaMapper;

    @Override
    public Persona save(Persona persona) {
        var entity = personaMapper.toEntity(persona);
        var saved = personaJpaRepository.save(entity);
        return personaMapper.toDomain(saved);
    }

    @Override
    public Optional<Persona> findByIdentification(String identificacion) {
        return personaJpaRepository.findByIdentificacion(identificacion)
                .map(personaMapper::toDomain);
    }
}
