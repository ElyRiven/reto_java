package com.banco.bank.infrastructure.persistence;

import com.banco.bank.domain.model.Cuenta;
import com.banco.bank.domain.port.out.CuentaRepositoryPort;
import com.banco.bank.infrastructure.mapper.CuentaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CuentaRepositoryAdapter implements CuentaRepositoryPort {

    private final CuentaJpaRepository cuentaJpaRepository;
    private final CuentaMapper cuentaMapper;

    @Override
    public Optional<Cuenta> findByCuentaId(UUID cuentaId) {
        return cuentaJpaRepository.findById(cuentaId)
                .map(cuentaMapper::toDomain);
    }

    @Override
    public Page<Cuenta> findByClienteId(UUID clienteId, Pageable pageable) {
        return cuentaJpaRepository.findByClienteIdAndDeletedAtIsNull(clienteId, pageable)
                .map(cuentaMapper::toDomain);
    }

    @Override
    public Optional<Cuenta> findByNumeroCuenta(String numeroCuenta) {
        return cuentaJpaRepository.findByNumeroCuenta(numeroCuenta)
                .map(cuentaMapper::toDomain);
    }

    @Override
    public Cuenta save(Cuenta cuenta) {
        var entity = cuentaMapper.toEntity(cuenta);
        var saved = cuentaJpaRepository.save(entity);
        return cuentaMapper.toDomain(saved);
    }

    @Override
    public void delete(UUID cuentaId) {
        cuentaJpaRepository.findById(cuentaId).ifPresent(entity -> {
            entity.setDeletedAt(Instant.now());
            entity.setEstado(false);
            cuentaJpaRepository.save(entity);
        });
    }

    @Override
    public boolean existsByClienteId(UUID clienteId) {
        return cuentaJpaRepository.existsByClienteId(clienteId);
    }

    @Override
    public boolean existsByNumeroCuentaAndDeletedAtIsNull(String numeroCuenta) {
        return cuentaJpaRepository.existsByNumeroCuentaAndDeletedAtIsNull(numeroCuenta);
    }
}
