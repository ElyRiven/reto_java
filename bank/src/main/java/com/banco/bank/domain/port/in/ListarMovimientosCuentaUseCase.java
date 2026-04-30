package com.banco.bank.domain.port.in;

import com.banco.bank.domain.model.Movimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.UUID;

public interface ListarMovimientosCuentaUseCase {

    Page<Movimiento> execute(UUID cuentaId, Pageable pageable, Instant startDate, Instant endDate, String tipoMovimiento);
}
