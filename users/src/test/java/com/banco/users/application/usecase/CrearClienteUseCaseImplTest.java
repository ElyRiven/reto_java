package com.banco.users.application.usecase;

import com.banco.users.domain.exception.ClienteYaExisteException;
import com.banco.users.domain.exception.PersonaNotFoundException;
import com.banco.users.domain.model.Cliente;
import com.banco.users.domain.model.Persona;
import com.banco.users.domain.port.out.ClienteEventProducerPort;
import com.banco.users.domain.port.out.ClienteRepositoryPort;
import com.banco.users.domain.port.out.PersonaRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
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
class CrearClienteUseCaseImplTest {

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @Mock
    private PersonaRepositoryPort personaRepositoryPort;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private ClienteEventProducerPort eventProducerPort;

    @InjectMocks
    private CrearClienteUseCaseImpl useCase;

    @Test
    void debe_CrearCliente_Cuando_PersonaExisteYNoEstaRegistrada() {
        var personaId = UUID.randomUUID();
        var persona = new Persona();
        persona.setId(personaId);
        persona.setNombre("Ana");
        persona.setGenero("F");
        persona.setEdad(30);
        persona.setIdentificacion("123");
        persona.setDireccion("Calle 1");
        persona.setTelefono("3000000");
        persona.setCreatedAt(Instant.now());

        var saved = new Cliente();
        saved.setId(personaId);
        saved.setNombre("Ana");
        saved.setClienteId(UUID.randomUUID());
        saved.setContrasena("hashed-password");
        saved.setEstado(true);
        saved.setCreatedAt(Instant.now());

        when(personaRepositoryPort.findById(personaId)).thenReturn(Optional.of(persona));
        when(clienteRepositoryPort.existsByPersonaId(personaId)).thenReturn(false);
        when(passwordEncoder.encode("Password123")).thenReturn("hashed-password");
        when(clienteRepositoryPort.save(any(Cliente.class))).thenReturn(saved);

        var result = useCase.execute(personaId, "Password123", true);

        assertNotNull(result);
        assertEquals(saved.getClienteId(), result.getClienteId());
        assertEquals("Ana", result.getNombre());
        verify(personaRepositoryPort).findById(personaId);
        verify(clienteRepositoryPort).existsByPersonaId(personaId);
        verify(passwordEncoder).encode("Password123");
        verify(clienteRepositoryPort).save(any(Cliente.class));
        verify(eventProducerPort).publish(any());
    }

    @Test
    void debe_LanzarPersonaNotFoundException_Cuando_PersonaNoExiste() {
        var personaId = UUID.randomUUID();
        when(personaRepositoryPort.findById(personaId)).thenReturn(Optional.empty());

        assertThrows(PersonaNotFoundException.class,
                () -> useCase.execute(personaId, "Password123", true));

        verify(clienteRepositoryPort, never()).existsByPersonaId(any());
        verify(clienteRepositoryPort, never()).save(any());
        verify(eventProducerPort, never()).publish(any());
    }

    @Test
    void debe_LanzarClienteYaExisteException_Cuando_PersonaYaEsCliente() {
        var personaId = UUID.randomUUID();
        var persona = new Persona();
        persona.setId(personaId);

        when(personaRepositoryPort.findById(personaId)).thenReturn(Optional.of(persona));
        when(clienteRepositoryPort.existsByPersonaId(personaId)).thenReturn(true);

        assertThrows(ClienteYaExisteException.class,
                () -> useCase.execute(personaId, "Password123", true));

        verify(clienteRepositoryPort, never()).save(any());
        verify(eventProducerPort, never()).publish(any());
    }

    @Test
    void debe_RetornarCliente_Cuando_FallaPublicacionEvento() {
        var personaId = UUID.randomUUID();
        var persona = new Persona();
        persona.setId(personaId);
        persona.setNombre("Ana");
        persona.setGenero("F");
        persona.setEdad(30);
        persona.setIdentificacion("123");
        persona.setDireccion("Calle 1");
        persona.setTelefono("3000000");

        var saved = new Cliente();
        saved.setClienteId(UUID.randomUUID());
        saved.setNombre("Ana");
        saved.setEstado(true);

        when(personaRepositoryPort.findById(personaId)).thenReturn(Optional.of(persona));
        when(clienteRepositoryPort.existsByPersonaId(personaId)).thenReturn(false);
        when(passwordEncoder.encode("Password123")).thenReturn("hashed-password");
        when(clienteRepositoryPort.save(any(Cliente.class))).thenReturn(saved);
        doThrow(new RuntimeException("broker down")).when(eventProducerPort).publish(any());

        var result = useCase.execute(personaId, "Password123", true);

        assertNotNull(result);
        assertEquals(saved.getClienteId(), result.getClienteId());
        verify(clienteRepositoryPort).save(any(Cliente.class));
    }
}