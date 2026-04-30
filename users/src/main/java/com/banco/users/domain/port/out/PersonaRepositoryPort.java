package com.banco.users.domain.port.out;

import com.banco.users.domain.model.Persona;

import java.util.Optional;
import java.util.UUID;

public interface PersonaRepositoryPort {

    Persona save(Persona persona);

    Optional<Persona> findByIdentification(String identificacion);

    Optional<Persona> findById(UUID id);

    void softDelete(UUID id);
}
