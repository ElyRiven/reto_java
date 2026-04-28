package com.banco.users.infrastructure.persistence;

import com.banco.users.infrastructure.entity.PersonaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PersonaJpaRepository extends JpaRepository<PersonaEntity, UUID> {

    Optional<PersonaEntity> findByIdentificacion(String identificacion);
}
