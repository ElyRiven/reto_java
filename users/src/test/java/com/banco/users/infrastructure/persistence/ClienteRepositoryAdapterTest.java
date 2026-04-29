package com.banco.users.infrastructure.persistence;

import com.banco.users.domain.exception.ClienteNoEncontradoException;
import com.banco.users.domain.model.Cliente;
import com.banco.users.infrastructure.entity.ClienteEntity;
import com.banco.users.infrastructure.entity.PersonaEntity;
import com.banco.users.infrastructure.mapper.ClienteMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteRepositoryAdapterTest {

    @Mock
    private ClienteJpaRepository clienteJpaRepository;

    @Mock
    private PersonaJpaRepository personaJpaRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @InjectMocks
    private ClienteRepositoryAdapter adapter;

    @Test
    void debe_GuardarClienteYActualizarPersona_Cuando_SaveEsInvocado() {
        var personaId = UUID.randomUUID();
        var clienteId = UUID.randomUUID();

        var cliente = new Cliente();
        cliente.setId(personaId);
        cliente.setNombre("Ana");
        cliente.setGenero("F");
        cliente.setEdad(30);
        cliente.setIdentificacion("123");
        cliente.setDireccion("Dir");
        cliente.setTelefono("300");
        cliente.setClienteId(clienteId);
        cliente.setContrasena("hash");
        cliente.setEstado(true);

        var personaEntity = new PersonaEntity();
        personaEntity.setId(personaId);

        var clienteEntity = new ClienteEntity();
        clienteEntity.setClienteId(clienteId);
        clienteEntity.setPersona(personaEntity);

        var savedEntity = new ClienteEntity();
        savedEntity.setClienteId(clienteId);
        savedEntity.setPersona(personaEntity);

        var savedDomain = new Cliente();
        savedDomain.setClienteId(clienteId);
        savedDomain.setNombre("Ana");

        when(personaJpaRepository.getReferenceById(personaId)).thenReturn(personaEntity);
        when(clienteMapper.toEntity(cliente, personaEntity)).thenReturn(clienteEntity);
        when(clienteJpaRepository.save(clienteEntity)).thenReturn(savedEntity);
        when(clienteMapper.toDomain(savedEntity)).thenReturn(savedDomain);

        var result = adapter.save(cliente);

        assertNotNull(result);
        assertEquals(clienteId, result.getClienteId());
        assertEquals("Ana", result.getNombre());
        assertEquals("Ana", personaEntity.getNombre());
        verify(personaJpaRepository).save(personaEntity);
        verify(clienteJpaRepository).save(clienteEntity);
    }

    @Test
    void debe_RetornarCliente_Cuando_FindByClienteIdEncuentraRegistro() {
        var clienteId = UUID.randomUUID();
        var entity = new ClienteEntity();
        entity.setClienteId(clienteId);

        var domain = new Cliente();
        domain.setClienteId(clienteId);

        when(clienteJpaRepository.findByClienteIdAndDeletedAtIsNull(clienteId)).thenReturn(Optional.of(entity));
        when(clienteMapper.toDomain(entity)).thenReturn(domain);

        var result = adapter.findByClienteId(clienteId);

        assertEquals(true, result.isPresent());
        assertEquals(clienteId, result.orElseThrow().getClienteId());
    }

    @Test
    void debe_DelegarExistsByPersonaId_Cuando_SeConsultaExistencia() {
        var personaId = UUID.randomUUID();
        when(clienteJpaRepository.existsByPersona_Id(personaId)).thenReturn(true);

        var result = adapter.existsByPersonaId(personaId);

        assertEquals(true, result);
    }

    @Test
    void debe_RealizarSoftDelete_Cuando_ClienteExiste() {
        var clienteId = UUID.randomUUID();
        var entity = new ClienteEntity();
        entity.setClienteId(clienteId);
        entity.setEstado(true);

        when(clienteJpaRepository.findByClienteIdAndDeletedAtIsNull(clienteId)).thenReturn(Optional.of(entity));

        adapter.softDelete(clienteId);

        assertFalse(entity.getEstado());
        assertNotNull(entity.getDeletedAt());
        verify(clienteJpaRepository).save(entity);
    }

    @Test
    void debe_LanzarClienteNoEncontradoException_Cuando_SoftDeleteSinCliente() {
        var clienteId = UUID.randomUUID();
        when(clienteJpaRepository.findByClienteIdAndDeletedAtIsNull(clienteId)).thenReturn(Optional.empty());

        assertThrows(ClienteNoEncontradoException.class,
                () -> adapter.softDelete(clienteId));
    }
}
