package com.banco.users.application.usecase;

import com.banco.users.domain.exception.PersonaNotFoundException;
import com.banco.users.domain.model.Persona;
import com.banco.users.domain.port.out.PersonaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActualizarPersonaUseCaseImpl implements ActualizarPersonaUseCase {

    private final PersonaRepositoryPort personaRepositoryPort;

    @Override
    public Persona execute(UUID id, Persona datosActualizados) {
        var existente = personaRepositoryPort.findById(id)
                .orElseThrow(() -> new PersonaNotFoundException(
                        "No existe una persona con el id indicado"));

        existente.setNombre(datosActualizados.getNombre());
        existente.setGenero(datosActualizados.getGenero());
        existente.setEdad(datosActualizados.getEdad());
        existente.setIdentificacion(datosActualizados.getIdentificacion());
        existente.setDireccion(datosActualizados.getDireccion());
        existente.setTelefono(datosActualizados.getTelefono());
        existente.setUpdatedAt(Instant.now());

        return personaRepositoryPort.save(existente);
    }
}
