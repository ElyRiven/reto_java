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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarParcialClienteUseCaseImplTest {

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private ClienteEventProducerPort eventProducerPort;

    @InjectMocks
    private ActualizarParcialClienteUseCaseImpl useCase;

    @Test
    void debe_ActualizarSoloCamposEnviados_Cuando_ComandoParcialEsValido() {
        var clienteId = UUID.randomUUID();
        var existente = new Cliente();
        existente.setClienteId(clienteId);
        existente.setNombre("Antes");
        existente.setDireccion("Dir anterior");
        existente.setContrasena("hash-anterior");
        existente.setEstado(true);

        var command = new PatchClienteCommand(
                "Despues",
                null,
                null,
                null,
                "Dir nueva",
                null,
                "NuevaPass123",
                false
        );

        when(clienteRepositoryPort.findByClienteId(clienteId)).thenReturn(Optional.of(existente));
        when(passwordEncoder.encode("NuevaPass123")).thenReturn("nuevo-hash");
        when(clienteRepositoryPort.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = useCase.execute(clienteId, command);

        assertNotNull(result);
        assertEquals("Despues", result.getNombre());
        assertEquals("Dir nueva", result.getDireccion());
        assertEquals("nuevo-hash", result.getContrasena());
        assertEquals(false, result.getEstado());
        verify(passwordEncoder).encode("NuevaPass123");
        verify(eventProducerPort).publish(any());
    }

    @Test
    void debe_NoRehashearContrasena_Cuando_NoSeEnviaContrasenaEnPatch() {
        var clienteId = UUID.randomUUID();
        var existente = new Cliente();
        existente.setClienteId(clienteId);
        existente.setContrasena("hash-anterior");

        var command = new PatchClienteCommand(null, null, null, null, null, null, null, null);

        when(clienteRepositoryPort.findByClienteId(clienteId)).thenReturn(Optional.of(existente));
        when(clienteRepositoryPort.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = useCase.execute(clienteId, command);

        assertEquals("hash-anterior", result.getContrasena());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void debe_LanzarClienteNoEncontradoException_Cuando_ClienteNoExiste() {
        var clienteId = UUID.randomUUID();
        when(clienteRepositoryPort.findByClienteId(clienteId)).thenReturn(Optional.empty());

        assertThrows(ClienteNoEncontradoException.class,
                () -> useCase.execute(clienteId, new PatchClienteCommand(null, null, null, null, null, null, null, null)));
    }

    @Test
    void debe_RetornarClienteActualizado_Cuando_FallaPublicacionEvento() {
        var clienteId = UUID.randomUUID();
        var existente = new Cliente();
        existente.setClienteId(clienteId);

        when(clienteRepositoryPort.findByClienteId(clienteId)).thenReturn(Optional.of(existente));
        when(clienteRepositoryPort.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doThrow(new RuntimeException("broker down")).when(eventProducerPort).publish(any());

        var result = useCase.execute(clienteId, new PatchClienteCommand("Nombre", null, null, null, null, null, null, null));

        assertNotNull(result);
        assertEquals("Nombre", result.getNombre());
    }
}
