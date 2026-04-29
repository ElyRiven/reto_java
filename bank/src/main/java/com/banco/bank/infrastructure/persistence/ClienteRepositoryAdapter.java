package com.banco.bank.infrastructure.persistence;

import com.banco.bank.domain.model.Cliente;
import com.banco.bank.domain.port.out.ClienteRepositoryPort;
import com.banco.bank.infrastructure.entity.ProcessedEventEntity;
import com.banco.bank.infrastructure.mapper.ClienteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository clienteJpaRepository;
    private final ProcessedEventJpaRepository processedEventJpaRepository;
    private final ClienteMapper clienteMapper;

    @Override
    public Optional<Cliente> findByClienteId(UUID clienteId) {
        return clienteJpaRepository.findByClienteId(clienteId)
                .map(clienteMapper::toDomain);
    }

    @Override
    public Cliente save(Cliente cliente) {
        var entity = clienteMapper.toEntity(cliente);
        var saved = clienteJpaRepository.save(entity);
        return clienteMapper.toDomain(saved);
    }

    @Override
    public boolean existsByEventId(String eventId) {
        return processedEventJpaRepository.existsById(eventId);
    }

    @Override
    public void markEventProcessed(String eventId) {
        var processed = new ProcessedEventEntity();
        processed.setEventId(eventId);
        processed.setProcessedAt(Instant.now());
        processedEventJpaRepository.save(processed);
    }
}
