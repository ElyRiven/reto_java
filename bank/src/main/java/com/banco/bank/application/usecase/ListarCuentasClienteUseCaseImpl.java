package com.banco.bank.application.usecase;

import com.banco.bank.domain.model.Cuenta;
import com.banco.bank.domain.port.in.ListarCuentasClienteUseCase;
import com.banco.bank.domain.port.out.CuentaRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListarCuentasClienteUseCaseImpl implements ListarCuentasClienteUseCase {

    private final CuentaRepositoryPort cuentaRepositoryPort;

    @Override
    public Page<Cuenta> execute(UUID clienteId, Pageable pageable) {
        return cuentaRepositoryPort.findByClienteId(clienteId, pageable);
    }
}
