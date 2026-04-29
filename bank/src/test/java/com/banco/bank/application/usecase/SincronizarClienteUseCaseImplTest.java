package com.banco.bank.application.usecase;

import com.banco.bank.domain.model.Cliente;
import com.banco.bank.domain.model.events.ClienteEvent;
import com.banco.bank.domain.port.out.ClienteRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SincronizarClienteUseCaseImplTest {

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @InjectMocks
    private SincronizarClienteUseCaseImpl useCase;

    @Test
    void debe_OmitirEvento_Cuando_EventoYaFueProcesado() {
        var event = buildEvent(UUID.randomUUID(), "Ana", true, "CREATE_OR_UPDATE", "evt-1");
        when(clienteRepositoryPort.existsByEventId("evt-1")).thenReturn(true);

        useCase.execute(event);

        verify(clienteRepositoryPort, never()).save(any());
        verify(clienteRepositoryPort, never()).markEventProcessed(any());
    }

    @Test
    void debe_InsertarCliente_Cuando_NoExisteRegistroPrevio() {
        var clienteId = UUID.randomUUID();
        var event = buildEvent(clienteId, "Ana", true, "CREATE_OR_UPDATE", "evt-2");

        when(clienteRepositoryPort.existsByEventId("evt-2")).thenReturn(false);
        when(clienteRepositoryPort.findByClienteId(clienteId)).thenReturn(Optional.empty());
        when(clienteRepositoryPort.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(event);

        verify(clienteRepositoryPort).save(any(Cliente.class));
        verify(clienteRepositoryPort).markEventProcessed("evt-2");
    }

    @Test
    void debe_ActualizarCliente_Cuando_RegistroYaExiste() {
        var clienteId = UUID.randomUUID();
        var existente = new Cliente();
        existente.setClienteId(clienteId);
        existente.setNombre("Antes");
        existente.setEstado(true);
        existente.setCreatedAt(Instant.now().minusSeconds(60));

        var event = buildEvent(clienteId, "Despues", true, "UPDATE", "evt-3");

        when(clienteRepositoryPort.existsByEventId("evt-3")).thenReturn(false);
        when(clienteRepositoryPort.findByClienteId(clienteId)).thenReturn(Optional.of(existente));
        when(clienteRepositoryPort.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(event);

        assertEquals("Despues", existente.getNombre());
        assertNotNull(existente.getUpdatedAt());
        verify(clienteRepositoryPort).markEventProcessed("evt-3");
    }

    @Test
    void debe_MarcarDeletedAt_Cuando_EstadoEsFalseEnNuevoRegistro() {
        var clienteId = UUID.randomUUID();
        var event = buildEvent(clienteId, "Ana", false, "PATCH_UPDATE", "evt-4");

        when(clienteRepositoryPort.existsByEventId("evt-4")).thenReturn(false);
        when(clienteRepositoryPort.findByClienteId(clienteId)).thenReturn(Optional.empty());
        when(clienteRepositoryPort.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(event);

        verify(clienteRepositoryPort).save(any(Cliente.class));
        verify(clienteRepositoryPort).markEventProcessed("evt-4");
    }

    @Test
    void debe_MarcarDeletedAt_Cuando_EstadoEsFalseEnRegistroExistente() {
        var clienteId = UUID.randomUUID();
        var existente = new Cliente();
        existente.setClienteId(clienteId);
        existente.setNombre("Ana");
        existente.setEstado(true);
        existente.setCreatedAt(Instant.now().minusSeconds(60));

        var event = buildEvent(clienteId, "Ana", false, "PATCH_UPDATE", "evt-5");

        when(clienteRepositoryPort.existsByEventId("evt-5")).thenReturn(false);
        when(clienteRepositoryPort.findByClienteId(clienteId)).thenReturn(Optional.of(existente));
        when(clienteRepositoryPort.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(event);

        assertEquals(false, existente.getEstado());
        assertNotNull(existente.getDeletedAt());
        verify(clienteRepositoryPort).markEventProcessed("evt-5");
    }

    private ClienteEvent buildEvent(UUID clienteId, String nombre, Boolean estado, String action, String eventId) {
        var metadata = new ClienteEvent.Metadata(eventId, Instant.now(), "1.0");
        var payload = new ClienteEvent.Payload(clienteId, nombre, estado, action);
        return new ClienteEvent(metadata, payload);
    }
}
