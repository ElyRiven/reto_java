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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EliminarPersonaUseCaseImplTest {

    @Mock
    private PersonaRepositoryPort personaRepositoryPort;

    @InjectMocks
    private EliminarPersonaUseCaseImpl useCase;

    @Test
    void debe_RealizarSoftDelete_Cuando_PersonaExiste() {
        var id = UUID.randomUUID();
        when(personaRepositoryPort.findById(id)).thenReturn(Optional.of(new Persona()));

        assertDoesNotThrow(() -> useCase.execute(id));

        verify(personaRepositoryPort).softDelete(id);
    }

    @Test
    void debe_LanzarPersonaNotFoundException_Cuando_PersonaNoExiste() {
        var id = UUID.randomUUID();
        when(personaRepositoryPort.findById(id)).thenReturn(Optional.empty());

        assertThrows(PersonaNotFoundException.class, () -> useCase.execute(id));
    }
}
