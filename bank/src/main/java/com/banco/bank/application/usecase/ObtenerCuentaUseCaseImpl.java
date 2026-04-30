package com.banco.bank.application.usecase;

import com.banco.bank.domain.exception.CuentaNoEncontradaException;
import com.banco.bank.domain.model.Cuenta;
import com.banco.bank.domain.port.in.ObtenerCuentaUseCase;
import com.banco.bank.domain.port.out.CuentaRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ObtenerCuentaUseCaseImpl implements ObtenerCuentaUseCase {

    private final CuentaRepositoryPort cuentaRepositoryPort;

    @Override
    public Cuenta execute(UUID cuentaId) {
        return cuentaRepositoryPort.findByCuentaId(cuentaId)
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new CuentaNoEncontradaException("Cuenta no encontrada"));
    }
}
