package com.banco.users.application.usecase;

import com.banco.users.domain.exception.ClienteNoEncontradoException;
import com.banco.users.domain.model.Cliente;
import com.banco.users.domain.port.out.ClienteRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarClienteUseCaseImplTest {

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @InjectMocks
    private ConsultarClienteUseCaseImpl useCase;

    @Test
    void debe_RetornarCliente_Cuando_Existe() {
        var clienteId = UUID.randomUUID();
        var cliente = new Cliente();
        cliente.setClienteId(clienteId);
        cliente.setNombre("Ana");

        when(clienteRepositoryPort.findByClienteId(clienteId)).thenReturn(Optional.of(cliente));

        var result = useCase.execute(clienteId);

        assertEquals(clienteId, result.getClienteId());
        assertEquals("Ana", result.getNombre());
    }

    @Test
    void debe_LanzarClienteNoEncontradoException_Cuando_NoExiste() {
        var clienteId = UUID.randomUUID();
        when(clienteRepositoryPort.findByClienteId(clienteId)).thenReturn(Optional.empty());

        assertThrows(ClienteNoEncontradoException.class,
                () -> useCase.execute(clienteId));
    }
}
