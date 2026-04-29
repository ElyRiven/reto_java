package com.banco.bank.infrastructure.persistence;

import com.banco.bank.infrastructure.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, UUID> {

    Optional<ClienteEntity> findByClienteId(UUID clienteId);
}
