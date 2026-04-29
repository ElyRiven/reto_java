package com.banco.bank.application.usecase;

import com.banco.bank.domain.exception.ClienteNoEncontradoException;
import com.banco.bank.domain.exception.NumeroCuentaDuplicadoException;
import com.banco.bank.domain.exception.NumeroCuentaInvalidoException;
import com.banco.bank.domain.exception.TipoCuentaInvalidoException;
import com.banco.bank.domain.model.Cuenta;
import com.banco.bank.domain.model.TipoCuentaEnum;
import com.banco.bank.domain.port.in.CrearCuentaUseCase;
import com.banco.bank.domain.port.out.ClienteRepositoryPort;
import com.banco.bank.domain.port.out.CuentaRepositoryPort;
import com.fasterxml.uuid.Generators;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrearCuentaUseCaseImpl implements CrearCuentaUseCase {

    private final CuentaRepositoryPort cuentaRepositoryPort;
    private final ClienteRepositoryPort clienteRepositoryPort;

    @Override
    public Cuenta execute(Cuenta cuenta) {
        var cliente = clienteRepositoryPort.findByClienteId(cuenta.getCliente().getClienteId())
                .orElseThrow(() -> new ClienteNoEncontradoException("Cliente no encontrado"));
        cuenta.setCliente(cliente);

        if (!cuenta.getNumeroCuenta().matches("^[0-9]+$")) {
            throw new NumeroCuentaInvalidoException("Número de cuenta debe contener solo dígitos [0-9]");
        }

        try {
            TipoCuentaEnum.fromDisplayName(cuenta.getTipoCuenta());
        } catch (IllegalArgumentException e) {
            throw new TipoCuentaInvalidoException("Tipo de cuenta debe ser 'Ahorros' o 'Corriente'");
        }

        if (cuentaRepositoryPort.existsByNumeroCuentaAndDeletedAtIsNull(cuenta.getNumeroCuenta())) {
            throw new NumeroCuentaDuplicadoException("Número de cuenta ya existe");
        }

        cuenta.setCuentaId(Generators.timeBasedEpochGenerator().generate());
        cuenta.setCreatedAt(Instant.now());

        var saved = cuentaRepositoryPort.save(cuenta);
        log.info("Cuenta creada: cuentaId={}", saved.getCuentaId());
        return saved;
    }
}
