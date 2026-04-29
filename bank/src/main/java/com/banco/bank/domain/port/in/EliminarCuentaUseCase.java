package com.banco.bank.domain.port.in;

import java.util.UUID;

public interface EliminarCuentaUseCase {

    void execute(UUID cuentaId);
}
