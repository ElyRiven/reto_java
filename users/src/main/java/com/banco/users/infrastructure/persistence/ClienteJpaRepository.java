package com.banco.users.infrastructure.persistence;

import com.banco.users.infrastructure.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, UUID> {

    Optional<ClienteEntity> findByClienteIdAndDeletedAtIsNull(UUID clienteId);

    boolean existsByPersona_Id(UUID personaId);
}
