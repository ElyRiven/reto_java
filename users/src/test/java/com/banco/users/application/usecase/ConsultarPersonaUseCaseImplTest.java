package com.banco.users.application.usecase;

import com.banco.users.domain.exception.PersonaNotFoundException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarPersonaUseCaseImplTest {

    @Mock
    private PersonaRepositoryPort personaRepositoryPort;

    @InjectMocks
    private ConsultarPersonaUseCaseImpl useCase;

    @Test
    void debe_RetornarPersona_Cuando_Existe() {
        var id = UUID.randomUUID();
        var persona = new Persona();
        persona.setId(id);
        persona.setNombre("Ana");

        when(personaRepositoryPort.findById(id)).thenReturn(Optional.of(persona));

        var result = useCase.execute(id);

        assertEquals(id, result.getId());
        assertEquals("Ana", result.getNombre());
    }

    @Test
    void debe_LanzarPersonaNotFoundException_Cuando_NoExiste() {
        var id = UUID.randomUUID();
        when(personaRepositoryPort.findById(id)).thenReturn(Optional.empty());

        assertThrows(PersonaNotFoundException.class, () -> useCase.execute(id));
    }
}
