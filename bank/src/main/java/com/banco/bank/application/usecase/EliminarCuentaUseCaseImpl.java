package com.banco.bank.application.usecase;

import com.banco.bank.domain.exception.CuentaNoEncontradaException;
import com.banco.bank.domain.port.in.EliminarCuentaUseCase;
import com.banco.bank.domain.port.out.CuentaRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EliminarCuentaUseCaseImpl implements EliminarCuentaUseCase {

    private final CuentaRepositoryPort cuentaRepositoryPort;

    @Override
    public void execute(UUID cuentaId) {
        cuentaRepositoryPort.findByCuentaId(cuentaId)
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new CuentaNoEncontradaException("Cuenta no encontrada"));

        cuentaRepositoryPort.delete(cuentaId);
        log.info("Cuenta eliminada (soft-delete): cuentaId={}", cuentaId);
    }
}
