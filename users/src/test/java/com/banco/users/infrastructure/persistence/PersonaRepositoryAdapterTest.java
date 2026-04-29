package com.banco.users.infrastructure.persistence;

import com.banco.users.domain.exception.PersonaNotFoundException;
import com.banco.users.domain.model.Persona;
import com.banco.users.infrastructure.entity.PersonaEntity;
import com.banco.users.infrastructure.mapper.PersonaMapper;
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
class PersonaRepositoryAdapterTest {

    @Mock
    private PersonaJpaRepository personaJpaRepository;

    @Mock
    private PersonaMapper personaMapper;

    @InjectMocks
    private PersonaRepositoryAdapter adapter;

    @Test
    void debe_GuardarPersona_Cuando_SaveEsInvocado() {
        var persona = new Persona();
        persona.setId(UUID.randomUUID());
        persona.setNombre("Ana");

        var entity = new PersonaEntity();
        entity.setId(persona.getId());

        var savedEntity = new PersonaEntity();
        savedEntity.setId(persona.getId());

        var savedDomain = new Persona();
        savedDomain.setId(persona.getId());
        savedDomain.setNombre("Ana");

        when(personaMapper.toEntity(persona)).thenReturn(entity);
        when(personaJpaRepository.save(entity)).thenReturn(savedEntity);
        when(personaMapper.toDomain(savedEntity)).thenReturn(savedDomain);

        var result = adapter.save(persona);

        assertNotNull(result);
        assertEquals(persona.getId(), result.getId());
        assertEquals("Ana", result.getNombre());
    }

    @Test
    void debe_RetornarPersona_Cuando_FindByIdentificationEncuentraRegistro() {
        var entity = new PersonaEntity();
        entity.setId(UUID.randomUUID());
        entity.setIdentificacion("123");
        var domain = new Persona();
        domain.setIdentificacion("123");

        when(personaJpaRepository.findByIdentificacionAndDeletedAtIsNull("123")).thenReturn(Optional.of(entity));
        when(personaMapper.toDomain(entity)).thenReturn(domain);

        var result = adapter.findByIdentification("123");

        assertEquals(true, result.isPresent());
        assertEquals("123", result.orElseThrow().getIdentificacion());
    }

    @Test
    void debe_RetornarPersona_Cuando_FindByIdEncuentraRegistro() {
        var id = UUID.randomUUID();
        var entity = new PersonaEntity();
        entity.setId(id);
        var domain = new Persona();
        domain.setId(id);

        when(personaJpaRepository.findByIdAndDeletedAtIsNull(id)).thenReturn(Optional.of(entity));
        when(personaMapper.toDomain(entity)).thenReturn(domain);

        var result = adapter.findById(id);

        assertEquals(true, result.isPresent());
        assertEquals(id, result.orElseThrow().getId());
    }

    @Test
    void debe_RealizarSoftDelete_Cuando_PersonaExiste() {
        var id = UUID.randomUUID();
        var entity = new PersonaEntity();
        entity.setId(id);

        when(personaJpaRepository.findByIdAndDeletedAtIsNull(id)).thenReturn(Optional.of(entity));

        adapter.softDelete(id);

        assertNotNull(entity.getDeletedAt());
        verify(personaJpaRepository).save(entity);
    }

    @Test
    void debe_LanzarPersonaNotFoundException_Cuando_SoftDeleteSinPersona() {
        var id = UUID.randomUUID();
        when(personaJpaRepository.findByIdAndDeletedAtIsNull(id)).thenReturn(Optional.empty());

        assertThrows(PersonaNotFoundException.class, () -> adapter.softDelete(id));
    }
}
