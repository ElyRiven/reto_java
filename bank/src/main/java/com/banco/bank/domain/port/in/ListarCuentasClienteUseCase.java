package com.banco.bank.domain.port.in;

import com.banco.bank.domain.model.Cuenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ListarCuentasClienteUseCase {

    Page<Cuenta> execute(UUID clienteId, Pageable pageable);
}
