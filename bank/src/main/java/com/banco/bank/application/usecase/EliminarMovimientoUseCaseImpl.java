package com.banco.bank.application.usecase;

import com.banco.bank.domain.exception.MovimientoNoEncontradoException;
import com.banco.bank.domain.port.in.EliminarMovimientoUseCase;
import com.banco.bank.domain.port.out.CuentaRepositoryPort;
import com.banco.bank.domain.port.out.MovimientoRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EliminarMovimientoUseCaseImpl implements EliminarMovimientoUseCase {

    private final MovimientoRepositoryPort movimientoRepositoryPort;
    private final CuentaRepositoryPort cuentaRepositoryPort;

    @Override
    @Transactional
    public void execute(UUID movimientoId) {
        var movimiento = movimientoRepositoryPort.findById(movimientoId)
                .filter(m -> m.getDeletedAt() == null)
                .orElseThrow(() -> new MovimientoNoEncontradoException("Movimiento no encontrado"));

        var cuenta = cuentaRepositoryPort.findByCuentaId(movimiento.getCuenta().getCuentaId())
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new MovimientoNoEncontradoException("Movimiento no encontrado"));

        // Revertir el movimiento del saldo de la cuenta
        var saldoRevertido = cuenta.getSaldoInicial().subtract(movimiento.getValor());
        cuenta.setSaldoInicial(saldoRevertido);
        cuenta.setUpdatedAt(Instant.now());
        cuentaRepositoryPort.save(cuenta);

        movimientoRepositoryPort.softDelete(movimientoId);
        log.info("Movimiento eliminado (soft-delete): movimientoId={}", movimientoId);
    }
}
