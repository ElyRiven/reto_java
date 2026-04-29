package com.banco.users.application.usecase;

import com.banco.users.domain.exception.ClienteYaExisteException;
import com.banco.users.domain.exception.PersonaNotFoundException;
import com.banco.users.domain.model.Cliente;
import com.banco.users.domain.model.events.ClienteEvent;
import com.banco.users.domain.port.out.ClienteEventProducerPort;
import com.banco.users.domain.port.out.ClienteRepositoryPort;
import com.banco.users.domain.port.out.PersonaRepositoryPort;
import com.fasterxml.uuid.Generators;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrearClienteUseCaseImpl implements CrearClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final PersonaRepositoryPort personaRepositoryPort;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ClienteEventProducerPort eventProducerPort;

    @Override
    public Cliente execute(UUID personaId, String contrasena, Boolean estado) {
        var persona = personaRepositoryPort.findById(personaId)
                .orElseThrow(() -> new PersonaNotFoundException(
                        "Persona no encontrada con id: " + personaId));

        if (clienteRepositoryPort.existsByPersonaId(personaId)) {
            throw new ClienteYaExisteException(
                    "El personaId ya está registrado como cliente");
        }

        var cliente = new Cliente();
        cliente.setId(persona.getId());
        cliente.setNombre(persona.getNombre());
        cliente.setGenero(persona.getGenero());
        cliente.setEdad(persona.getEdad());
        cliente.setIdentificacion(persona.getIdentificacion());
        cliente.setDireccion(persona.getDireccion());
        cliente.setTelefono(persona.getTelefono());
        cliente.setClienteId(Generators.timeBasedEpochGenerator().generate());
        cliente.setContrasena(passwordEncoder.encode(contrasena));
        cliente.setEstado(estado);
        cliente.setCreatedAt(Instant.now());

        var saved = clienteRepositoryPort.save(cliente);

        try {
            var metadata = new ClienteEvent.Metadata(
                    Generators.timeBasedEpochGenerator().generate().toString(),
                    Instant.now(),
                    "1.0"
            );
            var payload = new ClienteEvent.Payload(
                    saved.getClienteId(), saved.getNombre(), saved.getEstado(), "CREATE_OR_UPDATE"
            );
            eventProducerPort.publish(new ClienteEvent(metadata, payload));
        } catch (Exception e) {
            log.warn("Error al publicar evento de creación clienteId={}: {}", saved.getClienteId(), e.getMessage());
        }

        return saved;
    }
}
