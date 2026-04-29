package com.banco.users.application.usecase;

import com.banco.users.domain.exception.ClienteNoEncontradoException;
import com.banco.users.domain.model.Cliente;
import com.banco.users.domain.port.out.ClienteEventProducerPort;
import com.banco.users.domain.port.out.ClienteRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EliminarClienteUseCaseImplTest {

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @Mock
    private ClienteEventProducerPort eventProducerPort;

    @InjectMocks
    private EliminarClienteUseCaseImpl useCase;

    @Test
    void debe_RealizarSoftDelete_Cuando_ClienteExiste() {
        var clienteId = UUID.randomUUID();
        var cliente = new Cliente();
        cliente.setClienteId(clienteId);
        cliente.setNombre("Ana");

        when(clienteRepositoryPort.findByClienteId(clienteId)).thenReturn(Optional.of(cliente));

        assertDoesNotThrow(() -> useCase.execute(clienteId));

        verify(clienteRepositoryPort).softDelete(clienteId);
        verify(eventProducerPort).publish(any());
    }

    @Test
    void debe_LanzarClienteNoEncontradoException_Cuando_ClienteNoExiste() {
        var clienteId = UUID.randomUUID();
        when(clienteRepositoryPort.findByClienteId(clienteId)).thenReturn(Optional.empty());

        assertThrows(ClienteNoEncontradoException.class,
                () -> useCase.execute(clienteId));
    }

    @Test
    void debe_CompletarSoftDelete_Cuando_FallaPublicacionEvento() {
        var clienteId = UUID.randomUUID();
        var cliente = new Cliente();
        cliente.setClienteId(clienteId);
        cliente.setNombre("Ana");

        when(clienteRepositoryPort.findByClienteId(clienteId)).thenReturn(Optional.of(cliente));
        doThrow(new RuntimeException("broker down")).when(eventProducerPort).publish(any());

        assertDoesNotThrow(() -> useCase.execute(clienteId));

        verify(clienteRepositoryPort).softDelete(clienteId);
    }
}
