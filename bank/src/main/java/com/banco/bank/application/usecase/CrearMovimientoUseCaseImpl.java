package com.banco.bank.application.usecase;

import com.banco.bank.domain.exception.CuentaNoEncontradaException;
import com.banco.bank.domain.exception.SaldoNoDisponibleException;
import com.banco.bank.domain.model.Movimiento;
import com.banco.bank.domain.model.TipoMovimientoEnum;
import com.banco.bank.domain.port.in.CrearMovimientoUseCase;
import com.banco.bank.domain.port.out.CuentaRepositoryPort;
import com.banco.bank.domain.port.out.MovimientoRepositoryPort;
import com.fasterxml.uuid.Generators;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrearMovimientoUseCaseImpl implements CrearMovimientoUseCase {

    private final MovimientoRepositoryPort movimientoRepositoryPort;
    private final CuentaRepositoryPort cuentaRepositoryPort;

    @Override
    @Transactional
    public Movimiento execute(Movimiento movimiento) {
        TipoMovimientoEnum.fromDisplayName(movimiento.getTipoMovimiento());

        var cuenta = cuentaRepositoryPort.findByCuentaId(movimiento.getCuenta().getCuentaId())
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new CuentaNoEncontradaException("Cuenta no encontrada"));

        var nuevoSaldo = cuenta.getSaldoInicial().add(movimiento.getValor());

        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new SaldoNoDisponibleException("Saldo no disponible");
        }

        cuenta.setSaldoInicial(nuevoSaldo);
        cuenta.setUpdatedAt(Instant.now());
        cuentaRepositoryPort.save(cuenta);

        movimiento.setMovimientoId(Generators.timeBasedEpochGenerator().generate());
        movimiento.setCuenta(cuenta);
        movimiento.setFecha(Instant.now());
        movimiento.setSaldo(nuevoSaldo);
        movimiento.setCreatedAt(Instant.now());

        var saved = movimientoRepositoryPort.save(movimiento);
        log.info("Movimiento creado: movimientoId={}, cuentaId={}", saved.getMovimientoId(), cuenta.getCuentaId());
        return saved;
    }
}
