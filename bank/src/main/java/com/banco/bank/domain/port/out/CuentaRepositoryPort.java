package com.banco.bank.domain.port.out;

import com.banco.bank.domain.model.Cuenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface CuentaRepositoryPort {

    Optional<Cuenta> findByCuentaId(UUID cuentaId);

    Page<Cuenta> findByClienteId(UUID clienteId, Pageable pageable);

    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);

    Cuenta save(Cuenta cuenta);

    void delete(UUID cuentaId);

    boolean existsByClienteId(UUID clienteId);

    boolean existsByNumeroCuentaAndDeletedAtIsNull(String numeroCuenta);
}
