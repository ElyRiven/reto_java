package com.banco.users.application.usecase;

import com.banco.users.domain.exception.ClienteNoEncontradoException;
import com.banco.users.domain.model.events.ClienteEvent;
import com.banco.users.domain.port.out.ClienteEventProducerPort;
import com.banco.users.domain.port.out.ClienteRepositoryPort;
import com.fasterxml.uuid.Generators;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EliminarClienteUseCaseImpl implements EliminarClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final ClienteEventProducerPort eventProducerPort;

    @Override
    public void execute(UUID clienteId) {
        var cliente = clienteRepositoryPort.findByClienteId(clienteId)
                .orElseThrow(() -> new ClienteNoEncontradoException(
                        "Cliente no encontrado con clienteId: " + clienteId));
        clienteRepositoryPort.softDelete(clienteId);

        try {
            var metadata = new ClienteEvent.Metadata(
                    Generators.timeBasedEpochGenerator().generate().toString(),
                    Instant.now(),
                    "1.0"
            );
            var payload = new ClienteEvent.Payload(
                    cliente.getClienteId(), cliente.getNombre(), false, "PATCH_UPDATE"
            );
            eventProducerPort.publish(new ClienteEvent(metadata, payload));
        } catch (Exception e) {
            log.warn("Error al publicar evento de eliminación clienteId={}: {}", clienteId, e.getMessage());
        }
    }
}
