package com.banco.bank.application.usecase;

import com.banco.bank.domain.exception.MovimientoNoEncontradoException;
import com.banco.bank.domain.exception.SaldoNoDisponibleException;
import com.banco.bank.domain.model.Movimiento;
import com.banco.bank.domain.model.TipoMovimientoEnum;
import com.banco.bank.domain.port.in.ActualizarParcialMovimientoUseCase;
import com.banco.bank.domain.port.out.CuentaRepositoryPort;
import com.banco.bank.domain.port.out.MovimientoRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActualizarParcialMovimientoUseCaseImpl implements ActualizarParcialMovimientoUseCase {

    private final MovimientoRepositoryPort movimientoRepositoryPort;
    private final CuentaRepositoryPort cuentaRepositoryPort;

    @Override
    @Transactional
    public Movimiento execute(UUID movimientoId, Movimiento patches) {
        var existing = movimientoRepositoryPort.findById(movimientoId)
                .filter(m -> m.getDeletedAt() == null)
                .orElseThrow(() -> new MovimientoNoEncontradoException("Movimiento no encontrado"));

        if (patches.getTipoMovimiento() != null) {
            TipoMovimientoEnum.fromDisplayName(patches.getTipoMovimiento());
            existing.setTipoMovimiento(patches.getTipoMovimiento());
        }

        if (patches.getValor() != null) {
            var cuenta = cuentaRepositoryPort.findByCuentaId(existing.getCuenta().getCuentaId())
                    .filter(c -> c.getDeletedAt() == null)
                    .orElseThrow(() -> new MovimientoNoEncontradoException("Movimiento no encontrado"));

            var saldoRevertido = cuenta.getSaldoInicial().subtract(existing.getValor());
            var nuevoSaldo = saldoRevertido.add(patches.getValor());

            if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
                throw new SaldoNoDisponibleException("Saldo no disponible");
            }

            cuenta.setSaldoInicial(nuevoSaldo);
            cuenta.setUpdatedAt(Instant.now());
            cuentaRepositoryPort.save(cuenta);

            existing.setValor(patches.getValor());
            existing.setSaldo(nuevoSaldo);
        }

        existing.setUpdatedAt(Instant.now());
        var saved = movimientoRepositoryPort.save(existing);
        log.info("Movimiento actualizado (PATCH): movimientoId={}", saved.getMovimientoId());
        return saved;
    }
}
