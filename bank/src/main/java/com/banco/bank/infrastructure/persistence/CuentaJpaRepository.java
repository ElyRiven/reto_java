package com.banco.bank.infrastructure.persistence;

import com.banco.bank.infrastructure.entity.CuentaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CuentaJpaRepository extends JpaRepository<CuentaEntity, UUID> {

    Page<CuentaEntity> findByClienteIdAndDeletedAtIsNull(UUID clienteId, Pageable pageable);

    Optional<CuentaEntity> findByNumeroCuenta(String numeroCuenta);

    boolean existsByClienteId(UUID clienteId);

    boolean existsByNumeroCuentaAndDeletedAtIsNull(String numeroCuenta);
}
