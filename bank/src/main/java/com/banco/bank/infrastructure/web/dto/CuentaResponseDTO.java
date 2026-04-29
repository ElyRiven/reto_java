package com.banco.bank.infrastructure.web.dto;

import com.banco.bank.domain.model.Cuenta;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CuentaResponseDTO(
        UUID cuentaId,
        ClienteDTO cliente,
        String numeroCuenta,
        String tipoCuenta,
        BigDecimal saldoInicial,
        Boolean estado,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {

    public record ClienteDTO(
            UUID clienteId,
            String nombre,
            Boolean estado,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt
    ) {}

    public static CuentaResponseDTO from(Cuenta cuenta) {
        ClienteDTO clienteDTO = null;
        if (cuenta.getCliente() != null) {
            var c = cuenta.getCliente();
            clienteDTO = new ClienteDTO(
                    c.getClienteId(),
                    c.getNombre(),
                    c.getEstado(),
                    c.getCreatedAt(),
                    c.getUpdatedAt(),
                    c.getDeletedAt()
            );
        }
        return new CuentaResponseDTO(
                cuenta.getCuentaId(),
                clienteDTO,
                cuenta.getNumeroCuenta(),
                cuenta.getTipoCuenta(),
                cuenta.getSaldoInicial(),
                cuenta.getEstado(),
                cuenta.getCreatedAt(),
                cuenta.getUpdatedAt(),
                cuenta.getDeletedAt()
        );
    }
}
