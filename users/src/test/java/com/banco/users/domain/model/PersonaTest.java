package com.banco.users.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PersonaTest {

    @Test
    void debe_AlmacenarCamposDePersona() {
        var persona = new Persona();
        var id = UUID.randomUUID();
        var createdAt = Instant.now();
        var updatedAt = createdAt.plusSeconds(10);
        var deletedAt = createdAt.plusSeconds(20);

        persona.setId(id);
        persona.setNombre("Ana");
        persona.setGenero("F");
        persona.setEdad(20);
        persona.setIdentificacion("123");
        persona.setDireccion("Dir");
        persona.setTelefono("300");
        persona.setCreatedAt(createdAt);
        persona.setUpdatedAt(updatedAt);
        persona.setDeletedAt(deletedAt);

        assertEquals(id, persona.getId());
        assertEquals("Ana", persona.getNombre());
        assertEquals("F", persona.getGenero());
        assertEquals(20, persona.getEdad());
        assertEquals("123", persona.getIdentificacion());
        assertEquals("Dir", persona.getDireccion());
        assertEquals("300", persona.getTelefono());
        assertEquals(createdAt, persona.getCreatedAt());
        assertEquals(updatedAt, persona.getUpdatedAt());
        assertEquals(deletedAt, persona.getDeletedAt());
    }

    @Test
    void debe_InicializarCamposNulosPorDefecto() {
        var persona = new Persona();

        assertNull(persona.getId());
        assertNull(persona.getNombre());
        assertNull(persona.getCreatedAt());
        assertNull(persona.getUpdatedAt());
        assertNull(persona.getDeletedAt());
    }
}
