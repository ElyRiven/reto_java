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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarParcialPersonaUseCaseImplTest {

    @Mock
    private PersonaRepositoryPort personaRepositoryPort;

    @InjectMocks
    private ActualizarParcialPersonaUseCaseImpl useCase;

    @Test
    void debe_ActualizarSoloCamposEnviados_Cuando_PatchEsValido() {
        var id = UUID.randomUUID();
        var existente = new Persona();
        existente.setId(id);
        existente.setNombre("Anterior");
        existente.setDireccion("Dir anterior");

        var command = new PatchPersonaCommand("Nuevo", null, null, null, "Dir nueva", null);

        when(personaRepositoryPort.findById(id)).thenReturn(Optional.of(existente));
        when(personaRepositoryPort.save(existente)).thenReturn(existente);

        var result = useCase.execute(id, command);

        assertEquals("Nuevo", result.getNombre());
        assertEquals("Dir nueva", result.getDireccion());
        assertNotNull(result.getUpdatedAt());
        verify(personaRepositoryPort).save(existente);
    }

    @Test
    void debe_LanzarPersonaNotFoundException_Cuando_NoExiste() {
        var id = UUID.randomUUID();
        when(personaRepositoryPort.findById(id)).thenReturn(Optional.empty());

        assertThrows(PersonaNotFoundException.class,
                () -> useCase.execute(id, new PatchPersonaCommand(null, null, null, null, null, null)));
    }
}
