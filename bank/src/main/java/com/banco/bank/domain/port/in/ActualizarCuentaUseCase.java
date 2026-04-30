package com.banco.bank.domain.port.in;

import com.banco.bank.domain.model.Cuenta;

import java.util.UUID;

public interface ActualizarCuentaUseCase {

    Cuenta execute(UUID cuentaId, Cuenta updates);
}
