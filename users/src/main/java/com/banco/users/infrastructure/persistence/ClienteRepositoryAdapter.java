package com.banco.users.infrastructure.persistence;

import com.banco.users.domain.exception.ClienteNoEncontradoException;
import com.banco.users.domain.model.Cliente;
import com.banco.users.domain.port.out.ClienteRepositoryPort;
import com.banco.users.infrastructure.mapper.ClienteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository clienteJpaRepository;
    private final PersonaJpaRepository personaJpaRepository;
    private final ClienteMapper clienteMapper;

    @Override
    public Cliente save(Cliente cliente) {
        var personaEntity = personaJpaRepository.getReferenceById(cliente.getId());
        personaEntity.setNombre(cliente.getNombre());
        personaEntity.setGenero(cliente.getGenero());
        personaEntity.setEdad(cliente.getEdad());
        personaEntity.setIdentificacion(cliente.getIdentificacion());
        personaEntity.setDireccion(cliente.getDireccion());
        personaEntity.setTelefono(cliente.getTelefono());
        personaEntity.setUpdatedAt(cliente.getUpdatedAt());
        personaJpaRepository.save(personaEntity);
        var entity = clienteMapper.toEntity(cliente, personaEntity);
        var saved = clienteJpaRepository.save(entity);
        return clienteMapper.toDomain(saved);
    }

    @Override
    public Optional<Cliente> findByClienteId(UUID clienteId) {
        return clienteJpaRepository.findByClienteIdAndDeletedAtIsNull(clienteId)
                .map(clienteMapper::toDomain);
    }

    @Override
    public boolean existsByPersonaId(UUID personaId) {
        return clienteJpaRepository.existsByPersona_Id(personaId);
    }

    @Override
    public void softDelete(UUID clienteId) {
        var entity = clienteJpaRepository.findByClienteIdAndDeletedAtIsNull(clienteId)
                .orElseThrow(() -> new ClienteNoEncontradoException(
                        "Cliente no encontrado con clienteId: " + clienteId));
        entity.setEstado(false);
        entity.setDeletedAt(Instant.now());
        clienteJpaRepository.save(entity);
    }
}
