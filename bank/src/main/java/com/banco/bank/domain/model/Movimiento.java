package com.banco.bank.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Movimiento {

    private UUID movimientoId;
    private Cuenta cuenta;
    private Instant fecha;
    private String tipoMovimiento;
    private BigDecimal valor;
    private BigDecimal saldo;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
}
