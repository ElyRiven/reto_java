package com.banco.users.application.usecase;

import com.banco.users.domain.exception.ClienteNoEncontradoException;
import com.banco.users.domain.model.Cliente;
import com.banco.users.domain.model.events.ClienteEvent;
import com.banco.users.domain.port.out.ClienteEventProducerPort;
import com.banco.users.domain.port.out.ClienteRepositoryPort;
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
public class ActualizarClienteUseCaseImpl implements ActualizarClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ClienteEventProducerPort eventProducerPort;

    @Override
    public Cliente execute(UUID clienteId, Cliente datosActualizados) {
        var existente = clienteRepositoryPort.findByClienteId(clienteId)
                .orElseThrow(() -> new ClienteNoEncontradoException(
                        "Cliente no encontrado con clienteId: " + clienteId));

        existente.setNombre(datosActualizados.getNombre());
        existente.setGenero(datosActualizados.getGenero());
        existente.setEdad(datosActualizados.getEdad());
        existente.setIdentificacion(datosActualizados.getIdentificacion());
        existente.setDireccion(datosActualizados.getDireccion());
        existente.setTelefono(datosActualizados.getTelefono());
        existente.setContrasena(passwordEncoder.encode(datosActualizados.getContrasena()));
        existente.setEstado(datosActualizados.getEstado());
        existente.setUpdatedAt(Instant.now());

        var saved = clienteRepositoryPort.save(existente);

        try {
            var metadata = new ClienteEvent.Metadata(
                    Generators.timeBasedEpochGenerator().generate().toString(),
                    Instant.now(),
                    "1.0"
            );
            var payload = new ClienteEvent.Payload(
                    saved.getClienteId(), saved.getNombre(), saved.getEstado(), "UPDATE"
            );
            eventProducerPort.publish(new ClienteEvent(metadata, payload));
        } catch (Exception e) {
            log.warn("Error al publicar evento de actualización clienteId={}: {}", clienteId, e.getMessage());
        }

        return saved;
    }
}
