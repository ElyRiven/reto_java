package com.banco.bank.application.usecase;

import com.banco.bank.domain.model.Cliente;
import com.banco.bank.domain.model.events.ClienteEvent;
import com.banco.bank.domain.port.in.SincronizarClienteUseCase;
import com.banco.bank.domain.port.out.ClienteRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class SincronizarClienteUseCaseImpl implements SincronizarClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;

    @Override
    public void execute(ClienteEvent event) {
        var eventId = event.getMetadata().getEventId();

        if (clienteRepositoryPort.existsByEventId(eventId)) {
            log.info("Evento ya procesado, se omite: eventId={}", eventId);
            return;
        }

        var payload = event.getPayload();
        var existing = clienteRepositoryPort.findByClienteId(payload.getClienteId());

        Cliente cliente;
        if (existing.isPresent()) {
            cliente = existing.get();
            cliente.setNombre(payload.getNombre());
            cliente.setEstado(payload.getEstado());
            cliente.setUpdatedAt(Instant.now());
            if (Boolean.FALSE.equals(payload.getEstado()) && cliente.getDeletedAt() == null) {
                cliente.setDeletedAt(Instant.now());
            }
        } else {
            cliente = new Cliente();
            cliente.setClienteId(payload.getClienteId());
            cliente.setNombre(payload.getNombre());
            cliente.setEstado(payload.getEstado());
            cliente.setCreatedAt(Instant.now());
            if (Boolean.FALSE.equals(payload.getEstado())) {
                cliente.setDeletedAt(Instant.now());
            }
        }

        clienteRepositoryPort.save(cliente);
        clienteRepositoryPort.markEventProcessed(eventId);
        log.info("Cliente sincronizado: clienteId={}, action={}", payload.getClienteId(), payload.getAction());
    }
}
