package com.banco.bank.infrastructure.mapper;

import com.banco.bank.domain.model.Cliente;
import com.banco.bank.infrastructure.entity.ClienteEntity;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    public ClienteEntity toEntity(Cliente cliente) {
        var entity = new ClienteEntity();
        entity.setClienteId(cliente.getClienteId());
        entity.setNombre(cliente.getNombre());
        entity.setEstado(cliente.getEstado());
        entity.setCreatedAt(cliente.getCreatedAt());
        entity.setUpdatedAt(cliente.getUpdatedAt());
        entity.setDeletedAt(cliente.getDeletedAt());
        return entity;
    }

    public Cliente toDomain(ClienteEntity entity) {
        var cliente = new Cliente();
        cliente.setClienteId(entity.getClienteId());
        cliente.setNombre(entity.getNombre());
        cliente.setEstado(entity.getEstado());
        cliente.setCreatedAt(entity.getCreatedAt());
        cliente.setUpdatedAt(entity.getUpdatedAt());
        cliente.setDeletedAt(entity.getDeletedAt());
        return cliente;
    }
}
