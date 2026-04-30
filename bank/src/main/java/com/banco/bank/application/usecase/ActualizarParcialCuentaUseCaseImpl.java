package com.banco.bank.application.usecase;

import com.banco.bank.domain.exception.CuentaNoEncontradaException;
import com.banco.bank.domain.exception.TipoCuentaInvalidoException;
import com.banco.bank.domain.model.Cuenta;
import com.banco.bank.domain.model.TipoCuentaEnum;
import com.banco.bank.domain.port.in.ActualizarParcialCuentaUseCase;
import com.banco.bank.domain.port.out.CuentaRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActualizarParcialCuentaUseCaseImpl implements ActualizarParcialCuentaUseCase {

    private final CuentaRepositoryPort cuentaRepositoryPort;

    @Override
    public Cuenta execute(UUID cuentaId, Cuenta patchData) {
        if (patchData.getTipoCuenta() == null
                && patchData.getSaldoInicial() == null
                && patchData.getEstado() == null) {
            throw new IllegalArgumentException("Al menos un campo debe ser proporcionado");
        }

        var existing = cuentaRepositoryPort.findByCuentaId(cuentaId)
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new CuentaNoEncontradaException("Cuenta no encontrada"));

        if (patchData.getTipoCuenta() != null) {
            try {
                TipoCuentaEnum.fromDisplayName(patchData.getTipoCuenta());
            } catch (IllegalArgumentException e) {
                throw new TipoCuentaInvalidoException("Tipo de cuenta debe ser 'Ahorros' o 'Corriente'");
            }
            existing.setTipoCuenta(patchData.getTipoCuenta());
        }

        if (patchData.getSaldoInicial() != null) {
            existing.setSaldoInicial(patchData.getSaldoInicial());
        }

        if (patchData.getEstado() != null) {
            existing.setEstado(patchData.getEstado());
        }

        existing.setUpdatedAt(Instant.now());
        var saved = cuentaRepositoryPort.save(existing);
        log.info("Cuenta actualizada (PATCH): cuentaId={}", saved.getCuentaId());
        return saved;
    }
}
