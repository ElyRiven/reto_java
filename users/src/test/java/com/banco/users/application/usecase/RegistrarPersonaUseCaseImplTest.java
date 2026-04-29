package com.banco.users.application.usecase;

import com.banco.users.domain.exception.PersonaYaExisteException;
import com.banco.users.domain.model.Persona;
import com.banco.users.domain.port.out.PersonaRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarPersonaUseCaseImplTest {

    @Mock
    private PersonaRepositoryPort personaRepositoryPort;

    @InjectMocks
    private RegistrarPersonaUseCaseImpl useCase;

    @Test
    void debe_RegistrarPersona_Cuando_IdentificacionNoExiste() {
        var persona = new Persona();
        persona.setNombre("Ana");
        persona.setGenero("F");
        persona.setEdad(30);
        persona.setIdentificacion("123456");
        persona.setDireccion("Dir");
        persona.setTelefono("300");

        var saved = new Persona();
        saved.setId(UUID.randomUUID());
        saved.setNombre("Ana");
        saved.setIdentificacion("123456");
        saved.setCreatedAt(persona.getCreatedAt());

        when(personaRepositoryPort.findByIdentification("123456")).thenReturn(Optional.empty());
        when(personaRepositoryPort.save(any(Persona.class))).thenAnswer(invocation -> {
            var p = invocation.getArgument(0, Persona.class);
            saved.setId(p.getId());
            saved.setCreatedAt(p.getCreatedAt());
            return saved;
        });

        var result = useCase.execute(persona);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getCreatedAt());
        assertEquals("123456", result.getIdentificacion());
        verify(personaRepositoryPort).findByIdentification("123456");
        verify(personaRepositoryPort).save(any(Persona.class));
    }

    @Test
    void debe_LanzarPersonaYaExisteException_Cuando_IdentificacionYaExiste() {
        var persona = new Persona();
        persona.setIdentificacion("123456");

        when(personaRepositoryPort.findByIdentification("123456")).thenReturn(Optional.of(new Persona()));

        assertThrows(PersonaYaExisteException.class, () -> useCase.execute(persona));

        verify(personaRepositoryPort, never()).save(any());
    }
}
