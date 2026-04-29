package com.banco.users.application.usecase;

import com.banco.users.domain.exception.PersonaNotFoundException;
import com.banco.users.domain.model.Persona;
import com.banco.users.domain.port.out.PersonaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActualizarParcialPersonaUseCaseImpl implements ActualizarParcialPersonaUseCase {

    private final PersonaRepositoryPort personaRepositoryPort;

    @Override
    public Persona execute(UUID id, PatchPersonaCommand command) {
        var existente = personaRepositoryPort.findById(id)
                .orElseThrow(() -> new PersonaNotFoundException(
                        "No existe una persona con el id indicado"));

        Optional.ofNullable(command.nombre()).ifPresent(existente::setNombre);
        Optional.ofNullable(command.genero()).ifPresent(existente::setGenero);
        Optional.ofNullable(command.edad()).ifPresent(existente::setEdad);
        Optional.ofNullable(command.identificacion()).ifPresent(existente::setIdentificacion);
        Optional.ofNullable(command.direccion()).ifPresent(existente::setDireccion);
        Optional.ofNullable(command.telefono()).ifPresent(existente::setTelefono);
        existente.setUpdatedAt(Instant.now());

        return personaRepositoryPort.save(existente);
    }
}
