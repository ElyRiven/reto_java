package com.banco.bank.domain.port.in;

import java.util.UUID;

public interface EliminarMovimientoUseCase {

    void execute(UUID movimientoId);
}
