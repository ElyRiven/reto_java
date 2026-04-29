package com.banco.users.infrastructure.persistence;

import com.banco.users.domain.exception.PersonaNotFoundException;
import com.banco.users.domain.model.Persona;
import com.banco.users.domain.port.out.PersonaRepositoryPort;
import com.banco.users.infrastructure.mapper.PersonaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

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
        return personaJpaRepository.findByIdentificacionAndDeletedAtIsNull(identificacion)
                .map(personaMapper::toDomain);
    }

    @Override
    public Optional<Persona> findById(UUID id) {
        return personaJpaRepository.findByIdAndDeletedAtIsNull(id)
                .map(personaMapper::toDomain);
    }

    @Override
    public void softDelete(UUID id) {
        var entity = personaJpaRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new PersonaNotFoundException(
                        "No existe una persona con el id indicado"));
        entity.setDeletedAt(Instant.now());
        personaJpaRepository.save(entity);
    }
}
