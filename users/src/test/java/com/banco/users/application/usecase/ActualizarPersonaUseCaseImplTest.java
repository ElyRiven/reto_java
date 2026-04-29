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
class ActualizarPersonaUseCaseImplTest {

    @Mock
    private PersonaRepositoryPort personaRepositoryPort;

    @InjectMocks
    private ActualizarPersonaUseCaseImpl useCase;

    @Test
    void debe_ActualizarPersona_Cuando_Existe() {
        var id = UUID.randomUUID();
        var existente = new Persona();
        existente.setId(id);
        existente.setNombre("Anterior");

        var datos = new Persona();
        datos.setNombre("Nuevo");
        datos.setGenero("F");
        datos.setEdad(28);
        datos.setIdentificacion("999");
        datos.setDireccion("Dir nueva");
        datos.setTelefono("777");

        when(personaRepositoryPort.findById(id)).thenReturn(Optional.of(existente));
        when(personaRepositoryPort.save(existente)).thenReturn(existente);

        var result = useCase.execute(id, datos);

        assertNotNull(result);
        assertEquals("Nuevo", result.getNombre());
        assertEquals("999", result.getIdentificacion());
        assertNotNull(result.getUpdatedAt());
        verify(personaRepositoryPort).save(existente);
    }

    @Test
    void debe_LanzarPersonaNotFoundException_Cuando_NoExiste() {
        var id = UUID.randomUUID();
        when(personaRepositoryPort.findById(id)).thenReturn(Optional.empty());

        assertThrows(PersonaNotFoundException.class, () -> useCase.execute(id, new Persona()));
    }
}
