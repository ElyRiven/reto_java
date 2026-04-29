package com.banco.users.infrastructure.mapper;

import com.banco.users.domain.model.Cliente;
import com.banco.users.infrastructure.entity.ClienteEntity;
import com.banco.users.infrastructure.entity.PersonaEntity;
import com.banco.users.infrastructure.web.dto.ClienteResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    public ClienteEntity toEntity(Cliente cliente, PersonaEntity personaEntity) {
        var entity = new ClienteEntity();
        entity.setClienteId(cliente.getClienteId());
        entity.setPersona(personaEntity);
        entity.setContrasena(cliente.getContrasena());
        entity.setEstado(cliente.getEstado());
        entity.setCreatedAt(cliente.getCreatedAt());
        entity.setUpdatedAt(cliente.getUpdatedAt());
        entity.setDeletedAt(cliente.getDeletedAt());
        return entity;
    }

    public Cliente toDomain(ClienteEntity entity) {
        var cliente = new Cliente();
        var persona = entity.getPersona();
        cliente.setId(persona.getId());
        cliente.setNombre(persona.getNombre());
        cliente.setGenero(persona.getGenero());
        cliente.setEdad(persona.getEdad());
        cliente.setIdentificacion(persona.getIdentificacion());
        cliente.setDireccion(persona.getDireccion());
        cliente.setTelefono(persona.getTelefono());
        cliente.setCreatedAt(entity.getCreatedAt());
        cliente.setUpdatedAt(entity.getUpdatedAt());
        cliente.setClienteId(entity.getClienteId());
        cliente.setContrasena(entity.getContrasena());
        cliente.setEstado(entity.getEstado());
        cliente.setDeletedAt(entity.getDeletedAt());
        return cliente;
    }

    public ClienteResponseDTO toResponseDTO(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getClienteId(),
                cliente.getNombre(),
                cliente.getGenero(),
                cliente.getEdad(),
                cliente.getIdentificacion(),
                cliente.getDireccion(),
                cliente.getTelefono(),
                cliente.getEstado(),
                cliente.getCreatedAt(),
                cliente.getUpdatedAt(),
                cliente.getDeletedAt()
        );
    }
}
