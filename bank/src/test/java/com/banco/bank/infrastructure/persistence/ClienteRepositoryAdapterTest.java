package com.banco.bank.infrastructure.persistence;

import com.banco.bank.domain.model.Cliente;
import com.banco.bank.infrastructure.entity.ClienteEntity;
import com.banco.bank.infrastructure.entity.ProcessedEventEntity;
import com.banco.bank.infrastructure.mapper.ClienteMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteRepositoryAdapterTest {

    @Mock
    private ClienteJpaRepository clienteJpaRepository;

    @Mock
    private ProcessedEventJpaRepository processedEventJpaRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @InjectMocks
    private ClienteRepositoryAdapter adapter;

    @Test
    void debe_RetornarCliente_Cuando_FindByClienteIdEncuentraRegistro() {
        var id = UUID.randomUUID();
        var entity = new ClienteEntity();
        entity.setClienteId(id);
        var domain = new Cliente();
        domain.setClienteId(id);

        when(clienteJpaRepository.findByClienteId(id)).thenReturn(Optional.of(entity));
        when(clienteMapper.toDomain(entity)).thenReturn(domain);

        var result = adapter.findByClienteId(id);

        assertEquals(true, result.isPresent());
        assertEquals(id, result.orElseThrow().getClienteId());
    }

    @Test
    void debe_GuardarCliente_Cuando_SaveEsInvocado() {
        var id = UUID.randomUUID();
        var domain = new Cliente();
        domain.setClienteId(id);
        domain.setNombre("Ana");
        domain.setEstado(true);

        var entity = new ClienteEntity();
        entity.setClienteId(id);

        var savedEntity = new ClienteEntity();
        savedEntity.setClienteId(id);

        var savedDomain = new Cliente();
        savedDomain.setClienteId(id);
        savedDomain.setNombre("Ana");

        when(clienteMapper.toEntity(domain)).thenReturn(entity);
        when(clienteJpaRepository.save(entity)).thenReturn(savedEntity);
        when(clienteMapper.toDomain(savedEntity)).thenReturn(savedDomain);

        var result = adapter.save(domain);

        assertNotNull(result);
        assertEquals(id, result.getClienteId());
        assertEquals("Ana", result.getNombre());
    }

    @Test
    void debe_ValidarExistenciaEventoProcesado_Cuando_ExistsByEventId() {
        when(processedEventJpaRepository.existsById("evt-1")).thenReturn(true);

        var result = adapter.existsByEventId("evt-1");

        assertEquals(true, result);
    }

    @Test
    void debe_MarcarEventoProcesado_Cuando_MarkEventProcessed() {
        adapter.markEventProcessed("evt-2");

        var captor = ArgumentCaptor.forClass(ProcessedEventEntity.class);
        verify(processedEventJpaRepository).save(captor.capture());

        var saved = captor.getValue();
        assertEquals("evt-2", saved.getEventId());
        assertNotNull(saved.getProcessedAt());
    }
}
