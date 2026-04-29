package com.banco.bank.application.usecase;

import com.banco.bank.domain.exception.CuentaNoEncontradaException;
import com.banco.bank.domain.exception.TipoCuentaInvalidoException;
import com.banco.bank.domain.model.Cuenta;
import com.banco.bank.domain.model.TipoCuentaEnum;
import com.banco.bank.domain.port.in.ActualizarCuentaUseCase;
import com.banco.bank.domain.port.out.CuentaRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActualizarCuentaUseCaseImpl implements ActualizarCuentaUseCase {

    private final CuentaRepositoryPort cuentaRepositoryPort;

    @Override
    public Cuenta execute(UUID cuentaId, Cuenta updates) {
        var existing = cuentaRepositoryPort.findByCuentaId(cuentaId)
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new CuentaNoEncontradaException("Cuenta no encontrada"));

        if (updates.getTipoCuenta() != null) {
            try {
                TipoCuentaEnum.fromDisplayName(updates.getTipoCuenta());
            } catch (IllegalArgumentException e) {
                throw new TipoCuentaInvalidoException("Tipo de cuenta debe ser 'Ahorros' o 'Corriente'");
            }
            existing.setTipoCuenta(updates.getTipoCuenta());
        }

        if (updates.getSaldoInicial() != null) {
            existing.setSaldoInicial(updates.getSaldoInicial());
        }

        if (updates.getEstado() != null) {
            existing.setEstado(updates.getEstado());
        }

        existing.setUpdatedAt(Instant.now());
        var saved = cuentaRepositoryPort.save(existing);
        log.info("Cuenta actualizada (PUT): cuentaId={}", saved.getCuentaId());
        return saved;
    }
}
