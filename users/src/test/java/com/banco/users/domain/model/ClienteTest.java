package com.banco.users.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClienteTest {

    @Test
    void debe_HeredarDePersona() {
        var cliente = new Cliente();

        assertInstanceOf(Persona.class, cliente);
    }

    @Test
    void debe_AlmacenarCamposPropiosYHeredados() {
        var cliente = new Cliente();
        var id = UUID.randomUUID();
        var clienteId = UUID.randomUUID();
        var createdAt = Instant.now();
        var updatedAt = createdAt.plusSeconds(10);
        var deletedAt = createdAt.plusSeconds(20);

        cliente.setId(id);
        cliente.setNombre("Ana");
        cliente.setGenero("F");
        cliente.setEdad(22);
        cliente.setIdentificacion("123");
        cliente.setDireccion("Dir");
        cliente.setTelefono("300");
        cliente.setCreatedAt(createdAt);
        cliente.setUpdatedAt(updatedAt);
        cliente.setDeletedAt(deletedAt);
        cliente.setClienteId(clienteId);
        cliente.setContrasena("hash");
        cliente.setEstado(true);

        assertEquals(id, cliente.getId());
        assertEquals("Ana", cliente.getNombre());
        assertEquals(clienteId, cliente.getClienteId());
        assertEquals("hash", cliente.getContrasena());
        assertEquals(true, cliente.getEstado());
        assertEquals(createdAt, cliente.getCreatedAt());
        assertEquals(updatedAt, cliente.getUpdatedAt());
        assertEquals(deletedAt, cliente.getDeletedAt());
    }

    @Test
    void debe_InicializarCamposNulos_PorDefecto() {
        var cliente = new Cliente();

        assertNull(cliente.getClienteId());
        assertNull(cliente.getContrasena());
        assertNull(cliente.getEstado());
        assertNull(cliente.getDeletedAt());
    }
}
