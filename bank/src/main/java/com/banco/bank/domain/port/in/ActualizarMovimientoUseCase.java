package com.banco.bank.domain.port.in;

import com.banco.bank.domain.model.Movimiento;

import java.util.UUID;

public interface ActualizarMovimientoUseCase {

    Movimiento execute(UUID movimientoId, Movimiento updates);
}
