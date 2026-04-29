package com.banco.bank.domain.port.in;

import com.banco.bank.domain.model.Movimiento;

public interface CrearMovimientoUseCase {

    Movimiento execute(Movimiento movimiento);
}
