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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarClienteUseCaseImplTest {

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private ClienteEventProducerPort eventProducerPort;

    @InjectMocks
    private ActualizarClienteUseCaseImpl useCase;

    @Test
    void debe_ActualizarCliente_Cuando_Existe() {
        var clienteId = UUID.randomUUID();

        var existente = new Cliente();
        existente.setClienteId(clienteId);
        existente.setNombre("Anterior");

        var datos = new Cliente();
        datos.setNombre("Nuevo");
        datos.setGenero("F");
        datos.setEdad(20);
        datos.setIdentificacion("321");
        datos.setDireccion("Dir");
        datos.setTelefono("111");
        datos.setContrasena("plain-password");
        datos.setEstado(true);

        when(clienteRepositoryPort.findByClienteId(clienteId)).thenReturn(Optional.of(existente));
        when(passwordEncoder.encode("plain-password")).thenReturn("hashed-password");
        when(clienteRepositoryPort.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = useCase.execute(clienteId, datos);

        assertNotNull(result);
        assertEquals("Nuevo", result.getNombre());
        assertEquals("hashed-password", result.getContrasena());
        verify(clienteRepositoryPort).save(existente);
        verify(eventProducerPort).publish(any());
    }

    @Test
    void debe_LanzarClienteNoEncontradoException_Cuando_NoExiste() {
        var clienteId = UUID.randomUUID();
        var datos = new Cliente();

        when(clienteRepositoryPort.findByClienteId(clienteId)).thenReturn(Optional.empty());

        assertThrows(ClienteNoEncontradoException.class,
                () -> useCase.execute(clienteId, datos));
    }

    @Test
    void debe_RetornarClienteActualizado_Cuando_FallaPublicacionEvento() {
        var clienteId = UUID.randomUUID();
        var existente = new Cliente();
        existente.setClienteId(clienteId);

        var datos = new Cliente();
        datos.setNombre("Nuevo");
        datos.setGenero("F");
        datos.setEdad(20);
        datos.setIdentificacion("321");
        datos.setDireccion("Dir");
        datos.setTelefono("111");
        datos.setContrasena("plain-password");
        datos.setEstado(true);

        when(clienteRepositoryPort.findByClienteId(clienteId)).thenReturn(Optional.of(existente));
        when(passwordEncoder.encode("plain-password")).thenReturn("hashed-password");
        when(clienteRepositoryPort.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doThrow(new RuntimeException("broker down")).when(eventProducerPort).publish(any());

        var result = useCase.execute(clienteId, datos);

        assertNotNull(result);
        assertEquals("hashed-password", result.getContrasena());
    }
}
