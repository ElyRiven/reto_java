package com.banco.bank.application.usecase;

import com.banco.bank.domain.exception.CuentaNoEncontradaException;
import com.banco.bank.domain.model.Movimiento;
import com.banco.bank.domain.port.in.ListarMovimientosCuentaUseCase;
import com.banco.bank.domain.port.out.CuentaRepositoryPort;
import com.banco.bank.domain.port.out.MovimientoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListarMovimientosCuentaUseCaseImpl implements ListarMovimientosCuentaUseCase {

    private final MovimientoRepositoryPort movimientoRepositoryPort;
    private final CuentaRepositoryPort cuentaRepositoryPort;

    @Override
    public Page<Movimiento> execute(UUID cuentaId, Pageable pageable, Instant startDate, Instant endDate, String tipoMovimiento) {
        cuentaRepositoryPort.findByCuentaId(cuentaId)
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new CuentaNoEncontradaException("Cuenta no encontrada"));

        return movimientoRepositoryPort.findByCuentaId(cuentaId, pageable, startDate, endDate, tipoMovimiento);
    }
}
