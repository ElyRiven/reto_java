package com.banco.users.domain.port.out;

import com.banco.users.domain.model.Persona;

import java.util.Optional;

public interface PersonaRepositoryPort {

    Persona save(Persona persona);

    Optional<Persona> findByIdentification(String identificacion);
}
