package com.banco.bank.application.usecase;

import com.banco.bank.domain.exception.MovimientoNoEncontradoException;
import com.banco.bank.domain.exception.SaldoNoDisponibleException;
import com.banco.bank.domain.model.Movimiento;
import com.banco.bank.domain.model.TipoMovimientoEnum;
import com.banco.bank.domain.port.in.ActualizarMovimientoUseCase;
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
public class ActualizarMovimientoUseCaseImpl implements ActualizarMovimientoUseCase {

    private final MovimientoRepositoryPort movimientoRepositoryPort;
    private final CuentaRepositoryPort cuentaRepositoryPort;

    @Override
    @Transactional
    public Movimiento execute(UUID movimientoId, Movimiento updates) {
        var existing = movimientoRepositoryPort.findById(movimientoId)
                .filter(m -> m.getDeletedAt() == null)
                .orElseThrow(() -> new MovimientoNoEncontradoException("Movimiento no encontrado"));

        if (updates.getTipoMovimiento() != null) {
            TipoMovimientoEnum.fromDisplayName(updates.getTipoMovimiento());
        }

        var cuenta = cuentaRepositoryPort.findByCuentaId(existing.getCuenta().getCuentaId())
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new MovimientoNoEncontradoException("Movimiento no encontrado"));

        // Revertir el movimiento anterior del saldo de la cuenta
        var saldoRevertido = cuenta.getSaldoInicial().subtract(existing.getValor());

        var nuevoValor = updates.getValor() != null ? updates.getValor() : existing.getValor();
        var nuevoSaldo = saldoRevertido.add(nuevoValor);

        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new SaldoNoDisponibleException("Saldo no disponible");
        }

        cuenta.setSaldoInicial(nuevoSaldo);
        cuenta.setUpdatedAt(Instant.now());
        cuentaRepositoryPort.save(cuenta);

        if (updates.getTipoMovimiento() != null) {
            existing.setTipoMovimiento(updates.getTipoMovimiento());
        }
        if (updates.getValor() != null) {
            existing.setValor(updates.getValor());
        }
        existing.setSaldo(nuevoSaldo);
        existing.setUpdatedAt(Instant.now());

        var saved = movimientoRepositoryPort.save(existing);
        log.info("Movimiento actualizado (PUT): movimientoId={}", saved.getMovimientoId());
        return saved;
    }
}
