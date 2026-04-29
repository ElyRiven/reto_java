package com.banco.bank.infrastructure.web.dto;

import com.banco.bank.domain.model.Movimiento;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record MovimientoResponseDTO(
        UUID movimientoId,
        CuentaResumenDTO cuenta,
        Instant fecha,
        String tipoMovimiento,
        BigDecimal valor,
        BigDecimal saldo,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {

    public record CuentaResumenDTO(
            UUID cuentaId,
            UUID clienteId,
            String numeroCuenta,
            String tipoCuenta,
            BigDecimal saldoInicial,
            Boolean estado,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt
    ) {}

    public static MovimientoResponseDTO from(Movimiento movimiento) {
        CuentaResumenDTO cuentaDTO = null;
        if (movimiento.getCuenta() != null) {
            var c = movimiento.getCuenta();
            cuentaDTO = new CuentaResumenDTO(
                    c.getCuentaId(),
                    c.getClienteId(),
                    c.getNumeroCuenta(),
                    c.getTipoCuenta(),
                    c.getSaldoInicial(),
                    c.getEstado(),
                    c.getCreatedAt(),
                    c.getUpdatedAt(),
                    c.getDeletedAt()
            );
        }
        return new MovimientoResponseDTO(
                movimiento.getMovimientoId(),
                cuentaDTO,
                movimiento.getFecha(),
                movimiento.getTipoMovimiento(),
                movimiento.getValor(),
                movimiento.getSaldo(),
                movimiento.getCreatedAt(),
                movimiento.getUpdatedAt(),
                movimiento.getDeletedAt()
        );
    }
}
