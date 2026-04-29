package com.banco.bank.domain.port.out;

import com.banco.bank.domain.model.Movimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface MovimientoRepositoryPort {

    Movimiento save(Movimiento movimiento);

    Optional<Movimiento> findById(UUID movimientoId);

    Page<Movimiento> findByCuentaId(UUID cuentaId, Pageable pageable, Instant startDate, Instant endDate, String tipoMovimiento);

    boolean existsByCuentaId(UUID cuentaId);

    void softDelete(UUID movimientoId);
}
