package com.banco.bank.domain.port.in;

import com.banco.bank.domain.model.Cuenta;

public interface CrearCuentaUseCase {

    Cuenta execute(Cuenta cuenta);
}
